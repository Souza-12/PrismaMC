package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class NoFall extends Kit {
	   public NoFall() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler(
	      priority = EventPriority.HIGHEST
	   )
	   public void onEntityDamageEvent(EntityDamageEvent event) {
	      if (event.getEntity() instanceof Player) {
	         Player player = (Player)event.getEntity();
	         if (event.getCause() == DamageCause.FALL && this.hasAbility(player)) {
	            event.setCancelled(true);
	         }
	      }

	   }

	   protected void clean(Player player) {
	   }
	}