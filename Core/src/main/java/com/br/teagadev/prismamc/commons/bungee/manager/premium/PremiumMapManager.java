package com.br.teagadev.prismamc.commons.bungee.manager.premium;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.utility.Cache;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.google.common.base.Charsets;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

public class PremiumMapManager {
   private static final HashMap<String, Cache> CACHE = new HashMap();
   private static final List<String> changedNicks = new ArrayList();
   private static final String[] tabelasToUpdate = new String[]{"accounts", "hardcoregames", "kitpvp", "gladiator", "premium_map", "bans", "mutes"};

   public static boolean load(String nick) throws UUIDFetcherException {
      boolean premium = false;
      UUID uuidProfile = null;

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var4 = null;

         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM `premium_map` WHERE nick='" + nick + "'");
            ResultSet result = preparedStatament.executeQuery();
            if (result.next()) {
               premium = result.getBoolean("premium");
               uuidProfile = UUID.fromString(result.getString("uuid"));
            } else {
               UUID uuid = CommonsGeneral.getUUIDFetcher().getUUID(nick);
               premium = uuid != null;
               uuidProfile = uuid != null ? uuid : UUID.nameUUIDFromBytes(("OfflinePlayer:" + nick).getBytes(Charsets.UTF_8));
               if (premium && hasChangedNick(uuidProfile, nick)) {
                  result.close();
                  preparedStatament.close();
                  changedNicks.add(nick);
                  boolean var22 = true;
                  return var22;
               }

               if (!changedNicks.contains(nick)) {
                  PreparedStatement insert = connection.prepareStatement("INSERT INTO premium_map(nick, uuid, premium) VALUES (?, ?, ?)");
                  insert.setString(1, nick);
                  insert.setString(2, String.valueOf(uuidProfile));
                  insert.setBoolean(3, premium);
                  insert.executeUpdate();
                  insert.close();
               }
            }

            result.close();
            preparedStatament.close();
         } catch (Throwable var19) {
            var4 = var19;
            throw var19;
         } finally {
            if (connection != null) {
               if (var4 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var18) {
                     var4.addSuppressed(var18);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var21) {
         var21.printStackTrace();
      }

      CACHE.put(nick.toLowerCase(), new Cache(nick, new PremiumMap(uuidProfile, nick, premium), 3));
      return true;
   }

   public static boolean hasChangedNick(UUID uuid, String newNick) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var3 = null;

         boolean var7;
         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM `premium_map` WHERE uuid='" + uuid.toString() + "'");
            ResultSet result = preparedStatament.executeQuery();
            if (!result.next()) {
               result.close();
               preparedStatament.close();
               boolean var21 = false;
               return var21;
            }

            String olderNick = result.getString("nick");
            result.close();
            preparedStatament.close();
            if (!olderNick.equalsIgnoreCase(newNick)) {
               updateNick(uuid, olderNick, newNick);
            }

            var7 = true;
         } catch (Throwable var18) {
            var3 = var18;
            throw var18;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var17) {
                     var3.addSuppressed(var17);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return var7;
      } catch (SQLException var20) {
         return false;
      }
   }

   private static void updateNick(UUID uuid, String oldNick, String newNick) {
      CACHE.put(newNick.toLowerCase(), new Cache(newNick, new PremiumMap(uuid, newNick, true), 3));
      if (!oldNick.equalsIgnoreCase(newNick)) {
         BungeeMain.console("[PremiumMap] detectou a mudança de Nick de " + oldNick + " para " + newNick + "!");
         CommonsGeneral.runAsync(() -> {
            String[] var2 = tabelasToUpdate;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
               String table = var2[var4];
               MySQLManager.containsAndUpdate(table, "nick", oldNick, "UPDATE " + table + " SET nick='" + newNick + "' where nick='" + oldNick + "';");
            }

         });
         MySQLManager.containsAndUpdate("registros", "nick", oldNick, "DELETE FROM registros WHERE nick='" + oldNick + "';");
      }

   }

   public static void removePremiumMap(String nickTarget) {
      CACHE.remove(nickTarget.toLowerCase());
   }

   public static int getCrackedAmount() {
      int crackedAmount = 0;
      Iterator var1 = CACHE.values().iterator();

      while(var1.hasNext()) {
         Cache cache = (Cache)var1.next();
         if (cache != null && cache.getValue1() != null && !((PremiumMap)cache.getValue1()).isPremium()) {
            ++crackedAmount;
         }
      }

      return crackedAmount;
   }

   public static int getPremiumAmount() {
      int premiumAmont = 0;
      Iterator var1 = CACHE.values().iterator();

      while(var1.hasNext()) {
         Cache cache = (Cache)var1.next();
         if (cache != null && cache.getValue1() != null && ((PremiumMap)cache.getValue1()).isPremium()) {
            ++premiumAmont;
         }
      }

      return premiumAmont;
   }

   public static PremiumMap getPremiumMap(String nick) {
      Cache data = (Cache)CACHE.get(nick.toLowerCase());
      return data == null ? null : (PremiumMap)data.getValue1();
   }

   public static boolean containsMap(String nick) {
      return CACHE.containsKey(nick.toLowerCase());
   }

   public static long getPremiumMaps() {
      return (long)CACHE.size();
   }

   public static void checkCache() {
      checkCache((Runnable)null);
   }

   public static void checkCache(Runnable callback) {
      List<String> keys = Lists.newArrayList();
      Iterator var2 = CACHE.values().iterator();

      while(var2.hasNext()) {
         Cache cache = (Cache)var2.next();
         if (cache.isExpired()) {
            keys.add(cache.getName());
         }
      }

      if (keys.size() != 0) {
         var2 = keys.iterator();

         while(var2.hasNext()) {
            String key = (String)var2.next();
            CACHE.remove(key);
         }
      }

      keys.clear();
      keys = null;
      if (callback != null) {
         callback.run();
      }

   }

   public static List<String> getChangedNicks() {
      return changedNicks;
   }
}