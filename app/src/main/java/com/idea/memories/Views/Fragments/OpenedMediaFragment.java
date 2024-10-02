package com.idea.memories.Views.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.idea.memories.Classes.MediaFile;
import com.idea.memories.R;

import com.idea.memories.Classes.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.idea.memories.Adapters.MediaAdapter;

import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

public class OpenedMediaFragment extends Fragment {

    View v;
    RecyclerView recyclerView;
    float height1 , width1 , x1 ,y1 ,radius, scaleX_value , scaleY_value;
    boolean is_playing;
    Fragment current_media_file;
    MediaFileFragment simpleImageFragment;
    Uri image_uri;

    View play_button;
    public boolean is_controls_on = true;

    ArrayList<MediaFile> mediaFiles;
    View.OnClickListener onBackClickListener;

    Memory memory_data;
    Toolbar toolbar;
    CardView bottom_navigation;
    public ViewPager image_switcher;
    CardView image_switcher_container;
    FloatingActionButton next , back;
    View root;
    MediaAdapter mediaAdapter;
    MediaFragment mediaFragment;
    FragmentManager fragmentManager;

    public OpenedMediaFragment.GalleryAdapter galleryAdapter;
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             final Bundle savedInstanceState) {


        root = inflater.inflate(R.layout.media_opened_layout, container, false);


        next = root.findViewById(R.id.next);
        back = root.findViewById(R.id.back);
        next.setVisibility(View.INVISIBLE);
        back.setVisibility(View.INVISIBLE);

        height1 = v.getHeight(); width1 = v.getWidth(); x1 = v.getLeft() - v.getWidth() +(v.getPivotX() / 2); y1 = v.getTop() -height1 + (v.getPivotY()) ; radius = ((CardView)v).getRadius();
        bottom_navigation = root.findViewById(R.id.media_bottom_navigation);
        toolbar = root.findViewById(R.id.media_toolbar);
        Drawable back_icon;
        back_icon = getResources().getDrawable(R.drawable.ic_half_transparent_back);
        toolbar.setNavigationIcon(back_icon);
        toolbar.setNavigationOnClickListener(onBackClickListener);
        image_switcher = root.findViewById(R.id.image_switcher);
        image_switcher_container = root.findViewById(R.id.image_switcher_container);

        image_switcher_container.setRadius(radius*2);
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);

                ObjectAnimator translationY = ObjectAnimator.ofFloat(bottom_navigation , "translationY" , bottom_navigation.getHeight() , 0);
                ValueAnimator height= ValueAnimator.ofFloat(v.getHeight() , root.getHeight());
                height.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = image_switcher_container.getLayoutParams();
                        layoutParams.height = (int)val;
                        image_switcher_container.setLayoutParams(layoutParams);
                    }
                });

                ValueAnimator width= ValueAnimator.ofFloat(v.getWidth() , root.getWidth());
                width.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = image_switcher_container.getLayoutParams();
                        layoutParams.width = (int) val;
                        image_switcher_container.setLayoutParams(layoutParams);
                    }
                });

                ObjectAnimator x = ObjectAnimator.ofFloat(image_switcher_container , "X" ,v.getX() , 0);
                ObjectAnimator y = ObjectAnimator.ofFloat(image_switcher_container , "Y" ,v.getY()+toolbar.getHeight() , 0);

                ObjectAnimator curve = ObjectAnimator.ofFloat(image_switcher_container , "radius" , ((CardView) v).getRadius() , 1);
                //ObjectAnimator curve = ObjectAnimator.ofFloat(image_switcher_container , "radius" , 1f);
                // why 1?
                // try 0 and u will see
                // and if u noticed nothing then that will be probably bcs they fixed it trust me u are not idiot
                ObjectAnimator alpha = ObjectAnimator.ofFloat(recyclerView, "alpha" , 1 ,0);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationY).with(alpha).with(height).with(width).with(x).with(y).with(curve);
                animatorSet.setDuration(100);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        int fx = next.getWidth() / 2;
                        int fy = next.getHeight() / 2;

                        int final_radius = (int) Math.hypot(fx , fy);
                        AnimatorSet animatorSet1 = new AnimatorSet();
                        Animator next_arrow_animation = ViewAnimationUtils.createCircularReveal(next , (int) next.getPivotX() ,(int) next.getPivotY() , 0 , final_radius );
                        Animator back_arrow_animation = ViewAnimationUtils.createCircularReveal(back , (int) back.getPivotX() ,(int) back.getPivotY() , 0 , final_radius );
                        animatorSet1.play(next_arrow_animation).with(back_arrow_animation);
                        next.setVisibility(View.VISIBLE);
                        back.setVisibility(View.VISIBLE);
                        animatorSet1.start();
                    }
                });
                animatorSet.start();

                return true;
            }
        });
        play_button = root.findViewById(R.id.play_button);

        mediaFiles = new ArrayList<>();

        if (memory_data == null | memory_data.getMediaFiles() == null) {
            //do nothing
        } else{
            for (MediaFile u : memory_data.getMediaFiles())
                mediaFiles.add(u);

        }
        if(mediaFiles.get((int)v.getTag()).getMimeType().contains("video") | mediaFiles.get((int)v.getTag()).getMimeType().contains("audio"))
            play_button.setVisibility(View.VISIBLE);
        else
            play_button.setVisibility(View.GONE);

        toolbar.setTitle(memory_data.getTitle());





        galleryAdapter = new OpenedMediaFragment.GalleryAdapter(getActivity().getSupportFragmentManager() , mediaFiles);
        image_switcher.setAdapter(galleryAdapter);
        image_switcher.setCurrentItem((int)v.getTag());
        DepthPageTransformer depthPageTransformer =  new DepthPageTransformer();
        image_switcher.setPageTransformer(false , depthPageTransformer);
        image_switcher.setOffscreenPageLimit(2);

        image_switcher.setNestedScrollingEnabled(false);

        final View delete_button = root.findViewById(R.id.delete_button);
        delete_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final int position = image_switcher.getCurrentItem();
                image_switcher.arrowScroll(View.FOCUS_LEFT);
                Handler handler= new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        mediaAdapter.removeMediaFile(position);
                        mediaAdapter.notifyItemRemoved(position);
                        if(mediaAdapter.getItemCount() == 0){
                            mediaFragment.toolbar.setVisibility(View.VISIBLE);
                            recyclerView.setAlpha(1);
                            fragmentManager.popBackStackImmediate();
                        }
                        galleryAdapter.destroyItem(image_switcher , position , galleryAdapter.instantiateItem(image_switcher , position));

                        galleryAdapter.remove_item(position);

                    }
                } , 100);

            }
        });
        play_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                current_media_file = (Fragment) galleryAdapter.instantiateItem(image_switcher, image_switcher.getCurrentItem());

                ((MediaFileFragment)current_media_file).root.callOnClick();
            }
        });

        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_switcher.arrowScroll(View.FOCUS_RIGHT);
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                image_switcher.arrowScroll(View.FOCUS_LEFT);

            }
        });

        image_switcher.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (mediaFiles.get(position).getMimeType().contains("video") | mediaFiles.get(position).getMimeType().contains("audio"))
                    play_button.setVisibility(View.VISIBLE);
                else
                    play_button.setVisibility(View.GONE);
            }

            @Override
            public void onPageScrollStateChanged(int state) {
                if(state == 1){
                    current_media_file = (Fragment) galleryAdapter.instantiateItem(image_switcher, image_switcher.getCurrentItem());
                    ((MediaFileFragment)current_media_file).pause();
                }
            }
        });


        return root;
    }

    public void setV(View v) {
        this.v = v;
    }


    public void setFragmentManager(FragmentManager fragmentManager) {
        this.fragmentManager = fragmentManager;
    }

    public void setOnBackClickListener(View.OnClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;

    }

    public void setMediaFragment(MediaFragment mediaFragment) {
        this.mediaFragment = mediaFragment;
    }

    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    public void setMemory_data(Memory memory_data) {
        this.memory_data = memory_data;
    }

    public void setMediaAdapter(MediaAdapter mediaAdapter) {
        this.mediaAdapter = mediaAdapter;
    }


    public void show_controls () {
        is_controls_on = true;

        ObjectAnimator toolbar_animation = ObjectAnimator.ofFloat(toolbar, "translationY", toolbar.getTranslationY(), 0);
        ObjectAnimator bottom_animation = ObjectAnimator.ofFloat(bottom_navigation, "translationY", bottom_navigation.getTranslationY(), 0);
        ObjectAnimator scaleY_animation = ObjectAnimator.ofFloat(image_switcher_container, "ScaleY", 1);

        int fx = next.getWidth() / 2;
        int fy = next.getHeight() / 2;

        int final_radius = (int) Math.hypot(fx, fy);
        AnimatorSet animatorSet1 = new AnimatorSet();
        Animator next_arrow_animation = ViewAnimationUtils.createCircularReveal(next, (int) next.getPivotX(), (int) next.getPivotY(), 0, final_radius);
        Animator back_arrow_animation = ViewAnimationUtils.createCircularReveal(back, (int) back.getPivotX(), (int) back.getPivotY(), 0, final_radius);
        animatorSet1.play(next_arrow_animation).with(back_arrow_animation).with(toolbar_animation).with(bottom_animation).with(scaleY_animation);
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                next.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });
        animatorSet1.setDuration(150);
        animatorSet1.start();

    }
    public void hide_controls () {
        is_controls_on = false;
        ObjectAnimator toolbar_animation = ObjectAnimator.ofFloat(toolbar, "translationY", 0, -toolbar.getHeight());
        ObjectAnimator bottom_animation = ObjectAnimator.ofFloat(bottom_navigation, "translationY", 0, bottom_navigation.getHeight());
        float scaleY = (float) root.getHeight() / (float) image_switcher_container.getHeight();
       // Log.e(getClass().getName(), scaleY + "!");
        ObjectAnimator scaleY_animation = ObjectAnimator.ofFloat(image_switcher_container, "ScaleY", scaleY);
        int fx = next.getWidth() / 2;
        int fy = next.getHeight() / 2;

        int final_radius = (int) Math.hypot(fx, fy);
        AnimatorSet animatorSet1 = new AnimatorSet();
        Animator next_arrow_animation = ViewAnimationUtils.createCircularReveal(next, (int) next.getPivotX(), (int) next.getPivotY(), final_radius, 0);
        Animator back_arrow_animation = ViewAnimationUtils.createCircularReveal(back, (int) back.getPivotX(), (int) back.getPivotY(), final_radius, 0);
        animatorSet1.play(next_arrow_animation).with(back_arrow_animation).with(toolbar_animation).with(bottom_animation).with(scaleY_animation);
        animatorSet1.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                next.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });
        animatorSet1.setDuration(150);
        animatorSet1.start();

    }

    public void ender_animations(){
        mediaFragment.gridLayoutManager.scrollToPositionWithOffset(image_switcher.getCurrentItem() , 0);
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                v = mediaFragment.gridLayoutManager.findViewByPosition(image_switcher.getCurrentItem());
               // Log.e(getClass().getName() , image_switcher.getCurrentItem() + "YAY!");
                ObjectAnimator translationY = ObjectAnimator.ofFloat(bottom_navigation , "translationY" , bottom_navigation.getHeight() );
                ValueAnimator height= ValueAnimator.ofFloat(root.getHeight() , v.getHeight());
                height.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = image_switcher_container.getLayoutParams();
                        layoutParams.height = (int)val;
                        image_switcher_container.setLayoutParams(layoutParams);
                    }
                });

                ValueAnimator width= ValueAnimator.ofFloat(root.getWidth() , v.getWidth());
                width.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        float val = (float) animation.getAnimatedValue();
                        ViewGroup.LayoutParams layoutParams = image_switcher_container.getLayoutParams();
                        layoutParams.width = (int) val;
                        image_switcher_container.setLayoutParams(layoutParams);
                    }
                });

                ObjectAnimator x = ObjectAnimator.ofFloat(image_switcher_container , "X" ,v.getX());
                ObjectAnimator y = ObjectAnimator.ofFloat(image_switcher_container , "Y" ,v.getY()+toolbar.getHeight());

                ObjectAnimator curve = ObjectAnimator.ofFloat(image_switcher_container , "radius" , ((CardView) v).getRadius());
                //ObjectAnimator curve = ObjectAnimator.ofFloat(image_switcher_container , "radius" , 1f);
                // why 1?
                // try 0 and u will see
                // and if u noticed nothing then that will be probably bcs they fixed it trust me u are not idiot
                ObjectAnimator alpha = ObjectAnimator.ofFloat(recyclerView, "alpha" , 1);

                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationY).with(alpha).with(height).with(width).with(x).with(y).with(curve);
                animatorSet.setDuration(100);
                animatorSet.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        int fx = next.getWidth() / 2;
                        int fy = next.getHeight() / 2;

                        int final_radius = (int) Math.hypot(fx , fy);
                        AnimatorSet animatorSet1 = new AnimatorSet();
                        Animator next_arrow_animation = ViewAnimationUtils.createCircularReveal(next , (int) next.getPivotX() ,(int) next.getPivotY() , final_radius , 0);
                        Animator back_arrow_animation = ViewAnimationUtils.createCircularReveal(back , (int) back.getPivotX() ,(int) back.getPivotY() , final_radius , 0);
                        animatorSet1.play(next_arrow_animation).with(back_arrow_animation);
                        animatorSet1.setDuration(100);
                        animatorSet1.start();
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        fragmentManager.popBackStackImmediate();
                        super.onAnimationEnd(animation);
                    }
                });
                animatorSet.start();


            }
        } , 100);

    }



    class DepthPageTransformer implements ViewPager.PageTransformer {
        private static final float MIN_SCALE = 0.75f;


        public void transformPage(View view, float position) {
            int pageWidth = view.getWidth();

            if (position < -1) { // [-Infinity,-1)
                // This page is way off-screen to the left.
                view.setAlpha(0f);

            } else if (position <= 0) { // [-1,0]
                // Use the default slide transition when moving to the left page
                view.setAlpha(1f);
                view.setTranslationX(0f);
                view.setScaleX(1f);
                view.setScaleY(1f);

            } else if (position <= 1) { // (0,1]
                // Fade the page out.
                view.setAlpha(1 - position);

                // Counteract the default slide transition
                view.setTranslationX(pageWidth * -position);

                // Scale the page down (between MIN_SCALE and 1)
                float scaleFactor = MIN_SCALE
                        + (1 - MIN_SCALE) * (1 - Math.abs(position));
                view.setScaleX(scaleFactor);
                view.setScaleY(scaleFactor);

            } else { // (1,+Infinity]
                // This page is way off-screen to the right.
                view.setAlpha(0f);
            }
        }


    }

    public MediaFileFragment getFragment (){
        return ((MediaFileFragment)galleryAdapter.instantiateItem(image_switcher , image_switcher.getCurrentItem()));
    }
    private class GalleryAdapter extends FragmentStatePagerAdapter {

        ArrayList<MediaFile> uris;
        public GalleryAdapter(FragmentManager fm , ArrayList<MediaFile> uris) {
            super(fm);
            this.uris = uris;
        }

        @Override
        public Fragment getItem(int position) {
            position = position % uris.size();
            image_uri = uris.get(position).getPath();
            simpleImageFragment = new MediaFileFragment();
            simpleImageFragment.setMediaFile(uris.get(position));
            simpleImageFragment.setPlayButton(play_button);
            simpleImageFragment.setOpenedMediaFragment(OpenedMediaFragment.this);
            return simpleImageFragment;
        }

        @Override
        public int getCount() {
            return uris.size();
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            //Log.e(getClass().getName() , "i came here!");
            Object obj = super.instantiateItem(container, position);
            return obj;
        }


        //this is called when notifyDataSetChanged() is called
        @Override
        public int getItemPosition(Object object) {
            // refresh all fragments when data set changed
            return PagerAdapter.POSITION_NONE;
        }

        public void remove_item (int position){
            uris.remove(position);
            notifyDataSetChanged();
        }
    }
}




