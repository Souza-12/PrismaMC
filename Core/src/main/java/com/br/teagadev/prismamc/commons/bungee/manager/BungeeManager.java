package com.br.teagadev.prismamc.commons.bungee.manager;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.manager.config.BungeeConfigurationManager;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeManager {
   private int actualMessageID;
   private int minutos;
   private BungeeConfigurationManager configManager = new BungeeConfigurationManager();
   private boolean globalWhitelist = false;
   private List<String> whitelistPlayers = new ArrayList();
   private ArrayList<String> messages = new ArrayList();

   public void init() {
      this.loadDefaults();
   }

   private void loadDefaults() {
      BungeeMain.runLater(() -> {
         BungeeMain.runAsync(() -> {
            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var2 = null;

               try {
                  PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM skins WHERE nick='0171'");
                  ResultSet result = preparedStatement.executeQuery();
                  if (!result.next()) {
                     PreparedStatement insert = connection.prepareStatement("INSERT INTO skins (nick, lastUse, value, signature, timestamp) VALUES (?, ?, ?, ?, ?)");
                     insert.setString(1, "0171");
                     insert.setString(2, String.valueOf(System.currentTimeMillis()));
                     insert.setString(3, "eyJ0aW1lc3RhbXAiOjE1NzUxNTE1Mjg5OTcsInByb2ZpbGVJZCI6IjM2NTBlMzZhZmU0ZjRmMGRiYTQ4NDAxY2VkYjE1MGUxIiwicHJvZmlsZU5hbWUiOiIwMTcxIiwic2lnbmF0dXJlUmVxdWlyZWQiOnRydWUsInRleHR1cmVzIjp7IlNLSU4iOnsidXJsIjoiaHR0cDovL3RleHR1cmVzLm1pbmVjcmFmdC5uZXQvdGV4dHVyZS9kYWFmOWZhZGQ3ZWQyMDAxZmUwYTlmZjI3ZmQxYmY3YjBhNjMyZjJlZmY3MGYxMjkzMGIzNGIzNWJlYWE4NjFkIn19fQ==");
                     insert.setString(4, "iEPj+++vKgJsMDXvgqjMZpag/Wzz/LLOjEK+OlUlf1Fn4vRFrfylMYlH+KopzX1TcdoY5vt1fEbgradTJEFUuFOXL7AI+RZYUoZ9mMbPpXn3Xbkhcw+Q5D9+EDV1WHVLXpnM3sMZPApMAGNMzOAUUChxQbz0HVrB3OjHtbbmkns2hecABaomRC9Gd4b8mWK/5u1gYUQEwF2I+VmJNuwkzOCUhMhMp4Z4RKg8vfePEXqi+cXD4p+phUU+CGxorHr3VakOI2RuGig8JQAI9L92q6C6yFL1j61nCUn1CVokHtGNFtLxE1ow6PuofLsWjR8+F0ksv/qk1jzYY3tucaXGuuz+QRiWTQiXGd+jp5oHFMx5IF77zU7naRJ4fNunhn3Z/AWQpV4v5huRemHe2QLSfPaICNe6cs9P11R/+EsEJs1R7cO9k3rfulMQPlKLHntI2vxVRnl3VqarD4r3o0sZJWfOp9g3xiKg3KTwx5d28zd7uZqmx+i2ien8Vz8J42/II8bGNP9GIxgbWAPRT2bERJca+lRwDnkS5CGA6QZB3euuWubBLPVV/oG7Q28Ij/MCMTOLoVrBIqM39punuEtULK57u/ERS4YkTIh7+j55tAl0lB9Vpo7Uu3yAPjOsG6OPMrZMUNgzi/PtC3KhWyYgFfGVUBNoIAdSuKs5C2JH1ps=");
                     insert.setString(5, String.valueOf(System.currentTimeMillis() + TimeUnit.DAYS.toMillis(8L)));
                     insert.executeUpdate();
                  }

                  preparedStatement.close();
                  result.close();
                  preparedStatement = connection.prepareStatement("SELECT * FROM global_whitelist WHERE identify='default'");
                  result = preparedStatement.executeQuery();
                  if (result.next()) {
                     this.globalWhitelist = result.getBoolean("actived");
                     Iterator var18 = StringUtility.formatStringToArrayWithoutSpace(result.getString("nicks")).iterator();

                     while(var18.hasNext()) {
                        String nick = (String)var18.next();
                        this.getWhitelistPlayers().add(nick.toLowerCase());
                     }
                  }

                  result.close();
                  preparedStatement.close();
               } catch (Throwable var15) {
                  var2 = var15;
                  throw var15;
               } finally {
                  if (connection != null) {
                     if (var2 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var14) {
                           var2.addSuppressed(var14);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var17) {
               BungeeMain.console("Ocorreu um erro ao tentar carregar coisas necessarias -> " + var17.getLocalizedMessage());
            }

            BungeeMain.setLoaded(true);
         });
      }, 2, TimeUnit.SECONDS);
   }

   public void warnStaff(String message, Groups tag) {
      Iterator var3 = ProxyServer.getInstance().getPlayers().iterator();

      while(var3.hasNext()) {
         ProxiedPlayer proxiedPlayer = (ProxiedPlayer)var3.next();
         if (proxiedPlayer.hasPermission("commons.receivewarn") && BungeeMain.isValid(proxiedPlayer) && BungeeMain.getBungeePlayer(proxiedPlayer.getName()).getGroup().getLevel() >= tag.getLevel()) {
            proxiedPlayer.sendMessage(message);
         }
      }

   }

   public int getActualMessageID() {
      return this.actualMessageID;
   }

   public int getMinutos() {
      return this.minutos;
   }

   public BungeeConfigurationManager getConfigManager() {
      return this.configManager;
   }

   public boolean isGlobalWhitelist() {
      return this.globalWhitelist;
   }

   public List<String> getWhitelistPlayers() {
      return this.whitelistPlayers;
   }

   public ArrayList<String> getMessages() {
      return this.messages;
   }

   public void setActualMessageID(int actualMessageID) {
      this.actualMessageID = actualMessageID;
   }

   public void setMinutos(int minutos) {
      this.minutos = minutos;
   }

   public void setConfigManager(BungeeConfigurationManager configManager) {
      this.configManager = configManager;
   }

   public void setGlobalWhitelist(boolean globalWhitelist) {
      this.globalWhitelist = globalWhitelist;
   }

   public void setWhitelistPlayers(List<String> whitelistPlayers) {
      this.whitelistPlayers = whitelistPlayers;
   }

   public void setMessages(ArrayList<String> messages) {
      this.messages = messages;
   }
}