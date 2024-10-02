package com.idea.memories.Views.CustomViews;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.View;
import android.widget.RelativeLayout;
import com.idea.memories.R;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class NavigationBar extends RelativeLayout {
    private float shadowRadius;
    private int pathColor, shadowColor, fabId;

    private boolean drawn;

    private Bitmap cacheBitmap;
    public NavigationBar(Context ctx , AttributeSet attr) {
        super(ctx , attr);

        TypedArray a = ctx.obtainStyledAttributes(attr , R.styleable.NavigationBar);
        pathColor = a.getInteger(R.styleable.NavigationBar_nav_path_color, 0);
        shadowRadius = a.getFloat(R.styleable.NavigationBar_nav_shadow_radius, 8f);
        shadowColor = a.getInteger(R.styleable.NavigationBar_nav_shadow_color, Color.BLACK);
        fabId = a.getResourceId(R.styleable.NavigationBar_nav_fab_id, 0);
        a.recycle();

        setLayerType(LAYER_TYPE_SOFTWARE , null);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        if(!drawn) {
            cacheBitmap = Bitmap.createBitmap(getWidth() , getHeight() , Bitmap.Config.ARGB_8888);
            Canvas cache = new Canvas(cacheBitmap);

            FloatingActionButton fab = ((View) getParent()).findViewById(fabId);

            float x = fab.getX();
            float size = fab.getHeight();
            float y = size / 2;

            Paint paint = new Paint();
            paint.setColor(pathColor);
            paint.setShadowLayer(shadowRadius, 0, 0, shadowColor);

            Path path = new Path();
            path.moveTo(0, getHeight());
            path.quadTo(0, y, size / 2, y);
            path.lineTo(0, y);
            path.lineTo(x - 6, y);
            path.cubicTo(x - 6, (y / 2) + size - 6, x + size + 6, (y / 2) + size - 8, x + size + 5, y);
            path.lineTo(getWidth() - size / 2, y);
            path.quadTo(getWidth(), y, getWidth(), getHeight());
            path.close();
            cache.drawPath(path , paint);
            canvas.drawPath(path, paint);

            super.onDraw(canvas);
            drawn = true;
        }
        else
            canvas.drawBitmap(cacheBitmap , 0 , 0 ,null);
    }
}