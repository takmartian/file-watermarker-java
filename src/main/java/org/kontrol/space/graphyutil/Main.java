package org.kontrol.space.graphyutil;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("图片隐写术工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new GridLayout(0, 2));

        // 创建组件
        JLabel fileLabel = new JLabel("选择图片文件：");
        JButton fileButton = new JButton("选择...");
        JLabel encodeLabel = new JLabel("隐写内容：");
        JTextField encodeField = new JTextField();
        JButton encodeButton = new JButton("添加隐写内容");
        JButton decodeButton = new JButton("提取隐写内容");

        JFileChooser fileChooser = new JFileChooser();

        // 文件选择行为
        fileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileButton.setText(fileChooser.getSelectedFile().getName());
            }
        });

        // 添加隐写内容行为
        encodeButton.addActionListener(e -> {
            File inputFile = fileChooser.getSelectedFile();
            if (inputFile == null || !inputFile.exists()) {
                JOptionPane.showMessageDialog(frame, "请选择一个有效的图像文件！");
                return;
            }

            String text = encodeField.getText();
            if (text.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入要隐藏的文本！");
                return;
            }

            try {
                BufferedImage image = ImageSteganographyUtils.loadImage(inputFile);
                ImageSteganographyUtils.encodeText(image, text);
                File outputFile = new File(inputFile.getParent(), "steganographed_" + inputFile.getName());
                ImageSteganographyUtils.saveImage(image, outputFile);
                JOptionPane.showMessageDialog(frame, "隐写内容已添加，文件已保存：" + outputFile.getAbsolutePath());
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "错误：" + ex.getMessage());
            }
        });

        // 提取隐写内容行为
        decodeButton.addActionListener(e -> {
            File inputFile = fileChooser.getSelectedFile();
            if (inputFile == null || !inputFile.exists()) {
                JOptionPane.showMessageDialog(frame, "请选择一个有效的图像文件！");
                return;
            }

            try {
                BufferedImage image = ImageSteganographyUtils.loadImage(inputFile);
                String hiddenText = ImageSteganographyUtils.decodeText(image);
                JOptionPane.showMessageDialog(frame, "提取的隐写内容：" + hiddenText);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(frame, "错误：" + ex.getMessage());
            }
        });

        // 添加组件到框架
        frame.add(fileLabel);
        frame.add(fileButton);
        frame.add(encodeLabel);
        frame.add(encodeField);
        frame.add(encodeButton);
        frame.add(decodeButton);

        frame.setVisible(true);
    }
}