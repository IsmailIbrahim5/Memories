package com.idea.memories.Views.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.idea.memories.Classes.ColorsGenerator;
import com.idea.memories.Views.CustomViews.ImageShape;
import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;
import com.idea.memories.R;

import com.idea.memories.Views.CustomViews.CircularRevealFrameLayout;
import com.idea.memories.Views.Activities.MainActivity;
import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Classes.UserData;
import com.idea.memories.Views.Activities.NightMode;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import java.io.File;

import com.idea.memories.Adapters.MemoriesAdapter;

import androidx.appcompat.app.AppCompatDelegate;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.core.view.MenuItemCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.interpolator.view.animation.FastOutSlowInInterpolator;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class NavigationDrawerFragment extends Fragment {
    public boolean recently_deleted;
    ImageView nav_button;
    public OpenedMemoryFragment o;
    FragmentTransaction transaction;
    FragmentManager fragmentManager;
    public DrawerLayout drawerLayout;
    NavigationView nav_view;
    public boolean is_memory_open;
    public CircularRevealFrameLayout circular_animation_view;
    public int[] coordinates = null;
    public ImageView image;
    ColorsGenerator colorsGenerator;
    public boolean is;
    MainFragment mainFragment;
    TrashFragment trashFragment;
    FavouriteFragment favouriteFragment;
    SupportUsFragment supportUsFragment;
    View root, fake;
    Window window;
    private int memoryPosition = -1 , width , height;
    ImageView avatar_photo;
    TextView i_dont_know_yet;
    UserData user_profile;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_navigation_drawer, container, false);
        user_profile = new UserData(getContext());
        circular_animation_view = root.findViewById(R.id.opened_memory_frame_layout);

        window = getActivity().getWindow();
        window.setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color));
        colorsGenerator = new ColorsGenerator(getContext());
        nav_button = root.findViewById(R.id.nav_button);

        drawerLayout = root.findViewById(R.id.drawer_layout);

        nav_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                drawerLayout.openDrawer(Gravity.LEFT, true);
            }
        });

        nav_view = root.findViewById(R.id.nav_view);

        final View header_view = inflater.inflate(R.layout.nav_header, null);

        header_view.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));

        header_view.findViewById(R.id.close_drawer_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                drawerLayout.closeDrawer(GravityCompat.START, true);
            }
        });

        final CardView bl = header_view.findViewById(R.id.bl);
        avatar_photo = header_view.findViewById(R.id.profile_picture);

        bl.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                final View grey_layer = header_view.findViewById(R.id.grey_layer);

                final ImageView edit_icon = header_view.findViewById(R.id.edit_icon);
                edit_icon.setVisibility(View.VISIBLE);
                grey_layer.animate().alpha(1).setDuration(300).start();
                edit_icon.animate().alpha(1).setDuration(300).start();
                edit_icon.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        grey_layer.animate().alpha(0).setDuration(300).start();
                        edit_icon.setVisibility(View.GONE);
                        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            requestPermissions(permission, 5);
                        } else {
                            Log.d(getClass().getName(), "All Permissions Are Granted");
                            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                            startActivityForResult(pickIntent, 5);
                        }
                    }
                });
                drawerLayout.addDrawerListener(new DrawerLayout.SimpleDrawerListener() {
                    @Override
                    public void onDrawerStateChanged(int newState) {
                        if (grey_layer.getAlpha() == 1f) {
                            grey_layer.animate().alpha(0).setDuration(300).start();
                            edit_icon.animate().alpha(0).setDuration(300).start();
                            edit_icon.setVisibility(View.GONE);
                        }
                    }
                });
                return false;
            }
        });

        nav_view.addHeaderView(header_view);

        if (user_profile.getData().getUserProfilePicture() == null) {
            i_dont_know_yet = header_view.findViewById(R.id.i_dont_know_yet);
            i_dont_know_yet.setText(String.valueOf(user_profile.getData().getUserName().charAt(0)));
        } else {
            avatar_photo.post(new Runnable() {
                @Override
                public void run() {
                    width = avatar_photo.getWidth();
                    height = avatar_photo.getHeight();
                    ((MainActivity) getActivity()).loadingBitmap(new MediaFile(Uri.fromFile(new File(user_profile.getData().getUserProfilePicture())), "image", 223), avatar_photo, width, height);
                }
            });
        }


        ((TextView) header_view.findViewById(R.id.user_name)).setText(user_profile.getData().getUserName());
        fragmentManager = getChildFragmentManager();

        trashFragment = new TrashFragment();
        supportUsFragment = new SupportUsFragment();
        favouriteFragment = new FavouriteFragment();
        favouriteFragment.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_memory(v, (Memory) v.getTag(), favouriteFragment.recyclerView);
            }
        });

        trashFragment.onClickListener = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                open_memory(v, (Memory) v.getTag(), trashFragment.recyclerView);
            }
        };

        mainFragment = new MainFragment();
        mainFragment.setMemoryPosition(memoryPosition);
        transaction = fragmentManager.beginTransaction();
        nav_view.setCheckedItem(R.id.nav_home_button);

        transaction.replace(R.id.frame_layout, mainFragment);
        transaction.addToBackStack(null);
        transaction.commit();
        nav_view.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {
                if (nav_view.getCheckedItem() != menuItem) {
                    if (menuItem.getItemId() == R.id.nav_home_button) {
                        mainFragment = new MainFragment();
                        fragmentManager.popBackStackImmediate();
                        transaction = fragmentManager.beginTransaction();
                        nav_view.setCheckedItem(R.id.nav_home_button);

                        transaction.replace(R.id.frame_layout, mainFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    } else if (menuItem.getItemId() == R.id.nav_favourite_button) {
                        fragmentManager.popBackStackImmediate();
                        transaction = fragmentManager.beginTransaction();
                        nav_view.setCheckedItem(R.id.nav_favourite_button);

                        transaction.replace(R.id.frame_layout, favouriteFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();

                    } else if (menuItem.getItemId() == R.id.nav_trash_button) {
                        fragmentManager.popBackStackImmediate();
                        transaction = fragmentManager.beginTransaction();
                        nav_view.setCheckedItem(R.id.nav_trash_button);
                        transaction.replace(R.id.frame_layout, trashFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                    else if (menuItem.getItemId() == R.id.nav_support_us_button) {
                        fragmentManager.popBackStackImmediate();
                        transaction = fragmentManager.beginTransaction();
                        nav_view.setCheckedItem(R.id.nav_support_us_button);
                        transaction.replace(R.id.frame_layout, supportUsFragment);
                        transaction.addToBackStack(null);
                        transaction.commit();
                    }
                }
                drawerLayout.closeDrawer(GravityCompat.START, true);
                return true;
            }
        });

        ImageView night_mode_switch = MenuItemCompat.getActionView(nav_view.getMenu().getItem(4)).findViewById(R.id.night_mode_switch);
        final AnimationDrawable animatedVectorDrawableCompat;
        is = getActivity().getSharedPreferences("nightMode", MODE_PRIVATE).getInt("isNightModeOn", 1) == AppCompatDelegate.MODE_NIGHT_YES;

        if(is)
             animatedVectorDrawableCompat = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.anim_night_mode_switch);
        else
             animatedVectorDrawableCompat = (AnimationDrawable) getActivity().getResources().getDrawable(R.drawable.anim_day_mode_switch);

        night_mode_switch.setImageDrawable(animatedVectorDrawableCompat);
        night_mode_switch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                fake.callOnClick();
            }
        });


        fake = new View(getContext());
        fake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                animatedVectorDrawableCompat.start();
                Handler h= new Handler();
                h.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if(is)
                            NightMode.day();
                        else
                            NightMode.night();
                    }
                } , 375);
            }
        });
        return root;
    }

    AnimatedVectorDrawableCompat animatedVectorDrawableCompat;


    View container;

    public void open_memory(final View view, final Memory memory_data, final RecyclerView recyclerView) {

        image = null;
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }


        if (recyclerView.isClickable()) {
            recyclerView.setClickable(false);
            new Thread() {
                @Override
                public void run() {
                    final MemoriesAdapter adapter = (MemoriesAdapter) recyclerView.getAdapter();
                    o = new OpenedMemoryFragment();
                    o.setBack_icon_click_listener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            view.setClickable(false);
                            if (o.title_edit.getVisibility() == View.VISIBLE) {
                                Log.e(getClass().getName(), "WTF!");

                                if (!o.title_edit.getText().toString().trim().isEmpty())
                                    memory_data.setTitle(o.title_edit.getText().toString());
                                if (!o.desc_edit.getText().toString().trim().isEmpty() & o.desc_edit.getText().toString() != "null")
                                    memory_data.setDesc(o.desc_edit.getText().toString());
                                else
                                    memory_data.setDesc("");
                            }
                            MemoriesGenerator mg = new MemoriesGenerator(getContext());
                            mg.updateMemory(memory_data);
                            adapter.notifyItemChanged(memory_data.getPosition());
                            if (image != null) {
                                container.setVisibility(View.VISIBLE);
                                image.setVisibility(View.VISIBLE);
                                image.setBackground(null);
                                image.setScaleX(1);
                                image.setScaleY(1);
                            }
                            o.enderAnimations(NavigationDrawerFragment.this, recyclerView, memory_data);
                            if (adapter.searchKey != null) {
                                adapter.memories.clear();
                                adapter.memories = mg.search(adapter.searchKey, adapter.mnen);
                                adapter.notifyDataSetChanged();
                            }
                            Log.e(getClass().getName(), adapter.memories.size() + "?");
                        }
                    });
                    o.setRecyclerView(recyclerView);
                    o.setMemory_data(memory_data);
                    transaction = fragmentManager.beginTransaction();
                    transaction.add(R.id.opened_memory_frame_layout, o);
                    transaction.addToBackStack(null);
                }
            }.start();

            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);
            is_memory_open = true;

            ObjectAnimator scaleX = ObjectAnimator.ofFloat(null, "ScaleX", 1, 0.3f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(null, "ScaleY", 1, 0.3f);

            if (view instanceof FloatingActionButton) {
                animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.ic_add);
                ((FloatingActionButton) view).setImageDrawable(animatedVectorDrawableCompat);
                scaleX.setTarget(view);
                scaleY.setTarget(view);
            } else {
                container = view.findViewById(R.id.container);
                container.setVisibility(View.GONE);
                animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_memory_to_circle);
                image = view.findViewById(R.id.background_image);
                image.setBackground(animatedVectorDrawableCompat);
                if (memory_data.isDeleted()) {
                    image.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorsGenerator.deleted)));
                } else {
                    image.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(memory_data.getColorHex())));
                }
                scaleX.setTarget(image);
                scaleY.setTarget(image);
            }

            final AnimatorSet a = new AnimatorSet();
            a.play(scaleX).with(scaleY);
            a.setDuration(100);
            animatedVectorDrawableCompat.start();
            a.setInterpolator(new FastOutSlowInInterpolator());
            a.start();
            a.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            root.getViewTreeObserver().removeOnPreDrawListener(this);

