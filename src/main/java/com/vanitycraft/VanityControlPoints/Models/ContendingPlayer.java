package com.vanitycraft.VanityControlPoints.Models;

import org.bukkit.entity.Player;

public class ContendingPlayer {
    private Player contender;
    private Point point;

    public ContendingPlayer(Player contender, Point point) {
        this.contender = contender;
        this.point = point;
    }

    public Player getContendingPlayer() {
        return contender;
    }

    public Point getPoint() {
        return point;
    }
}
