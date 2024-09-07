package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;

public class CompassListener implements Listener {
	@EventHandler
	public void onCompassIntereact(PlayerInteractEvent e) {
		if ((e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
				&& e.getPlayer().getInventory().getItemInMainHand().getType().equals(Material.COMPASS)) {

			ItemStack compass = e.getPlayer().getInventory().getItemInMainHand();
			ItemMeta compassMeta = compass.getItemMeta();

			Player p = e.getPlayer();

			if (VanityControlPoints.COMPASS_ITEM.getName().equalsIgnoreCase(compassMeta.getDisplayName())) {
				if(VanityControlPoints.ACTIVE_POINT == null) {
					p.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
							+ "]: There is no active point currently. Try again later.");
				} else {
					p.setCompassTarget(VanityControlPoints.ACTIVE_POINT.getCenterLocation());

					p.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
							+ "]: Your compass has been calibrated to the active point!");
				}
			}
		}
	}
}