/*
                                final ImageView circular_reveal = circular_animation_view.findViewById(R.id.circular_reveal);



                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(circular_reveal, "scaleX", 0.15f, 3.0f);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(circular_reveal, "scaleY", 0.15f, 3.0f);

                                scaleX.setInterpolator(new FastOutLinearInInterpolator());
                                scaleY.setInterpolator(new FastOutLinearInInterpolator());


//                                ObjectAnimator pivotX= ObjectAnimator.ofFloat(circular_reveal , "PivotX" , coordinates[0] + (view.getWidth()/2) , circular_animation_view.getPivotX());
                                //                              ObjectAnimator pivotY= ObjectAnimator.ofFloat(circular_reveal , "PivotY" , coordinates[1]  , circular_animation_view.getPivotY());

                                //                          AnimatorSet aa= new AnimatorSet();
                                //                            aa.play(pivotX).with(pivotY);
                                //                        aa.setDuration(10000);
                                //                      aa.start();
                                //circular_reveal.setPivotX(root.getPivotX());
                                //circular_reveal.setPivotY(root.getPivotY());

                                Log.e(getClass().getName(), root.getPivotX() + "!");
                                ValueAnimator pivotX = ValueAnimator.ofFloat(view.getX() + view.getPivotX(), root.getPivotX());
                                pivotX.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float newValue = (float) animation.getAnimatedValue();
                                        Log.e(getClass().getName(), newValue + "!");
                                        circular_reveal.setPivotX(newValue);
                                    }
                                });
                                ValueAnimator pivotY = ValueAnimator.ofFloat(coordinates[1] + view.getPivotY(), root.getPivotY());
                                pivotY.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                                    @Override
                                    public void onAnimationUpdate(ValueAnimator animation) {
                                        float newValue = (float) animation.getAnimatedValue();
                                        Log.e(getClass().getName(), newValue + "!");
                                        circular_reveal.setPivotY(newValue);
                                    }
                                });

                                pivotX.setInterpolator(new LinearOutSlowInInterpolator());
                                pivotY.setInterpolator(new LinearOutSlowInInterpolator());
                                if (memory_data.isDeleted())
                                    circular_reveal.setImageTintList(ColorStateList.valueOf(Color.parseColor(colors.deleted)));
                                else
                                    circular_reveal.setImageTintList(ColorStateList.valueOf(Color.parseColor(memory_data.getColorHex())));

                                AnimatorSet a = new AnimatorSet();
                                a.play(scaleX).with(scaleY).with(pivotX).with(pivotY);

                                a.setDuration(300);
//                                a.start();
*/


                            coordinates = new int[2];

                            view.getLocationOnScreen(coordinates);

                            int sx = animatedVectorDrawableCompat.getIntrinsicWidth() / 2;
                            int sy = animatedVectorDrawableCompat.getIntrinsicHeight() / 2;

                            final float startRadius = (float) Math.hypot(sx, sy);

                            int fx = root.getWidth() / 2;
                            int fy = root.getHeight() / 2;

                            //why 16?
                            //cuz this is the age that im gonna so fkng rich and famous on it
                            float finalRadius = (float) Math.max(fx, fy);
                            finalRadius += finalRadius/2;

                            Log.e(getClass().getName(), "Final radius = " + finalRadius);
                            final int color;
                            if (memory_data.isDeleted())
                                color = Color.parseColor(colorsGenerator.deleted);
                            else
                                color = Color.parseColor(memory_data.getColorHex());


                            float centerX, centerY;

                            if (view instanceof FloatingActionButton) {
                                view.setVisibility(View.GONE);
                                centerY = coordinates[1] - (view.getPivotY() / 2);
                                centerX = root.getPivotX();
                            } else {
                                image.setVisibility(View.GONE);
                                centerX = coordinates[0] + view.getPivotX();
                                centerY = coordinates[1] + (view.getPivotY() / 2) + sy;
                            }

                            circular_animation_view.Reveal(startRadius, finalRadius, centerX, centerY, 300, color).addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    circular_animation_view.setBackgroundColor(color);
                                    transaction.commit();
                                }
                            });

                            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
                            if (memory_data.isDeleted()) {
                                window.setStatusBarColor(Color.parseColor(colorsGenerator.deleted));
                                window.setNavigationBarColor(Color.parseColor(colorsGenerator.deleted));
                            } else {
                                window.setStatusBarColor(Color.parseColor(memory_data.getColorHex()));
                                window.setNavigationBarColor(Color.parseColor(memory_data.getColorHex()));
                            }
                            return true;
                        }

                    });
                }
            });
        }
    }


    public void close_memory(final RecyclerView recyclerView, final Memory memory_data) {
        int firstVisibleItemPosition = ((GridLayoutManager) recyclerView.getLayoutManager()).findFirstVisibleItemPosition();
        Log.e(getClass().getName(), memory_data.getPosition() + " - " + firstVisibleItemPosition);
        final View view = recyclerView.getLayoutManager().getChildAt(memory_data.getPosition() - firstVisibleItemPosition);
        try {
            image = view.findViewById(R.id.background_image);
        } catch (Exception e) {
            image = new ImageShape(getContext());
        }
        fragmentManager.popBackStackImmediate();
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);



                if (!recently_deleted) {
                    try {
                        view.findViewById(R.id.container).setVisibility(View.GONE);
                        circular_animation_view.setVisibility(View.VISIBLE);
                        image.setVisibility(View.GONE);
                    } catch (Exception e) {

                    }
                }
                final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_circle_to_memory);

                int sx = animatedVectorDrawableCompat.getIntrinsicWidth() / 2;
                int sy = animatedVectorDrawableCompat.getIntrinsicHeight() / 2;


                if (mainFragment.addMemoryButton.getVisibility() == View.GONE) {
                    mainFragment.addMemoryButton.setVisibility(View.VISIBLE);
                    mainFragment.addMemoryButton.setScaleX(1);
                    mainFragment.addMemoryButton.setScaleY(1);
                    mainFragment.addMemoryButton.setTranslationY(0);
                    mainFragment.addMemoryButton.setImageDrawable(getResources().getDrawable(R.drawable.ic_add));
                }
                final int color;
                if (memory_data.isDeleted()) {
                    color = Color.parseColor(colorsGenerator.deleted);
                } else {
                    color = Color.parseColor(memory_data.getColorHex());
                }
                image.getLocationOnScreen(coordinates);
                final float startRadius = (float) Math.hypot(sx, sy);

                int fx = root.getWidth() / 2;
                int fy = root.getHeight() / 2;

                //why 16?
                //cuz this is the age when im gonna so fkng rich and famous on it
                float finalRadius = (float) Math.max(fx, fy);
                finalRadius += finalRadius/2;


                circular_animation_view.setBackgroundColor(getResources().getColor(android.R.color.transparent));
                if (!recently_deleted) {
                    try {
                        circular_animation_view.Hide(finalRadius, startRadius, coordinates[0] + view.getPivotX(), coordinates[1] + (view.getPivotY() / 2) + sy, 300, color).addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                image.setVisibility(View.VISIBLE);
                                image.setScaleX(0.3f);
                                image.setScaleY(0.3f);

                                image.setBackground(animatedVectorDrawableCompat);

                                if (memory_data.isDeleted()) {
                                    image.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorsGenerator.deleted)));
                                } else {
                                    image.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(memory_data.getColorHex())));
                                }


                                ObjectAnimator scaleX = ObjectAnimator.ofFloat(image, "ScaleX", 0.3f, 1);
                                ObjectAnimator scaleY = ObjectAnimator.ofFloat(image, "ScaleY", 0.3f, 1);

                                AnimatorSet a = new AnimatorSet();
                                a.play(scaleX).with(scaleY);
                                a.setDuration(100);
                                a.setInterpolator(new FastOutSlowInInterpolator());
                                a.start();
                                animatedVectorDrawableCompat.start();
                                a.addListener(new AnimatorListenerAdapter() {
                                    @Override
                                    public void onAnimationEnd(Animator animation) {

                                        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                        try {
                                            image.setBackground(null);
                                        } catch (Exception e) {
                                        }
                                        recyclerView.setClickable(true);
                                        if (memory_data.isDeleted()) {
                                            ((ImageShape) view.findViewById(R.id.background_photo)).setPathColor(Color.parseColor(colorsGenerator.deleted));
                                        } else {
                                            ((ImageShape) view.findViewById(R.id.background_photo)).setPathColor(Color.parseColor(memory_data.getColorHex()));
                                        }
                                        if (memory_data.getMainPhoto() != null) {

                                            view.findViewById(R.id.background_photo).getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                                                @Override
                                                public boolean onPreDraw() {
                                                    view.findViewById(R.id.background_photo).getViewTreeObserver().removeOnPreDrawListener(this);
                                                    ((MainActivity) getActivity()).loadingBitmap(memory_data.getMainPhoto(), (ImageShape) view.findViewById(R.id.background_photo), view.findViewById(R.id.background_photo).getWidth(), view.findViewById(R.id.background_photo).getHeight());
                                                    return false;
                                                }
                                            });
                                        }

                                        view.findViewById(R.id.container).setVisibility(View.VISIBLE);
                                        Log.e(getClass().getName(), memory_data.getColorHex());
                                    }
                                });

                            }
                        });
                    } catch (Exception ignored){
                        recently_deleted = false;
                        circular_animation_view.Hide(finalRadius, startRadius, circular_animation_view.getPivotX(), circular_animation_view.getHeight() + 100, 500, color).addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                image.setScaleY(1);
                                image.setScaleX(1);
                                drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                                image.setBackground(null);
                                recyclerView.setClickable(true);

                            }
                        });
                    }
                }
                else{
                    recently_deleted = false;
                    circular_animation_view.Hide(finalRadius, startRadius, circular_animation_view.getPivotX(), circular_animation_view.getHeight() + 100, 500, color).addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            image.setScaleY(1);
                            image.setScaleX(1);
                            drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
                            image.setBackground(null);
                            recyclerView.setClickable(true);

                        }
                    });
                }
                return true;
            }
        });

 /*ImageShape image = (ImageShape) open_view;

        final AnimatedVectorDrawableCompat animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext() , R.drawable.anim_circle_to_memory);

        image.setElevation(0); image.setPath_string("M 24 12.53 C 24 13.955 23.021 15.153 21.692 15.506 C 21.703 15.639 21.709 15.775 21.709 15.912 C 21.709 17.929 20.526 19.563 19.067 19.563 C 18.548 19.563 18.064 19.356 17.655 18.998 C 16.889 19.91 15.485 20.521 13.878 20.521 C 12.57 20.521 11.396 20.115 10.591 19.472 C 9.748 20.397 8.37 21 6.811 21 C 4.244 21 2.164 19.365 2.164 17.349 C 2.164 16.971 2.239 16.608 2.375 16.265 C 1.263 15.761 0.509 14.787 0.509 13.667 C 0.509 13.341 0.574 13.027 0.692 12.734 C 0.264 12.341 -0.001 11.804 -0.001 11.213 C -0.001 10.352 0.558 9.609 1.369 9.253 C 1.264 9.018 1.209 8.773 1.209 8.52 C 1.209 6.884 3.532 5.557 6.397 5.557 C 7.232 5.557 8.021 5.67 8.72 5.871 C 9.608 4.763 11.441 4.001 13.559 4.001 C 16.548 4.001 18.97 5.515 18.97 7.383 C 18.97 7.393 18.969 7.402 18.969 7.412 C 19.406 7.26 19.905 7.174 20.434 7.174 C 22.157 7.174 23.553 8.085 23.553 9.209 C 23.553 9.618 23.367 9.998 23.049 10.317 C 23.634 10.877 23.999 11.662 23.999 12.531 Z"); image.setBackground(animatedVectorDrawableCompat);
        int sx = animatedVectorDrawableCompat.getIntrinsicWidth() / 2;
        int sy = animatedVectorDrawableCompat.getIntrinsicHeight() / 2;

        final float startRadius= (float) Math.hypot(sx , sy);

        int fx = root.getWidth();
        int fy = root.getHeight();

        float finalRadius= (float) Math.max(fx , fy);


        int coordinates[] = new int[2];
        image.getLocationOnScreen(coordinates);

        Animator animator = ViewAnimationUtils.createCircularReveal(circular_animation_view , coordinates[0]+sx , (int) coordinates[1], finalRadius , startRadius);

        animator.start();

        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                animatedVectorDrawableCompat.start();
            }
        });
        */
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));

        window.setNavigationBarColor(getResources().getColor(R.color.navigation_bar_color));


        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(INPUT_METHOD_SERVICE);
        try {
            imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        } catch (Exception e) {
        }

    }

    public void setMemoryPosition(int memoryPosition) {
        this.memoryPosition = memoryPosition;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (grantResults[0] == 0) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, 5);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            final Uri profile_picture = data.getData();
            avatar_photo.post(new Runnable() {
                @Override
                public void run() {
                    ((MainActivity) getActivity()).loadingBitmap(new MediaFile(profile_picture, "image", 222), avatar_photo, avatar_photo.getWidth(), avatar_photo.getHeight());
                    if(i_dont_know_yet != null)
                    i_dont_know_yet.setVisibility(View.GONE);
                }
            });

                    user_profile.writeData(user_profile.getData().getUserName(), profile_picture);
        } catch (Exception e) {
            Toast.makeText(getContext(), e.getLocalizedMessage(), Toast.LENGTH_SHORT).show();
        }
    }

}
