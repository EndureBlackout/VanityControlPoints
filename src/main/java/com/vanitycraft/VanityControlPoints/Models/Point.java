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
	private CapturingPlayer capturingPlayer;
	private ContendingPlayer contendingPlayer;
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

	public CapturingPlayer getCapturingPlayer() {
		return this.capturingPlayer;
	}

	public void setContendingPlayer(ContendingPlayer contendingPlayer) {
		this.contendingPlayer = contendingPlayer;
	}

	public ContendingPlayer getContendingPlayer() {
		return contendingPlayer;
	}

	public void startCapture(Player p) {
		Point point = this;

		if(capturingPlayer == null || (capturingPlayer != null && !capturingPlayer.getCapturer().equals(p))) {
			CapturingPlayer capturingPlayer = new CapturingPlayer(p, point, VanityControlPoints.CAPTURE_TIME, false);
			this.capturingPlayer = capturingPlayer;
		} else {
			capturingPlayer.setContested(false);
		}

		contendingPlayer = null;

		captureCountdown = new BukkitRunnable() {
			public void run() {
				int timeRemaining = capturingPlayer.getTimeToCapture() - 1;
				capturingPlayer.setTimeToCapture(timeRemaining);

				if(timeRemaining <= 0) {
					PlayerCapturePointEvent e = new PlayerCapturePointEvent(point, p);

					Bukkit.getPluginManager().callEvent(e);

					cancel();
				}
			}
		}.runTaskTimer(VanityControlPoints.PLUGIN, 20, 20);
	}

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
		this.capturingPlayer = null;
		captureCountdown.cancel();
	}

	public String getWorld() {
		return world;
	}

	public void setWorld(String world) {
		this.world = world;
	}
}
