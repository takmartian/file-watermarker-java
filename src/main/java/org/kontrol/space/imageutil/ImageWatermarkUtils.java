package org.kontrol.space.imageutil;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.Iterator;
import java.util.Locale;

public class ImageWatermarkUtils {

    private ImageWatermarkUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    public static void addWatermark(File inputFile, File outputFile, String watermarkText, int fontSize, int fontBold, int textSpacing, float quality)
            throws IOException, IllegalArgumentException, FontFormatException {

        // 加载字体文件
        Font font;
        try (InputStream fontStream = ImageWatermarkUtils.class.getClassLoader().getResourceAsStream("fonts/dengxian.ttf")) {
            if (fontStream == null) {
                throw new IOException("字体文件未找到");
            }
            font = Font.createFont(Font.TRUETYPE_FONT, fontStream).deriveFont(fontBold, fontSize);
        }

        // 读取图片并处理透明背景
        BufferedImage originalImage = ImageIO.read(inputFile);
        BufferedImage processedImage = handleTransparentBackground(originalImage);

        // 绘制水印
        BufferedImage watermarkedImage = new BufferedImage(
                processedImage.getWidth(),
                processedImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );
        Graphics2D g = watermarkedImage.createGraphics();
        g.drawImage(processedImage, 0, 0, null);

        // 设置水印样式
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f)); // 水印透明度固定
        g.setColor(Color.WHITE);
        g.setFont(font); // 使用加载的字体
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // 计算水印平铺参数
        FontMetrics metrics = g.getFontMetrics();
        int textWidth = metrics.stringWidth(watermarkText);
        int textHeight = metrics.getHeight();

        // 旋转画布并平铺水印
        g.rotate(Math.toRadians(-45), watermarkedImage.getWidth() / 2.0, watermarkedImage.getHeight() / 2.0);
        for (int x = -watermarkedImage.getWidth(); x < watermarkedImage.getWidth() * 2; x += textWidth + textSpacing) {
            for (int y = -watermarkedImage.getHeight(); y < watermarkedImage.getHeight() * 2; y += textHeight + textSpacing) {
                g.drawString(watermarkText, x, y);
            }
        }
        g.dispose();

        // 存储图片并调整质量
        try (OutputStream os = new FileOutputStream(outputFile)) {
            Iterator<ImageWriter> writers = ImageIO.getImageWritersByFormatName("jpg");
            ImageWriter writer = writers.next();

            JPEGImageWriteParam jpegParams = new JPEGImageWriteParam(Locale.getDefault());
            jpegParams.setCompressionMode(ImageWriteParam.MODE_EXPLICIT);
            jpegParams.setCompressionQuality(quality);

            writer.setOutput(ImageIO.createImageOutputStream(os));
            writer.write(null, new javax.imageio.IIOImage(watermarkedImage, null, null), jpegParams);
        }
    }

    // 处理透明背景（填充为白色）
    private static BufferedImage handleTransparentBackground(BufferedImage originalImage) {
        if (originalImage.getTransparency() == Transparency.OPAQUE) {
            return originalImage;
        }

        BufferedImage rgbImage = new BufferedImage(
                originalImage.getWidth(),
                originalImage.getHeight(),
                BufferedImage.TYPE_INT_RGB
        );

        Graphics2D g = rgbImage.createGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, rgbImage.getWidth(), rgbImage.getHeight());
        g.drawImage(originalImage, 0, 0, null);
        g.dispose();

        return rgbImage;
    }
}