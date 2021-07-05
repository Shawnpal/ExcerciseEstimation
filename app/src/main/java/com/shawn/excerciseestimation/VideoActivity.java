package com.shawn.excerciseestimation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.ContentUris;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Size;
import android.view.SurfaceHolder;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.utils.ColorTemplate;

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import static com.shawn.excerciseestimation.GlobalConstants.MODEL_HEIGHT;
import static com.shawn.excerciseestimation.GlobalConstants.MODEL_WIDTH;

public class VideoActivity extends AppCompatActivity {
    private static int VIDEO_REQUEST = 101;
    private LineChart Linechart1, Linechart2;

    SurfaceHolder surfaceHolder;
    private static final int MP_INPUT_SIZE = 368;
    Hashtable<Integer,  Point[]> ht1 = new Hashtable<>();
    ArrayList<Point[]> PointsArray = new ArrayList<>();
    private static final String MP_INPUT_NAME = "image";
    private static final String MP_OUTPUT_L1 = "Openpose/MConv_Stage6_L1_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_OUTPUT_L2 = "Openpose/MConv_Stage6_L2_5_pointwise/BatchNorm/FusedBatchNorm";
    private static final String MP_MODEL_FILE = "file:///android_asset/frozen_person_model.pb";

    FrameLayout frame;
    AnimatedSurfaceView mAnimatedSurfaceView;
    private Classifier detector;





    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        frame = findViewById(R.id.Frame);
        mAnimatedSurfaceView = new AnimatedSurfaceView(getApplicationContext());
        frame.addView(mAnimatedSurfaceView);
        Linechart1 = findViewById(R.id.LineChart1);
        Linechart2 = findViewById(R.id.LineChart2);
        //Bracket Organizer for Permission Checks
        {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.CAMERA)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, 1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.READ_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }


            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {

                } else {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, 1);
                    // MY_PERMISSIONS_REQUEST_READ_CONTACTS is an
                    // app-defined int constant. The callback method gets the
                    // result of the request.
                }
            }
        }
    }
    private void setChart() {

        // now in hours
        long now = TimeUnit.MILLISECONDS.toHours(System.currentTimeMillis());



        ArrayList<ArrayList<Entry>> EntryArray = new ArrayList<>();

        for(int i = 0; i < 19; i++ )
        {
            ArrayList<Entry> e =  new ArrayList<>();
            EntryArray.add(e);
        }

        int i = 0;
        for(Point[] pa : PointsArray)
        {
            //Skips some by frames which doesn't see the whole body
            if (pa.length != 19 )
            continue;
            for(Point  P : pa)
            {
                if (P!=null) {
                    ArrayList<Entry> entrylist = EntryArray.get(i);
                    entrylist.add(new Entry(i, P.y));
                    i++;
                }
            }
            i=0;
        }




        // create a dataset and give it a type
        LineDataSet set1 = new LineDataSet(EntryArray.get(0), "DataSet 1");
        set1.setAxisDependency(YAxis.AxisDependency.LEFT);
        set1.setColor(ColorTemplate.getHoloBlue());
        set1.setValueTextColor(ColorTemplate.getHoloBlue());
        set1.setLineWidth(1.5f);
        set1.setDrawCircles(false);
        set1.setDrawValues(false);
        set1.setFillAlpha(65);
        set1.setFillColor(ColorTemplate.getHoloBlue());
        set1.setHighLightColor(Color.rgb(244, 117, 117));
        set1.setDrawCircleHole(false);

        // create a data object with the data sets
        LineData data = new LineData(set1);
        data.setValueTextColor(Color.WHITE);
        data.setValueTextSize(9f);

        // set data
        Linechart1.setData(data);
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


    //CCOde after
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

            new Thread(() -> {
                try {
                       File auxFile = new File(Environment.getExternalStorageDirectory() + "/Movies", "sample1.mp4");
                //    File auxFile = new File(getPathFromUri(getApplicationContext(), videoUri));
                    doConvert(auxFile);
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).start();
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

            // Configure the detector
            detector = TensorFlowPoseDetector.create(
                    this.getAssets(),
                    MP_MODEL_FILE,
                    MP_INPUT_SIZE,
                    MP_INPUT_NAME,
                    new String[]{MP_OUTPUT_L1, MP_OUTPUT_L2}
            );
            // Perform inference.
            final List<Classifier.Recognition> results = detector.recognizeImage(finalImage);
            surfaceHolder = mAnimatedSurfaceView.getHolder();
            setupCanvas(canvas, results);

            PopulateList(results.get(0).humans);

            Log.d("Result",PointsArray.toString());

        }
        setChart();
        Log.d("Result",PointsArray.toString());
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
        Bitmap croppedBitmap = getResizedBitmap(bitmap, cropSize, cropSize);

        return croppedBitmap;
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

    private void PopulateList(List<TensorFlowPoseDetector.Human> human_list)
    {
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
                //part_coord = human[i][1]
                TensorFlowPoseDetector.Coord part_coord = human.parts.get(i.index);
                //center = (int(part_coord[0] * image_w + 0.5), int(part_coord[1] * image_h + 0.5))
                Point center = new Point((int) (part_coord.x * image_w + 0.5f), (int) (part_coord.y * image_h + 0.5f));
                //centers[i] = center
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
