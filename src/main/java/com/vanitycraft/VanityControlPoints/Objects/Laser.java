package com.vanitycraft.VanityControlPoints.Objects;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Guardian;
import org.bukkit.entity.Squid;

public class Laser {
	private Guardian guardian;
	private Squid squid;

	public Laser(Location laserBeginLocation, Location laserEndLocation) {
		guardian = (Guardian) laserBeginLocation.getWorld().spawnEntity(laserBeginLocation, EntityType.GUARDIAN);

		guardian.setInvisible(true);
		guardian.setGravity(false);
		guardian.setSilent(true);
		guardian.setInvulnerable(true);
		guardian.setCanPickupItems(false);
		guardian.setCollidable(false);
		guardian.setAI(false);

		squid = (Squid) laserEndLocation.getWorld().spawnEntity(laserEndLocation, EntityType.SQUID);

		squid.setInvisible(true);
		squid.setInvulnerable(true);
		squid.setGravity(false);
		squid.setSilent(true);
	}

	public boolean activateLaser() {
		guardian.setTarget(squid);
		return guardian.setLaser(true);
	}

	public boolean disableLaser() {
		guardian.remove();
		squid.remove();

		return guardian.setLaser(false);
	}
}
