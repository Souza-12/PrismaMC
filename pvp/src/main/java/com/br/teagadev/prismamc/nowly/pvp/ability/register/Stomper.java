package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.nowly.pvp.manager.combatlog.CombatLogManager;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Stomper extends Kit {
	   public Stomper() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler
	   public void onFall(EntityDamageEvent event) {
	      if (!event.isCancelled()) {
	         if (event.getEntity() instanceof Player) {
	            Player p = (Player)event.getEntity();
	            if (event.getCause() == DamageCause.FALL && this.hasAbility(p)) {
	               if (event.getDamage() <= 4.0D) {
	                  return;
	               }

	               p.getNearbyEntities(6.0D, 3.0D, 6.0D).stream().filter((entity) -> {
	                  return entity instanceof Player && !GamerManager.getGamer(entity.getUniqueId()).containsKit("AntiTower") && !((Player)entity).isSneaking() && ((Player)entity).getGameMode() != GameMode.CREATIVE;
	               }).forEach((entity) -> {
	                  Player player = (Player)entity;
	                  player.playSound(player.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
	                  CombatLogManager.removeCombatLog(player);
	                  CombatLogManager.removeCombatLog(p);
	                  CombatLogManager.setCombatLog(player, p);
	                  CombatLogManager.setCombatLog(p, player);
	                  player.damage(event.getFinalDamage());
	               });
	               if (event.getDamage() > 4.0D) {
	                  event.setDamage(4.0D);
	               }
	            }
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}