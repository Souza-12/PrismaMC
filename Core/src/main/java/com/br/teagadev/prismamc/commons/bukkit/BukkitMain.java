package com.br.teagadev.prismamc.commons.bukkit;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ActionItemListener;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuListener;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandFramework;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.scheduler.BukkitUpdateScheduler;
import com.br.teagadev.prismamc.commons.bukkit.listeners.CoreListener;
import com.br.teagadev.prismamc.commons.bukkit.listeners.WorldDListener;
import com.br.teagadev.prismamc.commons.bukkit.manager.BukkitManager;
import com.br.teagadev.prismamc.commons.bukkit.networking.BukkitPacketsHandler;
import com.br.teagadev.prismamc.commons.bukkit.utility.loader.BukkitListeners;
import com.br.teagadev.prismamc.commons.common.PluginInstance;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.client.Client;
import com.br.teagadev.servercommunication.server.Server;

import java.io.IOException;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

public class BukkitMain extends JavaPlugin {
   private static BukkitMain instance;
   private static int serverID;
   private static ServerType serverType;
   private static boolean loaded;
   private static BukkitManager manager;

   public static void console(String msg) {
      Bukkit.getConsoleSender().sendMessage("[Commons] " + msg);
   }

   public static void runAsync(Runnable runnable) {
      Bukkit.getScheduler().runTaskAsynchronously(getInstance(), runnable);
   }

   public static void runLater(Runnable runnable) {
      runLater(runnable, 5L);
   }

   public static void runLater(Runnable runnable, long ticks) {
      Bukkit.getScheduler().runTaskLater(getInstance(), runnable, ticks);
   }

   public static void runSync(Runnable runnable) {
      if (getInstance().isEnabled() && !getInstance().getServer().isPrimaryThread()) {
         getInstance().getServer().getScheduler().runTask(getInstance(), runnable);
      } else {
         runnable.run();
      }

   }

   public static BukkitPlayer getBukkitPlayer(UUID uniqueId) {
      return (BukkitPlayer)CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
   }

   public static void shutdown() {
      Bukkit.shutdown();
   }

   public void onLoad() {
      this.saveDefaultConfig();
      setLoaded(false);
      setInstance(this);
      setManager(new BukkitManager());
      CommonsGeneral.setPluginInstance(PluginInstance.BUKKIT);
      getManager().getConfigurationManager().init();
      this.getServer().getMessenger().registerOutgoingPluginChannel(this, "WDL|CONTROL");
      this.getServer().getMessenger().registerIncomingPluginChannel(this, "WDL|INIT", new WorldDListener());
      setServerType(ServerType.getServer(this.getConfig().getString("Servidor")));
      setServerID(this.getConfig().getInt("ServidorID"));
      CommonsGeneral.getMySQL().openConnection();
   }

   public void onEnable() {
      if (getServerType() != ServerType.UNKNOWN) {
         if (CommonsGeneral.correctlyStarted()) {
            CommonsGeneral.getMySQL().createTables();
            ServerCommunication.startClient(getServerType().getName(), getServerID(), this.getConfig().getString("Socket.Host"));
            ServerCommunication.setPacketHandler(new BukkitPacketsHandler());
            BukkitServerAPI.unregisterCommands(new String[]{"op", "deop", "kill", "about", "ver", "?", "scoreboard", "me", "say", "pl", "plugins", "reload", "rl", "stop", "ban", "ban-ip", "msg", "ban-list", "help", "pardon", "pardon-ip", "tp", "xp", "gamemode", "minecraft"});
            BukkitCommandFramework.INSTANCE.loadCommands(this, "com.br.teagadev.prismamc.commons.bukkit.commands.register");
            BukkitListeners.loadListeners(getInstance(), "com.br.teagadev.prismamc.commons.bukkit.listeners");
            this.getServer().getMessenger().registerOutgoingPluginChannel(getInstance(), "BungeeCord");
            if (getServerType().useMenuListener()) {
               MenuListener.registerListeners();
            }

            if (getServerType().useActionItem()) {
               Bukkit.getServer().getPluginManager().registerEvents(new ActionItemListener(), getInstance());
            }

            CoreListener.init();
            Bukkit.getServer().getScheduler().runTaskTimer(getInstance(), new BukkitUpdateScheduler(), 1L, 1L);
            this.initialize();
         } else {
            Bukkit.shutdown();
         }
      } else {
         console("§cTipo de servidor nao encontrado, mude na configuracao!");
         Bukkit.shutdown();
      }

   }

   public void onDisable() {
      Client.getInstance().getClientConnection().sendPacket((new CPacketCustomAction(getServerType(), getServerID())).type(PacketType.BUKKIT_SEND_INFO).field("bukkit-server-turn-off"));
      CommonsGeneral.getMySQL().closeConnection();
      if (Server.getInstance() != null) {
         try {
            Server.getInstance().getServerGeneral().disconnect();
         } catch (IOException var2) {
         }
      }

   }

   public void initialize() {
      this.getServer().getScheduler().runTaskTimer(getInstance(), () -> {
         ServerStatusUpdateEvent event = new ServerStatusUpdateEvent(this.getServer().getOnlinePlayers().size(), true);
         this.getServer().getPluginManager().callEvent(event);
      }, 0L, 20L * (long)getServerType().getSecondsUpdateStatus());
   }

   public static BukkitMain getInstance() {
      return instance;
   }

   public static void setInstance(BukkitMain instance) {
      BukkitMain.instance = instance;
   }

   public static int getServerID() {
      return serverID;
   }

   public static void setServerID(int serverID) {
      BukkitMain.serverID = serverID;
   }

   public static ServerType getServerType() {
      return serverType;
   }

   public static void setServerType(ServerType serverType) {
      BukkitMain.serverType = serverType;
   }

   public static boolean isLoaded() {
      return loaded;
   }

   public static void setLoaded(boolean loaded) {
      BukkitMain.loaded = loaded;
   }

   public static BukkitManager getManager() {
      return manager;
   }

   public static void setManager(BukkitManager manager) {
      BukkitMain.manager = manager;
   }
}