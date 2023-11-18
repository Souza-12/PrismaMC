package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.meta.ItemMeta;

public class Viking extends Kit {
   public Viking() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      Player player = event.getPlayer();
      if (this.containsHability(player) && player.getItemInHand() != null && player.getItemInHand().getType().name().contains("AXE")) {
         event.setDamage(event.getDamage() + 2.0D);
         player.getItemInHand().setDurability((short)0);
         ItemMeta meta = player.getItemInHand().getItemMeta();
         if (!meta.spigot().isUnbreakable()) {
            meta.spigot().setUnbreakable(true);
            player.getItemInHand().setItemMeta(meta);
            player.updateInventory();
         }
      }

   }

   protected void clean(Player player) {
   }
}