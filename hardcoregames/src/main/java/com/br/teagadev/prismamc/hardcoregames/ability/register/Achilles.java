package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.inventory.ItemStack;

public class Achilles extends Kit {
   public Achilles() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      if (!event.isCancelled()) {
         if (this.useAbility(event.getDamaged())) {
            ItemStack inHand = event.getPlayer().getItemInHand();
            if (inHand == null || inHand.getType() == Material.AIR) {
               return;
            }

            if (!inHand.getType().name().contains("WOOD") && inHand.getType() != Material.STICK) {
               event.setDamage(2.0D);
               event.getPlayer().sendMessage("§c" + event.getDamaged().getName() + " está usando o kit Achilles, itens de madeira causam mais dano.");
            } else {
               event.setDamage(Math.max(7.0D, event.getDamage() + 4.0D));
            }

            inHand = null;
         }

      }
   }

   protected void clean(Player player) {
   }
}