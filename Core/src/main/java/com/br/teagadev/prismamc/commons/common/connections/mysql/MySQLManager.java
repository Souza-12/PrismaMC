package com.br.teagadev.prismamc.commons.common.connections.mysql;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class MySQLManager {
   public static int getPlayerPositionRanking(String name) {
      return getPlayerPosition("accounts", "xp", "nick", name);
   }

   public static int getPlayerPosition(String table, String field, String where, String name) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var5 = null;

         int var9;
         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE data>'$." + field + "' > (SELECT data>'$." + field + "' from " + table + " WHERE " + where + "='" + name + "')");
            ResultSet result = preparedStatament.executeQuery();
            if (!result.next()) {
               result.close();
               preparedStatament.close();
               byte var23 = 0;
               return var23;
            }

            int pos = Integer.parseInt(result.getString("COUNT(*)")) + 1;
            result.close();
            preparedStatament.close();
            var9 = pos;
         } catch (Throwable var20) {
            var5 = var20;
            throw var20;
         } finally {
            if (connection != null) {
               if (var5 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var19) {
                     var5.addSuppressed(var19);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return var9;
      } catch (SQLException var22) {
         CommonsGeneral.console("Ocorreu um erro ao tentar obter uma posição. -> " + var22.getLocalizedMessage());
         return 0;
      }
   }

   public static int getPlayerPositionByColumn(String table, String field, String where, String name) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var5 = null;

         byte var8;
         try {
            PreparedStatement preparedStatament = connection.prepareStatement("SELECT COUNT(*) FROM " + table + " WHERE " + field + " > (SELECT " + field + " from " + table + " WHERE " + where + "='" + name + "')");
            ResultSet result = preparedStatament.executeQuery();
            if (result.next()) {
               int pos = Integer.parseInt(result.getString("COUNT(*)")) + 1;
               result.close();
               preparedStatament.close();
               int var9 = pos;
               return var9;
            }

            result.close();
            preparedStatament.close();
            var8 = 0;
         } catch (Throwable var20) {
            var5 = var20;
            throw var20;
         } finally {
            if (connection != null) {
               if (var5 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var19) {
                     var5.addSuppressed(var19);
                  }
               } else {
                  connection.close();
               }
            }

         }

         return var8;
      } catch (SQLException var22) {
         CommonsGeneral.console("Ocorreu um erro ao tentar obter uma posiçao. -> " + var22.getLocalizedMessage());
         return 0;
      }
   }

   public static String getStringFromJSON(String table, String where, String whereValue, String field) {
      String string = "N/A";

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var6 = null;

         try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT data->'$." + field + "' FROM " + table + " WHERE " + where + "='" + whereValue + "'");
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
               string = result.getString("data->'$." + field + "'").replace("\"", "");
            }

            preparedStatement.close();
            result.close();
         } catch (Throwable var17) {
            var6 = var17;
            throw var17;
         } finally {
            if (connection != null) {
               if (var6 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var16) {
                     var6.addSuppressed(var16);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var19) {
         CommonsGeneral.error("getString() : MySQLManager.Java -> " + var19.getLocalizedMessage());
      }

      return string;
   }

   public static String getString(String table, String where, String who, String toGet) {
      String string = "N/A";

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var6 = null;

         try {
            PreparedStatement statement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`=?");
            statement.setString(1, who);
            ResultSet result = statement.executeQuery();
            if (result.next()) {
               string = result.getString(toGet);
            }

            statement.close();
            result.close();
         } catch (Throwable var17) {
            var6 = var17;
            throw var17;
         } finally {
            if (connection != null) {
               if (var6 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var16) {
                     var6.addSuppressed(var16);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var19) {
         CommonsGeneral.error("getString() : MySQLManager.Java -> " + var19.getLocalizedMessage());
      }

      return string;
   }

   public static void deleteFromTable(String table, String where, String who) {
      containsAndUpdate(table, where, who, "DELETE FROM " + table + " WHERE " + where + "='" + who + "';");
   }

   public static boolean contains(String table, String where, String who) {
      boolean contains = false;

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var5 = null;

         try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + who + "';");
            ResultSet result = preparedStatement.executeQuery();
            contains = result.next();
            preparedStatement.close();
            result.close();
         } catch (Throwable var16) {
            var5 = var16;
            throw var16;
         } finally {
            if (connection != null) {
               if (var5 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var15) {
                     var5.addSuppressed(var15);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var18) {
         CommonsGeneral.error("contains() : MySQLManager.Java -> " + var18.getLocalizedMessage());
      }

      return contains;
   }

   public static boolean updateIfNotExists(String table, String where, String who, String update) {
      boolean updated = false;

      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var6 = null;

         try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`='" + who + "';");
            ResultSet result = preparedStatement.executeQuery();
            if (!result.next()) {
               PreparedStatement updateStatement = connection.prepareStatement(update);
               updateStatement.executeUpdate();
               updateStatement.close();
               updated = true;
            }

            result.close();
            preparedStatement.close();
         } catch (Throwable var18) {
            var6 = var18;
            throw var18;
         } finally {
            if (connection != null) {
               if (var6 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var17) {
                     var6.addSuppressed(var17);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var20) {
         CommonsGeneral.error("updateIfNotExists() : MySQLManager.Java -> " + var20.getLocalizedMessage());
      }

      return updated;
   }

   public static void containsAndUpdate(String table, String where, String who, String update) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var5 = null;

         try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + table + "` WHERE `" + where + "`=?");
            preparedStatement.setString(1, who);
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
               PreparedStatement updateStatement = connection.prepareStatement(update);
               updateStatement.executeUpdate();
               updateStatement.close();
            }

            result.close();
            preparedStatement.close();
         } catch (Throwable var17) {
            var5 = var17;
            throw var17;
         } finally {
            if (connection != null) {
               if (var5 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var16) {
                     var5.addSuppressed(var16);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var19) {
         CommonsGeneral.error("containsAndUpdate() : MySQLManager.Java -> " + var19.getLocalizedMessage());
      }

   }

   public static void updateValue(DataCategory dataCategory, DataType dataType, String value, String nick) {
      executeUpdate("UPDATE " + dataCategory.getTableName() + " SET " + dataType.getField() + "='" + value + "' where nick='" + nick + "';");
   }

   public static void updateStats(String table, String stats, String where, String who, String value) {
      executeUpdate("UPDATE " + table + " SET " + stats + "='" + value + "' where '" + where + "'='" + who + "';");
   }

   public static void updateStats(String table, String stats, String nick, String value) {
      executeUpdate("UPDATE " + table + " SET " + stats + "='" + value + "' where nick='" + nick + "';");
   }

   public static void executeUpdate(String... commands) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var2 = null;

         try {
            String[] var3 = commands;
            int var4 = commands.length;

            for(int var5 = 0; var5 < var4; ++var5) {
               String command = var3[var5];

               try {
                  PreparedStatement preparedStatement = connection.prepareStatement(command);
                  preparedStatement.executeUpdate();
                  preparedStatement.close();
               } catch (SQLException var17) {
                  CommonsGeneral.error("executeUpdate() : onHandleCommand : MySQLManager.Java -> " + var17.getLocalizedMessage());
               }
            }
         } catch (Throwable var18) {
            var2 = var18;
            throw var18;
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
      } catch (SQLException var20) {
         CommonsGeneral.error("executeUpdate() : MySQLManager.Java -> " + var20.getLocalizedMessage());
      }

   }
}