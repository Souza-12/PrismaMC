package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.nowly.pvp.listeners.CombatLogListener;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snail extends Kit {
	   public Snail() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   @EventHandler(
	      priority = EventPriority.HIGH,
	      ignoreCancelled = true
	   )
	   public void playerDamage(PlayerDamagePlayerEvent event) {
	      if (this.hasAbility(event.getPlayer()) && Math.random() <= 0.33D && !CombatLogListener.isProtected(event.getPlayer()) && !CombatLogListener.isProtected(event.getDamaged())) {
	         event.getDamaged().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
	         event.getDamaged().getLocation().getWorld().playEffect(event.getDamaged().getLocation().add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, 13);
	      }

	   }

	   protected void clean(Player player) {
	   }
	}