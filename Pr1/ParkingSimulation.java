import java.util.concurrent.TimeUnit;

//Головний клас програми

public class ParkingSimulation {
    public static void main(String[] args) {
        System.out.println("\nСИМУЛЯЦІЯ ПАРКУВАННЯ");
        Parking parking = new Parking();
        System.out.println();

        // Створюємо 12 машин
        Thread[] cars = new Thread[12];
        for (int i = 0; i < cars.length; i++) {
            cars[i] = new Thread(new Car("Автомобіль " + (i + 1), parking));
        }

        // Запускаємо потоки з невеликою затримкою
        for (Thread car : cars) {
            car.start();
            try {
                TimeUnit.MILLISECONDS.sleep(500); // затримка перед приїздом наступного авто
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        // Чекаємо завершення всіх потоків
        for (Thread car : cars) {
            try {
                car.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        System.out.println("\nПаркування завершено. Всі автомобілі виїхали.");
    }
}
