part of zebra_scanner;

class ZebraScanner {
  static const MethodChannel _channel = const MethodChannel('zebra_scanner');
  final ZebraScannerOptions options;
  bool isInitialized = false;

  ZebraScanner(this.options);

  Future<void> initialize() async {
    await _channel.invokeMethod('initialize', options.serialize());
    isInitialized = true;
  }

  Future<Barcode> detectInImage(ZebraScannerVisionImage image) async {
    assert(isInitialized, "the scanner was instantiated but not initialized");
    // print("image.serialize: ${image.serialize()}");

    final Map<String, dynamic> reply = await _channel.invokeMapMethod('detectInImage', image.serialize());
    print("reply: $reply");
    return reply != null ? Barcode.fromMap(reply) : null;
  }

  Future<void> dispose() async {
    await _channel.invokeMethod('close');
    isInitialized = false;
  }
}

class ZebraScannerOptions {
  final barcodeFormats;

  ZebraScannerOptions({this.barcodeFormats = BarcodeFormat.all});

  Map<String, dynamic> serialize() => <String, dynamic>{
        'barcodeFormats': barcodeFormats.value,
      };
}
