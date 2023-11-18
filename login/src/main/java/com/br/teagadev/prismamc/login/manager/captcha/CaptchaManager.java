package com.br.teagadev.prismamc.login.manager.captcha;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.CommonsConst;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;

public class CaptchaManager {
	   private static final ItemStack olhoDoFim;
	   private static final ItemStack enderPearl;

	   public static void createCaptcha(Player player) {
	      if (player.isOnline()) {
	         Inventory inventory = player.getServer().createInventory((InventoryHolder)null, 27, "Sistema de captcha");
	         int randomSlot = CommonsConst.RANDOM.nextInt(26);
	         inventory.setItem(randomSlot, olhoDoFim);

	         for(int i = 0; i < 27; ++i) {
	            if (i != randomSlot) {
	               inventory.setItem(i, enderPearl);
	            }
	         }

	         player.openInventory(inventory);
	      }
	   }

	   static {
	      olhoDoFim = (new ItemBuilder()).type(Material.SKULL_ITEM).skinURL("http://textures.minecraft.net/texture/57c57ecc6f34fc34cd3524ce0b7c1dd1c405f1310ea25425e72c8a502e99ad52").durability(3).name("§aClique para escolher este").build();
	      enderPearl = (new ItemBuilder()).type(Material.SKULL_ITEM).skinURL("http://textures.minecraft.net/texture/c9f28bf9d149443583f9c1cbc0f17d8f186648336d7d3688ed471cfdf8837002").durability(3).name("§7Ops! Escolha outro está é a errada.").build();
	   }
	}