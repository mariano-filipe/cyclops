package com.example.cyclops;

class DecodedImage {
  public int[] pixels;
  public int width;
  public int height;

  DecodedImage(int[] pixels, int width, int height) {
    this.pixels = pixels;
    this.width = width;
    this.height = height;
  }
}