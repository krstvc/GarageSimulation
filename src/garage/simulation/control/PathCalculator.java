package garage.simulation.control;

import garage.Garage;
import garage.GarageSpot;
import garage.Platform;

public class PathCalculator {

    public static void calculatePaths() {
        calculateFindParkingPaths();
        calculateNearbyParkingSpotsPaths();
        calculateNeighborPaths();
        calculateNextPlatformPaths();
        calculatePreviousPlatformPaths();
    }

    private static void calculateFindParkingPaths() {
        for (Platform platform : Garage.getPlatforms()) {
            for (int i = 0; i <= 8; ++i) {
                platform.getSpotAt(1, i).setToTraverseCurrentPlatform(platform.getSpotAt(1, i + 1));
                platform.getSpotAt(6, i + 1).setToTraverseCurrentPlatform(platform.getSpotAt(6, i));
            }
            for (int i = 2; i <= 8; ++i) {
                platform.getSpotAt(2, i).setToTraverseCurrentPlatform(platform.getSpotAt(2, i - 1));
                platform.getSpotAt(5, i - 1).setToTraverseCurrentPlatform(platform.getSpotAt(5, i));
            }
            for (int i = 2; i <= 4; ++i) {
                platform.getSpotAt(i, 1).setToTraverseCurrentPlatform(platform.getSpotAt(i + 1, 1));
                platform.getSpotAt(i + 1, 8).setToTraverseCurrentPlatform(platform.getSpotAt(i, 8));
            }
            for (int i = 2; i <= 6; ++i) {
                platform.getSpotAt(i, 0).setToTraverseCurrentPlatform(platform.getSpotAt(i - 1, 0));
                platform.getSpotAt(i - 1, 9).setToTraverseCurrentPlatform(platform.getSpotAt(i, 9));
            }
            platform.getSpotAt(7, 0).setToTraverseCurrentPlatform(platform.getSpotAt(6, 0));
            platform.getSpotAt(0, 1).setToTraverseCurrentPlatform(platform.getSpotAt(1, 1));

            if (platform.getPlatformIndex() != 0)
                platform.getSpotAt(0, 0).setToTraverseCurrentPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 0)
                );

