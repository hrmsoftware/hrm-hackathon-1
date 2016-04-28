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
			Location loc = pl.getLocation();
			loc.setX(loc.getX() + 5);
			Block block = loc.getBlock();

			block.setType(Material.SIGN_POST);

			Sign sign = (Sign) block.getState();
			sign.setLine(0, "Foo");
			sign.setLine(1, "Fuck");
			sign.update();

			sender.sendMessage("DONE!");
			return true;
		}
		return false;
	}
}
