import java.util.*;
import java.util.concurrent.*;

public class PairSumWorkDealing {
    public static void main(String[] args) throws InterruptedException, ExecutionException {
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

        int[] arr = new Random().ints(n, min, max+1).toArray();
        System.out.println("Згенерований масив: " + Arrays.toString(arr));

        int threads = Runtime.getRuntime().availableProcessors();
        ExecutorService exec = Executors.newFixedThreadPool(threads);

        List<Future<Long>> futures = new ArrayList<>();
        int chunk = arr.length / threads;

        long start = System.nanoTime();

        for (int i = 0; i < threads; i++) {
            int startIdx = i * chunk;
            int endIdx = (i == threads - 1) ? arr.length : startIdx + chunk;

            futures.add(exec.submit(() -> {
                long sum = 0;
                for (int j = startIdx; j < endIdx - 1; j++) {
                    sum += arr[j] + arr[j + 1];
                }
                return sum;
            }));
        }

        long result = 0;
        for (Future<Long> f : futures) result += f.get();
        long time = System.nanoTime() - start;

        exec.shutdown();

        System.out.println("\nРезультат: " + result);
        System.out.println("Час виконання (Work Dealing): " + time/1_000_000 + " ms");
    }
}
