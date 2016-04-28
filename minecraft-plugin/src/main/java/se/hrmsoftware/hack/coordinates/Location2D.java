package se.hrmsoftware.hack.coordinates;

import java.util.Objects;

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

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		Location2D that = (Location2D) o;
		return Double.compare(that.x, x) == 0 &&
				Double.compare(that.y, y) == 0;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
