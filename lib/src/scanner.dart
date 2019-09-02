part of zebra_scanner;

class ZebraScanner {
  static const MethodChannel _channel = const MethodChannel('zebra_scanner');
  final ZebraScannerOptions options;
  bool _isInitialized = false;

  ZebraScanner(this.options);

  Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  Future<void> initialize() async {
    await _channel.invokeMethod('initialize', options.serialize());
    _isInitialized = true;
  }

  Future<Barcode> detectInImage(ZebraScannerVisionImage image) async {
    assert(_isInitialized, "the scanner was instantiated but not initialized");
    // print("image.serialize: ${image.serialize()}");

    final Map<String, dynamic> reply = await _channel.invokeMapMethod('detectInImage', image.serialize());
    print("reply: $reply");
    return reply != null ? Barcode.fromMap(reply) : null;
  }

  Future<void> close() async {
    await _channel.invokeMethod('close');
    _isInitialized = false;
  }
}

class ZebraScannerOptions {
  final barcodeFormats;

  ZebraScannerOptions({this.barcodeFormats = BarcodeFormat.all});

  Map<String, dynamic> serialize() => <String, dynamic>{
        'barcodeFormats': barcodeFormats.value,
      };
}
