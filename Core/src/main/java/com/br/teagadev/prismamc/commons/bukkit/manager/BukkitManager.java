package com.br.teagadev.prismamc.commons.bukkit.manager;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramInjector;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.HologramListeners;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.NPCManager;
import com.br.teagadev.prismamc.commons.bukkit.api.npc.listener.NPCListener;
import com.br.teagadev.prismamc.commons.bukkit.custom.injector.packets.NPCPacketInjector;
import com.br.teagadev.prismamc.commons.bukkit.custom.injector.packets.ServerPacketInjector;
import com.br.teagadev.prismamc.commons.bukkit.manager.configuration.BukkitConfigurationManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BukkitManager {
   private final BukkitConfigurationManager configurationManager = new BukkitConfigurationManager();
   private boolean NPC_EVENTS = false;
   private boolean PACKET_INJECTOR = false;
   private boolean HOLOGRAM_EVENTS = false;

   public void disable() {
      if (this.NPC_EVENTS) {
      }

      if (this.HOLOGRAM_EVENTS) {
         HologramAPI.getHolograms().forEach((holograms) -> {
            HologramAPI.removeHologram(holograms);
         });
      }

   }

   public void enablePacketInjector(Plugin plugin) {
      if (!this.PACKET_INJECTOR) {
         this.PACKET_INJECTOR = true;
         ServerPacketInjector.inject(plugin);
         BukkitMain.console("§a[BukkitManager] ServerPacketInjector has been enabled!");
      }
   }

   public void enableNPC(Plugin plugin) {
      if (!this.NPC_EVENTS) {
         this.NPC_EVENTS = true;
         NPCPacketInjector.inject(plugin);
         NPCManager.register();
         Bukkit.getServer().getPluginManager().registerEvents(new NPCListener(), plugin);
      }

   }

   public void enableHologram(Plugin plugin) {
      if (!this.HOLOGRAM_EVENTS) {
         this.HOLOGRAM_EVENTS = true;
         HologramAPI.packetsEnabled = true;
         HologramListeners.registerListeners();
         HologramInjector.inject(plugin);
         BukkitMain.console("§a[BukkitManager] HologramAPI has been enabled!");
      }
   }

   public BukkitConfigurationManager getConfigurationManager() {
      return this.configurationManager;
   }

   public boolean isNPC_EVENTS() {
      return this.NPC_EVENTS;
   }

   public boolean isPACKET_INJECTOR() {
      return this.PACKET_INJECTOR;
   }

   public boolean isHOLOGRAM_EVENTS() {
      return this.HOLOGRAM_EVENTS;
   }
}