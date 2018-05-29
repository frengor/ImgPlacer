package com.fren_gor.img;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Material;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;

public class Config {

	public static void load() {
		if(!new File(Main.getInstance().getDataFolder(), "config.yml").exists()){
			Main.getInstance().saveResource("config.yml", true);
		}
		try {
			f.load(new File(Main.getInstance().getDataFolder(), "config.yml"));
		} catch (IOException | InvalidConfigurationException e) {
			e.printStackTrace();
		}
	}

	private static YamlConfiguration f = new YamlConfiguration();

	public static boolean getAutoUpdater() {
		return f.getBoolean("Autp-Updater");
	}

	public static boolean getShowImage() {
		return f.getBoolean("Show-Image-With-No-Perms");
	}

	public static boolean getWhitelistBlocks() {
		return f.getBoolean("Whitelist-Blocks");
	}

	public static List<Material> getList() {
		List<String> l = f.getStringList("Block-List");
		List<Material> lm = new ArrayList<>();
		for (String g : l) {
			if (Material.valueOf(g) != null) {
				lm.add(Material.valueOf(g));
			}
		}
		return lm;
	}

}
