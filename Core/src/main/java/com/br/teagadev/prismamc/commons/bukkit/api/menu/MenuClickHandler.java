package com.br.teagadev.prismamc.commons.bukkit.api.menu;

import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public interface MenuClickHandler {

    void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot);
}