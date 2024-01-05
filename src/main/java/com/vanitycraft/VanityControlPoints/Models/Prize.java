package com.vanitycraft.VanityControlPoints.Models;

import org.bukkit.inventory.ItemStack;

public class Prize {
  private String name;
  private float chance;
  private ItemStack item;
  
  public Prize(String name, float chance, ItemStack item) {
    this.name = name;
    this.chance = chance;
    this.item = item;
  }
  
  public String getName() {
    return name;
  }
  
  public float getChance() {
    return chance;
  }
  
  public ItemStack getItem() {
    return item;
  }
}
