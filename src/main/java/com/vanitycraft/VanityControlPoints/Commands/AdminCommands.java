package com.vanitycraft.VanityControlPoints.Commands;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Models.Point;
import com.vanitycraft.VanityControlPoints.Models.Prize;
import com.vanitycraft.VanityControlPoints.Objects.Laser;

import de.tr7zw.nbtapi.NBTItem;
import su.nightexpress.excellentcrates.CratesAPI;
import su.nightexpress.excellentcrates.key.CrateKey;

public class AdminCommands {
	private static VanityControlPoints plugin = VanityControlPoints.PLUGIN;

	public static void CreatePoint(Player sender, String pointName) {
		File file = new File(plugin.getDataFolder(), "points.yml");
		YamlConfiguration points = YamlConfiguration.loadConfiguration(file);

		if (points.getKeys(false).contains(pointName)) {
			sender.sendMessage(ChatColor.RED + "A control point with that name already exists.");
			return;
		}

		ConfigurationSection point = points.createSection("Points." + pointName);
		point.set("World", sender.getWorld().getName());

		try {
			points.save(file);

			sender.sendMessage(
					ChatColor.GREEN + pointName + " has been created. You must set the bounds for it to be active.");
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "There was an error creating this control point.");
		}

	}

	public static void SetRegion(Player sender, String pointName, String regionId) {
		File file = new File(plugin.getDataFolder(), "points.yml");
		YamlConfiguration points = YamlConfiguration.loadConfiguration(file);

		if (!points.getConfigurationSection("Points").getKeys(false).contains(pointName)) {
			sender.sendMessage(ChatColor.RED + "The point does not exits.");

			return;
		}

		RegionContainer container = VanityControlPoints.WORLD_GUARD.getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(sender.getWorld()));

		if (!regions.hasRegion(regionId)) {
			sender.sendMessage(ChatColor.RED + "No region with the given id.");

			return;
		}

		ConfigurationSection point = points.getConfigurationSection("Points." + pointName);

		point.set("Region", regionId);

		try {
			points.save(file);

			plugin.loadPointsFromFile();

			sender.sendMessage(ChatColor.GREEN + "Region has been set successfully.");
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "There was a problem setting the region " + regionId + " for the point "
					+ pointName);
		}
	}

	public static void AddPrize(Player sender, String name, float chance, ItemStack item) {
		ConfigurationSection prizes = plugin.getConfig().getConfigurationSection("Prizes");

		NBTItem nbtItem = new NBTItem(item);
		ItemMeta itemMeta = item.getItemMeta();
		int amount = item.getAmount();

//		new NamespacedKey();
//		NamespacedKey nameKey = new NamespacedKey(, "crate_key.id");
//		CustomItemTagContainer tags = itemMeta.getCustomTagContainer();

		if (nbtItem.hasTag("voucher")) {
			prizes.set(name + ".Voucher", nbtItem.getString("voucher"));
		}

		if (CratesAPI.getKeyManager().isKey(item)) {
			CrateKey actualKey = CratesAPI.getKeyManager().getKeyByItem(item);
			Map<String, CrateKey> keys = CratesAPI.getKeyManager().getKeysMap();
			String keyId = null;
			
			for(Entry<String, CrateKey> key : keys.entrySet()) {
				if(key.getValue().getItem().equals(actualKey.getItem())) {
					keyId = key.getKey();
				}
			}
			
			prizes.set(name + ".KeyId", keyId);
			prizes.set(name + ".Amount", amount);
			prizes.set(name + ".Chance", chance);

			sender.sendMessage(ChatColor.GREEN + "Key added successfully.");

			CrateKey key = VanityControlPoints.CRATES_API.getKeyManager().getKeyById(keyId);

			ItemStack keyItem = key.getItem();
			keyItem.setAmount(amount);

			Prize newPrize = new Prize(name, chance, keyItem);

			VanityControlPoints.PRIZES.add(newPrize);

			plugin.saveConfig();

			return;
		}

		if (itemMeta.hasEnchants()) {
			List<String> enchantList = new ArrayList<String>();
			for (Entry<Enchantment, Integer> enchant : itemMeta.getEnchants().entrySet()) {
				String key = enchant.getKey().getKey().getKey();
				int level = enchant.getValue();

				enchantList.add(key + "," + level);
			}

			prizes.set(name + ".Enchantments", enchantList);
		}

		if (itemMeta.hasLore()) {
			List<String> lore = new ArrayList<String>();

			for (String loreLine : itemMeta.getLore()) {
				lore.add(loreLine);
			}

			prizes.set(name + ".Lore", lore);
		}

		prizes.set(name + ".Amount", amount);

		if (itemMeta.hasDisplayName()) {
			prizes.set(name + ".Name", itemMeta.getDisplayName());
		}

		prizes.set(name + ".Chance", chance);
		prizes.set(name + ".Item", item.getType().toString());

		plugin.saveConfig();

		Prize newPrize = new Prize(name, chance, nbtItem.getItem());

		VanityControlPoints.PRIZES.add(newPrize);

		sender.sendMessage(ChatColor.GREEN + "Item added successfully.");
	}

	public static void RemovePrize(Player sender, String prizeName) {
		ConfigurationSection prizes = plugin.getConfig().getConfigurationSection("Prizes");

		if (prizes.contains(prizeName)) {
			boolean deleted = VanityControlPoints.PRIZES.removeIf(x -> x.getName() != null && x.getName().equals(prizeName));

			if (deleted) {
				prizes.set(prizeName, null);

				plugin.saveConfig();

				sender.sendMessage(ChatColor.GREEN + "Prize removed successfully.");
			} else {
				sender.sendMessage(ChatColor.RED + "No prize by that name.");
			}
		}

	}

	public static void testLaser(Player sender) {
		Location endLocation = new Location(sender.getWorld(), sender.getLocation().getX(),
				sender.getLocation().getY() + 100, sender.getLocation().getZ());

		Laser laser = new Laser(sender.getLocation(), endLocation);

		if (laser.activateLaser()) {
			sender.sendMessage(ChatColor.GREEN + "Should be working!");
		} else {
			sender.sendMessage(ChatColor.RED + "Broken I guess");
		}

		VanityControlPoints.LASER = laser;
	}

	public static void activePoint(Player sender) {
		Point point = VanityControlPoints.ACTIVE_POINT;

		sender.sendMessage(ChatColor.GREEN + "The active point is currently: " + point.getName());
	}

	public static void pointList(Player sender) {
		StringBuilder builder = new StringBuilder();

		builder.append(ChatColor.GREEN + "The current active point list is: ");

		for (Point point : VanityControlPoints.POINTS) {
			builder.append(", " + point.getName());
		}

		sender.sendMessage(builder.toString());
	}

	public static void disableLaser(Player sender) {
		VanityControlPoints.LASER.disableLaser();

		VanityControlPoints.LASER = null;
	}
}
