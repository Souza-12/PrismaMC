package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Poseidon extends Kit {
	   public Poseidon() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler
	   public void onRealMove(PlayerMoveEvent event) {
	      if (!event.isCancelled()) {
	         Player player = event.getPlayer();
	         if (this.hasAbility(player)) {
	            Material blockType = event.getTo().getBlock().getType();
	            if (blockType.equals(Material.WATER) || blockType.equals(Material.STATIONARY_WATER)) {
	               player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 100, 0));
	               player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 100, 0));
	               player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 0));
	            }

	         }
	      }
	   }

	   protected void clean(Player player) {
	   }
	}