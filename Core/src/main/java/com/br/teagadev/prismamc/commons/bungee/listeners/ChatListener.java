package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.common.clan.Clan;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.punishment.types.Mute;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import java.util.Iterator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class ChatListener implements Listener {
   @EventHandler
   public void onChatMessage(ChatEvent event) {
      if (!event.isCommand() || event.getMessage().toLowerCase().startsWith("/tell")) {
         if (!event.isCancelled()) {
            ProxiedPlayer proxiedPlayer = (ProxiedPlayer)event.getSender();
            if (!CommonsGeneral.getProfileManager().containsProfile(proxiedPlayer.getName())) {
               proxiedPlayer.disconnect(TextComponent.fromLegacyText("§4§lERRO\n\n§fVocê não possuí uma sessão no servidor."));
               event.setCancelled(true);
            } else {
               BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
               Mute mute = bungeePlayer.getPunishmentHistoric().getActualMute();
               if (mute != null) {
                  if (mute.isExpired()) {
                     mute.unmute("CONSOLE > Punição expirada");
                  } else if (mute.isPermanent()) {
                     event.setCancelled(true);
                     proxiedPlayer.sendMessage("\n§cVocê está mutado permanentemente.\n§eVocê pode ser desmutado adquirindo unmute em: www.crazzymc.com.br\n");
                  } else {
                     event.setCancelled(true);
                     proxiedPlayer.sendMessage("\n§cVocê está mutado temporariamente.\n§cTempo restante: §e%tempo%\n§eVocê pode ser desmutado adquirindo unmute em: www.crazzymc.com.br\n".replace("%tempo%", DateUtils.formatDifference(mute.getPunishmentTime())));
                  }
               }

               if (!event.isCancelled() && !event.getMessage().toLowerCase().startsWith("/tell")) {
                  String message = event.getMessage();
                  if (message.contains("%")) {
                     message = message.replaceAll("%", "%%");
                  }

                  Groups playerGroup = bungeePlayer.getGroup();
                  if (!event.isCancelled()) {
                     if (message.contains("&") && playerGroup.getLevel() > Groups.MEMBRO.getLevel()) {
                        message = message.replaceAll("&", "§");
                     }

                     if (bungeePlayer.isInClanChat()) {
                        event.setCancelled(true);
                        Clan clan = ClanManager.getClan(bungeePlayer.getString(DataType.CLAN));
                        String colorNick = clan.getOwner().equals(proxiedPlayer.getName()) ? "§4" : (clan.getAdminList().contains(proxiedPlayer.getName()) ? "§c" : "§7");
                        String formatado = "§e§l(CLAN) " + colorNick + proxiedPlayer.getName() + ": §f" + message;
                        Iterator var10 = clan.getOnlines().iterator();

                        while(var10.hasNext()) {
                           Object object = var10.next();
                           ProxiedPlayer onlines = (ProxiedPlayer)object;
                           onlines.sendMessage(formatado);
                        }
                     } else if (bungeePlayer.isInStaffChat()) {
                        event.setCancelled(true);
                        if (!bungeePlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                           event.setCancelled(true);
                           proxiedPlayer.sendMessage("§cVocê está no StaffChat, mas não está vendo as mensagens. Ative-as.");
                        } else {
                           String formatado = "§e§l(STAFF) " + playerGroup.getColor() + "§l" + playerGroup.getName().toUpperCase() + " " + playerGroup.getColor() + proxiedPlayer.getName() + ": §f" + message;
                           Iterator var14 = ProxyServer.getInstance().getPlayers().iterator();
                           if (var14.hasNext()) {
                              ProxiedPlayer proxiedPlayers = (ProxiedPlayer)var14.next();
                              if (bungeePlayer.getGroup().getLevel() >= Groups.TRIAL.getLevel() && BungeeMain.isValid(proxiedPlayers) && bungeePlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                                 proxiedPlayer.sendMessage(formatado);
                              }

                              return;
                           }
                        }
                     }
                  }
               }

            }
         }
      }
   }
}