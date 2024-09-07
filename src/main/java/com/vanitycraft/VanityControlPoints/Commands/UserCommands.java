package com.vanitycraft.VanityControlPoints.Commands;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;

public class UserCommands {
	public static void getPointCompass(Player sender) {
		
		sender.getInventory().addItem(VanityControlPoints.COMPASS_ITEM.getCompass());
		
		sender.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
				+ "]: You have received your" + VanityControlPoints.COMPASS_ITEM.getName() +  "!");
	}
}
