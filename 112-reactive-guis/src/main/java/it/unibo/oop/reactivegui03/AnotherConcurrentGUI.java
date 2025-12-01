package it.unibo.oop.reactivegui03;

import java.io.Serial;
import java.lang.reflect.InvocationTargetException;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.SwingUtilities;

import it.unibo.oop.JFrameUtil;

/**
 * Third experiment with reactive gui.
 */
public final class AnotherConcurrentGUI extends JFrame {

   @Serial
    private static final long serialVersionUID = 1L;
    private final JLabel display = new JLabel();

    /**
     * .
     */
    public AnotherConcurrentGUI() {
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
        final Agent2 agent2 = new Agent2(agent);
        new Thread(agent2).start();

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
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(number));
                        this.counter++;
                        Thread.sleep(100);
                    } catch (InvocationTargetException | InterruptedException ex) {
                        ex.printStackTrace(); //NOPMD
                    }
                } else {
                    try {
                        final String number2 = String.valueOf(counter);
                        SwingUtilities.invokeAndWait(() -> AnotherConcurrentGUI.this.display.setText(number2));
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

    private final class Agent2 implements Runnable {
        private static final int TIME = 10_000;
        private final Agent ag;

        Agent2(final Agent agent) {
            this.ag = agent;
        }

        @Override
        public void run() {
            final double temp = System.currentTimeMillis();
            while (System.currentTimeMillis() - temp <= TIME) {
                System.out.println("sto aspettando"); //NOPMD
            }
            ag.stopCounting();
        }
    }
}
