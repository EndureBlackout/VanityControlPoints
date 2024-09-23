package com.vanitycraft.VanityControlPoints.Listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.vanitycraft.VanityControlPoints.VanityControlPoints;
import com.vanitycraft.VanityControlPoints.Events.PlayerCapturePointEvent;
import com.vanitycraft.VanityControlPoints.Models.Point;
import com.vanitycraft.VanityControlPoints.Models.Prize;

import de.tr7zw.nbtapi.NBTItem;

public class CaptureListener implements Listener {

	@EventHandler
	public void onCapture(PlayerCapturePointEvent e) {
		Point point = e.getPoint();
		Player capturer = e.getCapturer();

		Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + capturer.getName()
				+ " has captured " + point.getName() + "!");

		givePlayerRewards();

		point.setContendingPlayer(null);

		VanityControlPoints.POINTS_IN_CAPTURE.remove(point);

		VanityControlPoints.ACTIVE_POINT = null;
		VanityControlPoints.ROTATION_TASK.cancel();

		new BukkitRunnable() {
			public void run() {
				VanityControlPoints.ACTIVE_POINT = VanityControlPoints.pickRandomPoint();

				Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: "
						+ VanityControlPoints.ACTIVE_POINT.getName() + " is now active. Go capture it! /point finder to track it down!");

				VanityControlPoints.PLUGIN.startCapturePointRotations();
			}
		}.runTaskLaterAsynchronously(VanityControlPoints.PLUGIN, VanityControlPoints.COOLDOWN_TIME);
	}

	private void givePlayerRewards() {
		for (Player player : VanityControlPoints.PRIZE_RECEIVERS) {
			for (Prize prize : VanityControlPoints.PRIZES) {
				Random rand = new Random();
				float chance = rand.nextFloat();
				float prizeChance = prize.getChance();

				if (chance <= prizeChance) {					
					NBTItem nbtPrize = new NBTItem(prize.getItem());

					player.getInventory().addItem(nbtPrize.getItem());
				}
			}
		}
	}
}