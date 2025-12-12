import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.Comparator;
import java.util.List;

/**
 * Програма для планування подорожі з демонстрацією роботи методів CompletableFuture:
 * thenCompose(), thenCombine(), allOf(), anyOf().
 */
public class TravelPlanner {

    // Клас для представлення маршруту
    static class Route {
        private String transportType;
        private String from;
        private String to;
        private double price;
        private double durationHours;
        private String departureTime;

        public Route(String transportType, String from, String to, double price,
                     double durationHours, String departureTime) {
            this.transportType = transportType;
            this.from = from;
            this.to = to;
            this.price = price;
            this.durationHours = durationHours;
            this.departureTime = departureTime;
        }

        public double getScore() {
            return price * 0.5 + durationHours * 100;
        }

        public String getTransportType() {
            return transportType;
        }

        public double getPrice() {
            return price;
        }

        public double getDurationHours() {
            return durationHours;
        }

        @Override
        public String toString() {
            return String.format("%s: %s -> %s | Ціна: %.2f грн | Час: %.1f год | Відправлення: %s",
                    transportType, from, to, price, durationHours, departureTime);
        }
    }

    // Клас для деталей маршруту
    static class RouteDetails {
        private Route route;
        private String carrier;
        private int availableSeats;
        private boolean hasWifi;

        public RouteDetails(Route route, String carrier, int availableSeats, boolean hasWifi) {
            this.route = route;
            this.carrier = carrier;
            this.availableSeats = availableSeats;
            this.hasWifi = hasWifi;
        }

        public Route getRoute() {
            return route;
        }

        @Override
        public String toString() {
            return route + "\n" +
                    "  Перевізник: " + carrier + "\n" +
                    "  Вільних місць: " + availableSeats + "\n" +
                    "  WiFi: " + (hasWifi ? "Так" : "Ні");
        }
    }

