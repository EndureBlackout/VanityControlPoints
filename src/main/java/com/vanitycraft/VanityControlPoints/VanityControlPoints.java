package com.vanitycraft.VanityControlPoints;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.palmergames.bukkit.towny.TownyAPI;
import com.sk89q.worldguard.WorldGuard;
import com.vanitycraft.VanityControlPoints.Commands.CommandDispatcher;
import com.vanitycraft.VanityControlPoints.Listeners.CaptureListener;
import com.vanitycraft.VanityControlPoints.Listeners.CompassListener;
import com.vanitycraft.VanityControlPoints.Listeners.EnterRegionListener;
import com.vanitycraft.VanityControlPoints.Listeners.LeavePointListener;
import com.vanitycraft.VanityControlPoints.Listeners.LeaveRegionListener;
import com.vanitycraft.VanityControlPoints.Listeners.PointContendListener;
import com.vanitycraft.VanityControlPoints.Listeners.StartCaptureListener;
import com.vanitycraft.VanityControlPoints.Models.Compass;
import com.vanitycraft.VanityControlPoints.Models.Point;
import com.vanitycraft.VanityControlPoints.Models.Prize;
import com.vanitycraft.VanityControlPoints.Objects.Laser;

import de.tr7zw.nbtapi.NBTItem;
import su.nightexpress.excellentcrates.CratesAPI;
import su.nightexpress.excellentcrates.CratesPlugin;
import su.nightexpress.excellentcrates.key.CrateKey;

public class VanityControlPoints extends JavaPlugin {
	public static VanityControlPoints PLUGIN;
	public static CratesPlugin CRATES_API;

	public static Point ACTIVE_POINT;
	public static BukkitTask ROTATION_TASK;

	public static List<Point> POINTS_ON_COOLDOWN = new ArrayList<Point>();
	public static List<Point> POINTS = new ArrayList<Point>();
	public static List<Player> COOLDOWN_NOTIFIED = new ArrayList<Player>();
	public static List<Prize> PRIZES = new ArrayList<Prize>();
	public static List<Player> PRIZE_RECEIVERS = new ArrayList<Player>();

	public static Compass COMPASS_ITEM;

	public static Laser LASER;

	public static HashMap<Point, List<Player>> POINTS_IN_CONTENTION = new HashMap<Point, List<Player>>();
	public static HashMap<Point, Player> POINTS_IN_CAPTURE = new HashMap<Point, Player>();
	public static HashMap<Point, Player> LAST_TO_CONTROL = new HashMap<Point, Player>();

	public static int COOLDOWN_TIME = 10;
	public static int CAPTURE_TIME = 5;

	public static WorldGuard WORLD_GUARD;
	public static TownyAPI TOWNY;

	@Override
	public void onEnable() {
		PLUGIN = this;
		CRATES_API = CratesAPI.PLUGIN;

		// Get the WorldGuard plugin
		VanityControlPoints.WORLD_GUARD = WorldGuard.getInstance();
		VanityControlPoints.TOWNY = TownyAPI.getInstance();

		// Setup the required stuff
		setupConfigFiles();
		loadPointsFromFile();
		setupTimers();
		setupPrizes();
		setupCompass();

		ACTIVE_POINT = pickRandomPoint();
		
		startCapturePointRotations();

		// Register event listeners
		Bukkit.getServer().getPluginManager().registerEvents(new StartCaptureListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CaptureListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LeavePointListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new PointContendListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new EnterRegionListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new LeaveRegionListener(), this);
		Bukkit.getServer().getPluginManager().registerEvents(new CompassListener(), this);

		// Register commands
		getCommand("point").setExecutor(new CommandDispatcher());
		getCommand("laser").setExecutor(new CommandDispatcher());
	}

