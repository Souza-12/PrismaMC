package com.br.teagadev.prismamc.commons.bungee.skins;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMap;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import com.br.teagadev.prismamc.commons.bungee.skins.MojangAPI.SkinRequestException;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.utility.Cache;
import com.br.teagadev.prismamc.commons.common.utility.skin.Skin;
import com.google.common.collect.Lists;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SkinStorage {
   private static final HashMap<String, Cache> CACHE = new HashMap();
   public static final HashMap<String, String> PLAYER_SKIN = new HashMap();
   private static Class<?> property;
   private static Object DEFAULT_SKIN = null;

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
      if (callback != null) {
         callback.run();
      }

   }

   public static Object getDefaultSkin() {
      if (DEFAULT_SKIN == null) {
         DEFAULT_SKIN = getSkinData("0171");
      }

      return DEFAULT_SKIN;
   }

   public static void removeFromHash(String nick) {
      PLAYER_SKIN.remove(nick);
   }

   public static Object createProperty(String name, String value, String signature) {
      try {
         return ReflectionUtil.invokeConstructor(property, new Class[]{String.class, String.class, String.class}, new Object[]{name, value, signature});
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public static Object createSkinIfNotCreated(String nick) {
      try {
         return getOrCreateSkinForPlayer(nick);
      } catch (SkinRequestException var2) {
         var2.printStackTrace();
         return null;
      }
   }

   public static Object getOrCreateSkinForPlayer(String name) throws SkinRequestException {
      String skin = getPlayerSkin(name);
      if (skin == null) {
         skin = name.toLowerCase();
      }

      Object textures = getSkinData(skin);
      if (textures != null) {
         return textures;
      } else {
         PremiumMap premium = PremiumMapManager.getPremiumMap(name);
         if (premium != null && !premium.isPremium()) {
            textures = getDefaultSkin();
         }

         if (textures != null) {
            return textures;
         } else {
            String uuid = "N/A";
            PremiumMap data = PremiumMapManager.getPremiumMap(name);
            if (data != null) {
               uuid = String.valueOf(data.getUniqueId()).replaceAll("-", "");
            }

            if (uuid.equals("N/A")) {
               uuid = MojangAPI.getUUID(skin);
            }

            textures = MojangAPI.getSkinProperty(uuid);
            setSkinData(skin, textures);
            return textures;
         }
      }
   }

   public static void createSkin(String name) throws SkinRequestException {
      String uuid = "N/A";
      PremiumMap data = PremiumMapManager.getPremiumMap(name);
      if (data != null) {
         uuid = String.valueOf(data.getUniqueId()).replaceAll("-", "");
      }

      try {
         if (uuid.equals("N/A")) {
            uuid = MojangAPI.getUUID(name);
         }

         Object textures = MojangAPI.getSkinProperty(uuid);
         setSkinData(name, textures);
      } catch (SkinRequestException var5) {
         throw new SkinRequestException(var5.getReason());
      } catch (Exception var6) {
         throw new SkinRequestException("aguarde");
      }
   }

   public static String getPlayerSkin(String name) {
      name = name.toLowerCase();
      if (PLAYER_SKIN.containsKey(name)) {
         return (String)PLAYER_SKIN.get(name);
      } else {
         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var2 = null;

            String var6;
            try {
               PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM playerSkin where nick='" + name + "';");
               ResultSet result = preparedStatament.executeQuery();
               String skinUsing;
               if (!result.next()) {
                  result.close();
                  preparedStatament.close();
                  PLAYER_SKIN.put(name, name);
                  skinUsing = name;
                  return skinUsing;
               }

               skinUsing = result.getString("skin");
               result.close();
               preparedStatament.close();
               PLAYER_SKIN.put(name, skinUsing);
               var6 = skinUsing;
            } catch (Throwable var17) {
               var2 = var17;
               throw var17;
            } finally {
               if (connection != null) {
                  if (var2 != null) {
                     try {
                        connection.close();
                     } catch (Throwable var16) {
                        var2.addSuppressed(var16);
                     }
                  } else {
                     connection.close();
                  }
               }

            }

            return var6;
         } catch (SQLException var19) {
            CommonsGeneral.console("Ocorreu um erro ao tentar obter a skin do jogador -> " + var19.getLocalizedMessage());
            return name;
         }
      }
   }

   public static Object getSkinData(String name) {
      name = name.toLowerCase();
      Skin cachedSkin = null;
      if (CACHE.containsKey(name)) {
         cachedSkin = (Skin)((Cache)CACHE.get(name)).getValue1();
      }

      if (cachedSkin != null) {
         return createProperty("textures", cachedSkin.getValue(), cachedSkin.getSignature());
      } else {
         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var3 = null;

            String value;
            try {
               PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM skins where nick='" + name + "';");
               ResultSet result = preparedStatament.executeQuery();
               if (result.next()) {
                  value = result.getString("value");
                  String signature = result.getString("signature");
                  String timestamp = result.getString("timestamp");
                  result.close();
                  preparedStatament.close();
                  Object skin;
                  if (isOld(Long.parseLong(timestamp))) {
                     skin = MojangAPI.getSkinProperty(MojangAPI.getUUID(name));
                     if (skin != null) {
                        setSkinData(name, skin);
                     }
                  } else {
                     CACHE.put(name, new Cache(name, new Skin(name, value, signature), 3));
                  }

                  skin = createProperty("textures", value, signature);
                  return skin;
               }

               result.close();
               preparedStatament.close();
               value = null;
            } catch (Throwable var21) {
               var3 = var21;
               throw var21;
            } finally {
               if (connection != null) {
                  if (var3 != null) {
                     try {
                        connection.close();
                     } catch (Throwable var20) {
                        var3.addSuppressed(var20);
                     }
                  } else {
                     connection.close();
                  }
               }

            }

            return value;
         } catch (SQLException var23) {
            BungeeMain.console("Ocorreu um erro ao tentar obter a data de uma skin (SQL) -> " + var23.getLocalizedMessage());
            var23.printStackTrace();
         } catch (SkinRequestException var24) {
            var24.printStackTrace();
         }

         return getSkinData("0171");
      }
   }

   public static boolean isOld(long timestamp) {
      return timestamp + TimeUnit.MINUTES.toMillis(1584L) <= System.currentTimeMillis();
   }

   public static void removePlayerSkin(String name) {
      name = name.toLowerCase();
      PLAYER_SKIN.remove(name);
      MySQLManager.deleteFromTable("playerSkin", "nick", name);
   }

   public static void removeSkinData(String name) {
      String name1 = name.toLowerCase();
      CACHE.remove(name);
      CommonsGeneral.runAsync(() -> {
         MySQLManager.deleteFromTable("skins", "nick", name1);
      });
   }

   public static void setPlayerSkin(String name, String skin) {
      name = name.toLowerCase();
      PLAYER_SKIN.put(name, skin);

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var3 = null;

         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM playerSkin where nick='" + name + "';");
            ResultSet result = preparedStatament.executeQuery();
            PreparedStatement set;
            if (result.next()) {
               result.close();
               preparedStatament.close();
               set = connection.prepareStatement("UPDATE playerSkin SET skin=? where nick='" + name + "'");
               set.setString(1, skin);
               set.executeUpdate();
               set.close();
               return;
            }

            result.close();
            preparedStatament.close();
            set = connection.prepareStatement("INSERT INTO playerSkin (nick, skin) VALUES (?, ?)");
            set.setString(1, name);
            set.setString(2, skin);
            set.executeUpdate();
            set.close();
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var16) {
                     var3.addSuppressed(var16);
                  }
               } else {
                  connection.close();
               }
            }

         }

      } catch (SQLException var19) {
         CommonsGeneral.error("Erro ao setar skin de um jogador no MySQL -> " + var19.getLocalizedMessage());
      }
   }

   public static void setSkinData(String name, Object textures) {
      String name1 = name.toLowerCase();

      String value;
      String signature;
      String timestamp;
      try {
         value = (String)ReflectionUtil.invokeMethod(textures, "getValue");
         signature = (String)ReflectionUtil.invokeMethod(textures, "getSignature");
         timestamp = String.valueOf(System.currentTimeMillis());
      } catch (Exception var22) {
         return;
      }

      CACHE.put(name1, new Cache(name1, new Skin(name1, value, signature), 3));

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var7 = null;

         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT * FROM skins where nick='" + name1 + "';");
            ResultSet result = preparedStatament.executeQuery();
            PreparedStatement set;
            if (result.next()) {
               result.close();
               preparedStatament.close();
               set = connection.prepareStatement("UPDATE skins SET lastUse=?, value=?, signature=?, timestamp=? where nick='" + name1 + "'");
               set.setString(1, "" + System.currentTimeMillis());
               set.setString(2, value);
               set.setString(3, signature);
               set.setString(4, timestamp);
               set.executeUpdate();
               set.close();
               return;
            }

            result.close();
            preparedStatament.close();
            set = connection.prepareStatement("INSERT INTO skins (nick, lastUse, value, signature, timestamp) VALUES (?, ?, ?, ?, ?)");
            set.setString(1, name1);
            set.setString(2, "" + System.currentTimeMillis());
            set.setString(3, value);
            set.setString(4, signature);
            set.setString(5, timestamp);
            set.executeUpdate();
            set.close();
         } catch (Throwable var23) {
            var7 = var23;
            throw var23;
         } finally {
            if (connection != null) {
               if (var7 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var21) {
                     var7.addSuppressed(var21);
                  }
               } else {
                  connection.close();
               }
            }

         }

      } catch (SQLException var25) {
         CommonsGeneral.console("Erro ao setar skin no MySQL -> " + var25.getLocalizedMessage());
      }
   }

   public void checkCache() {
      checkCache((Runnable)null);
   }

   static {
      try {
         property = Class.forName("com.mojang.authlib.properties.Property");
      } catch (Exception var5) {
         try {
            property = Class.forName("net.md_5.bungee.connection.LoginResult$Property");
         } catch (Exception var4) {
            try {
               property = Class.forName("net.minecraft.util.com.mojang.authlib.properties.Property");
            } catch (Exception var3) {
               System.out.println("[SkinsRestorer] Could not find a valid Property class! Plugin will not work properly");
            }
         }
      }

   }
}