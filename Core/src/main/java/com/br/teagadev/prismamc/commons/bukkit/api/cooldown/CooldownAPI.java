package com.br.teagadev.prismamc.commons.bukkit.api.cooldown;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.actionbar.ActionBarAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class CooldownAPI {
   private static final Map<UUID, List<Cooldown>> map = new ConcurrentHashMap();
   private static boolean registred = false;

   private static void registerListener() {
      if (!registred) {
         registred = true;
         Bukkit.getServer().getPluginManager().registerEvents(new Listener() {
            @EventHandler
            public void onUpdate(BukkitUpdateEvent event) {
               if (event.getType() == UpdateType.TICK) {
                  if (event.getCurrentTick() % 2L <= 0L) {
                     Iterator var2 = CooldownAPI.map.keySet().iterator();

                     while(true) {
                        while(true) {
                           UUID uuid;
                           Player player;
                           do {
                              if (!var2.hasNext()) {
                                 return;
                              }

                              uuid = (UUID)var2.next();
                              player = Bukkit.getPlayer(uuid);
                           } while(player == null);

                           List<Cooldown> list = (List)CooldownAPI.map.get(uuid);
                           Iterator<Cooldown> it = list.iterator();
                           Cooldown found = null;

                           while(it.hasNext()) {
                              Cooldown cooldown = (Cooldown)it.next();
                              if (!cooldown.expired()) {
                                 found = cooldown;
                              } else {
                                 it.remove();
                                 player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
                              }
                           }

                           if (found != null && found.isBarAPI()) {
                              CooldownAPI.display(player, found);
                           } else if (list.isEmpty()) {
                              ActionBarAPI.send(player, "");
                              CooldownAPI.map.remove(uuid);
                           }
                        }
                     }
                  }
               }
            }
         }, BukkitMain.getInstance());
      }
   }

   private static void display(Player player, Cooldown cooldown) {
      StringBuilder bar = new StringBuilder();
      double percentage = cooldown.getPercentage();
      double remaining = cooldown.getRemaining();
      double count = 20.0D - Math.max(percentage > 0.0D ? 1.0D : 0.0D, percentage / 5.0D);

      int a;
      for(a = 0; (double)a < count; ++a) {
         bar.append("§a:");
      }

      for(a = 0; (double)a < 20.0D - count; ++a) {
         bar.append("§c:");
      }

      String name = cooldown.getName();
      ActionBarAPI.send(player, name + " " + bar + "§f " + String.format(Locale.US, "%.1fs", remaining));
   }

   public static void removeAllCooldowns(Player player) {
      if (map.containsKey(player.getUniqueId())) {
         List<Cooldown> list = (List)map.get(player.getUniqueId());
         Iterator it = list.iterator();

         while(it.hasNext()) {
            Cooldown cooldown = (Cooldown)it.next();
            it.remove();
         }
      }

   }

   public static void sendMessage(Player player, String name) {
      if (map.containsKey(player.getUniqueId())) {
         List<Cooldown> list = (List)map.get(player.getUniqueId());
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            Cooldown cooldown = (Cooldown)var3.next();
            if (cooldown.getName().equals(name)) {
               player.sendMessage("§cAguarde %tempo% para usar novamente.".replace("%tempo%", StringUtility.toMillis(cooldown.getRemaining())));
            }
         }
      }

   }

   public static void addCooldown(Player player, Cooldown cooldown) {
      if (!registred) {
         registerListener();
      }

      List<Cooldown> list = (List)map.computeIfAbsent(player.getUniqueId(), (v) -> {
         return new ArrayList();
      });
      list.add(cooldown);
   }

   public static boolean removeCooldown(Player player, String name) {
      if (map.containsKey(player.getUniqueId())) {
         List<Cooldown> list = (List)map.get(player.getUniqueId());
         Iterator it = list.iterator();

         while(it.hasNext()) {
            Cooldown cooldown = (Cooldown)it.next();
            if (cooldown.getName().equals(name)) {
               it.remove();
               return true;
            }
         }
      }

      return false;
   }

   public static boolean hasCooldown(Player player, String name) {
      if (map.containsKey(player.getUniqueId())) {
         List<Cooldown> list = (List)map.get(player.getUniqueId());
         Iterator var3 = list.iterator();

         while(var3.hasNext()) {
            Cooldown cooldown = (Cooldown)var3.next();
            if (cooldown.getName().equals(name)) {
               return true;
            }
         }
      }

      return false;
   }
}