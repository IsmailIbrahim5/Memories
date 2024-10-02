package com.idea.memories.Views.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Classes.DateGenerator;
import com.idea.memories.R;

import com.idea.memories.Views.Activities.MainActivity;
import com.idea.memories.Views.CustomViews.CircularRevealFrameLayout;
import com.idea.memories.Classes.ColorsGenerator;
import com.idea.memories.Views.CustomViews.ImageShape;
import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;
import com.idea.memories.Views.CustomViews.OnSwipeTouchListener;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import com.idea.memories.Adapters.MemoriesAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class OpenedMemoryFragment extends Fragment{
    CollapsingToolbarLayout collapsingToolbarLayout;
    public FloatingActionButton edit_fab;
    FloatingActionButton color_fab;
    public FloatingActionButton media_fab;
    ViewSwitcher my_switcher;
    public ViewSwitcher media_button;
    public EditText desc_edit;
    public EditText title_edit;
    TextView desc, date;
    DateGenerator dateGenerator;
    View.OnClickListener back_icon_click_listener;
    public MemoriesAdapter adapter;
    public Memory memory_data;
    String colorHex, lightColorHex;
    ImageView  toolbar_background, media_thumb;
    AppBarLayout.LayoutParams p;
    View root;
    public CircularRevealFrameLayout circular_animation_view;
    LinearLayout fabs;
    MemoriesGenerator mg;
    public MediaFragment mediaFragment;
    public boolean is_media_on;
    FragmentTransaction fragmentTransaction;
    public FragmentManager fragmentManager;
    View top_side , bot_side;
    AppBarLayout appBarLayout;
    DatePickerDialog datePickerDialog;
    RecyclerView recyclerView;
    public View fake;
    ColorsGenerator colorsGenerator;
    NestedScrollView nestedScrollView;
    Menu menu;
    Drawable back_icon;
    Toolbar toolbar;
    float last_sp;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_opened_memory, container, false);
        setHasOptionsMenu(true);
        collapsingToolbarLayout = root.findViewById(R.id.collapsing);
        fabs = root.findViewById(R.id.fabs);
        edit_fab = root.findViewById(R.id.edit_fab);
        color_fab = root.findViewById(R.id.color_fab);
        media_fab = root.findViewById(R.id.media_fab);
        top_side = root.findViewById(R.id.top_side);
        bot_side = root.findViewById(R.id.bot_side);
        my_switcher = root.findViewById(R.id.my_switcher);
        media_button = root.findViewById(R.id.media_button);
        media_thumb = root.findViewById(R.id.media_thumb);
        desc_edit = root.findViewById(R.id.desc_edit);
        desc = root.findViewById(R.id.desc);
        date = root.findViewById(R.id.date);
        title_edit = root.findViewById(R.id.title_edit);
        toolbar_background = root.findViewById(R.id.memory_background);
        circular_animation_view = root.findViewById(R.id.media_frame);
        fragmentManager = getActivity().getSupportFragmentManager();
        fragmentTransaction = fragmentManager.beginTransaction();

        colorsGenerator = new ColorsGenerator(getContext());
        p = (AppBarLayout.LayoutParams) collapsingToolbarLayout.getLayoutParams();
        toolbar = root.findViewById(R.id.opened_memory_toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowHomeEnabled(true);
        back_icon = getResources().getDrawable(R.drawable.ic_back);
        if(memory_data.getMainPhoto() != null & !memory_data.isDeleted()){
            back_icon.setTint(getResources().getColor(R.color.half_transparent));
        }
        else{
            title_edit.setTextColor(Color.WHITE);
            title_edit.setBackgroundTintList(ColorStateList.valueOf(Color.WHITE));
            collapsingToolbarLayout.setCollapsedTitleTextColor(Color.WHITE);
            collapsingToolbarLayout.setExpandedTitleColor(Color.WHITE);
            date.setTextColor(Color.WHITE);
            if(((MainActivity)getActivity()).nightMode == AppCompatDelegate.MODE_NIGHT_YES)
            back_icon.setTint(getResources().getColor(R.color.light_grey));
            else
                back_icon.setTint(Color.WHITE);
        }
        toolbar.setNavigationIcon(back_icon);
        toolbar.setNavigationOnClickListener(back_icon_click_listener);
        toolbar.inflateMenu(R.menu.opened_memory_menu);
        final float size = title_edit.getTextSize();

        appBarLayout = root.findViewById(R.id.app_bar);
        appBarLayout.addOnOffsetChangedListener(new AppBarLayout.BaseOnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
                float value = (-i / (float) appBarLayout.getTotalScrollRange());
                if (value > 0) {
                    root.findViewById(R.id.date).animate().alpha(0).setDuration(50).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            root.findViewById(R.id.date).setVisibility(View.GONE);
                            super.onAnimationEnd(animation);
                        }
                    });
                 } else if (i == 0) {
                    root.findViewById(R.id.date).animate().alpha(1).setDuration(200).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationStart(Animator animation) {
                            root.findViewById(R.id.date).setVisibility(View.VISIBLE);
                            super.onAnimationStart(animation);
                        }
                    });
                }

                value = 1 - value;
                float sp = ((value * size) / getResources().getDisplayMetrics().scaledDensity);
                float maximum = (size / getResources().getDisplayMetrics().scaledDensity) / 2;
                if (sp >= maximum) {
                    title_edit.setTextSize(sp);
                }

                sp = (value * size);
                if(last_sp != sp) {
                    last_sp =sp;
                    ((CollapsingToolbarLayout.LayoutParams) title_edit.getLayoutParams()).bottomMargin = (int) sp;

                    title_edit.requestLayout();
                }

            }
        });
        final Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "comicbd.ttf");
        collapsingToolbarLayout.setCollapsedTitleTypeface(tf);
        collapsingToolbarLayout.setExpandedTitleTypeface(tf);
        title_edit.setTypeface(tf);

        edit_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AnimatedVectorDrawableCompat animatedVectorDrawableCompat;
                if (my_switcher.getCurrentView().getId() == R.id.desc) {
                    animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_edit_to_done);
                    edit_mode();
                } else {
                    animatedVectorDrawableCompat = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_done_to_edit);
                    switch_read_mode();
                }
                edit_fab.setImageDrawable(animatedVectorDrawableCompat);
                animatedVectorDrawableCompat.start();
            }
        });

        media_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!is_media_on)
                    open_media_fragment();
            }
        });
        media_thumb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                media_fab.callOnClick();
            }
        });
        color_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorHex = colorsGenerator.next(colorHex);
                lightColorHex = colorsGenerator.getLightColor(colorHex);
                memory_data.setColorHex(colorHex);
                color();
            }
        });

        putData();

        try {
          //  Log.e(getClass().getName(), memory_data.getBackground_photo().getPath().getPath());
        } catch (Exception e) {

        }


        color();

        fake = new View(getContext());
        fake.setOnClickListener(back_icon_click_listener);
        nestedScrollView = root.findViewById(R.id.nested_scroll_view);

        nestedScrollView.setOnTouchListener(new OnSwipeTouchListener(getContext()) {
            @Override
            public void onSwipeLeft() {
                color_fab.callOnClick();
            }

            @Override
            public void onSwipeRight() {
                colorHex = colorsGenerator.previous(colorHex);
                lightColorHex = colorsGenerator.getLightColor(colorHex);
                memory_data.setColorHex(colorHex);
                color();
            }
        });



        appBarLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 10);
                } else {
              //      Log.d(getClass().getName(), "All Permissions Are Granted");
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                    OpenedMemoryFragment.this.startActivityForResult(pickIntent, 55);
                }
            }
        });
        date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog.show();
                datePickerDialog.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor(memory_data.getColorHex()));
                datePickerDialog.getButton(DatePickerDialog.BUTTON_NEGATIVE).setTextColor(Color.parseColor(memory_data.getColorHex()));
            }
        });

        date.setClickable(false);
        appBarLayout.setClickable(false);
        media_button.getCurrentView().setClickable(false);
        color_fab.setClickable(false);
        adapter = (MemoriesAdapter) recyclerView.getAdapter();
        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

            if (memory_data.getDesc().equalsIgnoreCase("new")) {
                memory_data.setDesc("");
                desc.setText("");
                edit_mode();
            }
        starterAnimations();
    }

    private void edit_mode() {
        my_switcher.showNext();
        title_edit.setVisibility(View.VISIBLE);

        title_edit.setText(collapsingToolbarLayout.getTitle());
        desc_edit.setText(desc.getText());

circular_ravel_animation();

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int heightDiff = root.getRootView().getHeight() - root.getHeight();
                if (heightDiff > dpToPx(200))
                    fabs.setVisibility(View.INVISIBLE);
                else
                    fabs.setVisibility(View.VISIBLE);

            }

        });
        collapsingToolbarLayout.setTitle(" ");
        edit_fab.setImageDrawable(getContext().getDrawable(R.drawable.anim_done_to_edit));
        date.setClickable(true);
        appBarLayout.setClickable(true);
        nestedScrollView.performClick();
    }

    private void switch_read_mode() {
        ((TextView) root.findViewById(R.id.desc)).setText(((TextView) my_switcher.getCurrentView()).getText());
        my_switcher.showPrevious();
        title_edit.setVisibility(View.GONE);
        if (!title_edit.getText().toString().trim().isEmpty()) {
            collapsingToolbarLayout.setTitle(title_edit.getText());
            memory_data.setTitle(title_edit.getText().toString());
        } else {
            collapsingToolbarLayout.setTitle(memory_data.getTitle());
        }
        memory_data.setDesc(desc_edit.getText().toString());
        circular_hide_animation();
        date.setClickable(false);
        appBarLayout.setClickable(false);
    }

    private void putData() {
        collapsingToolbarLayout.setTitle(memory_data.getTitle());
        date.setText(memory_data.getDate());
        desc.setText(memory_data.getDesc());
        colorHex = memory_data.getColorHex();
        lightColorHex = colorsGenerator.getLightColor(colorHex);
        if (memory_data.isDeleted())
            edit_fab.setVisibility(View.GONE);
        else {
            if (memory_data.getMainPhoto() != null ) {

                root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        toolbar_background.getViewTreeObserver().removeOnPreDrawListener(this);
                        try {
                            ((MainActivity)getActivity()).loadingBitmap(memory_data.getMainPhoto() , toolbar_background , toolbar_background.getWidth() , toolbar_background.getHeight());
                          }catch (Exception e){
                        //    Log.e(getClass().getName() , "Error : "+e.getMessage());
                        }
                        return false;
                    }
                });
            }
           // else
                //Log.e(getClass().getName() , "LOL");
        }
        try {
                for(int i = 0; i <= memory_data.getMediaFiles().length-1 ; i++){
                    if(memory_data.getMediaFiles()[i].getMimeType().contains("image")){
                        media_thumb.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver() , memory_data.getMediaFiles()[i].getID() , MediaStore.Images.Thumbnails.MINI_KIND , new BitmapFactory.Options()));
                        media_button.showNext();
                        break;
                    }
              //  Log.e(getClass().getName(), memory_data.getMedia_files()[0].getPath().getPath());
            }
        } catch (Exception e) {
          //  Log.e(getClass().getName(), e.getMessage());
        }

        title_edit.setText(memory_data.getTitle());
    }


    private void circular_ravel_animation() {
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(media_fab, "scaleX", 0, 1);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(media_fab, "scaleY", 0, 1);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(color_fab , "scaleX" , 0 , 1);
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(color_fab , "scaleY" , 0 , 1);
        AnimatorSet a = new AnimatorSet();
        if(media_button.getCurrentView() == media_fab) {
            a.play(scaleX).with(scaleX2).with(scaleY).with(scaleY2);
        }
        else
            a.play(scaleX2).with(scaleY2);
        a.setDuration(200);
        a.start();
        a.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                media_button.getCurrentView().setClickable(true);
                 color_fab.setClickable(true);
            }
        });
    }

    private void circular_hide_animation() {
        media_button.getCurrentView().setClickable(false);
        color_fab.setClickable(false);
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(media_fab, "scaleX", 0);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(media_fab, "scaleY", 0);
        ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(color_fab , "scaleX" , 0 );
        ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(color_fab , "scaleY" , 0 );
        AnimatorSet a = new AnimatorSet();
        if(media_button.getCurrentView() == media_fab) {
            a.play(scaleX).with(scaleY).with(scaleX2).with(scaleY2);
        }else {
            a.play(scaleX2).with(scaleY2);
        }
        a.setDuration(200);
        a.start();
    }

    private void color() {
        int color = Color.parseColor(colorHex);
        int light_color = Color.parseColor(lightColorHex);
        if (memory_data.isDeleted())
            color = Color.parseColor(colorsGenerator.deleted);

        //uri status bar
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(color);

        window.setNavigationBarColor(color);

        edit_fab.setBackgroundTintList(ColorStateList.valueOf(color));
        color_fab.setBackgroundTintList(ColorStateList.valueOf(color));
        if (media_button.getCurrentView() == media_fab)
            media_fab.setBackgroundTintList(ColorStateList.valueOf(color));
        //uri EditText's SelectHandle

        desc_edit.setBackgroundTintList(ColorStateList.valueOf(color));

        desc_edit.setTextColor(color);

        desc_edit.setHintTextColor(light_color);

        desc_edit.setHighlightColor(light_color);


        desc.setTextColor(color);
        dateGenerator = new DateGenerator(memory_data.getDate());

        root.setBackgroundColor(color);

        if (colorHex.equalsIgnoreCase(colorsGenerator.red)){
            datePickerDialog = new DatePickerDialog(getContext(), R.style.RedDatePickerDialogTheme,  new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    dateGenerator.setYear(year);
                    dateGenerator.setMonth(month);
                    dateGenerator.setDay(day);
                    memory_data.setDate(dateGenerator.date);
                    date.setText(memory_data.getDate());
                  //  Log.e(getClass().getName(), year + "/" + month + "/" + day);
                }
            }, dateGenerator.year, dateGenerator.month-1, dateGenerator.day);
        }
        else if(colorHex.equalsIgnoreCase(colorsGenerator.blue)){
            datePickerDialog = new DatePickerDialog(getContext(), R.style.BlueDatePickerDialogTheme,  new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    dateGenerator.setYear(year);
                    dateGenerator.setMonth(month);
                    dateGenerator.setDay(day);
                    memory_data.setDate(dateGenerator.getDate());
                    date.setText(memory_data.getDate());
                 //   Log.e(getClass().getName(), year + "/" + month + "/" + day);
                }
            }, dateGenerator.year, dateGenerator.month-1, dateGenerator.day);
        }
        else if (colorHex.equalsIgnoreCase(colorsGenerator.yellow)){
            datePickerDialog = new DatePickerDialog(getContext(), R.style.YellowDatePickerDialogTheme,  new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    dateGenerator.setYear(year);
                    dateGenerator.setMonth(month);
                    dateGenerator.setDay(day);
                    memory_data.setDate(dateGenerator.date);
                    date.setText(memory_data.getDate());
                  //  Log.e(getClass().getName(), year + "/" + month + "/" + day);
                }
            }, dateGenerator.year, dateGenerator.month-1, dateGenerator.day);
        }
        else if(colorHex.equalsIgnoreCase(colorsGenerator.green)){
            datePickerDialog = new DatePickerDialog(getContext(), R.style.GreenDatePickerDialogTheme,  new DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                    dateGenerator.setYear(year);
                    dateGenerator.setMonth(month);
                    dateGenerator.setDay(day);
                    memory_data.setDate(dateGenerator.date);
                    date.setText(memory_data.getDate());
                   // Log.e(getClass().getName(), year + "/" + month + "/" + day);
                }
            }, dateGenerator.year, dateGenerator.month-1, dateGenerator.day);
        }


        try{
            ((ImageShape)recyclerView.getLayoutManager().getChildAt(memory_data.getPosition())).setPathColor(color);
        }catch (Exception e){}
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        if (memory_data.isDeleted()) {
            inflater.inflate(R.menu.deleted_opened_memory_menu, menu);
        } else if(memory_data.getMainPhoto() ==null | memory_data.isDeleted())
            inflater.inflate(R.menu.white_opened_memory_menu, menu);
        else
            inflater.inflate(R.menu.opened_memory_menu, menu);

        if (memory_data.isFavored()) {
            if(memory_data.getMainPhoto() != null)
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favourite_on));
            else
                menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_white_favourite_on));
            menu.getItem(0).setChecked(true);
        }
        this.menu = menu;
        super.onCreateOptionsMenu(menu, inflater);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.toolbar_favourite) {
            if (item.isChecked()) {
                Toast.makeText(getContext(), "Removed From Favourites", Toast.LENGTH_LONG).show();
                item.setChecked(false);
                memory_data.setFavored(false);
                if(memory_data.getMainPhoto() != null)
                    item.setIcon(getResources().getDrawable(R.drawable.ic_state_favourite));
                else
                    item.setIcon(getResources().getDrawable(R.drawable.ic_white_favourite));
                if (adapter.mnen == 1) {
                    adapter.deleteMemory(memory_data);
                    ((NavigationDrawerFragment)getParentFragment()).recently_deleted = true;
                }
            } else {
                Toast.makeText(getContext(), "Added To Favourites", Toast.LENGTH_LONG).show();
                if(memory_data.getMainPhoto() != null)
                    item.setIcon(getResources().getDrawable(R.drawable.ic_favourite_on));
                else
                    item.setIcon(getResources().getDrawable(R.drawable.ic_white_favourite_on));

                item.setChecked(true);
                memory_data.setFavored(true);
            }
            return true;
        } else if (id == R.id.toolbar_delete ) {
            Toast.makeText(getContext(), "Deleted", Toast.LENGTH_SHORT).show();
            memory_data.setDeleted(true);
            memory_data.setFavored(false);
            adapter.deleteMemory(memory_data);

            if (title_edit.getVisibility() == View.VISIBLE) {
                    memory_data.setTitle(title_edit.getText().toString());
                memory_data.setDesc(desc_edit.getText().toString());
            }
            ((NavigationDrawerFragment) getParentFragment()).recently_deleted =true;
            enderAnimations((NavigationDrawerFragment) getParentFragment() , recyclerView , memory_data);
            return false;
        } else if (id == R.id.toolbar_restore & memory_data.isDeleted()) {
            Toast.makeText(getContext(), "Restored", Toast.LENGTH_SHORT).show();

            memory_data.setDeleted(false);
            mg = new MemoriesGenerator(getContext());
            mg.updateMemory(memory_data);

            ((NavigationDrawerFragment) getParentFragment()).recently_deleted =true;
            adapter.removeMemory(memory_data.getPosition());
           // Log.e(getClass().getName() , memory_data.getPosition()+"!");
            enderAnimations((NavigationDrawerFragment) getParentFragment() , recyclerView , memory_data);
        }

        return false;
    }

    public void setBack_icon_click_listener(View.OnClickListener back_icon_click_listener) {
        this.back_icon_click_listener = back_icon_click_listener;
      //  Log.e(getClass().getName() , "WFRTT");
    }

    public void setMemory_data(Memory memory_data) {
        this.memory_data = memory_data;
    }


    public void setRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (is_media_on) {
            if (resultCode == RESULT_OK) {
           //     Log.e(getClass().getName(), "OMG");
                mediaFragment.onActivityResult(requestCode, resultCode, data);
            }
        } else {
            if (resultCode == RESULT_OK) {
        //        Log.e(getClass().getName(), "OMG");
                final Uri background_picture = data.getData();
                toolbar_background.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                    @Override
                    public boolean onPreDraw() {
                        toolbar_background.getViewTreeObserver().removeOnPreDrawListener(this);
                        try {
                           ((MainActivity)getActivity()).loadingBitmap(new MediaFile(background_picture , "image" , getId(background_picture)) , toolbar_background , toolbar_background.getWidth() , toolbar_background.getHeight());
                        }catch (Exception e){
                       //     Log.e(getClass().getName() , "Error : "+e.getMessage());
                        }
                        if(memory_data.getMainPhoto() == null){
                            int wwhite= getResources().getColor(R.color.half_transparent);
                            back_icon.setTint(wwhite);
                            toolbar.setNavigationIcon(back_icon);
                            collapsingToolbarLayout.setExpandedTitleColor(wwhite);
                            collapsingToolbarLayout.setCollapsedTitleTextColor(wwhite);
                            date.setTextColor(wwhite);
                            title_edit.setTextColor(wwhite);
                            title_edit.setBackgroundTintList(ColorStateList.valueOf(wwhite));
                            if(!memory_data.isDeleted())
                                if(!memory_data.isFavored())
                                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_state_favourite));
                                else
                                    menu.getItem(0).setIcon(getResources().getDrawable(R.drawable.ic_favourite_on));
                             menu.getItem(1).setIcon(R.drawable.ic_trash);
                        }
                        memory_data.setMainPhoto(new MediaFile(background_picture , "image" , getId(background_picture)));
                        return false;
                    }
                });
            }
        }
    }

    public float dpToPx(float valueInDp) {
        try {
            DisplayMetrics metrics = getContext().getResources().getDisplayMetrics();
            return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, valueInDp, metrics);
        } catch (Exception e) {
            return 0;
        }
    }

    @Override
    public void onPause() {
      //  Log.e(getClass().getName() , "paused");
        if (title_edit.getVisibility() == View.VISIBLE) {
        //    Log.e(getClass().getName(), "WTF!");

            if (!title_edit.getText().toString().trim().isEmpty())
                memory_data.setTitle(title_edit.getText().toString());
            if (!desc_edit.getText().toString().trim().isEmpty() & desc_edit.getText().toString() != "null")
                memory_data.setDesc(desc_edit.getText().toString());
            else
                memory_data.setDesc("");
        }
        mg = new MemoriesGenerator(getContext());
        mg.updateMemory(memory_data);
        super.onPause();
    }



    public void open_media_fragment() {
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        window.setStatusBarColor(getResources().getColor(R.color.status_bar_color));
        window.setNavigationBarColor(getResources().getColor(R.color.status_bar_color));
        fragmentTransaction = fragmentManager.beginTransaction();
        is_media_on = true;

        mediaFragment = new MediaFragment();
        mediaFragment.setMemory_data(memory_data);
        mediaFragment.setO(this);
        fragmentTransaction.replace(R.id.media_frame, mediaFragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public void close_media_fragment() {
        if(media_button.getCurrentView() == media_fab) {
            if(memory_data.getMediaFiles() !=null) {
                for (int i = 0; i <= memory_data.getMediaFiles().length - 1; i++) {
                    if (memory_data.getMediaFiles()[i].getMimeType().contains("image")) {
                        media_button.showNext();
                        media_button.getCurrentView().setScaleY(1);
                        media_thumb.setImageBitmap(MediaStore.Images.Thumbnails.getThumbnail(getContext().getContentResolver(), memory_data.getMediaFiles()[i].getID(), MediaStore.Images.Thumbnails.MINI_KIND, new BitmapFactory.Options()));
                        break;
                    }
                }
            }
        }
        else{
            if(memory_data.getMediaFiles().length == 0){
                media_button.showPrevious();
                media_fab.setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorHex)));
            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (is_media_on) {
            if (grantResults[0] == 0) {
                mediaFragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        } else {
            if (grantResults[0] == 0) {
                Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                OpenedMemoryFragment.this.startActivityForResult(pickIntent, 55);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void starterAnimations() {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);

                if(memory_data.isDeleted())
                    root.setBackgroundColor(Color.parseColor(colorsGenerator.deleted));
                else
                    root.setBackgroundColor(Color.parseColor(memory_data.getColorHex()));
                desc.setTranslationY(-desc.getHeight());

                ObjectAnimator translationY1 = ObjectAnimator.ofFloat(bot_side,"translationY", bot_side.getHeight() , 0);
                ObjectAnimator translationY2 = ObjectAnimator.ofFloat(appBarLayout,"translationY", -appBarLayout.getHeight() , 0);
                ObjectAnimator translationY3 = ObjectAnimator.ofFloat(toolbar_background,"translationY", -toolbar_background.getHeight() , 0);

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(edit_fab , "scaleX" , 0 , 1);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(edit_fab , "scaleY" , 0 , 1);


                AnimatorSet aa = new AnimatorSet();
                aa.play(translationY1).with(translationY2).with(translationY3).with(scaleX).with(scaleY);
                aa.setDuration(500);

//                Log.e(getClass().getName()  , media_button.getCurrentView().getId() + "!" + media_fab.getId());

                if(media_button.getCurrentView() != media_fab){
                    ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(media_button.getCurrentView(), "scaleX", 0 , 1);
                    ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(media_button.getCurrentView(), "scaleY", 0 , 1);
                    aa.play(translationY1).with(translationY2).with(translationY3).with(scaleX).with(scaleY).with(scaleX1).with(scaleY1);
                }

                aa.start();
                translationY1.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ObjectAnimator translationY = ObjectAnimator.ofFloat(desc , "translationY" , 0);
                       translationY.start();
                    }
                });
                return true;
            }
        });
    }

    public void enderAnimations(final NavigationDrawerFragment navigationDrawerFragment, final RecyclerView recyclerView, final Memory memory_data) {
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_PAN);
        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);


                ObjectAnimator translationY1 = ObjectAnimator.ofFloat(desc, "translationY", -desc.getHeight());

                translationY1.start();
                translationY1.addListener(new AnimatorListenerAdapter() {

                                              @Override
                                              public void onAnimationEnd(Animator animator) {
                                                  ObjectAnimator translationY1 = ObjectAnimator.ofFloat(bot_side, "translationY", bot_side.getHeight());
                                                  ObjectAnimator translationY2 = ObjectAnimator.ofFloat(appBarLayout, "translationY", -appBarLayout.getHeight());
                                                  ObjectAnimator translationY3 = ObjectAnimator.ofFloat(toolbar_background, "translationY", -toolbar_background.getHeight());

                                                  ObjectAnimator scaleX = ObjectAnimator.ofFloat(edit_fab , "scaleX" , 0);
                                                  ObjectAnimator scaleY = ObjectAnimator.ofFloat(edit_fab , "scaleY" , 0);



                                                  AnimatorSet a = new AnimatorSet();
                                                  a.play(scaleX).with(scaleY).with(translationY1).with(translationY2).with(translationY3);
                                                  a.setDuration(500);
                                                  if(title_edit.getVisibility() == View.VISIBLE)
                                                  circular_hide_animation();


                                                  if(media_button.getCurrentView() != media_fab){
                                                 //     Log.e(getClass().getName()  , "!");
                                                      ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(media_button.getCurrentView(), "scaleX", 0);
                                                      ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(media_button.getCurrentView(), "scaleY", 0);
                                                      a.play(translationY1).with(translationY2).with(translationY3).with(scaleX).with(scaleY).with(scaleX1).with(scaleY1);
                                                  }
                                                  a.start();
                                                  a.addListener(new AnimatorListenerAdapter() {
                                                      @Override
                                                      public void onAnimationEnd(Animator animation) {
                                                          navigationDrawerFragment.close_memory(recyclerView, memory_data);
                                                      }
                                                  });
                                              }
                                          }
                );

                return true;
            }
        });
    }


    public String getPath(Uri uri)
    {
        if (uri == null)
            return null ;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }




    public long getId(Uri uri)
    {
        if (uri == null)
            return 0;
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getActivity().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return 0;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        cursor.moveToFirst();
        long s = cursor.getLong(column_index);
        cursor.close();
        return s;
    }


}
