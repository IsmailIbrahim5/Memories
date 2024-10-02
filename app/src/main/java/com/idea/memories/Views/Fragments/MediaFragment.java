package com.idea.memories.Views.Fragments;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.idea.memories.R;
import com.idea.memories.Views.Activities.MainActivity;
import com.idea.memories.Views.CustomViews.CircularRevealFrameLayout;
import com.idea.memories.Classes.ColorsGenerator;
import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Classes.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Objects;

import com.idea.memories.Adapters.MediaAdapter;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import static androidx.appcompat.app.AppCompatActivity.RESULT_OK;

public class MediaFragment extends Fragment {

    Memory memory_data;
    MediaAdapter gallery_filesAdapter;
    boolean is_add_on;

    private final static int IMAGE_GALLERY_REQUEST = 5;
    private final static int IMAGE_CAMERA_REQUEST = 55;
    private final static int VIDEO_GALLERY_REQUEST = 555;
    private final static int VIDEO_CAMERA_REQUEST = 5555;
    private final static int VOICE_REQUEST = 55555;
    private final static int PHOTO_REQUEST_PERMISSION = 1;
    private final static int VIDEO_REQUEST_PERMISSION = 11;
    private final static int VOICE_REQUEST_PERMISSION = 111;


    FloatingActionButton photo_fab, video_fab, voice_fab;

    static String cameraFilePath ;
    View root;
    public  RecyclerView media_files_list;
    View.OnClickListener onBackClickListener;
    FragmentTransaction fragmentTransaction;
    public OpenedMediaFragment openedMediaFragment;
    public FragmentManager fragmentManager;
    public  boolean is_media_opened;
    public Toolbar toolbar;
    public  GridLayoutManager gridLayoutManager;
    boolean clicked;
    CircularRevealFrameLayout circularRevealFrameLayout;
    ColorsGenerator colorsGenerator;
    OpenedMemoryFragment o;
    FloatingActionButton add_fab;
    LinearLayout top;
    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_media, container, false);
        toolbar = root.findViewById(R.id.media_toolbar);
        toolbar.setNavigationIcon(R.drawable.ic_sky_back);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ender_animation();
            }
        });

        colorsGenerator = new ColorsGenerator(getContext());
        toolbar.setTitle(memory_data.getTitle());

        media_files_list = root.findViewById(R.id.media_files_list);
        top = root.findViewById(R.id.top);
        gridLayoutManager =
                new GridLayoutManager(getContext(), 2);
        media_files_list.setLayoutManager(gridLayoutManager);
