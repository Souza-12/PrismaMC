package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

public class Neo extends Kit {
   public Neo() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onProjectDamage(EntityDamageByEntityEvent event) {
      if (event.getEntity() instanceof Player) {
         Player p = (Player)event.getEntity();
         if (this.containsHability(p)) {
            if (event.getDamager() instanceof Projectile) {
               event.setCancelled(true);
               Projectile projectile = (Projectile)event.getDamager();
               if (projectile.getShooter() != null && projectile.getShooter() instanceof Player) {
                  Player projectator = (Player)projectile.getShooter();
                  projectator.sendMessage("§cO jogador está utilizando o kit Neo!");
               }
            }

         }
      }
   }

   protected void clean(Player player) {
   }
}