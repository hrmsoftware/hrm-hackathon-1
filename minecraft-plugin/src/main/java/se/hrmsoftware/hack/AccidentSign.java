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
		Location loc = location.clone();
		loc.getWorld().strikeLightningEffect(loc);
		Block block = loc.getBlock();
		block.setType(Material.NETHERRACK);
		loc.setY(loc.getY()+1);
		loc.getBlock().setType(Material.FIRE);
	}

	@Override
	public void delete() {
		Location loc = location.clone();
		loc.getWorld().strikeLightningEffect(loc);
		Block block = loc.getBlock();
		block.setType(Material.AIR);
		loc.setY(loc.getY()+1);
		loc.getBlock().setType(Material.AIR);
	}

	@Override
	public Location getLocation() {
		return location.clone();
	}

	@Override
	public String toString() {
		return "AccidentSign{" +
				"location=" + location +
				'}';
	}
}
