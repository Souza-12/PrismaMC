package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.entity.Player;

public class Surprise extends Kit {
   public Surprise() {
      this.initialize(this.getClass().getSimpleName());
   }

   protected void clean(Player player) {
   }
}