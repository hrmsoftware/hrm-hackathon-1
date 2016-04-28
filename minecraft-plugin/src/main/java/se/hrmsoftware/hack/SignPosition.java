package se.hrmsoftware.hack;

import org.bukkit.Location;

import java.util.Objects;

/**
 */
public class SignPosition {

	final int x;
	final int y;

	public SignPosition(int x, int y) {
		this.x = x;
		this.y = y;
	}

	public static SignPosition of(Location loc) {
		return new SignPosition(loc.getBlockX(), loc.getBlockZ());
	}

	public long getX() {
		return x;
	}

	public long getY() {
		return y;
	}

	@Override
	public String toString() {
		return "SignPosition{" +
				"x=" + x +
				", y=" + y +
				'}';
	}

	@Override
	public boolean equals(Object o) {

		if (this == o) {
			return true;
		}
		if (o == null || getClass() != o.getClass()) {
			return false;
		}
		SignPosition that = (SignPosition) o;
		return x == that.x &&
				y == that.y;
	}

	@Override
	public int hashCode() {
		return Objects.hash(x, y);
	}
}
