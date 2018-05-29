package com.fren_gor.img;

import java.util.Arrays;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class MapUtil {

	private final static ItemStack arrowWoodLeft;
	private final static ItemStack arrowWoodRight;
	private final static ItemStack barrier;

	static {

		arrowWoodLeft = MenuUtil.craftCustomSkull(
				"http://textures.minecraft.net/texture/3ebf907494a935e955bfcadab81beafb90fb9be49c7026ba97d798d5f1a23",
				"Left");
		arrowWoodRight = MenuUtil.craftCustomSkull(
				"http://textures.minecraft.net/texture/1b6f1a25b6bc199946472aedb370522584ff6f4e83221e5946bd2e41b5ca13b",
				"Right");
		barrier = Main.craftItem(Material.BARRIER, (byte) 0, "");

	}

	public static ItemStack setEnch(ItemStack item, boolean b) {
		ItemStack i = item.clone();
		if (b) {
			i.addUnsafeEnchantment(Enchantment.WATER_WORKER, 1);
		} else {
			i.removeEnchantment(Enchantment.WATER_WORKER);
		}
		ItemMeta m = i.getItemMeta();
		m.addItemFlags(ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES, ItemFlag.HIDE_DESTROYS,
				ItemFlag.HIDE_PLACED_ON, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_UNBREAKABLE);
		i.setItemMeta(m);
		return i;
	}

	public static ItemStack setName(ItemStack item, String display_name, String... lore) {
		ItemStack i = item.clone();
		i = setDisplayName(i, display_name);
		i = setLore(i, lore);
		return i;
	}

	public static ItemStack setLore(ItemStack item, String... s) {
		ItemStack i = item.clone();
		ItemMeta m = i.getItemMeta();
		m.setLore(Arrays.asList(s));
		i.setItemMeta(m);
		return i;

	}

	public static ItemStack setDisplayName(ItemStack item, String s) {
		ItemStack i = item.clone();
		ItemMeta m = i.getItemMeta();
		m.setDisplayName(s);
		i.setItemMeta(m);
		return i;
	}

	public static ItemStack getArrowWoodLeft() {
		return arrowWoodLeft.clone();
	}

	public static ItemStack getArrowWoodRight() {
		return arrowWoodRight.clone();
	}

	public static ItemStack getBarrier() {
		return barrier.clone();
	}

	public static ItemStack getMapWithLore(MapData m, Player p) {

		if (p.hasPermission("img.image") || p.hasPermission("img.image." + m.s)) {
			return setName(new ItemStack(Material.MAP), ChatColor.YELLOW.toString() + m.s, "", "§aClick to place this image");
		}

		return setName(new ItemStack(Material.MAP), ChatColor.RED.toString() + m.s, "",
				"§cYou don't have the permission" + "§cto place this image");

	}

}
