package ru.ifmo.practice.seabattle.battle;

import java.util.HashSet;
import java.util.Random;

public class ShipPlacement {
    private HashSet<Coordinates> blackList = new HashSet<>();

    public Field getRandomPlacement() {
        Cell[][] cells = Field.getVoidField().getCells();

        for (int i = 1; i <= 4; i++) {
            int n = 5 - i;  // n - Количество клеток в корабле
                            // i - Количество n-клеточных кораблей

            for (int j = 0; j < i; j++) {
                Coordinates[] shipCoordinates = getRandomCoordinatesOfShip(n);
                for(Coordinates coordinates : shipCoordinates) {
                    cells[coordinates.getX()][coordinates.getY()] = Cell.getShipCell();
                }
            }
        }

        return new Field(cells);
    }

    private Coordinates[] getRandomCoordinatesOfShip(int cellNumber) {
        Random random = new Random();
        boolean isPlaced = false;
        Coordinates[] shipCoordinates = new Coordinates[cellNumber];

        do {
            Route route;
            int x = random.nextInt(10), y;

            if (10 - x < cellNumber) {
                y = random.nextInt(10 - cellNumber + 1);
                route = Route.Horisontal;
            } else {
                y = random.nextInt(10);
                if (10 - y < cellNumber) {
                    route = Route.Vertical;
                } else {
                    route = Route.getRoute(random.nextInt(2));
                }
            }

            Coordinates startingPoint = new Coordinates(x, y);

            if (!blackList.contains(startingPoint)) {
                shipCoordinates = getShipInRoute(startingPoint, route, cellNumber);
                if (shipCoordinates != null) isPlaced = true;
            }
        } while (!isPlaced);

        for (Coordinates coordinates : shipCoordinates) blackList.add(coordinates);
        return shipCoordinates;
    }

    private Coordinates[] getShipInRoute(Coordinates startingPoint, Route route, int cellNumber) {
        Coordinates[] shipCoordinates = new Coordinates[cellNumber];
        shipCoordinates[0] = startingPoint;

        if (route == Route.Horisontal) {
            for (int k = 1; k < cellNumber; k++) {
                shipCoordinates[k] = new Coordinates(startingPoint.getX(), startingPoint.getY() + k);
                if (blackList.contains(shipCoordinates[k])) {
                    return null;
                }
            }
        } else {
            for (int k = 1; k < cellNumber; k++) {
                shipCoordinates[k] = new Coordinates(startingPoint.getX() + k, startingPoint.getY());
                if (blackList.contains(shipCoordinates[k])) {
                    return null;
                }
            }
        }

        return shipCoordinates;
    }

    private enum Route {
        Vertical, Horisontal;

        public static Route getRoute(int n) throws IllegalArgumentException {
            if (n < 0 || n > 1) throw new IllegalArgumentException();
            else if (n == 0) {
                return Vertical;
            } else {
                return Horisontal;
            }
        }
    }
}
