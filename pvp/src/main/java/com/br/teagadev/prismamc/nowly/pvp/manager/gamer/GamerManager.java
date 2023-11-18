package com.br.teagadev.prismamc.nowly.pvp.manager.gamer;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class GamerManager {
   private static HashMap<UUID, Gamer> gamers = new HashMap();

   public static Gamer getGamer(UUID uniqueId) {
      return (Gamer)gamers.get(uniqueId);
   }

   public static Gamer getGamer(Player player) {
      return (Gamer)gamers.get(player.getUniqueId());
   }

   public static void addGamer(UUID uniqueId) {
      gamers.put(uniqueId, new Gamer(uniqueId));
   }

   public static void removeGamer(UUID uniqueId) {
      gamers.remove(uniqueId);
   }

   public static HashMap<UUID, Gamer> getGamers() {
      return gamers;
   }
}