package com.vanitycraft.VanityControlPoints.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.vanitycraft.VanityControlPoints.Models.Point;

/**
 * This event is fired when a player starts a capture on a control point.
 * Canceling this event will stop the capture from starting.
 */
public class PlayerStartCaptureEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	private Point point;
	private Player capturer;
	
	public PlayerStartCaptureEvent(Point point, Player capturer) {
		this.point = point;
		this.capturer = capturer;
		this.cancelled = false;
	}
	
	public Point getPoint() {
		return point;
	}
	
	public Player getCapturer() {
		return capturer;
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
