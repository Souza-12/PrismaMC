package com.br.teagadev.prismamc.lobby.common.hologram.types;

import com.br.teagadev.prismamc.commons.bukkit.api.hologram.types.SimpleHologram;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.common.hologram.HologramCommon;

import lombok.Getter;

public class LobbyHGHologram extends HologramCommon {
	   private SimpleHologram kills;
	   private SimpleHologram wins;
	   private SimpleHologram killsEvent;
	   private SimpleHologram winsEvent;

	   public void create() {
	      this.kills = new SimpleHologram("kills", "KILLS HG", DataCategory.HARDCORE_GAMES, DataType.HG_KILLS, LobbyMain.getInstance());
	      this.wins = new SimpleHologram("wins", "WINS HG", DataCategory.HARDCORE_GAMES, DataType.HG_WINS, LobbyMain.getInstance());
	      this.killsEvent = new SimpleHologram("killsEvent", "KILLS SET-HG", DataCategory.HARDCORE_GAMES, DataType.HG_EVENT_KILLS, LobbyMain.getInstance());
	      this.winsEvent = new SimpleHologram("winsEvent", "WINS SET-HG", DataCategory.HARDCORE_GAMES, DataType.HG_EVENT_WINS, LobbyMain.getInstance());
	      this.kills.create();
	      this.wins.create();
	      this.winsEvent.create();
	      this.killsEvent.create();
	      this.update();
	   }

	   public void update() {
	      this.kills.updateValues();
	      this.wins.updateValues();
	      this.killsEvent.updateValues();
	      this.winsEvent.updateValues();
	   }

	   public void recreate() {
	   }

	   public SimpleHologram getKills() {
	      return this.kills;
	   }

	   public SimpleHologram getWins() {
	      return this.wins;
	   }

	   public SimpleHologram getKillsEvent() {
	      return this.killsEvent;
	   }

	   public SimpleHologram getWinsEvent() {
	      return this.winsEvent;
	   }
	}