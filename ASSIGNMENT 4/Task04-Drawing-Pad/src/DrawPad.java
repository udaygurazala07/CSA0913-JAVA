package task04;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Label;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class DrawPad extends Frame {
    private final List<Point> points = Collections.synchronizedList(new ArrayList<>());
    private final Label status = new Label("Clicks: 0");

    public DrawPad() {
        super("Draw Pad");
        add(status, BorderLayout.SOUTH);
        addMouseListener(new MouseAdapter() {
            // TODO 1: Override mouseClicked correctly: store the point, update the
            // label and request a repaint
            @Override
            public void mouseClicked(MouseEvent e) {
                points.add(e.getPoint());
                status.setText("Clicks: " + points.size());
                repaint();
            }
        });
        addWindowListener(new WindowAdapter() {
            @Override public void windowClosing(WindowEvent e) { dispose(); }
        });
        setSize(400, 300);
        setLocationRelativeTo(null);
    }

    @Override public void paint(Graphics g) {
        // TODO 2: Draw every stored point as a 6 × 6 filled circle centred on the point
        super.paint(g);
        g.setColor(Color.RED);
        synchronized (points) {
            for (Point p : points) {
                g.fillOval(p.x - 3, p.y - 3, 6, 6);
            }
        }
    }

    public List<Point> getPoints() { return points; }
    public Label getStatusLabel() { return status; }

    public static void main(String[] args) { new DrawPad().setVisible(true); }
}