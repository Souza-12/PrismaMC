package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.entity.Player;

public class PvP extends Kit {
	   public PvP() {
	      this.initialize(this.getClass().getSimpleName());
	   }

	   protected void clean(Player player) {
	   }
	}