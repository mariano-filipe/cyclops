package com.example.cyclops;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat; // [DEBUG]
import android.graphics.Matrix;
import android.os.Environment; // [DEBUG]

import com.google.zxing.Result;
import com.google.zxing.ResultPoint;

import java.io.File; // [DEBUG]
import java.io.FileOutputStream; // [DEBUG]
import java.text.SimpleDateFormat; // [DEBUG]
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date; // [DEBUG]
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Utils {

  // Used for debug purposes only
  static public void saveImage(Bitmap finalBitmap) {
    String root = Environment.getExternalStorageDirectory().toString();
    File myDir = new File(root + "/saved_images");
    myDir.mkdirs();

    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
    String fname = "Cyclops-" + timeStamp + ".png";

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

  private static List<List> encodeResultPoints(ResultPoint[] points) {
    List<List> ret = new ArrayList<List>();
    for (ResultPoint point : points) {
      ret.add(Arrays.asList(point.getX(), point.getY()));
    }
    return ret;
  }

  public static Map<String, Object> encodeResult(Result result) {
    if (result == null) return null;

    Map<String, Object> ret = new HashMap<String, Object>();
    ret.put("rawValue", result.getText());
    ret.put("format", BarcodeFormat.mapToInt(result.getBarcodeFormat()));
    ret.put("points", Utils.encodeResultPoints(result.getResultPoints()));

    return ret;
  }
}