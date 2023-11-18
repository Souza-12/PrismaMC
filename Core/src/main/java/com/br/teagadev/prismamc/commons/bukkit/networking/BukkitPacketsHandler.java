package com.br.teagadev.prismamc.commons.bukkit.networking;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.fake.FakeAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeGroupEvent;
import com.br.teagadev.prismamc.commons.bukkit.listeners.LoginListener;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.profile.token.AcessToken;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.GameServer;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import com.br.teagadev.prismamc.commons.custompackets.CommonPacketHandler;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketAction;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.client.Client;
import com.google.gson.JsonObject;
import com.mojang.authlib.properties.Property;
import java.net.Socket;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class BukkitPacketsHandler extends CommonPacketHandler {
   public void handleCPacketAction(CPacketAction packet, Socket socket) {
      if (packet.getType().equalsIgnoreCase("TimedOut")) {
         Client.getInstance().getClientConnection().debug("Connection timed out!");
      }

   }

   public void handleCPacketPlayerAction(CPacketCustomAction packet, Socket socket) {
      PacketType type = packet.getPacketType();
      if (type != null) {
         switch(type) {
         case BUKKIT_RECEIVE_ACCOUNT_FROM_BUNGEECORD:
            this.handleAccountReceive(packet);
            break;
         case BUKKIT_PLAYER_RESPAWN:
            this.handleRespawnPlayer(packet);
            break;
         case BUKKIT_RECEIVE_SKIN_DATA:
            this.handleSkinData(packet);
            break;
         case BUKKIT_GO:
            this.handleGo(packet);
            break;
         case BUNGEE_SEND_UPDATED_STATS:
            this.handleBungeeSendStats(packet);
            break;
         case BUNGEE_SEND_KICK:
            this.handleKick(packet);
            break;
         case BUNGEE_SEND_PLAYER_ACTION:
            this.handleBungeeSendPlayerAction(packet);
            break;
         case BUNGEE_SEND_INFO:
            this.handleReceiveInfo(packet);
         }
      }

   }

   private void handleGo(CPacketCustomAction packet) {
      Player staff = Bukkit.getPlayerExact(packet.getField());
      if (staff != null) {
         if (!VanishAPI.inAdmin(staff)) {
            VanishAPI.changeAdmin(staff);
         }

         staff.teleport(Bukkit.getPlayer(packet.getUniqueId()));
      }

   }

   private void handleReceiveInfo(CPacketCustomAction packet) {
      Iterator var2;
      Player reportado1;
      if (packet.getField().equalsIgnoreCase("bukkit-server-turn-on")) {
         var2 = Bukkit.getOnlinePlayers().iterator();

         while(var2.hasNext()) {
            reportado1 = (Player)var2.next();
            reportado1.sendMessage(packet.getFieldValue());
            reportado1.playSound(reportado1.getLocation(), Sound.ARROW_HIT, 1.0F, 1.0F);
         }
      } else if (packet.getField().equalsIgnoreCase("teleport-player-from-report")) {
         Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
         if (target != null) {
            reportado1 = BukkitServerAPI.getExactPlayerByNick(packet.getField());
            if (reportado1 == null) {
               target.sendMessage("§cJogador offline.");
               return;
            }

            if (!VanishAPI.inAdmin(target)) {
               VanishAPI.changeAdmin(target, true);
            }

         }
      } else if (packet.getField().equalsIgnoreCase("bungee-send-servers-info")) {
         var2 = CommonsGeneral.getServersManager().getServers().iterator();

         while(var2.hasNext()) {
            NetworkServer networkServers = (NetworkServer)var2.next();
            String prefix = networkServers.getServerType().getName().toLowerCase() + "-" + networkServers.getServerId();
            if (!packet.getJson().has(prefix)) {
               CommonsGeneral.error("Erro ao tentar processar informaçőes de um servidor -> " + networkServers.getServerType().getName() + "-" + networkServers.getServerId());
            } else {
               try {
                  JsonObject INFO = CommonsConst.PARSER.parse(packet.getJson().get(prefix).getAsString()).getAsJsonObject();
                  if (networkServers instanceof GameServer) {
                     CommonsGeneral.getServersManager().getGameServer(networkServers.getServerType(), networkServers.getServerId()).updateData(INFO);
                  } else {
                     CommonsGeneral.getServersManager().updateServerData(networkServers.getServerType(), networkServers.getServerId(), INFO);
                  }
               } catch (Exception var7) {
                  CommonsGeneral.error("Error on update server data! -> " + var7.getLocalizedMessage());
               }
            }
         }
      }

   }

   private void handleKick(CPacketCustomAction packet) {
      Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
      BukkitMain.runSync(() -> {
         if (target != null && target.isOnline()) {
            target.kickPlayer(packet.getField());
         }

      });
   }

   private void handleRespawnPlayer(CPacketCustomAction packet) {
      Player target = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
      if (target != null && target.isOnline()) {
         FakeAPI.respawnPlayer(target);
      }

   }

   private void handleSkinData(CPacketCustomAction packet) {
      BukkitPlayer player = BukkitMain.getBukkitPlayer(packet.getUniqueId());
      if (player != null) {
         player.setLastSkin(new Property(packet.getFieldValue(), packet.getExtraValue(), packet.getExtraValue2()));
         if (packet.getField() != null && packet.getField().equals("sendPacket") && player.getPlayer() != null) {
            FakeAPI.respawnPlayer(player.getPlayer());
         }

      }
   }

   private void handleAccountReceive(CPacketCustomAction packet) {
      GamingProfile profile = (GamingProfile)LoginListener.connectionQueue.get(packet.getUniqueId());
      if (profile != null) {
         for(int i = 1; i < 10; ++i) {
            if (packet.getJson().has("dataCategory-" + i)) {
               JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-" + i).getAsString()).getAsJsonObject();
               profile.getDataHandler().loadFromJSON(DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);
            }
         }

         profile.getTokenListener().onAcessToken(AcessToken.ACCEPTED);
      }

   }

   private void handleBungeeSendPlayerAction(CPacketCustomAction packet) {
      Player player = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
      if (player == null) {
         BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
      } else {
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
         if (bukkitPlayer == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao e nao possui profile");
         } else {
            if (packet.getField().equals("update-data") && packet.getFieldValue().equals("clan")) {
               bukkitPlayer.set(DataType.CLAN, packet.getExtraValue());
               bukkitPlayer.updateTag(bukkitPlayer.getPlayer(), bukkitPlayer.getActualTag(), true);
               BukkitMain.runAsync(() -> {
                  bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
               });
            }

         }
      }
   }

   private void handleBungeeSendStats(CPacketCustomAction packet) {
      Player player = BukkitServerAPI.getExactPlayerByNick(packet.getNick());
      if (player == null) {
         BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
      } else {
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
         if (bukkitPlayer == null) {
            BukkitMain.console(packet.getNick() + " recebeu uma atualizaçao de categoria e nao possui profile");
         } else {
            JsonObject json = CommonsConst.PARSER.parse(packet.getJson().get("dataCategory-1").getAsString()).getAsJsonObject();
            bukkitPlayer.getDataHandler().loadFromJSON(DataCategory.getCategoryByName(json.get("dataCategory-name").getAsString()), json);
            if (!packet.getField().isEmpty()) {
               if (packet.getField().equalsIgnoreCase("group")) {
                  Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeGroupEvent(player, bukkitPlayer.getGroup()));
                  bukkitPlayer.injectPermissions(player);
               } else if (packet.getField().equalsIgnoreCase("add-perm")) {
                  String permission = packet.getFieldValue();
                  bukkitPlayer.getPlayerAttachment().addPermission(permission);
                  bukkitPlayer.getData(DataType.PERMISSIONS).getList().add(permission);
                  BukkitMain.runAsync(() -> {
                     bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
                  });
               }
            }

         }
      }
   }
}