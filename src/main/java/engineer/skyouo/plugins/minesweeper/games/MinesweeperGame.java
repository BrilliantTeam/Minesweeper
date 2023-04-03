package engineer.skyouo.plugins.minesweeper.games;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

public class MinesweeperGame {
    public static void main(String[] args) {
        MinesweeperGame game = new MinesweeperGame(10, 7);

        for (Point[] xCoordPoint : game.landmines) {
            for (Point point : xCoordPoint) {
                System.out.print(point.isFlagged() ? "F" : !point.isVisable() ? "?" : point.hasLandmine() ? "X" : point.getValue());
                System.out.print(" ");
            }
            System.out.println("");
        }

        for (int i = 0; i < 10; i++)
            System.out.println("");

        System.out.println(game.sweep(7, 7));
        for (Point[] xCoordPoint : game.landmines) {
            for (Point point : xCoordPoint) {
                System.out.print(point.isFlagged() ? "F" : !point.isVisable() ? "?" : point.hasLandmine() ? "X" : point.getValue());
                System.out.print(" ");
            }
            System.out.println("");
        }

        Point pointRandomMine = game.realLandmine.get(ThreadLocalRandom.current().nextInt(game.realLandmine.size()));
        game.flag(pointRandomMine.getX(), pointRandomMine.getY());

        for (int i = 0; i < 10; i++)
            System.out.println("");

        for (Point[] xCoordPoint : game.landmines) {
            for (Point point : xCoordPoint) {
                System.out.print(point.isFlagged() ? "F" : !point.isVisable() ? "?" : point.hasLandmine() ? "X" : point.getValue());
                System.out.print(" ");
            }
            System.out.println("");
        }
    }

    public Point[][] landmines;
    public List<Point> realLandmine = new ArrayList<>();
    public boolean gameOver = false;

    // public int size;
    public int sizeX;
    public int sizeY;

    public MinesweeperGame(int size, int landmineCount) {
        this(size, size, landmineCount);

        /*this.sizeX = size;
        this.sizeY = size;
        landmines = new Point[size][size];

        for (int x = 0; x < size; x++) {
            for (int y = 0; y < size; y++) {
                landmines[y][x] = new Point(x, y);
            }
        }

        if (landmineCount * 2 < size * size) {
            for (int i = 0; i < landmineCount; i++) {
                int j = ThreadLocalRandom.current().nextInt(size * size - 1);

                if (landmines[j / size][j % size].hasLandmine()) {
                    i--;
                    continue;
                }

                landmines[j / size][j % size].setLandmine(true);
                realLandmine.add(landmines[j / size][j % size]);
            }
        } else {
            List<Integer> indexList = new ArrayList<>();

            for (int i = 0; i < landmineCount; i++) {
                indexList.add(i);
            }

            for (int i = 0; i < landmineCount; i++) {
                int j = ThreadLocalRandom.current().nextInt(indexList.size() - 1);

                int index = indexList.get(j);

                landmines[index / size][index % size].setLandmine(true);
                realLandmine.add(landmines[index / size][index % size]);
                indexList.remove(j);
            }
        }

        for (int y = 0; y < size; y++) {
            for (int x = 0; x < size; x++) {
                if (landmines[y][x].hasLandmine()) {
                    landmines[y][x].setValue(-1);
                    continue;
                }

                landmines[y][x].setValue(getSurroundingLandmine(landmines, landmines[y][x], sizeX, sizeY));
            }
        }*/
    }

