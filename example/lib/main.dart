import 'dart:io' show File;

import 'package:camera/camera.dart'
    show CameraController, CameraDescription, CameraPreview, ResolutionPreset, availableCameras;
import 'package:flutter/material.dart';
import 'package:image_picker/image_picker.dart' show ImagePicker, ImageSource;

import 'package:zebra_scanner/zebra_scanner.dart'
    show ZebraScanner, ZebraScannerOptions, ZebraScannerVisionImage, BarcodeFormat;
import 'package:zebra_scanner_example/scanner_utils.dart' show ScannerUtils;

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  File _image;
  ZebraScanner _scanner;
  Future _isScannerInitialized;
  CameraController _cameraController;
  int _sensorOrientation;
  String _barcode;

  @override
  void initState() {
    super.initState();

    var options = ZebraScannerOptions(
      barcodeFormats: BarcodeFormat.all,
    );
    _scanner = ZebraScanner(options);
    _isScannerInitialized = _scanner.initialize();

    initCameraApp();
  }

  Future<void> initCameraApp() async {
    List<CameraDescription> cameras = await availableCameras();
    setState(() {
      _sensorOrientation = cameras[0].sensorOrientation;
      _cameraController = CameraController(cameras[0], ResolutionPreset.medium);
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Container(
          child: Column(
            children: <Widget>[
              _barcode == null
                  ? Text("Searching for barcodes...")
                  : Text("Found barcode: $_barcode"),
              _image == null
                  ? Text("Waiting for an image from gallery...")
                  : Container(width: 200, child: Image.file(_image)),
              _cameraController != null
                  ? CameraApp(controller: _cameraController)
                  : CircularProgressIndicator(),
            ],
          ),
        ),
        floatingActionButton: Column(
          mainAxisSize: MainAxisSize.min,
          children: <Widget>[
            FloatingActionButton(
              onPressed: () => _onAddImageButtonPressed(),
              child: Icon(Icons.add_photo_alternate),
            ),
            FloatingActionButton(
              onPressed: () => _onScanImageButtonPressed(),
              child: Icon(Icons.scanner),
            ),
            FloatingActionButton(
              onPressed: () => _onStartImageStreamButtonPressed(),
              child: Icon(Icons.view_stream),
            ),
          ],
        ),
      ),
    );
  }

  _onAddImageButtonPressed() async {
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);
    setState(() {
      _image = image;
    });
  }

  _onScanImageButtonPressed() async {
    await _isScannerInitialized;
    var visionImage = ZebraScannerVisionImage.fromFile(_image);
    await _scanner.detectInImage(visionImage);
  }

  _onStartImageStreamButtonPressed() {
    assert(_cameraController.value.isInitialized, "camera controller is not initialized");
    _cameraController.startImageStream((image) {
      print("Now streaming images");

      ScannerUtils.detect(
        image: image,
        detectInImage: _scanner.detectInImage,
        imageRotation: _sensorOrientation,
      ).then(
        (dynamic result) {
          _handleResult(
            barcodes: result,
            imageSize: Size(image.width.toDouble(), image.height.toDouble()),
          );
        },
      );
    });
  }

  void _handleResult({barcodes, Size imageSize}) {
    if (barcodes != null) {
      if (!_cameraController.value.isStreamingImages) return;
      setState(() {
        _barcode = barcodes;
      });
      _cameraController.stopImageStream();
    }
  }
}

class CameraApp extends StatefulWidget {
  final CameraController controller;

  const CameraApp({Key key, @required this.controller}) : super(key: key);

  @override
  _CameraAppState createState() => _CameraAppState();
}

class _CameraAppState extends State<CameraApp> {
  Future<void> initializeController() async {
    widget.controller.initialize().then((_) {
      if (!mounted) {
        return;
      }
      setState(() {});
    });
  }

  @override
  void initState() {
    super.initState();
    initializeController();
  }

  @override
  void dispose() {
    widget.controller?.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    if (widget.controller == null || !widget.controller.value.isInitialized) {
      return Container();
    }
    return AspectRatio(
      aspectRatio: widget.controller.value.aspectRatio,
      child: CameraPreview(widget.controller),
    );
  }
}
