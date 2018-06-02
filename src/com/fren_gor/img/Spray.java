package com.fren_gor.img;

import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.HashMap;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.fren_gor.img.events.InteractItemFrameEvent;
import com.fren_gor.libraries.RandomString;

public class Spray implements Listener, CommandExecutor {

	static {

		Bukkit.getPluginManager().registerEvents(new Spray(), Main.getInstance());

	}

	private static HashMap<Player, Image> h = new HashMap<>();

	public static void setSpray(Player p, String imageName) throws IOException {
		setSpray(p, ImgAPI.getImage(imageName));
	}

	public static void setSpray(Player p, Image image) {

		if (h.containsKey(p)) {
			h.remove(p);
		}

		if (!ImgAPI.getImages().containsValue(image)) {
			ImgAPI.loadImage(image, new RandomString(8).nextString() + ".png");
		}

		h.put(p, image);

	}

	static BufferedImage deepCopy(BufferedImage bi) {
		ColorModel cm = bi.getColorModel();
		boolean isAlphaPremultiplied = cm.isAlphaPremultiplied();
		WritableRaster raster = bi.copyData(null);
		return new BufferedImage(cm, raster, isAlphaPremultiplied, null);
	}

	public static void removeSpray(Player p) {

		if (h.containsKey(p)) {
			h.remove(p);
		}

	}

	@EventHandler
	public void onPlace(InteractItemFrameEvent e) {

		if (!Main.h.containsKey(e.getClicked().getUniqueId())) {
			return;
		}

		if (e.getPlaceMode() || ImgAPI.getImageName(e.getClicked()) == null
				|| !ImgAPI.getImageName(e.getClicked()).equals("")) {
			return;
		}

		if (e.getClicker().hasPermission("img.spray") && h.containsKey(e.getClicker())) {

			ImgAPI.setMap(deepCopy((BufferedImage) h.get(e.getClicker())), e.getClicked());
			ImgAPI.resetOnRestart(e.getClicked());

		}

	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		if (label.equalsIgnoreCase("setimage") || label.equalsIgnoreCase("imgplacer:setimage")) {

			if (args.length < 2) {

				sender.sendMessage("§cUsage: /setimage <player> <imageName>");
				return false;

			}

			Player p = null;
			try {
				p = Bukkit.getPlayer(args[0]);
			} catch (Exception e) {
				sender.sendMessage("§cPlayer not online!");
				return false;
			}

			if (!ImgAPI.getImages().containsKey(args[1])) {
				sender.sendMessage("§cCan't found Image. Invalid Image Name");
				return false;
			}

			try {
				setSpray(p, args[1]);
			} catch (IOException e) {
				sender.sendMessage("§cInvalid Image Name");
			}

			sender.sendMessage("§aSuccesfully setted spray §e" + args[1] + " §ato player " + args[0]);

		} else if (label.equalsIgnoreCase("removeimage") || label.equalsIgnoreCase("imgplacer:removeimage")) {

			if (args.length < 1) {

				sender.sendMessage("§cUsage: /removeimage <player>");
				return false;

			}

			Player p = null;
			try {
				p = Bukkit.getPlayer(args[0]);
			} catch (Exception e) {
				sender.sendMessage("§cPlayer not online!");
				return false;
			}

			removeSpray(p);

			sender.sendMessage("§aSuccesfully removed spray from player " + args[0]);

		}
		return false;
	}

}