    public MinesweeperGame(int sizeX, int sizeY, int landmineCount) {
        // todo: initialize field, set landmine, assign landmine value
        this.sizeX = sizeX;
        this.sizeY = sizeY;
        landmines = new Point[sizeY][sizeX];

        for (int x = 0; x < sizeX; x++) {
            for (int y = 0; y < sizeY; y++) {
                landmines[y][x] = new Point(x, y);
            }
        }

        if (landmineCount * 2 < sizeX * sizeY) {
            for (int i = 0; i < landmineCount; i++) {
                int j = ThreadLocalRandom.current().nextInt(sizeY - 1);
                int k = ThreadLocalRandom.current().nextInt(sizeX - 1);

                if (landmines[j][k].hasLandmine()) {
                    i--;
                    continue;
                }

                landmines[j][k].setLandmine(true);
                realLandmine.add(landmines[j][k]);
            }
        } else {
            List<Integer> indexList = new ArrayList<>();

            for (int i = 0; i < landmineCount; i++) {
                indexList.add(i);
            }

            for (int i = 0; i < landmineCount; i++) {
                int j = ThreadLocalRandom.current().nextInt(indexList.size() - 1);

                int index = indexList.get(j);

                landmines[index / sizeY][index % sizeX].setLandmine(true);
                realLandmine.add(landmines[index / sizeY][index % sizeX]);
                indexList.remove(j);
            }
        }

        for (int y = 0; y < sizeY; y++) {
            for (int x = 0; x < sizeX; x++) {
                if (landmines[y][x].hasLandmine()) {
                    landmines[y][x].setValue(-1);
                    continue;
                }

                landmines[y][x].setValue(getSurroundingLandmine(landmines, landmines[y][x], sizeX, sizeY));
            }
        }
    }

    private List<Point> getSurroundingPoint(Point[][] points, Point point, int sizeX, int sizeY) {
        List<Point> pointsList = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (x == y/* && x == 0*/)
                    continue;

                if (x + point.getX() < 0 || y + point.getY() < 0)
                    continue;

                if (x + point.getX() > (sizeX - 1) || y + point.getY() > (sizeY - 1))
                    continue;

                pointsList.add(points[point.getY() + y][point.getX() + x]);
            }
        }

        return pointsList;
    }

    private int getSurroundingLandmine(Point[][] points, Point point, int sizeX, int sizeY) {
        List<Point> pointsList = new ArrayList<>();
        for (int x = -1; x < 2; x++) {
            for (int y = -1; y < 2; y++) {
                if (x == y && x == 0)
                    continue;

                if (x + point.getX() < 0 || y + point.getY() < 0)
                    continue;

                if (x + point.getX() > (sizeX - 1) || y + point.getY() > (sizeY - 1))
                    continue;

                Point neighborPoint = points[point.getY() + y][point.getX() + x];

                if (!neighborPoint.hasLandmine())
                    continue;

                pointsList.add(neighborPoint);
            }
        }

        return pointsList.size();
    }

    public boolean sweep(int x, int y) {
        if (gameOver)
            return false;

        Point mine = landmines[y][x];

        if (mine.isFlagged())
            return false;

        if (mine.hasLandmine()) {
            gameOver = true;
            mine.setVisable(true);
            return true;
        }

        if (mine.getValue() == 0) {
            List<Point> laterTravel = new ArrayList<>();

            mine.setVisable(true);

            for (Point point : getSurroundingPoint(landmines, mine, sizeX, sizeY)) {
                if (point.isFlagged())
                    continue;

                if (point.getValue() == 0 && !point.isVisable()) {
                    laterTravel.add(point);
                }

                if (!point.isVisable())
                    point.setVisable(true);
            }

            for (Point point : laterTravel) {
                sweep(point.getX(), point.getY());
            }
        } else {
            mine.setVisable(true);
        }

        if (realLandmine.stream().allMatch(Point::isFlagged) && Arrays.stream(landmines).flatMap(Arrays::stream).filter(point -> !point.hasLandmine()).allMatch(Point::isVisable)) {
            gameOver = true;
        }

        return true;
    }

    public boolean flag(int x, int y) {
        if (gameOver)
            return false;

        Point mine = landmines[y][x];

        if (mine.isVisable())
            return false;

        if (mine.isFlagged()) {
            mine.setFlagged(false);
            return true;
        }

        mine.setFlagged(true);

        System.out.println(realLandmine.stream().allMatch(Point::isFlagged));
        System.out.println(Arrays.stream(landmines).flatMap(Arrays::stream).filter(point -> !point.hasLandmine()).allMatch(Point::isVisable));

        if (realLandmine.stream().allMatch(Point::isFlagged) && Arrays.stream(landmines).flatMap(Arrays::stream).filter(point -> !point.hasLandmine()).allMatch(Point::isVisable)) {
            gameOver = true;
        }

        return true;
    }
}
