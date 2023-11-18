package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Magma extends Kit {
   public Magma() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onRealMove(PlayerMoveEvent event) {
      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
         if (!event.isCancelled()) {
            if ((event.getTo().getBlock().getType().equals(Material.WATER) || event.getTo().getBlock().getType().equals(Material.STATIONARY_WATER)) && this.containsHability(event.getPlayer())) {
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.POISON, 100, 0), true);
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 100, 0), true);
               event.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, 100, 0), true);
            }

         }
      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      if (this.useAbility(event.getPlayer()) && Math.random() <= 0.35D) {
         event.getDamaged().setFireTicks(140);
         event.getDamaged().getLocation().getWorld().playEffect(event.getDamaged().getLocation().add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, 13);
      }

   }

   @EventHandler
   public void onDamage(EntityDamageEvent event) {
      if (!event.isCancelled()) {
         if (event.getEntity() instanceof Player && (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) && this.containsHability((Player)event.getEntity())) {
            event.setCancelled(true);
            event.getEntity().setFireTicks(0);
            ((Player)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0), true);
            ((Player)event.getEntity()).addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0), true);
         }

      }
   }

   protected void clean(Player player) {
   }
}