package task04;

import java.awt.Frame;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.SwingUtilities;

public class Task04Capture {
    public static void main(String[] args) throws Exception {
        SwingUtilities.invokeAndWait(() -> {
            try {
                DrawPad pad = new DrawPad();
                pad.setVisible(true);

                File outDir = new File("..\\Assignment 1 -java\\Java-Assignment-4\\Task04-Drawing-Pad\\Screenshots");
                if (!outDir.exists()) outDir.mkdirs();

                // 1. Capture Before Clicks
                capture(pad, new File(outDir, "Task04-Before-Clicks.png"));
                System.out.println("Captured: Task04-Before-Clicks.png");

                // 2. Perform 6 Clicks
                Point[] pts = {
                    new Point(80, 80),
                    new Point(140, 120),
                    new Point(200, 90),
                    new Point(260, 160),
                    new Point(320, 110),
                    new Point(190, 210)
                };

                for (Point p : pts) {
                    MouseEvent me = new MouseEvent(pad, MouseEvent.MOUSE_CLICKED, System.currentTimeMillis(), 0, p.x, p.y, 1, false);
                    pad.getMouseListeners()[0].mouseClicked(me);
                }

                pad.repaint();

                // 3. Capture After Clicks
                capture(pad, new File(outDir, "Task04-After-Clicks.png"));
                System.out.println("Captured: Task04-After-Clicks.png (Clicks: " + pad.getPoints().size() + ")");

                // 4. Simulate Minimise and Restore
                pad.setExtendedState(Frame.ICONIFIED);
                pad.setExtendedState(Frame.NORMAL);
                pad.repaint();

                // 5. Capture After Minimise & Restore
                capture(pad, new File(outDir, "Task04-After-Minimise-Restore.png"));
                System.out.println("Captured: Task04-After-Minimise-Restore.png (Surviving clicks: " + pad.getPoints().size() + ")");

                pad.dispose();
                System.exit(0);
            } catch (Exception e) {
                e.printStackTrace();
                System.exit(1);
            }
        });
    }

    private static void capture(DrawPad pad, File file) throws Exception {
        BufferedImage img = new BufferedImage(pad.getWidth(), pad.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = img.createGraphics();
        pad.printAll(g2);
        g2.dispose();
        ImageIO.write(img, "png", file);
    }
}