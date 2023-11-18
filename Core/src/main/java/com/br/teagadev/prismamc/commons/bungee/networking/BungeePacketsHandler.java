package com.br.teagadev.prismamc.commons.bungee.networking;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.skins.SkinApplier;
import com.br.teagadev.prismamc.commons.bungee.skins.SkinStorage;
import com.br.teagadev.prismamc.commons.bungee.skins.MojangAPI.SkinRequestException;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.GameServer;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.custompackets.CommonPacketHandler;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import com.google.gson.JsonObject;
import java.net.Socket;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePacketsHandler extends CommonPacketHandler {
   private Long lastUpdate = 0L;
   private CPacketCustomAction lastResponse;

   public void handleCPacketAction(CPacketAction packet, Socket socket) {
      if (packet.getType().equalsIgnoreCase("HandShake")) {
         Server.getInstance().getServerGeneral().registerClient(packet.getField(), Integer.valueOf(packet.getFieldValue()), socket);
      }

      if (packet.getType().equalsIgnoreCase("Loggout")) {
         Server.getInstance().getServerGeneral().unregisterClient(socket);
      }

   }

   public void handleCPacketPlayerAction(CPacketCustomAction packet, Socket socket) {
      PacketType type = packet.getPacketType();
      if (type != null) {
         switch(type) {
         case BUKKIT_REQUEST_ACCOUNT_TO_BUNGEECORD:
            this.handleBukkitAccountRequest(packet, socket);
            break;
         case BUKKIT_SEND_UPDATED_DATA:
            this.handleBukkitSendDatas(packet);
            break;
         case BUKKIT_SEND_SERVER_DATA:
            this.handleReceivedServerData(packet);
            break;
         case BUNGEE_SET_FAST_SKIN:
            this.handleSetFastSkin(packet, socket);
            break;
         case BUNGEE_SET_RANDOM_SKIN:
            this.handleSetRandomSkin(packet);
            break;
         case BUNGEE_SET_SKIN:
            this.handleSetSkin(packet);
            break;
         case BUNGEE_UPDATE_SKIN:
            this.handleUpdateSkin(packet, socket);
            break;
         case BUKKIT_SEND_INFO:
            this.handleReceiveInfo(packet, socket);
         }
      }

   }

   private void handleReceiveInfo(CPacketCustomAction packet, Socket socket) {
      if (packet.getField().equalsIgnoreCase("bukkit-register-server")) {
         if (StringUtility.isInteger(packet.getExtraValue())) {
            boolean useId = packet.getServerType() == ServerType.HARDCORE_GAMES;
            BungeeMain.registerServer(packet.getServerType().getName() + (useId ? packet.getServerID() : ""), packet.getFieldValue(), Integer.parseInt(packet.getExtraValue()));
         }
      } else {
         ServerType serverType;
         if (packet.getField().equalsIgnoreCase("bukkit-server-turn-on")) {
            serverType = packet.getServerType();
            if (serverType.isPvP(false)) {
               Server.getInstance().sendPacket(ServerType.LOBBY_PVP.getName(), (new CPacketCustomAction(ServerType.BUNGEECORD, 1)).type(PacketType.BUNGEE_SEND_INFO).field(packet.getField()).fieldValue("§aAtenção! Uma sala de %sala% foi liberada para acesso.".replace("%prefix%", "§6§lPVP").replace("%sala%", serverType.getName().toUpperCase())));
            } else if (serverType.isHardcoreGames(false) && serverType != ServerType.MINIPRISMA && serverType != ServerType.EVENTO) {
               Server.getInstance().sendPacket(ServerType.LOBBY_HARDCOREGAMES.getName(), (new CPacketCustomAction(ServerType.BUNGEECORD, 1)).type(PacketType.BUNGEE_SEND_INFO).field(packet.getField()).fieldValue("§aAtenção! Uma sala de %sala% foi liberada para acesso.".replace("%prefix%", "§6§lHG").replace("%sala%", serverType.getName().toUpperCase())));
            }
         } else {
            NetworkServer networkServers;
            if (packet.getField().equalsIgnoreCase("bukkit-server-turn-off")) {
               serverType = packet.getServerType();
               int id = packet.getServerID();
               this.lastUpdate = 0L;
               networkServers = CommonsGeneral.getServersManager().getServerByType(serverType, id);
               if (networkServers != null) {
                  networkServers.setLastUpdate(-1L);
                  networkServers.setOnline(false);
                  networkServers.setOnlines(0);
               }
            } else {
               ProxiedPlayer proxiedPlayer;
               if (packet.getField().equalsIgnoreCase("add-perm")) {
                  proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
                  if (proxiedPlayer == null) {
                     return;
                  }

                  ProxyServer.getInstance().getPluginManager().dispatchCommand(ProxyServer.getInstance().getConsole(), "addperm " + proxiedPlayer.getName() + " " + packet.getFieldValue());
               } else if (packet.getField().equalsIgnoreCase("player-authenticated")) {
                  proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
                  if (proxiedPlayer == null) {
                     return;
                  }

                  BungeePlayer profile = (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());
                  if (profile != null) {
                     BungeeMain.runAsync(() -> {
                        try {
                           profile.getDataHandler().load(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.PREFERENCES});
                        } catch (SQLException var2) {
                           var2.printStackTrace();
                        }

                     });
                     String address = proxiedPlayer.getAddress().getAddress().getHostAddress();
                     profile.set(DataType.LAST_IP, address);
                     profile.setAddress(address);
                     proxiedPlayer.sendMessage("§f");
                  }
               } else if (packet.getField().equalsIgnoreCase("require-servers-info")) {
                  CPacketCustomAction RESPONSE = this.lastResponse;
                  if (this.lastUpdate >= System.currentTimeMillis() && RESPONSE != null) {
                     RESPONSE.setTimestamp(System.currentTimeMillis());
                  } else {
                     RESPONSE = (new CPacketCustomAction()).type(PacketType.BUNGEE_SEND_INFO).field("bungee-send-servers-info");
                     Iterator var11 = CommonsGeneral.getServersManager().getServers().iterator();

                     while(var11.hasNext()) {
                        networkServers = (NetworkServer)var11.next();
                        String prefix = networkServers.getServerType().getName().toLowerCase() + "-" + networkServers.getServerId();
                        if (networkServers instanceof GameServer) {
                           RESPONSE.getJson().addProperty(prefix, ((GameServer)networkServers).toJsonGame().toString());
                        } else {
                           RESPONSE.getJson().addProperty(prefix, networkServers.toJson().toString());
                        }
                     }

                     this.lastUpdate = System.currentTimeMillis() + 200L;
                     this.lastResponse = RESPONSE;
                  }

                  Server.getInstance().sendPacket(socket, RESPONSE);
               }
            }
         }
      }

   }

   private void handleUpdateSkin(CPacketCustomAction packet, Socket socket) {
      ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
      if (proxiedPlayer != null) {
         BungeeMain.runAsync(() -> {
            try {
               SkinStorage.createSkin(packet.getField());
               SkinStorage.getOrCreateSkinForPlayer(packet.getField());
               SkinStorage.setPlayerSkin(packet.getNick(), packet.getField());
               BungeeMain.runLater(() -> {
                  SkinApplier.fastApply(proxiedPlayer, packet.getField(), false);
                  Server.getInstance().sendPacket(socket, (new CPacketCustomAction(proxiedPlayer.getName())).type(PacketType.BUKKIT_PLAYER_RESPAWN));
                  proxiedPlayer.sendMessage("§aSua skin foi atualizada com sucesso!");
               });
            } catch (SkinRequestException var4) {
               proxiedPlayer.sendMessage("§cOcorreu um erro ao tentar atualizar sua skin!");
            }

         });
      }
   }

   private void handleSetSkin(CPacketCustomAction packet) {
      ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
      if (proxiedPlayer != null) {
         BungeeMain.runAsync(() -> {
            try {
               SkinStorage.getOrCreateSkinForPlayer(packet.getField());
               SkinStorage.setPlayerSkin(packet.getNick(), packet.getField());
               SkinApplier.applySkin(proxiedPlayer, packet.getField());
               BungeeMain.runLater(() -> {
                  proxiedPlayer.sendMessage("§aSua skin foi atualizada com sucesso!");
               });
            } catch (SkinRequestException var3) {
               proxiedPlayer.sendMessage("§cOcorreu um erro ao tentar trocar sua skin!");
            }

         });
      }
   }

   private void handleSetRandomSkin(CPacketCustomAction packet) {
      ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
      if (proxiedPlayer != null) {
         BungeeMain.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement preparedStatement = connection.prepareStatement("SELECT nick FROM skins ORDER BY RAND() LIMIT 1");
                  ResultSet result = preparedStatement.executeQuery();
                  if (result.next()) {
                     SkinApplier.fastApply(proxiedPlayer, result.getString("nick"));
                  }

                  BungeeMain.runLater(() -> {
                     proxiedPlayer.sendMessage("§aSua skin foi atualizada com sucesso!");
                  });
                  result.close();
                  preparedStatement.close();
               } catch (Throwable var13) {
                  var2 = var13;
                  throw var13;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var12) {
                           var2.addSuppressed(var12);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var15) {
               proxiedPlayer.sendMessage("§cOcorreu um erro ao tentar trocar sua skin!");
            }

         });
      }
   }

   private void handleSetFastSkin(CPacketCustomAction packet, Socket socket) {
      ProxiedPlayer proxiedPlayer = ProxyServer.getInstance().getPlayer(packet.getNick());
      if (proxiedPlayer != null) {
         SkinApplier.fastApply(proxiedPlayer, packet.getField(), false);
         Server.getInstance().sendPacket(socket, (new CPacketCustomAction(proxiedPlayer.getName())).type(PacketType.BUKKIT_PLAYER_RESPAWN));
      }
   }

   private void handleReceivedServerData(CPacketCustomAction packet) {
      CommonsGeneral.getServersManager().updateServerData(packet.getJson());
   }

   private void handleBukkitAccountRequest(CPacketCustomAction packet, Socket socket) {
      BungeePlayer profile = (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());
      if (profile == null) {
         BungeeMain.console(packet.getNick() + " requisitou que sua conta fosse carregada, e o mesmo nao se encontra no servidor.");
      } else {
         CPacketCustomAction PACKET = (new CPacketCustomAction(packet.getNick(), packet.getUniqueId())).type(PacketType.BUKKIT_RECEIVE_ACCOUNT_FROM_BUNGEECORD);
         List<DataCategory> datasToSend = new ArrayList();
         datasToSend.add(DataCategory.ACCOUNT);
         datasToSend.add(DataCategory.PREFERENCES);
         if (packet.getServerType().isPvP()) {
            datasToSend.add(DataCategory.KITPVP);
         }

         if (packet.getServerType().isHardcoreGames(true)) {
            datasToSend.add(DataCategory.HARDCORE_GAMES);
         }

         if (packet.getServerType() == ServerType.GLADIATOR) {
            datasToSend.add(DataCategory.GLADIATOR);
         }

         if (packet.getServerType() == ServerType.LOGIN) {
            datasToSend.add(DataCategory.REGISTER);
         }

         int categoryID = 1;

         for(Iterator var7 = datasToSend.iterator(); var7.hasNext(); ++categoryID) {
            DataCategory datas = (DataCategory)var7.next();
            PACKET.getJson().addProperty("dataCategory-" + categoryID, profile.getDataHandler().buildJSON(datas, profile.isValidSession()).toString());
         }

         Server.getInstance().getServerGeneral().sendPacket(socket, PACKET);
         datasToSend.clear();
      }
   }

   private void handleBukkitSendDatas(CPacketCustomAction packet) {
      BungeePlayer profile = (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(packet.getNick());
      if (profile == null) {
         BungeeMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
      } else {
         JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-1").getAsString()).getAsJsonObject();
         profile.getDataHandler().loadFromJSON(DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);
      }
   }
}