            if (platform.getPlatformIndex() != Garage.getPlatforms().size() - 1)
                platform.getSpotAt(7, 1).setToTraverseCurrentPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 1)
                );
        }
        Garage.getPlatforms().get(0).getSpotAt(0, 0).setToTraverseCurrentPlatform(
                Garage.getPlatforms().get(0).getSpotAt(0, 1)
        );
    }

    private static void calculateNextPlatformPaths() {
        for (Platform platform : Garage.getPlatforms()) {
            for (int i = 2; i <= 9; ++i) {
                platform.getSpotAt(0, i).setToNextPlatform(platform.getSpotAt(1, i));
                platform.getSpotAt(6, i).setToNextPlatform(platform.getSpotAt(6, i - 1));
                platform.getSpotAt(7, i).setToNextPlatform(platform.getSpotAt(6, i));
            }
            for (int i = 2; i <= 7; ++i) {
                platform.getSpotAt(2, i).setToNextPlatform(platform.getSpotAt(2, i - 1));
                platform.getSpotAt(5, i).setToNextPlatform(platform.getSpotAt(5, i + 1));
                platform.getSpotAt(3, i).setToNextPlatform(platform.getSpotAt(2, i));
                platform.getSpotAt(4, i).setToNextPlatform(platform.getSpotAt(5, i));
            }
            for (int i = 0; i <= 6; ++i) {
                platform.getSpotAt(i, 1).setToNextPlatform(platform.getSpotAt(i + 1, 1));
                platform.getSpotAt(1, i + 2).setToNextPlatform(platform.getSpotAt(1, i + 3));
            }
            for (int i = 1; i <= 5; ++i) {
                platform.getSpotAt(i, 9).setToNextPlatform(platform.getSpotAt(i + 1, 9));
            }
            for (int i = 3; i <= 5; ++i) {
                platform.getSpotAt(i, 8).setToNextPlatform(platform.getSpotAt(i - 1, 8));
            }
            for (int i = 2; i <= 4; ++i) {
                platform.getSpotAt(i, 0).setToNextPlatform(platform.getSpotAt(i - 1, 0));
            }
            platform.getSpotAt(1, 0).setToNextPlatform(platform.getSpotAt(1, 1));
            platform.getSpotAt(5, 0).setToNextPlatform(platform.getSpotAt(5, 1));
            platform.getSpotAt(6, 0).setToNextPlatform(platform.getSpotAt(5, 0));
            platform.getSpotAt(7, 0).setToNextPlatform(platform.getSpotAt(6, 0));
            platform.getSpotAt(2, 8).setToNextPlatform(platform.getSpotAt(2, 7));

            if (platform.getPlatformIndex() != 0)
                platform.getSpotAt(0, 0).setToNextPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 0)
                );

            if (platform.getPlatformIndex() != Garage.getPlatforms().size() - 1)
                platform.getSpotAt(7, 1).setToNextPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 1)
                );
        }
        Garage.getPlatforms().get(0).getSpotAt(0, 0).setToNextPlatform(
                Garage.getPlatforms().get(0).getSpotAt(0, 1)
        );
    }

    private static void calculateNearbyParkingSpotsPaths() {
        for (Platform platform : Garage.getPlatforms()) {
            for (int i = 2; i <= 9; ++i) {
                platform.getSpotAt(1, i).setToParkingSpotNearby(platform.getSpotAt(0, i));
                platform.getSpotAt(6, i).setToParkingSpotNearby(platform.getSpotAt(7, i));
            }
            for (int i = 2; i <= 7; ++i) {
                platform.getSpotAt(2, i).setToParkingSpotNearby(platform.getSpotAt(3, i));
                platform.getSpotAt(5, i).setToParkingSpotNearby(platform.getSpotAt(4, i));
            }
        }
    }

    private static void calculateNeighborPaths() {
        for (Platform platform : Garage.getPlatforms()) {
            for (int i = 2; i <= 7; ++i) {
                platform.getSpotAt(1, i).setToNeighborSpot(platform.getSpotAt(2, i));
                platform.getSpotAt(2, i).setToNeighborSpot(platform.getSpotAt(1, i));
                platform.getSpotAt(5, i).setToNeighborSpot(platform.getSpotAt(6, i));
                platform.getSpotAt(6, i).setToNeighborSpot(platform.getSpotAt(5, i));
            }
            platform.getSpotAt(2, 8).setToNeighborSpot(platform.getSpotAt(1, 8));
            platform.getSpotAt(5, 8).setToNeighborSpot(platform.getSpotAt(6, 8));
        }
    }

    private static void calculatePreviousPlatformPaths() {
        for (Platform platform : Garage.getPlatforms()) {
            for (int i = 1; i <= 9; ++i) {
                platform.getSpotAt(0, i).setToPreviousPlatform(platform.getSpotAt(1, i));
                platform.getSpotAt(6, i).setToPreviousPlatform(platform.getSpotAt(6, i - 1));
            }
            for (int i = 1; i <= 8; ++i) {
                platform.getSpotAt(1, i).setToPreviousPlatform(platform.getSpotAt(1, i + 1));
                platform.getSpotAt(2, i).setToPreviousPlatform(platform.getSpotAt(2, i - 1));
                platform.getSpotAt(7, i + 1).setToPreviousPlatform(platform.getSpotAt(6, i + 1));
            }
            for (int i = 2; i <= 7; ++i) {
                platform.getSpotAt(3, i).setToPreviousPlatform(platform.getSpotAt(2, i));
                platform.getSpotAt(4, i).setToPreviousPlatform(platform.getSpotAt(5, i));
                platform.getSpotAt(5, i).setToPreviousPlatform(platform.getSpotAt(5, i + 1));
            }
            for (int i = 3; i <= 5; ++i) {
                platform.getSpotAt(i, 1).setToPreviousPlatform(platform.getSpotAt(i + 1, 1));
                platform.getSpotAt(i, 8).setToPreviousPlatform(platform.getSpotAt(i - 1, 8));
            }
            for (int i = 1; i <= 5; ++i) {
                platform.getSpotAt(i, 9).setToPreviousPlatform(platform.getSpotAt(i + 1, 9));
            }
            for (int i = 1; i <= 7; ++i) {
                platform.getSpotAt(i, 0).setToPreviousPlatform(platform.getSpotAt(i - 1, 0));
            }
            if (platform.getPlatformIndex() != 0)
                platform.getSpotAt(0, 0).setToPreviousPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 0)
                );

            if (platform.getPlatformIndex() != Garage.getPlatforms().size() - 1)
                platform.getSpotAt(7, 1).setToPreviousPlatform(
                        Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 1)
                );
        }
    }

    public static GarageSpot getSpotToWaitFor(GarageSpot from, int direction) {
        int x = from.getxCoordinate();
        int y = from.getyCoordinate();

        if ((x == 1 || x == 5) && (y >= 2 && y <= 6) && direction == 0)
            return from.getPlatform().getSpotAt(x + 1, y + 1);
        if ((x == 2 || x == 6) && (y >= 3 && y <= 7) && direction == 0)
            return from.getPlatform().getSpotAt(x - 1, y - 1);
        if (x == 2 && y == 2 && direction == -1)
            return from.getPlatform().getSpotAt(4, 0);
        if (x == 6 && y == 2 && direction == -1)
            return from.getPlatform().getSpotAt(7, 0);
        if (x == 2 && y == 1 && direction == -1)
            return from.getPlatform().getSpotAt(3, 0);
        if (x == 6 && y == 1 && direction == -1)
            return from.getPlatform().getSpotAt(7, 0);
        if (x == 1 && y == 1 && direction == 1)
            return from.getPlatform().getSpotAt(2, 2);
        if (x == 5 && y == 1 && direction == 1)
            return from.getPlatform().getSpotAt(6, 2);
        return null;    //never null, just avoiding 'else'
    }

    public static GarageSpot getSpotToBeginOvertaking(GarageSpot from, int direction) {
        Platform platform = from.getPlatform();
        int x = from.getxCoordinate();
        int y = from.getyCoordinate();

        if ((x == 1 || x == 5) && (y >= 2 && y <= 7))
            return platform.getSpotAt(x + 1, y + 1);
        if ((x == 2 || x == 6) && (y >= 2 && y <= 8))
            return platform.getSpotAt(x - 1, y - 1);
        if ((x >= 1 && x <= 4) && y == 9)
            return platform.getSpotAt(x + 1, y - 1);
        if ((x >= 3 && x <= 5) && y == 8)
            return platform.getSpotAt(x - 1, y + 1);
        if ((x == 3 || x == 4 || x == 7) && y == 0)
            return platform.getSpotAt(x - 1, y + 1);
        if ((x == 0 || x == 3 || x == 4) && y == 1)
            return platform.getSpotAt(x + 1, y - 1);
        if (x == 0 && (y >= 2 && y <= 9))
            return platform.getSpotAt(x + 1, y);
        if (x == 7 && (y >= 2 && y <= 9))
            return platform.getSpotAt(x - 1, y);
        if (x == 3 && (y >= 2 && y <= 7))
            return platform.getSpotAt(x - 1, y);
        if (x == 4 && (y >= 2 && y <= 7))
            return platform.getSpotAt(x + 1, y);
        if (x == 1 && y == 8)
            return platform.getSpotAt(x + 1, y);
        if ((x == 5 || x == 6) && y == 9)
            return platform.getSpotAt(5, 8);
        if (x == 5 && y == 0)
            return platform.getSpotAt(4, 1);
        if (x == 0 && y == 0)
            return Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 1);
        if (x == 7 && y == 1)
            return Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 0);
        if (x == 1 && y == 0) {
            if (direction == -1)
                return platform.getSpotAt(0, 1);
            return platform.getSpotAt(2, 1);
        }
        if (x == 2 && y == 0) {
            if (direction == -1)
                return platform.getSpotAt(1, 1);
            return platform.getSpotAt(2, 1);
        }
        if (x == 1 && y == 1) {
            if (direction == 1)
                return platform.getSpotAt(2, 0);
            return platform.getSpotAt(2, 2);
        }
        if (x == 2 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(1, 1);
            return platform.getSpotAt(3, 1);
        }
        if (x == 6 && y == 0) {
            if (direction == 0)
                return platform.getSpotAt(6, 1);
            return platform.getSpotAt(5, 1);
        }
        if (x == 5 && y == 1) {
            if (direction == 1)
                return platform.getSpotAt(6, 0);
            return platform.getSpotAt(6, 2);
        }
        if (x == 6 && y == 1) {
            if (direction == 1)
                return platform.getSpotAt(7, 0);
            return platform.getSpotAt(5, 1);
        }
        return null;    //never null, just avoiding 'else'
    }

    public static GarageSpot getSpotToContinueOvertaking(GarageSpot from, int direction) {
        Platform platform = from.getPlatform();
        int x = from.getxCoordinate();
        int y = from.getyCoordinate();

        if ((x == 1 || x == 5) && (y >= 2 && y <= 8))
            return platform.getSpotAt(x, y - 1);
        if ((x == 2 || x == 6) && (y >= 2 && y <= 7))
            return platform.getSpotAt(x, y + 1);
        if ((x >= 2 && x <= 6) && y == 9)
            return platform.getSpotAt(x - 1, y);
        if ((x >= 2 && x <= 4) && y == 8)
            return platform.getSpotAt(x + 1, y);
        if (x == 1 && y == 9)
            return platform.getSpotAt(1, 8);
        if (x == 6 && y == 8)
            return platform.getSpotAt(6, 9);
        if ((x >= 0 && x <= 5 && x != 2) && y == 0)
            return platform.getSpotAt(x + 1, y);
        if ((x == 3 || x == 4 || x == 7) && y == 1)
            return platform.getSpotAt(x - 1, y);
        if (x == 0 && y == 1)
            return Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 1);
        if (x == 7 && y == 0)
            return Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 0);
        if (x == 2 && y == 0) {
            if (direction == 1)
                return platform.getSpotAt(3, 0);
            return platform.getSpotAt(2, 1);
        }
        if (x == 6 && y == 0) {
            if (direction == 1)
                return platform.getSpotAt(7, 0);
            return platform.getSpotAt(6, 1);
        }
        if (x == 1 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(0, 1);
            return platform.getSpotAt(1, 0);
        }
        if (x == 2 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(1, 1);
            return platform.getSpotAt(2, 2);
        }
        if (x == 5 && y == 1) {
            if (direction == 1)
                return platform.getSpotAt(5, 0);
            return platform.getSpotAt(4, 1);
        }
        if (x == 6 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(5, 1);
            return platform.getSpotAt(6, 2);
        }
        return null;    //never null, just avoiding 'else'
    }

    public static GarageSpot getSpotToEndOvertaking(GarageSpot from, int direction) {
        Platform platform = from.getPlatform();
        int x = from.getxCoordinate();
        int y = from.getyCoordinate();

        if ((x >= 0 && x <= 6 && x != 1) && y == 0)
            return platform.getSpotAt(x + 1, y + 1);
        if ((x == 1 || x == 5) && (y >= 2 && y <= 8))
            return platform.getSpotAt(x + 1, y - 1);
        if ((x == 2 || x == 6) && (y >= 2 && y <= 7))
            return platform.getSpotAt(x - 1, y + 1);
        if ((x >= 2 && x <= 4) && y == 8)
            return platform.getSpotAt(x + 1, y + 1);
        if ((x >= 3 && x <= 5) && (y == 9 || y == 1))
            return platform.getSpotAt(x - 1, y - 1);
        if (x == 1 && y == 1)
            return platform.getSpotAt(0, 0);
        if (x == 7 && y == 1)
            return platform.getSpotAt(6, 0);
        if (x == 0 && y == 1)
            return Garage.getPlatforms().get(platform.getPlatformIndex() - 1).getSpotAt(7, 0);
        if (x == 7 && y == 0)
            return Garage.getPlatforms().get(platform.getPlatformIndex() + 1).getSpotAt(0, 1);
        if (x == 2 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(1, 0);
            return platform.getSpotAt(1, 2);
        }
        if (x == 6 && y == 1) {
            if (direction == -1)
                return platform.getSpotAt(5, 0);
            return platform.getSpotAt(5, 2);
        }
        return null;
    }

    public static int distance(GarageSpot from, GarageSpot to) {
        return Math.abs(
                from.getPlatform().getPlatformIndex() * 8 + from.getxCoordinate()
                        - to.getPlatform().getPlatformIndex() * 8 - to.getxCoordinate()) +
                Math.abs(from.getyCoordinate() - to.getyCoordinate());
    }

}
