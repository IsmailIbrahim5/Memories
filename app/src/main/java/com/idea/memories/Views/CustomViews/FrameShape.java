package com.idea.memories.Views.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.widget.FrameLayout;
import com.idea.memories.R;


import androidx.core.graphics.PathParser;

public class FrameShape extends FrameLayout {

    private static String pathString;
    private float pathHeight, pathWidth;
    private int pathColor, shadowColor, shadowRadius, shadowX, shadowY;

    private boolean drawn;

    private Bitmap cacheBitmap;

    public FrameShape(Context context) {
        super(context);
    }

    public FrameShape(Context ctx, AttributeSet attrs) {
        super(ctx, attrs);
        TypedArray a = ctx.obtainStyledAttributes(attrs ,R.styleable.CustomShape);
        pathString = a.getString(R.styleable.CustomShape_path_string);
        pathHeight = a.getFloat(R.styleable.CustomShape_path_height, 24f);
        pathWidth = a.getFloat(R.styleable.CustomShape_path_width, 24f);
        shadowRadius = a.getInteger(R.styleable.CustomShape_shadow_radius, 8);
        pathColor = a.getInteger(R.styleable.CustomShape_path_color, Color.WHITE);
        shadowColor = a.getInteger(R.styleable.CustomShape_shadow_color, Color.BLACK);
        shadowX = a.getInteger(R.styleable.CustomShape_shadow_x, 0);
        shadowY = a.getInteger(R.styleable.CustomShape_shadow_y, 0);
        a.recycle();

        setLayerType(LAYER_TYPE_SOFTWARE , null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        int w = getWidth(), h = getHeight();

        if(!drawn) {
            cacheBitmap = Bitmap.createBitmap(getWidth() , getHeight() , Bitmap.Config.ARGB_8888);
            Canvas cache = new Canvas(cacheBitmap);


            Path path = PathParser.createPathFromPathData(pathString);
            path.setFillType(Path.FillType.WINDING);
            Matrix scaleMatrix = new Matrix();
            RectF rectF = new RectF();
            path.computeBounds(rectF, true);

            float x = (float) w / (pathWidth + shadowRadius);
            float y = (float) h / (pathHeight + shadowRadius);
            scaleMatrix.setScale(x, y, -((shadowRadius / 2f) + (shadowRadius / 16f)), -((shadowRadius / 2f) + (shadowRadius / 16f)));
            path.transform(scaleMatrix);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
            paint.setStyle(Paint.Style.FILL);
            paint.setColor(pathColor);
            paint.setShadowLayer(8 , shadowX, shadowY, shadowColor);
            canvas.drawPath(path, paint);
            cache.drawPath(path, paint);
            path.close();

            drawn = true;
        }
        else
            canvas.drawBitmap(cacheBitmap , 0 ,0 , null);
    }

}

