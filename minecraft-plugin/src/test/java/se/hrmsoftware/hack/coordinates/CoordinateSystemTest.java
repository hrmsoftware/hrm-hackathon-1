package se.hrmsoftware.hack.coordinates;

import org.junit.Test;

import se.hrmsoftware.hack.coordinates.CoordinateSystem;
import se.hrmsoftware.hack.coordinates.Location2D;

import com.grum.geocalc.DegreeCoordinate;
import com.grum.geocalc.Point;

public class CoordinateSystemTest {
	@Test
	public void shouldConvertToWorldPositionFromGds() throws Exception {
		CoordinateSystem converter = new CoordinateSystem(56.875605, 14.810618, 61056, -787945, 7.9776, 7.9975);
		Location2D location = converter.convert(
				new Point(new DegreeCoordinate(56.8779615), new DegreeCoordinate(14.8072923)));
		System.out.println(String.format("Vattentorget -> HRM: %f,%f", location.getX(), location.getY()));

		Location2D gemla = converter.convert(
				new Point(new DegreeCoordinate(56.867210), new DegreeCoordinate(14.643641)));
		System.out.println(String.format("Vattentorget -> Gemla: %f,%f (59784,-787834)", gemla.getX(), gemla.getY()));

		Location2D dadesjo = converter.convert(
				new Point(new DegreeCoordinate(57.009957), new DegreeCoordinate(15.101948)));
		System.out.println(String.format("Vattentorget -> Dädesjö: %f,%f (63276,-789813)", dadesjo.getX(), dadesjo.getY()));

		Location2D temp = converter.convert(
				new Point(new DegreeCoordinate(57.031174), new DegreeCoordinate(14.780709)));
		System.out.println(String.format("Vattentorget -> Temp: %f,%f ", temp.getX(), temp.getY()));
	}

	@Test
	public void determineBlockSize() throws Exception {
		double xSize, ySize;
		for (xSize = 6; xSize < 20; xSize += 0.0001) {
			CoordinateSystem converter = new CoordinateSystem(56.875605, 14.810618, 61056, -787945, xSize, xSize);
			Location2D gemla = converter.convert(
					new Point(new DegreeCoordinate(57.009957), new DegreeCoordinate(15.101948)));
			if (gemla.getX() <= 63274) {
				break;
			}
		}
		System.out.println(String.format("Block X scale: %f", xSize));
		for (ySize = 6; ySize < 20; ySize += 0.0001) {
			CoordinateSystem converter = new CoordinateSystem(56.875605, 14.810618, 61056, -787945, ySize, ySize);
			Location2D gemla = converter.convert(
					new Point(new DegreeCoordinate(57.009957), new DegreeCoordinate(15.101948)));
			if (gemla.getY() >= -789813) {
				break;
			}
		}
		System.out.println(String.format("Block Y scale: %f", ySize));
	}
}