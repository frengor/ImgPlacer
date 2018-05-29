package com.fren_gor.img;

import java.awt.Image;

import org.bukkit.Bukkit;
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

		for (MapRenderer r : map.getRenderers()) {
			map.removeRenderer(r);
		}

		map.addRenderer(new Renderer(img));
		data = map.getId();
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
