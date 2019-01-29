package garage.simulation.control;


import garage.Garage;
import garage.GarageSpot;
import garage.Platform;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import garage.vehicles.firefighting.Firefighting;
import garage.vehicles.firefighting.FirefightingVan;
import garage.vehicles.police.Police;
import garage.vehicles.police.PoliceCar;
import garage.vehicles.police.PoliceMotorcycle;
import garage.vehicles.police.PoliceVan;
import garage.vehicles.sanitary.Sanitary;
import garage.vehicles.sanitary.SanitaryCar;
import garage.vehicles.sanitary.SanitaryVan;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;

public class Accident {

    private static Vehicle actor1;
    private static Vehicle actor2;

    public static Platform platform;
    public static GarageSpot location;

    private static Vehicle policeVehicleOnDuty;
    private static Vehicle sanitaryVehicleOnDuty;
    private static Vehicle firefightingVehicleOnDuty;

    private static int specialVehiclesAtCrashSiteCount;

    public static boolean happening = false;
    public static boolean canHappen = true;

    private static Lock locker = new ReentrantLock();

    public static void collide(Vehicle first, Vehicle second) {
        System.out.println("KABOOOOOOOM");
        canHappen = false;
        happening = true;
        actor1 = first;
        actor2 = second;
        platform = actor1.getLocation().getPlatform();
        location = actor1.getLocation();
        specialVehiclesAtCrashSiteCount = 0;
        System.out.println(platform + " (" + location.getxCoordinate() + ", " + location.getyCoordinate() + ")");

        actor1.setLabel("X");
        actor2.setLabel("X");

        temporarilyBlockTraffic();

        findSanitaryVehicleToSendToCrashSite();
        findFirefightingVehicleToSendToCrashSite();
        findPoliceVehicleToSendToCrashSite();
    }

    public static void checkIn() {
        if (++specialVehiclesAtCrashSiteCount == 3)
            resolve();
    }

    public static void resolve() {
        System.out.println("RESOLVED");
        happening = false;
        for (VehicleController vehicle : VehicleController.movingVehicles) {
            if (vehicle.getControlledVehicle() == sanitaryVehicleOnDuty
                    || vehicle.getControlledVehicle() == firefightingVehicleOnDuty
                    || vehicle.getControlledVehicle() == policeVehicleOnDuty) {
                vehicle.setTarget(MovementTarget.EXIT);
                vehicle.setRunning(true);
            }
        }
        ((Sanitary) sanitaryVehicleOnDuty).turnOffRotation();
        ((Firefighting) firefightingVehicleOnDuty).turnOffRotation();
        ((Police) policeVehicleOnDuty).turnOffRotation();
        new Thread(() -> {
            try {
                Thread.sleep(VehicleController.getSleepTimeInMillis() * 20);
                canHappen = true;
            } catch (InterruptedException interrupted) {
                GarageLogger.log(Level.WARNING, "thread interrupted", interrupted);
            }
        }).start();
    }

    private static void temporarilyBlockTraffic() {
        new Thread(() -> {
            while (happening) {
                synchronized (VehicleController.getJumpToNextSpotLocker()) {
                    for (VehicleController movingVehicle : VehicleController.movingVehicles) {
                        if (movingVehicle.getControlledVehicle().getLocation() != null
                                && (movingVehicle.getControlledVehicle().getLocation().getPlatform() == actor1.getLocation().getPlatform()
                                || movingVehicle.getControlledVehicle().getLocation().getPlatform() == actor2.getLocation().getPlatform())
                                && movingVehicle.getControlledVehicle() != sanitaryVehicleOnDuty
                                && movingVehicle.getControlledVehicle() != firefightingVehicleOnDuty
                                && movingVehicle.getControlledVehicle() != policeVehicleOnDuty) {
                            movingVehicle.setRunning(false);
                        }
                    }
                }
                try {
                    Thread.sleep(VehicleController.getSleepTimeInMillis());
                } catch (InterruptedException interrupted) {
                    GarageLogger.log(Level.WARNING, "thread interrupted", interrupted);
                }
            }
            synchronized (VehicleController.getJumpToNextSpotLocker()) {
                for (VehicleController movingVehicle : VehicleController.movingVehicles)
                    movingVehicle.setRunning(true);
            }
        }).start();
    }

