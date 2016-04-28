package se.hrmsoftware.hack.coordinates;

import org.junit.Test;

import se.hrmsoftware.hack.coordinates.Location2D;
import se.hrmsoftware.hack.coordinates.MinecraftCoordinateSystem;

import static org.junit.Assert.*;

public class MinecraftCoordinateSystemTest {
	private MinecraftCoordinateSystem system = new MinecraftCoordinateSystem();

	@Test
	public void testStringInput() throws Exception {
		Location2D location = system.fromDecimalCoordinates("56.911179, 14.855898");
		assertNotNull(location);
		System.out.println(String.format("%f 130 %f", location.getX(), location.getY()));
	}
}