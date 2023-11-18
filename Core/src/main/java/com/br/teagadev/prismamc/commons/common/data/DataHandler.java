package com.br.teagadev.prismamc.commons.common.data;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.PluginInstance;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.client.Client;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

public class DataHandler {
   private final String nick;
   private final UUID uniqueId;
   private final Map<DataType, Data> datas;
   private final Map<DataCategory, Boolean> loadedCategories;

   public DataHandler(String nick, UUID uniqueId) {
      this.nick = nick;
      this.uniqueId = uniqueId;
      this.datas = new ConcurrentHashMap();
      this.loadedCategories = new ConcurrentHashMap();
      DataCategory[] var3 = DataCategory.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DataCategory dataCategory = var3[var5];
         this.loadedCategories.put(dataCategory, false);
      }

   }

   public void load(DataCategory... dataCategory) throws SQLException {
      for(int i = 0; i < dataCategory.length; ++i) {
         DataCategory current = dataCategory[i];
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var5 = null;

         try {
            PreparedStatement preparedStatement = connection.prepareStatement("SELECT * FROM `" + current.getTableName() + "` WHERE `nick`='" + this.getNick() + "';");
            ResultSet result = preparedStatement.executeQuery();
            if (result.next()) {
               this.loadFromJSONString(current, result.getString("data"));
            } else {
               if (current.create()) {
                  this.insertOnTable(current);
                  this.datas.put(DataType.FIRST_LOGGED_IN, new Data(System.currentTimeMillis()));
                  DataType[] var8 = current.getDataTypes();
                  int var9 = var8.length;

                  for(int var10 = 0; var10 < var9; ++var10) {
                     DataType dataType = var8[var10];
                     if (dataType == DataType.FIRST_LOGGED_IN) {
                        this.datas.put(dataType, new Data(System.currentTimeMillis()));
                     } else {
                        this.datas.put(dataType, new Data(dataType.getDefaultValue()));
                     }
                  }
               }

               this.getLoadedCategories().put(current, true);
            }

            preparedStatement.close();
            result.close();
         } catch (Throwable var19) {
            var5 = var19;
            throw var19;
         } finally {
            if (connection != null) {
               if (var5 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var18) {
                     var5.addSuppressed(var18);
                  }
               } else {
                  connection.close();
               }
            }

         }
      }

   }

   private void insertOnTable(DataCategory category) {
      CommonsGeneral.runAsync(() -> {
         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var3 = null;

            try {
               PreparedStatement insert = connection.prepareStatement(this.createInsertIntoStringQuery(category));
               insert.execute();
               insert.close();
               insert = null;
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
            CommonsGeneral.error("load() : insertOnTable() : DataHandler.java -> " + var15.getLocalizedMessage());
         }

      });
   }

   public void saveCategorys(DataCategory... dataCategorys) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var3 = null;

         try {
            for(int i = 0; i < dataCategorys.length; ++i) {
               DataCategory dataCategory = dataCategorys[i];

               try {
                  PreparedStatement save = connection.prepareStatement("UPDATE " + dataCategory.getTableName() + " SET data=? where nick='" + this.getNick() + "'");
                  save.setString(1, this.createJSON(dataCategory, false).toString());
                  save.execute();
                  save.close();
                  save = null;
               } catch (SQLException var16) {
                  CommonsGeneral.error("Ocorreu um erro ao tentar salvar a categoria '" + dataCategory.getTableName() + "' de " + this.getNick() + " -> " + var16.getLocalizedMessage());
               }
            }
         } catch (Throwable var17) {
            var3 = var17;
            throw var17;
         } finally {
            if (connection != null) {
               if (var3 != null) {
                  try {
                     connection.close();
                  } catch (Throwable var15) {
                     var3.addSuppressed(var15);
                  }
               } else {
                  connection.close();
               }
            }

         }
      } catch (SQLException var19) {
         CommonsGeneral.error("Ocorreu um erro ao tentar abrir um pool de conexao " + var19.getLocalizedMessage());
      }

      this.sendCategoryToBungeecord(dataCategorys);
   }

   public void saveCategory(DataCategory dataCategory) {
      try {
         Connection connection = CommonsGeneral.getMySQL().getConnection();
         Throwable var3 = null;

         try {
            PreparedStatement save = connection.prepareStatement("UPDATE " + dataCategory.getTableName() + " SET data=? where nick='" + this.getNick() + "'");
            save.setString(1, this.createJSON(dataCategory, false).toString());
            save.execute();
            save.close();
            save = null;
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
         CommonsGeneral.error("Ocorreu um erro ao tentar salvar a categoria '" + dataCategory.getTableName() + "' de " + this.getNick() + " -> " + var15.getLocalizedMessage());
      }

      this.sendCategoryToBungeecord(dataCategory);
   }

   public void sendCategoryToBungeecord(DataCategory... dataCategorys) {
      if (CommonsGeneral.getPluginInstance() == PluginInstance.BUKKIT) {
         CPacketCustomAction PACKET = (new CPacketCustomAction(this.nick, this.uniqueId)).type(PacketType.BUKKIT_SEND_UPDATED_DATA);
         int id = 1;

         for(int i = 0; i < dataCategorys.length; ++i) {
            DataCategory dataCategory = dataCategorys[i];
            PACKET.getJson().addProperty("dataCategory-" + id, this.buildJSON(dataCategory, true).toString());
            ++id;
         }

         Client.getInstance().getClientConnection().sendPacket(PACKET);
      }

   }

   public void loadFromJSON(DataCategory dataCategory, JsonObject json) {
      if (json != null) {
         Iterator var3 = json.entrySet().iterator();

         while(var3.hasNext()) {
            Entry<String, JsonElement> entry = (Entry)var3.next();
            DataType data = DataType.getDataTypeByField((String)entry.getKey());
            if (data != null) {
               if (!this.getDatas().containsKey(data)) {
                  this.datas.put(data, new Data(data.getDefaultValue()));
               }

               String classExpected = data.getClassExpected();
               if (data == DataType.PERMISSIONS) {
                  this.getData(data).setValue(StringUtility.formatStringToArrayWithoutSpace(((JsonElement)entry.getValue()).getAsString()));
               } else if (classExpected.equalsIgnoreCase("Boolean")) {
                  this.getData(data).setValue(((JsonElement)entry.getValue()).getAsBoolean());
               } else if (classExpected.equalsIgnoreCase("Int")) {
                  this.getData(data).setValue(((JsonElement)entry.getValue()).getAsInt());
               } else if (classExpected.equalsIgnoreCase("Long")) {
                  this.getData(data).setValue(((JsonElement)entry.getValue()).getAsLong());
               } else {
                  this.getData(data).setValue(((JsonElement)entry.getValue()).getAsString());
               }
            }
         }

         this.getLoadedCategories().put(dataCategory, true);
         DataType[] var7 = dataCategory.getDataTypes();
         int var8 = var7.length;

         for(int var9 = 0; var9 < var8; ++var9) {
            DataType data = var7[var9];
            if (!this.getDatas().containsKey(data)) {
               this.datas.put(data, new Data(data.getDefaultValue()));
            }
         }

      }
   }

   public void loadFromJSONString(DataCategory dataCategory, String jsonString) {
      if (jsonString != null) {
         if (!jsonString.isEmpty()) {
            this.loadFromJSON(dataCategory, CommonsConst.PARSER.parse(jsonString).getAsJsonObject());
         }
      }
   }

   public JsonObject buildJSON(DataCategory dataCategory, boolean checkIfIsNotLoadedAndLoad) {
      JsonObject json = this.createJSON(dataCategory, checkIfIsNotLoadedAndLoad);
      json.addProperty("dataCategory-name", dataCategory.getTableName());
      return json;
   }

   public JsonObject createJSON(DataCategory dataCategory, boolean checkIfIsNotLoadedAndLoad) {
      JsonObject json = new JsonObject();
      boolean loaded = this.isCategoryLoaded(dataCategory);
      if (checkIfIsNotLoadedAndLoad && !loaded) {
         try {
            this.load(dataCategory);
         } catch (SQLException var9) {
         }
      }

      DataType[] var5 = dataCategory.getDataTypes();
      int var6 = var5.length;

      for(int var7 = 0; var7 < var6; ++var7) {
         DataType dataType = var5[var7];
         if (dataType == DataType.PERMISSIONS) {
            json.addProperty(dataType.getField(), loaded ? StringUtility.formatStringToArrayWithoutSpace(this.getData(dataType).getList()) : "");
         } else if (dataType.getClassExpected().equalsIgnoreCase("Boolean")) {
            json.addProperty(dataType.getField(), loaded ? this.getData(dataType).getBoolean() : false);
         } else if (dataType.getClassExpected().equalsIgnoreCase("Int")) {
            json.addProperty(dataType.getField(), loaded ? this.getData(dataType).getInt() : 0);
         } else if (dataType.getClassExpected().equalsIgnoreCase("Long")) {
            json.addProperty(dataType.getField(), loaded ? this.getData(dataType).getLong() : 0L);
         } else {
            json.addProperty(dataType.getField(), loaded ? this.getData(dataType).getString() : "" + dataType.getDefaultValue());
         }
      }

      return json;
   }

   public void loadDefault(DataCategory dataCategory) {
      DataType[] var2 = dataCategory.getDataTypes();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DataType dataType = var2[var4];
         if (!this.isCategoryLoaded(dataCategory)) {
            this.datas.put(dataType, new Data(dataType.getDefaultValue()));
         }
      }

   }

   public void reset() {
      DataCategory[] var1 = DataCategory.values();
      int var2 = var1.length;

      for(int var3 = 0; var3 < var2; ++var3) {
         DataCategory dataCategory = var1[var3];
         if (this.isCategoryLoaded(dataCategory)) {
            DataType[] var5 = dataCategory.getDataTypes();
            int var6 = var5.length;

            for(int var7 = 0; var7 < var6; ++var7) {
               DataType data = var5[var7];
               this.datas.put(data, new Data(data.getDefaultValue()));
            }

            this.getLoadedCategories().put(dataCategory, false);
         }
      }

   }

   public String getIntFormatted(DataType dataType) {
      return StringUtility.formatValue(this.getInt(dataType));
   }

   public List<DataCategory> getListDataCategorysLoadeds() {
      List<DataCategory> list = new ArrayList();
      DataCategory[] var2 = DataCategory.values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DataCategory datas = var2[var4];
         if (this.isCategoryLoaded(datas)) {
            list.add(datas);
         }
      }

      return list;
   }

   public void remove(DataType dataType) {
      this.remove(dataType, 1);
   }

   public void remove(DataType dataType, int value) {
      this.getData(dataType).remove(value);
   }

   public void add(DataType dataType) {
      this.add(dataType, 1);
   }

   public void add(DataType dataType, int value) {
      this.getData(dataType).add(value);
   }

   public void set(DataType dataType, Object value) {
      this.getData(dataType).setValue(value);
   }

   public boolean isCategoryLoaded(DataCategory category) {
      return (Boolean)this.loadedCategories.get(category);
   }

   public Data getData(DataType dataType) {
      return (Data)this.datas.get(dataType);
   }

   public int getInt(DataType dataType) {
      return ((Data)this.datas.get(dataType)).getInt();
   }

   public String getString(DataType dataType) {
      return ((Data)this.datas.get(dataType)).getString();
   }

   public Boolean getBoolean(DataType dataType) {
      return ((Data)this.datas.get(dataType)).getBoolean();
   }

   public Long getLong(DataType dataType) {
      return ((Data)this.datas.get(dataType)).getLong();
   }

   public String createInsertIntoStringQuery(DataCategory category) {
      return "INSERT INTO `" + category.getTableName() + "` (`nick`, `data`) VALUES ('" + this.getNick() + "', '" + this.createJSON(category, false).toString() + "');";
   }

   public void sendCategoryToBungeecord(List<DataCategory> list) {
      CPacketCustomAction PACKET = (new CPacketCustomAction(this.nick, this.uniqueId)).type(PacketType.BUKKIT_SEND_UPDATED_DATA);

      for(int i = 0; i < list.size(); ++i) {
         PACKET.getJson().addProperty("dataCategory-" + (i + 1), this.buildJSON((DataCategory)list.get(i), true).toString());
      }

      Client.getInstance().getClientConnection().sendPacket(PACKET);
   }

   public String getNick() {
      return this.nick;
   }

   public UUID getUniqueId() {
      return this.uniqueId;
   }

   public Map<DataType, Data> getDatas() {
      return this.datas;
   }

   public Map<DataCategory, Boolean> getLoadedCategories() {
      return this.loadedCategories;
   }
}