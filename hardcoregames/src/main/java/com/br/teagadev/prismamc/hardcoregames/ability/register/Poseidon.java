package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Kit {
   public Poseidon() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onRealMove(PlayerMoveEvent event) {
      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
         if (!event.isCancelled()) {
            if ((event.getTo().getBlock().getType().equals(Material.WATER) || event.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) && this.containsHability(event.getPlayer())) {
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0), true);
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0), true);
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0), true);
            }

         }
      }
   }

   @EventHandler
   public void onDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player && this.containsHability((Player)e.getEntity()) && e.getCause() == DamageCause.DROWNING) {
         e.setCancelled(true);
      }

   }

   protected void clean(Player player) {
   }
}