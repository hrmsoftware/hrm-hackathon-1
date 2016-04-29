package se.hrmsoftware.hack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;

/**
 */
class DefaultSign implements HRMSign {

	private final Location location;

	DefaultSign(Location location) {
		this.location = location;
	}

	@Override
	public void update(String value) {
		location.getWorld().strikeLightningEffect(location);
		Block block = location.getBlock();
		block.setType(Material.SIGN_POST);

		Sign sign = (Sign) block.getState();
		sign.setLine(0, value);
		sign.update();
	}

	@Override
	public void delete() {
		location.getWorld().strikeLightningEffect(location);
		Block block = location.getBlock();
		block.setType(Material.AIR);
	}

	@Override
	public Location getLocation() {
		return location.clone();
	}

	@Override
	public String toString() {
		return "DefaultSign{" +
				"location=" + location +
				'}';
	}
}
