part of zebra_scanner;

class Barcode {
  final String displayValue;
  final String rawValue;
  final BarcodeFormat format;
  final List<Offset> points;

  Barcode._({
    @required rawValue,
    displayValue,
    @required format,
    points,
  })  : this.rawValue = rawValue,
        this.displayValue = displayValue,
        this.format = format,
        this.points = points;

  factory Barcode.fromMap(data) {
    return Barcode._(
      rawValue: data['rawValue'],
      displayValue: data['rawValue'],
      format: BarcodeFormat._(data['format']),
      points: data['points'].map((point) => Offset(point[0], point[1])).toList().cast<Offset>(),
    );
  }
}

class BarcodeFormat {
  const BarcodeFormat._(this.value);

  /// Barcode format constant representing the union of all supported formats.
  static const BarcodeFormat all = BarcodeFormat._(0xFFFF);

  /// Barcode format unknown to the current SDK.
  static const BarcodeFormat unknown = BarcodeFormat._(0);

  /// Barcode format constant for Code 128.
  static const BarcodeFormat code128 = BarcodeFormat._(0x0001);

  /// Barcode format constant for Code 39.
  static const BarcodeFormat code39 = BarcodeFormat._(0x0002);

  /// Barcode format constant for Code 93.
  static const BarcodeFormat code93 = BarcodeFormat._(0x0004);

  /// Barcode format constant for CodaBar.
  static const BarcodeFormat codabar = BarcodeFormat._(0x0008);

  /// Barcode format constant for Data Matrix.
  static const BarcodeFormat dataMatrix = BarcodeFormat._(0x0010);

  /// Barcode format constant for EAN-13.
  static const BarcodeFormat ean13 = BarcodeFormat._(0x0020);

  /// Barcode format constant for EAN-8.
  static const BarcodeFormat ean8 = BarcodeFormat._(0x0040);

  /// Barcode format constant for ITF (Interleaved Two-of-Five).
  static const BarcodeFormat itf = BarcodeFormat._(0x0080);

  /// Barcode format constant for QR Code.
  static const BarcodeFormat qrCode = BarcodeFormat._(0x0100);

  /// Barcode format constant for UPC-A.
  static const BarcodeFormat upca = BarcodeFormat._(0x0200);

  /// Barcode format constant for UPC-E.
  static const BarcodeFormat upce = BarcodeFormat._(0x0400);

  /// Barcode format constant for PDF-417.
  static const BarcodeFormat pdf417 = BarcodeFormat._(0x0800);

  /// Barcode format constant for AZTEC.
  static const BarcodeFormat aztec = BarcodeFormat._(0x1000);

  /// Raw BarcodeFormat value.
  final int value;

  BarcodeFormat operator |(BarcodeFormat other) => BarcodeFormat._(value | other.value);
}
