package com.br.teagadev.prismamc.lobby.common.hologram.types;

import com.br.teagadev.prismamc.commons.bukkit.api.hologram.types.SimpleHologram;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.common.hologram.HologramCommon;

import lombok.Getter;

public class LobbyPvPHologram extends HologramCommon {
	   private SimpleHologram kills;
	   private SimpleHologram maxKillStreak;

	   public void create() {
	      this.kills = new SimpleHologram("kills", "ARENA KILLS", DataCategory.KITPVP, DataType.PVP_KILLS, LobbyMain.getInstance());
	      this.maxKillStreak = new SimpleHologram("killstreak", "ARENA MAXSTREAKS", DataCategory.KITPVP, DataType.PVP_MAXKILLSTREAK, LobbyMain.getInstance());
	      this.kills.create();
	      this.maxKillStreak.create();
	      this.update();
	   }

	   public void update() {
	      this.kills.updateValues();
	      this.maxKillStreak.updateValues();
	   }

	   public SimpleHologram getKills() {
	      return this.kills;
	   }

	   public SimpleHologram getMaxKillStreak() {
	      return this.maxKillStreak;
	   }
	}