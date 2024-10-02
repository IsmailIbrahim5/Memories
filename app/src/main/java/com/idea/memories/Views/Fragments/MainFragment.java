package com.idea.memories.Views.Fragments;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;

import com.idea.memories.Classes.ColorsGenerator;
import com.idea.memories.Classes.DateGenerator;
import com.idea.memories.R;

import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.analytics.FirebaseAnalytics;


import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.RecyclerView;
import androidx.vectordrawable.graphics.drawable.AnimatedVectorDrawableCompat;
import androidx.viewpager.widget.ViewPager;


public class MainFragment extends Fragment {

    private ImageView searchButton, homeButton;
    public HomeFragment homeTab;
    public SearchFragment searchTab;
    private ViewPager pager;
    private boolean scrolled;
    public FloatingActionButton addMemoryButton;
    private View root, bottomNavigation, bottomNavigationContainer;
    private int whichPage;
    private AnimatedVectorDrawableCompat homeAnim, reverseHomeAnim, searchAnim, reverseSearchAnim;
    private float Y;
    private MemoriesGenerator memoriesGenerator;
    private ColorsGenerator colorsGenerator;
    private int memoryPosition = -1;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        root = inflater.inflate(R.layout.fragment_main, container, false);

        memoriesGenerator = new MemoriesGenerator(getContext());
        colorsGenerator = new ColorsGenerator(getContext());

        FragmentManager fragmentManager = getChildFragmentManager();


        bottomNavigation = root.findViewById(R.id.bottom_navigation);

        bottomNavigationContainer = root.findViewById(R.id.bottom_navigation_container);

        homeButton = root.findViewById(R.id.bottom_nav_home_button);
        searchButton = root.findViewById(R.id.search_button);
        pager = root.findViewById(R.id.pager);
        addMemoryButton = root.findViewById(R.id.add_memory_button);

