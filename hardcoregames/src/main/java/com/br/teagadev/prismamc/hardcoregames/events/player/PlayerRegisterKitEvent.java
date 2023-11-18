package com.br.teagadev.prismamc.hardcoregames.events.player;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;

public class PlayerRegisterKitEvent extends PlayerEvent {
   private String kitName;

   public PlayerRegisterKitEvent(Player player, String kitName) {
      super(player);
      this.kitName = kitName;
   }

   public String getKitName() {
      return this.kitName;
   }
}