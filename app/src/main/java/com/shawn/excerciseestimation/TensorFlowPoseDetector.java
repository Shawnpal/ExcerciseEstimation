package com.shawn.excerciseestimation;

import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.RectF;
import android.os.Trace;
import android.util.Log;

import com.shawn.excerciseestimation.env.Common;

import org.tensorflow.contrib.android.TensorFlowInferenceInterface;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TensorFlowPoseDetector implements Classifier {
    private static final String TAG = "TensorFlowPoseDetector";

    // Config values.
    private String inputName;
    private String[] outputNames;

    private int inputSize;

    // Pre-allocated buffers.
    private int[] intValues;
    private float[] floatValues;
    private float[] outputHeatMap;
    private float[] outputPafMat;

    private boolean logStats = false;

    private TensorFlowInferenceInterface inferenceInterface;

    private TensorFlowPoseDetector() {
    }



    public static Classifier create(
            AssetManager assetManager,
            String modelFilename,
            int inputSize,
            String inputNodeName,
            String[] outputNodeNames) {
        com.shawn.excerciseestimation.TensorFlowPoseDetector c = new com.shawn.excerciseestimation.TensorFlowPoseDetector();
        c.inputName = inputNodeName;

        try {
            c.inferenceInterface = new TensorFlowInferenceInterface(assetManager, modelFilename);
        } catch (RuntimeException re) {
            Log.e(TAG, "CAUSE " + re.getCause().getMessage(), re);
        }

        int heatMap = (46 * 46 * 19);
        int pafMat = (46 * 46 * 38);


        // Ideally, inputSize could have been retrieved from the shape of the input operation.  Alas,
        // the placeholder node for input in the graphdef typically used does not specify a shape, so it
        // must be passed in as a parameter.
        c.inputSize = inputSize;

        // Pre-allocate buffers.
        c.outputNames = outputNodeNames;
        c.intValues = new int[inputSize * inputSize];
        c.floatValues = new float[inputSize * inputSize * 3];
        c.outputHeatMap = new float[heatMap];
        c.outputPafMat = new float[pafMat];

        return c;
    }

    @Override
    public List<Recognition> recognizeImage(Bitmap bitmap) { //final
        // Log this method so that it can be analyzed with systrace.
        Trace.beginSection("recognizeImage");
        float imageMean = 0;
        float imageStd = 1;

        Trace.beginSection("preprocessBitmap");
        // Preprocess the image data from 0-255 int to normalized float based
        // on the provided parameters.


        int w = bitmap.getWidth();

        bitmap.getPixels(intValues, 0, bitmap.getWidth(), 0, 0, bitmap.getWidth(), bitmap.getHeight());
        for (int i = 0; i < intValues.length; ++i) {
            final int val = intValues[i];


            // OpenCV uses : B G R
            floatValues[i * 3 + 0] = ((float) (val & 0xFF) * 2.0f / 255.0f) - 1.0f;         //B
            floatValues[i * 3 + 1] = ((float) ((val >> 8) & 0xFF) * 2.0f / 255.0f) - 1.0f;  //G
            floatValues[i * 3 + 2] = ((float) ((val >> 16) & 0xFF) * 2.0f / 255.0f) - 1.0f; //R

            bitmap.setPixel(i % w, i / w,
                    Color.rgb(
                            (int) ((floatValues[i * 3 + 0] + 1.0f) * (255.0f / 2.0f)),
                            (int) ((floatValues[i * 3 + 1] + 1.0f) * (255.0f / 2.0f)),
                            (int) ((floatValues[i * 3 + 2] + 1.0f) * (255.0f / 2.0f))));
        }
        Trace.endSection();

        Trace.beginSection("feed");
        inferenceInterface.feed(inputName, floatValues, 1, inputSize, inputSize, 3);
        Trace.endSection();


        Trace.beginSection("run");
        inferenceInterface.run(outputNames, logStats);
        Trace.endSection();

        Trace.beginSection("fetch");


        inferenceInterface.fetch(outputNames[0], outputPafMat);
        inferenceInterface.fetch(outputNames[1], outputHeatMap);

        Log.w(TAG, "recognizeImage: OK");


        Trace.endSection();


        // TODO: capture DEBUG images and compute parts and pose

        Trace.endSection(); // "recognizeImage"

        final ArrayList<Recognition> recognitions = new ArrayList<Recognition>();
        Recognition r0 = new Recognition("a", "a", 1f, new RectF(0, 0, 10, 10));


        List<Human> humans = estimatePose(outputHeatMap, outputPafMat);
        Log.e(TAG, "Humans found = " + humans.size());

        r0.humans = humans;

        recognitions.add(r0);
        return recognitions;
    }

    @Override
    public void enableStatLogging(boolean logStats) {
        this.logStats = logStats;
    }

    @Override
    public String getStatString() {
        return inferenceInterface.getStatString();
    }

    @Override
    public void close() {
        inferenceInterface.close();
    }


    public static float clamp(float val, float min, float max) {
        return Math.max(min, Math.min(max, val));
    }

    private int outToColor(float val) {

        int v = (int) clamp(val * 255 * 50, 0, 255);
        return Color.rgb(v, 0, 0);
    }

    float NMS_Threshold = 0.1f;
    int InterMinAbove_Threshold = 6;
    float Inter_Threashold = 0.1f;
    int Min_Subset_Cnt = 4;
    float Min_Subset_Score = 0.8f;
    int Max_Human = 96;


    private Bitmap debugOutput(float[] mat, int[] shape) {
        int w = shape[0];
        int h = shape[1];
        int c = shape[2];
        Log.i(TAG, "debugOutput " + w + " " + h + " " + c);

        Bitmap bmp = Bitmap.createBitmap(w, (c * h), Bitmap.Config.ARGB_8888);
        for (int i = 0; i < c; i++) {
            for (int x = 0; x < w; x++) {
                for (int y = 0; y < h; y++) {
                    bmp.setPixel(x, (i * h) + y, outToColor(mat[(i * w * h) + (x * w) + y]));
                }
            }
        }
        return bmp;

    }


    private float[] rollAxis(float[] input, int[] size, int axis) {
        float[] o = new float[size[0] * size[1] * size[2]];
        int i = 0;
        for (int k = 0; k < size[axis]; k++) {
            for (int j = 0; j < input.length; j += size[axis]) {
                o[i++] = input[k + j];
            }
        }
        return o;

    }


    public static float[] restByMin(float[] inputArray) {
        float minValue = inputArray[0];
        for (int i = 1; i < inputArray.length; i++) {
            if (inputArray[i] < minValue) {
                minValue = inputArray[i];
            }
        }
        for (int j = 0; j < inputArray.length; j++) {
            inputArray[j] -= minValue;
        }
        return inputArray;
    }


    private float[] non_max_suppression(float[] img, int w, int h, int window_size, float threshold) {
        float[] result = new float[img.length];
        int hws = (int) Math.floor(window_size / 2);
        // maximum_filter_only
        for (int i = 0; i < w; i++) {
            for (int j = 0; j < h; j++) {

                if (img[(i * w) + j] < threshold) {
                    img[(i * w) + j] = 0;
                }


                float wm = 0;
                for (int x = Math.max(i - hws, 0); x < Math.min(w, i + window_size - hws); x++) {
                    for (int y = Math.max(j - hws, 0); y < Math.min(h, j + window_size - hws); y++) {
                        wm = Math.max(wm, img[(x * w) + y]);
                    }
                }
                result[(i * w) + j] = wm;


                if (result[(i * w) + j] != img[(i * w) + j]) {
                    result[(i * w) + j] = 0;
                }
            }
        }

        return result;
    }

    class Coord {
        public float x;
        public float y;

        Coord(float x, float y) {
            this.x = x;
            this.y = y;
        }
    }

    private List<Coord> findCoords(float[] img, int w, float threshold) {
        List<Coord> cc = new ArrayList<>();

        for (int i = 0; i < w; i++) {
            for (int j = 0; j < w; j++) {
                if (img[(i * w) + j] >= threshold) {
                    cc.add(new Coord(i, j));
                }
            }
        }
        return cc;
    }

    private String[][] itertools_combinations(String[] input) {

        List<String[]> re = new ArrayList<>();
        int k = 0;
        for (int i = 0; i < input.length; i++) {
            for (int j = i+1; j < input.length; j++) {
                re.add(new String[]{input[i], input[j]});
            }
        }

        if (re.size() == 0) {
            return new String[0][];
        } else {
            return re.toArray(new String[re.size()][]);
        }
    }

    private Connection[][] itertools_product(List<Connection> a, List<Connection> b) {
        Connection[][] re = new Connection[a.size() * b.size()][];
        int k = 0;
        for (int i = 0; i < a.size(); i++) {
            for (int j = 0; j < b.size(); j++) {
                re[k++] = new Connection[]{a.get(i), b.get(j)};
            }
        }
        return re;
    }

    private boolean inBothSets(String[] a, String[] b) {

        for (int i = 0; i < a.length; i++) {
            for (int j = 0; j < b.length; j++) {
                if (a[i].equals(b[j])) {
                    return true;
                }
            }
        }
        return false;
    }
    private List<Human> estimatePose(float[] heatMat, float[] pafMat) {

        /** heatMat is (19c,46a,46b) **/
        heatMat = rollAxis(heatMat, new int[]{46, 46, 19}, 2);

        pafMat = rollAxis(pafMat, new int[]{46, 46, 38}, 2);
        /** pafMat is (38c,46a,46b) **/

        int w = 46;

        for (int i = 0; i < 19; i++) {
            float[] dd = new float[w * w];
            System.arraycopy(heatMat, i * w * w, dd, 0, w * w);
            dd = restByMin(dd);
            System.arraycopy(dd, 0, heatMat, i * w * w, w * w);
        }

        for (int i = 0; i < 19; i++) {  // each image
            for (int j = 0; j < w; j++) { // each row
                float[] rd = new float[w];
                System.arraycopy(heatMat, i * w * w + (j * w), rd, 0, w);
                rd = restByMin(rd);
                System.arraycopy(rd, 0, heatMat, i * w * w + (j * w), w);
            }
        }

        //float _NMS_Threshold = Math.max(np.average(heatMat) * 4.0f, NMS_Threshold);
        float dSum = 0;
        for (int i = 0; i < heatMat.length; i++) {
            dSum += heatMat[i];
        }
        float _NMS_Threshold = Math.max(dSum / heatMat.length * 4.0f, NMS_Threshold);

        _NMS_Threshold = Math.min(_NMS_Threshold, 0.3f);

        @SuppressWarnings("unchecked")
        ArrayList[] coords = new ArrayList[18];


        for (int i = 0; i < 18; i++) {
            float[] img = new float[w * w];
            System.arraycopy(heatMat, i * w * w, img, 0, w * w);
            float[] nms = non_max_suppression(img, w, w, 5, _NMS_Threshold);
            coords[i] = new ArrayList<Coord>();
            coords[i].addAll(findCoords(nms, w, _NMS_Threshold));
        }


        List<Connection> connection_all = new ArrayList<>();

        for (int i = 0; i < Common.CocoPairs.length; i++) {
            int idx1 = Common.CocoPairs[i][0];
            int idx2 = Common.CocoPairs[i][1];
            float[] paf_x = new float[w * w];
            float[] paf_y = new float[w * w];
            System.arraycopy(pafMat, Common.CocoPairsNetwork[i][0] * w * w, paf_x, 0, w * w);
            System.arraycopy(pafMat, Common.CocoPairsNetwork[i][1] * w * w, paf_y, 0, w * w);

            List<Connection> connections = estimate_pose_pair(coords,
                    idx1, idx2, // (idx1, idx2)
                    paf_x, paf_y, w); // (paf_x_idx, paf_y_idx)

            connection_all.addAll(connections);
        }

        HashMap<String, List<Connection>> connection_by_human = new HashMap<>();

        for (int idx = 0; idx < connection_all.size(); idx++) {

            String key = String.format("human_%d", idx);
            if (!connection_by_human.containsKey(key)) {
                connection_by_human.put(key, new ArrayList<Connection>());
            }
            connection_by_human.get(key).add(connection_all.get(idx));
        }

        HashMap<String, List<String>> no_merge_cache = new HashMap<>();
        //while True:
        while (true) {
            boolean is_merged = false;
            //for k1, k2 in itertools.combinations(connection_by_human.keys(), 2):{
            String[][] keyComb = itertools_combinations(
                    connection_by_human.keySet().toArray(new String[connection_by_human.size()]));

            for (String[] comb : keyComb) {
                String k1 = comb[0];
                String k2 = comb[1];

                if (no_merge_cache.containsKey(k1) && no_merge_cache.get(k1).contains(k2)) {
                    continue;
                }

                //for c1, c2 in itertools.product(connection_by_human[k1], connection_by_human[k2]):{
                Connection[][] conProd = itertools_product(connection_by_human.get(k1), connection_by_human.get(k2));
                for (Connection[] prod : conProd) {
                    Connection c1 = prod[0];
                    Connection c2 = prod[1];
                    //if len(set(c1['uPartIdx']) & set(c2['uPartIdx'])) > 0:{
                    if (inBothSets(c1.uPartIdx, c2.uPartIdx)) {
                        is_merged = true;
                        //connection_by_human[k1].extend(connection_by_human[k2])
                        connection_by_human.get(k1).addAll(connection_by_human.get(k2));
                        //connection_by_human.pop(k2)
                        connection_by_human.remove(k2);
                        break;
                    }
                }
                if (is_merged) {
                    no_merge_cache.remove(k1);
                    break;
                } else {
                    if (!no_merge_cache.containsKey(k1)) {
                        no_merge_cache.put(k1, new ArrayList<String>());
                    }
                    no_merge_cache.get(k1).add(k2);
                }
            } //!for
            if (!is_merged) {
                break;
            }
        }
        HashMap<String, List<Connection>> tmpCBH = new HashMap<>();
        for (Map.Entry<String, List<Connection>> entry : connection_by_human.entrySet()) {
            if (entry.getValue().size() >= Min_Subset_Cnt) {
                tmpCBH.put(entry.getKey(), entry.getValue());
            }
        }
        connection_by_human = tmpCBH;

        tmpCBH = new HashMap<>();
        for (Map.Entry<String, List<Connection>> entry : connection_by_human.entrySet()) {
            float maxScH = 0;
            for (Connection chs : entry.getValue()) {
                maxScH = Math.max(maxScH, chs.score);
            }

            if (maxScH >= Min_Subset_Score) {
                tmpCBH.put(entry.getKey(), entry.getValue());
            }
        }
        connection_by_human = tmpCBH;

        List<Human> humans = new ArrayList<>();
        for (List<Connection> conn : connection_by_human.values()) {
            humans.add(connections_to_human(conn, heatMat, w));
        }
        return humans;
    }

    class Human {
        Map<Integer, Coord> parts = new HashMap<>();

    }

    private Human connections_to_human(List<Connection> connections, float[] heatMat, int w) {

        Human human = new Human();


        for (Connection conn : connections) {

            human.parts.put(conn.partIdx[0], new Coord(conn.c1.x / w, conn.c1.y / w));
            human.parts.put(conn.partIdx[1], new Coord(conn.c2.x / w, conn.c2.y / w));


        }
        return human;
    }

    class Connection {
        public float score;
        public Coord c1; // (x1, y1),
        public Coord c2; // (x2, y2),
        public int[] idx; // (idx1, idx2),
        public int[] partIdx; // (partIdx1, partIdx2),
        public String[] uPartIdx; // ('{}-{}-{}'.format(x1, y1, partIdx1) , '{}-{}-{}'.format(x2, y2, partIdx2))
    }

    class ScoreOutput {
        public float score;
        public int count;

        ScoreOutput(float score, int count) {
            this.score = score;
            this.count = count;
        }
    }



    private List<Connection> estimate_pose_pair(ArrayList<Coord>[] coords, int partIdx1, int partIdx2, float[] pafMatX, float[] pafMatY, int w) {


        List<Connection> connection_temp = new ArrayList<>();

        List<Coord> peak_coord1 = coords[partIdx1];
        List<Coord> peak_coord2 = coords[partIdx2];

        int cnt = 0;


        for (int idx1 = 0; idx1 < peak_coord1.size(); idx1++) {
            //for idx2, (y2, x2) in enumerate(zip(peak_coord2[0], peak_coord2[1])):
            for (int idx2 = 0; idx2 < peak_coord2.size(); idx2++) {
                float y1 = peak_coord1.get(idx1).x;
                float x1 = peak_coord1.get(idx1).y;
                float y2 = peak_coord2.get(idx2).x;
                float x2 = peak_coord2.get(idx2).y;

                ScoreOutput scoreCount = get_score(x1, y1, x2, y2, pafMatX, pafMatY, w);
                cnt += 1;

                int[][] cc1 = {{2, 3}, {3, 4}, {5, 6}, {6, 7}};
                if ((partIdx1 == cc1[0][0] && partIdx2 == cc1[0][1]) ||
                        (partIdx1 == cc1[1][0] && partIdx2 == cc1[1][1]) ||
                        (partIdx1 == cc1[2][0] && partIdx2 == cc1[2][1]) ||
                        (partIdx1 == cc1[3][0] && partIdx2 == cc1[3][1])) {

                    if (scoreCount.count < Math.floor(InterMinAbove_Threshold / 2)
                            || scoreCount.score <= 0.0f) {
                        continue;
                    }
                }

                else if (scoreCount.count < InterMinAbove_Threshold
                        || scoreCount.score < 0.0) {
                    continue;
                }


                Connection cnn = new Connection();
                cnn.score = scoreCount.score;
                cnn.c1 = new Coord(x1, y1);
                cnn.c2 = new Coord(x2, y2);
                cnn.idx = new int[]{idx1, idx2};
                cnn.partIdx = new int[]{partIdx1, partIdx2};
                cnn.uPartIdx = new String[]{
                        String.format("%s-%s-%s", x1, y1, partIdx1),
                        String.format("%s-%s-%s", x2, y2, partIdx2)
                };
                connection_temp.add(cnn);
            }
        }

        List<Connection> connection = new ArrayList<>();

        List<Integer> used_idx1 = new ArrayList<>();
        List<Integer> used_idx2 = new ArrayList<>();

        for (Connection candidate : sortConnections(connection_temp)) {

            if (used_idx1.contains(candidate.idx[0])
                    || used_idx2.contains(candidate.idx[1])) {
                continue;
            }
            connection.add(candidate);
            used_idx1.add(candidate.idx[0]);
            used_idx2.add(candidate.idx[1]);
        }
        return connection;
    }

    private List<Connection> sortConnections(List<Connection> connections) {

        Collections.sort(connections, new Comparator<Connection>() {
            @Override
            public int compare(Connection o1, Connection o2) {
                return Float.compare(o1.score, o2.score);
            }
        });
        Collections.reverse(connections);
        return connections;
    }

    private float[] np_arange(float start, float stop, float step) {
        float[] re = new float[(int) Math.ceil((stop - start) / step)];
        for (int i = 0; i < re.length; i++) {
            re[i] = start + (step * i);
        }
        return re;
    }

    private float[] np_full(int size, float value) {
        float[] re = new float[size];
        for (int i = 0; i < re.length; i++) {
            re[i] = value;
        }
        return re;
    }

    private float[] vector_add(float[] input, float value) {
        float[] re = new float[input.length];
        for (int i = 0; i < re.length; i++) {
            re[i] = input[i] + value;
        }
        return re;
    }

    private float[] vector_mul(float[] input, float value) {
        float[] re = new float[input.length];
        for (int i = 0; i < re.length; i++) {
            re[i] = input[i] * value;
        }
        return re;
    }

    private float[] vector_sum(float[] i1, float[] i2) {
        float[] re = new float[i1.length];
        for (int i = 0; i < re.length; i++) {
            re[i] = i1[i] + i2[i];
        }
        return re;
    }

    private boolean[] vector_grater(float[] input, float value) {
        boolean[] re = new boolean[input.length];
        for (int i = 0; i < re.length; i++) {
            re[i] = input[i] > value;
        }
        return re;
    }

    private int[] np_astype_int8(float[] input) {
        int[] re = new int[input.length];
        for (int i = 0; i < input.length; i++) {
            re[i] = (int) Math.floor(input[i]);
        }
        return re;
    }

    private ScoreOutput get_score(float x1, float y1, float x2, float y2, float[] pafMatX, float[] pafMatY, int w) {
        int __num_inter = 10;
        float __num_inter_f = (float) __num_inter;
        float dx = x2 - x1;
        float dy = y2 - y1;
        //normVec = math.sqrt(dx ** 2 + dy ** 2)
        float normVec = (float) Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));

        if (normVec < 1e-4) {
            return new ScoreOutput(0.0f, 0);
        }

        float vx = dx / normVec;
        float vy = dy / normVec;


        int[] xs = np_astype_int8(vector_add(
                (x1 != x2) ? np_arange(x1, x2, dx / __num_inter_f) : np_full(__num_inter, x1)
                , 0.5f));

        int[] ys = np_astype_int8(vector_add(
                (y1 != y2) ? np_arange(y1, y2, dy / __num_inter_f) : np_full(__num_inter, y1),
                0.5f));


        float[] pafXs = np_full(__num_inter, 0);
        float[] pafYs = np_full(__num_inter, 0);


        for (int idx = 0; idx < xs.length; idx++) {
            int mx = xs[idx];
            int my = ys[idx];
            //pafXs[idx] = pafMatX[my][mx]
            pafXs[idx] = pafMatX[my * w + mx];
            //pafYs[idx] = pafMatY[my][mx]
            pafYs[idx] = pafMatY[my * w + mx];
        }


        float[] local_scores = vector_sum(vector_mul(pafXs, vx), vector_mul(pafYs, vy));

        boolean[] thidxs = vector_grater(local_scores, Inter_Threashold);

        float score = sum(filter(local_scores, thidxs));
        int count = sum(thidxs);
        return new ScoreOutput(score, count);
    }

    private int sum(boolean[] vec) {
        int sum = 0;
        for (int i = 0; i < vec.length; i++) {
            if (vec[i]) {
                sum++;
            }
        }
        return sum;
    }

    private float sum(float[] vec) {
        float sum = 0;
        for (int i = 0; i < vec.length; i++) {
            sum += vec[i];
        }
        return sum;
    }

    private float[] filter(float[] vec, boolean[] f) {
        List<Float> re = new ArrayList<>();
        for (int i = 0; i < vec.length; i++) {
            if (f[i]) {
                re.add(vec[i]);
            }
        }

        float[] floatArray = new float[re.size()];
        int i = 0;
        for (float xf : re) {
            floatArray[i++] = xf;
        }
        return floatArray;
    }
}


