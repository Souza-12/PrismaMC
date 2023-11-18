package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class Grandpa extends Kit {
	   public Grandpa() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.STICK).name(this.getItemColor() + "Kit " + this.getName()).enchantment(Enchantment.KNOCKBACK, 3).build()});
	   }

	   protected void clean(Player player) {
	   }
	}