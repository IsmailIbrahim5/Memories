package com.idea.memories.Views.Activities;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.LruCache;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.view.GravityCompat;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import com.idea.memories.R;

import com.idea.memories.Views.Fragments.RetainFragment;
import com.idea.memories.Classes.MediaFile;
import com.idea.memories.Views.Fragments.SignUpFragment;
import com.idea.memories.Views.Fragments.WelcomeFragment;
import com.idea.memories.Views.Fragments.NavigationDrawerFragment;

import java.io.InputStream;

public class MainActivity extends AppCompatActivity{
    private FragmentTransaction fragmentTransaction;
    private FragmentManager fragmentManager;

    private NavigationDrawerFragment navigationDrawerFragment;

    private LruCache<String , Bitmap> memoryCache;

    private SharedPreferences sharedPreferences;
    public int nightMode;
    /*libcore.io.DiskLruCache diskLruCache ;

       private final Object diskCacheLock = new Object();
       private boolean diskCacheStarting = true;
       private static final int DISK_CACHE_SIZE = 1024 * 1024 * 10;
       private static final String DISK_CACHE_SUBDIR = "thumbnails";

        */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

        final int cacheSize = maxMemory / 8;

        RetainFragment retainFragment = RetainFragment.findOrCreateRetainFragment(getSupportFragmentManager());
        memoryCache = retainFragment.retainedCache;
        if(memoryCache == null) {
            memoryCache = new LruCache<String, Bitmap>(cacheSize) {
                @Override
                protected int sizeOf(String key, Bitmap bitmap) {
                    return bitmap.getByteCount() / 1024;
                }
            };
            retainFragment.retainedCache = memoryCache;
        }

