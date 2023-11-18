package com.br.teagadev.prismamc.lobby.common.inventory.types;

import org.bukkit.Material;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.lobby.common.inventory.InventoryCommon;

public class GamesInventory extends MenuInventory {
	   public GamesInventory() {
	      super("Selecione um modo", 3);
	      this.update();
	   }

	   public void update() {
	      ItemBuilder itemBuilder = new ItemBuilder();
	      ServerType serverType = BukkitMain.getServerType();
	      this.setItem(10, itemBuilder.type(Material.MUSHROOM_SOUP).name("§aHG").lore(new String[]{"§7" + CommonsGeneral.getServersManager().getAmountOnlineHardcoreGames(serverType != ServerType.LOBBY_HARDCOREGAMES) + " §7jogando agora!"}).build(), InventoryCommon.getDefaultClickHandler());
	      this.setItem(11, itemBuilder.type(Material.IRON_CHESTPLATE).name("§aPvP").lore(new String[]{"§7" + CommonsGeneral.getServersManager().getAmountOnlinePvP(serverType != ServerType.LOBBY_PVP) + " §7jogando agora!"}).build(), InventoryCommon.getDefaultClickHandler());
	      this.setItem(12, itemBuilder.type(Material.IRON_FENCE).name("§aGladiador").lore(new String[]{"§7" + CommonsGeneral.getServersManager().getAmountOnlineDuels(serverType != ServerType.LOBBY_DUELS) + " §7jogando agora!"}).build(), InventoryCommon.getDefaultClickHandler());
	   }
	}