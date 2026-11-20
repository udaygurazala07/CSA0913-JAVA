package task05;

import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;
import task05.lambdaexpression.Converter;

public class Task05Capture {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                Converter conv = new Converter();
                conv.setVisible(true);

                File outDir = new File("..\\Assignment 1 -java\\Java-Assignment-4\\Task05-Currency-Converter\\Screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                // 1. Valid Conversion (10000 INR -> USD)
                conv.getAmountField().setText("10000");
                conv.getTargetCombo().setSelectedItem("USD");
                conv.triggerConvert();
                conv.pack();

                BufferedImage img1 = new BufferedImage(conv.getWidth(), conv.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g1 = img1.createGraphics();
                conv.printAll(g1);
                g1.dispose();
                ImageIO.write(img1, "png", new File(outDir, "Task05-Valid-Conversion.png"));
                System.out.println("Captured: Task05-Valid-Conversion.png (Result: " + conv.getResultLabel().getText() + ")");

                // 2. Invalid Amount ("abc")
                conv.getAmountField().setText("abc");
                conv.triggerConvert();
                conv.pack();

                BufferedImage img2 = new BufferedImage(conv.getWidth(), conv.getHeight(), BufferedImage.TYPE_INT_RGB);
                Graphics2D g2 = img2.createGraphics();
                conv.printAll(g2);
                g2.dispose();
                ImageIO.write(img2, "png", new File(outDir, "Task05-Invalid-Amount.png"));
                System.out.println("Captured: Task05-Invalid-Amount.png (Result: " + conv.getResultLabel().getText() + ")");

                conv.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}