package com.idea.memories.Adapters;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.idea.memories.R;

import com.idea.memories.Classes.ColorsGenerator;
import com.idea.memories.Views.Activities.MainActivity;
import com.idea.memories.Views.CustomViews.ImageShape;
import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;

import java.util.ArrayList;

import androidx.dynamicanimation.animation.DynamicAnimation;
import androidx.dynamicanimation.animation.SpringAnimation;
import androidx.dynamicanimation.animation.SpringForce;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;


public class MemoriesAdapter extends RecyclerView.Adapter<MemoriesAdapter.MemoryViewHolder> {

    public ArrayList<Memory> memories;
    private ColorsGenerator colorsGenerator;
    private Context context;
    private LayoutInflater inflater;
    private MainActivity activity;
    private MemoriesGenerator memoriesGenerator;
    private View.OnClickListener onClickListener;
    private ImageView favouriteButton, trashButton;

    public String searchKey;
    private int height ,width;
    public int mnen;
    public MemoriesAdapter(Context context, FragmentActivity activity, ArrayList<Memory> memories , int mnen, View.OnClickListener onClickListener) {
        inflater = LayoutInflater.from(context);
        memoriesGenerator = new MemoriesGenerator(context);

        this.context = context;
        this.memories = memories;
        this.activity = (MainActivity) activity;
        this.mnen = mnen;
        this.onClickListener = onClickListener;
    }


