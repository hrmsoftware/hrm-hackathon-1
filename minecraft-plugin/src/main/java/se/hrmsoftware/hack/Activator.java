package se.hrmsoftware.hack;

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
}
