package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerCapturePointEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class CaptureListener implements Listener {

	@EventHandler
	public void onCapture(PlayerCapturePointEvent e) {
		Point point = e.getPoint();
		Player capturer = e.getCapturer();
		
		Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + capturer.getName()
		+ " has captured " + point.getName() + "!");
		
		VanityControlPoints.POINTS_IN_CAPTURE.remove(point);
		VanityControlPoints.POINTS_ON_COOLDOWN.add(point);
		
		new BukkitRunnable() {
			public void run() {
				VanityControlPoints.POINTS_ON_COOLDOWN.remove(point);
			}
		}.runTaskLater(VanityControlPoints.PLUGIN, (VanityControlPoints.COOLDOWN_TIME*60)*20);
	}
}
