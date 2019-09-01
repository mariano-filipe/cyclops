package com.example.zebra_scanner;

import android.graphics.Color;
import android.graphics.ImageFormat;

import java.util.ArrayList;
import java.util.Map;

class Decoder {
  protected DecodedImage decode(byte[] bytes, Map<String, Object> metadata) {
    int format = (int) metadata.get("rawFormat");
    if (format == ImageFormat.YUV_420_888) {
      return this.decodeYUV_420_88(bytes, metadata);
    }
    return null;
  }

  private DecodedImage decodeYUV_420_88(byte[] imgBytes, Map<String, Object> imgMetadata) {
    // Image info
    int width = (int) Math.round((double) imgMetadata.get("width"));
    int height = (int) Math.round((double) imgMetadata.get("height"));
    ArrayList imgPlanesMetadata = (ArrayList) imgMetadata.get("planeData");

    // Y Plane (0)
    Map yPlaneMetadata = (Map) imgPlanesMetadata.get(0);
    int bytesPerPixel = (int) yPlaneMetadata.get("bytesPerPixel");
    int bytesPerRow = (int) yPlaneMetadata.get("bytesPerRow");
    int rowPadding = bytesPerRow - bytesPerPixel * width; // in bytes
    int rowStride = width + rowPadding / bytesPerPixel; // in pixels
    int yPlaneExtent = rowStride * height;

    /*
     * [FIX] Use all image planes instead of only the Y one. For now, we're only
     * using data of the Y plane which should be enough for the purpose of barcode
     * scanning. There are some decoding functions I found that seem to do a
     * reasonable conversion from YUV to RGB, but they are usually slower than the
     * current approach and result in some weird clip effects:
     * http://www.41post.com/3470/programming/android-retrieving-the-camera-preview-as-a-pixel-array
     * https://stackoverflow.com/questions/12469730/confusion-on-yuv-nv21-conversion-to-rgb
     * https://github.com/flutter/flutter/issues/26348#issuecomment-461692876
     */
    /*
     * [FIX] For some reason the bytes of YUV planes are not entirely given. Maybe
     * related to: https://github.com/flutter/flutter/issues/27686
     */
    int[] argbImg = new int[yPlaneExtent];
    for (int i = 0; i < yPlaneExtent; i++) {
      // transform byte value to unsigned int with value between [0, 255]
      int y = (0xff & ((int) imgBytes[i]));
      // assign each byte a grayscale value
      argbImg[i] = Color.rgb(y, y, y);
    }

    return new DecodedImage(argbImg, rowStride, height);
  }
}