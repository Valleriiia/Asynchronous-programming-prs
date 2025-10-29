import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Main {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        Random random = new Random();

        int min, max, multiplier;
        int size = random.nextInt(21) + 40; // 40–60 елементів

        System.out.println("\nАсинхронна обробка масиву чисел");

        // Валідація вводу
        while (true) {
            System.out.print("Введіть нижню межу діапазону: ");
            if (scanner.hasNextInt()) {
                min = scanner.nextInt();
                break;
            } else scanner.next();
            System.out.println("Помилка! Введіть ціле число.");
        }

        while (true) {
            System.out.print("Введіть верхню межу діапазону: ");
            if (scanner.hasNextInt()) {
                max = scanner.nextInt();
                if (max > min) break;
            } else scanner.next();
            System.out.println("Помилка! Введіть ціле число, що більше за нижню межу діапазону.");
        }

        while (true) {
            System.out.print("Введіть множник: ");
            if (scanner.hasNextInt()) {
                multiplier = scanner.nextInt();
                if (multiplier != 0) break;
            } else scanner.next();
            System.out.println("Помилка! Введіть ненульове ціле число.");
        }

        // Генерація масиву
        List<Integer> numbers = new CopyOnWriteArrayList<>();
        for (int i = 0; i < size; i++) {
            numbers.add(random.nextInt(max - min + 1) + min);
        }

        System.out.println("\nКількість елементів у масиві: " + size);
        System.out.println("Початковий масив:");
        System.out.println(numbers);

        // Обробка
        ArrayProcessor processor = new ArrayProcessor();
        processor.processArray(numbers, multiplier);
    }
}
