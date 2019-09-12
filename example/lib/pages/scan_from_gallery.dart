import 'dart:io' show File;

import 'package:flutter/material.dart';

import 'package:image_picker/image_picker.dart' show ImagePicker, ImageSource;
import 'package:cyclops/cyclops.dart'
    show Barcode, BarcodeFormat, BarcodeScanner, BarcodeScannerOptions, CyclopsVisionImage;

class ScanFromGalleryPage extends StatefulWidget {
  @override
  _ScanFromGalleryPageState createState() => _ScanFromGalleryPageState();
}

class _ScanFromGalleryPageState extends State<ScanFromGalleryPage> {
  File _image;
  Barcode _barcode;
  TextEditingController _barcodeFieldController = TextEditingController();
  Future _initializeScanner;
  BarcodeScanner _scanner;
  BarcodeScannerOptions _options = BarcodeScannerOptions(barcodeFormats: BarcodeFormat.all);

  @override
  void didChangeDependencies() {
    _scanner = BarcodeScanner(_options);
    _initializeScanner = _scanner.initialize();

    super.didChangeDependencies();
  }

  @override
  Widget build(BuildContext context) {
    Size pageSize = MediaQuery.of(context).size;

    return Container(
      child: Column(
        children: <Widget>[
          Expanded(
            flex: 3,
            child: Container(
              width: pageSize.width,
              padding: EdgeInsets.all(16.0),
              child: _buildGalleryTouchable(),
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

  _buildGalleryTouchable() {
    return InkWell(
      onTap: _onSelectPhotoFromGallery,
      child: _image != null
          ? Image.file(_image)
          : Container(
              decoration: BoxDecoration(
                color: Colors.grey[100],
                border: Border.all(
                  width: 1,
                  style: BorderStyle.solid,
                ),
              ),
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: <Widget>[
                  Icon(Icons.add_a_photo, size: 50),
                  SizedBox(height: 16.0),
                  Text("Tap to select a photo from the gallery"),
                ],
              ),
            ),
    );
  }

  void _onSelectPhotoFromGallery() async {
    _barcodeFieldController.text = "";
    var image = await ImagePicker.pickImage(source: ImageSource.gallery);
    setState(() {
      _image = image;
    });
    _detectBarcodeInImage(_image);
  }

  void _detectBarcodeInImage(File image) async {
    await _initializeScanner;
    _scanner.detectInImage(CyclopsVisionImage.fromFile(image)).then((barcode) {
      setState(() {
        _barcode = barcode;
      });
      _barcodeFieldController.text = barcode?.displayValue;
    });
  }

  _buildBarcodeField() {
    String header;
    if (_image == null) {
      header = "Select an image to start scanning.";
    } else if (_barcode == null) {
      header = "Couldn't find any barcode in the image. Try again with another one.";
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
    _scanner.dispose();
    super.dispose();
  }
}
