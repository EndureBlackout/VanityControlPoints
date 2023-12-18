package com.vanitycraft.VanityControlPoints.Listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerContendPointEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class PointContendListener implements Listener {
	
	@EventHandler
	public void onPointContent(PlayerContendPointEvent e) {
		Player player = e.getPlayer();
		Point point = e.getPoint();
		
		Player capturer = VanityControlPoints.POINTS_IN_CAPTURE.get(point);
		
		List<Player> playersContending = new ArrayList<Player>();
		
		playersContending.add(capturer);
		playersContending.add(player);
		
		VanityControlPoints.POINTS_IN_CAPTURE.remove(point);
		VanityControlPoints.POINTS_IN_CONTENTION.put(point, playersContending);
		VanityControlPoints.LAST_TO_CONTROL.put(point, capturer);
		
		new BukkitRunnable() {
			public void run() {
				VanityControlPoints.LAST_TO_CONTROL.remove(point);
			}
		}.runTaskLater(VanityControlPoints.PLUGIN, 30*20);
		
		point.cancelCapture();

		capturer.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: "
				+ player.getName() + " has contested the point!");

		player.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
				+ "]: You have contested the point!");
	}
}
