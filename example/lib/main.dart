import 'dart:io' show File;

import 'package:flutter/material.dart';
import 'package:flutter/services.dart' show PlatformException;
import 'package:image_picker/image_picker.dart' show ImagePicker, ImageSource;

import 'package:zebra_scanner/zebra_scanner.dart' show ZebraScanner, ZebraScannerVisionImage;

void main() => runApp(MyApp());

class MyApp extends StatefulWidget {
  @override
  _MyAppState createState() => _MyAppState();
}

class _MyAppState extends State<MyApp> {
  String _platformVersion = 'Unknown';
  File _image;
  final _scanner = ZebraScanner();

  @override
  void initState() {
    super.initState();
    initPlatformState();
  }

  // Platform messages are asynchronous, so we initialize in an async method.
  Future<void> initPlatformState() async {
    String platformVersion;

    // Platform messages may fail, so we use a try/catch PlatformException.
    try {
      platformVersion = await _scanner.platformVersion;
    } on PlatformException {
      platformVersion = 'Failed to get platform version.';
    }

    // If the widget was removed from the tree while the asynchronous platform
    // message was in flight, we want to discard the reply rather than calling
    // setState to update our non-existent appearance.
    if (!mounted) return;

    setState(() {
      _platformVersion = platformVersion;
    });
  }

  @override
  Widget build(BuildContext context) {
    return MaterialApp(
      home: Scaffold(
        appBar: AppBar(
          title: const Text('Plugin example app'),
        ),
        body: Column(
          children: <Widget>[
            Text('Running on: $_platformVersion\n'),
            _image == null ? Text("Loading image...") : Container(width: 200, child: Image.file(_image)),
          ],
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
            )
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
    var visionImage = ZebraScannerVisionImage.fromFile(_image);
    await _scanner.detectInImage(visionImage);
  }
}
