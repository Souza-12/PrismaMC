package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import com.br.teagadev.prismamc.commons.bungee.skins.SkinApplier;
import com.br.teagadev.prismamc.commons.bungee.skins.SkinStorage;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.punishment.PunishmentManager;
import com.br.teagadev.prismamc.commons.common.punishment.types.Ban;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.LoginEvent;
import net.md_5.bungee.api.event.PlayerDisconnectEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class LoginListener implements Listener {
   @EventHandler
   public void onLogin(LoginEvent event) {
      if (!event.isCancelled()) {
         String nick = event.getConnection().getName();
         String address = event.getConnection().getAddress().getAddress().getHostAddress();
         if (!PremiumMapManager.containsMap(nick)) {
            event.setCancelled(true);
            event.setCancelReason("§fO seu registro não foi verificado, tente entrar novamente.");
         } else if (BungeeMain.getManager().isGlobalWhitelist() && !BungeeMain.getManager().getWhitelistPlayers().contains(nick.toLowerCase())) {
            event.setCancelled(true);
            event.setCancelReason("§cEste servidor está disponivel somente para membros da equipe.");
         } else {
            event.registerIntent(BungeeMain.getInstance());
            BungeeMain.runAsync(() -> {
               UUID uniqueId = CommonsGeneral.getUUIDFetcher().getOfflineUUID(event.getConnection().getName());
               BungeePlayer profile = (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(uniqueId);
               if (profile != null && !profile.isAvailableToJoin()) {
                  event.setCancelled(true);
                  event.setCancelReason("§cErro ao carregar sua sessão, tente novamente mais tarde.");
                  event.completeIntent(BungeeMain.getInstance());
               } else if (event.isCancelled()) {
                  event.completeIntent(BungeeMain.getInstance());
               } else {
                  if (profile == null) {
                     profile = new BungeePlayer(event.getConnection().getName(), address, uniqueId);
                  }

                  Ban ban = PunishmentManager.getBanCache(nick);
                  if (ban == null) {
                     try {
                        profile.getPunishmentHistoric().loadBans();
                     } catch (SQLException var10) {
                        CommonsGeneral.error("onLogin() : loadBans() : LoginListener.Java -> " + var10.getLocalizedMessage());
                        event.setCancelled(true);
                        event.completeIntent(BungeeMain.getInstance());
                        return;
                     }

                     ban = profile.getPunishmentHistoric().getActualBan();
                     if (ban != null) {
                        PunishmentManager.addCache(nick, ban);
                     }
                  }

                  if (ban != null && ban.isPunished()) {
                     if (!ban.isExpired()) {
                        event.setCancelled(true);
                        if (ban.isPermanent()) {
                           event.setCancelReason(TextComponent.fromLegacyText("§cVocê está permanentemente banido.\n\n§cMotivo: %motivo%\n§cAutor: %baniu%\n\n§cSolicite seu appeal no nosso discord: discord..crazzymc.com.br\n§cAdquira unban em nossa loja: www.crazzymc.com.br".replace("%motivo%", ban.getMotive()).replace("%baniu%", ban.getBannedBy())));
                        } else {
                           event.setCancelReason(TextComponent.fromLegacyText("§cVocê está temporariamente banido.\n\n§cDuração: %tempo%\n\n§cMotivo: %motivo%\n§cAutor: %baniu%\n\n§cSolicite seu appeal no nosso discord: discord..crazzymc.com.br\n§cAdquira unban em nossa loja: www.crazzymc.com.br".replace("%motivo%", ban.getMotive()).replace("%baniu%", ban.getBannedBy()).replace("%tempo%", DateUtils.formatDifference(ban.getPunishmentTime()))));
                        }
                     } else {
                        ban.unban("CONSOLE - BAN EXPIRADO");
                        PunishmentManager.removeCache(nick);
                     }
                  }

                  if (event.isCancelled()) {
                     event.completeIntent(BungeeMain.getInstance());
                  } else {
                     try {
                        SkinApplier.handleSkin(event.getConnection());
                     } catch (Exception var9) {
                        event.setCancelled(true);
                        event.setCancelReason("§cHouve um erro ao carregar sua skin em nossa API-MINESKIN.");
                        BungeeMain.console("Ocorreu um erro ao tentar setar a skin de um jogador -> " + var9.getLocalizedMessage());
                        var9.printStackTrace();
                        event.completeIntent(BungeeMain.getInstance());
                        return;
                     }

                     if (this.availableToLoad(profile, uniqueId, address, event.getConnection().isOnlineMode())) {
                        try {
                           profile.getDataHandler().load(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.PREFERENCES});
                        } catch (SQLException var8) {
                           BungeeMain.console("§cOcorreu um erro ao tentar carregar as categorias principais de um player -> " + var8.getLocalizedMessage());
                           event.setCancelled(true);
                           event.setCancelReason("Não foi possivel carregar sua conta, tente novamente mais tarde.");
                           event.completeIntent(BungeeMain.getInstance());
                           return;
                        }

                        profile.getDataHandler().getData(DataType.LAST_IP).setValue(address);
                        profile.getDataHandler().getData(DataType.LAST_LOGGED_IN).setValue(System.currentTimeMillis());
                        profile.setAddress(address);
                        if (!profile.getDataHandler().getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
                           ClanManager.load(profile.getString(DataType.CLAN));
                        }
                     } else {
                        profile.getDataHandler().loadDefault(DataCategory.ACCOUNT);
                     }

                     CommonsGeneral.getProfileManager().addGamingProfile(uniqueId, profile);
                     event.completeIntent(BungeeMain.getInstance());
                  }
               }
            });
         }
      }
   }

   @EventHandler
   public void onPostLogin(PostLoginEvent event) {
      ProxiedPlayer proxiedPlayer = event.getPlayer();
      BungeeMain.runAsync(() -> {
         BungeeMain.runLater(() -> {
            if (proxiedPlayer != null && proxiedPlayer.getServer() != null) {
               if (BungeeMain.getBungeePlayer(proxiedPlayer.getName()) != null) {
                  BungeeMain.getBungeePlayer(proxiedPlayer.getName()).handleLogin(proxiedPlayer);
               }

            }
         }, 1, TimeUnit.SECONDS);
      });
   }

   @EventHandler(
      priority = 64
   )
   public void onDisconnect(PlayerDisconnectEvent event) {
      BungeeMain.runAsync(() -> {
         ProxiedPlayer proxiedPlayer = event.getPlayer();
         SkinStorage.removeFromHash(proxiedPlayer.getName());
         if (proxiedPlayer.getServer() != null) {
            BungeePlayerPermissions.clearPermissions(proxiedPlayer);
            BungeePlayer profile = (BungeePlayer)CommonsGeneral.getProfileManager().getGamingProfile(proxiedPlayer.getName());
            if (profile != null) {
               profile.handleQuit();
               String serverName = proxiedPlayer.getServer().getInfo().getName().toLowerCase();
               if (serverName.equals("login")) {
                  return;
               }

               profile.setLoginOnServer("Lobby");
            }
         }

      });
   }

   private boolean availableToLoad(BungeePlayer profile, UUID uniqueId, String address, boolean premium) {
      if (premium) {
         return true;
      } else if (!CommonsGeneral.getProfileManager().containsProfile(uniqueId)) {
         return false;
      } else {
         return profile.isValidSession() && profile.getAddress().equals(address);
      }
   }
}