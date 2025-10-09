import java.time.LocalTime;
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;

// Клас, що моделює паркінг з обмеженою кількістю місць

class Parking {
    private Semaphore semaphore;
    private int maxSpots;

    public Parking() {
        this.maxSpots = getSpotsBasedOnTime();
        this.semaphore = new Semaphore(maxSpots, true);
        System.out.println("\nПаркінг відкрито. Кількість місць: " + maxSpots);
    }

    //Метод визначає кількість місць залежно від часу доби

    private int getSpotsBasedOnTime() {
        int hour = LocalTime.now().getHour();
        // День: 06:00 - 20:59 → 5 місць
        // Ніч: 21:00 - 05:59 → 8 місць
        if (hour >= 6 && hour <= 20) {
            return 5;
        } else {
            return 8;
        }
    }

    //Метод для спроби припаркувати автомобіль

    public void parkCar(String carName) {
        try {
            System.out.println(carName + " під'їжджає до паркінгу...");
            semaphore.acquire(); // очікує, якщо всі місця зайняті
            System.out.println(carName + " припаркувався. Вільних місць: " + semaphore.availablePermits());
            TimeUnit.SECONDS.sleep((long) (Math.random() * 5 + 2)); // стоїть 2–7 секунд
            System.out.println(carName + " виїжджає з паркінгу.");
        } catch (InterruptedException e) {
            System.err.println(carName + " не зміг припаркуватися (помилка: " + e.getMessage() + ")");
            Thread.currentThread().interrupt();
        } finally {
            semaphore.release(); // звільняє місце
        }
    }
}
