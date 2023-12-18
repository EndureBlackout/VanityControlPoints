package com.vanitycraft.VanityControlPoints.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.vanitycraft.VanityControlPoints.Models.Point;

/**
 * This is the event that gets fired when a player captures a control point
 * Canceling this will stop the point from being captured and will not give the player the prizes for capturing
 */
public class PlayerCapturePointEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private Point point;
	private Player capturer;
	private boolean cancelled;
	
	public PlayerCapturePointEvent(Point point, Player capturer) {
		this.point = point;
		this.capturer = capturer;
		this.cancelled = false;
	}
	
	public Player getCapturer() {
		return capturer;
	}
	
	public Point getPoint() {
		return point;
	}

	@Override
	public boolean isCancelled() {
		return cancelled;
	}

	@Override
	public void setCancelled(boolean cancel) {
		this.cancelled = cancel;
	}

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}
	
	public static HandlerList getHandlerList() {
		return handlers;
	}

}