    private static void findSanitaryVehicleToSendToCrashSite() {
        ArrayList<Vehicle> sanitaryVehiclesInGarage = new ArrayList<>();
        synchronized (VehicleController.getJumpToNextSpotLocker()) {
            for (Vehicle vehicle : Garage.getVehicleHashMap().values()) {
                if (vehicle instanceof Sanitary && vehicle != actor1 && vehicle != actor2)
                    sanitaryVehiclesInGarage.add(vehicle);
            }
            if (!sanitaryVehiclesInGarage.isEmpty()) {
                sanitaryVehiclesInGarage.sort(new Comparator<Vehicle>() {
                    @Override
                    public int compare(Vehicle o1, Vehicle o2) {
                        return PathCalculator.distance(o1.getLocation(), actor1.getLocation()) -
                                PathCalculator.distance(o2.getLocation(), actor1.getLocation());
                    }
                });
                sanitaryVehicleOnDuty = sanitaryVehiclesInGarage.get(0);
                ((Sanitary) sanitaryVehicleOnDuty).turnOnRotation();
                for (VehicleController controller : VehicleController.movingVehicles) {
                    if (controller.getControlledVehicle() == sanitaryVehicleOnDuty) {
                        System.out.println("                    SETOVANO");
                        controller.setTarget(MovementTarget.PLACE_OF_ACCIDENT);
                        return;
                    }
                }
                new VehicleController(sanitaryVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            } else {
                int type = ThreadLocalRandom.current().nextInt(2);
                switch (type) {
                    case 0:
                        sanitaryVehicleOnDuty = new SanitaryCar();
                        break;
                    case 1:
                        sanitaryVehicleOnDuty = new SanitaryVan();
                        break;
                }
                ((Sanitary) sanitaryVehicleOnDuty).turnOnRotation();
                int platformIndex;
                do {
                    platformIndex = ThreadLocalRandom.current().nextInt(Garage.getPlatforms().size());
                } while (Garage.getPlatforms().get(platformIndex).isFull());
                Garage.getPlatforms().get(platformIndex).findRandomPositionAndPark(sanitaryVehicleOnDuty);
                new VehicleController(sanitaryVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            }
        }
    }

    private static void findFirefightingVehicleToSendToCrashSite() {
        ArrayList<Vehicle> firefightingVehiclesInGarage = new ArrayList<>();
        synchronized (VehicleController.getJumpToNextSpotLocker()) {
            for (Vehicle vehicle : Garage.getVehicleHashMap().values()) {
                if (vehicle instanceof Firefighting && vehicle != actor1 && vehicle != actor2)
                    firefightingVehiclesInGarage.add(vehicle);
            }
            if (!firefightingVehiclesInGarage.isEmpty()) {
                firefightingVehiclesInGarage.sort(new Comparator<Vehicle>() {
                    @Override
                    public int compare(Vehicle o1, Vehicle o2) {
                        return PathCalculator.distance(o1.getLocation(), actor1.getLocation()) -
                                PathCalculator.distance(o2.getLocation(), actor1.getLocation());
                    }
                });
                firefightingVehicleOnDuty = firefightingVehiclesInGarage.get(0);
                ((Firefighting) firefightingVehicleOnDuty).turnOnRotation();
                for (VehicleController controller : VehicleController.movingVehicles) {
                    if (controller.getControlledVehicle() == firefightingVehicleOnDuty) {
                        System.out.println("                    SETOVANO");
                        controller.setTarget(MovementTarget.PLACE_OF_ACCIDENT);
                        return;
                    }
                }
                new VehicleController(firefightingVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            } else {
                firefightingVehicleOnDuty = new FirefightingVan();
                ((Firefighting) firefightingVehicleOnDuty).turnOnRotation();
                int platformIndex;
                do {
                    platformIndex = ThreadLocalRandom.current().nextInt(Garage.getPlatforms().size());
                } while (Garage.getPlatforms().get(platformIndex).isFull());
                Garage.getPlatforms().get(platformIndex).findRandomPositionAndPark(firefightingVehicleOnDuty);
                new VehicleController(firefightingVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            }
        }
    }

    private static void findPoliceVehicleToSendToCrashSite() {
        ArrayList<Vehicle> policeVehiclesInGarage = new ArrayList<>();
        synchronized (VehicleController.getJumpToNextSpotLocker()) {
            for (Vehicle vehicle : Garage.getVehicleHashMap().values()) {
                if (vehicle instanceof Police && vehicle != actor1 && vehicle != actor2)
                    policeVehiclesInGarage.add(vehicle);
            }
            if (!policeVehiclesInGarage.isEmpty()) {
                policeVehiclesInGarage.sort(new Comparator<Vehicle>() {
                    @Override
                    public int compare(Vehicle o1, Vehicle o2) {
                        return PathCalculator.distance(o1.getLocation(), actor1.getLocation()) -
                                PathCalculator.distance(o2.getLocation(), actor1.getLocation());
                    }
                });
                policeVehicleOnDuty = policeVehiclesInGarage.get(0);
                ((Police) policeVehicleOnDuty).turnOnRotation();
                for (VehicleController controller : VehicleController.movingVehicles) {
                    if (controller.getControlledVehicle() == policeVehicleOnDuty) {
                        System.out.println("                    SETOVANO");
                        controller.setTarget(MovementTarget.PLACE_OF_ACCIDENT);
                        return;
                    }
                }
                new VehicleController(policeVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            } else {
                int type = ThreadLocalRandom.current().nextInt(3);
                switch (type) {
                    case 0:
                        policeVehicleOnDuty = new PoliceCar();
                        break;
                    case 1:
                        policeVehicleOnDuty = new PoliceMotorcycle();
                        break;
                    case 2:
                        policeVehicleOnDuty = new PoliceVan();
                        break;
                }
                ((Police) policeVehicleOnDuty).turnOnRotation();
                int platformIndex;
                do {
                    platformIndex = ThreadLocalRandom.current().nextInt(Garage.getPlatforms().size());
                } while (Garage.getPlatforms().get(platformIndex).isFull());
                Garage.getPlatforms().get(platformIndex).findRandomPositionAndPark(policeVehicleOnDuty);
                new VehicleController(policeVehicleOnDuty, MovementTarget.PLACE_OF_ACCIDENT).start();
            }
        }
    }

    public static boolean involved(Vehicle vehicle) {
        return vehicle == sanitaryVehicleOnDuty || vehicle == firefightingVehicleOnDuty || vehicle == policeVehicleOnDuty;
    }

}
