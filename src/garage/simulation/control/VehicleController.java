package garage.simulation.control;

import garage.Garage;
import garage.GarageSpot;
import garage.utility.logging.GarageLogger;
import garage.vehicles.Vehicle;

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
        while (!finished) {
            try {
                sleep(sleepTimeInMillis);
            } catch (InterruptedException interrupted) {
                GarageLogger.log(Level.WARNING, "thread " + getName() + " interrupted", interrupted);
            }
            synchronized (jumpToNextSpotLocker) {
                if (running) {
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
                                    Garage.getVehicleHashMap().remove(controlledVehicle.getChassisNumber());
                                    movingVehicles.remove(this);
                                } else {
                                    if (current.getToPreviousPlatform() != null && current.getToPreviousPlatform().isFree() && !thereAreVehiclesOnTheRight(current, -1)) {
                                        next = current.getToPreviousPlatform();
                                    } else if (current.getToPreviousPlatform() != null && !current.getToPreviousPlatform().isFree()) {
                                        potentiallyCauseAnAccident(controlledVehicle, current.getToPreviousPlatform().getVehicle());
                                    }
                                }

                                break;
                            case PLACE_OF_ACCIDENT:
                                if (overtaking) {
                                    if (current.getPlatform().getPlatformIndex() < Accident.platform.getPlatformIndex())
                                        endOvertakingOrCarryOnIfSpotTaken(current, 1);
                                    else if (current.getPlatform().getPlatformIndex() > Accident.platform.getPlatformIndex())
                                        endOvertakingOrCarryOnIfSpotTaken(current, -1);
                                    else {
                                        endOvertakingOrCarryOnIfSpotTaken(current, 0);
                                        if (PathCalculator.distance(current, Accident.location) < 4) {
                                            Accident.checkIn();
                                            running = false;
                                        }
                                    }
                                } else {
                                    if (current.getPlatform().getPlatformIndex() < Accident.platform.getPlatformIndex()) {
                                        if (current.getToNextPlatform() != null && current.getToNextPlatform().isFree()) {
                                            next = current.getToNextPlatform();
                                            waitCount = 0;
                                        } else if (++waitCount > 5) {
                                            next = PathCalculator.getSpotToBeginOvertaking(current, 1);
                                            if (next != null && next.isFree()) {
                                                waitCount = 0;
                                                overtaking = true;
                                            } else if (next != null && ++waitCount > 10) {
                                                current.place(next.getVehicle());
                                                next.place(controlledVehicle);
                                                overtaking = true;
                                                next = null;
                                            } else
                                                next = null;
                                        }
                                    } else if (current.getPlatform().getPlatformIndex() > Accident.platform.getPlatformIndex()) {
                                        if (current.getToPreviousPlatform() != null && current.getToPreviousPlatform().isFree()) {
                                            next = current.getToPreviousPlatform();
                                            waitCount = 0;
                                        } else if (++waitCount > 5) {
                                            System.out.println(controlledVehicle.getType() + " " + waitCount);
                                            next = PathCalculator.getSpotToBeginOvertaking(current, -1);
                                            if (next != null && next.isFree()) {
                                                waitCount = 0;
                                                overtaking = true;
                                            } else if (next != null && ++waitCount > 10) {
                                                current.place(next.getVehicle());
                                                next.place(controlledVehicle);
                                                overtaking = true;
                                                next = null;
                                            } else
                                                next = null;
                                        }
                                    } else {
                                        if (PathCalculator.distance(current, Accident.location) < 4) {
                                            Accident.checkIn();
                                            running = false;
                                        } else {
                                            if (current.isParkingSpot() && current.getToPreviousPlatform() != null)
                                                next = current.getToPreviousPlatform();
                                            else if (current.getToTraverseCurrentPlatform() != null && current.getToTraverseCurrentPlatform().isFree())
                                                next = current.getToTraverseCurrentPlatform();
                                            else {
                                                next = PathCalculator.getSpotToBeginOvertaking(current, 0);
                                                if (next != null && next.isFree()) {
                                                    waitCount = 0;
                                                    overtaking = true;
                                                } else if (next != null && ++waitCount > 5) {
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

                        if (next != null) {
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
        if (actor2 != null && Accident.canHappen && probability < 0.1) {
            Accident.collide(actor1, actor2);
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
                }
            }
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

    public void setRunning(boolean running) {
        this.running = running;
    }

    public static Object getJumpToNextSpotLocker() {
        return jumpToNextSpotLocker;
    }

}
