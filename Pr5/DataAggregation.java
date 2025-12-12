import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.List;

/**
 * Програма для отримання даних з кількох джерел одночасно
 * та їх обробки після завершення всіх завдань
 */
public class DataAggregation {

    // Клас для представлення даних користувача
    static class UserData {
        private String name;
        private String email;

        public UserData(String name, String email) {
            this.name = name;
            this.email = email;
        }

        @Override
        public String toString() {
            return "UserData{name='" + name + "', email='" + email + "'}";
        }
    }

    // Клас для представлення даних замовлень
    static class OrderData {
        private String orderId;
        private double amount;

        public OrderData(String orderId, double amount) {
            this.orderId = orderId;
            this.amount = amount;
        }

        @Override
        public String toString() {
            return "OrderData{orderId='" + orderId + "', amount=" + amount + "}";
        }
    }

    // Клас для представлення статистики
    static class StatisticsData {
        private int totalVisits;
        private int activeUsers;

        public StatisticsData(int totalVisits, int activeUsers) {
            this.totalVisits = totalVisits;
            this.activeUsers = activeUsers;
        }

        @Override
        public String toString() {
            return "StatisticsData{totalVisits=" + totalVisits + ", activeUsers=" + activeUsers + "}";
        }
    }

    // Клас для агрегованих даних
    static class AggregatedData {
        private UserData userData;
        private List<OrderData> orders;
        private StatisticsData statistics;

        public AggregatedData(UserData userData, List<OrderData> orders, StatisticsData statistics) {
            this.userData = userData;
            this.orders = orders;
            this.statistics = statistics;
        }

        @Override
        public String toString() {
            return "AggregatedData{\n" +
                    "  userData=" + userData + ",\n" +
                    "  orders=" + orders + ",\n" +
                    "  statistics=" + statistics + "\n}";
        }
    }

    // Симуляція отримання даних користувача з бази даних
    public static CompletableFuture<UserData> fetchUserData(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Завантаження даних користувача...");
            simulateDelay(2000);
            System.out.println("[" + Thread.currentThread().getName() + "] Дані користувача завантажено");
            return new UserData("Олександр Петренко", "oleksandr.petrenko@email.com");
        });
    }

    // Симуляція отримання замовлень користувача
    public static CompletableFuture<List<OrderData>> fetchUserOrders(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Завантаження замовлень...");
            simulateDelay(1500);
            System.out.println("[" + Thread.currentThread().getName() + "] Замовлення завантажено");
            return List.of(
                    new OrderData("ORD-001", 1250.50),
                    new OrderData("ORD-002", 890.75),
                    new OrderData("ORD-003", 2100.00)
            );
        });
    }

    // Симуляція отримання статистики користувача
    public static CompletableFuture<StatisticsData> fetchUserStatistics(String userId) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Завантаження статистики...");
            simulateDelay(1000);
            System.out.println("[" + Thread.currentThread().getName() + "] Статистику завантажено");
            return new StatisticsData(152, 48);
        });
    }

    // Симуляція затримки (виклик API, запит до БД тощо)
    private static void simulateDelay(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Операцію перервано", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("\nДемонстрація allOf() для агрегації даних\n");

        String userId = "USER-12345";
        long startTime = System.currentTimeMillis();

        // Створюємо три незалежні асинхронні завдання
        CompletableFuture<UserData> userDataFuture = fetchUserData(userId);
        CompletableFuture<List<OrderData>> ordersFuture = fetchUserOrders(userId);
        CompletableFuture<StatisticsData> statisticsFuture = fetchUserStatistics(userId);

        // Використовуємо allOf() для очікування завершення всіх завдань
        CompletableFuture<Void> allDataFuture = CompletableFuture.allOf(
                userDataFuture,
                ordersFuture,
                statisticsFuture
        );

        // Після завершення всіх завдань обробляємо результати
        CompletableFuture<AggregatedData> aggregatedFuture = allDataFuture.thenApply(v -> {
            System.out.println("\n[" + Thread.currentThread().getName() + "] Всі дані отримано, виконуємо агрегацію...");
            // Отримуємо результати з кожного CompletableFuture
            UserData userData = userDataFuture.join();
            List<OrderData> orders = ordersFuture.join();
            StatisticsData statistics = statisticsFuture.join();

            return new AggregatedData(userData, orders, statistics);
        });

        // Обробка результату та помилок
        aggregatedFuture
                .thenAccept(data -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println("\nАгреговані дані успішно отримано:");
                    System.out.println(data);
                    System.out.println("\nЗагальний час виконання: " + (endTime - startTime) + " мс");
                })
                .exceptionally(ex -> {
                    System.err.println("Помилка при отриманні даних: " + ex.getMessage());
                    return null;
                })
                .join();

        System.out.println("\n" + "-".repeat(60));
        System.out.println("Демонстрація anyOf() для найшвидшого джерела\n");

        // Створюємо кілька джерел даних з різною швидкістю відповіді
        CompletableFuture<String> source1 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Джерело 1: починає завантаження...");
            simulateDelay(1500);
            return "Дані з джерела 1 (повільне)";
        });

        CompletableFuture<String> source2 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Джерело 2: починає завантаження...");
            simulateDelay(800);
            return "Дані з джерела 2 (швидке)";
        });

        CompletableFuture<String> source3 = CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Джерело 3: починає завантаження...");
            simulateDelay(1200);
            return "Дані з джерела 3 (середнє)";
        });

        long startTime2 = System.currentTimeMillis();

        // anyOf() поверне результат першого завершеного завдання
        CompletableFuture<Object> fastestSource = CompletableFuture.anyOf(source1, source2, source3);

        fastestSource
                .thenAccept(result -> {
                    long endTime = System.currentTimeMillis();
                    System.out.println("\nОтримано результат від найшвидшого джерела:");
                    System.out.println("  " + result);
                    System.out.println("  Час отримання: " + (endTime - startTime2) + " мс");
                })
                .join();
    }
}