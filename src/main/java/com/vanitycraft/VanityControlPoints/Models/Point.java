package com.vanitycraft.VanityControlPoints.Models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerCapturePointEvent;

public class Point {
	private String name;
	private String world;
	private Location pos1;
	private Location pos2;
	private BukkitTask captureCountdown;
	
	public Point(String name, String world, Location pos1, Location pos2) {
		this.name = name;
		this.setWorld(world);
		this.pos1 = pos1;
		this.pos2 = pos2;
	}
	
	public Point(String name, String world) {
		this.name = name;
		this.setWorld(world);
	}
	
	public void setPosition1(Location pos1) {
		this.pos1 = pos1;
	}
	
	public void setPosition2(Location pos2) {
		this.pos2 = pos2;
	}
	
	public Location getPosition1() {
		return pos1;
	}
	
	public Location getPosition2() {
		return pos2;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
	
	public void startCapture(Player p) {
		Point point = this;
		captureCountdown = new BukkitRunnable() {
			public void run() {
				PlayerCapturePointEvent e = new PlayerCapturePointEvent(point, p);
				
				Bukkit.getPluginManager().callEvent(e);
			}
		}.runTaskLater(VanityControlPoints.PLUGIN, (VanityControlPoints.CAPTURE_TIME*60)*20);
	}
	
	public void cancelCapture() {
		captureCountdown.cancel();
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}
}
