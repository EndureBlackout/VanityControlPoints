package com.vanitycraft.VanityControlPoints.Models;

import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class Compass {
	private String name;
	private List<String> lore;
	
	public Compass(String name, List<String> lore) {
		this.name = ChatColor.translateAlternateColorCodes('&', name);
		this.lore = lore;
		
		for(int i = 0; i < lore.size(); i++) {
			lore.set(i, ChatColor.translateAlternateColorCodes('&', lore.get(i)));
		}
	}
	
	public ItemStack getCompass() {
		ItemStack compass = new ItemStack(Material.COMPASS);
		ItemMeta compassMeta = compass.getItemMeta();
		
		compassMeta.setLore(lore);
		compassMeta.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
		
		compass.setItemMeta(compassMeta);
		
		return compass;
	}
	
	public String getName() {
		return name;
	}
}