        PagerAdapter pagerAdapter = new PagerAdapter(fragmentManager, 2);
        pager.setAdapter(pagerAdapter);
        pager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                try {
                    whichPage = position;
                    if (whichPage == 0) {
                        homeButton.callOnClick();
                    } else {
                        searchButton.callOnClick();
                    }
                } catch (Exception e) {
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        homeAnim = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_home_button);
        reverseHomeAnim = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_home_button_reverse);

        searchAnim = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_search_button);
        reverseSearchAnim = AnimatedVectorDrawableCompat.create(getContext(), R.drawable.anim_search_button_reverse);

        homeButton.setImageDrawable(homeAnim);
        searchButton.setImageDrawable(searchAnim);

        homeTab = new HomeFragment();
        searchTab = new SearchFragment();

        homeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichPage = 0;
                onPageChange(whichPage);
            }
        });
        homeButton.setClickable(false);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                whichPage = 1;
                onPageChange(whichPage);
            }
        });
        addMemoryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String date = DateGenerator.getDateOfToday();
                String colorHex;
                try {
                    colorHex = colorsGenerator.next(memoriesGenerator.generateMain().get(0).getColorHex());
                } catch (Exception e) {
                    colorHex = colorsGenerator.yellow;
                }

                Memory new_memory = new Memory(0, 0, "Day " + (memoriesGenerator.generateMain().size() + 1), "new", date, null, null, colorHex, false, false);

                //we use "new" as desc to tell openedMemoryFragment that this is a new memory so it open edit mode
                homeTab.memoriesAdapter.add_memory(new_memory);
                homeTab.recyclerView.scrollToPosition(0);
                ((NavigationDrawerFragment) getParentFragment()).open_memory(addMemoryButton, new_memory, homeTab.recyclerView);
                new Thread() {
                    @Override
                    public void run() {
                        FirebaseAnalytics firebaseAnalytics = FirebaseAnalytics.getInstance(getContext());
                        Bundle b = new Bundle();
                        b.putInt("memoriesNum", 5);
                        firebaseAnalytics.logEvent("CreatedANewMemory", b);
                        Log.e(getClass().getName() , "SUCESS!");
                    }
                }.start();
            }
        });

        homeTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationDrawerFragment) getParentFragment()).open_memory(v, (Memory) v.getTag(), homeTab.recyclerView);
            }
        });


        searchTab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((NavigationDrawerFragment) getParentFragment()).open_memory(v, (Memory) v.getTag(), searchTab.recyclerView);
            }
        });

        bottomNavigation.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                                                                             @Override
                                                                             public void onGlobalLayout() {
                                                                                 bottomNavigation.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                                                                                 Y = bottomNavigation.getHeight();
                                                                             }
                                                                         }
        );


        RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                if (dy > 0 & !scrolled) {
                    bottomNavigationContainer.animate().y(Y).setDuration(500).start();
                    scrolled = true;
                } else if (dy < 0 & scrolled) {
                    bottomNavigationContainer.animate().y(0).setDuration(500).start();
                    scrolled = false;
                }
                super.onScrolled(recyclerView, dx, dy);
            }
        };

        homeTab.setOnScrollListener(onScrollListener);

        searchTab.setOnScrollListener(onScrollListener);

        homeTab.setMemoryPosition(memoryPosition);

        return root;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (memoryPosition == -1)
            starter_animations();
    }

    private void onPageChange(int whichPage) {
        if (whichPage == 0) {
            pager.setCurrentItem(0);
            homeButton.setClickable(false);
            searchButton.setClickable(true);
            homeButton.setImageDrawable(homeAnim);
            homeAnim.start();

            searchButton.setImageDrawable(reverseSearchAnim);
            reverseSearchAnim.start();


            homeTab.memoriesAdapter.memories.clear();
            homeTab.memoriesAdapter.setSearchKey(null);
            homeTab.memoriesAdapter.memories.addAll(memoriesGenerator.generateMain());
            homeTab.memoriesAdapter.notifyDataSetChanged();
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            try {
                inputMethodManager.hideSoftInputFromWindow(getView().getWindowToken(), 0);
            } catch (NullPointerException ignored) {
            }
        } else if (whichPage == 1) {
            searchButton.setImageDrawable(searchAnim);
            homeButton.setImageDrawable(reverseHomeAnim);
            searchButton.setClickable(false);
            homeButton.setClickable(true);
            searchAnim.start();
            reverseHomeAnim.start();
            pager.setCurrentItem(1);

            searchTab.memoriesAdapter.memories.clear();
            if (searchTab.search_bar.getText().toString().isEmpty())
                searchTab.memoriesAdapter.memories.addAll(memoriesGenerator.search(null, 0));
            else
                searchTab.memoriesAdapter.memories.addAll(memoriesGenerator.search(searchTab.search_bar.getText().toString().trim(), 0));
            searchTab.memoriesAdapter.notifyDataSetChanged();
        }
    }

    public void setMemoryPosition(int memoryPosition) {
        this.memoryPosition = memoryPosition;
    }


    class PagerAdapter extends FragmentStatePagerAdapter {
        int numOfTabs;

        public PagerAdapter(FragmentManager fm, int NumberOFTabs) {
            super(fm);
            this.numOfTabs = NumberOFTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return homeTab;
                case 1:
                    return searchTab;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return numOfTabs;
        }
    }


    public void starter_animations() {
        root.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {

                root.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                ObjectAnimator scaleX = ObjectAnimator.ofFloat(addMemoryButton, "scaleX", 0, 1);
                ObjectAnimator scaleY = ObjectAnimator.ofFloat(addMemoryButton, "scaleY", 0, 1);


                ObjectAnimator translationY = ObjectAnimator.ofFloat(bottomNavigation, "translationY", bottomNavigation.getHeight(), 0);

                AnimatorSet a = new AnimatorSet();

                a.play(scaleX).with(scaleY).with(translationY);

                a.setDuration(500);
                a.start();
                homeAnim.start();
            }
        });
    }

}