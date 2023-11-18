package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import lombok.val;
import org.bukkit.Material;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class Camel extends Kit {
	   public Camel() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler
	   public void onRealMove(PlayerMoveEvent event) {
	      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
	         if (!event.isCancelled()) {
	            Player player = event.getPlayer();
	            if (this.hasAbility(player)) {
	               Material blockType = event.getTo().getBlock().getRelative(BlockFace.DOWN).getType();
	               if (blockType.equals(Material.SAND) || blockType.equals(Material.SOUL_SAND)) {
	                  player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 150, 0));
	                  player.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 150, 0));
	                  player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 100, 1));
	               }

	            }
	         }
	      }
	   }

	   protected void clean(Player player) {
	   }
	}