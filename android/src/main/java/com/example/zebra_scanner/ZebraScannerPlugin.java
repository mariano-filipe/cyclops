package com.example.zebra_scanner;

import android.graphics.Color;
import android.graphics.Bitmap;
// import android.graphics.Bitmap.CompressFormat;  // [DEBUG]
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
// import android.Manifest;  // [DEBUG]
// import android.os.Environment;  // [DEBUG]

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

// import java.io.File;  // [DEBUG]
// import java.io.FileOutputStream;  // [DEBUG]
import java.io.IOException;
// import java.text.SimpleDateFormat; // [DEBUG]
import java.util.ArrayList;
import java.util.Arrays;
// import java.util.Date;  // [DEBUG]
import java.util.Hashtable;
import java.util.Map;

// import com.google.zxing.BarcodeFormat; // [FUTURE]
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

/** ZebraScannerPlugin */
public class ZebraScannerPlugin implements MethodCallHandler {
  private MultiFormatReader detector = new MultiFormatReader();
  private String barcode;

  // Used for debug purposes only
  // private void saveImage(Bitmap finalBitmap) {
  //   String root = Environment.getExternalStorageDirectory().toString();
  //   File myDir = new File(root + "/saved_images");
  //   myDir.mkdirs();

  //   String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
  //   String fname = "ZebraScanner-" + timeStamp + ".png";

  //   File file = new File(myDir, fname);
  //   if (file.exists())
  //     file.delete();
  //   try {
  //     FileOutputStream out = new FileOutputStream(file);
  //     finalBitmap.compress(Bitmap.CompressFormat.PNG, 100, out);
  //     out.flush();
  //     out.close();
  //   } catch (Exception e) {
  //     e.printStackTrace();
  //   }
  // }

  private static Bitmap rotateBitmap(Bitmap sourceBitmap, int rotationInDegrees) {
    int width = sourceBitmap.getWidth();
    int height = sourceBitmap.getHeight();
    Matrix matrix = new Matrix();
    matrix.preRotate(rotationInDegrees);
    return Bitmap.createBitmap(sourceBitmap, 0, 0, width, height, matrix, true);
  }

  private Bitmap decodeBytesToBitmap(byte[] imgBytes, Map<String, Object> imgMetadata) {
    // Image info
    int width = (int) Math.round((double) imgMetadata.get("width"));
    int height = (int) Math.round((double) imgMetadata.get("height"));
    ArrayList imgPlanesMetadata = (ArrayList) imgMetadata.get("planeData");

    // Y Plane (0)
    Map yPlaneMetadata = (Map) imgPlanesMetadata.get(0);
    int bytesPerPixel = (int) yPlaneMetadata.get("bytesPerPixel");
    int bytesPerRow = (int) yPlaneMetadata.get("bytesPerRow");
    int rowPadding = bytesPerRow - bytesPerPixel * width; // in bytes
    int rowStride = width + rowPadding / bytesPerPixel; // in pixels

    // [FIX] Use all image planes instead of only the Y one. For now, imgBytes only contains data of
    //    the Y plane.
    // [FIX] For some reason the bytes of Y plane are not complete. In my tests [rowPadding] bytes
    //    are missing. This is why we initialize the int array with [rowStride * height] instead of
    //    [imgBytes.length].
    int[] rgbaImage = new int[rowStride * height];
    for (int i = 0; i < imgBytes.length; i++) {
      rgbaImage[i] = Color.rgb(imgBytes[i], imgBytes[i], imgBytes[i]);
    }
    return Bitmap.createBitmap(rgbaImage, rowStride, height, Config.ARGB_8888);
  }

  public void initialize(MethodCall call) {
    // Map options = call.argument("options");
    // String optionsStringified = options != null ? options.toString() : null;

    // [FIX] Add more hints to the detector so that it can perform better in more restricted
    //    scenarios. The [options] arguments should be used for that.
    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(1);
    // hints.put(DecodeHintType.POSSIBLE_FORMATS,
    // Arrays.asList(BarcodeFormat.EAN_13));
    hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
    detector.setHints(hints);
  }

  // Resets any internal state the implementation has after a decode, to prepare
  // it for reuse.
  public void close(MethodCall call) {
    this.detector.reset();
  }

  private String detectInBitmap(Bitmap bitmap) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();

    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    RGBLuminanceSource luminanceSource = new RGBLuminanceSource(width, height, pixels);
    HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
    BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

    com.google.zxing.Result result;
    try {
      result = detector.decodeWithState(binaryBitmap);
    } catch (NotFoundException e) {
      result = null;
    }

    // [FIX] Check if zxing library can detect more than one barcode at the same time.
    return result != null ? result.getText() : null;
  }

  public void detectInImage(MethodCall call) {
    Bitmap bitmap = null;
    String imgType = call.argument("type");

    if (imgType.equals("file")) {
      String imgPath = call.argument("path");
      bitmap = BitmapFactory.decodeFile(imgPath);
    } else if (imgType.equals("bytes")) {
      byte[] bytes = call.argument("bytes");
      Map<String, Object> metadata = call.argument("metadata");

      bitmap = this.decodeBytesToBitmap(bytes, metadata);
      // This can be used to visually check the output of the decoded image
      // this.saveImage(bitmap);
    }

    this.barcode = this.detectInBitmap(bitmap);
    // If barcode is null, try again in opposite orientation (horizontal <-> vertical)
    // [FIX] Try to find a better way of approaching this situation.
    if (this.barcode == null) {
      bitmap = rotateBitmap(bitmap, 90);
      this.barcode = this.detectInBitmap(bitmap);
    }
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    // registrar.activity().requestPermissions(new String[] { Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "zebra_scanner");
    channel.setMethodCallHandler(new ZebraScannerPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    Object args = call.arguments();
    Class argsClass = args != null ? args.getClass() : null;
    String argsClassname = argsClass != null ? argsClass.toString() : null;
    String argsStringified = args != null ? args.toString() : null;
    System.out.println("Calling method: " + call.method);
    System.out.println("The arguments (" + argsClassname + ") are:" + argsStringified);

    if (call.method.equals("detectInImage")) {
      this.detectInImage(call);
      result.success(Arrays.asList(this.barcode));
    } else if (call.method.equals("initialize")) {
      this.initialize(call);
      result.success(null);
    } else if (call.method.equals("close")) {
      this.close(call);
      result.success(null);
    } else {
      result.notImplemented();
    }
  }
}
