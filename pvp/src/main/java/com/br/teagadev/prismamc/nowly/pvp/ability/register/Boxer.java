package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Boxer extends Kit {
	   public Boxer() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler(
	      priority = EventPriority.HIGH,
	      ignoreCancelled = true
	   )
	   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
	      if (this.hasAbility(event.getDamaged())) {
	         event.setDamage(event.getDamage() - 0.9D);
	      }

	   }

	   protected void clean(Player player) {
	   }
	}