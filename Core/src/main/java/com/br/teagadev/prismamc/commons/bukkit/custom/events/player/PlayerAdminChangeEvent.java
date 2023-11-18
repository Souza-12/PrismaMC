package com.br.teagadev.prismamc.commons.bukkit.custom.events.player;

import org.bukkit.entity.Player;

public class PlayerAdminChangeEvent extends PlayerCancellableEvent {
   private final PlayerAdminChangeEvent.AdminChangeType changeType;

   public PlayerAdminChangeEvent(Player player, PlayerAdminChangeEvent.AdminChangeType changeType) {
      super(player);
      this.changeType = changeType;
   }

   public PlayerAdminChangeEvent.AdminChangeType getChangeType() {
      return this.changeType;
   }

   public static enum AdminChangeType {
      ENTROU,
      SAIU;
   }
}