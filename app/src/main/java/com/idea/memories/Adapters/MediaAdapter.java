package com.idea.memories.Adapters;
//remember to use DiskLruCache Whenever you can
//all the code has been set
//the only problem was that i couldn't find the right implementation of the class
//i hope you knew the cause of it :D

import android.database.Cursor;
import android.graphics.Color;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;
import android.widget.TextView;
import com.idea.memories.R;

import com.idea.memories.Views.Activities.MainActivity;
import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Classes.MemoriesGenerator;
import com.idea.memories.Classes.Memory;

import java.io.File;
import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.GalleryFileViewHolder> {

    private ArrayList<MediaFile> mediaFiles;
    private LayoutInflater inflater;
    private Memory memory;
    private MainActivity mainActivity;
    private MemoriesGenerator memoriesGenerator;
    private RecyclerView recyclerView;
    private View.OnClickListener onItemClickListener;
    // deleteOn = item with delete sign on it
    // if there is no item with sign on it
    // deleteOn = -1
    public int deleteOn = -1;
    private int width,height;

    public MediaAdapter(ArrayList<MediaFile> mediaFiles, MainActivity mainActivity, Memory memory , RecyclerView recyclerView,View.OnClickListener OnItemClickListener ) {
        inflater = LayoutInflater.from(mainActivity);
        memoriesGenerator = new MemoriesGenerator(mainActivity);

        this.mainActivity = mainActivity;
        this.mediaFiles = mediaFiles;
        this.memory = memory;
        this.onItemClickListener = OnItemClickListener;
        this.recyclerView = recyclerView;
    }

    @Override
    public void onViewRecycled(@NonNull GalleryFileViewHolder holder) {
        holder.redLayer.setAlpha(0f);
        holder.deleteIcon.setAlpha(0f);
        super.onViewRecycled(holder);
    }

    @Override
    public GalleryFileViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = inflater.inflate(R.layout.media_file_sample, parent, false);
        return new GalleryFileViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final GalleryFileViewHolder holder, final int position) {
        final MediaFile mediaFile = mediaFiles.get(position);
        if(width == 0 & height == 0) {
            holder.imageSource.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
                @Override
                public void onGlobalLayout() {
                    holder.imageSource.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                    width = holder.imageSource.getWidth();
                    height = holder.imageSource.getHeight();
                    mainActivity.loadingBitmap(mediaFile, holder.imageSource, width, height);
                }
            });
        }
        else
            mainActivity.loadingBitmap(mediaFile, holder.imageSource, width, height);

        switch (mediaFile.getMimeType()) {
            case "image":
                holder.mediaType.setImageDrawable(null);
                holder.mediaDuration.setText("");
                break;

            case "video":
                holder.mediaType.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_play));
                holder.mediaDuration.setText(getDuration(mediaFile.getPath()));
                break;

            case "audio":
                holder.mediaType.setImageDrawable(mainActivity.getResources().getDrawable(R.drawable.ic_microphone));
                holder.mediaDuration.setText(getDuration(mediaFile.getPath()));
                holder.mediaDuration.setTextColor(Color.WHITE);
                break;
        }

        holder.itemView.setOnClickListener(onItemClickListener);
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if(deleteOn == (int)holder.itemView.getTag()) {
                    holder.redLayer.animate().alpha(0).setDuration(200).start();
                    holder.deleteIcon.animate().alpha(0).setDuration(200).start();
                    deleteOn = -1;
                }
                else{
                    if(deleteOn != -1){
                        recyclerView.getLayoutManager().getChildAt(deleteOn).findViewById(R.id.delete_icon).animate().alpha(0).setDuration(200).start();
                        recyclerView.getLayoutManager().getChildAt(deleteOn).findViewById(R.id.red_layer).animate().alpha(0).setDuration(200).start();
                    }
                    holder.redLayer.animate().alpha(0.8f).setDuration(200).start();
                    holder.deleteIcon.animate().alpha(1).setDuration(200).start();
                    deleteOn = (int) holder.itemView.getTag();
                }
                return true;
            }
        });
        holder.itemView.setTag(position);
    }

    public static class GalleryFileViewHolder extends RecyclerView.ViewHolder
    {
        ImageView imageSource , mediaType, deleteIcon;
        TextView mediaDuration;
        View redLayer;
        public GalleryFileViewHolder(View itemView) {
            super(itemView);
            imageSource = itemView.findViewById(R.id.image_src);
            mediaType = itemView.findViewById(R.id.media_type);
            mediaDuration = itemView.findViewById(R.id.media_duration);
            deleteIcon = itemView.findViewById(R.id.delete_icon);
            redLayer = itemView.findViewById(R.id.red_layer);
        }
    }

    @Override
    public int getItemCount() {
        return mediaFiles.size();
    }


    public void addMediaFile(Uri uri , String mimeType , long ID) {
        try {
            File file = new File(getPath(uri));
            mediaFiles.add(new MediaFile(Uri.fromFile(file), mimeType, ID));
            notifyItemInserted(mediaFiles.size());
            notifyDataSetChanged();
            new Thread() {
                @Override
                public void run() {
                    MediaFile[] mediaFiles = new MediaFile[MediaAdapter.this.mediaFiles.size()];
                    for (
                            int i = 0;
                            i < MediaAdapter.this.mediaFiles.size(); i++)
                        mediaFiles[i] = MediaAdapter.this.mediaFiles.get(i);
                    memory.setMediaFiles(mediaFiles);
                    memoriesGenerator.updateMemory(memory);
                }
            }.start();
        }catch (Exception e){
            Log.e(getClass().getName() , e.getMessage());
        }
    }

    public void removeMediaFile(int position) {
        mediaFiles.remove(position);
        notifyItemRemoved(position);
        notifyItemRangeChanged(position , mediaFiles.size());
        new Thread() {
            @Override
            public void run() {
                MediaFile[] mediaFiles = new MediaFile[MediaAdapter.this.mediaFiles.size()];
                for (int i = 0; i < MediaAdapter.this.mediaFiles.size(); i++)
                    mediaFiles[i] = MediaAdapter.this.mediaFiles.get(i);
                memory.setMediaFiles(mediaFiles);
                memoriesGenerator.updateMemory(memory);

            }
        }.start();
    }


    public String getPath(Uri uri)
    {
        if (uri == null)
            return null ;
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = mainActivity.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) {
            return uri.getPath();
        }
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s = cursor.getString(column_index);
        cursor.close();
        return s;
    }


    private String getDuration(Uri uri)
    {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(mainActivity, uri);
            String time = retriever.extractMetadata(MediaMetadataRetriever.METADATA_KEY_DURATION);
            long durationInMilliSec = Long.parseLong(time);

            retriever.release();
            long minutes = (durationInMilliSec / 1000) / 60;
            long seconds = (durationInMilliSec / 1000) % 60;

            return minutes + ":" + seconds;
        }catch (Exception e){
            Log.e(getClass().getName() , e.getMessage());
        }
        return null;
    }
}


