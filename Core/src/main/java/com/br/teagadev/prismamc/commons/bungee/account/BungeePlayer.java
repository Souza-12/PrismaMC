package com.br.teagadev.prismamc.commons.bungee.account;

import com.br.teagadev.prismamc.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeePlayer extends GamingProfile {
   private boolean inStaffChat = false;
   private boolean inClanChat = false;
   private long lastReport;
   private long lastLoggedOut;
   private String loginOnServer;

   public BungeePlayer(String nick, String address, UUID uniqueId) {
      super(nick, address, uniqueId);
      this.setLastReport(0L);
      this.setLastLoggedOut(0L);
      this.setLoginOnServer("Lobby");
   }

   public void handleLogin(ProxiedPlayer proxiedPlayer) {
      BungeePlayerPermissions.injectPermissions(proxiedPlayer, this.getGroup().getName());
      if (!this.getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
         ClanManager.getClan(this.getString(DataType.CLAN)).addOnline(proxiedPlayer);
      }

      try {
         this.getPunishmentHistoric().loadMutes();
      } catch (SQLException var3) {
         var3.printStackTrace();
      }

   }

   public void handleQuit() {
      this.set(DataType.LAST_LOGGED_OUT, System.currentTimeMillis());
      this.setLastLoggedOut(System.currentTimeMillis());
   }

   public boolean isValidSession() {
      return System.currentTimeMillis() < this.getLong(DataType.LAST_LOGGED_IN) + TimeUnit.HOURS.toMillis(4L);
   }

   public boolean isAvailableToJoin() {
      return System.currentTimeMillis() > this.getLastLoggedOut() + TimeUnit.SECONDS.toMillis(4L);
   }

   public boolean podeReportar() {
      return this.getLastReport() + TimeUnit.SECONDS.toMillis(30L) < System.currentTimeMillis();
   }

   public boolean isValidPlayer() {
      return this.getPlayer() != null && this.getPlayer().getServer() != null;
   }

   public ProxiedPlayer getPlayer() {
      return ProxyServer.getInstance().getPlayer(this.getUniqueId());
   }

   public boolean isInStaffChat() {
      return this.inStaffChat;
   }

   public boolean isInClanChat() {
      return this.inClanChat;
   }

   public long getLastReport() {
      return this.lastReport;
   }

   public long getLastLoggedOut() {
      return this.lastLoggedOut;
   }

   public String getLoginOnServer() {
      return this.loginOnServer;
   }

   public void setInStaffChat(boolean inStaffChat) {
      this.inStaffChat = inStaffChat;
   }

   public void setInClanChat(boolean inClanChat) {
      this.inClanChat = inClanChat;
   }

   public void setLastReport(long lastReport) {
      this.lastReport = lastReport;
   }

   public void setLastLoggedOut(long lastLoggedOut) {
      this.lastLoggedOut = lastLoggedOut;
   }

   public void setLoginOnServer(String loginOnServer) {
      this.loginOnServer = loginOnServer;
   }
}