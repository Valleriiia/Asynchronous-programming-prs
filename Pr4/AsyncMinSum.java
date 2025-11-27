import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Програма для асинхронного знаходження мінімальної суми сусідніх елементів
 */
public class AsyncMinSum {

    public static void main(String[] args) {
        System.out.println("\nПрограма знаходження min(a1+a2, a2+a3, ...)\n");

        long totalStartTime = System.currentTimeMillis();

        // Крок 1: Асинхронна генерація послідовності
        CompletableFuture<int[]> generateSequenceFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("[ГЕНЕРАЦІЯ] Створення послідовності з 20 елементів...");

            int[] sequence = new int[20];
            Random random = new Random();

            for (int i = 0; i < sequence.length; i++) {
                sequence[i] = random.nextInt(100) + 1; // Натуральні числа від 1 до 100
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ГЕНЕРАЦІЯ] Завершено за " + duration + " мс\n");

            return sequence;
        });

        // Крок 2: Асинхронний вивід початкової послідовності
        CompletableFuture<int[]> printSequenceFuture = generateSequenceFuture.thenApplyAsync(sequence -> {
            long startTime = System.currentTimeMillis();

            System.out.println("[ВИВІД] Початкова послідовність:");
            System.out.println(Arrays.toString(sequence));
            System.out.println();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ВИВІД] Виведено послідовність за " + duration + " мс\n");

            return sequence;
        });

        // Крок 3: Асинхронне обчислення сум сусідніх елементів
        CompletableFuture<int[]> calculateSumsFuture = printSequenceFuture.thenApplyAsync(sequence -> {
            long startTime = System.currentTimeMillis();
            System.out.println("[ОБЧИСЛЕННЯ] Розрахунок сум сусідніх елементів...");

            int[] sums = new int[sequence.length - 1];

            for (int i = 0; i < sequence.length - 1; i++) {
                sums[i] = sequence[i] + sequence[i + 1];
            }

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ОБЧИСЛЕННЯ] Обчислено " + sums.length + " сум за " + duration + " мс\n");

            return sums;
        });

        // Крок 4: Асинхронний вивід сум
        CompletableFuture<int[]> printSumsFuture = calculateSumsFuture.thenApplyAsync(sums -> {
            long startTime = System.currentTimeMillis();

            System.out.println("[ВИВІД] Суми сусідніх елементів:");
            System.out.print("(");
            for (int i = 0; i < sums.length; i++) {
                System.out.print("a" + (i + 1) + "+a" + (i + 2) + "=" + sums[i]);
                if (i < sums.length - 1) {
                    System.out.print(", ");
                }
            }
            System.out.println(")\n");

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ВИВІД] Виведено суми за " + duration + " мс\n");

            return sums;
        });

        // Крок 5: Асинхронне знаходження мінімуму
        CompletableFuture<Integer> findMinFuture = printSumsFuture.thenApplyAsync(sums -> {
            long startTime = System.currentTimeMillis();
            System.out.println("[ПОШУК] Знаходження мінімальної суми...");

            int minSum = Arrays.stream(sums).min().orElseThrow();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ПОШУК] Знайдено мінімум за " + duration + " мс\n");

            return minSum;
        });

        // Крок 6: Асинхронний вивід результату
        CompletableFuture<Void> printResultFuture = findMinFuture.thenAcceptAsync(minSum -> {
            long startTime = System.currentTimeMillis();

            System.out.println("[РЕЗУЛЬТАТ] Мінімальна сума сусідніх елементів: " + minSum);
            System.out.println();

            long endTime = System.currentTimeMillis();
            long duration = endTime - startTime;
            System.out.println("[ВИВІД] Виведено результат за " + duration + " мс\n");
        });

        // Крок 7: Фінальна операція після завершення всіх асинхронних задач
        printResultFuture.thenRunAsync(() -> {
            long totalEndTime = System.currentTimeMillis();
            long totalDuration = totalEndTime - totalStartTime;

            System.out.println("ВСІ АСИНХРОННІ ОПЕРАЦІЇ ЗАВЕРШЕНО");
            System.out.println("Загальний час виконання всіх операцій: " + totalDuration + " мс");
        }).join(); // Очікування завершення всіх операцій
    }
}