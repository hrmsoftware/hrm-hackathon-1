package se.hrmsoftware.hack;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class Activator extends JavaPlugin implements Listener {

	@Override public void onEnable() {
		getLogger().info("ENABLED");
		getServer().getPluginManager().registerEvents(this, this);
	}

	@Override public void onDisable() {
		getLogger().info("DISABLED");
	}


	@EventHandler
	public void handle(PlayerLoginEvent evt) {
		getLogger().info("Player logged in: " + evt.getPlayer().getDisplayName());
	}

	@Override public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		System.out.println("Command issued by "+sender+": " + command);
		Player pl = getServer().getPlayer(sender.getName());
		if (pl != null) {
			double treeIndex = calculateTreeIndex(pl.getLocation(), 10);

			Location loc = pl.getLocation();
			loc.add(loc.getDirection().setY(0).normalize().multiply(1));

			Block block = loc.getBlock();
			block.setType(Material.SIGN_POST);

			Sign sign = (Sign) block.getState();
			sign.setLine(0, "Tree Index");
			sign.setLine(1, Double.toString(treeIndex));
			sign.update();

			sender.sendMessage("DONE! Tree Index:" + treeIndex);

			return true;
		}
		return false;
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
