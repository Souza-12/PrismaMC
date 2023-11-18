package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class Ironman extends Kit {
   public Ironman() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      Player morreu = event.getEntity();
      Player killer = morreu.getKiller();
      if (killer != null) {
         if (killer instanceof Player) {
            if (this.containsHability(killer) && Math.random() <= 0.45D) {
               if (PlayerAPI.isFull(killer.getInventory())) {
                  killer.getWorld().dropItem(killer.getLocation(), new ItemStack(Material.IRON_INGOT));
               } else {
                  killer.getInventory().addItem(new ItemStack[]{new ItemStack(Material.IRON_INGOT)});
               }
            }

         }
      }
   }

   protected void clean(Player player) {
   }
}