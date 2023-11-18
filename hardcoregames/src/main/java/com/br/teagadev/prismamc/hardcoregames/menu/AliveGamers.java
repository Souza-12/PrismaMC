package com.br.teagadev.prismamc.hardcoregames.menu;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;

public class AliveGamers extends MenuInventory {
	   private static final int ITEMS_PER_PAGE = 28;
	   private static final int PREVIOUS_PAGE_SLOT = 27;
	   private static final int NEXT_PAGE_SLOT = 35;
	   private static final int CENTER = 31;
	   private static final int HEADS_PER_ROW = 7;

	   public AliveGamers() {
	      this(1);
	   }

	   public AliveGamers(int page) {
	      this(page, 1);
	   }

	   public AliveGamers(int page, int maxPages) {
	      super("Jogadores Vivos", 6);
	      List<MenuItem> itens = new ArrayList();
	      ItemBuilder itemBuilder = new ItemBuilder();
	      Iterator var5 = Bukkit.getOnlinePlayers().iterator();

	      while(var5.hasNext()) {
	         Player onlines = (Player)var5.next();
	         Gamer gamer = GamerManager.getGamer(onlines.getUniqueId());
	         if (gamer != null) {
	            if (gamer.isPlaying()) {
	               itens.add(new MenuItem(itemBuilder.type(Material.SKULL_ITEM).durability(3).name("§a" + onlines.getName()).skin(onlines.getName()).lore(new String[]{"§fKills: §7" + gamer.getKills(), "§fKit(s): §6" + gamer.getKits()}).build(), new AliveGamers.ClickHandler()));
	            }

	            gamer = null;
	         }
	      }

	      int pageStart = 0;
	      int pageEnd = 28;
	      if (page > 1) {
	         pageStart = (page - 1) * 28;
	         pageEnd = page * 28;
	      }

	      if (pageEnd > itens.size()) {
	         pageEnd = itens.size();
	      }

	      if (page == 1) {
	         this.setItem(27, itemBuilder.type(Material.AIR).build());
	      } else {
	         this.setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPágina Anterior").build(), (player, arg1, arg2, arg3, arg4) -> {
	            (new AliveGamers(page - 1)).open(player);
	         }), 27);
	      }

	      if (itens.size() / 28 + 1 <= page) {
	         this.setItem(35, itemBuilder.type(Material.AIR).build());
	      } else {
	         this.setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPróxima Página").build(), (player, arg1, arg2, arg3, arg4) -> {
	            (new AliveGamers(page + 1)).open(player);
	         }), 35);
	      }

	      int kitSlot = 10;

	      for(int i = pageStart; i < pageEnd; ++i) {
	         MenuItem item = (MenuItem)itens.get(i);
	         this.setItem(item, kitSlot);
	         if (kitSlot % 9 == 7) {
	            kitSlot += 3;
	         } else {
	            ++kitSlot;
	         }
	      }

	      if (itens.size() == 0) {
	         this.setItem(31, itemBuilder.type(Material.REDSTONE_BLOCK).name("§cNenhum jogador vivo.").build());
	      }

	      if (StructuresManager.getFeast().getLocation() != null) {
	         this.setItem(4, new MenuItem((new ItemBuilder()).type(Material.CAKE).name("§aFeast").build(), new AliveGamers.ClickHandler()));
	      }

	      itemBuilder = null;
	      itens.clear();
	      itens = null;
	   }

	   private static class ClickHandler implements MenuClickHandler {
	      private ClickHandler() {
	      }

	      public void onClick(Player player, Inventory inventory, ClickType clickType, ItemStack item, int slot) {
	         if (clickType == ClickType.LEFT) {
	            player.closeInventory();
	            if (item.getType() == Material.CAKE) {
	               player.sendMessage("§aVocê foi para o feast!");
	               player.teleport(StructuresManager.getFeast().getLocation().clone().add(0.5D, 3.0D, 0.5D));
	            } else {
	               String name = item.getItemMeta().getDisplayName().replace("§a", "");
	               Player target = Bukkit.getPlayer(name);
	               if (target != null) {
	                  player.teleport(target.getLocation().clone().add(0.0D, 1.2D, 0.0D));
	                  player.sendMessage("§aVocê se teleportou para o §f" + target.getName());
	                  target = null;
	               } else {
	                  player.sendMessage("§cJogador offline!");
	               }
	            }

	         }
	      }

	      // $FF: synthetic method
	      ClickHandler(Object x0) {
	         this();
	      }
	   }
	}