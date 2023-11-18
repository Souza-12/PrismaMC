package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;

public class PlayerChangeVisibilityEvent extends PlayerCancellableEvent {
   private final boolean visibility;

   public PlayerChangeVisibilityEvent(Player player, boolean visibility) {
      super(player);
      this.visibility = visibility;
   }

   public boolean isVisibility() {
      return this.visibility;
   }
}