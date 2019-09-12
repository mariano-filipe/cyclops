part of cyclops;

enum _ImageType { file, bytes }

enum ImageRotation { rotation0, rotation90, rotation180, rotation270 }

class CyclopsVisionImage {
  CyclopsVisionImage._({
    @required _ImageType type,
    CyclopsVisionImageMetadata metadata,
    File imageFile,
    Uint8List bytes,
  })  : _imageFile = imageFile,
        _metadata = metadata,
        _bytes = bytes,
        _type = type;

  factory CyclopsVisionImage.fromFile(File imageFile) {
    assert(imageFile != null);
    return CyclopsVisionImage._(
      type: _ImageType.file,
      imageFile: imageFile,
    );
  }

  factory CyclopsVisionImage.fromFilePath(String imagePath) {
    assert(imagePath != null);
    return CyclopsVisionImage._(
      type: _ImageType.file,
      imageFile: File(imagePath),
    );
  }

  factory CyclopsVisionImage.fromBytes(
    Uint8List bytes,
    CyclopsVisionImageMetadata metadata,
  ) {
    assert(bytes != null);
    assert(metadata != null);
    return CyclopsVisionImage._(
      type: _ImageType.bytes,
      bytes: bytes,
      metadata: metadata,
    );
  }

  final Uint8List _bytes;
  final File _imageFile;
  final CyclopsVisionImageMetadata _metadata;
  final _ImageType _type;

  Map<String, dynamic> serialize() => <String, dynamic>{
        'type': _enumToString(_type),
        'bytes': _bytes,
        'path': _imageFile?.path,
        'metadata': _type == _ImageType.bytes ? _metadata._serialize() : null,
      };
}

class CyclopsVisionImageMetadata {
  CyclopsVisionImageMetadata({
    @required this.size,
    @required this.rawFormat,
    @required this.planeData,
    this.rotation = ImageRotation.rotation0,
  })  : assert(size != null),
        assert(defaultTargetPlatform == TargetPlatform.iOS ? rawFormat != null : true),
        assert(defaultTargetPlatform == TargetPlatform.iOS ? planeData != null : true),
        assert(defaultTargetPlatform == TargetPlatform.iOS ? planeData.isNotEmpty : true);

  final Size size;

  final ImageRotation rotation;

  final dynamic rawFormat;

  final List<CyclopsVisionImagePlaneMetadata> planeData;

  int _imageRotationToInt(ImageRotation rotation) {
    switch (rotation) {
      case ImageRotation.rotation90:
        return 90;
      case ImageRotation.rotation180:
        return 180;
      case ImageRotation.rotation270:
        return 270;
      default:
        assert(rotation == ImageRotation.rotation0);
        return 0;
    }
  }

  Map<String, dynamic> _serialize() => <String, dynamic>{
        'width': size.width,
        'height': size.height,
        'rotation': _imageRotationToInt(rotation),
        'rawFormat': rawFormat,
        'planeData': planeData
            .map((CyclopsVisionImagePlaneMetadata plane) => plane._serialize())
            .toList(),
      };
}

class CyclopsVisionImagePlaneMetadata {
  CyclopsVisionImagePlaneMetadata({
    @required this.bytesPerRow,
    this.bytesPerPixel,
    @required this.height,
    @required this.width,
  })  : assert(defaultTargetPlatform == TargetPlatform.iOS ? bytesPerRow != null : true),
        assert(defaultTargetPlatform == TargetPlatform.android ? bytesPerPixel != null : true),
        assert(defaultTargetPlatform == TargetPlatform.iOS ? height != null : true),
        assert(defaultTargetPlatform == TargetPlatform.iOS ? width != null : true);

  /// The row stride for this color plane, in bytes.
  final int bytesPerRow;

  /// The pixel stride for this color plane, in bytes.
  final int bytesPerPixel;

  /// Height of the pixel buffer on iOS.
  final int height;

  /// Width of the pixel buffer on iOS.
  final int width;

  Map<String, dynamic> _serialize() => <String, dynamic>{
        'bytesPerRow': bytesPerRow,
        'bytesPerPixel': bytesPerPixel,
        'height': height,
        'width': width,
      };
}

String _enumToString(dynamic enumValue) {
  final String enumString = enumValue.toString();
  return enumString.substring(enumString.indexOf('.') + 1);
}
