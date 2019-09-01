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
    int val = this.foreignValue & foreignFormats;
    System.out.println("val: " + val);
    return val != 0;
  }

  public static ArrayList<BarcodeFormat> enumerate() {
    ArrayList<BarcodeFormat> formats = new ArrayList<BarcodeFormat>();

    // Yet available formats provided by Zxing: [MAXICODE, RSS_14, RSS_EXPANDED,
    // UPC_EAN_EXTENSION]
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.AZTEC, 0x1000));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.CODABAR, 0x8));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.CODE_39, 0x2));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.CODE_93, 0x4));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.AZTEC, 0x1000));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.CODE_128, 0x1));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.DATA_MATRIX, 0x10));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.EAN_8, 0x40));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.EAN_13, 0x20));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.ITF, 0x80));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.PDF_417, 0x800));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.QR_CODE, 0x100));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.UPC_A, 0x200));
    formats.add(new BarcodeFormat(com.google.zxing.BarcodeFormat.UPC_E, 0x400));

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