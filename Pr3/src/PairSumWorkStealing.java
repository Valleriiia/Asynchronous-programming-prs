import java.util.*;
import java.util.concurrent.*;

public class PairSumWorkStealing {

    static class PairSumTask extends RecursiveTask<Long> {
        private final int[] arr;
        private final int start, end;
        private static final int THRESHOLD = 10000;

        PairSumTask(int[] arr, int start, int end) {
            this.arr = arr;
            this.start = start;
            this.end = end;
        }

        @Override
        protected Long compute() {
            if (end - start <= THRESHOLD) {
                long sum = 0;
                for (int i = start; i < end - 1; i++)
                    sum += arr[i] + arr[i + 1];
                return sum;
            }

            int mid = (start + end) / 2;
            PairSumTask left = new PairSumTask(arr, start, mid);
            PairSumTask right = new PairSumTask(arr, mid, end);

            left.fork();
            return right.compute() + left.join();
        }
    }

    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);

        int n;
        while (true) {
            System.out.print("Введіть кількість елементів масиву (мінімум 2): ");
            if (sc.hasNextInt()) {
                n = sc.nextInt();
                if (n >= 2) break;
                System.out.println("Масив повинен містити хоча б 2 елементи");
            } else {
                System.out.println("Введіть ціле число");
                sc.next(); // очищення некоректного вводу
            }
        }

        int min;
        while (true) {
            System.out.print("Мінімальне значення: ");
            if (sc.hasNextInt()) {
                min = sc.nextInt();
                break;
            }
            System.out.println("Введіть ціле число");
            sc.next();
        }

        int max;
        while (true) {
            System.out.print("Максимальне значення: ");
            if (sc.hasNextInt()) {
                max = sc.nextInt();
                if (max >= min) break;
                System.out.println("Максимальне значення повинно бути не меньше мінімального");
            } else {
                System.out.println("Введіть ціле число!");
                sc.next();
            }
        }

        // Генерація масиву
        int[] arr = new Random().ints(n, min, max + 1).toArray();
        System.out.println("\nЗгенерований масив: " + Arrays.toString(arr));

        ForkJoinPool pool = new ForkJoinPool();

        long start = System.nanoTime();
        long result = pool.invoke(new PairSumTask(arr, 0, arr.length));
        long time = System.nanoTime() - start;

        System.out.println("\nРезультат: " + result);
        System.out.println("Час виконання (Work Stealing): " + time / 1_000_000 + " ms");
    }
}
