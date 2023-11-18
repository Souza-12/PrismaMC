package com.br.teagadev.prismamc.commons.bukkit.api.npc;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.api.NPC;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.listener.NPCListener;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import org.bukkit.Bukkit;

public final class NPCManager {
   private static Set<NPC> npcs;
   private static boolean registred = false;

   private NPCManager() {
      throw new SecurityException("You cannot initialize this class.");
   }

   public static void register() {
      if (!registred) {
         registred = true;
         npcs = new HashSet();
         Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), BukkitMain.getInstance());
         BukkitMain.console("§a[NPCS] has been registred!");
      }
   }

   public static Set<NPC> getAllNPCs() {
      return npcs;
   }

   public static void add(NPC npc) {
      npcs.add(npc);
   }

   public static void remove(NPC npc) {
      npcs.remove(npc);
   }

   public static NPC getNPCByName(String name) {
      NPC finded = null;
      Iterator var2 = npcs.iterator();

      while(var2.hasNext()) {
         NPC npc = (NPC)var2.next();
         if (npc.getCustomName().equalsIgnoreCase(name)) {
            finded = npc;
            break;
         }
      }

      return finded;
   }
}