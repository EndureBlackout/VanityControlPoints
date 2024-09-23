package com.vanitycraft.VanityControlPoints.Models;

import org.bukkit.entity.Player;

public class CapturingPlayer {
    private Player capturer;
    private Point point;
    private int timeToCapture;
    private boolean isContested;

    public CapturingPlayer(Player capturer, Point point, int timeToCapture, boolean isContested) {
        this.capturer = capturer;
        this.point = point;
        this.timeToCapture = timeToCapture;
        this.isContested = isContested;
    }

    public Player getCapturer() {
        return this.capturer;
    }

    public Point getPoint() {
        return point;
    }

    public int getTimeToCapture() {
        return this.timeToCapture;
    }

    public void setTimeToCapture(int timeToCapture) {
        this.timeToCapture = timeToCapture;
    }

    public boolean isContested() {
        return this.isContested;
    }

    public void setContested(boolean contested) {
        this.isContested = contested;
    }
}
