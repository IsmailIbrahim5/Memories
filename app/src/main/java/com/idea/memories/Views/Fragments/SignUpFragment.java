package com.idea.memories.Views.Fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.ColorStateList;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Classes.UserData;
import com.idea.memories.R;

import com.idea.memories.Views.Activities.MainActivity;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;

import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.Fragment;

public class SignUpFragment extends Fragment {

    private Uri profile_picture;
    ImageView avatar_photo;
    TextView i_dont_know_yet , done_button;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View root = inflater.inflate(R.layout.fragment_sign_up , container , false);
        final UserData userData = new UserData(getContext());

        CardView picture_layout = root.findViewById(R.id.picture_layout);

        final TextInputEditText name = root.findViewById(R.id.name);

        i_dont_know_yet = root.findViewById(R.id.i_dont_know_yet);
        done_button = root.findViewById(R.id.done_button);

        final TextInputLayout name_layout = root.findViewById(R.id.name_layout);

        View avatar_edit_button = root.findViewById(R.id.avatar_edit_button);

        avatar_photo = root.findViewById(R.id.avatar_photo_image_view);

        final SpringAnimation animX1 = new SpringAnimation(picture_layout, DynamicAnimation.SCALE_X, 1.0f);
        animX1.setStartValue(0);
        animX1.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        final SpringAnimation animY1 = new SpringAnimation(picture_layout, DynamicAnimation.SCALE_Y, 1.0f);
        animY1.setStartValue(0);
        animY1.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        final SpringAnimation animX2 = new SpringAnimation(name_layout, DynamicAnimation.SCALE_X, 1.0f);
        animX2.setStartValue(0);
        animX2.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        final SpringAnimation animY2 = new SpringAnimation(name_layout, DynamicAnimation.SCALE_Y, 1.0f);
        animY2.setStartValue(0);
        animY2.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        animX1.start();
        animY1.start();
        animX2.start();
        animY2.start();

        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().trim().isEmpty())
                    i_dont_know_yet.setText("M");
                else {
                    i_dont_know_yet.setText(String.valueOf(s.toString().trim().charAt(0)).toUpperCase());
                    name_layout.setBoxStrokeColor(getResources().getColor(R.color.white));
                    name_layout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.white)));
                    name_layout.setError(null);
                }

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!name.getText().toString().trim().isEmpty()) {
                    final String user_name = name.getText().toString();
                    animX1.setStartValue(1);
                    animX1.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    animX1.animateToFinalPosition(0);
                    animX2.setStartValue(1);
                    animX2.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    animX2.animateToFinalPosition(0);
                    animY1.setStartValue(1);
                    animY1.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    animY1.animateToFinalPosition(0);
                    animY2.setStartValue(1);
                    animY2.getSpring().setStiffness(SpringForce.STIFFNESS_HIGH);
                    animY2.animateToFinalPosition(0);
                    animX1.addEndListener(new DynamicAnimation.OnAnimationEndListener() {
                        @Override
                        public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {
                            try {
                                userData.writeData(user_name, profile_picture);
                            } catch (Exception e) {
                                userData.writeData(user_name, null);
                            }
                            ((MainActivity) getActivity()).main();
                        }

                    });
                } else {
                    name_layout.setBoxStrokeColor(getResources().getColor(R.color.red));
                    name_layout.setDefaultHintTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    name_layout.setErrorTextColor(ColorStateList.valueOf(getResources().getColor(R.color.red)));
                    name_layout.setError("Please Enter Your Name :D");
                    Animation shake = AnimationUtils.loadAnimation(getContext(), R.anim.right_shake);
                    name_layout.startAnimation(shake);
                }
            }
        });
        name.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                done_button.callOnClick();
                return true;
            }
        });

        avatar_edit_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(getContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
                    String[] permission = {Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    requestPermissions(permission, 5);
                }
                else{
                    Log.d(getClass().getName() , "All Permissions Are Granted");
                    Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickIntent, 5);
                }
            }
        });
        return root;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if (grantResults[0] == 0) {
            Intent pickIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickIntent, 5);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        try {
            profile_picture = data.getData();
            avatar_photo.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
                @Override
                public boolean onPreDraw() {
                    avatar_photo.getViewTreeObserver().removeOnPreDrawListener(this);
                    ((MainActivity) getActivity()).loadingBitmap(new MediaFile(profile_picture, "image", 222), avatar_photo, avatar_photo.getWidth(), avatar_photo.getHeight());
                    i_dont_know_yet.setVisibility(View.GONE);
                    return false;
                }
            });

        }catch (Exception e){
            Toast.makeText(getContext(), "There's error selecting your photo, please try again :D", Toast.LENGTH_SHORT).show();
        }
        }

}