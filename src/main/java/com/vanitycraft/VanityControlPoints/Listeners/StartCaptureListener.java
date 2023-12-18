package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerStartCaptureEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;

public class StartCaptureListener implements Listener {

	@EventHandler(ignoreCancelled = true)
	public void onPlayerStartCapture(PlayerStartCaptureEvent e) {
		if(LeavePointListener.GRACE.containsKey(e.getCapturer())) {
			LeavePointListener.GRACE.remove(e.getCapturer());
			
			return;
		}
		
		Point point = e.getPoint();
		Player capturer = e.getCapturer();
		
		point.startCapture(capturer);
		
		VanityControlPoints.POINTS_IN_CAPTURE.put(point, capturer);

		Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + capturer.getName()
				+ " has started capturing " + point.getName() + " you have " + VanityControlPoints.CAPTURE_TIME
				+ " minutes to stop them!");
	}
}
