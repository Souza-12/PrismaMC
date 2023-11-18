package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.inventory.ItemStack;

public class Fireman extends Kit {
   public Fireman() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{new ItemStack(Material.WATER_BUCKET)});
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (!event.isCancelled()) {
         if (event.getEntity() instanceof Player && (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) && this.containsHability((Player)event.getEntity())) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
         }

      }
   }

   protected void clean(Player player) {
   }
}