package it.unibo.oop.reactivegui02;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

/**
 * Second example of reactive GUI.
 */
public final class ConcurrentGUI extends JFrame {

    @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    /**
     * .
     */
    public ConcurrentGUI() {
        super();
        JFrameUtil.dimensionJFrame(this);
        final JLabel canvas = new JLabel();
        canvas.setLayout(new BoxLayout(canvas, BoxLayout.X_AXIS));
        final JButton stop = new JButton("stop");
        final JButton up = new JButton("up");
        final JButton down = new JButton("down");
        canvas.add(display);
        canvas.add(down);
        canvas.add(stop);
        canvas.add(up);
        this.setContentPane(canvas);
        this.setVisible(true);

        final Agent agent = new Agent();
        new Thread(agent).start();

        stop.addActionListener(e -> agent.stopCounting());
        up.addActionListener(e -> agent.upCounting());
        down.addActionListener(e -> agent.downCounting());

    }

    private final class Agent implements Runnable {
        /**
         * 
         */
        private volatile boolean up = true;
        private volatile boolean stop;
        private int counter;

        @Override
        public void run() {
            while (!this.stop) {
                if (up) {
                    try {
                        final String number = String.valueOf(counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(number));
                        this.counter++;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace(); //NOPMD
                    }
                } else {
                    try {
                        final String number2 = String.valueOf(counter);
                        SwingUtilities.invokeAndWait(() -> ConcurrentGUI.this.display.setText(number2));
                        this.counter--;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace(); //NOPMD
                    }
                }
            }
        }

        /**
         * External command to stop counting.
         */
        public void stopCounting() {
            this.stop = true;
        }

        public void upCounting() {
            this.up = true;
        }

        public void downCounting() {
            this.up = false;
        }
    }
}
