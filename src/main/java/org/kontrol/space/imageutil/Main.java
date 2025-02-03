package org.kontrol.space.imageutil;

import javax.imageio.ImageIO;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.plugins.jpeg.JPEGImageWriteParam;
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.Locale;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("水印添加工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(500, 300);
        frame.setLayout(new GridLayout(0, 2));

        // 创建组件
        JLabel fileLabel = new JLabel("选择文件：");
        JButton fileButton = new JButton("选择...");
        JLabel fontSizeLabel = new JLabel("字体大小：");
        JTextField fontSizeField = new JTextField("30");
        JLabel fontBoldLabel = new JLabel("字体粗细：");
        JTextField fontBoldField = new JTextField(String.valueOf(Font.BOLD));
        JLabel textSpacingLabel = new JLabel("文本间隔：");
        JTextField textSpacingField = new JTextField("50");
        JLabel qualityLabel = new JLabel("图片质量(0.1-1)：");
        JTextField qualityField = new JTextField("0.9");
        JLabel watermarkTextLabel = new JLabel("水印文本：");
        JTextField watermarkTextField = new JTextField();
        JButton addButton = new JButton("添加水印");
        JButton exitButton = new JButton("退出");

        // 文件选择行为
        JFileChooser fileChooser = new JFileChooser();
        fileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileButton.setText(fileChooser.getSelectedFile().getName());
            }
        });

        // 添加水印行为
        addButton.addActionListener(e -> {
            try {
                File inputFile = fileChooser.getSelectedFile();
                if (inputFile == null || !inputFile.exists()) {
                    JOptionPane.showMessageDialog(frame, "请选择一个有效的输入文件！");
                    return;
                }

                String watermarkText = watermarkTextField.getText();
                if (watermarkText.isEmpty()) {
                    JOptionPane.showMessageDialog(frame, "请输入水印文本！");
                    return;
                }

                int fontSize = Integer.parseInt(fontSizeField.getText());
                int fontBold = Integer.parseInt(fontBoldField.getText());
                int textSpacing = Integer.parseInt(textSpacingField.getText());
                float quality = Float.parseFloat(qualityField.getText());
                String outputPath = generateOutputPath(inputFile.getAbsolutePath());

                ImageWatermarkUtils.addWatermark(inputFile, new File(outputPath), watermarkText, fontSize, fontBold, textSpacing, quality);
                JOptionPane.showMessageDialog(frame, "水印已添加，保存路径：" + outputPath);

            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "错误：" + ex.getMessage());
            }
        });

        // 退出行为
        exitButton.addActionListener(e -> System.exit(0));

        // 添加组件到框架
        frame.add(fileLabel);
        frame.add(fileButton);
        frame.add(fontSizeLabel);
        frame.add(fontSizeField);
        frame.add(fontBoldLabel);
        frame.add(fontBoldField);
        frame.add(textSpacingLabel);
        frame.add(textSpacingField);
        frame.add(qualityLabel);
        frame.add(qualityField);
        frame.add(watermarkTextLabel);
        frame.add(watermarkTextField);
        frame.add(addButton);
        frame.add(exitButton);

        frame.setVisible(true);
    }

    private static String generateOutputPath(String inputPath) {
        int dotIndex = inputPath.lastIndexOf('.');
        return (dotIndex > 0) ?
                inputPath.substring(0, dotIndex) + "_watermarked.jpg" :
                inputPath + "_watermarked.jpg";
    }
}