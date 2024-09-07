package com.vanitycraft.VanityControlPoints.Models;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;
import com.sk89q.worldguard.protection.regions.RegionContainer;
import com.sk89q.worldguard.protection.util.WorldEditRegionConverter;
import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerCapturePointEvent;
import com.vanitycraft.VanityControlPoints.Objects.Laser;

public class Point {
	private String name;
	private boolean isOnCooldown = false;
	private String world;
	private ProtectedRegion region;
	private BukkitTask captureCountdown;
	private Laser laser;

	public Point(String name, String world, String regionId) {
		this.name = name;
		this.setWorld(world);
		this.region = getRegionFromId(regionId);
	}

	private ProtectedRegion getRegionFromId(String regionId) {
		RegionContainer container = VanityControlPoints.WORLD_GUARD.getPlatform().getRegionContainer();
		RegionManager regions = container.get(BukkitAdapter.adapt(Bukkit.getWorld(world)));

		return regions.getRegion(regionId);
	}

	public Point(String name, String world) {
		this.name = name;
		this.setWorld(world);
	}

	public ProtectedRegion getRegion() {
		return region;
	}

	public void setCooldown(boolean isOnCooldown) {
		this.isOnCooldown = isOnCooldown;
	}

	public boolean isOnCoolDown() {
		return this.isOnCooldown;
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
		}.runTaskLater(VanityControlPoints.PLUGIN, VanityControlPoints.CAPTURE_TIME);
	}

//	public void showLaser() {
//		Location topLocation = new Location(Bukkit.getWorld(world), region.getMaximumPoint().getX(),
//				region.getMaximumPoint().getY(), region.getMaximumPoint().getZ());
//
//		Location minLocation = new Location(Bukkit.getWorld(world), region.getMinimumPoint().getX(),
//				region.getMinimumPoint().getY(), region.getMinimumPoint().getZ());
//
//		double midX = ((minLocation.getX() - topLocation.getBlockX()) / 2) + minLocation.getX();
//		double midY = ((minLocation.getY() - topLocation.getBlockY()) / 2) + minLocation.getY();
//		double midZ = ((minLocation.getZ() - topLocation.getBlockZ()) / 2) + minLocation.getZ();
//
//		Location midLoc = new Location(Bukkit.getWorld(world), midX, midY, midZ);
//		Location midLocTop = new Location(Bukkit.getWorld(world), midX, midY + 100, midZ);
//
//		Laser laser = new Laser(midLoc, midLocTop);
//
//		laser.activateLaser();
//
//		this.laser = laser;
//	}

	public Location getCenterLocation() {
		Region weRegion = WorldEditRegionConverter.convertToRegion(region);

		Location center = new Location(Bukkit.getWorld(world), weRegion.getCenter().getX(), weRegion.getCenter().getY(),
				weRegion.getCenter().getZ());

		return center;
	}

	public void hideLaser() {
		this.laser.disableLaser();

		this.laser = null;
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
