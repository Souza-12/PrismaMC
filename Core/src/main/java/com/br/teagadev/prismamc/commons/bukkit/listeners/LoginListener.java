package com.br.teagadev.prismamc.commons.bukkit.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.profile.token.AcessToken;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.client.Client;
import java.util.HashMap;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent.Result;

public class LoginListener implements Listener {
   public static final HashMap<UUID, GamingProfile> connectionQueue = new HashMap();

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onLoad(AsyncPlayerPreLoginEvent event) {
      if (!this.isRunning()) {
         event.disallow(Result.KICK_OTHER, "§cO servidor ainda não carregou.");
      } else {
         UUID uniqueId = event.getUniqueId();
         if (Bukkit.getPlayer(uniqueId) == null) {
            BukkitPlayer profile;
            connectionQueue.put(uniqueId, profile = new BukkitPlayer(event.getName(), "", uniqueId));
            profile.setTokenListener((accessToken) -> {
               profile.setAcessToken(accessToken);
               synchronized(profile) {
                  profile.notifyAll();
               }
            });
            profile.sendPacket((new CPacketCustomAction(event.getName(), uniqueId)).type(PacketType.BUKKIT_REQUEST_ACCOUNT_TO_BUNGEECORD));
            synchronized(profile) {
               try {
                  profile.wait(6000L);
               } catch (InterruptedException var7) {
                  connectionQueue.remove(uniqueId);
                  event.setKickMessage("§cFalha no login: " + var7);
                  event.setLoginResult(Result.KICK_OTHER);
                  var7.printStackTrace();
               }
            }

            if (profile.getAcessToken() != null) {
               if (profile.getAcessToken() == AcessToken.REJECTED) {
                  event.disallow(Result.KICK_OTHER, "§cSeu acesso foi invalidado pelo servidor.");
               } else {
                  CommonsGeneral.getProfileManager().addGamingProfile(uniqueId, profile);
               }
            } else {
               event.disallow(Result.KICK_OTHER, String.format("§cA sala %s §cnão possui recebimentos de dados do mesmo.", BukkitMain.getServerType().getName()));
            }

            connectionQueue.remove(uniqueId);
         } else {
            event.disallow(Result.KICK_OTHER, "§cVocê já está conectado ao servidor.");
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onRemoveAccount(AsyncPlayerPreLoginEvent event) {
      if (event.getLoginResult() != Result.KICK_OTHER) {
         if (!CommonsGeneral.getProfileManager().containsProfile(event.getUniqueId())) {
            event.disallow(Result.KICK_OTHER, String.format("§cA sala %s §cnão possui recebimentos de dados do mesmo.", BukkitMain.getServerType().getName()));
            CommonsGeneral.getProfileManager().removeGamingProfile(event.getUniqueId());
         }

      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onLeave(PlayerQuitEvent event) {
      CommonsGeneral.getProfileManager().removeGamingProfile(event.getPlayer().getUniqueId());
   }

   private boolean isRunning() {
      if (!BukkitMain.isLoaded()) {
         return false;
      } else {
         return !BukkitSettings.LOGIN_OPTION ? false : Client.getInstance().getClientConnection().isConnected();
      }
   }
}