package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import java.util.Iterator;
import java.util.List;
import org.bukkit.GameMode;
import org.bukkit.Sound;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;

public class Stomper extends Kit {
   private final double STOMPER_FALL_NERF = 3.5D;

   public Stomper() {
      this.initialize(this.getClass().getSimpleName());
   }

   @EventHandler
   public void onFall(EntityDamageEvent event) {
      if (!event.isCancelled()) {
         if (event.getEntity() instanceof Player) {
            Player p = (Player)event.getEntity();
            if (event.getCause() == DamageCause.FALL && this.containsHability(p)) {
               if (event.getDamage() <= 4.0D) {
                  return;
               }

               List<Entity> entity = p.getNearbyEntities(6.0D, 3.0D, 6.0D);
               boolean stomped = false;
               Iterator var5 = entity.iterator();

               while(var5.hasNext()) {
                  Entity en = (Entity)var5.next();
                  if (en instanceof Player) {
                     Player stompados = (Player)en;
                     if (GamerManager.getGamer(stompados.getUniqueId()).isPlaying() && stompados.getGameMode().equals(GameMode.SURVIVAL)) {
                        stomped = true;
                        double damage = 4.0D;
                        double life = stompados.getHealth();
                        if (!stompados.isSneaking()) {
                           damage = (double)p.getFallDistance() - 3.5D;
                        }

                        stompados.playSound(stompados.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
                        if (life - damage <= 0.0D) {
                           stompados.setHealth(1.0D);
                           stompados.damage(4.0D, p);
                        } else {
                           stompados.setHealth(life - damage);
                        }
                     }
                  }
               }

               if (stomped) {
                  p.playSound(p.getLocation(), Sound.ANVIL_BREAK, 1.0F, 1.0F);
               }

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