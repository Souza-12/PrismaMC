package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.register.EventoCommand;
import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent;
import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ServerConnectEvent;
import net.md_5.bungee.api.event.ServerConnectEvent.Reason;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class SessionListener implements Listener {
   @EventHandler(
      priority = -64
   )
   public void onServerConnect(ServerConnectEvent event) {
      ProxiedPlayer proxiedPlayer = event.getPlayer();
      if (event.getTarget().getName().equals(ServerType.EVENTO.getName())) {
         BungeePlayer bp = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
         if (!EventoCommand.hasEvento && !bp.isStaffer()) {
            event.setCancelled(true);
            BungeeMain.runLater(() -> {
               proxiedPlayer.sendMessage("§eNenhum evento está disponivel.");
            }, 200L);
            return;
         }
      }

      if (event.getReason() == Reason.JOIN_PROXY) {
         String serverToConnect = "Login";
         String address = proxiedPlayer.getAddress().getAddress().getHostAddress();
         UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(proxiedPlayer.getName());
         if (CommonsGeneral.getProfileManager().containsProfile(uniqueId)) {
            BungeePlayer profile = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
            if (!proxiedPlayer.getPendingConnection().isOnlineMode()) {
               if (profile.isValidSession() && profile.getAddress().equals(address)) {
                  serverToConnect = profile.getLoginOnServer();
               }
            } else {
               serverToConnect = profile.getLoginOnServer();
            }
         }

         if (!CommonsGeneral.getServersManager().getNetworkServer(serverToConnect).isOnline() && !serverToConnect.equalsIgnoreCase("Lobby") && !serverToConnect.equalsIgnoreCase("Login")) {
            serverToConnect = "Lobby";
         }

         if (CommonsGeneral.getServersManager().getNetworkServer(serverToConnect).isOnline()) {
            if (!event.getTarget().getName().equalsIgnoreCase(serverToConnect)) {
               event.setTarget(BungeeMain.getInstance().getProxy().getServerInfo(serverToConnect));
            }
         } else {
            event.setCancelled(true);
            proxiedPlayer.disconnect("§cNão foi possivel conectar-se a uma sala LB.".replace("%servidor%", serverToConnect));
         }

      }
   }

   @EventHandler
   public void onUpdate(BungeeUpdateEvent event) {
      if (event.getType() == BungeeUpdateType.HORA) {
         List<UUID> profilesToRemove = new ArrayList();

         Iterator var3;
         GamingProfile profile;
         for(var3 = CommonsGeneral.getProfileManager().getGamingProfiles().iterator(); var3.hasNext(); profile = null) {
            profile = (GamingProfile)var3.next();
            BungeePlayer bungeePlayer = (BungeePlayer)profile;
            if (bungeePlayer != null) {
               if (!bungeePlayer.isValidSession() && !bungeePlayer.isValidPlayer()) {
                  profilesToRemove.add(profile.getUniqueId());
               }

               bungeePlayer = null;
            } else {
               BungeeMain.console("Um BungeePlayer foi detectado como nulo.");
            }
         }

         var3 = profilesToRemove.iterator();

         while(var3.hasNext()) {
            UUID uuid = (UUID)var3.next();
            CommonsGeneral.getProfileManager().removeGamingProfile(uuid);
         }

         if (profilesToRemove.size() != 0) {
            BungeeMain.console("[Cache] Foram removidas " + profilesToRemove.size() + " contas do cache");
         }

         profilesToRemove.clear();
      }
   }
}