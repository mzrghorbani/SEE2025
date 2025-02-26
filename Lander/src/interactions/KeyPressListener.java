package interactions;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyPressListener {

    private String lastKey = null;

    public KeyPressListener(String title) {
        startInputListener(title);
    }

    private void startInputListener(String title) {
        JFrame frame = new JFrame(title);
        frame.setUndecorated(true);
        frame.setOpacity(0f);
        frame.setFocusable(true);
        frame.setVisible(true);

        frame.addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                setKey(KeyEvent.getKeyText(e.getKeyCode()));  
            }
        });

        frame.requestFocus();
    }

    public synchronized boolean hasKeyPress() {
        return lastKey != null;
    }

    public synchronized String getKey() {
        return lastKey;
    }

    public synchronized void setKey(String key) {
        this.lastKey = key;
    }

    public synchronized void reset() {
        this.lastKey = null;
    }
}
