import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PartSeventeen {
    static boolean debug = false;

    public static void main(String[] args) throws IOException {
        var lines = Files.readAllLines(Paths.get("17/test.txt")).get(
                0);

        long maxY = -1;
        int width = 7;
        Map<Point, Material> map = new HashMap<>();

        /*
         * ####
         *
         */
        Shape line = new Shape(List.of(new Point(0, 0), new Point(1, 0), new Point(2, 0), new Point(3, 0)), 4, 1);
        /*
         * .#.
         * ###
         * .#.
         */
        Shape cross = new Shape(List.of(
                new Point(1, 0),
                new Point(0, 1), new Point(1, 1), new Point(2, 1),
                new Point(1, 2)), 3, 3);
        /*
         * ..#
         * ..#
         * ###
         */
        Shape el = new Shape(List.of(
                new Point(2, 0),
                new Point(2, 1),
                new Point(0, 2), new Point(1, 2), new Point(2, 2)), 3, 3);
        /*
         * #
         * #
         * #
         * #
         */
        Shape ih = new Shape(List.of(
                new Point(0, 0),
                new Point(0, 1),
                new Point(0, 2),
                new Point(0, 3)), 1, 4);
        /*
         * ##
         * ##
         */
        Shape cube = new Shape(List.of(
                new Point(0, 0), new Point(1, 0),
                new Point(0, 1), new Point(1, 1)), 2, 2);
        boolean newShape = true;
        List<Shape> shapes = List.of(line, cross, el, ih, cube);

        int shapeNumber = 0;
        Shape currShape = null;

        List<Point> fallingRocks = null;
        long nrOfShapes = 1;
        int partOne = 2023;
        long partTwo = 1000000000000L;
        for (int tick = 0; nrOfShapes <= partTwo; tick++) {
            if (newShape) {
                if (nrOfShapes == partOne) {
                    System.out.println("Answer 1: " + (maxY + 1));
                }
                nrOfShapes++;
                fallingRocks = new ArrayList<>();
                newShape = false;
                currShape = shapes.get(shapeNumber);

                shapeNumber = (shapeNumber + 1) % 5;

                for (int x = 0; x < currShape.width; x++) {
                    for (int y = 0; y < currShape.height; y++) {
                        Material m = Material.FALLING_AIR;
                        Point p = new Point(x, y);
                        Point np = new Point(x + 2, maxY + 3 + currShape.height - y);
                        if (currShape.points.contains(p)) {
                            m = Material.FALLING;
                            fallingRocks.add(np);
                        }

                        map.put(np, m);
                    }
                }
                if (debug) {
                    System.out.println("The rock begins falling:");
                    prettyPrint(map, width, maxY + currShape.height + 3);
                }
                int kalle = 1;
            }
            // First blow
            Point direction = null;
            switch (lines.charAt(tick % lines.length())) {
                case '<':
                    direction = new Point(-1, 0);

                    break;

                case '>':
                    direction = new Point(1, 0);

                    break;
            }
            List<Point> newFalling = new ArrayList<>();
            for (Point fall : fallingRocks) {
                Point newPoint = new Point(fall.x + direction.x, fall.y + direction.y);
                Material m = map.getOrDefault(newPoint, Material.AIR);
                if (newPoint.x < 0 || newPoint.x >= width || m == Material.ROCK) {
                    newFalling = fallingRocks;
                    break;
                }
                newFalling.add(newPoint);
            }
            for (int i = 0; i < fallingRocks.size(); i++) {
                Point old = fallingRocks.get(i);
                if (!newFalling.contains(old)) {
                    map.remove(old);
                }
                map.put(newFalling.get(i), Material.FALLING);
            }
            fallingRocks = newFalling;
            if (debug) {
                System.out.println("Jet pushes rock " + lines.charAt(tick % lines.length()) + ":");
                prettyPrint(map, width, maxY + currShape.height + 3);
            }
            // Then fall
            newFalling = new ArrayList<>();
            boolean touch = false;
            for (Point fall : fallingRocks) {
                Point newPoint = new Point(fall.x, fall.y - 1);
                Material m = map.getOrDefault(newPoint, Material.AIR);
                if (newPoint.y < 0 || m == Material.ROCK) {
                    newFalling = fallingRocks;
                    touch = true;
                    break;
                }
                newFalling.add(newPoint);
            }
            for (int i = 0; i < fallingRocks.size(); i++) {
                Point old = fallingRocks.get(i);
                if (!newFalling.contains(old)) {
                    map.remove(old);
                }
                map.put(newFalling.get(i), Material.FALLING);
            }
            fallingRocks = newFalling;

            if (touch) {
                for (Point p : fallingRocks) {
                    if (p.y > maxY) {
                        maxY = p.y;
                    }
                    map.put(p, Material.ROCK);
                }
                newShape = true;
            } else {
            }
            // If touch something
            // newShape = true;
            // currShape++
            // maxY = topOfShape + 3
            if (debug) {
                System.out.println("Rock falls 1 unit:");
                prettyPrint(map, width, maxY + currShape.height + 3);
            }
        }

        System.out.println("Max: " + (maxY + 1));
        // 3110 too high
        // 3109
        // 3108 too low
    }

    public static void prettyPrint(Map<Point, Material> map, int width, long height) {
        for (long y = height - 1L; y >= Math.max(height - 20, -1); y--) {
            for (int x = -1; x < width + 1; x++) {
                String c = ".";
                if (y == -1 && (x == -1 || x == width)) {
                    c = "+";
                } else if (x == -1 || x == width) {
                    c = "|";
                } else if (y == -1) {
                    c = "-";
                } else {
                    Material m = map.getOrDefault(new Point(x, y), Material.AIR);
                    switch (m) {
                        case AIR:
                            c = ".";
                            break;
                        case ROCK:
                            c = "#";
                            break;
                        case FALLING:
                            c = "@";
                            break;

                        default:
                            break;
                    }
                }
                System.out.print(c);
            }
            System.out.println();
        }
        System.out.println();
    }

    public enum Material {
        AIR,
        ROCK,
        FALLING,
        FALLING_AIR,
    }

    public record Shape(List<Point> points, int width, int height) {
    };

    public record Point(int x, long y) {
    };
}