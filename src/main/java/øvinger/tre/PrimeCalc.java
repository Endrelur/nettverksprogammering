package øvinger.tre;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;

/**
 * A multithreaded class used for calculating all prime numbers in a given interval.
 */
public class PrimeCalc {

    public static void main(String[] args) {
        PrimeCalc primeCalc = new PrimeCalc();
        Scanner sc = new Scanner(System.in);
        System.out.println("Type in the lower boundary (int)...");
        System.out.print(">");
        int from = sc.nextInt();
        System.out.println("Type in th upper boundary (int)...");
        System.out.print(">");
        int to = sc.nextInt();
        primeCalc.calculatePrimes(from, to + 1);
    }

    PrimeCalc() {
        this.resultArray = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            this.resultArray.add(i, new LinkedList<>());
        }
    }

    //the array the threads deposit their results in.
    private final ArrayList<LinkedList<Integer>> resultArray;

    /**
     * Calculates all prime numbers in a given interval an prints them out to the terminal.
     *
     * @param from the lower boundary to calculate in.
     * @param to   the upper boundary to calculate in.
     */
    private void calculatePrimes(int from, int to) {
        long start = System.nanoTime();
        int[] ranges = splitIntoFourRanges(from, to);
        List<Thread> threadPool = new ArrayList<>();
        for (int i = 0; i < 4; i++) {
            Thread t = new Thread(new PrimeThread(resultArray.get(i), ranges[i], ranges[i + 1]));
            threadPool.add(t);
            t.start();
        }
        for (Thread t : threadPool) {
            try {
                t.join();
            } catch (InterruptedException e) {
            }
        }
        long end = System.nanoTime();
        printResult(from, to, end - start);
    }

    /**
     * Used to print out the result of the calculation in a nice fashion.
     *
     * @param from     the lower boundary for the calculation.
     * @param to       the lower boundary for the calculation.
     * @param timeInNs the time the calculation took.
     */
    private void printResult(int from, int to, long timeInNs) {
        System.out.print("start->");
        int counter = 0;
        for (LinkedList<Integer> list : resultArray) {
            list.forEach(this::printNicely);
            counter += list.size();
        }
        System.out.print("end\n");
        System.out.println("There was found: " + counter + " prime numbers between " + from + " & " + (to - 1));
        System.out.println("It took " + (timeInNs / (long) 1000) + "microseconds using 4 threads");
    }

    //keeps count of the amount of objects printed pr line.
    private int counter;

    /**
     * prints a integer out in a nice fashion to the terminal :)
     *
     * @param i the integer to print in a nice fashion :)
     */
    private void printNicely(int i) {
        if (this.counter < 20) {
            System.out.print(i + " -> ");
            this.counter++;
        } else {
            System.out.print("\n");
            this.counter = 0;
        }

    }

    /**
     * splits a given range into four ranges represented in a int[] array.
     *
     * @param from the lower boundary.
     * @param to   the upper boundary.
     * @return an array containing the boundary of four ranges split from the original one.
     * where int[0]->int[1] is the first, int[1]->int[2] is the second etc.
     */
    private int[] splitIntoFourRanges(int from, int to) {
        int[] ranges = new int[5];
        if (from < to) {
            ranges[0] = from;
            ranges[4] = to;
            ranges[2] = (from + to) / 2;
            ranges[1] = (ranges[0] + ranges[2]) / 2;
            ranges[3] = (ranges[2] + ranges[4]) / 2;
        }
        return ranges;
    }

}
