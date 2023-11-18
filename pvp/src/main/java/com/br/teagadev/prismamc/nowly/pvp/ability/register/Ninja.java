package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerToggleSneakEvent;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.GamerDeathEvent;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.nowly.pvp.listeners.CombatLogListener;

import lombok.Getter;

public class Ninja extends Kit {
	   private final Map<Player, Ninja.NinjaHit> ninja;

	   public Ninja() {
	      this.initialize(this.getClass().getSimpleName());
	      this.ninja = new HashMap();
	   }

	   @EventHandler(
	      priority = EventPriority.HIGH,
	      ignoreCancelled = true
	   )
	   public void onPlayerDamagePlayerEvent(PlayerDamagePlayerEvent event) {
	      if (!CombatLogListener.isProtected(event.getPlayer()) && !CombatLogListener.isProtected(event.getDamaged()) && this.hasAbility(event.getPlayer())) {
	         Ninja.NinjaHit ninjaHit = (Ninja.NinjaHit)this.ninja.get(event.getPlayer());
	         if (ninjaHit == null) {
	            ninjaHit = new Ninja.NinjaHit(event.getDamaged());
	            this.ninja.put(event.getPlayer(), ninjaHit);
	         } else {
	            ninjaHit.setTarget(event.getDamaged());
	         }
	      }

	   }

	   @EventHandler
	   public void onPlayerToggleSneakEvent(PlayerToggleSneakEvent event) {
	      Player player = event.getPlayer();
	      if (event.isSneaking() && this.ninja.containsKey(player) && !CombatLogListener.isProtected(player) && this.hasAbility(player)) {
	         Ninja.NinjaHit hit = (Ninja.NinjaHit)this.ninja.get(player);
	         if (hit == null) {
	            return;
	         }

	         if (hit.getTargetExpires() < System.currentTimeMillis()) {
	            return;
	         }

	         Player target = hit.getTarget();
	         if (target.isOnline() && !CombatLogListener.isProtected(target)) {
	            if (player.getLocation().distance(target.getLocation()) > 40.0D) {
	               player.sendMessage("§cJogador muito longe!");
	               return;
	            }

	            if (!this.hasCooldown(player)) {
	               player.teleport(target.getLocation());
	               this.addCooldown(player, (long)this.getCooldownSeconds());
	               player.getWorld().playSound(player.getLocation().add(0.0D, 0.5D, 0.0D), Sound.ENDERMAN_TELEPORT, 0.3F, 1.0F);
	            }
	         }
	      }

	   }

	   @EventHandler
	   public void onPlayerWarpDeathEvent(GamerDeathEvent event) {
	      this.clean(event.getPlayer());
	      if (event.hasKiller()) {
	         this.clean(event.getKiller());
	      }

	   }

	   protected void clean(Player player) {
	      this.ninja.remove(player);
	   }

	   private static class NinjaHit {
	      private Player target;
	      private long targetExpires;

	      public NinjaHit(Player target) {
	         this.target = target;
	         this.targetExpires = System.currentTimeMillis() + 15000L;
	      }

	      public void setTarget(Player player) {
	         this.target = player;
	         this.targetExpires = System.currentTimeMillis() + 15000L;
	      }

	      public Player getTarget() {
	         return this.target;
	      }

	      public long getTargetExpires() {
	         return this.targetExpires;
	      }
	   }
	}