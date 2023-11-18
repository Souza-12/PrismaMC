package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;

public class PlayerRequestEvent extends PlayerCancellableEvent {
   private final String requestType;

   public PlayerRequestEvent(Player player, String requestType) {
      super(player);
      this.requestType = requestType;
   }

   public String getRequestType() {
      return this.requestType;
   }
}