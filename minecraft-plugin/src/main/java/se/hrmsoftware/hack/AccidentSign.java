package se.hrmsoftware.hack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 */
class AccidentSign implements HRMSign {

	private final Location location;

	AccidentSign(Location location) {

		this.location = location;
	}

	@Override
	public void update(String value) {

		Block block = location.getBlock();
		block.setType(Material.NETHERRACK);






	}

	@Override
	public void delete() {

		Block block = location.getBlock();
		block.setType(Material.AIR);


	}

	@Override
	public String toString() {
		return "AccidentSign{" +
				"location=" + location +
				'}';
	}
}
