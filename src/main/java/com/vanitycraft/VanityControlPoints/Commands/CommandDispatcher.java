package com.vanitycraft.VanityControlPoints.Commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

import net.md_5.bungee.api.ChatColor;

public class CommandDispatcher implements CommandExecutor, Listener {

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (sender instanceof Player) {
      Player p = (Player) sender;

      if (cmd.getName().equalsIgnoreCase("point")) {
        if (args.length == 2) {
          if (args[0].equalsIgnoreCase("create")) {
            AdminCommands.CreatePoint(p, args[1]);
          }

          if (args[0].equalsIgnoreCase("pos1")) {
            String name = args[1];

            AdminCommands.SetPosition(p, name, 1);
          }

          if (args[0].equalsIgnoreCase("pos2")) {
            String name = args[1];

            AdminCommands.SetPosition(p, name, 2);
          }
        }

        if (args.length == 4) {
          if (args[0].equalsIgnoreCase("prize") && args[1].equalsIgnoreCase("add")) {
            try {
              String name = args[2];
              float chance = Float.parseFloat(args[3]);
              ItemStack prize = p.getInventory().getItemInMainHand();

              AdminCommands.AddPrize(p, name, chance, prize);
            } catch (NumberFormatException e) {
              p.sendMessage(ChatColor.RED + "Usage: /point prize add <name> <chance>");
            }
          }
        }
      }
    }

    return true;
  }

}
