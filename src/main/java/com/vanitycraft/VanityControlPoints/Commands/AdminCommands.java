package com.vanitycraft.VanityControlPoints.Commands;

import java.io.File;
import java.io.IOException;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;

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
			
			sender.sendMessage(ChatColor.GREEN + pointName + " has been created. You must set the bounds for it to be active.");
		} catch (IOException e) {
			sender.sendMessage(ChatColor.RED + "There was an error creating this control point.");
		}

	}
	
	public static void SetPosition(Player sender, String pointName, int position) {
		File file = new File(plugin.getDataFolder(), "points.yml");
		YamlConfiguration points = YamlConfiguration.loadConfiguration(file);
		
		if(!points.getConfigurationSection("Points").getKeys(false).contains(pointName)) {
			sender.sendMessage(ChatColor.RED + "That point does not exist.");
			
			return;
		}
		
		if(position == 1) {
			ConfigurationSection point = points.getConfigurationSection("Points." + pointName);
			
			Location pos1 = sender.getLocation();
			
			point.set("pos1.X", pos1.getX());
			point.set("pos1.Y", pos1.getY());
			point.set("pos1.Z", pos1.getZ());
			
			try {
				points.save(file);
				
				sender.sendMessage(ChatColor.GREEN + "Position 1 was set successfully.");
			} catch (IOException e) {
				sender.sendMessage(ChatColor.RED + "There was a problem setting position 1 for " + pointName);
			}
		}
		
		if(position == 2) {
			ConfigurationSection point = points.getConfigurationSection("Points." + pointName);
			
			Location pos2 = sender.getLocation();
			
			point.set("pos2.X", pos2.getX());
			point.set("pos2.Y", pos2.getY());
			point.set("pos2.Z", pos2.getZ());
			
			try {
				points.save(file);
				
				sender.sendMessage(ChatColor.GREEN + "Position 2 was set successfully.");
			} catch (IOException e) {
				sender.sendMessage(ChatColor.RED + "There was a problem setting position 2 for " + pointName);
			}
		}
	}
}
