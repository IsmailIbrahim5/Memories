package com.idea.memories.Views.Fragments;import android.animation.AnimatorSet;import android.animation.ObjectAnimator;import android.os.Bundle;import com.idea.memories.Adapters.MemoriesAdapter;import androidx.annotation.Nullable;import androidx.dynamicanimation.animation.DynamicAnimation;import androidx.dynamicanimation.animation.SpringAnimation;import androidx.dynamicanimation.animation.SpringForce;import androidx.fragment.app.Fragment;import androidx.recyclerview.widget.GridLayoutManager;import androidx.recyclerview.widget.RecyclerView;import android.os.Handler;import android.view.LayoutInflater;import android.view.View;import android.view.ViewGroup;import android.view.ViewTreeObserver;import android.widget.ImageView;import com.idea.memories.R;import com.idea.memories.Classes.MemoriesGenerator;import com.idea.memories.Classes.Memory;public class HomeFragment extends Fragment {    private ImageView navButton;    private View backdropChild , logo , emories , root;    private int memoryPosition = -1;    public MemoriesAdapter memoriesAdapter;    public RecyclerView recyclerView;    private View.OnClickListener onClickListener;    private RecyclerView.OnScrollListener onScrollListener;    @Override    public View onCreateView(LayoutInflater inflater, ViewGroup container,                             Bundle savedInstanceState) {        root = inflater.inflate(R.layout.fragment_home, container, false);        navButton = getActivity().findViewById(R.id.nav_button);        backdropChild = root.findViewById(R.id.backdrop_child);        recyclerView = root.findViewById(R.id.recycler_view);        final GridLayoutManager gridLayoutManager = new GridLayoutManager(getContext(), 2);        recyclerView.setLayoutManager(gridLayoutManager);        recyclerView.addOnScrollListener(onScrollListener);        final MemoriesGenerator memoriesGenerator = new MemoriesGenerator(getContext());        memoriesAdapter = new MemoriesAdapter(getContext() , getActivity() , memoriesGenerator.generateMain() , 0, onClickListener);        recyclerView.setAdapter(memoriesAdapter);        logo = root.findViewById(R.id.logo);        emories = root.findViewById(R.id.emories);        recyclerView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {            @Override            public void onGlobalLayout() {                recyclerView.getViewTreeObserver().removeOnGlobalLayoutListener(this);                if(memoryPosition == -2) {                    ((MainFragment) getParentFragment()).addMemoryButton.callOnClick();                    memoryPosition = -1;                }               else if (memoryPosition != -1) {                    final Memory m = memoriesGenerator.generateMain().get(memoryPosition);                    m.setPosition(memoryPosition);                    recyclerView.scrollToPosition(memoryPosition);                    Handler h = new Handler();                    h.postDelayed(new Runnable() {                        @Override                        public void run() {                            final int firstVisibleItem = gridLayoutManager.findFirstVisibleItemPosition();                            ((NavigationDrawerFragment) getParentFragment().getParentFragment()).open_memory(recyclerView.getChildAt(m.getPosition() - firstVisibleItem), m, recyclerView);                        }                    }   , 160);                    // why 160                    // bcs it's the age when im gonna be so fkng rich and famous + 0 at the end                    memoryPosition = -1;                }            }});        /*        circular_animation_view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {            @Override            public boolean onPreDraw() {                circular_animation_view.getViewTreeObserver().removeOnPreDrawListener(this);                //float sx = view.getWidth() / 2;                //float sy = view.getHeight() / 2;                //float startRadius = (float) Math.hypot(sx , sy) / 2;                 fx = root.getWidth() / 2;                 fy = root.getHeight() / 2;                 finalRadius = (float) Math.hypot(fx , fy);                circular_animation_view.setBackgroundColor(Color.parseColor(Colors.blue));                //            Window window = getActivity().getWindow();                //              window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);//                window.setStatusBarColor(Color.parseColor(Colors.blue));                return true;            }        });        circular_animation_view.setOnClickListener(new View.OnClickListener() {            @Override            public void onClick(View view) {                final Animator animator = ViewAnimationUtils.createCircularReveal(circular_animation_view, (int) circular_animation_view.getPivotX(), (int) circular_animation_view.getPivotY(), 0, finalRadius);                animator.setDuration(5000);                animator.start();            }        });        */        return root;    }    @Override    public void onActivityCreated(@Nullable Bundle savedInstanceState) {        super.onActivityCreated(savedInstanceState);        if(memoryPosition == -1)            starterAnimations();        else{            logo.setScaleX(1);            emories.setTranslationX(0);        }    }    public void setMemoryPosition(int memoryPosition) {        this.memoryPosition = memoryPosition;    }    public void starterAnimations() {        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {            @Override            public void onGlobalLayout() {                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);                ObjectAnimator translationY = ObjectAnimator.ofFloat(backdropChild, "translationY", backdropChild.getHeight(), 0f);                ObjectAnimator translationX = ObjectAnimator.ofFloat(navButton, "translationX", -(navButton.getWidth()), 0);                AnimatorSet a = new AnimatorSet();                a.play(translationY).with(translationX);                a.setDuration(250);                SpringAnimation animX , animTX , animY;                animX = new SpringAnimation(logo, DynamicAnimation.SCALE_X, 1f);                animX.setStartValue(0);                animX.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);                animY = new SpringAnimation(logo, DynamicAnimation.SCALE_Y, 1f);                animY.setStartValue(0);                animY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);                animTX = new SpringAnimation(emories, DynamicAnimation.TRANSLATION_X, 0f);                animTX.setStartValue(emories.getTranslationX());                animTX.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);                animTX.getSpring().setDampingRatio(SpringForce.DAMPING_RATIO_LOW_BOUNCY);                animX.start();                animY.start();                animTX.start();                a.start();            }        });    }    public void setOnClickListener(View.OnClickListener onClickListener) {        this.onClickListener = onClickListener;    }    public void setOnScrollListener(RecyclerView.OnScrollListener onScrollListener) {        this.onScrollListener = onScrollListener;    }}