package com.vanitycraft.VanityControlPoints.Commands;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;

public class CommandDispatcher implements CommandExecutor, Listener {

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (sender instanceof Player) {
			Player p = (Player) sender;
			
			if(cmd.getName().equalsIgnoreCase("point")) {
				if(args.length == 2) {
					if(args[0].equalsIgnoreCase("create")) {
						AdminCommands.CreatePoint(p, args[1]);
					}
					
					if(args[0].equalsIgnoreCase("pos1")) {
						String name = args[1];
						
						AdminCommands.SetPosition(p, name, 1);
					}
					
					if(args[0].equalsIgnoreCase("pos2")) {
						String name = args[1];
						
						AdminCommands.SetPosition(p, name, 2);
					}
				}
				
				if(args.length == 3) {
					if(args[0].equalsIgnoreCase("prize") && args[1].equalsIgnoreCase("add")) {
						String name = args[2];
						ItemStack prize = p.getInventory().getItemInMainHand();
						
						// Send prize add command
					}
				}
			}
		}

		return true;
	}

}
