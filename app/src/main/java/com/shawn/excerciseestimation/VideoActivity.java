package com.shawn.excerciseestimation;
import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.shawn.excerciseestimation.Retrofit.ExerciseResult;
import com.shawn.excerciseestimation.Retrofit.RestClient;
import com.shawn.excerciseestimation.Retrofit.RetrofitInterface;
import com.shawn.excerciseestimation.env.Common;
import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class VideoActivity extends AppCompatActivity {


    private static int VIDEO_REQUEST = 101;
    private static int chartindex = 0;
    private static final int MP_INPUT_SIZE = 368;
    private static final String MP_INPUT_NAME = "image";
    private static final String MP_OUTPUT_L1 = "Openpose/MConv_Stage6_L1_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_OUTPUT_L2 = "Openpose/MConv_Stage6_L2_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_MODEL_FILE = "file:///android_asset/frozen_person_model.pb";


    private Retrofit retrofit;
    private RetrofitInterface retrofitInterface;
    String Email;
    private StorageManager storageManager;
    SurfaceHolder surfaceHolder;
    double[] Result;
    Thread firstThread;
    private LineChart chart;
    ListView listView;
    Dialog myDialog;
    Spinner spinner;
    ArrayList<LineData> Datalist;
    ArrayList<Point[]> PointsArray = new ArrayList<>();
    ArrayList<TextView> TextResultList = new ArrayList<>();

    FrameLayout frame;
    AnimatedSurfaceView mAnimatedSurfaceView;
    private Classifier detector;
    private List<ExerciseResult> ERlist ;
    Button PrevButton, NextButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        frame = findViewById(R.id.Frame);
        spinner = findViewById(R.id.spinnerexercise);
        mAnimatedSurfaceView = new AnimatedSurfaceView(getApplicationContext());
        frame.addView(mAnimatedSurfaceView);
        listView = new ListView(this);
        myDialog = new Dialog(this);
        Intent intent = getIntent();
        Email = intent.getStringExtra("Email");


        retrofit = new Retrofit.Builder()
                .baseUrl(((GlobalConstants) this.getApplication()).getBASE_URL())
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        retrofitInterface = RestClient.getClient();

        chart = findViewById(R.id.chart1);
        PrevButton = findViewById(R.id.prevbutton);
        NextButton = findViewById(R.id.nextbutton);
        TextResultList.add(findViewById(R.id.txtResult1));
        TextResultList.add(findViewById(R.id.txtResult2));
        TextResultList.add(findViewById(R.id.txtResult3));
        TextResultList.add(findViewById(R.id.txtResult4));
        TextResultList.add(findViewById(R.id.txtResult5));
        TextResultList.add(findViewById(R.id.txtResult6));
        TextResultList.add(findViewById(R.id.txtResult7));
        TextResultList.add(findViewById(R.id.txtResult8));
        TextResultList.add(findViewById(R.id.txtResult9));
        TextResultList.add(findViewById(R.id.txtResult10));
        TextResultList.add(findViewById(R.id.txtResult11));
        TextResultList.add(findViewById(R.id.txtResult12));
        TextResultList.add(findViewById(R.id.txtResult13));
        TextResultList.add(findViewById(R.id.txtResult14));
        TextResultList.add(findViewById(R.id.txtResult15));
        TextResultList.add(findViewById(R.id.txtResult16));
        TextResultList.add(findViewById(R.id.txtResult17));

        // Configure the detector
        detector = TensorFlowPoseDetector.create(
                this.getAssets(),
                MP_MODEL_FILE,
                MP_INPUT_SIZE,
                MP_INPUT_NAME,
                new String[]{MP_OUTPUT_L1, MP_OUTPUT_L2}
        );



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
            }
        }

    }

    private void setChart() {

        // no description text
        chart.getDescription().setEnabled(false);

        // enable touch gestures
        chart.setTouchEnabled(true);

        chart.setDragDecelerationFrictionCoef(0.9f);

        // enable scaling and dragging
        chart.setDragEnabled(true);
        chart.setScaleEnabled(true);
        chart.setDrawGridBackground(false);
        chart.setHighlightPerDragEnabled(true);

        // set an alternative background color
        chart.setBackgroundColor(Color.WHITE);
        chart.setViewPortOffsets(0f, 0f, 0f, 0f);


        ArrayList<ArrayList<Entry>> EntryArray = new ArrayList<>();

        Datalist = new ArrayList<>();
        for (int i = 0; i < 17; i++) {
            ArrayList<Entry> e = new ArrayList<>();
            EntryArray.add(e);
        }
        for (int p = 0; p < 17; p++) {
            int index2 = 0;
            for (Point[] points : PointsArray) {
                ArrayList<Entry> entrys = EntryArray.get(p);
                if (points[p] != null) {
                    entrys.add(new Entry(index2, points[p].y));
                } else {
                    if (points[p - 1] != null)
                        entrys.add(new Entry(index2, points[p - 1].y));
                    else
                        entrys.add(new Entry(index2, 0));
                }
                index2++;
            }
            // create a dataset and give it a type
            LineDataSet set = new LineDataSet(EntryArray.get(p), GetBodyClassifier(p) + " Dataset");
            set.setAxisDependency(YAxis.AxisDependency.LEFT);
            set.setColor(ColorTemplate.getHoloBlue());
            set.setValueTextColor(ColorTemplate.getHoloBlue());
            set.setLineWidth(1.5f);
            set.setDrawCircles(false);
            set.setDrawValues(false);
            set.setFillAlpha(65);
            set.setFillColor(ColorTemplate.getHoloBlue());
            set.setHighLightColor(Color.rgb(244, 117, 117));
            set.setDrawCircleHole(false);
            LineData data = new LineData(set);
            data.setValueTextColor(Color.BLACK);
            data.setValueTextSize(3f);
            Datalist.add(data);

            // create a data object with the data sets


            TextView result = TextResultList.get(p);
            if (result != null) {
                result.setText(GetBodyClassifier(p) + " " + Result[p]);
            }

        }


        Legend l = chart.getLegend();
        l.setEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.TOP_INSIDE);
        xAxis.setTextSize(10f);
        xAxis.setTextColor(Color.WHITE);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(true);
        xAxis.setTextColor(Color.rgb(255, 192, 56));
        xAxis.setCenterAxisLabels(true);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);

        leftAxis.setTextColor(ColorTemplate.getHoloBlue());
        leftAxis.setDrawGridLines(true);
        leftAxis.setGranularityEnabled(true);
        leftAxis.setAxisMinimum(0f);
        leftAxis.setAxisMaximum(500f);
        leftAxis.setYOffset(-9f);
        leftAxis.setTextColor(Color.rgb(255, 192, 56));

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setEnabled(false);

        chart.setData(Datalist.get(chartindex));
        chart.invalidate();
        PrevButton.setEnabled(true);
        NextButton.setEnabled(true);


    }


    //Confirms our camera permissions are actually permitted
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(this, "camera permission granted", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "camera permission denied", Toast.LENGTH_LONG).show();
            }
        }
    }


    public void captureVideo(View view) {
        Intent videoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
        startActivityForResult(videoIntent, VIDEO_REQUEST);

    }


    public void openFileChooser(View view) {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);

        /**Display all pickers for data that can be opened with ContentResolver.openInputStream(),
         *   allowing the user to pick one of them and then some data inside of it and
         *   returning the resulting URI to the caller.
         */
        intent.setType("*/*");

        startActivityForResult(intent, 1);
    }

    public void NextChart(View view) {
        chartindex++;
        if (chartindex > 16)
            chartindex = 0;
        if (!Datalist.isEmpty() && Datalist != null) {
            chart.setData(Datalist.get(chartindex));
            chart.invalidate();
        }
    }



    public void PrevChart(View view) {
        chartindex--;
        if (chartindex < 0)
            chartindex = 16;
        if (!Datalist.isEmpty() && Datalist != null) {
            chart.setData(Datalist.get(chartindex));
            chart.invalidate();
        }
    }

    public void GetResult(View view) {

        myDialog.setContentView(R.layout.activity_popuplist);
        ListView listView = myDialog.findViewById(R.id.ResultList);
        HashMap<String, String> map = new HashMap<>();
        map.put("Email","ShawnChen1915@gmail.com");
        ERlist = new ArrayList<ExerciseResult>();

        Call<List<ExerciseResult>> call = retrofitInterface.loadExerciseResults(map);
        Context context = this;
        call.enqueue(new Callback<List<ExerciseResult>>() {
            @Override
            public void onResponse(Call<List<ExerciseResult>> call, Response<List<ExerciseResult>> response) {
                for (ExerciseResult result : response.body()) {
                    ERlist.add(result);
                }
                ResultAdapter adapter = new ResultAdapter(context,R.layout.listadapter_view,ERlist);
                listView.setAdapter(adapter);
                myDialog.show();

            }
            @Override
            public void onFailure(Call<List<ExerciseResult>> call, Throwable t) {
            }
        });

        Handler handler = new Handler();
        handler.postDelayed(() -> {
            }, 4000);

    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //After Video is captured
        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {

        }

        //After the selected Video is selected
        if (requestCode == 1 && resultCode == RESULT_OK) {
            if (data == null) {
                Toast.makeText(getApplicationContext(), "File Selected is Null", Toast.LENGTH_SHORT).show();
                return;
            }

            Uri videoUri = data.getData();

            firstThread = new Thread(() -> {

                try {
                    synchronized (this) {
                        //Grabs the file from
                        File auxFile = new File(getPathFromUri(getApplicationContext(), videoUri));

                        PointsArray = new ArrayList<>();
                        doConvert(auxFile);
                        //After the video is processed
                        storageManager = new StorageManager(PointsArray, "result.txt", this.getApplicationContext());

                        DTW dtw = new DTW();
                        storageManager.writeToFile();
                        Hashtable<Integer, int[]> StorageHashTable = storageManager.getHashTable( spinner.getSelectedItem().toString(), getApplicationContext());

                        Result = dtw.ConvertToHashedFrames(StorageHashTable, PointsArray);

                        runOnUiThread(() -> {
                            // Stuff that updates the UI
                            setChart();
                            //Save our Results to database
                            ExerciseResult exerciseResult = new ExerciseResult(Result,spinner.getSelectedItem().toString(),Email);
                            HashMap<String, String> map = new HashMap<>();
                            map.put("Result", exerciseResult.getResult()) ;
                            map.put("ExerciseID", exerciseResult.getExerciseID()) ;
                            map.put("ResultUID",exerciseResult.getResultUID());
                            map.put("PersonEmail",exerciseResult.getPersonEmail());
                            map.put("Date",exerciseResult.getDate());

                            Call<Void> call = retrofitInterface.executeSaveResult(map);
                            call.enqueue(new Callback<Void>() {
                                @Override
                                public void onResponse(Call<Void> call, Response<Void> response) {
                                }
                                @Override
                                public void onFailure(Call<Void> call, Throwable t) {
                                }

                            });
                        });

                    }
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
            firstThread.start();


        }


    }


    private void doConvert(File file) throws FrameGrabber.Exception, FrameRecorder.Exception,
            IOException {
        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
        Frame frame;
        Canvas canvas = null;
        videoGrabber.start();
        AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();
        while (true) {
            frame = videoGrabber.grabFrame();
            if (frame == null) {
                break;
            }
            if (frame.image == null) {
                continue;
            }

            Bitmap currentImage = bitmapConverter.convert(frame);
            final Bitmap finalImage = processImage(currentImage);

            // Perform inference.
            final List<Classifier.Recognition> results = detector.recognizeImage(finalImage);
            surfaceHolder = mAnimatedSurfaceView.getHolder();
            setupCanvas(canvas, results);
            PopulateList(results.get(0).humans);
            Log.d("Result", PointsArray.toString());

        }

        Log.d("Result", PointsArray.toString());


    }

    @SuppressLint("WrongCall")
    public void setupCanvas(Canvas canvas, List<Classifier.Recognition> human_list) {
        long mTimeNow;
        long mTimePrevious = 0;
        long mTimeDelta;


        canvas = null;
        mTimeNow = System.currentTimeMillis();
        mTimeDelta = mTimeNow - mTimePrevious;
        if (mTimeDelta < 1) {
            try {
                Thread.sleep(4 - mTimeDelta);
            } catch (InterruptedException e) {
            }
        }
        mTimePrevious = System.currentTimeMillis();

        try {
            canvas = surfaceHolder.lockCanvas(null);

            synchronized (surfaceHolder) {
                mAnimatedSurfaceView.onDraw(canvas, human_list.get(0).humans);
            }
        } finally {
            if (canvas != null) {
                surfaceHolder.unlockCanvasAndPost(canvas);
            }
        }

    }


    private Bitmap processImage(Bitmap bitmap) {
        int cropSize = MP_INPUT_SIZE;
        //Tensor flow 2.0 have a hash limit of 368 which is our MP_INPUT_SIZE
        //This means the pixel limit for the image is 368x368. this defect is fixed in tensorflor 2.3.2
        //First we grabbed the Center Crop for our rame
        Matrix matrix = new Matrix();
        matrix.postRotate(90);
        Bitmap croppedBitmap = getResizedBitmap(bitmap, cropSize, cropSize);
        Bitmap rotatedBitmap = Bitmap.createBitmap(croppedBitmap, 0, 0, croppedBitmap.getWidth(), croppedBitmap.getHeight(), matrix, true);
        return rotatedBitmap;
    }


    public Bitmap getResizedBitmap(Bitmap bm, int newWidth, int newHeight) {

        //This performs a center Crop of our image first
        Bitmap dstBmp;
        if (bm.getWidth() >= bm.getHeight()) {

            dstBmp = Bitmap.createBitmap(
                    bm,
                    bm.getWidth() / 2 - bm.getHeight() / 2,
                    0,
                    bm.getHeight(),
                    bm.getHeight()
            );

        } else {

            dstBmp = Bitmap.createBitmap(
                    bm,
                    0,
                    bm.getHeight() / 2 - bm.getWidth() / 2,
                    bm.getWidth(),
                    bm.getWidth()
            );
        }

        int width = dstBmp.getWidth();
        int height = dstBmp.getHeight();
        float scaleWidth = ((float) newWidth) / width;
        float scaleHeight = ((float) newHeight) / height;
        // CREATE A MATRIX FOR THE MANIPULATION
        Matrix matrix = new Matrix();
        // RESIZE THE BIT MAP
        matrix.postScale(scaleWidth, scaleHeight);

        // "RECREATE" THE NEW BITMAP
        Bitmap resizedBitmap = Bitmap.createBitmap(
                dstBmp, 0, 0, width, height, matrix, false);
        dstBmp.recycle();
        return resizedBitmap;
    }

    private String GetBodyClassifier(int p) {
        switch (p) {
            case 0:
                return "Nose";
            case 1:
                return "Neck";
            case 2:
                return "RShoulder";
            case 3:
                return "RElbow";
            case 4:
                return "RWrist";
            case 5:
                return "LShoulder";
            case 6:
                return "LElbow";
            case 7:
                return "LWrist";
            case 8:
                return "RHip";
            case 9:
                return "RKnee";
            case 10:
                return "RAnkle";
            case 11:
                return "LHip";
            case 12:
                return "LKnee";
            case 13:
                return "LAnkle";
            case 14:
                return "REye";
            case 15:
                return "LEye";
            case 16:
                return "REar";
            case 17:
                return "LEar";
            default:
                return "NO Body Part";
            // code block
        }
    }


    private void PopulateList(List<TensorFlowPoseDetector.Human> human_list) {
        int cp = Common.CocoPart.values().length;
        int image_w = 480;
        int image_h = 480;
        //    for human in human_list:
        for (TensorFlowPoseDetector.Human human : human_list) {
            Point[] centers = new Point[cp];
            //part_idxs = human.keys()
            Set<Integer> part_idxs = human.parts.keySet();

            for (Common.CocoPart i : Common.CocoPart.values()) {
                //if i not in part_idxs:
                if (!part_idxs.contains(i.index)) {
                    Log.w("COORD %s, NULL, NULL", i.toString());
                    continue;
                }
                TensorFlowPoseDetector.Coord part_coord = human.parts.get(i.index);
                Point center = new Point((int) (part_coord.x * image_w + 0.5f), (int) (part_coord.y * image_h + 0.5f));
                centers[i.index] = center;

            }
            PointsArray.add(centers);
        }


    }


    //gets the actual File Path from URI rather than emulatated
    // Code copied from https://stackoverflow.com/questions/17546101/get-real-path-for-uri-android
    public static String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

}