    // Пошук поїзда
    public static CompletableFuture<Route> searchTrainRoute(String from, String to) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Шукаємо маршрут на поїзді...");
            simulateDelay(1500);
            Route route = new Route("Поїзд", from, to, 450.00, 8.5, "09:30");
            System.out.println("[" + Thread.currentThread().getName() + "] Маршрут на поїзді знайдено");
            return route;
        });
    }

    // Пошук автобуса
    public static CompletableFuture<Route> searchBusRoute(String from, String to) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Шукаємо маршрут на автобусі...");
            simulateDelay(1200);
            Route route = new Route("Автобус", from, to, 350.00, 10.0, "08:00");
            System.out.println("[" + Thread.currentThread().getName() + "] Маршрут на автобусі знайдено");
            return route;
        });
    }

    // Пошук літака
    public static CompletableFuture<Route> searchFlightRoute(String from, String to) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() + "] Шукаємо авіарейси...");
            simulateDelay(2000);
            Route route = new Route("Літак", from, to, 1200.00, 1.5, "14:45");
            System.out.println("[" + Thread.currentThread().getName() + "] Авіарейс знайдено");
            return route;
        });
    }

    // Отримання детальної інформації (для поїзда та автобуса)
    public static CompletableFuture<RouteDetails> getRouteDetails(Route route) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() +
                    "] Отримуємо деталі для: " + route.getTransportType());
            simulateDelay(800);

            switch (route.getTransportType()) {
                case "Поїзд":
                    return new RouteDetails(route, "Укрзалізниця", 45, true);
                case "Автобус":
                    return new RouteDetails(route, "Автолюкс", 28, false);
                default:
                    return new RouteDetails(route, "Невідомо", 0, false);
            }
        });
    }

    // Перевірка доступності місць (використовують усі види транспорту)
    public static CompletableFuture<Integer> checkAvailability(Route route) {
        return CompletableFuture.supplyAsync(() -> {
            System.out.println("[" + Thread.currentThread().getName() +
                    "] Перевіряємо наявність місць для: " + route.getTransportType());
            simulateDelay(600);

            switch (route.getTransportType()) {
                case "Поїзд":
                    return 45;
                case "Автобус":
                    return 28;
                case "Літак":
                    return 12;
                default:
                    return 0;
            }
        });
    }

    private static void simulateDelay(int milliseconds) {
        try {
            TimeUnit.MILLISECONDS.sleep(milliseconds);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new RuntimeException("Операцію перервано", e);
        }
    }

    public static void main(String[] args) {
        System.out.println("\nСистема планування подорожі\n");

        String from = "Київ";
        String to = "Львів";

        System.out.println("Планування подорожі: " + from + " -> " + to + "\n");

        long startTime = System.currentTimeMillis();

        // Паралельний пошук
        CompletableFuture<Route> trainFuture = searchTrainRoute(from, to);
        CompletableFuture<Route> busFuture = searchBusRoute(from, to);
        CompletableFuture<Route> flightFuture = searchFlightRoute(from, to);

        // thenCompose для поїзда
        CompletableFuture<RouteDetails> trainDetailsFuture = trainFuture.thenCompose(route -> {
            System.out.println("\nОбробка маршруту на поїзді з thenCompose()");
            return getRouteDetails(route);
        });

        // thenCompose для автобуса
        CompletableFuture<RouteDetails> busDetailsFuture = busFuture.thenCompose(route -> {
            System.out.println("\nОбробка маршруту на автобусі з thenCompose()");
            return getRouteDetails(route);
        });

        // thenCombine для літака → повертає RouteDetails
        CompletableFuture<RouteDetails> flightDetailsFuture =
                flightFuture.thenCombine(
                        flightFuture.thenCompose(TravelPlanner::checkAvailability),
                        (route, seats) -> {
                            System.out.println("\nВикористання thenCombine() для об'єднання маршруту з даними про місця");
                            return new RouteDetails(route,
                                    "Ukraine International Airlines",
                                    seats,
                                    true);
                        });

        // allOf для очікування всіх
        CompletableFuture<Void> allRoutes = CompletableFuture.allOf(
                trainDetailsFuture,
                busDetailsFuture,
                flightDetailsFuture
        );

        allRoutes.thenRun(() -> {
            System.out.println("\nВсі маршрути знайдено — аналіз результатів");

            RouteDetails trainDetails = trainDetailsFuture.join();
            RouteDetails busDetails = busDetailsFuture.join();
            RouteDetails flightDetails = flightDetailsFuture.join();

            System.out.println("\nПОЇЗД:");
            System.out.println(trainDetails);
            System.out.println("  Оцінка: " + trainDetails.getRoute().getScore());

            System.out.println("\nАВТОБУС:");
            System.out.println(busDetails);
            System.out.println("  Оцінка: " + busDetails.getRoute().getScore());

            System.out.println("\nЛІТАК:");
            System.out.println(flightDetails);
            System.out.println("  Оцінка: " + flightDetails.getRoute().getScore());

            // Обираємо кращий маршрут
            List<Route> routes = List.of(
                    trainDetails.getRoute(),
                    busDetails.getRoute(),
                    flightDetails.getRoute()
            );

            Route bestRoute = routes.stream()
                    .min(Comparator.comparingDouble(Route::getScore))
                    .orElse(null);

            long endTime = System.currentTimeMillis();

            System.out.println("\nРекомендований маршрут:");
            if (bestRoute != null) {
                System.out.println(bestRoute);
            }

            System.out.println("\nЗагальний час пошуку та аналізу: " + (endTime - startTime) + " мс");
        }).join();

        // Демонстрація anyOf()
        System.out.println("\nДемонстрація anyOf() — пошук найшвидшої відповіді (Київ → Одеса)");

        CompletableFuture<Route> t2 = searchTrainRoute(from, "Одеса");
        CompletableFuture<Route> b2 = searchBusRoute(from, "Одеса");
        CompletableFuture<Route> f2 = searchFlightRoute(from, "Одеса");

        CompletableFuture<Object> first = CompletableFuture.anyOf(t2, b2, f2);

        first.thenAccept(result -> {
            Route r = (Route) result;
            System.out.println("\nПерша відповідь (anyOf):");
            System.out.println("  " + r);
        }).join();
    }
}
