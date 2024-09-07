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

			if (cmd.getName().equalsIgnoreCase("laser") && p.hasPermission("vanitycontrolpoints.admin")) {
				if (args[0].equalsIgnoreCase("test")) {
					AdminCommands.testLaser(p);
				} else if (args[0].equalsIgnoreCase("stop") && p.hasPermission("vanitycontrolpoints.admin")) {
					AdminCommands.disableLaser(p);
				}
			}

			if (cmd.getName().equalsIgnoreCase("point")) {
				
				if(args.length == 1) {
					if(args[0].equalsIgnoreCase("finder")) {
						UserCommands.getPointCompass(p);
					}
					
					if(args[0].equalsIgnoreCase("active") && sender.hasPermission("vanitycontrolpoints.admin")) {
						AdminCommands.activePoint(p);
					}
					
					if(args[0].equalsIgnoreCase("list") && sender.hasPermission("vanitycontrolpoints.admin")) {
						AdminCommands.pointList(p);
					}
				}
				
				if (args.length == 2) {
					if (args[0].equalsIgnoreCase("create") && sender.hasPermission("vanitycontrolpoints.admin")) {
						AdminCommands.CreatePoint(p, args[1]);
					}
				}
				
				if (args.length == 3) {
					if(args[0].equalsIgnoreCase("prize") && args[1].equalsIgnoreCase("remove") && sender.hasPermission("vanitycontrolpoints.admin")) {
						String name = args[2];
						
						AdminCommands.RemovePrize(p, name);
					}
				}
				
				if (args.length == 4) {
					if (args[0].equalsIgnoreCase("region") && args[1].equalsIgnoreCase("set") && sender.hasPermission("vanitycontrolpoints.admin")) {
						AdminCommands.SetRegion(p, args[2], args[3]);
					}

					if (args[0].equalsIgnoreCase("prize") && args[1].equalsIgnoreCase("add") && sender.hasPermission("vanitycontrolpoints.admin")) {
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
