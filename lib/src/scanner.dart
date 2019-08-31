part of zebra_scanner;

class ZebraScanner {
  static const MethodChannel _channel = const MethodChannel('zebra_scanner');

  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion', [
      'xxx',
    ]);
    return version;
  }

  Future<void> detectInImage(ZebraScannerVisionImage image) async {
    print("image.serialize: ${image.serialize()}");

    final List<dynamic> reply = await _channel.invokeListMethod('detectInImage', image.serialize());
    print("reply: $reply");
    return null;
  }
}
