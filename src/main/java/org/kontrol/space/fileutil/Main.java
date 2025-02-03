package org.kontrol.space.fileutil;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class Main {

    public static void main(String[] args) {
        JFrame frame = new JFrame("数字水印工具");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(600, 300);
        frame.setLayout(new GridLayout(0, 2));

        // 创建组件
        JLabel fileLabel = new JLabel("选择文件：");
        JButton fileButton = new JButton("选择...");
        JLabel watermarkLabel = new JLabel("数字水印内容（可选）：");
        JTextField watermarkField = new JTextField();
        JLabel keyLabel = new JLabel("对称密钥：");
        JTextField keyField = new JTextField();
        JButton addWatermarkButton = new JButton("添加数字水印");
        JButton checkWatermarkButton = new JButton("校验数字水印");

        JFileChooser fileChooser = new JFileChooser();

        // 文件选择行为
        fileButton.addActionListener(e -> {
            int returnValue = fileChooser.showOpenDialog(null);
            if (returnValue == JFileChooser.APPROVE_OPTION) {
                fileButton.setText(fileChooser.getSelectedFile().getName());
            }
        });

        // 添加数字水印行为
        addWatermarkButton.addActionListener((ActionEvent e) -> {
            File inputFile = fileChooser.getSelectedFile();
            if (inputFile == null || !inputFile.exists()) {
                JOptionPane.showMessageDialog(frame, "请选择一个有效的文件！");
                return;
            }

            String watermark = watermarkField.getText();
            String secretKey = keyField.getText();
            if (secretKey.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入有效的对称密钥！");
                return;
            }

            File outputFile = new File(inputFile.getParent(), inputFile.getName() + "_watermarked.jpg");

            try {
                FileWatermarkUtils.addWatermark(inputFile, outputFile, watermark.isEmpty() ? null : watermark, secretKey);
                JOptionPane.showMessageDialog(frame, "水印已添加，文件已保存：" + outputFile.getAbsolutePath());
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "错误：" + ex.getMessage());
            }
        });

        // 校验数字水印行为
        checkWatermarkButton.addActionListener((ActionEvent e) -> {
            File inputFile = fileChooser.getSelectedFile();
            if (inputFile == null || !inputFile.exists()) {
                JOptionPane.showMessageDialog(frame, "请选择一个有效的文件！");
                return;
            }

            String secretKey = keyField.getText();
            if (secretKey.isEmpty()) {
                JOptionPane.showMessageDialog(frame, "请输入有效的对称密钥！");
                return;
            }

            try {
                String watermark = FileWatermarkUtils.checkWatermark(inputFile, secretKey);
                JOptionPane.showMessageDialog(frame, "水印内容：" + watermark);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, "错误：" + ex.getMessage());
            }
        });

        // 添加组件到框架
        frame.add(fileLabel);
        frame.add(fileButton);
        frame.add(watermarkLabel);
        frame.add(watermarkField);
        frame.add(keyLabel);
        frame.add(keyField);
        frame.add(addWatermarkButton);
        frame.add(checkWatermarkButton);

        frame.setVisible(true);
    }
}