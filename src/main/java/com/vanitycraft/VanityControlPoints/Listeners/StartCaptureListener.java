package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitTask;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerStartCaptureEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class StartCaptureListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerStartCapture(PlayerStartCaptureEvent e) {

		if (VanityControlPoints.POINTS_ON_COOLDOWN.contains(e.getPoint())) {
			e.setCancelled(true);

			if (!VanityControlPoints.COOLDOWN_NOTIFIED.contains(e.getCapturer())) {
				e.getCapturer().sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
						+ "]: This point is currently on cooldown.");

				VanityControlPoints.COOLDOWN_NOTIFIED.add(e.getCapturer());

				Bukkit.getScheduler().runTaskLaterAsynchronously(VanityControlPoints.PLUGIN, () -> {
					VanityControlPoints.COOLDOWN_NOTIFIED.remove(e.getCapturer());
				}, 1200);
			}

			return;
		}

		if (LeavePointListener.GRACE.containsKey(e.getCapturer())) {
			BukkitTask task = LeavePointListener.GRACE.get(e.getCapturer());
			
			task.cancel();
			
			LeavePointListener.GRACE.remove(e.getCapturer());
			
			return;
		}

		Point point = e.getPoint();
		Player capturer = e.getCapturer();

		if(VanityControlPoints.ACTIVE_POINT != null && VanityControlPoints.ACTIVE_POINT.equals(point)) {
			point.startCapture(capturer);
	
			VanityControlPoints.POINTS_IN_CAPTURE.put(point, capturer);
			VanityControlPoints.PRIZE_RECEIVERS.add(capturer);
			
			if(VanityControlPoints.ROTATION_TASK != null) {
				VanityControlPoints.ROTATION_TASK.cancel();
			}
	
			Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + capturer.getName()
					+ " has started capturing " + point.getName() + " you have " + VanityControlPoints.CAPTURE_TIME / (60*20)
					+ " minutes to stop them!");
		}
	}
}
