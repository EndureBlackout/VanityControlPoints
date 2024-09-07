package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerContendPointEvent;
import com.vanitycraft.VanityControlPoints.Events.PlayerStartCaptureEvent;

import de.netzkronehd.wgregionevents.events.RegionEnteredEvent;

public class EnterRegionListener implements Listener {
	@EventHandler
	public void onRegionEnterEvent(RegionEnteredEvent e) {
		VanityControlPoints.POINTS.forEach((point) -> {
			if (point.getRegion().getId().equalsIgnoreCase(e.getRegion().getId())) {
				if (!VanityControlPoints.POINTS_IN_CONTENTION.containsKey(point)
						&& (!VanityControlPoints.POINTS_IN_CAPTURE.containsKey(point)
								|| LeavePointListener.GRACE.containsKey(e.getPlayer()))) {
					Bukkit.getPluginManager().callEvent(new PlayerStartCaptureEvent(point, e.getPlayer()));

					return;
				}

				if (VanityControlPoints.POINTS_IN_CAPTURE.containsKey(point)) {
					Bukkit.getPluginManager().callEvent(new PlayerContendPointEvent(e.getPlayer(), point));

					return;
				}
			}
		});
	}
}
