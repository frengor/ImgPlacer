package com.fren_gor.img;

import java.util.Arrays;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import com.fren_gor.Skull.Skull;

public class MenuUtil implements Listener {

	private int page = 0;
	private ItemFrame it;

	private final HashMap<Integer, HashMap<Integer, MapData>> pages = new HashMap<>();

	private final Player p;

	private static int[] slots = new int[] { 10, 11, 12, 13, 14, 15, 16, 19, 20, 21, 22, 23, 24, 25, 28, 29, 30, 31, 32,
			33, 34 };

	@EventHandler
	public void onInvInteract(InventoryClickEvent e) {
		if (e.getClickedInventory() == null) {
			return;
		}
		if (e.getClickedInventory().getTitle().equals("§a§lSelect Map")) {
			e.setCancelled(true);
		} else {
			return;
		}
		if (!e.getWhoClicked().getUniqueId().equals(p.getUniqueId()) || !e.getAction().toString().contains("PICKUP"))
			return;
		if (e.getCurrentItem() == null) {
			return;
		}

		for (Integer c : pages.get(page).keySet()) {

			if (e.getRawSlot() == c) {

				MapData o = pages.get(page).get(c);

				it.setItem(new ItemStack(Material.MAP, 1, new Map(Main.li.get(o.s)).data));
				if (Main.h.containsKey(it.getUniqueId()))
					Main.h.remove(it.getUniqueId());
				Main.h.put(it.getUniqueId(), o.s);
				p.sendMessage("§aSelected map: §e" + o.s);
				p.closeInventory();
				return;
			}

		}

	}

	public MenuUtil(Player p, ItemFrame it, MapData... c) {

		this.p = p;
		int ii = 1;
		int i = 0;
		this.it = it;

		pages.put(1, new HashMap<Integer, MapData>());
		for (MapData o : c) {

			if (i == 21) {
				ii++;
				pages.put(ii, new HashMap<Integer, MapData>());

				i = 0;

			}

			pages.get(ii).put(slots[i], o);

			i++;

		}

		if (pages == null || pages.size() == 0 || pages.values() == null || pages.values().size() == 0) {
			pages.put(1, new HashMap<Integer, MapData>());
		}
		Bukkit.getPluginManager().registerEvents(this, Main.getInstance());
		loop();
	}

	private boolean b = true;

	private final static ClickableItem left;
	private final static ClickableItem right;
	private final static ItemStack gray;
	private final static ItemStack white;

	public static ItemStack craftCustomSkull(String url, String name, String... lore) {

		ItemStack i = Skull.getCustomSkull(url);

		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		i.setItemMeta(meta);
		i = MapUtil.setEnch(i, false);
		return i;

	}

	static {
		left = new ClickableItem(MapUtil.setName(MapUtil.getArrowWoodLeft(), "§9Pagina Precedente")) {

			@Override
			public void onClick(Player p) {

				if (Main.menus.containsKey(p.getUniqueId())) {
					((MenuUtil) Main.menus.get(p.getUniqueId())).prevPage();
				}

			}
		};
		right = new ClickableItem(MapUtil.setName(MapUtil.getArrowWoodRight(), "§9Prossima Pagina")) {

			@Override
			public void onClick(Player p) {

				if (Main.menus.containsKey(p.getUniqueId())) {
					((MenuUtil) Main.menus.get(p.getUniqueId())).nextPage();
				}

			}
		};
		gray = Main.craftItem(Material.STAINED_GLASS_PANE, (byte) 7, " ");
		white = Main.craftItem(Material.STAINED_GLASS_PANE, (byte) 0, " ");
	}

	public boolean isChangingPage() {
		return b;
	}

	private Inventory getBaseInv() {
		Inventory inv = Bukkit.createInventory(null, 54, "§a§lSelect Map");

		ItemStack paper = MapUtil.setDisplayName(new ItemStack(Material.PAPER), "§ePagina ");

		inv.setItem(18, left.getItemStack());
		inv.setItem(26, right.getItemStack());
		inv.setItem(48, white);
		inv.setItem(49, paper);
		inv.setItem(50, white);

		inv.setItem(0, gray);
		inv.setItem(1, gray);
		inv.setItem(2, gray);
		inv.setItem(3, gray);
		inv.setItem(4, gray);
		inv.setItem(5, gray);
		inv.setItem(6, gray);
		inv.setItem(7, gray);
		inv.setItem(8, gray);
		inv.setItem(9, gray);
		inv.setItem(17, gray);
		inv.setItem(27, gray);
		inv.setItem(35, gray);
		inv.setItem(36, gray);
		inv.setItem(37, gray);
		inv.setItem(38, gray);
		inv.setItem(39, gray);
		inv.setItem(40, gray);
		inv.setItem(41, gray);
		inv.setItem(42, gray);
		inv.setItem(43, gray);
		inv.setItem(44, gray);

		inv.setItem(45, white);
		inv.setItem(46, white);
		inv.setItem(47, white);
		inv.setItem(51, white);
		inv.setItem(52, white);
		inv.setItem(53, white);

		return inv;
	}

	public Inventory nextPage(boolean bool) {
		b = true;
		Inventory inv = getBaseInv();
		page++;
		if (page <= 1) {
			inv.setItem(18, gray);
			page = 1;
		}
		if (page == pages.size()) {
			inv.setItem(26, gray);
		}

		for (Integer c : pages.get(page).keySet()) {

			inv.setItem(c, MapUtil.getMapWithLore(pages.get(page).get(c), p));

		}
		inv.setItem(49,
				MapUtil.setDisplayName(new ItemStack(Material.PAPER, page), "§ePagina " + String.valueOf(page)));
		if (bool)
			p.openInventory(inv);
		b = false;
		return inv;
	}

	public Inventory nextPage() {
		return nextPage(true);
	}

	public Inventory prevPage() {
		return prevPage(true);
	}

	public Inventory prevPage(boolean bool) {
		b = true;
		Inventory inv = getBaseInv();
		page--;
		if (page <= 1) {
			inv.setItem(18, gray);
			page = 1;
		}
		if (page == pages.size()) {
			inv.setItem(26, gray);
		}

		for (Integer c : pages.get(page).keySet()) {

			inv.setItem(c, MapUtil.getMapWithLore(pages.get(page).get(c), p));

		}
		inv.setItem(49,
				MapUtil.setDisplayName(new ItemStack(Material.PAPER, page), "§ePagina " + String.valueOf(page)));
		if (bool)
			p.openInventory(inv);
		b = false;
		return inv;
	}

	public int getPage() {
		return this.page;
	}

	private void loop() {
		new BukkitRunnable() {

			@Override
			public void run() {
				if (p.getOpenInventory().getTopInventory() == null
						|| !p.getOpenInventory().getTopInventory().getTitle().equals("§a§lSelect Map")) {

					unregister();
					cancel();

				}

			}
		}.runTaskTimerAsynchronously(Main.getInstance(), 0, 1);
	}

	public synchronized void unregister() {
		InventoryClickEvent.getHandlerList().unregister(this);
	}

}
