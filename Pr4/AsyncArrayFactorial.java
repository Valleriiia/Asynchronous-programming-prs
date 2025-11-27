import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.CompletableFuture;

/**
 * Програма для асинхронної роботи з масивами та обчислення факторіалу
 */
public class AsyncArrayFactorial {

    public static void main(String[] args) {
        System.out.println("\nПочаток виконання програми\n");

        long programStartTime = System.currentTimeMillis();

        // Крок 1: Асинхронна генерація масиву
        CompletableFuture<int[]> generateArrayFuture = CompletableFuture.supplyAsync(() -> {
            long startTime = System.currentTimeMillis();
            System.out.println("Генерація початкового масиву...");

            int[] array = new int[10];
            Random random = new Random();
            for (int i = 0; i < array.length; i++) {
                array[i] = random.nextInt(10) + 1; // Числа від 1 до 10
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Час генерації масиву: " + (endTime - startTime) + " мс\n");

            return array;
        });

        // Крок 2: Асинхронний вивід першого масиву
        CompletableFuture<int[]> printFirstArrayFuture = generateArrayFuture.thenApplyAsync(array -> {
            long startTime = System.currentTimeMillis();

            System.out.println("Початковий масив: " + Arrays.toString(array));
            System.out.println("Сума елементів початкового масиву: " + Arrays.stream(array).sum());

            long endTime = System.currentTimeMillis();
            System.out.println("Час виводу початкового масиву: " + (endTime - startTime) + " мс\n");

            return array;
        });

        // Крок 3: Асинхронне збільшення елементів на 5
        CompletableFuture<int[]> incrementArrayFuture = printFirstArrayFuture.thenApplyAsync(array -> {
            long startTime = System.currentTimeMillis();
            System.out.println("Збільшення кожного елементу на 5...");

            int[] newArray = new int[array.length];
            for (int i = 0; i < array.length; i++) {
                newArray[i] = array[i] + 5;
            }

            long endTime = System.currentTimeMillis();
            System.out.println("Час збільшення елементів: " + (endTime - startTime) + " мс\n");

            return newArray;
        });

        // Крок 4: Асинхронний вивід другого масиву
        CompletableFuture<int[]> printSecondArrayFuture = incrementArrayFuture.thenApplyAsync(array -> {
            long startTime = System.currentTimeMillis();

            System.out.println("Масив після збільшення: " + Arrays.toString(array));
            System.out.println("Сума елементів другого масиву: " + Arrays.stream(array).sum());

            long endTime = System.currentTimeMillis();
            System.out.println("Час виводу другого масиву: " + (endTime - startTime) + " мс\n");

            return array;
        });

        // Крок 5: Асинхронне обчислення факторіалу
        CompletableFuture<BigInteger> factorialFuture = CompletableFuture.allOf(
                printFirstArrayFuture,
                printSecondArrayFuture
        ).thenApplyAsync(v -> {
            long startTime = System.currentTimeMillis();
            System.out.println("Обчислення факторіалу...");

            int[] firstArray = printFirstArrayFuture.join();
            int[] secondArray = printSecondArrayFuture.join();

            int sumFirst = Arrays.stream(firstArray).sum();
            int sumSecond = Arrays.stream(secondArray).sum();
            int totalSum = sumFirst + sumSecond;

            System.out.println("Сума всіх елементів (початковий + другий): " + totalSum);

            BigInteger factorial = calculateFactorial(totalSum);

            long endTime = System.currentTimeMillis();
            System.out.println("Час обчислення факторіалу: " + (endTime - startTime) + " мс\n");

            return factorial;
        });

        // Крок 6: Асинхронний вивід результату факторіалу
        CompletableFuture<Void> printFactorialFuture = factorialFuture.thenAcceptAsync(factorial -> {
            long startTime = System.currentTimeMillis();

            System.out.println("Результат обчислення факторіалу: " + factorial);

            long endTime = System.currentTimeMillis();
            System.out.println("Час виводу факторіалу: " + (endTime - startTime) + " мс\n");
        });

        // Крок 7: Фінальна асинхронна операція після завершення всього
        printFactorialFuture.thenRunAsync(() -> {
            long programEndTime = System.currentTimeMillis();
            long totalTime = programEndTime - programStartTime;

            System.out.println("Всі операції завершено");
            System.out.println("Загальний час виконання програми: " + totalTime + " мс");
        }).join(); // Очікування завершення всіх операцій
    }

    /**
     * Метод для обчислення факторіалу
     */
    private static BigInteger calculateFactorial(int n) {
        if (n < 0) {
            throw new IllegalArgumentException("Факторіал не визначений для від'ємних чисел");
        }

        BigInteger result = BigInteger.ONE;
        for (int i = 2; i <= n; i++) {
            result = result.multiply(BigInteger.valueOf(i));
        }

        return result;
    }
}