package garage.simulation.control;

import garage.Garage;
import garage.GarageSpot;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;
import garage.vehicles.firefighting.Firefighting;
import garage.vehicles.police.Police;
import garage.vehicles.sanitary.Sanitary;

import java.io.IOException;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;

public class VehicleController extends Thread {

    private Vehicle controlledVehicle;
    private MovementTarget target;
    private GarageSpot next = null;
    private static long sleepTimeInMillis = 500;
    private int waitCount = 0;

    private boolean finished = false;
    private boolean running = true;
    private boolean overtaking = false;

    public static CopyOnWriteArrayList<VehicleController> movingVehicles = new CopyOnWriteArrayList<>();

    private static Object jumpToNextSpotLocker = new Object();


    public VehicleController(Vehicle vehicle, MovementTarget target) {
        super();
        controlledVehicle = vehicle;
        this.target = target;
    }

    @Override
    public void run() {
        movingVehicles.add(this);
        while (!finished) {     //thread is alive until the vehicle reaches its destination
            try {
                sleep(sleepTimeInMillis);
            } catch (InterruptedException interrupted) {
                GarageLogger.log(Level.WARNING, "thread " + getName() + " interrupted", interrupted);
            }
            synchronized (jumpToNextSpotLocker) {       //only one vehicle at a time is allowed to choose a spot to jump to
                if (running) {      //busy waiting
                    if (controlledVehicle instanceof Police && Emergency.canHappen) {
                        checkForWantedVehicles();
                    }
                    GarageSpot current = controlledVehicle.getLocation();
                    if (current != null) {
                        next = null;
                        switch (target) {
                            case PARKING_SPOT:
                                if (current.getToParkingSpotNearby() != null && current.getToParkingSpotNearby().isFree()) {
                                    next = current.getToParkingSpotNearby();
                                } else if (current.getToNeighborSpot() != null && current.getToNeighborSpot().getToParkingSpotNearby().isFree()) {
                                    if (current.getToNeighborSpot().isFree() && !thereAreVehiclesOnTheRight(current, 0)) {
                                        next = current.getToNeighborSpot();
                                    } else if (!current.getToNeighborSpot().isFree()) {
                                        potentiallyCauseAnAccident(controlledVehicle, current.getToNeighborSpot().getVehicle());
                                    }
                                } else if (current.getPlatform().isFull()) {
                                    if (current.getToNextPlatform() != null && current.getToNextPlatform().isFree() && !thereAreVehiclesOnTheRight(current, 1)) {
                                        next = current.getToNextPlatform();
                                    } else if (current.getToNextPlatform() != null && !current.getToNextPlatform().isFree()) {
                                        potentiallyCauseAnAccident(controlledVehicle, current.getToNextPlatform().getVehicle());
                                    }
                                } else {
                                    if (current.getToTraverseCurrentPlatform() != null && current.getToTraverseCurrentPlatform().isFree() && !thereAreVehiclesOnTheRight(current, 0)) {
                                        next = current.getToTraverseCurrentPlatform();
                                    } else if (current.getToTraverseCurrentPlatform() != null && !current.getToTraverseCurrentPlatform().isFree()) {
                                        potentiallyCauseAnAccident(controlledVehicle, current.getToTraverseCurrentPlatform().getVehicle());
                                    }
                                }
                                break;

                            case EXIT:
                                if (current == Garage.getPlatforms().get(0).getSpotAt(0, 0)) {
                                    finished = true;
                                    next = null;
                                    current.leave();
                                    Garage.getVehicleHashMap().remove(controlledVehicle.getRegistrationNumber());
                                    if (!(controlledVehicle instanceof Police) && !(controlledVehicle instanceof Sanitary) && !(controlledVehicle instanceof Firefighting))
                                        Garage.chargeOnExit(controlledVehicle);
                                    movingVehicles.remove(this);
                                } else {
                                    if (current.getToPreviousPlatform() != null && current.getToPreviousPlatform().isFree() && !thereAreVehiclesOnTheRight(current, -1)) {
                                        next = current.getToPreviousPlatform();
                                    } else if (current.getToPreviousPlatform() != null && !current.getToPreviousPlatform().isFree()) {
                                        potentiallyCauseAnAccident(controlledVehicle, current.getToPreviousPlatform().getVehicle());
                                    }
                                }

                                break;
                            case EMERGENCY_LOCATION:
                                if (overtaking) {       //if the vehicle is overtaking, first check if it is possible to return to right lane
                                    if (current.getPlatform().getPlatformIndex() < Emergency.platform.getPlatformIndex())
                                        endOvertakingOrCarryOnIfSpotTaken(current, 1);      //1 indicates movement towards next platform
                                    else if (current.getPlatform().getPlatformIndex() > Emergency.platform.getPlatformIndex())
                                        endOvertakingOrCarryOnIfSpotTaken(current, -1);     //-1 indicates movement towards previous platform
                                    else {
                                        endOvertakingOrCarryOnIfSpotTaken(current, 0);      //0 indicates movement around current platform
                                        if (PathCalculator.distance(current, Emergency.location) < 4) {     //4 spots distance is considered 'close enough'
                                            Emergency.checkIn();
                                            running = false;
                                        }
                                    }
                                } else {
                                    if (current.getPlatform().getPlatformIndex() < Emergency.platform.getPlatformIndex()) {
                                        if (current.getToNextPlatform() != null && current.getToNextPlatform().isFree()) {
                                            next = current.getToNextPlatform();
                                            waitCount = 0;
                                        } else if (++waitCount > 5) {       //if locked in place for 5 cycles, it should start overtaking
                                            next = PathCalculator.getSpotToBeginOvertaking(current, 1);
                                            if (next != null && next.isFree()) {
                                                waitCount = 0;
                                                overtaking = true;
                                            } else if (next != null && ++waitCount > 10) {      //if it is not possible to overtake, switch places with the vehicle in the way
                                                current.place(next.getVehicle());
                                                next.place(controlledVehicle);
                                                overtaking = true;
                                                next = null;
                                            } else
                                                next = null;
                                        }
                                    } else if (current.getPlatform().getPlatformIndex() > Emergency.platform.getPlatformIndex()) {
                                        if (current.getToPreviousPlatform() != null && current.getToPreviousPlatform().isFree()) {
                                            next = current.getToPreviousPlatform();
                                            waitCount = 0;
                                        } else if (++waitCount > 5) {       //if locked in place for 5 cycles, it should start overtaking
                                            next = PathCalculator.getSpotToBeginOvertaking(current, -1);
                                            if (next != null && next.isFree()) {
                                                waitCount = 0;
                                                overtaking = true;
                                            } else if (next != null && ++waitCount > 10) {      //if it is not possible to overtake, switch places with the vehicle in the way
                                                current.place(next.getVehicle());
                                                next.place(controlledVehicle);
                                                overtaking = true;
                                                next = null;
                                            } else
                                                next = null;
                                        }
                                    } else {
                                        if (PathCalculator.distance(current, Emergency.location) < 4) {     //4 spots distance is considered 'close enough'
                                            Emergency.checkIn();
                                            running = false;
                                        } else {
                                            if (current.isParkingSpot() && current.getToPreviousPlatform() != null)
                                                next = current.getToPreviousPlatform();
                                            else if (current.getToTraverseCurrentPlatform() != null && current.getToTraverseCurrentPlatform().isFree())
                                                next = current.getToTraverseCurrentPlatform();
                                            else {          //no need to wait for overtaking because there is no movement on the platform
                                                next = PathCalculator.getSpotToBeginOvertaking(current, 0);
                                                if (next != null && next.isFree()) {
                                                    waitCount = 0;
                                                    overtaking = true;
                                                } else if (next != null && ++waitCount > 5) {       //switch places with vehicle in the way
                                                    current.place(next.getVehicle());
                                                    next.place(controlledVehicle);
                                                    overtaking = true;
                                                    next = null;
                                                } else
                                                    next = null;
                                            }
                                        }
                                    }
                                }
                        }

                        if (next != null) {     //if found a spot, jump to it, wait for another cycle otherwise
                            current.leave();
                            if (current.isParkingSpot())
                                current.getPlatform().getFreeParkingSpots().add(current);

                            next.place(controlledVehicle);
                            if (next.isParkingSpot()) {
                                next.getPlatform().getFreeParkingSpots().remove(next);
                                finished = true;
                            }
                        }
                    }
                }
            }
        }

        movingVehicles.remove(this);
    }

