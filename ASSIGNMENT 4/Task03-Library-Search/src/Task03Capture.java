package task03;

import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

public class Task03Capture {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                LibrarySearchFrame frame = new LibrarySearchFrame();
                frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                frame.setVisible(true);

                File outDir = new File("..\\Assignment 1 -java\\Java-Assignment-4\\Task03-Library-Search\\Screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                int[][] sizes = {
                    {550, 350},
                    {800, 500},
                    {1050, 700}
                };

                String[] names = {
                    "Task03-Size1-Compact-550x350.png",
                    "Task03-Size2-Medium-800x500.png",
                    "Task03-Size3-Expanded-1050x700.png"
                };

                for (int i = 0; i < sizes.length; i++) {
                    int w = sizes[i][0];
                    int h = sizes[i][1];
                    frame.setSize(w, h);
                    frame.revalidate();
                    frame.repaint();
                    Thread.sleep(500); // let Swing render

                    BufferedImage img = new BufferedImage(frame.getWidth(), frame.getHeight(), BufferedImage.TYPE_INT_RGB);
                    Graphics2D g2 = img.createGraphics();
                    frame.printAll(g2);
                    g2.dispose();

                    File file = new File(outDir, names[i]);
                    ImageIO.write(img, "png", file);
                    System.out.println("Captured: " + file.getName() + " Size: " + file.length() + " bytes");
                }

                frame.dispose();
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}