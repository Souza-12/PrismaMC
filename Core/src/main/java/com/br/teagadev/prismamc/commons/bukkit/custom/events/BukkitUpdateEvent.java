package com.br.teagadev.prismamc.commons.bukkit.custom.events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class BukkitUpdateEvent extends Event {
   public static final HandlerList handlers = new HandlerList();
   private final BukkitUpdateEvent.UpdateType type;
   private final long currentTick;

   public BukkitUpdateEvent(BukkitUpdateEvent.UpdateType type) {
      this(type, -1L);
   }

   public BukkitUpdateEvent(BukkitUpdateEvent.UpdateType type, long currentTick) {
      this.type = type;
      this.currentTick = currentTick;
   }

   public static HandlerList getHandlerList() {
      return handlers;
   }

   public HandlerList getHandlers() {
      return handlers;
   }

   public BukkitUpdateEvent.UpdateType getType() {
      return this.type;
   }

   public long getCurrentTick() {
      return this.currentTick;
   }

   public static enum UpdateType {
      TICK,
      SEGUNDO,
      MINUTO,
      HORA;
   }
}