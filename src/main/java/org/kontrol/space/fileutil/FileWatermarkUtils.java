package org.kontrol.space.fileutil;

import java.io.*;
import java.util.UUID;

public class FileWatermarkUtils {

    private static final String WATERMARK_IDENTIFIER = "WATERMARK_START";

    private FileWatermarkUtils() {
        throw new UnsupportedOperationException("工具类禁止实例化");
    }

    public static void addWatermark(File inputFile, File outputFile, String watermarkText, String secretKey) throws Exception {
        if (watermarkText == null) {
            watermarkText = UUID.randomUUID().toString();
        }

        // 加密水印内容
        String encryptedWatermark = EncryptionUtils.encrypt(watermarkText, secretKey);

        try (InputStream is = new FileInputStream(inputFile);
             OutputStream os = new FileOutputStream(outputFile)) {
            // 复制文件内容
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) != -1) {
                os.write(buffer, 0, length);
            }

            // 添加水印
            os.write(WATERMARK_IDENTIFIER.getBytes());
            os.write("\n".getBytes());
            os.write(encryptedWatermark.getBytes());
        }
    }

    public static String checkWatermark(File file, String secretKey) throws Exception {
        try (RandomAccessFile raf = new RandomAccessFile(file, "r")) {
            long fileLength = raf.length();
            long position = fileLength - 1024;
            position = Math.max(position, 0);
            raf.seek(position);

            byte[] buffer = new byte[1024];
            int length = raf.read(buffer);
            String content = new String(buffer, 0, length);

            int index = content.lastIndexOf(WATERMARK_IDENTIFIER);
            if (index != -1) {
                String encryptedWatermark = content.substring(index + WATERMARK_IDENTIFIER.length()).trim();
                return EncryptionUtils.decrypt(encryptedWatermark, secretKey);
            } else {
                return "此文件没有添加数字水印。";
            }
        }
    }
}