package com.vanitycraft.VanityControlPoints.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerLeavePointEvent;

import de.netzkronehd.wgregionevents.events.RegionLeftEvent;

public class LeaveRegionListener implements Listener {
	@EventHandler
	public void onLeaveRegionEvent(RegionLeftEvent e) {
		VanityControlPoints.POINTS.forEach((point) -> {
			if (point.getRegion().getId().equalsIgnoreCase(e.getRegion().getId())) {
				if (VanityControlPoints.POINTS_IN_CAPTURE.containsKey(point)
						|| VanityControlPoints.POINTS_IN_CONTENTION.containsKey(point)) {
					Bukkit.getPluginManager().callEvent(new PlayerLeavePointEvent(e.getPlayer(), point));
					
					return;
				}
			}
		});
	}
}