// Attach the layout manager to the recycler view

        final ArrayList<MediaFile> arrayList = new ArrayList<>();
        if (memory_data == null | memory_data.getMediaFiles() == null) {
            //do nothing
        } else{
            for (MediaFile u : memory_data.getMediaFiles())
                arrayList.add(u);
        }
        gallery_filesAdapter = new MediaAdapter(arrayList, (MainActivity) getActivity() , memory_data ,media_files_list , new View.OnClickListener() {
            @Override

            public void onClick(final View v) {
                if ((int)v.getTag() != gallery_filesAdapter.deleteOn & !is_media_opened) {
                    is_media_opened = true;
                    fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentTransaction = fragmentManager.beginTransaction();
                    openedMediaFragment = new OpenedMediaFragment();
                    openedMediaFragment.setV(v);
                    openedMediaFragment.setMemory_data(memory_data);

                    openedMediaFragment.setRecyclerView(media_files_list);
                    openedMediaFragment.setMediaAdapter(gallery_filesAdapter);
                    openedMediaFragment.setFragmentManager(fragmentManager);
                    openedMediaFragment.setMediaFragment(MediaFragment.this);

                    openedMediaFragment.setOnBackClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            openedMediaFragment.ender_animations();
                            is_media_opened = false;
                        }
                    });
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.replace(R.id.opened_media_frame_layout, openedMediaFragment);

                    fragmentTransaction.commit();

                    if(gallery_filesAdapter.deleteOn != -1){
                        gridLayoutManager.getChildAt(gallery_filesAdapter.deleteOn).findViewById(R.id.red_layer).animate().alpha(0).setDuration(200).start();
                        gridLayoutManager.getChildAt(gallery_filesAdapter.deleteOn).findViewById(R.id.delete_icon).animate().alpha(0).setDuration(200).start();
                        gallery_filesAdapter.deleteOn = -1;
                    }
                }
                else if (!is_media_opened){
                    ((ImageView)v.findViewById(R.id.delete_icon)).setImageResource(R.drawable.ic_white_trash_on);
                    v.findViewById(R.id.delete_icon).animate().alpha(0).setDuration(200).start();
                    v.findViewById(R.id.red_layer).animate().alpha(0).setDuration(200).start();
                    gallery_filesAdapter.removeMediaFile((int)v.getTag());
                    gallery_filesAdapter.deleteOn = -1;
                }
            }
        } );

        media_files_list.setAdapter(gallery_filesAdapter);

        photo_fab = root.findViewById(R.id.photo_fab);
        video_fab = root.findViewById(R.id.video_fab);
        voice_fab = root.findViewById(R.id.voice_fab);

        media_files_list.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged( RecyclerView recyclerView, int newState) {
                if(newState == RecyclerView.SCROLL_STATE_DRAGGING) {
                    if(gallery_filesAdapter.deleteOn != -1){
                        int firstVisibleItemPosition = gridLayoutManager.findFirstVisibleItemPosition();
                        gridLayoutManager.getChildAt(gallery_filesAdapter.deleteOn - firstVisibleItemPosition).findViewById(R.id.red_layer).animate().alpha(0).setDuration(200).start();
                        gridLayoutManager.getChildAt(gallery_filesAdapter.deleteOn - firstVisibleItemPosition).findViewById(R.id.delete_icon).animate().alpha(0).setDuration(200).start();
                        gallery_filesAdapter.deleteOn = -1;
                    }
                }
            }
        });
        add_fab = root.findViewById(R.id.add_fab);
        add_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {

                v.animate().rotationBy(135);
                if (is_add_on) {
                    is_add_on = false;

                    photo_fab.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            photo_fab.getViewTreeObserver().removeOnPreDrawListener(this);

                            ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(photo_fab , "scaleX" , 0);
                            ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(video_fab , "scaleX" , 0);
                            ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(voice_fab , "scaleX" , 0);
                            ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(photo_fab , "scaleY" , 0);
                            ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(video_fab , "scaleY" , 0);
                            ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(voice_fab , "scaleY" , 0);

                            AnimatorSet a = new AnimatorSet();
                            a.play(scaleX1).with(scaleX2).with(scaleX3).with(scaleY1).with(scaleY2).with(scaleY3);
                            a.setDuration(250);
                            a.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    photo_fab.setClickable(false);
                                    video_fab.setClickable(false);
                                    voice_fab.setClickable(false);
                                }
                            });
                            a.start();
                            return true;
                        }
                    });

                } else {
                    is_add_on = true;

                    photo_fab.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                        @Override
                        public boolean onPreDraw() {
                            photo_fab.getViewTreeObserver().removeOnPreDrawListener(this);

                            ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(photo_fab , "scaleX" , 0,1);
                            ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(video_fab , "scaleX" , 0,1);
                            ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(voice_fab , "scaleX" , 0,1);
                            ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(photo_fab , "scaleY" , 0,1);
                            ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(video_fab , "scaleY" , 0,1);
                            ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(voice_fab , "scaleY" , 0,1);


                            AnimatorSet a = new AnimatorSet();
                            a.play(scaleX1).with(scaleX2).with(scaleX3).with(scaleY1).with(scaleY2).with(scaleY3);
                            a.setDuration(250);
                            a.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    photo_fab.setClickable(true);
                                    video_fab.setClickable(true);
                                    voice_fab.setClickable(true);
                                }
                            });
                            a.start();
                            return true;
                        }
                    });

                }
            }
        });

        photo_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.e(this.getClass().getName() , "Helli");
                try {
                    if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                        showAlertDialog("image");
                    }else{
                        if (ContextCompat.checkSelfPermission(Objects.requireNonNull(getContext()), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                            Log.e(this.getClass().getName(), "Hello");
                            String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                            Log.e(this.getClass().getName(), String.valueOf(permission.length));
                            requestPermissions(permission, PHOTO_REQUEST_PERMISSION);
                        } else {
                            if (!clicked) {
                                clicked = true;
                                showAlertDialog("image");
                            }
                        }
                    }
                }catch (Exception e){
                    Log.e(this.getClass().getName() , "Got Error");
                    Log.e(this.getClass().getName() , e.getMessage());
                }
            }
        });


        video_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                    showAlertDialog("video");
                }else{
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        requestPermissions(permission, PHOTO_REQUEST_PERMISSION);
                    } else {
                        if (!clicked) {
                            clicked = true;
                            showAlertDialog("video");
                        }
                    }
                }
            }
        });

        voice_fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                    Intent audioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    audioIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                    MediaFragment.this.startActivityForResult(audioIntent, VOICE_REQUEST);
                }else{
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        MediaFragment.this.requestPermissions(permission, VOICE_REQUEST_PERMISSION);
                    } else {
                        if (!clicked) {
                            clicked = true;
                            Log.d(getClass().getName(), "All Permissions Are Granted");
                            Intent audioIntent = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                            audioIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);
                            MediaFragment.this.startActivityForResult(audioIntent, VOICE_REQUEST);
                            clicked = false;
                        }
                    }
                }
            }
        });
        photo_fab.setClickable(false);
        video_fab.setClickable(false);
        voice_fab.setClickable(false);
        return root;
    }


    public void setO(OpenedMemoryFragment o) {
        this.o = o;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        starter_animation();
     }

    private void starter_animation() {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(add_fab , "scaleX" , 0 , 1);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(add_fab , "scaleY" , 0 , 1);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(top , "translationY" , -top.getHeight() , 0);
                AnimatorSet a =new AnimatorSet();
                a.setDuration(250);
                a.play(scaleX).with(scaleY).with(translationY);

                a.start();
/*
                final int[] coordinates = new int[2];
                media_button.getCurrentView().getLocationInWindow(coordinates);
                coordinates[0] += media_button.getCurrentView().getWidth() / 2;
                coordinates[1] += media_button.getCurrentView().getHeight() / 4;


                final int startRadius = (int) Math.hypot(media_button.getCurrentView().getWidth()/2, media_button.getCurrentView().getHeight()/2);



                final float finalRadius = (float) Math.hypot(circularRevealFrameLayout.getWidth()/2, circularRevealFrameLayout.getHeight()/2) + 50;

                media_button.getCurrentView().setVisibility(View.GONE);
                int color = Color.WHITE;
                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                    color = getResources().getColor(R.color.background);
                        circularRevealFrameLayout.Reveal(startRadius , finalRadius , coordinates [0] , coordinates[1] - media_button.getCurrentView().getPivotY()/2 , 500 , color).addListener(new AnimatorListenerAdapter() {
                            @Override
                            public void onAnimationEnd(Animator animation) {
                                if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                                    circularRevealFrameLayout.setBackgroundColor(Color.TRANSPARENT);
                                    else
                                circularRevealFrameLayout.setBackgroundColor(Color.WHITE);
                                media_files_list.animate().translationY(0).start();
                            }

                        });
                        */
            }
        });

    }

    public void ender_animation (){
        Window window = getActivity().getWindow();
        window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
        if (memory_data.isDeleted())
            window.setStatusBarColor(Color.parseColor(colorsGenerator.deleted));
        else
            window.setStatusBarColor(Color.parseColor(memory_data.getColorHex()));

        window.setNavigationBarColor(Color.parseColor(memory_data.getColorHex()));

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                ObjectAnimator scaleX = ObjectAnimator.ofFloat(add_fab , "scaleX" , 0);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(add_fab , "scaleY" , 0);
                ObjectAnimator translationY = ObjectAnimator.ofFloat(top , "translationY" , -top.getHeight());
                ObjectAnimator scaleX1 = ObjectAnimator.ofFloat(photo_fab , "scaleX" , 0);
                ObjectAnimator scaleX2 = ObjectAnimator.ofFloat(video_fab , "scaleX" , 0);
                ObjectAnimator scaleX3 = ObjectAnimator.ofFloat(voice_fab , "scaleX" , 0);
                ObjectAnimator scaleY1 = ObjectAnimator.ofFloat(photo_fab , "scaleY" , 0);
                ObjectAnimator scaleY2 = ObjectAnimator.ofFloat(video_fab , "scaleY" , 0);
                ObjectAnimator scaleY3 = ObjectAnimator.ofFloat(voice_fab , "scaleY" , 0);

                AnimatorSet a = new AnimatorSet();
                a.setDuration(250);
                a.play(scaleX).with(scaleY).with(translationY).with(scaleX1).with(scaleX2).with(scaleX3).with(scaleY).with(scaleY1).with(scaleY2).with(scaleY3);

a.start();
a.addListener(new AnimatorListenerAdapter() {
    @Override
    public void onAnimationEnd(Animator animation) {
        o.is_media_on = false;
        o.close_media_fragment();
        o.fragmentManager.popBackStackImmediate();
    }
});
/*
                final int[] coordinates = new int[2];
                media_button.getCurrentView().getLocationInWindow(coordinates);
                coordinates[0] += media_button.getCurrentView().getWidth() / 2;
                coordinates[1] += media_button.getCurrentView().getHeight() / 4;



                final int startRadius = (int) Math.hypot(media_button.getCurrentView().getWidth()/2, media_button.getCurrentView().getHeight()/2);


                final float finalRadius = (float) Math.hypot(circularRevealFrameLayout.getWidth()/2, circularRevealFrameLayout.getHeight()/2) + 50;

                if(gallery_filesAdapter.getItemCount() != 0) {
                    media_files_list.animate().translationY(-root.getHeight()).setListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            circularRevealFrameLayout.setBackgroundColor(Color.TRANSPARENT);
                            a.start();
                            int color= Color.WHITE;
                            if(AppCompatDelegate.getDefaultNightMode() == AppCompatDelegate.MODE_NIGHT_YES)
                                color = getResources().getColor(R.color.background);
                            circularRevealFrameLayout.Hide(finalRadius, startRadius, coordinates[0], coordinates[1], 500, color).addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    media_button.getCurrentView().setVisibility(View.VISIBLE);
                                    o.is_media_on = false;
                                    o.close_media_fragment();
                                    o.fragmentManager.popBackStackImmediate();
                                }
                            });

                        }
                    }).start();
                }
                else{
                    circularRevealFrameLayout.setBackgroundColor(Color.TRANSPARENT);
                    a.start();
                    circularRevealFrameLayout.Hide(finalRadius, startRadius, coordinates[0], coordinates[1], 500, Color.WHITE).addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            media_button.getCurrentView().setVisibility(View.VISIBLE);
                            o.is_media_on = false;
                            o.close_media_fragment();
                            o.fragmentManager.popBackStackImmediate();
                        }
                    });

                }
*/
            }
        });

    }

    Uri PICK_INTENT;
    String TAKE_INTENT;
    int PICK_REQUEST_CODE = 0 , TAKE_REQUEST_CODE = 0;
    private void showAlertDialog (final String type){
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        ViewGroup viewGroup = root.findViewById(android.R.id.content);
        View dialogView = LayoutInflater.from(getContext()).inflate(R.layout.custom_alert_dialog, viewGroup, false);
        builder.setView(dialogView);
        builder.setCancelable(true);
        final AlertDialog alertDialog = builder.create();
        WindowManager.LayoutParams wlmp = alertDialog.getWindow().getAttributes();
        wlmp.gravity = Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL;
        alertDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialogView.findViewById(R.id.cancel_button).setBackground(new ColorDrawable(Color.TRANSPARENT));
        alertDialog.show();

        dialogView.findViewById(R.id.cancel_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        alertDialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                clicked = false;
            }
        });
        if(type.equals("image")){
            ((FloatingActionButton)dialogView.findViewById(R.id.take_fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_camera));
            ((FloatingActionButton)dialogView.findViewById(R.id.pick_fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_photo));
            ((TextView)dialogView.findViewById(R.id.pick_text)).setText("Pick From Gallery");
            ((TextView)dialogView.findViewById(R.id.take_text)).setText("Take A Photo");
            ((TextView)dialogView.findViewById(R.id.alert_dialog_title)).setText("Pick A Photo");
            PICK_INTENT = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
            TAKE_INTENT = MediaStore.ACTION_IMAGE_CAPTURE;
            PICK_REQUEST_CODE = IMAGE_GALLERY_REQUEST;
            TAKE_REQUEST_CODE = IMAGE_CAMERA_REQUEST;
        }
        else if(type.equals("video")){
            ((FloatingActionButton)dialogView.findViewById(R.id.take_fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_video_camera));
            ((FloatingActionButton)dialogView.findViewById(R.id.pick_fab)).setImageDrawable(getResources().getDrawable(R.drawable.ic_video));
            ((TextView)dialogView.findViewById(R.id.pick_text)).setText("Pick From Gallery");
            ((TextView)dialogView.findViewById(R.id.take_text)).setText("Take A Video");
            ((TextView)dialogView.findViewById(R.id.alert_dialog_title)).setText("Pick A Video");
            PICK_INTENT = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
            TAKE_INTENT = MediaStore.ACTION_VIDEO_CAPTURE;
            PICK_REQUEST_CODE = VIDEO_GALLERY_REQUEST;
            TAKE_REQUEST_CODE = VIDEO_CAMERA_REQUEST;
        }
        dialogView.findViewById(R.id.take_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT>= Build.VERSION_CODES.TIRAMISU) {
                    Intent cameraIntent = new Intent(TAKE_INTENT);
                    if (type.equals("video"))
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.idea.memories.provider", Objects.requireNonNull(createVideoFile())));
                    else
                        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(Objects.requireNonNull(getContext()), "com.idea.memories.provider", Objects.requireNonNull(createImageFile())));
                    MediaFragment.this.startActivityForResult(cameraIntent, TAKE_REQUEST_CODE);

                }else {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        MediaFragment.this.requestPermissions(permission, TAKE_REQUEST_CODE);
                    } else {
                        Log.d(getClass().getName(), "All Permissions Are Granted");
                        Intent cameraIntent = new Intent(TAKE_INTENT);
                        if (type.equals("video"))
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), "com.idea.memories.provider", createVideoFile()));
                        else
                            cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getContext(), "com.idea.memories.provider", createImageFile()));
                        MediaFragment.this.startActivityForResult(cameraIntent, TAKE_REQUEST_CODE);
                    }
                }
                alertDialog.dismiss();
            }
        });

        dialogView.findViewById(R.id.pick_fab).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                    Intent galleryIntent = new Intent(Intent.ACTION_PICK, PICK_INTENT);
                    galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                    MediaFragment.this.startActivityForResult(galleryIntent, PICK_REQUEST_CODE);
                } else {
                    if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                        String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                        MediaFragment.this.requestPermissions(permission, PICK_REQUEST_CODE);
                    } else {
                        Log.d(getClass().getName(), "All Permissions Are Granted");

                        Intent galleryIntent = new Intent(Intent.ACTION_PICK, PICK_INTENT);
                        galleryIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true);

                        MediaFragment.this.startActivityForResult(galleryIntent, PICK_REQUEST_CODE);
                    }
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void setMemory_data(Memory memory_data) {
        this.memory_data = memory_data;
    }

    public void setOnBackClickListener(View.OnClickListener onBackClickListener) {
        this.onBackClickListener = onBackClickListener;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            switch (requestCode) {
                case IMAGE_GALLERY_REQUEST:
                    try {
                        ClipData clip = data.getClipData();
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            ClipData.Item item = clip.getItemAt(i);
                            Uri uri = item.getUri();
                            Log.e(getClass().getName(), uri.getPath());
                            gallery_filesAdapter.addMediaFile(uri, "image" , getId(uri));
                        }
                    } catch (Exception e) {
                        Uri uri = data.getData();
                        Log.e(getClass().getName(), uri.getPath());
                        gallery_filesAdapter.addMediaFile(uri, "image" , getId(uri));
                    }
                    break;

                case IMAGE_CAMERA_REQUEST:
                    gallery_filesAdapter.addMediaFile(Uri.parse(cameraFilePath), "image" ,  getId(Uri.parse(cameraFilePath)));
                    break;

                case VIDEO_GALLERY_REQUEST:
                    try {
                        ClipData clip = data.getClipData();
                        for (int i = 0; i < clip.getItemCount(); i++) {
                            ClipData.Item item = clip.getItemAt(i);
                            Uri uri = item.getUri();
                            Log.e(getClass().getName(), uri.getPath());
                            gallery_filesAdapter.addMediaFile(uri, "video"  , getId(uri));
                        }
                    } catch (Exception e) {
                        Uri uri = data.getData();
                        Log.e(getClass().getName(), uri.getPath());
                        gallery_filesAdapter.addMediaFile(uri, "video" , getId(uri));
                    } break;

                case VIDEO_CAMERA_REQUEST:
                    gallery_filesAdapter.addMediaFile(Uri.parse(cameraFilePath), "video" , getId(Uri.parse(cameraFilePath)));
                    break;


                case VOICE_REQUEST:
                    Uri uri = data.getData();
                    Log.e(getClass().getName(), uri.getPath());
                    gallery_filesAdapter.addMediaFile(uri, "audio" , getId(uri));
                    break;

            }
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if(grantResults[0] == PackageManager.PERMISSION_GRANTED){
            if(requestCode == PHOTO_REQUEST_PERMISSION)
                photo_fab.callOnClick();
            else if(requestCode == VIDEO_REQUEST_PERMISSION)
                video_fab.callOnClick();
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }


    private File createImageFile(){
        try {
            @SuppressLint("SimpleDateFormat") String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "IMG_" + timeStamp;
            Log.e(getTag() , imageFileName);

            File storageDir = getContext().getExternalFilesDir("Camera");
            Log.e(getTag() , storageDir.getAbsolutePath());
            if(!storageDir.exists()){
                Log.e(getTag() , "Doesnt wexits");
                storageDir.mkdirs();
            }
            Log.e(getTag() , "Done");
            File image = File.createTempFile(
                    imageFileName ,
                    ".jpg",
                    storageDir
            );
            Log.e(getTag() , "Error");

            cameraFilePath = "file://" + image.getAbsolutePath();
            return image;
        }catch (Exception e){
            Log.e(getTag() , e.getMessage());
            return  null;
        }
    }


    private File createVideoFile(){
        try {
            String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            String imageFileName = "VID_" + timeStamp;

            File storageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DCIM) , "Camera");

            File image = File.createTempFile(
                    imageFileName ,
                    ".mp4",
                    storageDir
            );
            cameraFilePath = "file://" + image.getAbsolutePath();
            return image;
        }catch (Exception e){
            return null;
        }
    }


    public long getId(Uri uri)
    {
        if (uri == null)
            return 0 ;
        String[] projection = {MediaStore.Images.Media._ID};
        Cursor cursor = getContext().getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return 0;
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID);
        cursor.moveToFirst();
        long s = cursor.getLong(column_index);
        cursor.close();
        Log.e(getClass().getName() , s+"!");
        return s;
    }
}

