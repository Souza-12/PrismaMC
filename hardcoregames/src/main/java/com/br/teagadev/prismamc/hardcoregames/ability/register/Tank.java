package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Tank extends Kit {
   public Tank() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (event.getEntity() instanceof Player) {
         Player p = (Player)event.getEntity();
         if ((event.getCause() == DamageCause.ENTITY_EXPLOSION || event.getCause() == DamageCause.BLOCK_EXPLOSION) && this.containsHability(p)) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      Player morreu = event.getEntity();
      Player killer = morreu.getKiller();
      if (killer != null) {
         if (killer instanceof Player) {
            if (this.containsHability(killer)) {
               World world = morreu.getWorld();
               world.createExplosion(morreu.getLocation(), 2.0F);
            }

         }
      }
   }

   protected void clean(Player player) {
   }
}