package garage.simulation.control;

import garage.vehicles.Vehicle;
import garage.vehicles.civil.Car;
import garage.vehicles.civil.Motorcycle;
import garage.vehicles.civil.Van;
import garage.vehicles.firefighting.FirefightingVan;
import garage.vehicles.police.PoliceCar;
import garage.vehicles.police.PoliceMotorcycle;
import garage.vehicles.police.PoliceVan;
import garage.vehicles.sanitary.SanitaryCar;
import garage.vehicles.sanitary.SanitaryVan;

import java.util.concurrent.ThreadLocalRandom;

public class RandomVehicleGenerator {

    public static Vehicle generateNewRandomVehicle() {
        Vehicle vehicle = null;
        int typeFlag;
        double typeProbability = ThreadLocalRandom.current().nextDouble();
        if (typeProbability < 0.9) {
            typeFlag = ThreadLocalRandom.current().nextInt(3);
            switch (typeFlag) {
                case 0:
                    vehicle = new Car();
                    break;
                case 1:
                    vehicle = new Motorcycle();
                    break;
                case 2:
                    vehicle = new Van();
                    break;
            }
        } else {
            typeFlag = ThreadLocalRandom.current().nextInt(6);
            switch (typeFlag) {
                case 0:
                    vehicle = new PoliceCar();
                    break;
                case 1:
                    vehicle = new PoliceMotorcycle();
                    break;
                case 2:
                    vehicle = new PoliceVan();
                    break;
                case 3:
                    vehicle = new SanitaryCar();
                    break;
                case 4:
                    vehicle = new SanitaryVan();
                    break;
                case 5:
                    vehicle = new FirefightingVan();
                    break;
            }
        }
        return vehicle;
    }

}
