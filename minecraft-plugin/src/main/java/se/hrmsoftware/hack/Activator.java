package se.hrmsoftware.hack;

import com.google.gson.Gson;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Sign;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.java.JavaPlugin;
import spark.Request;
import spark.Response;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import static spark.Spark.*;

public class Activator extends JavaPlugin implements Listener {

	public static class UpdateSignRequest {
		private final String[] lines;

		public UpdateSignRequest(String[] lines) {
			this.lines = lines;
		}

		public String[] getLines() {
			return lines;
		}
	}

	public static class CreateSignRequest {
		private final Double x;
		private final Double y;
		private final String[] lines;

		public CreateSignRequest(Double x, Double y, String[] lines) {
			this.x = x;
			this.y = y;
			this.lines = lines;
		}

		public Double getX() {
			return x;
		}

		public Double getY() {
			return y;
		}

		public String[] getLines() {
			return lines;
		}
	}

	private static final class SignAtLocation {
		public Location location;
		public String[] data;
		public static SignAtLocation of(Location loc, String[] data) {
			SignAtLocation r = new SignAtLocation();
			r.location = loc;
			r.data = data;
			return r;
		}
	}
	private final Gson gson = new Gson();
	private final ConcurrentMap<String, SignAtLocation> signs = new ConcurrentHashMap<>();

	// REST-API

	private Location defaultWorldLocation(double x, double y) {
		World world = getServer().getWorlds().get(0);
		return new Location(world, x, world.getHighestBlockYAt((int)x, (int)y), y);
	}

	private Object onGet(Request request, Response response) throws Exception {
		SignAtLocation s = signs.get(request.params(":uid"));
		if (s != null) {
			Map<String,Object> r = new HashMap<>();
			r.put("lines", s.data);
			return r;
		} else {
			response.status(404);
			return null;
		}
	}

	private Object onPost(Request request, Response response) throws Exception {
		CreateSignRequest req = gson.fromJson(request.body(), CreateSignRequest.class);
		return createSign(defaultWorldLocation(req.getX(), req.getY()), req.getLines());
	}
	private Object onPut(Request request, Response response) throws Exception {
		UpdateSignRequest req = gson.fromJson(request.body(), UpdateSignRequest.class);
		SignAtLocation signAtLocation = signs.get(request.params(":uid"));
		if (signAtLocation != null) {
			getServer().getScheduler().runTask(this, () -> {
				Sign sign = (Sign) signAtLocation.location.getBlock().getState();
				for (int i = 0; i < req.getLines().length; i++) {
					String line = req.getLines()[i];
					sign.setLine(i, line);
				}
				sign.update();
				signs.put(request.params(":uid"), SignAtLocation.of(signAtLocation.location, req.getLines()));
			});
		}
		return null;
	}
	private Object onDelete(Request request, Response response) throws Exception {
		SignAtLocation s = signs.get(request.params(":uid"));
		if (s != null) {
			getServer().getScheduler().runTask(this, () -> {
				s.location.getBlock().setType(Material.AIR);
			});
		}
		return null;
	}

	private String createSign(Location location, String[] lines) {
		String uuid = UUID.randomUUID().toString();
		getServer().getScheduler().runTask(this, () -> {
			getLogger().info("Creating Sign @ " + location);
			Block block = location.getBlock();
			block.setType(Material.SIGN_POST);
			Sign sign = (Sign) block.getState();
			for (int i = 0; i < lines.length; i++) {
				String line = lines[i];
				sign.setLine(i, line);
			}
			sign.update();
			signs.put(uuid, SignAtLocation.of(location, lines));
		});
		return uuid;
	}



	@Override public void onEnable() {
		getServer().getPluginManager().registerEvents(this, this);
		// Start spark server
		init();

		get("/:uid", "application/json", this::onGet, gson::toJson);
		post("/", "application/json", this::onPost, gson::toJson);
		put("/:uid", "application/json", this::onPut, gson::toJson);
		delete("/:uid", "application/json", this::onDelete, gson::toJson);

	}

	@Override public void onDisable() {
		// Stop SPARK server
		stop();
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
