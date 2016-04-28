package se.hrmsoftware.hack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

public class PollutionSign  implements HRMSign {
	private final Location location;

	private Double pollutionValue;

	public PollutionSign(Location location) {
		this.location = location;
	}

	public void update(String value) {
		if (value != null) {
			pollutionValue = Double.valueOf(value);
		}
		double treeIndex = calculateTreeIndex(location, 10);
		render(pollutionValue, pollutionValue - treeEffect(pollutionValue, treeIndex));
	}

	private double treeEffect(double pollutionValue, double treeIndex) {
		return pollutionValue * treeIndex;
	}

	@Override
	public void delete() {
		location.getBlock().setType(Material.AIR);
	}

	private void render(double oldValue, double pollutionValue) {
		Block block = location.getBlock();
		block.setType(Material.SIGN_POST);
		Sign sign = (Sign) block.getState();
		sign.setLine(0, "PPM");
		sign.setLine(1, String.format("%f (%f)", pollutionValue, oldValue));
		sign.update();
	}

	/**
	 * The tree index is calculated from all the blocks with the type LEAF within a cube with the side blocks*2.
	 *
	 * The index is calculated by counting all the leaf blocks and multiply this by 3 and then divide this with the
	 * total number of blocks in the cube.
	 *
	 * @param loc the location where we need to calculate the index.
	 * @param blocks the number of blocks, times two that will be the side of the box. This means that the measure
	 *               point is almost in the center bottom of the cube.
	 *
	 * @return the index.
	 */
	private double calculateTreeIndex(Location loc, int blocks) {
		int scanBoxSide = blocks * 2;

		double initalX = loc.getX() + blocks;
		double initalY = loc.getY() + scanBoxSide;
		double initalZ = loc.getZ() + blocks;

		int count = 0;

		for (int x = 0; x < scanBoxSide; x++) {
			loc.setX(initalX - x);
			for (int y = 0; y <= scanBoxSide; y++) {
				loc.setY(initalY - y);
				for(int z = 0; z < scanBoxSide; z++) {
					loc.setZ(initalZ - z);
					Block b = loc.getBlock();

					if (b.getType().equals(Material.LEAVES) || b.getType().equals(Material.LEAVES_2)) {
						count++;
					}
				}
			}
		}

		return (double) count / ((double)scanBoxSide * scanBoxSide * scanBoxSide);
	}

	@Override
	public String toString() {
		return "PollutionSign{" +
				"location=" + location +
				", pollutionValue=" + pollutionValue +
				'}';
	}
}
