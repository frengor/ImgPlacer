package com.fren_gor.img;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.annotation.Nullable;
import javax.imageio.ImageIO;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.ItemFrame;
import org.bukkit.inventory.ItemStack;

public final class ImgAPI {

	public static List<ItemFrame> getItemFrames() {
		Set<UUID> h = Main.h.keySet();
		List<ItemFrame> l = new ArrayList<>();
		for (World w : Bukkit.getWorlds()) {
			for (Entity e : w.getEntities()) {
				if (e.getType() != EntityType.ITEM_FRAME) {
					continue;
				}
				UUID u = e.getUniqueId();
				if (h.contains(u)) {

					l.add((ItemFrame) e);

				}

			}
		}
		return l;
	}

	@Nullable
	public static String getImageName(ItemFrame i) {

		return Main.h.get(i.getUniqueId());

	}

	public static Image download(String URL, File file) throws IOException {

		URL url = new URL(URL);
		BufferedImage img = ImageIO.read(url);
		ImageIO.write(img, "png", file);

		return img;
	}

	public static Image getImage(String name) throws IOException {

		return ImageIO.read(new File(Main.getInstance().getDataFolder(), name));

	}

	public static HashMap<String, Image> getImages() {
		HashMap<String, Image> h = new HashMap<>();
		for (String s : Main.li.keySet()) {
			h.put(s, Main.li.get(s));
		}
		return h;
	}

	public static ItemFrame spawnItemFrame(Location l, BlockFace bf) {

		ItemFrame i = (ItemFrame) l.getWorld().spawnEntity(l, EntityType.ITEM_FRAME);
		i.setFacingDirection(bf);
		Main.h.put(i.getUniqueId(), "");
		return i;
	}

	public static Map setMap(String imageName, ItemFrame... i) throws IOException {

		Map m = new Map(getImage(imageName));
		for (ItemFrame it : i) {
			it.setItem(new ItemStack(Material.MAP, 1, new Map(Main.li.get(Main.h.get(it.getUniqueId()))).data));
			if (Main.h.containsKey(it.getUniqueId())) {
				Main.h.remove(it.getUniqueId());
			}
			Main.h.put(it.getUniqueId(), imageName);
		}
		return m;

	}

	public static void removeItemFrame(ItemFrame i) {
		if (Main.h.containsKey(i.getUniqueId()))
			Main.h.remove(i.getUniqueId());

		if (Main.lh.contains(i.getUniqueId()))
			Main.lh.remove(i.getUniqueId());

		i.remove();

	}

	public static void loadImage(Image img, String imageName) {

		Main.li.put(imageName, img);

	}

	public static void unloadImage(String imageName) {
		if (Main.li.containsKey(imageName)) {
			Main.li.remove(imageName);
		}
	}

}
