//Клас, що представляє автомобіль (потік)

class Car implements Runnable {
    private String name;
    private Parking parking;

    public Car(String name, Parking parking) {
        this.name = name;
        this.parking = parking;
    }

    @Override
    public void run() {
        parking.parkCar(name);
    }
}
