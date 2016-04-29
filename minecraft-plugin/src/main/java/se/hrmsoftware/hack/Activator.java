package se.hrmsoftware.hack;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.stream.Stream;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import spark.Request;
import spark.Response;
import spark.Spark;

import se.hrmsoftware.hack.coordinates.Location2D;
import se.hrmsoftware.hack.coordinates.MinecraftCoordinateSystem;

import com.google.gson.Gson;

import static spark.Spark.*;

public class Activator extends JavaPlugin implements Listener {

	private enum SignType {
		POLLUTION("p"),
		WATERTEMP("w"),
		ACCIDENT("a"),
		DEFAULT("d");

		private final String label;

		SignType(String a) {
			this.label = a;
		}

		public static SignType of(String t) {
			return Stream.of(values()).filter(v -> v.label.equals(t)).findFirst().orElse(DEFAULT);
		}
	}


	public static class DeleteRequest {
		private final Double latitude;
		private final Double longitude;

		public DeleteRequest(Double longitude, Double latitude) {
			this.longitude = longitude;
			this.latitude = latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public Double getLatitude() {
			return latitude;
		}
	}

	public static class CreateSignRequest {
		private final Double latitude;
		private final Double longitude;
		private final String value;
		private final SignType type;

		public CreateSignRequest(Double latitude, Double longitude, String value, SignType type) {
			this.latitude = latitude;
			this.longitude = longitude;
			this.value = value;
			this.type = type;
		}

		public Double getLatitude() {
			return latitude;
		}

		public Double getLongitude() {
			return longitude;
		}

		public String getValue() {
			return value;
		}

		public SignType getType() {
			return type;
		}

		@Override
		public String toString() {
			return "CreateSignRequest{" +
					"latitude=" + latitude +
					", longitude=" + longitude +
					", value='" + value + '\'' +
					", type=" + type +
					'}';
		}
	}

	private final Gson gson = new Gson();
	private final ConcurrentMap<SignPosition, HRMSign> signs = new ConcurrentHashMap<>();
	private final MinecraftCoordinateSystem coordinateSystem = new MinecraftCoordinateSystem();

	// REST-API

	private Location defaultWorldLocation(double latitude, double longitude) {
		Location2D worldPoint = coordinateSystem.fromDecimalCoordinates(latitude, longitude);

		HRMSign sign = signs.get(new SignPosition((int)worldPoint.getX(), (int)worldPoint.getY()));
		if (sign != null) {
			return sign.getLocation();
		}

		World world = getServer().getWorlds().get(0);

		Block b = world.getBlockAt((int) worldPoint.getX(),
				world.getMaxHeight() - 1,
				(int) worldPoint.getY());
		while (b != null && (b.getType() == Material.AIR || b.getType() == Material.SIGN_POST) &&
				b.getRelative(BlockFace.DOWN) != null &&
				(b.getRelative(BlockFace.DOWN).getType() == Material.AIR
						|| b.getRelative(BlockFace.DOWN).getType() == Material.SIGN_POST)) {
			b = b.getRelative(BlockFace.DOWN);
		}

		return b.getLocation();
	}

	private Object onPost(Request request, Response response) throws Exception {
		CreateSignRequest req = gson.fromJson(request.body(), CreateSignRequest.class);
		createOrUpdateSign(req.getLatitude(), req.getLongitude(), req.getType(), req.getValue());
		return null;
	}

	private Object onDelete(Request request, Response response) throws Exception {
		DeleteRequest req = gson.fromJson(request.body(), DeleteRequest.class);

		Location loc = defaultWorldLocation(req.getLatitude(), req.getLongitude());
		HRMSign s = signs.remove(SignPosition.of(loc));

		if (s != null) {
			getServer().getScheduler().runTask(this, s::delete);
		}

		return null;
	}

	private void createOrUpdateSign(Location location, SignType type, java.lang.String value) {
		getServer().getScheduler().runTask(this, () -> createOrUpdateSign(type, value, location));
	}

	private void createOrUpdateSign(double latitude, double longitude, SignType type, java.lang.String value) {
		getServer().getScheduler().runTask(this, () -> {
			Location location = defaultWorldLocation(latitude, longitude);
			createOrUpdateSign(type, value, location);
		});
	}

	private void createOrUpdateSign(SignType type, String value, Location location) {
			HRMSign sign;
			switch (type) {
				case POLLUTION:
					sign = new PollutionSign(location);
					break;

				case ACCIDENT:
					sign = new AccidentSign(location);
					break;

				case WATERTEMP:
				default:
					sign = new DefaultSign(location);
			}

			getLogger().info("Creating Sign @ " + location + " (" + sign + ")");

			sign.update(value);

			signs.put(SignPosition.of(location), sign);
	}


	@Override
	public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		// Start spark server
		Spark.options("/*", (request,response)->{

			String accessControlRequestHeaders = request.headers("Access-Control-Request-Headers");
			if (accessControlRequestHeaders != null) {
				response.header("Access-Control-Allow-Headers", accessControlRequestHeaders);
			}

			String accessControlRequestMethod = request.headers("Access-Control-Request-Method");
			if(accessControlRequestMethod != null){
				response.header("Access-Control-Allow-Methods", accessControlRequestMethod);
			}

			return "OK";
		});

		Spark.before((request,response)->{
			response.header("Access-Control-Allow-Origin", "*");
		});

		init();

		post("/", "application/json", this::onPost, gson::toJson);
		delete("/:uid", "application/json", this::onDelete, gson::toJson);

		startScheduled();
	}

	@Override
	public void onDisable() {
		// Stop SPARK server
		stop();
		getServer().getScheduler().cancelAllTasks();
	}

	private void startScheduled() {
		BukkitScheduler scheduler = getServer().getScheduler();
		scheduler.scheduleSyncRepeatingTask(this, () -> signs.values().stream().filter(s -> s instanceof PollutionSign).forEach(s -> s.update(null)), 0L, 200L); //A second is 20 ticks.
	}

	@EventHandler
	public void handle(PlayerLoginEvent evt) {
		getLogger().info("Player logged in: " + evt.getPlayer().getDisplayName());
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		System.out.println("Command issued by " + sender + ": " + command);
		Player pl = getServer().getPlayer(sender.getName());

		Location loc = pl.getLocation();
		loc.add(loc.getDirection().setY(0).normalize().multiply(1));

		if (args.length == 0) {
			createOrUpdateSign(loc, SignType.DEFAULT, "Bork Bork!");
		} else if (args.length == 2) {
			createOrUpdateSign(loc, SignType.of(args[0]), args[1]);
		}

		sender.sendMessage("DONE! Sign scheduled for creation!");

		return false;
	}

}
