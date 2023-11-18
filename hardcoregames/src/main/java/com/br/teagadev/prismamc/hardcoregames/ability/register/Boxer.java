package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class Boxer extends Kit {
   public Boxer() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      if (this.containsHability(event.getDamaged())) {
         event.setDamage(event.getDamage() - 1.0D);
      }

   }

   protected void clean(Player player) {
   }
}