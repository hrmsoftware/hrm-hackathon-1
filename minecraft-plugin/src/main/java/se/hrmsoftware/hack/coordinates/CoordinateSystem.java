package se.hrmsoftware.hack.coordinates;

import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.EarthCalc;
import com.grum.geocalc.Point;

public class CoordinateSystem {
	private final long blockX;
	private final long blockY;
	private final double blockXSize;
	private final double blockYSize;

	public CoordinateSystem(double latitude, double longitude, long blockX, long blockY, double blockXSize, double blockYSize) {
		this.blockX = blockX;
		this.blockY = blockY;
		this.blockXSize = blockXSize;
		this.blockYSize = blockYSize;
		this.origo = new Point(new DegreeCoordinate(latitude), new DegreeCoordinate(longitude));
	}

	private final Point origo;

	public Location2D convert(Point point) {
		double xDistance = EarthCalc.getDistance(origo,
				new Point(new DegreeCoordinate(origo.getLatitude()), new DegreeCoordinate(point.getLongitude())));
		double yDistance = EarthCalc.getDistance(origo,
				new Point(new DegreeCoordinate(point.getLatitude()), new DegreeCoordinate(origo.getLongitude())));

		if (origo.getLatitude() < point.getLatitude()) {
			yDistance *= -1;
		}
		if (origo.getLongitude() > point.getLongitude()) {
			xDistance *= -1;
		}

		return new Location2D(
				blockX + metersToBlocks(xDistance, blockXSize),
				blockY + metersToBlocks(yDistance, blockYSize));
	}

	private double metersToBlocks(double meters, double blockSize) {
		return meters / blockSize;
	}
}
