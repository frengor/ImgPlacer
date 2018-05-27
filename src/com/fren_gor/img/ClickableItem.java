package com.fren_gor.img;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class ClickableItem implements Listener {

	private final ItemStack item;

	public ClickableItem(ItemStack item) {
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		item = item.clone();
		ItemMeta m = item.getItemMeta();
		m.setDisplayName(m.getDisplayName() + "§n");
		while (names.contains(m.getDisplayName())) {
			m.setDisplayName(m.getDisplayName() + "§n");
		}
		item.setItemMeta(m);
		this.item = item;

	}

	@SuppressWarnings("deprecation")
	@EventHandler
	public final void onInvClick(InventoryClickEvent e) {
		if (e.getClickedInventory() == null || e.getCurrentItem() == null
				|| e.getCurrentItem().getType() == Material.AIR)
			return;
		if (e.getCurrentItem().getItemMeta() == null || e.getCurrentItem().getItemMeta().getDisplayName() == null)
			return;
		if (e.getCurrentItem().getItemMeta().getDisplayName().equals(this.item.getItemMeta().getDisplayName())
				&& e.getCurrentItem().getType() == this.item.getType()
				&& e.getCurrentItem().getData().getData() == this.item.getData().getData()
				&& e.getCurrentItem().getAmount() == this.item.getAmount()
				&& e.getCurrentItem().getDurability() == this.item.getDurability()) {
			e.setCancelled(true);
			Player p = (Player) e.getWhoClicked();
			onClick(p);
		}

	}

	@Override
	public boolean equals(Object o) {
		if (!(o instanceof ClickableItem))
			return false;

		ClickableItem i = (ClickableItem) o;
		if (i.item.isSimilar(this.item))
			return true;
		else
			return false;

	}

	private static List<String> names = new ArrayList<>();

	public final void unregister() {
		InventoryClickEvent.getHandlerList().unregister(this);
		names.remove(item.getItemMeta().getDisplayName());
	}

	public abstract void onClick(Player p);

	public final ItemStack getItemStack() {
		return item.clone();
	}

}
