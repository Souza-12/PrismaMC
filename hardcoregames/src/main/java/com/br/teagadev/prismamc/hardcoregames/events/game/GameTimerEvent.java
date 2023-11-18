package com.br.teagadev.prismamc.hardcoregames.events.game;

import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class GameTimerEvent extends Event {
   public static final HandlerList handlerList = new HandlerList();
   private int time;
   private boolean changedTime = false;

   public void setTime(int time) {
      this.time = time;
      this.changedTime = true;
   }

   public GameTimerEvent(int time) {
      this.time = time;
   }

   public HandlerList getHandlers() {
      return handlerList;
   }

   public void checkMessage() {
      if (this.time >= 10 & this.time % 60 == 0) {
         this.handleNotify(Sound.NOTE_PLING, this.getMensagem(this.time));
      } else if (this.time == 30 || this.time == 15 || this.time == 10 || this.time <= 5) {
         this.handleNotify(Sound.NOTE_PLING, this.getMensagem(this.time));
      }

   }

   private String getMensagem(int tempo) {
      if (HardcoreGamesMain.getGameManager().isPreGame()) {
         return "§eA partida irá iniciar em §c%tempo%.".replace("%tempo%", DateUtils.formatSecondsScore(tempo));
      } else {
         return HardcoreGamesMain.getGameManager().isInvencibilidade() ? "§eA invencibilidade irá acabar em §c%tempo%.".replace("%tempo%", DateUtils.formatSecondsScore(tempo)) : "";
      }
   }

   private void handleNotify(Sound sound, String message) {
      Iterator var3 = Bukkit.getOnlinePlayers().iterator();

      while(var3.hasNext()) {
         Player player = (Player)var3.next();
         player.playSound(player.getLocation(), sound, 1.0F, 1.0F);
      }

      Bukkit.broadcastMessage(message);
   }

   public static HandlerList getHandlerList() {
      return handlerList;
   }

   public int getTime() {
      return this.time;
   }

   public boolean isChangedTime() {
      return this.changedTime;
   }
}