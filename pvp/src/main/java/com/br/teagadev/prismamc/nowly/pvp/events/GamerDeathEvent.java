package com.br.teagadev.prismamc.nowly.pvp.events;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerEvent;
import org.bukkit.entity.Player;


public class GamerDeathEvent extends PlayerEvent {
   private Player killer;

   public GamerDeathEvent(Player player, Player killer) {
      super(player);
      this.killer = killer;
   }

   public boolean hasKiller() {
      return this.killer != null;
   }

   public Player getKiller() {
      return this.killer;
   }
}