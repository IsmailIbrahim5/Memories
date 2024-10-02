package com.idea.memories.Classes;

import android.net.Uri;
import java.io.Serializable;

public class MediaFile implements Serializable {

  private Uri path;
  private String mimeType;
  private long ID;

  public MediaFile(Uri path, String mimeType, long ID) {
    this.path = path;
    this.mimeType = mimeType;
    this.ID = ID;
  }

  public long getID() {
    return ID;
  }

  public Uri getPath() {
    return path;
  }

  public void setPath(Uri path) {
    this.path = path;
  }

  public String getMimeType() {
    return mimeType;
  }
}
