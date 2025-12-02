package it.unibo.oop.workers02;

import java.util.stream.DoubleStream;

/**
 * .
 */
public class MultiThreadedSumMatrix implements SumMatrix {

    private final int nthread;

    /**
     * @param n the number of thread.
     */
    public MultiThreadedSumMatrix(final int n) {
        this.nthread = n;
    }

    /**
     * .
     */
    @Override
    public double sum(final double[][] matrix) {
       final int size = matrix.length % nthread + matrix.length / nthread;
       return DoubleStream
              .iterate(0, start -> start + size)
              .limit(nthread)
              .mapToObj(start -> new Worker(size, start, matrix))
              .peek(Thread::start) 
              .peek(MultiThreadedSumMatrix::joinUninterruptibly)
              .mapToDouble(Worker::getResult)
              .sum();
    }

    /**
     * @param target the thread.
     */
    @SuppressWarnings("PMD.AvoidPrintStackTrace")
    private static void joinUninterruptibly(final Thread target) {
        var joined = false;
        while (!joined) {
            try {
                target.join();
                joined = true;
            } catch (final InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * implementation of algorithm.
     */
    public final class Worker extends Thread {
        private final double[][] matrix;
        private final int size;
        private final int startpos;
        private volatile double res;

        /**
         * @param size size on have to calcolate
         * @param startpos initial position.
         * @param matrix the multi-array.
         */
        private Worker(final int size, final double startpos, final double[][] matrix) {
            super();
            this.matrix = matrix.clone();
            this.startpos = (int) startpos;
            this.size = size;
        }

        /**
         * .
         */
        @Override
        public void run() {
            System.out.println("Working from position " + startpos + " to position " + (startpos + size - 1)); //NOPMD
            for (int y = startpos; y < size + startpos && y < matrix.length; y++) {
                for (int x = 0; x < matrix.length; x++) {
                    this.res += matrix[y][x];
                }
            }
        }

        /**
         * @return res .
         */
        public double getResult() {
            return this.res;
        }
    }
}