    @Override
    public MemoryViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.memory_sample, parent, false);
        return new MemoryViewHolder(view);
    }


    @Override
    public void onBindViewHolder(final MemoryViewHolder holder, final int position) {
        final Memory memory = memories.get(position);

        colorsGenerator = new ColorsGenerator(context);

        SpringAnimation scaleY = new SpringAnimation(holder.itemView, DynamicAnimation.SCALE_Y, 1.0f)
                , scaleX = new SpringAnimation(holder.itemView, DynamicAnimation.SCALE_X, 1.0f);

        scaleX.setStartValue(0);
        scaleX.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        scaleY.setStartValue(0);
        scaleY.getSpring().setStiffness(SpringForce.STIFFNESS_VERY_LOW);

        scaleX.start();
        scaleY.start();

        holder.title.setText(memory.getTitle());
        holder.date.setText(memory.getDate());

        holder.favored.setVisibility(memory.isFavored() ? View.VISIBLE : View.INVISIBLE);

        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                ObjectAnimator y1 = ObjectAnimator.ofFloat(holder.itemView, "translationY", -20);
                ObjectAnimator y2 = ObjectAnimator.ofFloat(holder.itemView, "translationY", 0);

                final AnimatorSet a = new AnimatorSet();
                a.play(y1).before(y2);

                a.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        a.start();
                    }
                });
                a.start();

                final PopupWindow popupWindow = popupDisplay(memory);

                popupWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                    @Override
                    public void onDismiss() {
                        a.removeAllListeners();
                    }
                });

                popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

                if (!memory.isDeleted()) {
                    if (memory.isFavored()) {
                        favouriteButton.setImageDrawable(context.getDrawable(R.drawable.ic_white_favourite_on));
                    }
                    favouriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {

                            final boolean favored = memory.isFavored();
                            final int intFavored = favored ? 1 : 0;
                            final int oppositeFavored = favored ? 0 : 1;

                            holder.favored.setVisibility(View.VISIBLE);

                            ObjectAnimator x = ObjectAnimator.ofFloat(holder.favored, "ScaleX", intFavored, oppositeFavored);
                            ObjectAnimator y = ObjectAnimator.ofFloat(holder.favored, "ScaleY", intFavored, oppositeFavored);

                            AnimatorSet a = new AnimatorSet();

                            a.play(x).with(y);

                            a.addListener(new AnimatorListenerAdapter() {
                                @Override
                                public void onAnimationEnd(Animator animation) {
                                    holder.favored.setVisibility(favored ? View.INVISIBLE : View.VISIBLE);
                                }
                            });

                            a.start();

                            if (favored)
                                memory.setFavored(false);
                            else
                                memory.setFavored(true);

                            memoriesGenerator.updateMemory(memory);

                            holder.root.setTag(memory);

                            if (mnen == 1 & favored)
                                removeMemory(position);

                            popupWindow.dismiss();
                        }
                    });
                }
                else {
                    favouriteButton.setImageDrawable(context.getDrawable(R.drawable.ic_undo));
                    favouriteButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            removeMemory(position);
                            popupWindow.dismiss();
                            memory.setDeleted(false);
                            memoriesGenerator.updateMemory(memory);
                        }
                    });

                    trashButton.setImageTintList(ColorStateList.valueOf(Color.parseColor(MemoriesAdapter.this.colorsGenerator.red)));
                }

                trashButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteMemory((Memory) holder.root.getTag());
                        popupWindow.dismiss();
                    }
                });

                popupWindow.showAsDropDown(v, 0, -48);
                return true;
            }

        });

        if(width== 0 & height == 0) {
            holder.itemView.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.itemView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    width = holder.mainPhoto.getWidth();
                    height = width;
                    holder.itemView.getLayoutParams().height = width;
                    holder.itemView.requestLayout();
                }
            });
        }
        else{
            holder.itemView.getLayoutParams().height = width;
            holder.itemView.requestLayout();
        }
        if (memory.isDeleted()) {
            ((ImageShape) holder.mainPhoto).setPathColor(Color.parseColor(colorsGenerator.deleted));
        }
        else {
                ((ImageShape)holder.mainPhoto).setPathColor(Color.parseColor(memory.getColorHex()));
                holder.mainPhoto.setImageDrawable(null);
                if(memory.getMainPhoto()!=null){
                    holder.mainPhoto.post(new Runnable() {
                        @Override
                        public void run() {
                            activity.loadingBitmap(memory.getMainPhoto(), holder.mainPhoto, holder.mainPhoto.getWidth(), holder.mainPhoto.getHeight());
                        }
                    });
                }
        }

        holder.root.setTag(memory);

        memoriesGenerator.putIndexes(memories);

    }

    public class MemoryViewHolder extends RecyclerView.ViewHolder {
        TextView title, date;
        ImageView mainPhoto, favored;
        View root;

        public MemoryViewHolder(View itemView) {
            super(itemView);
            title = itemView.findViewById(R.id.title);
            date = itemView.findViewById(R.id.date);
            favored = itemView.findViewById(R.id.favourited);
            root = itemView.findViewById(R.id.root);
            mainPhoto = itemView.findViewById(R.id.background_photo);
            root.setOnClickListener(onClickListener);
        }
    }

    public void add_memory(final Memory m) {
        new Thread() {
            @Override
            public void run() {
                memoriesGenerator.addMemory(m);
                memoriesGenerator.putIndexes(memories);
                m.setPosition(0);
            }
        }.start();
        memories.add(0, m);
        notifyItemInserted(0);
    }

    public void deleteMemory(Memory memory){
        if(mnen == 2)
            memoriesGenerator.removeMemory(memory);
        else
            memoriesGenerator.deleteMemory(memory);
        removeMemory(memory.getPosition());
    }

    public void removeMemory(int position){
        memories.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position , memories.size());
        memoriesGenerator.putIndexes(memories);
    }

    @Override
    public int getItemCount() {
        return memories.size();
    }


    private PopupWindow popupDisplay(Memory memory) {
        final PopupWindow popupWindow = new PopupWindow(context);

        final View view = inflater.inflate(R.layout.custom_pop_menu, null);

        favouriteButton = view.findViewById(R.id.favourite_button);
        trashButton =  view.findViewById(R.id.trash_button);


        if(memory.isDeleted())
            view.findViewById(R.id.root).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(colorsGenerator.deleted)));
        else
            view.findViewById(R.id.root).setBackgroundTintList(ColorStateList.valueOf(Color.parseColor(memory.getColorHex())));
        popupWindow.setFocusable(true);

        view.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                view.getViewTreeObserver().removeOnPreDrawListener(this);
                popupWindow.setWidth(RelativeLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setHeight(RelativeLayout.LayoutParams.WRAP_CONTENT);
                popupWindow.setElevation(0);
                return true;
            }
        });
        popupWindow.setContentView(view);

        return popupWindow;
    }

    public void setSearchKey(String searchKey) {
        this.searchKey = searchKey;
    }

}

