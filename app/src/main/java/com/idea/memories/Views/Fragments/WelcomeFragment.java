package com.idea.memories.Views.Fragments;import android.os.Bundle;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import com.idea.memories.Classes.UserData;import com.idea.memories.R;import com.idea.memories.Views.Activities.MainActivity;import com.idea.memories.Views.CustomViews.TextShape;import androidx.dynamicanimation.animation.DynamicAnimation;import androidx.dynamicanimation.animation.SpringAnimation;import androidx.dynamicanimation.animation.SpringForce;import androidx.fragment.app.Fragment;public class WelcomeFragment extends Fragment {    @Override    public View onCreateView(LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {        final View root = inflater.inflate(R.layout.fragment_welcome, container, false);        final UserData userData = new UserData(getContext());        final TextShape logo = root.findViewById(R.id.logo);        final SpringAnimation animY = new SpringAnimation(logo, DynamicAnimation.SCALE_Y, 1.0f), animX = new SpringAnimation(logo, DynamicAnimation.SCALE_X, 1.0f);        animX.setStartValue(0);        animX.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);        animY.setStartValue(0);        animY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);        animY.addEndListener(new DynamicAnimation.OnAnimationEndListener() {            @Override            public void onAnimationEnd(DynamicAnimation animation, boolean canceled, float value, float velocity) {                if (userData.isExists()) {                    ((MainActivity)getActivity()).main();                } else {                    ((MainActivity)getActivity()).signUp();                }            }        });        animX.start();        animY.start();        return root;    }}