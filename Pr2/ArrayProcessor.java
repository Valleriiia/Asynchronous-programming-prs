import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class ArrayProcessor {

    public List<Integer> processArray(List<Integer> numbers, int multiplier) {
        int size = numbers.size();
        int chunkSize = size / 4;
        List<List<Integer>> partitions = new ArrayList<>();

        for (int i = 0; i < size; i += chunkSize) {
            partitions.add(numbers.subList(i, Math.min(i + chunkSize, size)));
        }

        ExecutorService executor = Executors.newFixedThreadPool(partitions.size());
        List<Future<List<Integer>>> futures = new ArrayList<>();

        long startTime = System.currentTimeMillis();

        for (List<Integer> part : partitions) {
            futures.add(executor.submit(new MultiplierTask(part, multiplier)));
        }

        List<Integer> finalResult = new CopyOnWriteArrayList<>();

        for (Future<List<Integer>> future : futures) {
            try {
                while (!future.isDone()) {
                    System.out.println("Задача ще виконується...");
                    Thread.sleep(50);
                }

                if (!future.isCancelled()) {
                    finalResult.addAll(future.get());
                } else {
                    System.out.println("Задачу було скасовано.");
                }

            } catch (InterruptedException | ExecutionException e) {
                System.err.println("Помилка при виконанні: " + e.getMessage());
            }
        }

        executor.shutdown();
        long endTime = System.currentTimeMillis();

        System.out.println("\nРезультуючий масив:");
        System.out.println(finalResult);

        System.out.println("\nЧас виконання програми: " + (endTime - startTime) + " мс");

        return finalResult;
    }
}