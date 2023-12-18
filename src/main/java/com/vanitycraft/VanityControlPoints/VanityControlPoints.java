package com.vanitycraft.VanityControlPoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.util.Vector;

import com.vanitycraft.VanityControlPoints.Commands.CommandDispatcher;
import com.vanitycraft.VanityControlPoints.Events.PlayerContendPointEvent;
import com.vanitycraft.VanityControlPoints.Events.PlayerLeavePointEvent;
import com.vanitycraft.VanityControlPoints.Events.PlayerStartCaptureEvent;
import com.vanitycraft.VanityControlPoints.Listeners.CaptureListener;
import com.vanitycraft.VanityControlPoints.Listeners.LeavePointListener;
import com.vanitycraft.VanityControlPoints.Listeners.PointContendListener;
import com.vanitycraft.VanityControlPoints.Listeners.StartCaptureListener;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class VanityControlPoints extends JavaPlugin {
	public static VanityControlPoints PLUGIN;

	public static List<Point> POINTS_ON_COOLDOWN = new ArrayList<Point>();
	public static List<Point> POINTS = new ArrayList<Point>();
	public static List<Player> COOLDOWN_NOTIFIED = new ArrayList<Player>();
	public static List<ItemStack> PRIZES = new ArrayList<ItemStack>();

	public static HashMap<Point, List<Player>> POINTS_IN_CONTENTION = new HashMap<Point, List<Player>>();
	public static HashMap<Point, Player> POINTS_IN_CAPTURE = new HashMap<Point, Player>();
	public static HashMap<Point, Player> LAST_TO_CONTROL = new HashMap<Point, Player>();

	public static int COOLDOWN_TIME = 10;
	public static int CAPTURE_TIME = 5;

	@Override
	public void onEnable() {
		PLUGIN = this;

		setupConfigFiles();
		loadPointsFromFile();

		// Register event listeners
		Bukkit.getServer().getPluginManager().registerEvents(new StartCaptureListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CaptureListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LeavePointListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PointContendListener(), this);

		// Register commands
		getCommand("point").setExecutor(new CommandDispatcher());

		checkPoints();
	}

	public void loadPointsFromFile() {
		File file = new File(getDataFolder(), "points.yml");

		YamlConfiguration points = YamlConfiguration.loadConfiguration(file);

		for (String point : points.getConfigurationSection("Points").getKeys(false)) {
			ConfigurationSection sec = points.getConfigurationSection("Points." + point);

			String name = point;
			String world = sec.getString("World");

			int pos1X = sec.getInt("pos1.X");
			int pos1Y = sec.getInt("pos1.Y");
			int pos1Z = sec.getInt("pos1.Z");

			int pos2X = sec.getInt("pos2.X");
			int pos2Y = sec.getInt("pos2.Y");
			int pos2Z = sec.getInt("pos2.Z");

			Location pos1 = new Location(Bukkit.getWorld(world), pos1X, pos1Y, pos1Z);
			Location pos2 = new Location(Bukkit.getWorld(world), pos2X, pos2Y, pos2Z);

			Point newPoint = new Point(name, world, pos1, pos2);

			POINTS.add(newPoint);
		}
	}

	public void setupTimers() {
		COOLDOWN_TIME = getConfig().getInt("Point-Cooldown");
		CAPTURE_TIME = getConfig().getInt("Cooldown-Time");
	}

	public void setupPrizes() {
//		for (String prize : getConfig().getKeys(false)) {
//			ConfigurationSection sec = getConfig().getConfigurationSection(prize);
//		}
	}

	public void setupConfigFiles() {
		if (!getDataFolder().exists()) {
			File file = new File(getDataFolder(), "points.yml");

			try {
				YamlConfiguration points = YamlConfiguration.loadConfiguration(file);

				points.createSection("Points");

				points.save(file);

				getConfig().options().copyDefaults(true);
				saveConfig();
			} catch (IOException e) {
				Bukkit.getLogger().log(Level.SEVERE, "There was a problem creating points.yml");
			}
		} else {
			File points = new File(getDataFolder(), "points.yml");
			File config = new File(getDataFolder(), "config.yml");

			if (!points.exists()) {
				try {
					YamlConfiguration pointsConfig = YamlConfiguration.loadConfiguration(points);

					pointsConfig.createSection("Points");

					pointsConfig.save(points);
				} catch (IOException e) {
					Bukkit.getLogger().log(Level.SEVERE, "There was a problem creating points.yml");
				}
			}

			if (!config.exists()) {
				getConfig().options().copyDefaults(true);
				saveConfig();
			}
		}
	}

	private void checkPoints() {
		new BukkitRunnable() {
			public void run() {
				for (Player player : Bukkit.getOnlinePlayers()) {
					Location pLoc = player.getLocation();

					for (Point point : POINTS) {
						Vector pVec = pLoc.toVector();

						if (pVec.isInAABB(point.getPosition1().toVector(), point.getPosition2().toVector())
								&& !POINTS_ON_COOLDOWN.contains(point)) {
							if (!POINTS_IN_CAPTURE.containsKey(point) && !POINTS_IN_CONTENTION.containsKey(point)) {
								PlayerStartCaptureEvent e = new PlayerStartCaptureEvent(point, player);

								Bukkit.getPluginManager().callEvent(e);
							} else if (POINTS_IN_CAPTURE.containsKey(point) && POINTS_IN_CAPTURE.get(point) == player
									&& LeavePointListener.GRACE.containsKey(player)) {
								LeavePointListener.GRACE.get(player).cancel();

								LeavePointListener.GRACE.remove(player);
							} else if (POINTS_IN_CAPTURE.containsKey(point)
									&& !POINTS_IN_CONTENTION.containsKey(point)) {
								Player capturer = POINTS_IN_CAPTURE.get(point);

								if (capturer != player) {

									PlayerContendPointEvent e = new PlayerContendPointEvent(player, point);

									Bukkit.getPluginManager().callEvent(e);
								}
							}
						}

						if (pVec.isInAABB(point.getPosition1().toVector(), point.getPosition2().toVector())
								&& POINTS_ON_COOLDOWN.contains(point) && !COOLDOWN_NOTIFIED.contains(player)) {
							COOLDOWN_NOTIFIED.add(player);

							player.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
									+ "]: This point is currently on cooldown.");

							new BukkitRunnable() {
								public void run() {
									COOLDOWN_NOTIFIED.remove(player);
								}
							}.runTaskLater(PLUGIN, (1 * 60) * 20);
						}

						if (!pVec.isInAABB(point.getPosition1().toVector(), point.getPosition2().toVector())) {
							if (POINTS_IN_CAPTURE.containsKey(point) && POINTS_IN_CAPTURE.get(point) == player
									&& !LeavePointListener.GRACE.containsKey(POINTS_IN_CAPTURE.get(point))
									&& !LeavePointListener.GRACE.containsKey(player)
									&& !LAST_TO_CONTROL.containsKey(point)) {
								PlayerLeavePointEvent e = new PlayerLeavePointEvent(player, point);

								Bukkit.getPluginManager().callEvent(e);
							} else if (POINTS_IN_CONTENTION.containsKey(point)
									&& !LeavePointListener.GRACE.containsKey(POINTS_IN_CAPTURE.get(point))
									&& !LeavePointListener.GRACE.containsKey(player)
									&& !LAST_TO_CONTROL.containsKey(point)) {
								List<Player> contendingPlayers = POINTS_IN_CONTENTION.get(point);

								if (contendingPlayers.contains(player)) {
									PlayerLeavePointEvent e = new PlayerLeavePointEvent(player, point);

									Bukkit.getPluginManager().callEvent(e);
								}
							}
						}
					}
				}
			}
		}.runTaskTimer(PLUGIN, 0, 3);
	}
}
