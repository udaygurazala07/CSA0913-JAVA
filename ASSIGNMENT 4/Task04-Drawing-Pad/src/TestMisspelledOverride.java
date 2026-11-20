package task04;

import java.awt.Frame;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class TestMisspelledOverride extends Frame {
    public TestMisspelledOverride() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseclicked(MouseEvent e) {
                System.out.println("Clicked");
            }
        });
    }
}