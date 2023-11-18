package com.br.teagadev.prismamc.hardcoregames.manager.gamer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class GamerManager {
   private static HashMap<UUID, Gamer> gamers = new HashMap();

   public static Gamer getGamer(UUID uniqueId) {
      return (Gamer)gamers.get(uniqueId);
   }

   public static Gamer getGamer(Player player) {
      return (Gamer)gamers.getOrDefault(player.getUniqueId(), (Gamer)null);
   }

   public static boolean containsGamer(UUID uniqueId) {
      return gamers.containsKey(uniqueId);
   }

   public static void addGamer(UUID uniqueId) {
      gamers.put(uniqueId, new Gamer(uniqueId));
   }

   public static void removeGamer(UUID uniqueId) {
      gamers.remove(uniqueId);
   }

   public static Collection<Gamer> getGamers() {
      return gamers.values();
   }

   public static List<Player> getAliveGamers() {
      List<Player> alives = new ArrayList();
      Iterator var1 = Bukkit.getOnlinePlayers().iterator();

      while(var1.hasNext()) {
         Player onlines = (Player)var1.next();
         Gamer gamer = getGamer(onlines.getUniqueId());
         if (gamer != null && gamer.isPlaying()) {
            alives.add(onlines);
         }
      }

      return alives;
   }

   public static List<Gamer> getGamersVivos() {
      List<Gamer> vivos = new ArrayList();
      Iterator var1 = getGamers().iterator();

      while(var1.hasNext()) {
         Gamer gamers = (Gamer)var1.next();
         if (gamers.isPlaying()) {
            vivos.add(gamers);
         }
      }

      return vivos;
   }

   public static List<Gamer> getGamersSpecs() {
      List<Gamer> specs = new ArrayList();
      Iterator var1 = getGamers().iterator();

      while(var1.hasNext()) {
         Gamer gamers = (Gamer)var1.next();
         if (!gamers.isPlaying() && gamers.isOnline()) {
            specs.add(gamers);
         }
      }

      return specs;
   }

   public static List<Gamer> getGamersEliminateds() {
      List<Gamer> list = new ArrayList();
      Iterator var1 = getGamers().iterator();

      while(var1.hasNext()) {
         Gamer gamers = (Gamer)var1.next();
         if (gamers.isEliminado()) {
            list.add(gamers);
         }
      }

      return list;
   }

   public static List<Gamer> getGamersToRelog() {
      List<Gamer> list = new ArrayList();
      Iterator var1 = getGamers().iterator();

      while(var1.hasNext()) {
         Gamer gamers = (Gamer)var1.next();
         if (gamers.isRelogar()) {
            list.add(gamers);
         }
      }

      return list;
   }
}