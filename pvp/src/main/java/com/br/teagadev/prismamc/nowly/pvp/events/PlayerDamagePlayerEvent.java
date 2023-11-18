package com.br.teagadev.prismamc.nowly.pvp.events;

import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerCancellableEvent;
import org.bukkit.entity.Player;


public class PlayerDamagePlayerEvent extends PlayerCancellableEvent {
	   private Player damaged;
	   private double damage;

	   public PlayerDamagePlayerEvent(Player damager, Player damaged, double damage) {
	      super(damager);
	      this.damaged = damaged;
	      this.damage = damage;
	   }

	   public Player getDamaged() {
	      return this.damaged;
	   }

	   public double getDamage() {
	      return this.damage;
	   }

	   public void setDamaged(Player damaged) {
	      this.damaged = damaged;
	   }

	   public void setDamage(double damage) {
	      this.damage = damage;
	   }
	}