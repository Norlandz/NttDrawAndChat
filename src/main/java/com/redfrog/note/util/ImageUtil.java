package com.redfrog.note.util;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;

import javafx.embed.swing.SwingFXUtils;
import javafx.scene.image.Image;
import javafx.scene.image.WritableImage;

public class ImageUtil {

  public static byte[] fximg2byte(javafx.scene.image.Image img) {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      ImageIO.write(SwingFXUtils.fromFXImage(img, null), "png", byteArrayOutputStream);
    } catch (IOException e) {
      throw new Error(e);
    }
    byte[] image_byteArr = byteArrayOutputStream.toByteArray();
    return image_byteArr;
  }

  public static javafx.scene.image.WritableImage byte2fximg(byte[] image_byteArr) {
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(image_byteArr);
    BufferedImage bufferedImage = null;
    try {
      bufferedImage = ImageIO.read(byteArrayInputStream);
    } catch (IOException e) {
      throw new Error(e);
    }
    WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);
    return image;
  }

}
