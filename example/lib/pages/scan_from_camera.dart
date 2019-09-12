import 'package:camera/camera.dart'
    show CameraController, CameraDescription, CameraLensDirection, CameraPreview, ResolutionPreset;
import 'package:flutter/material.dart';
import 'package:cyclops_example/scanner_utils.dart' show ScannerUtils;
import 'package:cyclops/cyclops.dart'
    show BarcodeFormat, BarcodeScanner, BarcodeScannerOptions;

class ScanFromCameraPage extends StatefulWidget {
  @override
  _ScanFromCameraPageState createState() => _ScanFromCameraPageState();
}

class _ScanFromCameraPageState extends State<ScanFromCameraPage> {
  CameraController _controller;
  String _barcode;
  TextEditingController _barcodeFieldController = TextEditingController();
  BarcodeScanner _scanner;
  BarcodeScannerOptions _options = BarcodeScannerOptions(barcodeFormats: BarcodeFormat.all);

  @override
  void initState() {
    super.initState();
    this.initCameraController();
    this.initScanner();
  }

  Future initCameraController() async {
    CameraDescription camera = await ScannerUtils.getCamera(CameraLensDirection.back);
    _controller = CameraController(camera, ResolutionPreset.medium);
    await _controller.initialize().then((_) {
      if (!mounted) return;
      setState(() {});

      _controller.startImageStream((image) {
        ScannerUtils.detect(
          image: image,
          detectInImage: _scanner.detectInImage,
          imageRotation: camera.sensorOrientation,
        ).then(
          (result) {
            _handleResult(
              barcode: result,
              imageSize: Size(image.width.toDouble(), image.height.toDouble()),
            );
          },
        );
      });
    });
  }

  Future initScanner() async {
    _scanner = BarcodeScanner(_options);
    await _scanner.initialize();
  }

  void _handleResult({barcode, Size imageSize}) {
    if (barcode != null) {
      if (!_controller.value.isStreamingImages) return;
      setState(() {
        _barcode = barcode.displayValue;
        _barcodeFieldController.text = barcode.displayValue;
      });
    }
  }

  @override
  Widget build(BuildContext context) {
    Size pageSize = MediaQuery.of(context).size;
    bool isControllerInitialized = _controller != null && _controller.value.isInitialized;

    return Container(
      child: Column(
        children: <Widget>[
          isControllerInitialized ? Container() : LinearProgressIndicator(),
          Expanded(
            flex: 3,
            child: Container(
              width: pageSize.width,
              padding: EdgeInsets.all(16.0),
              child: isControllerInitialized ? _buildCameraPreview() : Container(),
            ),
          ),
          Expanded(
            child: Container(
              width: pageSize.width,
              padding: EdgeInsets.all(16.0),
              child: _buildBarcodeField(),
            ),
          ),
        ],
      ),
    );
  }

  _buildCameraPreview() {
    return Container(
      color: Colors.yellow,
      child: AspectRatio(
        aspectRatio: _controller.value.aspectRatio,
        child: CameraPreview(_controller),
      ),
    );
  }

  _buildBarcodeField() {
    String header;
    if (_barcode == null) {
      header = "Couldn't find any barcode yet... keep trying!";
    } else {
      header = "YAY! Found a barcode:";
    }

    return Container(
      child: Column(
        mainAxisAlignment: MainAxisAlignment.center,
        children: <Widget>[
          Text(header, textAlign: TextAlign.center),
          SizedBox(height: 16),
          TextField(
            controller: _barcodeFieldController,
            decoration: InputDecoration(
              labelText: "Barcode",
              border: OutlineInputBorder(borderRadius: BorderRadius.circular(8.0)),
            ),
            readOnly: true,
          ),
        ],
      ),
    );
  }

  @override
  void dispose() {
    _controller?.stopImageStream()?.then((_) {
      _controller.dispose();
    });
    super.dispose();
  }
}
