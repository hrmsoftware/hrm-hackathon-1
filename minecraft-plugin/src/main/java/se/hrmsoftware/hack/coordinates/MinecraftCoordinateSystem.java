package se.hrmsoftware.hack.coordinates;

import com.grum.geocalc.DegreeCoordinate;

public class MinecraftCoordinateSystem {
	private static final double ORIGO_LATITUDE = 56.875605;
	private static final double ORIGO_LONGITUDE = 14.810618;
	private static final long ORIGO_X = 61056;
	private static final long ORIGO_Y = -787945;
	private static final double BLOCK_SIZE_X = 7.9812;
	private static final double BLOCK_SIZE_Y = 7.9975;

	private final CoordinateSystem coordinateSystem;

	public MinecraftCoordinateSystem() {
		coordinateSystem = new CoordinateSystem(
				ORIGO_LATITUDE, ORIGO_LONGITUDE, ORIGO_X, ORIGO_Y, BLOCK_SIZE_X, BLOCK_SIZE_Y);
	}

	public Location2D fromDecimalCoordinates(double latitude, double longitude) {
		return coordinateSystem.convert(new com.grum.geocalc.Point(
				new DegreeCoordinate(latitude), new DegreeCoordinate(longitude)));
	}

	public Location2D fromDecimalCoordinates(String coordinates) {
		String[] values = coordinates.split(",");
		return coordinateSystem.convert(new com.grum.geocalc.Point(
				new DegreeCoordinate(Double.valueOf(values[0].trim())),
				new DegreeCoordinate(Double.valueOf(values[1].trim()))));
	}
}
