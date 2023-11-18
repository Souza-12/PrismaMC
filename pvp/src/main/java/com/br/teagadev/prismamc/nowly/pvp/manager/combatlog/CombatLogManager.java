package com.br.teagadev.prismamc.nowly.pvp.manager.combatlog;

import java.beans.ConstructorProperties;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

import com.br.teagadev.prismamc.nowly.pvp.PvPMain;

import lombok.Data;
import lombok.RequiredArgsConstructor;

public class CombatLogManager {
	   private static final String COMBATLOG_PLAYER = "combatlog.player";
	   private static final String COMBATLOG_EXPIRE = "combatlog.time";
	   private static final long COMBATLOG_TIME = 5000L;

	   public static void newCombatLog(Player damager, Player damaged) {
	      setCombatLog(damager, damaged);
	      setCombatLog(damaged, damager);
	   }

	   public static Player getLastHit(Player player) {
	      Player finded = null;
	      CombatLogManager.CombatLog log = getCombatLog(player);
	      if (log.isFighting()) {
	         Player combatLogger = log.getCombatLogged();
	         if (combatLogger != null && combatLogger.isOnline()) {
	            finded = combatLogger;
	         }
	      }

	      return finded;
	   }

	   public static void removeCombatLog(Player player) {
	      PvPMain plugin = PvPMain.getInstance();
	      if (player.hasMetadata("combatlog.player")) {
	         player.removeMetadata("combatlog.player", plugin);
	      }

	      if (player.hasMetadata("combatlog.time")) {
	         player.removeMetadata("combatlog.time", plugin);
	      }

	   }

	   public static void setCombatLog(Player player1, Player player2) {
	      PvPMain plugin = PvPMain.getInstance();
	      removeCombatLog(player1);
	      player1.removeMetadata("combatlog.player", plugin);
	      player1.removeMetadata("combatlog.time", plugin);
	      player1.setMetadata("combatlog.player", new FixedMetadataValue(plugin, player2.getName()));
	      player1.setMetadata("combatlog.time", new FixedMetadataValue(plugin, System.currentTimeMillis()));
	   }

	   public static CombatLogManager.CombatLog getCombatLog(Player player) {
	      String playerName = "";
	      long time = 0L;
	      if (player.hasMetadata("combatlog.player")) {
	         playerName = ((MetadataValue)player.getMetadata("combatlog.player").get(0)).asString();
	      }

	      if (player.hasMetadata("combatlog.time")) {
	         time = ((MetadataValue)player.getMetadata("combatlog.time").get(0)).asLong();
	      }

	      return new CombatLogManager.CombatLog(Bukkit.getPlayer(playerName), time);
	   }

	   public static class CombatLog {
	      private final Player combatLogged;
	      private final long time;

	      public boolean isFighting() {
	         return System.currentTimeMillis() < this.time + 5000L;
	      }

	      public Player getCombatLogged() {
	         return this.combatLogged;
	      }

	      public long getTime() {
	         return this.time;
	      }

	      public boolean equals(Object o) {
	         if (o == this) {
	            return true;
	         } else if (!(o instanceof CombatLogManager.CombatLog)) {
	            return false;
	         } else {
	            CombatLogManager.CombatLog other = (CombatLogManager.CombatLog)o;
	            if (!other.canEqual(this)) {
	               return false;
	            } else {
	               Object this$combatLogged = this.getCombatLogged();
	               Object other$combatLogged = other.getCombatLogged();
	               if (this$combatLogged == null) {
	                  if (other$combatLogged == null) {
	                     return this.getTime() == other.getTime();
	                  }
	               } else if (this$combatLogged.equals(other$combatLogged)) {
	                  return this.getTime() == other.getTime();
	               }

	               return false;
	            }
	         }
	      }

	      protected boolean canEqual(Object other) {
	         return other instanceof CombatLogManager.CombatLog;
	      }

	      public int hashCode() {
	         boolean PRIME = true;
	         int result = 1;
	         Object $combatLogged = this.getCombatLogged();
	         int result1 = result * 59 + ($combatLogged == null ? 43 : $combatLogged.hashCode());
	         long $time = this.getTime();
	         result = result * 59 + (int)($time >>> 32 ^ $time);
	         return result;
	      }

	      public String toString() {
	         return "CombatLogManager.CombatLog(combatLogged=" + this.getCombatLogged() + ", time=" + this.getTime() + ")";
	      }

	      @ConstructorProperties({"combatLogged", "time"})
	      public CombatLog(Player combatLogged, long time) {
	         this.combatLogged = combatLogged;
	         this.time = time;
	      }
	   }
	}