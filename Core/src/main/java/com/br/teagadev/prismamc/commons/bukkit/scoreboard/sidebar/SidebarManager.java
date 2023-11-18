package com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar;

import java.util.HashMap;
import java.util.UUID;
import org.bukkit.entity.Player;

public class SidebarManager {
   private static final HashMap<UUID, Sidebar> sidebars = new HashMap();

   public static void handleJoin(Player player) {
      sidebars.put(player.getUniqueId(), new Sidebar(player.getScoreboard()));
   }

   public static Sidebar getSidebar(Player player) {
      return (Sidebar)sidebars.get(player.getUniqueId());
   }

   public static Sidebar getSidebar(UUID uniqueId) {
      return (Sidebar)sidebars.get(uniqueId);
   }

   public static void handleQuit(UUID uniqueId) {
      sidebars.remove(uniqueId);
   }
}