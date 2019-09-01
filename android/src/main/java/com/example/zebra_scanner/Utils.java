package com.example.zebra_scanner;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat; // [DEBUG]
import android.graphics.Matrix;
import android.os.Environment; // [DEBUG]

import java.io.File; // [DEBUG]
import java.io.FileOutputStream; // [DEBUG]
import java.text.SimpleDateFormat; // [DEBUG]
import java.util.Date; // [DEBUG]
import java.util.Map;

class Utils {

  // Used for debug purposes only
  static public void saveImage(Bitmap finalBitmap) {
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/saved_images");
    myDir.mkdirs();

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fname = "ZebraScanner-" + timeStamp + ".png";

    File file = new File(myDir, fname);
    if (file.exists())
      file.delete();
    try {
      FileOutputStream out = new FileOutputStream(file);
      finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
      out.flush();
      out.close();
    } catch (Exception e) {
      e.printStackTrace();
    }
  }

  public static Bitmap rotateBitmap(Bitmap sourceBitmap, int rotationInDegrees) {
    int width = sourceBitmap.getWidth();
    int height = sourceBitmap.getHeight();
    Matrix matrix = new Matrix();
    matrix.preRotate(rotationInDegrees);
    return Bitmap.createBitmap(sourceBitmap, 0, 0, width, height, matrix, true);
  }
}