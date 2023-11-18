package com.br.teagadev.prismamc.commons.bukkit.api.bossbar;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class BossBarAPI {
   private static final HashMap<UUID, BossBar> bossBars = new HashMap();
   private static boolean initialized = false;

   private static void init() {
      if (!initialized) {
         initialized = true;
         Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onQuit(PlayerQuitEvent event) {
               if (BossBarAPI.getBossBars().containsKey(event.getPlayer().getUniqueId())) {
                  ((BossBar)BossBarAPI.getBossBars().get(event.getPlayer().getUniqueId())).destroy();
                  BossBarAPI.getBossBars().remove(event.getPlayer().getUniqueId());
               }

            }

            @EventHandler
            public void onDeath(PlayerDeathEvent event) {
               if (BossBarAPI.getBossBars().containsKey(event.getEntity().getUniqueId())) {
                  ((BossBar)BossBarAPI.getBossBars().get(event.getEntity().getUniqueId())).destroy();
                  BossBarAPI.getBossBars().remove(event.getEntity().getUniqueId());
               }

               if (event.getEntity().getKiller() != null && BossBarAPI.getBossBars().containsKey(event.getEntity().getKiller().getUniqueId())) {
                  ((BossBar)BossBarAPI.getBossBars().get(event.getEntity().getKiller().getUniqueId())).destroy();
                  BossBarAPI.getBossBars().remove(event.getEntity().getKiller().getUniqueId());
               }

            }

            @EventHandler
            public void onUpdate(BukkitUpdateEvent event) {
               if (event.getType() == UpdateType.SEGUNDO) {
                  if (BossBarAPI.bossBars.size() > 0) {
                     List<UUID> toRemove = new ArrayList();
                     Iterator var3 = BossBarAPI.bossBars.values().iterator();

                     while(var3.hasNext()) {
                        BossBar boss = (BossBar)var3.next();
                        if (boss.isCancelar()) {
                           boss.destroy();
                           toRemove.add(boss.getPlayer().getUniqueId());
                        } else {
                           boss.onSecond();
                        }
                     }

                     toRemove.forEach((uuids) -> {
                        BossBar var10000 = (BossBar)BossBarAPI.bossBars.remove(uuids);
                     });
                     toRemove.clear();
                     toRemove = null;
                  }

               }
            }
         }, BukkitMain.getInstance());
      }
   }

   public static void send(Player player, String name, int tempo) {
      if (!initialized) {
         init();
         send(player, name, tempo);
      } else {
         if (getBossBars().containsKey(player.getUniqueId())) {
            ((BossBar)getBossBars().get(player.getUniqueId())).destroy();
         }

         BossBar bossBar = new BossBar(player, name, true);
         bossBar.setSegundos(tempo);
         getBossBars().put(player.getUniqueId(), bossBar);
      }
   }

   public static void send(Player player, String name) {
      if (!initialized) {
         init();
         send(player, name);
      } else {
         if (getBossBars().containsKey(player.getUniqueId())) {
            ((BossBar)getBossBars().get(player.getUniqueId())).destroy();
         }

         BossBar bossBar = new BossBar(player, name, false);
         getBossBars().put(player.getUniqueId(), bossBar);
      }
   }

   public static HashMap<UUID, BossBar> getBossBars() {
      return bossBars;
   }
}