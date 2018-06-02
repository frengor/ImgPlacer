package com.fren_gor.img.events;

import org.bukkit.entity.ItemFrame;
import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class RemoveItemFrameEvent extends Event implements Cancellable {

	private final Player remover;
	private final ItemFrame removed;

	public RemoveItemFrameEvent(Player remover, ItemFrame removed) {

		this.remover = remover;
		this.removed = removed;

	}

	public Player getRemover() {
		return remover;
	}

	public ItemFrame getRemoved() {
		return removed;
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
