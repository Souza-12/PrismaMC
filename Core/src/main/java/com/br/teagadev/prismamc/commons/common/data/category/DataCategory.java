package com.br.teagadev.prismamc.commons.common.data.category;

import com.br.teagadev.prismamc.commons.common.data.type.DataType;

public enum DataCategory {
   ACCOUNT("accounts", new DataType[]{DataType.GROUP, DataType.GROUP_TIME, DataType.GROUP_CHANGED_BY, DataType.CLAN, DataType.MEDAL, DataType.TAG, DataType.PERMISSIONS, DataType.FAKE, DataType.DOUBLEXP, DataType.DOUBLEXP_TIME, DataType.DOUBLECOINS, DataType.DOUBLECOINS_TIME, DataType.XP, DataType.COINS, DataType.CASH, DataType.LAST_IP, DataType.FIRST_LOGGED_IN, DataType.LAST_LOGGED_IN, DataType.LAST_LOGGED_OUT}),
   KITPVP("kitpvp", new DataType[]{DataType.PVP_KILLS, DataType.PVP_DEATHS, DataType.PVP_KILLSTREAK, DataType.PVP_MAXKILLSTREAK, DataType.FPS_KILLS, DataType.FPS_DEATHS, DataType.FPS_KILLSTREAK, DataType.FPS_MAXKILLSTREAK, DataType.PVP_LAVACHALLENGE_FACIL, DataType.PVP_LAVACHALLENGE_MEDIO, DataType.PVP_LAVACHALLENGE_DIFICIL, DataType.PVP_LAVACHALLENGE_EXTREMO, DataType.PVP_LAVACHALLENGE_MORTES}),
   GLADIATOR("gladiator", new DataType[]{DataType.GLADIATOR_WINS, DataType.GLADIATOR_LOSES, DataType.GLADIATOR_WINSTREAK, DataType.GLADIATOR_MAXWINSTREAK, DataType.GLADIATOR_SLOTS}),
   HARDCORE_GAMES("hardcoregames", new DataType[]{DataType.HG_KILLS, DataType.HG_DEATHS, DataType.HG_WINS, DataType.HG_EVENT_WINS, DataType.HG_EVENT_KILLS, DataType.HG_EVENT_DEATHS, DataType.HG_DAILY_KIT, DataType.HG_DAILY_KIT_TIME}),
   PREFERENCES("preferences", new DataType[]{DataType.RECEIVE_PRIVATE_MESSAGES, DataType.RECEIVE_STAFFCHAT_MESSAGES, DataType.RECEIVE_REPORTS, DataType.PLAYERS_VISIBILITY, DataType.ANNOUNCEMENT_JOIN, DataType.CLAN_TAG_DISPLAY}),
   REGISTER("registros", new DataType[]{DataType.REGISTRO_SENHA, DataType.REGISTRO_DATA});

   private final String tableName;
   private final DataType[] dataTypes;

   private DataCategory(String tableName, DataType... dataTypes) {
      this.tableName = tableName;
      this.dataTypes = dataTypes;
   }

   public static DataCategory getCategoryByName(String name) {
      DataCategory finded = ACCOUNT;
      DataCategory[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         DataCategory datas = var2[var4];
         if (datas.getTableName().equalsIgnoreCase(name)) {
            finded = datas;
            break;
         }
      }

      return finded;
   }

   public boolean create() {
      return this != REGISTER;
   }

   public String buildTableQuery() {
      return "CREATE TABLE IF NOT EXISTS `" + this.getTableName() + "` (nick varchar(20), data JSON);";
   }

   public String getTableName() {
      return this.tableName;
   }

   public DataType[] getDataTypes() {
      return this.dataTypes;
   }
}