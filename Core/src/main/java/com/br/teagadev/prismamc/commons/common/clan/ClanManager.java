package com.br.teagadev.prismamc.commons.common.clan;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;

public class ClanManager {
   private static final HashMap<String, Clan> clans = new HashMap();

   public static void load(String name) {
      if (!hasClanData(name)) {
         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var2 = null;

            try {
               PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM clans WHERE (nome='" + name + "');");
               ResultSet result = preparedStatament.executeQuery();
               if (result.next()) {
                  loadFromJSON(result.getString("nome"), CommonsConst.PARSER.parse(result.getString("data")).getAsJsonObject());
               }

               result.close();
               preparedStatament.close();
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
            CommonsGeneral.error("§cOcorreu um erro ao tentar carregar um clan... -> " + var15.getLocalizedMessage());
         }

      }
   }

   private static void loadFromJSON(String name, JsonObject json) {
      if (json != null) {
         Clan clan = hasClanData(name) ? getClan(name) : new Clan(name, json.get("tag").getAsString());
         clan.setOwner(json.get("dono").getAsString());
         clan.setTag(json.get("tag").getAsString());
         clan.setAdminList(StringUtility.formatStringToArrayWithoutSpace(json.get("admins").getAsString()));
         clan.setMemberList(StringUtility.formatStringToArrayWithoutSpace(json.get("membros").getAsString()));
         clan.setPremium(json.get("premium").getAsBoolean());
         if (!hasClanData(name)) {
            putClan(name, clan);
         }

         clan = null;
      }
   }

   public static void removeClanData(String nome) {
      clans.remove(nome);
   }

   public static Clan getClan(String name) {
      if (!clans.containsKey(name)) {
         load(name);
      }

      return (Clan)clans.get(name.toLowerCase());
   }

   public static void putClan(String name, Clan clan) {
      clans.put(name.toLowerCase(), clan);
   }

   public static void unloadClan(String name) {
      clans.remove(name.toLowerCase());
   }

   public static boolean hasClanData(String nome) {
      return clans.containsKey(nome.toLowerCase());
   }

   public static void saveClan(String clanName) {
      saveClan(clanName, false);
   }

   public static void saveClan(String clanName, boolean remove) {
      if (hasClanData(clanName)) {
         BungeeMain.runAsync(() -> {
            Clan clan = getClan(clanName);

            try {
               Connection connection = CommonsGeneral.getMySQL().getConnection();
               Throwable var3 = null;

               try {
                  PreparedStatement preparedStatament = connection.prepareStatement("UPDATE clans SET data=? WHERE nome='" + clanName + "'");
                  preparedStatament.setString(1, clan.getJSON().toString());
                  preparedStatament.execute();
                  preparedStatament.close();
                  preparedStatament = null;
               } catch (Throwable var13) {
                  var3 = var13;
                  throw var13;
               } finally {
                  if (connection != null) {
                     if (var3 != null) {
                        try {
                           connection.close();
                        } catch (Throwable var12) {
                           var3.addSuppressed(var12);
                        }
                     } else {
                        connection.close();
                     }
                  }

               }
            } catch (SQLException var15) {
               CommonsGeneral.error("Ocorreu um erro ao tentar salvar as estatistícas do clan " + clanName + " > " + var15.getLocalizedMessage());
            }

         });
         if (remove) {
            removeClanData(clanName);
         }

      }
   }

   public static HashMap<String, Clan> getClans() {
      return clans;
   }
}