package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerRegisterKitEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;

public class Madman extends Kit {
   private final ConcurrentHashMap<UUID, Integer> efeitoMadman = new ConcurrentHashMap();
   private final ArrayList<UUID> madmans = new ArrayList();

   public Madman() {
      this.initialize(this.getClass().getSimpleName());
      this.setCallEventRegisterPlayer(true);
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onSecond(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.SEGUNDO) {
         Iterator var2;
         UUID uuidInfectados;
         if (this.madmans.size() > 0) {
            var2 = this.madmans.iterator();

            label44:
            while(true) {
               Player madman;
               do {
                  if (!var2.hasNext()) {
                     break label44;
                  }

                  uuidInfectados = (UUID)var2.next();
                  madman = Bukkit.getPlayer(uuidInfectados);
               } while(madman == null);

               List<Player> lista = this.getNearbyPlayers(madman, 15);
               if (lista.size() > 1) {
                  Iterator var6 = lista.iterator();

                  while(var6.hasNext()) {
                     Player perto = (Player)var6.next();
                     int efeito = lista.size() * 2;
                     this.addEffect(perto, efeito);
                  }
               }

               lista.clear();
               lista = null;
               madman = null;
            }
         }

         if (this.efeitoMadman.size() > 0) {
            var2 = this.efeitoMadman.keySet().iterator();

            while(var2.hasNext()) {
               uuidInfectados = (UUID)var2.next();
               this.removeEffect(uuidInfectados);
            }
         }

      }
   }

   @EventHandler(
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      if (this.efeitoMadman.containsKey(event.getDamaged().getUniqueId())) {
         double dano = event.getDamage();
         event.setDamage(dano + dano / 100.0D * (double)(Integer)this.efeitoMadman.get(event.getDamaged().getUniqueId()));
      }

   }

   @EventHandler
   public void onRegister(PlayerRegisterKitEvent event) {
      if (event.getKitName().equalsIgnoreCase(this.getName()) && !this.madmans.contains(event.getPlayer().getUniqueId())) {
         this.madmans.add(event.getPlayer().getUniqueId());
      }

   }

   private void removeEffect(UUID u) {
      int effect = (Integer)this.efeitoMadman.get(u);
      effect -= 10;
      this.efeitoMadman.put(u, effect);
      if (effect <= 0) {
         this.efeitoMadman.remove(u);
      }

   }

   private void addEffect(Player player, int efeito) {
      int effect = this.efeitoMadman.containsKey(player.getUniqueId()) ? (Integer)this.efeitoMadman.get(player.getUniqueId()) : 0;
      if (effect == 0) {
         player.sendMessage("§cTem um §lMADMAN §cpor perto!");
      }

      effect += efeito + 10;
      BossBarAPI.send(player, "EFEITO DO MADMAN " + effect + "%", 5);
      this.efeitoMadman.put(player.getUniqueId(), effect);
   }

   private List<Player> getNearbyPlayers(Player p, int i) {
      List<Player> players = new ArrayList();
      List<Entity> entities = p.getPlayer().getNearbyEntities((double)i, (double)i, (double)i);
      Iterator var5 = entities.iterator();

      while(var5.hasNext()) {
         Entity e = (Entity)var5.next();
         if (e instanceof Player && GamerManager.getGamer(((Player)e).getUniqueId()).isPlaying()) {
            players.add((Player)e);
         }
      }

      entities.clear();
      entities = null;
      return players;
   }

   protected void clean(Player player) {
      if (this.efeitoMadman.containsKey(player.getUniqueId())) {
         this.efeitoMadman.remove(player.getUniqueId());
      }

   }
}