    private boolean thereAreVehiclesOnTheRight(GarageSpot current, int direction) {
        return PathCalculator.getSpotToWaitFor(current, direction) != null && !PathCalculator.getSpotToWaitFor(current, direction).isFree();
    }

    private void potentiallyCauseAnAccident(Vehicle actor1, Vehicle actor2) {
        double probability = ThreadLocalRandom.current().nextDouble();
        if (actor2 != null && Emergency.canHappen && probability < 0.1) {
            Emergency.collide(actor1, actor2);
            setRunning(false);
            for (VehicleController movingVehicle : movingVehicles) {
                if (movingVehicle.getControlledVehicle() == actor2) {
                    movingVehicle.setRunning(false);
                    break;
                }
            }
        }
    }

    private void endOvertakingOrCarryOnIfSpotTaken(GarageSpot current, int direction) {
        GarageSpot spotToReturnToRightLane = PathCalculator.getSpotToEndOvertaking(current, direction);
        if (spotToReturnToRightLane != null && spotToReturnToRightLane.isFree()) {
            next = spotToReturnToRightLane;
            overtaking = false;
        } else {
            GarageSpot spotToContinueOvertaking = PathCalculator.getSpotToContinueOvertaking(current, direction);
            if (spotToContinueOvertaking != null && spotToContinueOvertaking.isFree()) {
                next = spotToContinueOvertaking;
            } else if (spotToContinueOvertaking != null) {
                if (++waitCount > 10) {
                    current.place(spotToContinueOvertaking.getVehicle());
                    spotToContinueOvertaking.place(controlledVehicle);
                    if (Emergency.involved(current.getVehicle())) {
                        for (VehicleController movingVehicle : VehicleController.movingVehicles) {
                            if (movingVehicle.getControlledVehicle() == current.getVehicle()) {
                                movingVehicle.overtaking = false;
                                return;
                            }
                        }
                    }
                }
            }
        }
    }

