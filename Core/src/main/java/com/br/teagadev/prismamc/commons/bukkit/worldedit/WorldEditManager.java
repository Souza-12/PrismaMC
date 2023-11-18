package com.br.teagadev.prismamc.commons.bukkit.worldedit;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class WorldEditManager {
   private static HashMap<UUID, Location> pos1;
   private static HashMap<UUID, Location> pos2;
   private static HashMap<UUID, Constructions> construçőes;

   public static void check() {
      if (pos1 == null) {
         pos1 = new HashMap();
      }

      if (pos2 == null) {
         pos2 = new HashMap();
      }

      if (construçőes == null) {
         construçőes = new HashMap();
      }

   }

   public static void addConstructionByUUID(Player owner, List<Location> locations) {
      construçőes.put(owner.getUniqueId(), new Constructions(owner, locations));
   }

   public static void removeConstructionByUUID(UUID uuid) {
      construçőes.remove(uuid);
   }

   public static boolean hasRollingConstructionByUUID(UUID uuid) {
      return construçőes.containsKey(uuid);
   }

   public static Constructions getConstructionByUUID(UUID uuid) {
      return (Constructions)construçőes.get(uuid);
   }

   public static Location getPos1(Player player) {
      return (Location)pos1.get(player.getUniqueId());
   }

   public static Location getPos2(Player player) {
      return (Location)pos2.get(player.getUniqueId());
   }

   public static boolean continueEdit(Player player) {
      if (!pos1.containsKey(player.getUniqueId())) {
         player.sendMessage("§e§lWORLDEDIT §fA primeira localização não foi setada.");
         return false;
      } else if (!pos2.containsKey(player.getUniqueId())) {
         player.sendMessage("§e§lWORLDEDIT §fA segunda localização não foi setada.");
         return false;
      } else {
         return true;
      }
   }

   public static void setPos1(Player player, Location loc) {
      pos1.put(player.getUniqueId(), loc);
      player.sendMessage("§e§lWORLDEDIT §fPrimeira localização setada em: (§7" + loc.getBlockX() + "§f,§7" + loc.getBlockY() + "§f,§7" + loc.getBlockZ() + "§f).");
   }

   public static void setPos2(Player player, Location loc) {
      pos2.put(player.getUniqueId(), loc);
      player.sendMessage("§e§lWORLDEDIT §fSegunda localização setada em: (§7" + loc.getBlockX() + "§f,§7" + loc.getBlockY() + "§f,§7" + loc.getBlockZ() + "§f).");
   }

   public static List<Location> getLocationsFromTwoPoints(Location location1, Location location2) {
      List<Location> locations = new ArrayList();
      int topBlockX = location1.getBlockX() < location2.getBlockX() ? location2.getBlockX() : location1.getBlockX();
      int bottomBlockX = location1.getBlockX() > location2.getBlockX() ? location2.getBlockX() : location1.getBlockX();
      int topBlockY = location1.getBlockY() < location2.getBlockY() ? location2.getBlockY() : location1.getBlockY();
      int bottomBlockY = location1.getBlockY() > location2.getBlockY() ? location2.getBlockY() : location1.getBlockY();
      int topBlockZ = location1.getBlockZ() < location2.getBlockZ() ? location2.getBlockZ() : location1.getBlockZ();
      int bottomBlockZ = location1.getBlockZ() > location2.getBlockZ() ? location2.getBlockZ() : location1.getBlockZ();

      for(int x = bottomBlockX; x <= topBlockX; ++x) {
         for(int z = bottomBlockZ; z <= topBlockZ; ++z) {
            for(int y = bottomBlockY; y <= topBlockY; ++y) {
               locations.add(new Location(location1.getWorld(), (double)x, (double)y, (double)z));
            }
         }
      }

      return locations;
   }

   public static void checkAndRemove(Player player) {
      check();
      construçőes.remove(player.getUniqueId());
   }
}