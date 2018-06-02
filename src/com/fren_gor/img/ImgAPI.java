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
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.fren_gor.libraries.RandomString;

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
	public static ItemFrame getItemFrame(UUID u) {
		for (ItemFrame i : getItemFrames()) {
			if (i.getUniqueId().equals(u))
				return i;
		}
		return null;
	}

	@Nullable
	public static String getImageName(ItemFrame i) {

		return Main.h.get(i.getUniqueId());

	}

	@Nullable
	public static String getImageName(Image i) {

		for (String s : Main.li.keySet()) {
			if (compareImages(Main.li.get(s), i)) {
				return s;
			}
		}

		return null;

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
		if (!l.getChunk().isLoaded()) {
			l.getChunk().load();
		}
		ItemFrame i = (ItemFrame) l.getWorld().spawnEntity(l, EntityType.ITEM_FRAME);
		i.setFacingDirection(bf);
		Main.h.put(i.getUniqueId(), "");
		return i;
	}

	public static Map setMap(String imageName, ItemFrame... i) throws IOException {

		Map m = new Map(getImage(imageName));
		for (ItemFrame it : i) {
			it.setItem(new ItemStack(Material.MAP, 1, m.data));
			if (Main.h.containsKey(it.getUniqueId())) {
				Main.h.remove(it.getUniqueId());
			}

			Main.h.put(it.getUniqueId(), imageName);

		}
		return m;

	}

	public static void setEmpty(ItemFrame... i) {

		for (ItemFrame it : i) {
			if (Main.h.containsKey(it.getUniqueId())) {
				Main.h.remove(it.getUniqueId());
			}

			Main.h.put(it.getUniqueId(), "");
			it.setItem(new ItemStack(Material.AIR));
			
		}

	}

	public static Map setMap(Image image, ItemFrame... i) {

		Map m = new Map(image);
		for (ItemFrame it : i) {
			it.setItem(new ItemStack(Material.MAP, 1, m.data));
			if (Main.h.containsKey(it.getUniqueId())) {
				Main.h.remove(it.getUniqueId());
			}

			if (getImageName(image) == null) {

				String randomString = new RandomString(8).nextString() + ".png";
				loadImage(image, randomString);
				Main.h.put(it.getUniqueId(), randomString);

			} else {

				Main.h.put(it.getUniqueId(), getImageName(image));

			}

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

	public static void setSpray(Player p, String imageName) throws IOException {
		Spray.setSpray(p, ImgAPI.getImage(imageName));
	}

	public static void setSpray(Player p, Image image) {

		Spray.setSpray(p, image);

	}

	public static void removeSpray(Player p) {

		Spray.removeSpray(p);

	}

	public static boolean compareImages(Image imgA, Image imgB) {
		return compareImages((BufferedImage) imgA, (BufferedImage) imgB);
	}

	/**
	 * Compares two images pixel by pixel.
	 *
	 * @param imgA
	 *            the first image.
	 * @param imgB
	 *            the second image.
	 * @return whether the images are both the same or not.
	 * @author Mr. Polywhirl
	 */
	public static boolean compareImages(BufferedImage imgA, BufferedImage imgB) {
		// The images must be the same size.
		if (imgA.getWidth() != imgB.getWidth() || imgA.getHeight() != imgB.getHeight()) {
			return false;
		}

		int width = imgA.getWidth();
		int height = imgA.getHeight();

		// Loop over every pixel.
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				// Compare the pixels for equality.
				if (imgA.getRGB(x, y) != imgB.getRGB(x, y)) {
					return false;
				}
			}
		}

		return true;
	}

}
