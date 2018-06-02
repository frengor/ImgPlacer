package com.fren_gor.img.events;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class InteractItemFrameEvent extends Event implements Cancellable {

	private final Player clicker;
	private final ItemFrame clicked;
	private final boolean placeMode;

	public boolean getPlaceMode() {
		return placeMode;
	}
	
	public InteractItemFrameEvent(Player clicker, ItemFrame clicked, boolean placeMode) {

		this.clicked = clicked;
		this.clicker = clicker;
		this.placeMode = placeMode;

	}

	public Player getClicker() {
		return clicker;
	}

	public ItemFrame getClicked() {
		return clicked;
	}

	private static final HandlerList handlers = new HandlerList();

	@Override
	public HandlerList getHandlers() {
		return handlers;
	}

	public static HandlerList getHandlerList() {
		return handlers;
	}

	private boolean c = false;

	@Override
	public boolean isCancelled() {
		return c;
	}

	@Override
	public void setCancelled(boolean bool) {
		c = bool;
	}

}
