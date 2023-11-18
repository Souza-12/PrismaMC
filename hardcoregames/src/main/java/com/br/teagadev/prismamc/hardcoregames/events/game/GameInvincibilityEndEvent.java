package com.br.teagadev.prismamc.hardcoregames.events.game;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameInvincibilityEndEvent extends Event {
   public static final HandlerList handlerList = new HandlerList();

   public HandlerList getHandlers() {
      return handlerList;
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }
}