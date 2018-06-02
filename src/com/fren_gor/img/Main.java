package com.fren_gor.img;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.URL;
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
import org.bukkit.plugin.java.JavaPlugin;

import com.fren_gor.img.events.InteractItemFrameEvent;
import com.fren_gor.img.events.RemoveItemFrameEvent;
import com.fren_gor.libraries.org.inventivetalent.update.spiget.SpigetUpdate;
import com.fren_gor.libraries.org.inventivetalent.update.spiget.UpdateCallback;
import com.fren_gor.libraries.org.inventivetalent.update.spiget.comparator.VersionComparator;

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

		Bukkit.getPluginCommand("setimage").setExecutor(new Spray());
		Bukkit.getPluginCommand("removeimage").setExecutor(new Spray());

		Config.load();

		if (Config.getAutoUpdater()) {
			SpigetUpdate updater = new SpigetUpdate(this, 57221);

			// This converts a semantic version to an integer and checks if the
			// updated version is greater
			updater.setVersionComparator(VersionComparator.SEM_VER);

			updater.checkForUpdate(new UpdateCallback() {
				@Override
				public void updateAvailable(String newVersion, String downloadUrl, boolean hasDirectDownload) {
					//// A new version is available
					// newVersion - the latest version
					// downloadUrl - URL to the download
					// hasDirectDownload - whether the update is available for a
					//// direct download on spiget.org
					Bukkit.getConsoleSender().sendMessage("§eImgPlacer is updating!");
					if (hasDirectDownload) {
						if (updater.downloadUpdate()) {
							// Update downloaded, will be loaded when the server
							// restarts
							Bukkit.getConsoleSender()
									.sendMessage("§bUpdate downloaded, will be loaded when the server restarts");
						} else {
							// Update failed
							getLogger().warning("Update download failed, reason is " + updater.getFailReason());
						}
					}
				}

				@Override
				public void upToDate() {
					//// Plugin is up-to-date
					Bukkit.getConsoleSender().sendMessage("§bImgPlacer is up to date!");
				}
			});
		}
		for (File f : getDataFolder().listFiles()) {
			if (f.getName().equals("saves.dat") || f.getName().equals("config.yml")) {
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

	// Tested with Imgur (https://imgur.com/)
	public static boolean download(String URL, String filename) {
		try {
			if (!filename.endsWith(".png")) {
				filename = filename + ".png";
			}
			URL url = new URL(URL);
			BufferedImage img = ImageIO.read(url);
			File file = new File(getInstance().getDataFolder(), filename);
			ImageIO.write(img, "png", file);
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
		return true;
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

			if (Config.getWhitelistBlocks() && !Config.getList().contains(e.getClickedBlock().getType())) {
				return;
			}

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
				InteractItemFrameEvent event1 = new InteractItemFrameEvent(e.getPlayer(),
						(ItemFrame) e.getRightClicked(), false);
				
				Bukkit.getPluginManager().callEvent(event1);
				return;
			}

			if (e.getPlayer().isSneaking()) {

				RemoveItemFrameEvent event = new RemoveItemFrameEvent(e.getPlayer(), (ItemFrame) e.getRightClicked());

				Bukkit.getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					return;
				}

				h.remove(e.getRightClicked().getUniqueId());
				e.getRightClicked().remove();

			} else {

				InteractItemFrameEvent event = new InteractItemFrameEvent(e.getPlayer(),
						(ItemFrame) e.getRightClicked(), true);

				Bukkit.getPluginManager().callEvent(event);

				if (event.isCancelled()) {
					return;
				}

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
			Config.load();
			li.clear();
			for (File f : getDataFolder().listFiles()) {
				if (f.getName().equals("saves.dat") || f.getName().equals("config.yml")) {
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
		} else if (label.equalsIgnoreCase("imagedownload") || label.equalsIgnoreCase("imgplacer:imagedownload")) {
			if (args.length > 2) {
				sender.sendMessage("§cUsage: /imagedownload <link> <imageName>");
				return false;
			}
			if (download(args[0], args[1])) {
				li.clear();
				for (File f : getDataFolder().listFiles()) {
					if (f.getName().equals("saves.dat") || f.getName().equals("config.yml")) {
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

				sender.sendMessage("§aSuccesfully downloaded image §e" + args[1]);

			} else {

				sender.sendMessage("§cCannot download image. Make sure that the download link is correct");
				return false;

			}

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

	public static Main getInstance() {
		return pl;
	}

	private static Main pl;
	static HashMap<UUID, MenuUtil> menus = new HashMap<>();

}
