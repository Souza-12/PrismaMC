package com.br.teagadev.prismamc.hardcoregames.manager.timer;

import org.bukkit.Bukkit;

import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameTimerEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;

import lombok.Getter;
import lombok.Setter;

import java.util.concurrent.atomic.AtomicInteger;

public class TimerManager {
	   private AtomicInteger time = new AtomicInteger(300);
	   private String lastFormatted = "";
	   private int lastAlive = 0;
	   private TimerType timerType;

	   public TimerManager() {
	      this.timerType = TimerType.COUNTDOWN;
	   }

	   public void onSecond() {
	      switch(this.timerType) {
	      case COUNTDOWN:
	         this.time.getAndDecrement();
	         break;
	      case COUNT_UP:
	         this.time.getAndIncrement();
	      }

	      GameTimerEvent event = new GameTimerEvent(this.getTime().get());
	      Bukkit.getServer().getPluginManager().callEvent(event);
	      if (event.isChangedTime()) {
	         this.time.set(event.getTime());
	         this.lastFormatted = DateUtils.formatSecondsScore(this.getTime().get());
	         Bukkit.getOnlinePlayers().forEach((onlines) -> {
	            HardcoreGamesScoreboard.getScoreBoardCommon().updateTime(onlines);
	         });
	      }

	      this.lastFormatted = DateUtils.formatSecondsScore(this.getTime().get());
	      this.lastAlive = GamerManager.getAliveGamers().size();
	   }

	   public void updateAlive() {
	      this.lastAlive = GamerManager.getAliveGamers().size();
	   }

	   public void updateTime(int newTime) {
	      this.time.set(newTime);
	      this.lastFormatted = DateUtils.formatSecondsScore(this.getTime().get());
	   }

	   public String getLastFormatted() {
	      return this.lastFormatted;
	   }

	   public int getLastAlive() {
	      return this.lastAlive;
	   }

	   public AtomicInteger getTime() {
	      return this.time;
	   }

	   public TimerType getTimerType() {
	      return this.timerType;
	   }

	   public void setTime(AtomicInteger time) {
	      this.time = time;
	   }

	   public void setLastFormatted(String lastFormatted) {
	      this.lastFormatted = lastFormatted;
	   }

	   public void setLastAlive(int lastAlive) {
	      this.lastAlive = lastAlive;
	   }

	   public void setTimerType(TimerType timerType) {
	      this.timerType = timerType;
	   }
	}