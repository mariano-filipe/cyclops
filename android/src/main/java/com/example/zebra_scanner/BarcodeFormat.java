package com.example.zebra_scanner;

import java.util.ArrayList;
import java.util.List;

class BarcodeFormat {
  public com.google.zxing.BarcodeFormat value;
  private int foreignValue;

  BarcodeFormat(com.google.zxing.BarcodeFormat value, int foreignValue) {
    this.value = value;
    this.foreignValue = foreignValue;
  }

  public boolean in(int foreignFormats) {
    if (foreignFormats == 0 && this.foreignValue == 0) return true;
    return (this.foreignValue & foreignFormats) != 0;
  }

  public static int mapToInt(com.google.zxing.BarcodeFormat format) {
    switch (format) {
      case AZTEC: return 0x1000;
      case CODABAR: return 0x8;
      case CODE_39: return 0x2;
      case CODE_93: return 0x4;
      case CODE_128: return 0x1;
      case DATA_MATRIX: return 0x10;
      case EAN_8: return 0x40;
      case EAN_13: return 0x20;
      case ITF: return 0x80;
      case PDF_417: return 0x800;
      case QR_CODE: return 0x100;
      case UPC_A: return 0x200;
      case UPC_E: return 0x400;
      default: return 0x0;
    }
  }

  public static ArrayList<BarcodeFormat> enumerate() {
    ArrayList<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

    // Yet available formats provided by Zxing: [MAXICODE, RSS_14, RSS_EXPANDED,
    // UPC_EAN_EXTENSION]
    for (com.google.zxing.BarcodeFormat format : com.google.zxing.BarcodeFormat.values()) {
      formats.add(new BarcodeFormat(format, BarcodeFormat.mapToInt(format)));
    }

    return formats;
  }

  public static ArrayList<com.google.zxing.BarcodeFormat> enumerateFromInt(int allowedFormats) {
    ArrayList<com.google.zxing.BarcodeFormat> formats = new ArrayList<com.google.zxing.BarcodeFormat>();
    for (BarcodeFormat barcodeFormat : BarcodeFormat.enumerate()) {
      if (barcodeFormat.in(allowedFormats)) {
        formats.add(barcodeFormat.value);
      }
    }
    return formats;
  }

  @Override
  public String toString() {
    return this.value.name();
  }
}