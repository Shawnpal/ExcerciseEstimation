package com.shawn.excerciseestimation;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.view.Display;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.WindowManager;

import androidx.annotation.NonNull;

import java.util.List;
import java.util.Set;

public class AnimatedSurfaceView extends SurfaceView implements SurfaceHolder.Callback {

    private Integer HUMAN_RADIUS = 3;

    public AnimatedSurfaceView(Context context) {
        super(context);

        initialize(context);
    }
    private void initialize(Context context) {
        getHolder().addCallback(this);
        setFocusable(true);

        WindowManager windowManager = (WindowManager) context.getSystemService(Context.WINDOW_SERVICE);
        Display display = windowManager.getDefaultDisplay();
        Point point = new Point();
        display.getSize(point);

    }
    @Override
    public void surfaceCreated(@NonNull SurfaceHolder holder) {

    }

    public void surfaceadded(@NonNull SurfaceHolder holder) {

    }
    @Override
    public void surfaceChanged(@NonNull SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(@NonNull SurfaceHolder holder) {

    }

    protected void onDraw(Canvas canvas,List<TensorFlowPoseDetector.Human> human_list){
        super.onDraw(canvas);

        if (canvas == null) { return; }
        canvas.drawColor(Color.WHITE);


        canvas =  draw_humans(canvas,human_list);
        canvas.save();
        canvas.restore();
        invalidate();


    }

    private Canvas draw_humans(Canvas canvas, List<TensorFlowPoseDetector.Human> human_list) {

        int cp = Common.CocoPart.values().length;
        int image_w = canvas.getWidth();
        int image_h = canvas.getHeight();

        //    for human in human_list:
        for (TensorFlowPoseDetector.Human human : human_list) {
            Point[] centers = new Point[cp];
            //part_idxs = human.keys()
            Set<Integer> part_idxs = human.parts.keySet();

            for (Common.CocoPart i : Common.CocoPart.values()) {

                if (!part_idxs.contains(i.index)) {
                    continue;
                }

                TensorFlowPoseDetector.Coord part_coord = human.parts.get(i.index);

                Point center = new Point((int) (part_coord.x * image_w + 0.5f), (int) (part_coord.y * image_h + 0.5f));

                centers[i.index] = center;

                Paint paint = new Paint();
                paint.setColor(Color.rgb(Common.CocoColors[i.index][0], Common.CocoColors[i.index][1], Common.CocoColors[i.index][2]));
                paint.setStyle(Paint.Style.FILL);
                canvas.drawCircle(center.x, center.y, HUMAN_RADIUS, paint);

            }

            //# draw line
            //for pair_order, pair in enumerate(CocoPairsRender):
            for (int pair_order = 0; pair_order < Common.CocoPairsRender.length; pair_order++) {
                int[] pair = Common.CocoPairsRender[pair_order];

                if (!part_idxs.contains(pair[0]) || !part_idxs.contains(pair[1])) {
                    continue;
                }

                Paint paint = new Paint();
                paint.setColor(Color.rgb(Common.CocoColors[pair_order][0], Common.CocoColors[pair_order][1], Common.CocoColors[pair_order][2]));
                paint.setStrokeWidth(HUMAN_RADIUS);
                paint.setStyle(Paint.Style.STROKE);

                canvas.drawLine(centers[pair[0]].x, centers[pair[0]].y, centers[pair[1]].x, centers[pair[1]].y, paint);
            }
        }
        return canvas;
    }

}
