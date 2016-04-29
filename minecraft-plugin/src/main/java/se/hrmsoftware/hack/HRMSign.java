package se.hrmsoftware.hack;

import org.bukkit.Location;

public interface HRMSign {
	void update(String value);
	void delete();
	Location getLocation();
}
