package com.vanitycraft.VanityControlPoints.Listeners;

import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerLeavePointEvent;
import com.vanitycraft.VanityControlPoints.Events.PlayerStartCaptureEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class LeavePointListener implements Listener {
	public static HashMap<Player, BukkitTask> GRACE = new HashMap<Player, BukkitTask>();

	@EventHandler
	public void onPointLeave(PlayerLeavePointEvent e) {
		Point point = e.getPoint();
		Player capturer = e.getPlayer();

		if (VanityControlPoints.POINTS_IN_CAPTURE.containsKey(point)
				&& VanityControlPoints.POINTS_IN_CAPTURE.get(point) == capturer && !GRACE.containsKey(capturer)) {
			capturer.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
					+ "]: You have left the point. You have 30 seconds to return or the capture will stop!");

			GRACE.put(capturer, new BukkitRunnable() {
				public void run() {
					VanityControlPoints.POINTS_IN_CAPTURE.remove(point);

					point.cancelCapture();
					GRACE.remove(capturer);

					capturer.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
							+ "]: You have left the point and are no longer capturing it!");
				}
			}.runTaskLater(VanityControlPoints.PLUGIN, 30 * 20));
		}

		if (VanityControlPoints.LAST_TO_CONTROL.containsKey(point)
				&& VanityControlPoints.LAST_TO_CONTROL.get(point) == capturer) {
			capturer.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
					+ "]: You have left the point while it was contested. You have 30 seconds to return or another player will start to capture it!");

			GRACE.put(capturer, new BukkitRunnable() {
				public void run() {
					VanityControlPoints.POINTS_IN_CAPTURE.remove(point);

					point.cancelCapture();
					GRACE.remove(capturer);

					capturer.sendMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET
							+ "]: You have left the point and are no longer contesting it!");
				}
			}.runTaskLater(VanityControlPoints.PLUGIN, 30 * 20));
		}

		if (VanityControlPoints.POINTS_IN_CONTENTION.containsKey(point)
				&& VanityControlPoints.POINTS_IN_CONTENTION.get(point).contains(capturer)) {
			List<Player> contendingPlayers = VanityControlPoints.POINTS_IN_CONTENTION.get(point);

			contendingPlayers.remove(capturer);

			Player newCapturer = contendingPlayers.get(0);

			VanityControlPoints.POINTS_IN_CONTENTION.remove(point);

			PlayerStartCaptureEvent event = new PlayerStartCaptureEvent(point, newCapturer);

			Bukkit.getPluginManager().callEvent(event);
		}
	}
}
