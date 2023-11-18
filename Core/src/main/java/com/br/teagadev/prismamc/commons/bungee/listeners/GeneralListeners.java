package com.br.teagadev.prismamc.commons.bungee.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.commands.register.ClearAccountCommand;
import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent;
import com.br.teagadev.prismamc.commons.bungee.events.BungeeUpdateEvent.BungeeUpdateType;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import com.br.teagadev.prismamc.commons.bungee.skins.SkinStorage;
import com.br.teagadev.prismamc.commons.common.punishment.PunishmentManager;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

public class GeneralListeners implements Listener {
   private int MINUTES = 0;

   @EventHandler
   public void onSecond(BungeeUpdateEvent event) {
      if (event.getType() == BungeeUpdateType.SEGUNDO) {
         if (event.getSeconds() % 2L == 0L) {
            NetworkServer bungee = CommonsGeneral.getServersManager().getNetworkServer("bungeecord");
            bungee.setOnline(true);
            bungee.setLastUpdate(System.currentTimeMillis());
            bungee.setOnlines(ProxyServer.getInstance().getOnlineCount());
         }

      }
   }

   @EventHandler
   public void onMinute(BungeeUpdateEvent event) {
      if (event.getType() == BungeeUpdateType.MINUTO) {
         ++this.MINUTES;
         if (this.MINUTES % BungeeMain.getManager().getMinutos() == 0) {
            if (BungeeMain.getManager().getMessages().size() == 0) {
               return;
            }

            int actualID = BungeeMain.getManager().getActualMessageID();
            if (actualID >= BungeeMain.getManager().getMessages().size()) {
               actualID = 0;
            }

            String message = (String)BungeeMain.getManager().getMessages().get(actualID);
            ProxyServer.getInstance().getPlayers().forEach((onlines) -> {
               onlines.sendMessage(message);
            });
            BungeeMain.getManager().setActualMessageID(actualID + 1);
         } else if (this.MINUTES % 30 == 0) {
            CommonsGeneral.getUUIDFetcher().checkCache(() -> {
               SkinStorage.checkCache(() -> {
                  PremiumMapManager.checkCache(PunishmentManager::checkCache);
               });
            });
         }

      }
   }

   @EventHandler
   public void onHour(BungeeUpdateEvent event) {
      if (event.getType() == BungeeUpdateType.HORA) {
         BungeeMain.runAsync(() -> {
            ArrayList toRemove = new ArrayList();

            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM accounts_to_delete");
                  ResultSet result = preparedStatement.executeQuery();

                  while(result.next()) {
                     String time = result.getString("timestamp");
                     long timestamp = 0L;

                     try {
                        timestamp = Long.parseLong(time);
                     } catch (Exception var18) {
                        toRemove.add(result.getString("nick"));
                     }

                     if (timestamp + TimeUnit.DAYS.toMillis(7L) < System.currentTimeMillis()) {
                        toRemove.add(result.getString("nick"));
                     }
                  }

                  result.close();
                  preparedStatement.close();
               } catch (Throwable var19) {
                  var2 = var19;
                  throw var19;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var17) {
                           var2.addSuppressed(var17);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var21) {
               BungeeMain.console("§cOcorreu um erro ao tentar checar a lista de jogadores banidos para serem removidos -> " + var21.getLocalizedMessage());
               var21.printStackTrace();
            }

            if (toRemove.size() != 0) {
               Iterator var22 = toRemove.iterator();

               while(var22.hasNext()) {
                  String nick = (String)var22.next();
                  ClearAccountCommand.clearAccount(nick);
               }
            }

            toRemove.clear();
         });
      }
   }
}