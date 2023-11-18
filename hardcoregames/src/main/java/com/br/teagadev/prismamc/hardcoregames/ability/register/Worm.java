package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Worm extends Kit {
   public Worm() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void entityDamage(EntityDamageEvent e) {
      if (e.getEntity() instanceof Player) {
         Player p = (Player)e.getEntity();
         if (e.getCause().equals(DamageCause.FALL) && this.containsHability(p) && p.getLocation().getBlock().getRelative(BlockFace.DOWN).getType().equals(Material.DIRT) && e.getDamage() >= 8.0D) {
            e.setDamage(8.0D);
         }
      }

   }

   @EventHandler
   public void onBlockDamage(BlockDamageEvent e) {
      if (e.getBlock().getType().equals(Material.DIRT) && this.containsHability(e.getPlayer())) {
         e.setInstaBreak(true);
         if (e.getPlayer().getHealth() < 20.0D) {
            double value = e.getPlayer().getHealth() + 2.0D;
            if (value > 20.0D) {
               value = 20.0D;
            }

            e.getPlayer().setHealth(value);
         }

         if (e.getPlayer().getFoodLevel() < 20) {
            int value = e.getPlayer().getFoodLevel() + 1;
            if (value > 20) {
               value = 20;
            }

            e.getPlayer().setFoodLevel(value);
         }

         e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 60, 1));
      }

   }

   protected void clean(Player player) {
   }
}