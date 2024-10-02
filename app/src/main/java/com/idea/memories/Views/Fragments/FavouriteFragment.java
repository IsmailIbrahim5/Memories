package com.idea.memories.Views.Fragments;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import com.idea.memories.R;

import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

import com.idea.memories.Adapters.MemoriesAdapter;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;


public class FavouriteFragment extends Fragment {

    private MemoriesAdapter memoriesAdapter;
    public RecyclerView recyclerView;

    private View.OnClickListener onClickListener;

    private FloatingActionButton searchFab;

    private View root , searchContainer , logo_container;
    private EditText searchBar;
    private boolean searchOn;
    private AnimatedVectorDrawableCompat searchAnim, reverseSearchAnim;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_favourite, container, false);

        searchFab = root.findViewById(R.id.search_fab);
        searchBar = root.findViewById(R.id.search_bar);
        searchContainer = root.findViewById(R.id.search_container);
        logo_container = root.findViewById(R.id.logo_container);

        recyclerView = root.findViewById(R.id.recycler_view);
        recyclerView.setHasFixedSize(false);

        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));


        final MemoriesGenerator memoriesGenerator = new MemoriesGenerator(getContext());

        final ArrayList<Memory> memories = memoriesGenerator.generateFavourites();
        memoriesAdapter = new MemoriesAdapter(getContext(),  getActivity(), memories, 1, onClickListener);
        recyclerView.setAdapter(memoriesAdapter);

        searchAnim = AnimatedVectorDrawableCompat.create(getContext() , R.drawable.anim_search_to_done);
        reverseSearchAnim = AnimatedVectorDrawableCompat.create(getContext() , R.drawable.anim_done_to_search);

        root.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                root.getViewTreeObserver().removeOnPreDrawListener(this);

                searchContainer.setPivotX(searchBar.getWidth());

                logo_container.setPivotX(0);
                return true;
            }
        });

        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                memoriesAdapter.memories.clear();
                if(!s.toString().trim().isEmpty()) {
                    memoriesAdapter.memories = memoriesGenerator.search(s.toString(), 1);
                    memoriesAdapter.setSearchKey(s.toString());
                }
                else
                    memoriesAdapter.memories = memoriesGenerator.generateFavourites();
                memoriesAdapter.notifyDataSetChanged();
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        final ObjectAnimator scale = ObjectAnimator.ofFloat(searchContainer, "scaleX",1);
        scale.setDuration(250);
        scale.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float value = (float) animation.getAnimatedValue();
                logo_container.setScaleX(1-value);
            }
        });

        searchFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(searchOn){
                    memoriesAdapter.memories.clear();
                    memoriesAdapter.setSearchKey(null);
                    memoriesAdapter.memories.addAll(memoriesGenerator.generateFavourites());
                    memoriesAdapter.notifyDataSetChanged();

                    searchOn = false;
                    searchFab.setImageDrawable(reverseSearchAnim);
                    reverseSearchAnim.start();
                    scale.reverse();
                    scale.addListener(new AnimatorListenerAdapter() {
                        @Override
                        public void onAnimationEnd(Animator animation) {
                            searchBar.setVisibility(View.GONE);
                            searchFab.setImageResource(R.drawable.ic_search);
                        }
                    });
                }
                else {
                    if(!searchBar.getText().toString().isEmpty()){
                        memoriesAdapter.memories.clear();
                        memoriesAdapter.setSearchKey(searchBar.getText().toString());
                        memoriesAdapter.memories.addAll(memoriesGenerator.search(memoriesAdapter.searchKey , 1));
                        memoriesAdapter.notifyDataSetChanged();
                    }
                    searchBar.setVisibility(View.VISIBLE);
                    searchOn = true;
                    searchFab.setImageDrawable(searchAnim);
                    searchAnim.start();
                    scale.removeAllListeners();
                    scale.start();
                }
            }
        });

        return root;

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
    }
}
