package com.idea.memories.Classes;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class UserData implements Serializable {
    private String userName;
    private String userProfilePicture;
    private transient Context context;

    public UserData(Context context) {
        this.context = context;
    }

    public void writeData(String username , Uri user_profile_picture){
        try {
            FileOutputStream fos = context.openFileOutput("userProfile" , Context.MODE_PRIVATE);
            ObjectOutputStream oos = new ObjectOutputStream(fos);
            this.userName = username;
            this.userProfilePicture = getPath(user_profile_picture);
            oos.writeObject(this);
            oos.close();
            fos.close();
        } catch (Exception e) {
            Log.e(getClass().getName() , e.getMessage());
        }
    }

    public UserData getData(){
        UserData result = null;
        try{
            FileInputStream fis = context.openFileInput("userProfile");
            ObjectInputStream ois = new ObjectInputStream(fis);
            result = (UserData) ois.readObject();
            ois.close();
            fis.close();
        } catch (Exception e){
            Log.e(getClass().getName() , e.getMessage());
        }
        return result;
    }

    public boolean isExists (){
        try {
            context.openFileInput("userProfile");
            return true;
        } catch (Exception e) {}
        return false;
    }

    private String getPath(Uri uri)
    {
        if(uri == null)
            return null;
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = context.getContentResolver().query(uri, projection, null, null, null);
        if (cursor == null) return null;
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        String s=cursor.getString(column_index);
        cursor.close();
        return s;
    }

    public String getUserName() {
        return userName;
    }

    public String getUserProfilePicture() {
        return userProfilePicture;
    }
}
