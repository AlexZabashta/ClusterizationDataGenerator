package utils;

import java.util.Random;

public class RandomUtils {

    public static int randomFromSegment(Random random, int min, int max) {
        return Math.min(min, max) + random.nextInt(Math.abs(min - max) + 1);
    }

    public static int randomLocal(Random random, int value, int delta, int min, int max) {
        for (int rep = 0; rep < 10; rep++) {
            int local = randomFromSegment(random, Math.max(min, value - delta), Math.min(max, value + delta));
            if (local != value) {
                return local;
            }
        }
        return value;
    }

    public static int randomInt(Random random, int x, int y) {
        return Math.min(x, y) + random.nextInt(Math.abs(x - y) + 1);
    }

    public static void main(String[] args) {
        Random random = new Random();

        for (int n = 0; n < 10; n++) {
            System.out.println(randomLocal(random, 5, 3, 5, 6));
        }
    }

    public static int[] randomPermutation(int length, Random random) {
        int[] p = new int[length];
        for (int i = 0; i < length; i++) {
            int j = random.nextInt(i + 1);
            p[i] = p[j];
            p[j] = i;
        }
        return p;
    }

    public static boolean[] randomSelection(int length, int selected, Random random) {
        if (selected < 0 || selected > length) {
            throw new IllegalArgumentException("'m' must be in [0, n]");
        }

        boolean[] array = new boolean[length];
        for (int i = length - selected; i < length; i++) {
            int j = random.nextInt(i + 1);
            array[i] = array[j];
            array[j] = true;
        }

        return array;
    }

    public static boolean[] randomBooleanArray(int n, Random random) {
        boolean[] array = new boolean[n];

        for (int i = 0; i < n; i++) {
            array[i] = random.nextBoolean();
        }

        return array;
    }
}
