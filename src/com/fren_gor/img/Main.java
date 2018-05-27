package com.fren_gor.img;

import java.awt.Image;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.hanging.HangingBreakEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener {

	static List<Player> lp = new ArrayList<>();
	static HashMap<String, Image> li = new HashMap<>();
	static HashMap<UUID, String> h = new HashMap<>();
	static List<UUID> lh = new ArrayList<>();

	@EventHandler
	public void onHit(EntityDamageEvent e) {
		if (e.getEntity().getType() != EntityType.ITEM_FRAME) {
			return;
		}
		ItemFrame i = (ItemFrame) e.getEntity();
		if (h.containsKey(i.getUniqueId())) {
			e.setCancelled(true);
		}
	}

	@EventHandler
	public void onMove(PlayerMoveEvent ev) {

		for (Entity e : ev.getPlayer().getWorld().getNearbyEntities(ev.getPlayer().getLocation(), 100, 100, 100)) {

			if (lh.contains(e.getUniqueId())) {
				continue;
			}

			if (e.getType() == EntityType.ITEM_FRAME) {

				if (h.keySet().contains(e.getUniqueId())) {
					UUID u = e.getUniqueId();
					if (h.containsKey(u)) {

						if (h.get(u) == null || h.get(u).equals("")) {
							continue;
						}
						if (!li.keySet().contains(h.get(u))) {
							((ItemFrame) e).setItem(new ItemStack(Material.AIR));
							continue;
						}
						((ItemFrame) e).setItem(new ItemStack(Material.MAP, 1, new Map(li.get(h.get(u))).data));
						lh.add(e.getUniqueId());
					}
				}
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public void onEnable() {
		pl = this;
		if (!getDataFolder().exists()) {
			getDataFolder().mkdirs();
		}
		for (File f : getDataFolder().listFiles()) {
			if (f.getName().equals("saves.dat")) {
				continue;
			}
			Image img = null;
			try {
				img = ImageIO.read(f);
			} catch (Exception e) {
				continue;
			}
			li.put(f.getName(), img);

		}
		File f = new File(getDataFolder(), "saves.dat");
		if (f.exists()) {
			ObjectInputStream out = null;
			try {
				out = new ObjectInputStream(new FileInputStream(f));
				h = (HashMap<UUID, String>) out.readObject();
			} catch (ClassNotFoundException | IOException e1) {
				e1.printStackTrace();
			}
			for (World w : Bukkit.getWorlds()) {
				for (Entity e : w.getEntities()) {
					if (e.getType() != EntityType.ITEM_FRAME) {
						continue;
					}
					UUID u = e.getUniqueId();
					if (h.containsKey(u)) {

						if (h.get(u) == null || h.get(u).equals("")) {
							continue;
						}
						if (!li.keySet().contains(h.get(u))) {
							((ItemFrame) e).setItem(new ItemStack(Material.AIR));
							continue;
						}
						((ItemFrame) e).setItem(new ItemStack(Material.MAP, 1, new Map(li.get(h.get(u))).data));

					}

				}
			}
		}

		Bukkit.getPluginManager().registerEvents(this, this);
	}

	@Override
	public void onDisable() {

		File f = new File(getDataFolder(), "saves.dat");
		ObjectOutputStream out = null;
		try {
			out = new ObjectOutputStream(new FileOutputStream(f));
			out.writeObject(h);
			out.close();
		} catch (IOException e) {
			e.printStackTrace();
		}

		h.clear();
		lp.clear();
		li.clear();
		lh.clear();

	}

	private boolean thereAreitemFrame(Location l) {
		for (Entity e : l.getWorld().getEntities()) {
			if (e.getType() == EntityType.ITEM_FRAME && e.getLocation().getBlock().getLocation().equals(l)) {
				return true;
			}
		}
		return false;
	}

	public static boolean clearItem(Material m, int amount, Player p) {
		if (m == Material.AIR || amount < 0) {
			return false;
		}
		if (!p.getInventory().containsAtLeast(new ItemStack(m), amount)) {
			p.sendMessage(ChatColor.DARK_RED + "Insufficent Materials");
			return false;
		}
		Inventory inv = p.getInventory();
		inv.removeItem(new ItemStack[] { new ItemStack(m, amount) });
		return true;
	}

	@EventHandler
	public void onH(HangingBreakEvent e) {
		if (e.getEntity().getType() != EntityType.ITEM_FRAME) {
			return;
		}
		ItemFrame i = (ItemFrame) e.getEntity();
		if (h.containsKey(i.getUniqueId())) {

			e.setCancelled(true);

		}
	}

	@EventHandler
	public void onSpawn(PlayerInteractEvent e) {
		if (e.getAction() != Action.RIGHT_CLICK_BLOCK) {
			return;
		}
		if (e.getBlockFace() == BlockFace.DOWN || e.getBlockFace() == BlockFace.UP) {
			return;
		}
		if (lp.contains(e.getPlayer()) && e.getItem() != null && e.getItem().getType() == Material.ITEM_FRAME
				&& !thereAreitemFrame(e.getClickedBlock().getRelative(e.getBlockFace()).getLocation())) {

			e.setCancelled(true);
			if (e.getPlayer().getGameMode() == GameMode.SPECTATOR
					|| e.getPlayer().getGameMode() == GameMode.ADVENTURE) {

				return;

			} else if (e.getPlayer().getGameMode() != GameMode.CREATIVE) {

				clearItem(Material.ITEM_FRAME, 1, e.getPlayer());

			}

			ItemFrame i = (ItemFrame) e.getClickedBlock().getLocation().getWorld().spawnEntity(
					e.getClickedBlock().getRelative(e.getBlockFace()).getLocation(), EntityType.ITEM_FRAME);
			i.setFacingDirection(e.getBlockFace());
			h.put(i.getUniqueId(), "");
			MapData[] c = new MapData[li.size()];
			int in = 0;
			for (String s : li.keySet()) {
				c[in] = new MapData(s);
				in++;
			}
			MenuUtil m = new MenuUtil(e.getPlayer(), i, c);

			e.getPlayer().openInventory(m.nextPage());
		}

	}

	@EventHandler
	public void interact(PlayerInteractEntityEvent e) {
		Set<UUID> list = h.keySet();
		if (list.contains(e.getRightClicked().getUniqueId())) {
			e.setCancelled(true);
			if (!lp.contains(e.getPlayer())) {
				return;
			}

			if (e.getPlayer().isSneaking()) {

				h.remove(e.getRightClicked().getUniqueId());
				e.getRightClicked().remove();

			} else {
				MapData[] c = new MapData[li.size()];
				int in = 0;
				for (String s : li.keySet()) {
					c[in] = new MapData(s);
					in++;
				}
				MenuUtil m = new MenuUtil(e.getPlayer(), (ItemFrame) e.getRightClicked(), c);
				e.getPlayer().openInventory(m.nextPage());

			}

		}
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("placemap") || label.equalsIgnoreCase("imgplacer:placemap")) {
			if (!(sender instanceof Player)) {
				sender.sendMessage("You must be a player for do /placemap");
				return false;
			}
			Player p = (Player) sender;

			if (lp.contains(p)) {

				lp.remove(p);
				p.sendMessage("§bRemoved from placer!");

			} else {

				lp.add(p);
				p.sendMessage("§bAdded to placer!");

			}

			return true;

		} else if (label.equalsIgnoreCase("reloadmaps") || label.equalsIgnoreCase("imgplacer:reloadmaps")) {
			
			if (!getDataFolder().exists()) {
				getDataFolder().mkdirs();
			}
			for (File f : getDataFolder().listFiles()) {
				if (f.getName().equals("saves.dat")) {
					continue;
				}
				Image img = null;
				try {
					img = ImageIO.read(f);
				} catch (Exception e) {
					continue;
				}
				li.put(f.getName(), img);

			}
			
			sender.sendMessage("§aMaps reloaded");
		}
		return false;
	}

	public static ItemStack craftItem(Material m, byte data, String name, String... lore) {

		ItemStack i = new ItemStack(m, 1, data);
		ItemMeta meta = i.getItemMeta();
		meta.setDisplayName(name);
		meta.setLore(Arrays.asList(lore));
		i.setItemMeta(meta);
		i = MapUtil.setEnch(i, false);
		return i;

	}

	public static Plugin getInstance() {
		return pl;
	}

	private static Plugin pl;
	static HashMap<UUID, MenuUtil> menus = new HashMap<>();

}
