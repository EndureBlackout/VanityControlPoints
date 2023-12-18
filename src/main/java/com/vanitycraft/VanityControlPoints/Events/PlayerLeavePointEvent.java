package com.vanitycraft.VanityControlPoints.Events;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.vanitycraft.VanityControlPoints.Models.Point;

public class PlayerLeavePointEvent extends Event implements Cancellable {
	private static final HandlerList handlers = new HandlerList();
	
	private boolean cancelled;
	private Player player;
	private Point point;
	
	public PlayerLeavePointEvent(Player player, Point point) {
		this.player = player;
		this.point = point;
		this.cancelled = false;
	}
	
	public Player getPlayer() {
		return player;
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
