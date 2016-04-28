package se.hrmsoftware.hack.coordinates;

public class Location2D {
	private final double x, y;

	public Location2D(double x, double y) {
		this.x = x;
		this.y = y;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	@Override
	public String toString() {
		return String.format("x = %f, y = %f", x, y);
	}
}
