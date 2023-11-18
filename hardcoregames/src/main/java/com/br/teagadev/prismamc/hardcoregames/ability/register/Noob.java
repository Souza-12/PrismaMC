package com.br.teagadev.prismamc.hardcoregames.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class Noob extends Kit {
   public Noob() {
      this.initialize(this.getClass().getSimpleName());
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_AXE).name("§bMachado Noob").build()});
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_PICKAXE).name("§bPicareta Noob").build()});
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_HOE).name("§bFoice Noob").build()});
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.STONE_SWORD).name("§bEspada Noob").build()});
      this.setItens(new ItemStack[]{(new ItemBuilder()).material(Material.MUSHROOM_SOUP).amount(24).build()});
   }

   protected void clean(Player player) {
   }
}