        welcome();
    }


    public void loadingBitmap (final MediaFile mediaFile , final ImageView imageSource , int width , int height) {
        final String key = String.valueOf(mediaFile.getID());
//        Bitmap bitmap = getBitmapFromDiskCache(key);

        Bitmap bitmap = getBitmapFromMemoryCache(key);
        try{
            if (bitmap.getHeight()>= height & bitmap.getWidth() >= width) {
                imageSource.setImageBitmap(bitmap);
            }
            else
                throw new Exception();
        }catch(Exception e) {
            BitmapWorkerTask task = new BitmapWorkerTask(imageSource, width, height);
            task.execute(mediaFile);
        }
    }

    private class BitmapWorkerTask extends AsyncTask<MediaFile , Void , Bitmap> {

        private final ImageView imageSource;
        private final int width ,height;
        public BitmapWorkerTask(ImageView imageSource , int width , int height) {
            this.imageSource = imageSource;
            this.height = height;
            this.width = width;
        }

        @Override
        protected Bitmap doInBackground(final MediaFile... mediaFiles) {
            Bitmap resizedBitmap = null;

            if(mediaFiles[0].getMimeType().contains("image")) {
                resizedBitmap = resizeBitmap(mediaFiles[0].getPath() , width , height);
            }
            else if (mediaFiles[0].getMimeType().contains("video")) {
                resizedBitmap = getBitmap(mediaFiles[0].getPath());
            }

            //addBitmapToCache(String.valueOf(mediaFiles[0].getID()), resizedBitmap);
            return resizedBitmap;
        }

        @Override
        protected void onPostExecute(Bitmap bitmap) {
            imageSource.setImageBitmap(bitmap);
        }
    }

    private Bitmap getBitmap(Uri uri)
    {
        try {
            MediaMetadataRetriever retriever = new MediaMetadataRetriever();
            retriever.setDataSource(this, uri);
            return retriever.getFrameAtTime(1,MediaMetadataRetriever.OPTION_NEXT_SYNC);
        }catch (Exception e){}
        return null;
    }

    private void addBitmapToCache(String key , Bitmap bitmap) { try { memoryCache.put(key, bitmap); } catch (Exception e) {} }

      /*  synchronized (diskCacheLock){
            try {
               if (diskLruCache != null & diskLruCache.get(key) == null){
                 diskLruCache.put(key , bitmap);
                }
            }catch (Exception e){
            }
        }

       */

    /*
    public Bitmap getBitmapFromDiskCache(String key) {
        synchronized (diskCacheLock) {
            while (diskCacheStarting) {
                try {
                    diskCacheLock.wait();
                } catch (InterruptedException e) {}
            }
            if (diskLruCache != null) {
                try {
                    return diskLruCache.get(key);
                } catch (Exception e) {
                    return null;
                }
            }
        }
        return null;
    }
 */


        /*
    private class InitDiskCacheTask extends AsyncTask<File , Void , Void> {
        @Override
        protected Void doInBackground(File... files) {
            synchronized (diskCacheLock){
                try {
                File cacheDir = files[0];
                diskLruCache = DiskLruCache.open(cacheDir , 0 , 0 , DISK_CACHE_SIZE);
                diskCacheStarting = false;
                diskCacheLock.notifyAll();
                } catch (Exception e) {
                    Log.e(getClass().getName(), e.getMessage());
                }
            }
            return null;
        }
    }
     */

        /*
  public Bitmap resize_bitmap(Uri image_picture) {
    Bitmap resizedBitmap;
    BitmapFactory.Options options = new BitmapFactory.Options();
    resizedBitmap = BitmapFactory.decodeFile(getId(image_picture), options);
    float height_scale = (float) resizedBitmap.getHeight() / img_min_height;
    float width_scale = (float) resizedBitmap.getWidth() / img_max_height;

    options.inScaled = true;
    options.inSampleSize = (int) ((height_scale + width_scale) / 2);
    options.inDensity = resizedBitmap.getWidth();
    options.inTargetDensity = (int)img_max_height * options.inSampleSize;
    resizedBitmap = BitmapFactory.decodeFile(getId(image_picture), options);
    return resizedBitmap;
  }
*/

    /*private File getDiskCacheDir(Context context, String uniqueName) {
        final String cachePath = Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState()) ||
                ! Environment.isExternalStorageRemovable() ? context.getExternalCacheDir().getPath() :
                context.getCacheDir().getPath();
        return new File(cachePath + File.separator + uniqueName);
    }
*/

    private Bitmap getBitmapFromMemoryCache(String key) { return memoryCache.get(key); }


    private Bitmap resizeBitmap(Uri imagePath , int width , int height) {
        Bitmap result = null;
        try {
            Bitmap resizedBitmap = BitmapFactory.decodeFile(imagePath.getPath());
            result = Bitmap.createScaledBitmap(resizedBitmap,width , height,true);
        }
        catch (Exception e){
            InputStream inputStream = null;
            try {
                inputStream = getContentResolver().openInputStream(imagePath);
                Bitmap resizedBitmap = BitmapFactory.decodeStream(inputStream);
                result = Bitmap.createScaledBitmap(resizedBitmap ,width , height, true);
            } catch (Exception e1) {}
        }
        return result;
    }


    private void welcome() {
        fragmentManager = getSupportFragmentManager();

        fragmentTransaction = fragmentManager.beginTransaction();

        WelcomeFragment welcomeFragment = new WelcomeFragment();
        fragmentTransaction.replace(R.id.navHost , welcomeFragment);
        fragmentTransaction.commit();
    }

    public void signUp() {

        fragmentTransaction = fragmentManager.beginTransaction();

        SignUpFragment signUpFragment = new SignUpFragment();
        fragmentTransaction.replace(R.id.navHost , signUpFragment);
        fragmentTransaction.commit();
    }

    public void main() {
        fragmentTransaction = fragmentManager.beginTransaction();

        navigationDrawerFragment = new NavigationDrawerFragment();
        int memoryPosition = -1;
        navigationDrawerFragment.setMemoryPosition(memoryPosition);
        navigationDrawerFragment.is = nightMode == AppCompatDelegate.MODE_NIGHT_YES;
        fragmentTransaction.replace(R.id.navHost , navigationDrawerFragment);
        fragmentTransaction.commit();
    }


    @SuppressLint("WrongConstant")
    @Override
    public void onBackPressed() {
        try {
            if (navigationDrawerFragment.is_memory_open) {
                if (navigationDrawerFragment.o.is_media_on) {
                    if(navigationDrawerFragment.o.mediaFragment.is_media_opened) {
                        navigationDrawerFragment.o.mediaFragment.openedMediaFragment.ender_animations();
                        navigationDrawerFragment.o.mediaFragment.is_media_opened = false;
                        navigationDrawerFragment.o.mediaFragment.toolbar.setVisibility(View.VISIBLE);
                    }
                    else
                        navigationDrawerFragment.o.mediaFragment.ender_animation();
                } else if (navigationDrawerFragment.o.title_edit.getVisibility() != View.VISIBLE) {
                    navigationDrawerFragment.o.fake.callOnClick();
                } else
                    navigationDrawerFragment.o.edit_fab.callOnClick();
            } else if (navigationDrawerFragment.drawerLayout.isDrawerOpen(Gravity.START)) {
                navigationDrawerFragment.drawerLayout.closeDrawer(GravityCompat.START, true);
            } else
                finish();
        } catch (Exception e) {
            finish();
        }
    }
/*
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK & requestCode == 65541) {
            signUpFragment.onActivityResult(requestCode , resultCode , data);
        } else if (resultCode == RESULT_OK) {
            Log.e(getClass().getName(), requestCode + "");
            o.onActivityResult(requestCode, resultCode, data);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,String[] permissions, int[] grantResults) {
        if (requestCode == 65541){
            signUpFragment.onRequestPermissionsResult(requestCode , permissions , grantResults);
        }
        else {
            if(grantResults[0] == 0){
                o.onRequestPermissionsResult(requestCode, permissions, grantResults);
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }
    */


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        nightMode = AppCompatDelegate.getDefaultNightMode();

        sharedPreferences = getSharedPreferences("nightMode" , MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putInt("isNightModeOn" , nightMode);
        editor.apply();
    }

}



