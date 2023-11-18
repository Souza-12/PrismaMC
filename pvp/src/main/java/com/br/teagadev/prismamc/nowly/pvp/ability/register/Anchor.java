package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.util.Vector;

import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;

public class Anchor extends Kit {
	   public Anchor() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler(
	      ignoreCancelled = true
	   )
	   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
	      if (this.hasAbility(event.getDamaged())) {
	         this.handle(event.getDamaged(), event.getPlayer());
	      } else if (this.hasAbility(event.getPlayer())) {
	         this.handle(event.getDamaged(), event.getPlayer());
	      }

	   }

	   public void handle(Player player1, Player player2) {
	      player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	      player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	      player1.playSound(player1.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
	      player2.playSound(player2.getLocation(), Sound.ANVIL_LAND, 1.0F, 1.0F);
	      PvPMain.runLater(() -> {
	         player1.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	         player2.setVelocity(new Vector(0.0D, 0.0D, 0.0D));
	      }, 1L);
	   }

	   protected void clean(Player player) {
	   }
	}