package se.hrmsoftware.hack;

import org.bukkit.plugin.java.JavaPlugin;

public class Activator extends JavaPlugin {

	@Override public void onEnable() {
		getLogger().info("ENABLED");
	}

	@Override public void onDisable() {
		getLogger().info("DISABLED");
	}
}
