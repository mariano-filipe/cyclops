package com.example.zebra_scanner;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.Map;

import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.DecodeHintType;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;

/** ZebraScannerPlugin */
public class ZebraScannerPlugin implements MethodCallHandler {
  private MultiFormatReader detector = new MultiFormatReader();
  private com.google.zxing.Result decodingResult;

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "zebra_scanner");
    channel.setMethodCallHandler(new ZebraScannerPlugin());
  }

  public void initialize(MethodCall call) {
    Map options = call.argument("options");
    String optionsStringified = options != null ? options.toString() : null;
    // System.out.println("options: " + optionsStringified);

    Hashtable<DecodeHintType, Object> hints = new Hashtable<DecodeHintType, Object>(1);
    hints.put(DecodeHintType.CHARACTER_SET, "UTF8");
    detector.setHints(hints);
  }

  // Resets any internal state the implementation has after a decode, to prepare
  // it for reuse.
  public void close(MethodCall call) {
    this.detector.reset();
  }

  public void detectInImage(MethodCall call) {
    String imgPath = call.argument("path");
    // System.out.println("imgPath: " + imgPath);
    Bitmap bitmap = BitmapFactory.decodeFile(imgPath);
    // System.out.println("bitmap: " + bitmap.toString());
    int width = bitmap.getWidth();
    // System.out.println("width: " + Integer.toString(width));
    int height = bitmap.getHeight();
    // System.out.println("height: " + Integer.toString(height));
    int[] pixels = new int[width * height];
    bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
    // System.out.println("pixels.length: " + Integer.toString(pixels.length));
    // System.out.println("pixels[100]: " + Integer.toString(pixels[100]));
    // System.out.println("pixels: " + pixels.toString());

    RGBLuminanceSource luminanceSource = new RGBLuminanceSource(width, height, pixels);
    // System.out.println("luminanceSource:" + luminanceSource.toString());
    HybridBinarizer binarizer = new HybridBinarizer(luminanceSource);
    // System.out.println("binarizer:" + binarizer.toString());
    BinaryBitmap binaryBitmap = new BinaryBitmap(binarizer);
    // System.out.println("binaryBitmap:" + binaryBitmap.toString());
    // System.out.println("detector:" + detector.toString());
    try {
      this.decodingResult = detector.decodeWithState(binaryBitmap);
    } catch (NotFoundException e) {
      this.decodingResult = null;
    }
    System.out.println("decodingResult:" + (decodingResult != null ? decodingResult.toString() : null));
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    Object args = call.arguments();
    Class argsClass = args != null ? args.getClass() : null;
    String argsClassname = argsClass != null ? argsClass.toString() : null;
    String argsStringified = args != null ? args.toString() : null;
    System.out.println("Calling method: " + call.method);
    System.out.println("The arguments (" + argsClassname + ") are:" + argsStringified);

    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("detectInImage")) {
      this.detectInImage(call);
      String ret = this.decodingResult != null ? this.decodingResult.getText() : null;
      result.success(Arrays.asList(ret));
    } else if (call.method.equals("initialize")) {
      this.initialize(call);
      result.success("foo");
    } else if (call.method.equals("close")) {
      this.close(call);
      result.success("bar");
    } else {
      result.notImplemented();
    }
  }
}
