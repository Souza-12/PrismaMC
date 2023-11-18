package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import org.bukkit.Effect;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Snail extends Kit {
   public Snail() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      if (this.useAbility(event.getPlayer()) && Math.random() <= 0.33D) {
         event.getDamaged().addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 120, 1));
         event.getDamaged().getLocation().getWorld().playEffect(event.getDamaged().getLocation().add(0.0D, 0.4D, 0.0D), Effect.STEP_SOUND, 159, 13);
      }

   }

   protected void clean(Player player) {
   }
}