import 'package:flutter/material.dart';

import 'package:cyclops_example/pages/scan_from_camera.dart' show ScanFromCameraPage;
import 'package:cyclops_example/pages/scan_from_gallery.dart' show ScanFromGalleryPage;

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage> {
  @override
  Widget build(BuildContext context) {
    return DefaultTabController(
      length: 2,
      child: Scaffold(
        appBar: AppBar(
          title: Text("Cyclops Example"),
          bottom: TabBar(
            tabs: <Widget>[
              Tab(icon: Icon(Icons.photo_library)),
              Tab(icon: Icon(Icons.photo_camera)),
            ],
          ),
        ),
        body: TabBarView(
          children: <Widget>[
            ScanFromGalleryPage(),
            ScanFromCameraPage(),
          ],
        ),
      ),
    );
  }
}