    private void checkForWantedVehicles() {
        try {
            List<String> wantedVehiclesRegistrationNumbers = Files.readAllLines(Police.WANTED_VEHICLES_FILE.toPath());
            for (String registrationNumber : wantedVehiclesRegistrationNumbers) {
                if (Garage.getVehicleHashMap().containsKey(registrationNumber)
                        && !(Garage.getVehicleHashMap().get(registrationNumber) instanceof Police)
                        && !(Garage.getVehicleHashMap().get(registrationNumber) instanceof Sanitary)
                        && !(Garage.getVehicleHashMap().get(registrationNumber) instanceof Firefighting)) {
                    Emergency.trackDownWantedVehicle(controlledVehicle, Garage.getVehicleHashMap().get(registrationNumber));
                    target = MovementTarget.EMERGENCY_LOCATION;
                }
            }
        } catch (IOException exception) {
            GarageLogger.log(Level.WARNING, "i/o exception occurred", exception);
        }
    }


    public Vehicle getControlledVehicle() {
        return controlledVehicle;
    }

    public MovementTarget getTarget() {
        return target;
    }

    public void setTarget(MovementTarget target) {
        this.target = target;
    }

    public static long getSleepTimeInMillis() {
        return sleepTimeInMillis;
    }

    public static void setSleepTimeInMillis(long sleepTimeInMillis) {
        VehicleController.sleepTimeInMillis = sleepTimeInMillis;
    }

    public void setFinished(boolean finished) {
        this.finished = finished;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public static Object getJumpToNextSpotLocker() {
        return jumpToNextSpotLocker;
    }

}
