package com.idea.memories.Views.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.content.ComponentName;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.graphics.PorterDuff;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.TextView;
import com.idea.memories.R;


import java.util.List;

public class SupportUsFragment extends Fragment {
    private final static String FACEBOOK_ID = "MemoriesOfficialApp";
    private final static String INSTAGRAM_ID = "MemoriesOfficialApp";
    private final static String TWITTER_ID = "MemoriesOApp";
    private final static String YOUTUBE_ID = "UCShFvYuO8H_jjkgbKgm62Wg";
    private final static String MAIL = "memoriesofficialapp@gmail.com";
    private final String PACKAGE_ID = "com.facebook.katana";// getActivity().getPackageName();
    View root, refer;
    FrameLayout googlePlay , googelePlay_inside , facebook, facebook_inside, twitter, twitter_inside, instagram_inside, instagram, youtube, youtube_inside, email, email_inside;
    ImageView facebook_logo, twitter_logo, instagram_logo, youtube_logo, email_logo , googleplay_logo;
    TextView facebook_text, instagram_text, twitter_text, youtube_text, email_text , googleplay_text;

    @Override
    public View onCreateView(LayoutInflater inflater, final ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_support_us, container, false);

        facebook = root.findViewById(R.id.facebook);
        facebook_inside = root.findViewById(R.id.facebook_inside);
        facebook_logo = root.findViewById(R.id.facebook_logo);
        twitter_inside = root.findViewById(R.id.twitter_inside);
        instagram = root.findViewById(R.id.instagram);
        googlePlay = root.findViewById(R.id.google_play);
        googelePlay_inside= root.findViewById(R.id.google_play_inside);
        googleplay_logo = root.findViewById(R.id.google_play_logo);
        googleplay_text = root.findViewById(R.id.google_play_text);
        twitter = root.findViewById(R.id.twitter);
        youtube = root.findViewById(R.id.youtube);
        email = root.findViewById(R.id.email);
        email_inside = root.findViewById(R.id.email_inside);
        youtube_inside = root.findViewById(R.id.youtube_inside);
        facebook_text = root.findViewById(R.id.facebook_text);
        instagram_text = root.findViewById(R.id.instagram_text);
        twitter_text = root.findViewById(R.id.twitter_text);
        twitter_logo = root.findViewById(R.id.twitter_logo);
        instagram_logo = root.findViewById(R.id.instagram_logo);
        instagram_inside = root.findViewById(R.id.instagram_inside);
        youtube_logo = root.findViewById(R.id.youtube_logo);
        youtube_text = root.findViewById(R.id.youtube_text);
        email_logo = root.findViewById(R.id.email_logo);
        email_text = root.findViewById(R.id.email_text);
        refer = root.findViewById(R.id.refer);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "impact.ttf");
        facebook_text.setTypeface(typeface);
        twitter_text.setTypeface(typeface);
        instagram_text.setTypeface(typeface);
        youtube_text.setTypeface(typeface);
        email_text.setTypeface(typeface);
        googleplay_text.setTypeface(typeface);

        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                int width = facebook.getWidth();
                facebook.getLayoutParams().height = width;
                twitter.getLayoutParams().height = width;
                instagram.getLayoutParams().height = width;
                youtube.getLayoutParams().height = width;
                googlePlay.getLayoutParams().height = width;
                email.getLayoutParams().height = width;
                refer.getLayoutParams().height = width;
                facebook.requestLayout();
                Log.e(getClass().getName(), width + "!!");
            }
        });
        final ObjectAnimator alpha = ObjectAnimator.ofFloat(null, "alpha", 1);

        final ObjectAnimator scaleX = ObjectAnimator.ofFloat(null, "scaleX", 0.75f);
        final ObjectAnimator scaleY = ObjectAnimator.ofFloat(null, "scaleY", 0.75f);
        final ObjectAnimator translationY = ObjectAnimator.ofFloat(null, "translationY", -100f);
        final AnimatorSet a = new AnimatorSet();

        int tint = twitter_logo.getImageTintList().getDefaultColor();
        final ValueAnimator color = ValueAnimator.ofObject(new ArgbEvaluator(), tint, 0);
        a.play(alpha).with(scaleX).with(scaleY).with(translationY).with(color);
        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(facebook_inside);
                translationY.setTarget(facebook_logo);
                scaleY.setTarget(facebook_logo);
                scaleX.setTarget(facebook_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {

                        facebook_inside.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://www.facebook.com/" + FACEBOOK_ID);
                                try {
                                    ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo("com.facebook.katana", 0);
                                    if (applicationInfo.enabled) {
                                        uri = Uri.parse("fb://facewebmodal/f?href=" + uri);
                                    }
                                } catch (Exception e) {
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        facebook_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();
            }
        });

        instagram.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(instagram_inside);
                translationY.setTarget(instagram_logo);
                scaleY.setTarget(instagram_logo);
                scaleX.setTarget(instagram_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        instagram.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Uri uri = Uri.parse("https://instagram.com/" + INSTAGRAM_ID);
                                try {
                                    ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo("com.instagram.android", 0);
                                    if (applicationInfo.enabled) {
                                        uri = Uri.parse("http://instagram.com/_u/" + INSTAGRAM_ID);
                                    }
                                } catch (Exception e) {
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });
                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        instagram_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(twitter_inside);
                translationY.setTarget(twitter_logo);
                scaleY.setTarget(twitter_logo);
                scaleX.setTarget(twitter_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        twitter.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Uri uri = Uri.parse("https://twitter.com/" + TWITTER_ID);
                                try {
                                    ApplicationInfo applicationInfo = getActivity().getPackageManager().getApplicationInfo("com.twitter.android", 0);
                                    if (applicationInfo.enabled) {
                                        uri = Uri.parse("twitter://user?screenname=" + TWITTER_ID);
                                    }
                                } catch (Exception e) {
                                }
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }
                        });

                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        twitter_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();
            }
        });


        youtube.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(youtube_inside);
                translationY.setTarget(youtube_logo);
                scaleY.setTarget(youtube_logo);
                scaleX.setTarget(youtube_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        youtube.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {

                                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/" + YOUTUBE_ID));
                                intent.setComponent(new ComponentName("com.google.android.youtube", "com.google.android.youtube.PlayerActivity"));

                                PackageManager manager = getContext().getPackageManager();
                                List<ResolveInfo> infos = manager.queryIntentActivities(intent, 0);

                                if (infos.size() > 0) {
                                    startActivity(intent);
                                } else {
                                    intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.youtube.com/channel/" + YOUTUBE_ID));
                                    startActivity(intent);
                                }
                            }
                        });
                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        youtube_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();

            }
        });


        googlePlay.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(googelePlay_inside);
                translationY.setTarget(googleplay_logo);
                scaleY.setTarget(googleplay_logo);
                scaleX.setTarget(googleplay_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        googelePlay_inside.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/details?id="+PACKAGE_ID)));
                            }
                        });
                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        googleplay_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();
            }
        });

        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                alpha.setTarget(email_inside);
                translationY.setTarget(email_logo);
                scaleY.setTarget(email_logo);
                scaleX.setTarget(email_logo);
                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        email.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent(Intent.ACTION_SENDTO);
                                intent.setData(Uri.parse("mailto:"));
                                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{MAIL});
                                startActivity(Intent.createChooser(intent, "Send us an email!"));
                            }
                        });
                    }
                });
                color.removeAllUpdateListeners();
                color.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                    @Override
                    public void onAnimationUpdate(ValueAnimator animation) {
                        email_logo.setColorFilter((int) animation.getAnimatedValue());
                    }
                });
                a.start();
            }
        });

        refer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, "HI !");
                startActivity(Intent.createChooser(intent, "LOL"));
            }
        });
        return root;
    }
}