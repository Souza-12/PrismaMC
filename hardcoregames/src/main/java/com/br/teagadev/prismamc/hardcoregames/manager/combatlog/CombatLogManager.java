package com.br.teagadev.prismamc.hardcoregames.manager.combatlog;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

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

	   public static Player getLastHit(Player p) {
	      Player finded = null;
	      CombatLogManager.CombatLog log = getCombatLog(p);
	      if (log.isFighting()) {
	         Player combatLogger = log.getCombatLogged();
	         if (combatLogger != null) {
	            if (combatLogger.isOnline()) {
	               removeCombatLog(p);
	               finded = combatLogger;
	            }

	            combatLogger = null;
	         }
	      }

	      log = null;
	      return finded;
	   }

	   public static void removeCombatLog(Player player) {
	      HardcoreGamesMain plugin = HardcoreGamesMain.getInstance();
	      if (player.hasMetadata("combatlog.player")) {
	         player.removeMetadata("combatlog.player", plugin);
	      }

	      if (player.hasMetadata("combatlog.time")) {
	         player.removeMetadata("combatlog.time", plugin);
	      }

	   }

	   private static void setCombatLog(Player player1, Player player2) {
	      HardcoreGamesMain plugin = HardcoreGamesMain.getInstance();
	      removeCombatLog(player1);
	      player1.setMetadata("combatlog.player", new FixedMetadataValue(plugin, player2.getName()));
	      player1.setMetadata("combatlog.time", new FixedMetadataValue(plugin, System.currentTimeMillis()));
	   }
	   
	   public static CombatLogManager.CombatLog getCombatLog(Player player) {
		      String playerName = "";
		      long time = 0L;
		      if (player.hasMetadata("combatlog.player")) {
		    	  playerName = player.getMetadata(COMBATLOG_PLAYER).get(0).asString();
		      }

		      if (player.hasMetadata("combatlog.time")) {
		    	  time = player.getMetadata(COMBATLOG_EXPIRE).get(0).asLong();
		      }

		      Player combatLogged = Bukkit.getPlayer(playerName);
		      return new CombatLogManager.CombatLog(combatLogged, time);
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
	            } else if (this.getTime() != other.getTime()) {
	               return false;
	            } else {
	               Object this$combatLogged = this.getCombatLogged();
	               Object other$combatLogged = other.getCombatLogged();
	               if (this$combatLogged == null) {
	                  if (other$combatLogged != null) {
	                     return false;
	                  }
	               } else if (!this$combatLogged.equals(other$combatLogged)) {
	                  return false;
	               }

	               return true;
	            }
	         }
	      }

	      protected boolean canEqual(Object other) {
	         return other instanceof CombatLogManager.CombatLog;
	      }

	      public int hashCode() {
	          boolean PRIME = true;
	          int result = 1;
	          long $time = this.getTime();
	          int result1 = result * 59 + (int)($time >>> 32 ^ $time);
	          Object $combatLogged = this.getCombatLogged();
	          result1 = result1 * 59 + ($combatLogged == null ? 43 : $combatLogged.hashCode());
	          return result1;
	       }

	      public String toString() {
	         return "CombatLogManager.CombatLog(combatLogged=" + this.getCombatLogged() + ", time=" + this.getTime() + ")";
	      }

	      public CombatLog(Player combatLogged, long time) {
	         this.combatLogged = combatLogged;
	         this.time = time;
	      }
	   }
	}