package se.hrmsoftware.hack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;

/**
 * Created by henrikgr on 2016-04-28.
 */
public class PollutionSign  implements HRMSign {

	Location loc;

	public PollutionSign(Location loc, double value) {
		this.loc = loc;
	}

	public void update(double value) {

		//todo calculate the tree index also !

	}

	@Override
	public void delete() {
		//s.location.getBlock().setType(Material.AIR);
	}

	public void render() {



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

					//b.setType(Material.GLASS);
				}
			}
		}

		return (double) count * 10 / ((double)scanBoxSide * scanBoxSide * scanBoxSide);
	}


}
