package com.shawn.excerciseestimation;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
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

import org.bytedeco.javacv.AndroidFrameConverter;
import org.bytedeco.javacv.FFmpegFrameGrabber;
import org.bytedeco.javacv.Frame;
import org.bytedeco.javacv.FrameGrabber;
import org.bytedeco.javacv.FrameRecorder;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Set;

import static com.shawn.excerciseestimation.GlobalConstants.MODEL_HEIGHT;
import static com.shawn.excerciseestimation.GlobalConstants.MODEL_WIDTH;

public class VideoActivity extends AppCompatActivity {
    private static int VIDEO_REQUEST = 101;
    private Uri videoUri;
    SurfaceHolder surfaceHolder;
    private final int PREVIEW_WIDTH = 640;
    private final int PREVIEW_HEIGHT = 480;
    private static final int MP_INPUT_SIZE = 368;
    private static final boolean MAINTAIN_ASPECT = true;
    private static final Size DESIRED_PREVIEW_SIZE = new Size(640, 480);

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

    public void playVideo(View view) {


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIDEO_REQUEST && resultCode == RESULT_OK) {
            videoUri = Uri.parse("android.resource://" + getPackageName() + "/" + R.raw.sample);


        }

        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    //  File auxFile = new File(videoUri.getPath());
                    File auxFile = new File(Environment.getExternalStorageDirectory() + "/Download", "sample.mp4");
                    doConvert(auxFile);
                } catch (FrameGrabber.Exception e) {
                    e.printStackTrace();
                } catch (FrameRecorder.Exception e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();
    }


    private void doConvert(File file) throws
            FrameGrabber.Exception,
            FrameRecorder.Exception,
            IOException {
        FFmpegFrameGrabber videoGrabber = new FFmpegFrameGrabber(file);
        Frame frame;
        Canvas canvas = null;
        int count = 0;
        videoGrabber.start();
        AndroidFrameConverter bitmapConverter = new AndroidFrameConverter();
        while (true) {
            long startRenderImage = System.nanoTime();
            frame = videoGrabber.grabFrame();
            if (frame == null) {
                break;
            }
            if (frame.image == null) {
                continue;
            }
            count++;


             Bitmap currentImage = bitmapConverter.convert(frame);
            final  Bitmap finalImage = processImage(currentImage);

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
            setupCanvas(canvas,results);



            long endRenderImage = System.nanoTime();
            final Float renderFPS = 1000000000.0f / (endRenderImage - startRenderImage + 1);


        }
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
                    }
                    catch(InterruptedException e) {}
                }
                mTimePrevious = System.currentTimeMillis();

                try {
                    canvas = surfaceHolder.lockCanvas(null);

                    synchronized (surfaceHolder) {
                        mAnimatedSurfaceView.onDraw(canvas,human_list.get(0).humans);
                    }
                }
                finally {
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

        Matrix frameToCropTransform =
                ImageUtils.getTransformationMatrix(
                        PREVIEW_WIDTH, PREVIEW_HEIGHT,
                        cropSize, cropSize,
                        90, true);
        Matrix cropToFrameTransform = new Matrix();
        frameToCropTransform.invert(cropToFrameTransform);

        // Created scaled version of bitmap for model input.
        Bitmap scaledBitmap = Bitmap.createScaledBitmap(croppedBitmap, MODEL_WIDTH, MODEL_HEIGHT, true);

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
}
