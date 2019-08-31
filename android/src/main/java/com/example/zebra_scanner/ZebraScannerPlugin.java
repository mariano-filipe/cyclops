package com.example.zebra_scanner;

import java.util.Arrays;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

import com.google.zxing.MultiFormatReader;

/** ZebraScannerPlugin */
public class ZebraScannerPlugin implements MethodCallHandler {
  private MultiFormatReader detector = new MultiFormatReader();

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "zebra_scanner");
    channel.setMethodCallHandler(new ZebraScannerPlugin());
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    System.out.println("The arguments are:" + call.arguments());
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("detectInImage")) {
      result.success(Arrays.asList("289038347893", "3232788932", "12128098309"));
    } else {
      result.notImplemented();
    }
  }
}
