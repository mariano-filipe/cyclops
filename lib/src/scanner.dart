part of cyclops;

class BarcodeScanner {
  static const MethodChannel _channel = const MethodChannel('cyclops');
  final BarcodeScannerOptions options;
  bool isInitialized = false;

  BarcodeScanner(this.options);

  Future<void> initialize() async {
    await _channel.invokeMethod('initialize', options.serialize());
    isInitialized = true;
  }

  Future<Barcode> detectInImage(CyclopsVisionImage image) async {
    assert(isInitialized, "the scanner was instantiated but not initialized");

    final Map<String, dynamic> reply = await _channel.invokeMapMethod('detectInImage', image.serialize());
    return reply != null ? Barcode.fromMap(reply) : null;
  }

  Future<void> dispose() async {
    await _channel.invokeMethod('close');
    isInitialized = false;
  }
}

class BarcodeScannerOptions {
  final barcodeFormats;

  BarcodeScannerOptions({this.barcodeFormats = BarcodeFormat.all});

  Map<String, dynamic> serialize() => <String, dynamic>{
        'barcodeFormats': barcodeFormats.value,
      };
}
