package com.vanitycraft.VanityControlPoints.Listeners;

import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitRunnable;

import com.palmergames.bukkit.towny.TownyAPI;
import com.palmergames.bukkit.towny.exceptions.NotRegisteredException;
import com.palmergames.bukkit.towny.object.Nation;
import com.palmergames.bukkit.towny.object.Resident;
import com.palmergames.bukkit.towny.object.Town;
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
    Resident playerRes = TownyAPI.getInstance().getResident(capturer);
    Town playerTown = null;
    Nation playerNation = null;

    if (playerRes.hasTown()) {
      try {
        playerTown = playerRes.getTown();

        if (playerTown.hasNation()) {
          playerNation = playerTown.getNation();
        }

      } catch (NotRegisteredException e1) {
        e1.printStackTrace();
      }
    }

    Bukkit.broadcastMessage("[" + ChatColor.RED + "ControlPoint" + ChatColor.RESET + "]: " + capturer.getName()
        + " has captured " + point.getName() + "!");
    
    if(playerNation != null) {
      giveNationRewards(playerNation);
    } else if (playerNation == null && playerTown != null) {
      giveTownRewards(playerTown);
    } else {
      givePlayerRewards(capturer);
    }

    VanityControlPoints.POINTS_IN_CAPTURE.remove(point);
    VanityControlPoints.POINTS_ON_COOLDOWN.add(point);

    new BukkitRunnable() {
      public void run() {
        VanityControlPoints.POINTS_ON_COOLDOWN.remove(point);
      }
    }.runTaskLater(VanityControlPoints.PLUGIN, (VanityControlPoints.COOLDOWN_TIME * 60) * 20);
  }
  
  private void giveNationRewards(Nation nation) {
    for(Resident nationRes : nation.getResidents()) {
      Player nationPlayer = nationRes.getPlayer();
      
      for (Prize prize : VanityControlPoints.PRIZES) {
        Random rand = new Random();
        float chance = rand.nextFloat();
        float prizeChance = prize.getChance();

        if (chance <= prizeChance) {
          NBTItem nbtPrize = new NBTItem(prize.getItem());

          nationPlayer.getInventory().addItem(nbtPrize.getItem());
        }
      }
    }
  }
  
  private void giveTownRewards(Town town) {
    for(Resident townRes : town.getResidents()) {
      Player townPlayer = townRes.getPlayer();
      
      for (Prize prize : VanityControlPoints.PRIZES) {
        Random rand = new Random();
        float chance = rand.nextFloat();
        float prizeChance = prize.getChance();

        if (chance <= prizeChance) {
          NBTItem nbtPrize = new NBTItem(prize.getItem());

          townPlayer.getInventory().addItem(nbtPrize.getItem());
        }
      }
    }
  }
  
  private void givePlayerRewards(Player player) {
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