	public void loadPointsFromFile() {
		File file = new File(getDataFolder(), "points.yml");

		YamlConfiguration points = YamlConfiguration.loadConfiguration(file);

		if (!VanityControlPoints.POINTS.isEmpty()) {
			VanityControlPoints.POINTS = new ArrayList<Point>();
		}

		for (String point : points.getConfigurationSection("Points").getKeys(false)) {
			ConfigurationSection sec = points.getConfigurationSection("Points." + point);

			String name = point;
			String world = sec.getString("World");

			String regionName = sec.getString("Region");

			Point newPoint = new Point(name, world, regionName);

			POINTS.add(newPoint);
		}
	}

	public void setupTimers() {
		COOLDOWN_TIME = (60*60*20) * getConfig().getInt("Point-Cooldown");
		CAPTURE_TIME = getConfig().getInt("Capture-Time") * 60;
	}

	public void setupPrizes() {
		for (String prize : getConfig().getConfigurationSection("Prizes").getKeys(false)) {
			ConfigurationSection sec = getConfig().getConfigurationSection("Prizes." + prize);
			
			if(sec.contains("KeyId")) {
				CrateKey key = CRATES_API.getKeyManager().getKeyById(sec.getString("KeyId"));
				int itemAmount = sec.getInt("Amount");
				float chance = Float.parseFloat(sec.getString("Chance"));
				
				ItemStack keyItem = key.getItem();
				keyItem.setAmount(itemAmount);
				
				Prize newPrize = new Prize(prize, chance, keyItem);
				
				PRIZES.add(newPrize);
				
				continue;
			}

			String itemName = sec.getString("Name");
			Material itemMaterial = Material.getMaterial(sec.getString("Item"));
			int itemAmount = sec.getInt("Amount");
			float chance = Float.parseFloat(sec.getString("Chance"));

			ItemStack newPrize = new ItemStack(itemMaterial, itemAmount);
			ItemMeta prizeMeta = newPrize.getItemMeta();

			if (sec.isSet("Name")) {
				prizeMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', itemName));
			}

			if (!sec.getStringList("Lore").isEmpty()) {
				List<String> newLore = new ArrayList<String>();
				for (String loreLine : sec.getStringList("Lore")) {
					newLore.add(ChatColor.translateAlternateColorCodes('&', loreLine));
				}

				prizeMeta.setLore(newLore);
			}

			if (!sec.getStringList("Echantments").isEmpty()) {
				for (String enchant : sec.getStringList("Enchantments")) {
					String[] splitEnchant = enchant.split(",");

					String enchantName = splitEnchant[0];
					int enchantLevel = Integer.parseInt(splitEnchant[1]);

					prizeMeta.addEnchant(Enchantment.getByKey(NamespacedKey.minecraft(enchantName)), enchantLevel,
							true);
				}
			}

			newPrize.setItemMeta(prizeMeta);

			if (sec.isSet("Voucher")) {
				NBTItem nbtItem = new NBTItem(newPrize);

				nbtItem.setString("voucher", sec.getString("Voucher"));

				newPrize = nbtItem.getItem();
			}

			Prize prizeObj = new Prize(prize, chance, newPrize);

			PRIZES.add(prizeObj);
		}
	}
	
	public void startCapturePointRotations() {
		ROTATION_TASK = new BukkitRunnable() {
			public void run() {
		    	  VanityControlPoints.ACTIVE_POINT = VanityControlPoints.pickRandomPoint();
		    	  
		    	  Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + ACTIVE_POINT.getName()
		        + " is now active. /point finder to track it down!");
			}
		}.runTaskTimerAsynchronously(PLUGIN, COOLDOWN_TIME, COOLDOWN_TIME);
	}

	public void setupCompass() {
		ConfigurationSection compassSec = getConfig().getConfigurationSection("Compass");
		
		String name = compassSec.getString("Display");
		List<String> lore = compassSec.getStringList("Lore");
		
		Compass compass = new Compass(name, lore);
		
		COMPASS_ITEM = compass;
	}

	public static Point pickRandomPoint() {
		Random rand = new Random();

		return VanityControlPoints.POINTS.get(rand.nextInt(VanityControlPoints.POINTS.size()));
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
}
