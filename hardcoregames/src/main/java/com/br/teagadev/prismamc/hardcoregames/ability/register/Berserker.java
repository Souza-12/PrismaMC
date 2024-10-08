package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Berserker extends Kit {
   public Berserker() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler(
      priority = EventPriority.HIGH,
      ignoreCancelled = true
   )
   public void onKilledPlayer(PlayerDeathEvent event) {
      Player player = event.getEntity();
      if (this.containsHability(player.getKiller()) && player.isDead() && player.getKiller() instanceof Player) {
         player.addPotionEffect(new PotionEffect(PotionEffectType.INCREASE_DAMAGE, 150, 1));
         player.sendMessage("§aVocê recebeu a força de um BERSERKER por um tempo valido de 10 segundos!");
      }

   }

   protected void clean(Player player) {
   }
}