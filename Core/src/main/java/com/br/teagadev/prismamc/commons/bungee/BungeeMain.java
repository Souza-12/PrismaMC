package com.br.teagadev.prismamc.commons.bungee;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandFramework;
import com.br.teagadev.prismamc.commons.bungee.manager.BungeeManager;
import com.br.teagadev.prismamc.commons.bungee.networking.BungeePacketsHandler;
import com.br.teagadev.prismamc.commons.bungee.scheduler.BungeeUpdateScheduler;
import com.br.teagadev.prismamc.commons.bungee.utility.BungeeListeners;
import com.br.teagadev.prismamc.commons.bungee.utility.logfilter.LogFilterBungee;
import com.br.teagadev.prismamc.commons.common.PluginInstance;
import com.br.teagadev.servercommunication.ServerCommunication;
import com.br.teagadev.servercommunication.server.Server;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.config.ServerInfo;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeMain extends Plugin {
   private List<String> messagesBroadcast = Arrays.asList("§eParticipe da nossa §6comunidade §eem §bdiscord.gg/crazzymc", "§eAcompanhe suas §6estatísticas §eusando o §b/stats", "§eCansado de usar sempre a mesma §6skin§e? Confira nosso sistema §b/skin", "§eCompre §6ranks e mais§e em nossa loja §eloja.crazzymc.com", "§eEncontrou um jogador usando §6trapaças§e? Utilize §b/report <jogador> <motivo>");
   private static BungeeMain instance;
   private static BungeeMain api;
   private static boolean loaded = false;
   private static BungeeManager manager;
   private static String socketServerHost;
   private int messagesIndex = 0;

   public static BungeeMain getApi() {
      return api;
   }

   public static void shutdown() {
      ProxyServer.getInstance().stop();
   }

   public static void console(String msg) {
      ProxyServer.getInstance().getConsole().sendMessage("[Commons] " + msg);
   }

   public static void runAsync(Runnable runnable) {
      ProxyServer.getInstance().getScheduler().runAsync(getInstance(), runnable);
   }

   public static void runLater(Runnable runnable) {
      ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, 500L, TimeUnit.MILLISECONDS);
   }

   public static void runLater(Runnable runnable, long ms) {
      ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, ms, TimeUnit.MILLISECONDS);
   }

   public static void runLater(Runnable runnable, int tempo, TimeUnit timeUnit) {
      ProxyServer.getInstance().getScheduler().schedule(getInstance(), runnable, (long)tempo, timeUnit);
   }

   public static boolean registerServer(String serverName, String address, int port) {
      if (ProxyServer.getInstance().getServers().containsKey(serverName)) {
         console("[DynamicServers] " + serverName + " error on register this server! (" + address + ":" + port + ")");
         return false;
      } else {
         ProxyServer.getInstance().getServers().put(serverName, ProxyServer.getInstance().constructServerInfo(serverName, new InetSocketAddress(address, port), "Unknown MOTD", false));
         console("[DynamicServers] " + serverName + " registred! (" + address + ":" + port + ")");
         return true;
      }
   }

   public static void removeServer(String serverName) {
      if (ProxyServer.getInstance().getServers().containsKey(serverName)) {
         TextComponent reason = new TextComponent("You were kicked because the server was removed.");
         ServerInfo info = ProxyServer.getInstance().getServerInfo(serverName);
         if (info != null) {
            Iterator var3 = info.getPlayers().iterator();

            while(var3.hasNext()) {
               ProxiedPlayer player = (ProxiedPlayer)var3.next();
               player.disconnect(reason);
            }

            console("[DynamicServers] " + serverName + " removed! (" + info.getAddress().getAddress().getHostAddress() + ":" + info.getAddress().getPort() + ")");
            ProxyServer.getInstance().getServers().remove(info.getName());
         }
      }
   }

   public static boolean isValid(ProxiedPlayer proxiedPlayer) {
      if (proxiedPlayer == null) {
         return false;
      } else if (proxiedPlayer.getServer() == null) {
         return false;
      } else {
         return proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("Login") ? false : CommonsGeneral.getProfileManager().containsProfile(proxiedPlayer.getName());
      }
   }

   public static BungeePlayer getBungeePlayer(UUID uniqueId) {
      return (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
   }

   public static BungeePlayer getBungeePlayer(String name) {
      return (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(name);
   }

   public void onLoad() {
      setInstance(this);
      CommonsGeneral.setPluginInstance(PluginInstance.BUNGEECORD);
      if (!this.getDataFolder().exists()) {
         this.getDataFolder().mkdir();
      }

      setManager(new BungeeManager());
      getManager().getConfigManager().getBungeeConfig().load();
      getManager().getConfigManager().getPermissionsConfig().load();
      CommonsGeneral.getMySQL().openConnection();
   }

   public void onEnable() {
      if (CommonsGeneral.correctlyStarted()) {
         LogFilterBungee.load(getInstance());
         CommonsGeneral.getMySQL().createTables();
         ServerCommunication.setPacketHandler(new BungeePacketsHandler());
         ServerCommunication.startServer(socketServerHost);
         CommonsGeneral.getServersManager().init();
         (new BungeeCommandFramework(this)).loadCommands(this, "com.br.teagadev.prismamc.commons.bungee.commands.register");
         BungeeListeners.loadListeners(getInstance(), "com.br.teagadev.prismamc.commons.bungee.listeners");
         this.getProxy().getScheduler().schedule(getInstance(), new BungeeUpdateScheduler(), 1L, 1L, TimeUnit.SECONDS);
         this.getProxy().getScheduler().schedule(this, () -> {
            if (this.messagesIndex >= this.messagesBroadcast.size()) {
               this.messagesIndex = 0;
            }

            this.getProxy().broadcast(TextComponent.fromLegacyText("§6§lCRAZZY §7» " + (String)this.messagesBroadcast.get(this.messagesIndex)));
            ++this.messagesIndex;
         }, 2L, 2L, TimeUnit.MINUTES);
         getManager().init();
      } else {
         shutdown();
      }

   }

   public void onDisable() {
      CommonsGeneral.getMySQL().closeConnection();
      if (Server.getInstance() != null) {
         try {
            Server.getInstance().getServerGeneral().disconnect();
         } catch (IOException var2) {
         }
      }

   }

   public static BungeeMain getInstance() {
      return instance;
   }

   public static void setInstance(BungeeMain instance) {
      BungeeMain.instance = instance;
   }

   public static void setApi(BungeeMain api) {
      BungeeMain.api = api;
   }

   public static boolean isLoaded() {
      return loaded;
   }

   public static void setLoaded(boolean loaded) {
      BungeeMain.loaded = loaded;
   }

   public static BungeeManager getManager() {
      return manager;
   }

   public static void setManager(BungeeManager manager) {
      BungeeMain.manager = manager;
   }

   public static String getSocketServerHost() {
      return socketServerHost;
   }

   public static void setSocketServerHost(String socketServerHost) {
      BungeeMain.socketServerHost = socketServerHost;
   }
}