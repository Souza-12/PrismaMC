package com.br.teagadev.prismamc.commons.bukkit.manager.configuration;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import java.util.HashMap;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public class PluginConfiguration {
   public static final HashMap<String, Location> CACHE_LOCATIONS = new HashMap();

   public static Location setLocation(Plugin instance, String nome, Player player) {
      instance.getConfig().set("locs." + nome + ".world", player.getLocation().getWorld().getName());
      instance.getConfig().set("locs." + nome + ".x", (double)player.getLocation().getBlockX() + 0.5D);
      instance.getConfig().set("locs." + nome + ".y", player.getLocation().getY());
      instance.getConfig().set("locs." + nome + ".z", (double)player.getLocation().getBlockZ() + 0.5D);
      instance.getConfig().set("locs." + nome + ".yaw", player.getLocation().getYaw());
      instance.getConfig().set("locs." + nome + ".pitch", player.getLocation().getPitch());
      instance.saveConfig();
      CACHE_LOCATIONS.remove(nome);
      return getLocation(instance, nome);
   }

   public static Location getLocation(Plugin instance, String nome) {
      if (CACHE_LOCATIONS.containsKey(nome)) {
         return (Location)CACHE_LOCATIONS.get(nome);
      } else if (!instance.getConfig().contains("locs." + nome + ".world")) {
         BukkitMain.console("Local inválido.");
         return null;
      } else {
         double x = instance.getConfig().getDouble("locs." + nome + ".x");
         double y = instance.getConfig().getDouble("locs." + nome + ".y");
         double z = instance.getConfig().getDouble("locs." + nome + ".z");
         Location loc = new Location(Bukkit.getWorld(instance.getConfig().getString("locs." + nome + ".world")), x, y, z, (float)instance.getConfig().getLong("locs." + nome + ".yaw"), (float)instance.getConfig().getLong("locs." + nome + ".pitch"));
         CACHE_LOCATIONS.put(nome, loc);
         return loc;
      }
   }

   public static Location createLocation(Plugin instance, String nome) {
      return createLocation(instance, nome, "world");
   }

   public static void createLocations(Plugin instance, String... nomes) {
      String[] var2 = nomes;
      int var3 = nomes.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         String name = var2[var4];
         createLocation(instance, name, "world");
      }

   }

   public static Location createLocation(Plugin instance, String nome, String world) {
      if (!instance.getConfig().contains("locs." + nome + ".world")) {
         instance.getConfig().set("locs." + nome + ".world", world);
         instance.getConfig().set("locs." + nome + ".x", 0.5D);
         instance.getConfig().set("locs." + nome + ".y", 80.5D);
         instance.getConfig().set("locs." + nome + ".z", 0.5D);
         instance.getConfig().set("locs." + nome + ".yaw", 0);
         instance.getConfig().set("locs." + nome + ".pitch", 0);
         instance.saveConfig();
      }

      return getLocation(instance, nome);
   }
}