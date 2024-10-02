package com.idea.memories.Views.CustomViews;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.FrameLayout;


public class CircularRevealFrameLayout extends FrameLayout {

    float pivotX,pivotY,radius;
    boolean animationOn;
    Paint paint;

    public CircularRevealFrameLayout(Context context) {
        super(context);
    }

    public CircularRevealFrameLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setStyle(Paint.Style.FILL);
    }


    @Override
    protected void onDraw(Canvas canvas) {
        if(animationOn){
            canvas.drawCircle(pivotX,pivotY , radius ,paint);
            super.onDraw(canvas);
        }
    }

    public Animator Reveal (final float startRadius , float endRadius , final float centerX , final float centerY , int duration , int color){

        animationOn = true;
        pivotX = centerX;
        pivotY = centerY;
        radius = startRadius;
        paint.setColor(color);
        invalidate();

        ValueAnimator scale = ValueAnimator.ofFloat(startRadius , endRadius);
        ValueAnimator pivotXAnim = ValueAnimator.ofFloat(centerX , getRootView().getPivotX());
        pivotXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pivotX = (float) animation.getAnimatedValue();
            }
        });
        ValueAnimator pivotYAnim = ValueAnimator.ofFloat(centerY , getRootView().getPivotY());
        pivotYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pivotY = (float) animation.getAnimatedValue();
            }
        });
        pivotYAnim.setInterpolator(new AccelerateDecelerateInterpolator() );

        scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float)animation.getAnimatedValue();
                invalidate();
            }
        });

        AnimatorSet a =new AnimatorSet();
        a.play(pivotXAnim).with(pivotYAnim);
        a.setDuration((long) (0.5 * duration));
        scale.setDuration(duration);
        scale.start();
        a.start();
        scale.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationOn = false;
                invalidate();
            }
        });
        return scale;
    }

    public Animator Hide (final float startRadius , float endRadius , final float centerX , final float centerY , int duration , int color){
        animationOn = true;
        paint.setColor(color);
        ValueAnimator scale = ValueAnimator.ofFloat(startRadius , endRadius);
        ValueAnimator pivotXAnim = ValueAnimator.ofFloat(getRootView().getPivotX() , centerX);
        pivotXAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pivotX = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        ValueAnimator pivotYAnim = ValueAnimator.ofFloat(getRootView().getPivotY() , centerY);
        pivotYAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                pivotY = (float) animation.getAnimatedValue();
            }
        });

        scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                radius = (float)animation.getAnimatedValue();
            }
        });

        AnimatorSet a =new AnimatorSet();
        a.play(pivotXAnim).with(pivotYAnim);
        a.setDuration(duration);
        scale.setDuration((long)(0.75 * duration));
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animationOn = false;
                invalidate();
            }
        });
        scale.start();
        a.start();
        return a;
    }

}
