package org.kontrol.space.graphyutil;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class ImageSteganographyUtils {

    private ImageSteganographyUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    public static void encodeText(BufferedImage image, String text) {
        int width = image.getWidth();
        int height = image.getHeight();
        text += '\0'; // 终止符号

        int textIndex = 0;
        int charValue = text.charAt(textIndex);
        int bitIndex = 0;

        outerLoop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (textIndex < text.length()) {
                    int rgb = image.getRGB(x, y);

                    int red = (rgb >> 16) & 0xff;
                    int green = (rgb >> 8) & 0xff;
                    int blue = rgb & 0xff;

                    // 修改蓝色通道的最低有效位
                    if (bitIndex < 8) {
                        blue = (blue & 0xFE) | ((charValue >> (7 - bitIndex)) & 1);
                        bitIndex++;
                    }

                    if (bitIndex == 8) {
                        bitIndex = 0;
                        textIndex++;
                        if (textIndex < text.length()) {
                            charValue = text.charAt(textIndex);
                        }
                    }

                    int newRgb = (red << 16) | (green << 8) | blue;
                    image.setRGB(x, y, newRgb);
                } else {
                    break outerLoop;
                }
            }
        }
    }

    public static String decodeText(BufferedImage image) {
        int width = image.getWidth();
        int height = image.getHeight();

        StringBuilder decodedText = new StringBuilder();
        int charValue = 0;
        int bitIndex = 0;

        outerLoop:
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (bitIndex < 8) {
                    int rgb = image.getRGB(x, y);
                    int blue = rgb & 0xff;
                    charValue = (charValue << 1) | (blue & 1);
                    bitIndex++;

                    if (bitIndex == 8) {
                        if (charValue == '\0') {
                            break outerLoop;
                        }
                        decodedText.append((char) charValue);
                        bitIndex = 0;
                        charValue = 0;
                    }
                }
            }
        }

        return decodedText.toString();
    }

    public static BufferedImage loadImage(File file) throws IOException {
        return ImageIO.read(file);
    }

    public static void saveImage(BufferedImage image, File file) throws IOException {
        ImageIO.write(image, "png", file); // 使用PNG格式以防止压缩损失
    }
}