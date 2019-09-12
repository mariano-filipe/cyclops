package com.example.cyclops;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
// import android.Manifest;  // [DEBUG]

import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.common.HybridBinarizer;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

/** CyclopsPlugin */
public class CyclopsPlugin implements MethodCallHandler {
  private MultiFormatReader detector = new MultiFormatReader();
  private Decoder decoder = new Decoder();

  // [FIX] Add more hints to the detector so that it can perform better in more
  // restricted scenarios. The [options] arguments should be used for that.
  public void initialize(MethodCall call) {
    int allowedFormats = call.argument("barcodeFormats");
    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(2);

    hints.put(DecodeHintType.POSSIBLE_FORMATS, BarcodeFormat.enumerateFromInt(allowedFormats));
    hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
    this.detector.setHints(hints);
  }

  // Resets any internal state the implementation has after a decode, to prepare
  // it for reuse.
  public void close(MethodCall call) {
    this.detector.reset();
  }

  private Map<String, Object> detectInPixels(int[] pixels, int width, int height) {
    RGBLuminanceSource luminanceSource = new RGBLuminanceSource(width, height, pixels);
    HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
    BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);

    com.google.zxing.Result result;
    try {
      result = detector.decodeWithState(binaryBitmap);
    } catch (NotFoundException e) {
      result = null;
    }

    return Utils.encodeResult(result);
  }

  private Map<String, Object> detectInBitmap(Bitmap bitmap) {
    int width = bitmap.getWidth();
    int height = bitmap.getHeight();

    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);

    return this.detectInPixels(pixels, width, height);
  }

  public Map<String, Object> detectInImage(MethodCall call) {
    String imgType = call.argument("type");

    if (imgType.equals("file")) {
      String imgPath = call.argument("path");

      Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
      Map<String, Object> result = this.detectInBitmap(bitmap);
      // If nothing was found, try again in opposite orientation (horizontal <->
      // vertical). This could be enabled for both image types, but performance is
      // more critical when using bytes because the image is probably coming from
      // camera image stream.
      if (result == null) {
        result = this.detectInBitmap(Utils.rotateBitmap(bitmap, 90));
      }
      return result;
    } else if (imgType.equals("bytes")) {
      byte[] bytes = call.argument("bytes");
      Map<String, Object> metadata = call.argument("metadata");

      DecodedImage image = decoder.decode(bytes, metadata);
      return this.detectInPixels(image.pixels, image.width, image.height);
    }

    return null;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    // registrar.activity().requestPermissions(new String[] {
    // Manifest.permission.WRITE_EXTERNAL_STORAGE }, 1);

    final MethodChannel channel = new MethodChannel(registrar.messenger(), "cyclops");
    channel.setMethodCallHandler(new CyclopsPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    // Object args = call.arguments();
    // Class argsClass = args != null ? args.getClass() : null;
    // String argsClassname = argsClass != null ? argsClass.toString() : null;
    // String argsStringified = args != null ? args.toString() : null;
    // System.out.println("Calling method: " + call.method);
    // System.out.println("The arguments (" + argsClassname + ") are:" +
    // argsStringified);

    if (call.method.equals("detectInImage")) {
      Map<String, Object> data = this.detectInImage(call);
      result.success(data);
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
