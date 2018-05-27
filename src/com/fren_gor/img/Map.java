package com.fren_gor.img;

import java.awt.Image;

import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.map.MapCanvas;
import org.bukkit.map.MapRenderer;
import org.bukkit.map.MapView;
import org.bukkit.map.MapView.Scale;

public class Map {

	private MapView map = Bukkit.getServer().createMap(Bukkit.getWorlds().get(0));

	@SuppressWarnings("deprecation")
	public Map(Image img) {

		map.setScale(Scale.FARTHEST);

		load(new Location(Bukkit.getWorlds().get(0), map.getCenterX(), 100, map.getCenterZ()));

		for (MapRenderer r : map.getRenderers()) {
			map.removeRenderer(r);
		}

		map.addRenderer(new Renderer(img));
		data = map.getId();
	}

	public static void load(Location l) {
		int x = l.getBlockX();
		int z = l.getBlockZ();
		World w = l.getWorld();
		Chunk c = w.getChunkAt(x, z);
		Chunk c1 = w.getChunkAt(x - 16, z);
		Chunk c2 = w.getChunkAt(x, z - 16);
		Chunk c3 = w.getChunkAt(x - 16, z - 16);
		Chunk c4 = w.getChunkAt(x + 16, z);
		Chunk c5 = w.getChunkAt(x, z + 16);
		Chunk c6 = w.getChunkAt(x + 16, z + 16);
		Chunk c7 = w.getChunkAt(x + 16, z - 16);
		Chunk c8 = w.getChunkAt(x - 16, z + 16);
		if (!c.isLoaded()) {
			c.load();
		}
		if (!c1.isLoaded()) {
			c1.load();
		}
		if (!c2.isLoaded()) {
			c2.load();
		}
		if (!c3.isLoaded()) {
			c3.load();
		}
		if (!c4.isLoaded()) {
			c4.load();
		}
		if (!c5.isLoaded()) {
			c5.load();
		}
		if (!c6.isLoaded()) {
			c6.load();
		}
		if (!c7.isLoaded()) {
			c7.load();
		}
		if (!c8.isLoaded()) {
			c8.load();
		}
	}

	public final short data;

	public MapView getMap() {
		return map;
	}

	private static class Renderer extends MapRenderer {
		private Image img;

		public Renderer(Image img) {
			this.img = img;

		}

		@Override
		public void render(MapView mapView, MapCanvas mapCanvas, Player p) {

			mapCanvas.drawImage(0, 0, img.getScaledInstance(128, 128, Image.SCALE_AREA_AVERAGING));

		}

	}

}
