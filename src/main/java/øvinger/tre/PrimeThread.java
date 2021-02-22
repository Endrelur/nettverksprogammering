package øvinger.tre;

import java.util.LinkedList;

/**
 * A class representing a thread made for calculating the prime numbers of a given range.
 */
public class PrimeThread implements Runnable {
    private LinkedList<Integer> result;
    private int start;
    private int finish;

    /**
     * Creates a thread whit a lower an higher bound to calculate primes in. The
     * thread deposits its result in the given LinkedList.
     *
     * @param result the result primes of the calculation.
     * @param start  the lower bound.
     * @param finish the upper bound.
     */
    public PrimeThread(LinkedList<Integer> result, int start, int finish) {
        this.result = result;
        this.start = start;
        this.finish = finish;
    }

    @Override
    public void run() {
        for (int i = this.start; i < finish; i++) {
            if (isPrime(i)) {
                this.result.addLast(i);
            }
        }
    }

    /**
     * Checks whether a given integer is a prime number.
     *
     * @param integer the integer to check if is prime.
     * @return tru if the integer is prime, false if not.
     */
    boolean isPrime(int integer) {
        if (integer % 2 == 0) return false;
        for (int i = 3; i * i <= integer; i += 2) {
            if (integer % i == 0)
                return false;
        }
        return true;
    }
}
