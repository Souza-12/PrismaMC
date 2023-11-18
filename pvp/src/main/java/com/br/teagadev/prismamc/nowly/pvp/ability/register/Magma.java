package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.nowly.pvp.listeners.CombatLogListener;

public class Magma extends Kit {
	   public Magma() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler(
	      priority = EventPriority.HIGHEST,
	      ignoreCancelled = true
	   )
	   public void onPlayerDamagePlayerEvent(PlayerDamagePlayerEvent event) {
	      if (Math.random() <= 0.35D && this.hasAbility(event.getPlayer()) && !CombatLogListener.isProtected(event.getPlayer()) && !CombatLogListener.isProtected(event.getDamaged())) {
	         event.getDamaged().setFireTicks(100);
	      }

	   }

	   @EventHandler
	   public void onEntityDamage(EntityDamageEvent event) {
	      if (!event.isCancelled()) {
	         if (event.getEntity() instanceof Player && (event.getCause() == DamageCause.FIRE || event.getCause() == DamageCause.FIRE_TICK || event.getCause() == DamageCause.LAVA) && this.hasAbility((Player)event.getEntity())) {
	            event.setCancelled(true);
	            event.getEntity().setFireTicks(0);
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}