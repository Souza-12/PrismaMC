package com.br.teagadev.prismamc.commons.common.connections.mysql;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.sql.SQLException;

public class MySQL {
   private HikariDataSource dataSource;
   private String host;
   private String porta;
   private String database;
   private String usuario;
   private String senha;

   public void openConnection() {
      CommonsGeneral.console("§eTentando estabelecer conexao com o MySQL...");
      if (this.dataSource != null) {
         throw new IllegalStateException("Hikari already initialized");
      } else {
         HikariConfig configuration = new HikariConfig();
         configuration.setJdbcUrl("jdbc:mysql://" + this.host + ":" + this.porta + "/" + this.database + "?characterEncoding=utf8&useSSL=false&useConfigs=maxPerformance");
         configuration.setUsername(this.usuario);
         configuration.setPassword(this.senha);
         configuration.setMaximumPoolSize(10);
         configuration.setMaxLifetime(3000000L);
         configuration.setLeakDetectionThreshold(50000000L);
         configuration.addDataSourceProperty("cachePrepStmts", "true");
         configuration.addDataSourceProperty("prepStmtCacheSize", "250");
         configuration.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");
         this.dataSource = new HikariDataSource(configuration);
      }
   }

   public void createTables() {
      String[] categorysList = new String[DataCategory.values().length];
      int id = 0;
      DataCategory[] var3 = DataCategory.values();
      int var4 = var3.length;

      for(int var5 = 0; var5 < var4; ++var5) {
         DataCategory categorys = var3[var5];
         categorysList[id] = categorys.buildTableQuery();
         ++id;
      }

      CommonsGeneral.runAsync(() -> {
         MySQLManager.executeUpdate(categorysList);
         MySQLManager.executeUpdate(new String[]{"CREATE TABLE IF NOT EXISTS global_whitelist(identify varchar(20), actived boolean, nicks text);", "CREATE TABLE IF NOT EXISTS accounts_to_delete(nick varchar(20), timestamp varchar(100));", "CREATE TABLE IF NOT EXISTS clans(nome varchar(20), data JSON);", "CREATE TABLE IF NOT EXISTS premium_map(nick varchar(20), uuid varchar(100), premium boolean);", "CREATE TABLE IF NOT EXISTS playerSkin(nick varchar(16), skin varchar(16));", "CREATE TABLE IF NOT EXISTS bans(nick varchar(20), address varchar(100), data JSON);", "CREATE TABLE IF NOT EXISTS mutes(nick varchar(20), data JSON);", "CREATE TABLE IF NOT EXISTS skins(nick varchar(16), lastUse varchar(100), value text, signature text, timestamp text);"});
      });
   }

   public void closeConnection() {
      try {
         if (this.getConnection() != null) {
            this.getConnection().close();
            this.dataSource.close();
            this.dataSource = null;
            CommonsGeneral.console("§aConexao com o MySQL encerrada.");
         }
      } catch (SQLException var2) {
         CommonsGeneral.error("closeConnection() : MySQL.Java -> " + var2.getLocalizedMessage());
      }

   }

   public boolean isConnected() {
      try {
         this.getConnection();
         return true;
      } catch (SQLException var2) {
         return false;
      }
   }

   public Connection getConnection() throws SQLException {
      if (this.dataSource == null) {
         this.openConnection();
      }

      return this.dataSource.getConnection();
   }

   public String getHost() {
      return this.host;
   }

   public String getPorta() {
      return this.porta;
   }

   public String getDatabase() {
      return this.database;
   }

   public String getUsuario() {
      return this.usuario;
   }

   public String getSenha() {
      return this.senha;
   }

   public void setHost(String host) {
      this.host = host;
   }

   public void setPorta(String porta) {
      this.porta = porta;
   }

   public void setDatabase(String database) {
      this.database = database;
   }

   public void setUsuario(String usuario) {
      this.usuario = usuario;
   }

   public void setSenha(String senha) {
      this.senha = senha;
   }
}