/// This library can be used to scan barcodes in images coming either from the gallery or the camera
library cyclops;

import 'dart:io' show File;
import 'dart:typed_data' show Uint8List;
import 'dart:ui' show Offset, Size;

import 'package:flutter/foundation.dart' show TargetPlatform, defaultTargetPlatform, required;
import 'package:flutter/services.dart' show MethodChannel;

part 'src/scanner.dart';
part 'src/image.dart';
part 'src/barcode.dart';
