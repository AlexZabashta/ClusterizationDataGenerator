package features_inversion.util;

public class Point {

    private final double[] coordinate;

    public int dimension() {
        return coordinate.length;
    }

    public double coordinate(int index) {
        return coordinate[index];
    }

    public double[] coordinates() {
        return coordinate.clone();
    }

    public Point(double... coordinate) {
        this.coordinate = coordinate.clone();
    }

}
