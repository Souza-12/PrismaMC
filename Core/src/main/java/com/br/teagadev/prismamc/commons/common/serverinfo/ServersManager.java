package com.br.teagadev.prismamc.commons.common.serverinfo;

import com.br.teagadev.prismamc.commons.common.serverinfo.types.GameServer;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.client.Client;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ServersManager {
   private final int HARDCOREGAMES_SERVIDORES = 6;
   private Map<String, NetworkServer> serverMap;

   public void init() {
      this.serverMap = new HashMap();
      this.registerServers(new NetworkServer(ServerType.LOBBY), new NetworkServer(ServerType.LOBBY_DUELS), new NetworkServer(ServerType.LOBBY_PVP), new NetworkServer(ServerType.LOBBY_HARDCOREGAMES), new NetworkServer(ServerType.LOGIN), new NetworkServer(ServerType.BUNGEECORD), new NetworkServer(ServerType.DUELS_SIMULATOR), new NetworkServer(ServerType.DUELS_GLADIATOR), new NetworkServer(ServerType.PVP_ARENA), new NetworkServer(ServerType.PVP_FPS), new NetworkServer(ServerType.PVP_LAVACHALLENGE), new NetworkServer(ServerType.GLADIATOR));

      for(int i = 1; i <= 6; ++i) {
         this.registerServer(new GameServer(ServerType.HARDCORE_GAMES, i));
      }

      this.registerServer(new GameServer(ServerType.MINIPRISMA, 1));
      this.registerServer(new GameServer(ServerType.EVENTO, 1));
   }

   public NetworkServer getServerByType(ServerType serverType) {
      return this.getServerByType(serverType, 1);
   }

   public NetworkServer getServerByType(ServerType serverType, int serverId) {
      NetworkServer server = (NetworkServer)this.serverMap.getOrDefault(serverType.getName().toLowerCase() + "-" + serverId, (NetworkServer)null);
      if (server == null) {
         Iterator var4 = this.serverMap.values().iterator();

         while(var4.hasNext()) {
            NetworkServer servers = (NetworkServer)var4.next();
            if (servers.getServerType() == serverType && servers.getServerId() == serverId) {
               server = servers;
               break;
            }
         }
      }

      return server;
   }

   public void registerServers(NetworkServer... servers) {
      NetworkServer[] var2 = servers;
      int var3 = servers.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         NetworkServer server = var2[var4];
         this.registerServer(server);
      }

   }

   public NetworkServer getNetworkServer(String name) {
      return this.getNetworkServer((String)name, 1);
   }

   public NetworkServer getNetworkServer(String name, int id) {
      return (NetworkServer)this.serverMap.get(name.toLowerCase() + "-" + id);
   }

   public NetworkServer getNetworkServer(ServerType type, int id) {
      return this.getNetworkServer(type.getName(), id);
   }

   public void registerServer(NetworkServer server) {
      this.serverMap.put(server.getServerType().getName().toLowerCase() + "-" + server.getServerId(), server);
   }

   public NetworkServer getServer(String serverId) {
      return (NetworkServer)this.serverMap.getOrDefault(serverId.toLowerCase(), (NetworkServer)null);
   }

   public List<NetworkServer> getServersList(ServerType serverType) {
      List<NetworkServer> servers = new ArrayList();
      Iterator var3 = this.serverMap.values().iterator();

      while(var3.hasNext()) {
         NetworkServer networkServer = (NetworkServer)var3.next();
         if (networkServer.getServerType() == serverType) {
            servers.add(networkServer);
         }
      }

      return servers;
   }

   public Collection<NetworkServer> getServers() {
      return this.serverMap.values();
   }

   public int getAmountOnNetwork() {
      return this.getNetworkServer("bungeecord").getOnlines();
   }

   public void sendRequireUpdate() {
      Client.getInstance().getClientConnection().sendPacket((new CPacketCustomAction()).type(PacketType.BUKKIT_SEND_INFO).field("require-servers-info"));
   }

   public void updateServerData(JsonObject json) {
      ServerType serverType = ServerType.getServer(json.get("serverType").getAsString());
      int serverId = json.get("serverID").getAsInt();
      this.updateServerData(serverType, serverId, json);
   }

   public void updateServerData(ServerType serverType, int serverId, JsonObject json) {
      if (serverType != ServerType.UNKNOWN) {
         if (serverType.isHardcoreGames(false)) {
            this.getHardcoreGamesServer(serverType.getName(), serverId).updateData(json);
         } else if (serverType != ServerType.BEDWARS) {
            this.getNetworkServer(serverType.getName(), serverId).update(json);
         }
      }

   }

   public int getAmountOnlinePvP(boolean getLobby) {
      int onlines = 0;
      int onlines1 = onlines + this.getNetworkServer("arena").getOnlines();
      onlines += this.getNetworkServer("fps").getOnlines();
      onlines += this.getNetworkServer("lavachallenge").getOnlines();
      if (getLobby) {
         onlines += this.getNetworkServer("lobbypvp").getOnlines();
      }

      return onlines;
   }

   public int getAmountOnlineDuels(boolean getLobby) {
      int onlines = 0;
      int onlines1 = onlines + this.getNetworkServer("gladiator").getOnlines();
      onlines += this.getNetworkServer("simulator").getOnlines();
      if (getLobby) {
         onlines += this.getNetworkServer("LobbyDuels").getOnlines();
      }

      return onlines;
   }

   public int getAmountOnlineHardcoreGames(boolean getLobby) {
      int onlines = 0;

      GameServer server;
      for(Iterator var3 = this.getHardcoreGamesServers().iterator(); var3.hasNext(); onlines += server.getOnlines()) {
         server = (GameServer)var3.next();
      }

      if (getLobby) {
         onlines += this.getNetworkServer("lobbyhardcoregames").getOnlines();
      }

      return onlines;
   }

   public GameServer getGameServer(String text) {
      return (GameServer)this.getServer(text);
   }

   public List<GameServer> getHardcoreGamesServers() {
      List<GameServer> list = new ArrayList();

      for(int i = 1; i <= 6; ++i) {
         list.add(this.getHardcoreGamesServer(i));
      }

      return list;
   }

   public GameServer getGameServer(ServerType serverType, int serverId) {
      return (GameServer)this.getServer(serverType.getName().toLowerCase() + "-" + serverId);
   }

   public GameServer getHardcoreGamesServer(int id) {
      return (GameServer)this.getServer("hardcoregames-" + id);
   }

   public GameServer getHardcoreGamesServer(String name, int id) {
      return (GameServer)this.getServer(name + "-" + id);
   }
}