package com.br.teagadev.prismamc.hardcoregames.menu;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.prismamc.hardcoregames.StringUtils;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import com.br.teagadev.prismamc.hardcoregames.menu.enums.InventoryMode;

public class KitSelector extends MenuInventory {
	   private static final int ITEMS_PER_PAGE = 28;
	   private static final int PREVIOUS_PAGE_SLOT = 27;
	   private static final int NEXT_PAGE_SLOT = 35;
	   private static final int CENTER = 31;
	   private static final int KITS_PER_ROW = 7;

	   public KitSelector(Player player) {
	      this(player, 1, InventoryMode.KIT_PRIMARIO);
	   }

	   public KitSelector(Player player, InventoryMode inventoryMode) {
	      this(player, 1, inventoryMode);
	   }

	   public KitSelector(Player player, int page, InventoryMode inventoryMode) {
	      this(player, page, 1, inventoryMode);
	   }

	   public KitSelector(Player player1, int page, int maxPages, InventoryMode inventoryMode) {
	      super(inventoryMode.getInventoryName(), 6);
	      List<Kit> kitList = new ArrayList(KitManager.getKits().values());
	      kitList.sort(Comparator.comparing(Kit::getName));
	      List<MenuItem> itens = new ArrayList();
	      ItemBuilder itemBuilder = new ItemBuilder();
	      Gamer gamer = GamerManager.getGamer(player1.getUniqueId());

	      ArrayList lore;
	      for(Iterator var9 = kitList.iterator(); var9.hasNext(); lore = null) {
	         Kit kit = (Kit)var9.next();
	         lore = new ArrayList();
	         Iterator var12 = kit.getDescription().iterator();

	         while(var12.hasNext()) {
	            String line = (String)var12.next();
	            lore.add(line.replaceAll("&", "§"));
	         }

	         if (inventoryMode == InventoryMode.LOJA) {
	            if (continuePlayer(player1, kit.getName(), inventoryMode)) {
	               lore.add("");
	               lore.add("§fCusto: §6" + StringUtility.formatValue(kit.getPrice()) + " coins");
	               lore.add("");
	               lore.add("§eClique para comprar.");
	               itens.add(new MenuItem(itemBuilder.type(kit.getIcon().getType()).name("§a" + kit.getName()).lore(lore).build(), new KitSelector.LojaKitsHandler(kit)));
	            } else {
	               lore.add("");
	               lore.add("§cVocê já possuí este Kit.");
	               itens.add(new MenuItem(itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§c" + kit.getName()).lore(lore).build(), new KitSelector.LojaKitsHandler(kit)));
	            }
	         } else if (continuePlayer(player1, kit.getName(), inventoryMode)) {
	            boolean isOp = KitManager.hasCombinationOp(inventoryMode == InventoryMode.KIT_PRIMARIO ? gamer.getKit2() : gamer.getKit1(), kit.getName());
	            if (KitManager.isSameKit(inventoryMode == InventoryMode.KIT_PRIMARIO ? gamer.getKit2() : gamer.getKit1(), kit.getName())) {
	               lore.add("");
	               lore.add("§cVocê ja escolheu este Kit.");
	               itens.add(new MenuItem(itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§c" + kit.getName()).lore(lore).build(), new KitSelector.KitSelectHandler(kit.getName(), inventoryMode)));
	            } else if (isOp) {
	               lore.add("");
	               lore.add("§cEsta combinação de Kit está bloqueada.");
	               itens.add(new MenuItem(itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§c" + kit.getName()).lore(lore).build(), new KitSelector.KitSelectHandler(kit.getName(), inventoryMode)));
	            } else {
	               lore.add("");
	               lore.add("§eClique para selecionar.");
	               itens.add(new MenuItem(itemBuilder.type(kit.getIcon().getType()).name("§a" + kit.getName()).lore(lore).build(), new KitSelector.KitSelectHandler(kit.getName(), inventoryMode)));
	            }
	         } else {
	            lore.add("");
	            lore.add("§cVocê não possui este Kit.");
	            itens.add(new MenuItem(itemBuilder.type(Material.STAINED_GLASS_PANE).durability(14).name("§c" + kit.getName()).lore(lore).build(), new KitSelector.KitSelectHandler(kit.getName(), inventoryMode)));
	         }

	         lore.clear();
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
	            (new KitSelector(player, page - 1, inventoryMode)).open(player);
	         }), 27);
	      }

	      if (itens.size() / 28 + 1 <= page) {
	         this.setItem(35, itemBuilder.type(Material.AIR).build());
	      } else {
	         this.setItem(new MenuItem(itemBuilder.type(Material.ARROW).name("§aPróxima Página").build(), (player, arg1, arg2, arg3, arg4) -> {
	            (new KitSelector(player, page + 1, inventoryMode)).open(player);
	         }), 35);
	      }

	      int kitSlot = 10;

	      for(int i = pageStart; i < pageEnd; ++i) {
	         this.setItem((MenuItem)itens.get(i), kitSlot);
	         if (kitSlot % 9 == 7) {
	            kitSlot += 3;
	         } else {
	            ++kitSlot;
	         }
	      }

	      if (itens.size() == 0) {
	         this.setItem(31, itemBuilder.type(Material.REDSTONE_BLOCK).name("§c" + (inventoryMode == InventoryMode.LOJA ? "Nenhum Kit para comprar!" : "Nenhum kit para selecionar!")).build());
	      }

	      itens.clear();
	      kitList.clear();
	   }

	   public static boolean continuePlayer(Player player, String kit, InventoryMode modo) {
	      if (modo == InventoryMode.KIT_PRIMARIO && HardcoreGamesOptions.DOUBLE_KIT) {
	         return true;
	      } else if (modo == InventoryMode.LOJA) {
	         return !KitManager.hasPermissionKit(player, kit, false);
	      } else {
	         return KitManager.hasPermissionKit(player, kit, false);
	      }
	   }

	   private static class KitSelectHandler implements MenuClickHandler {
	      private String kit;
	      private InventoryMode inventoryMode;

	      public KitSelectHandler(String kit, InventoryMode inventoryMode) {
	         this.kit = kit;
	         this.inventoryMode = inventoryMode;
	      }

	      public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
	         if (clickType == ClickType.LEFT) {
	            player.closeInventory();
	            player.performCommand("kit" + (this.inventoryMode == InventoryMode.KIT_PRIMARIO ? " " : "2 ") + this.kit);
	         }
	      }
	   }

	   private static class LojaKitsHandler implements MenuClickHandler {
	      private Kit kit;

	      public LojaKitsHandler(Kit kit) {
	         this.kit = kit;
	      }

	      public void onClick(Player player, Inventory arg1, ClickType clickType, ItemStack arg3, int arg4) {
	         if (clickType == ClickType.LEFT) {
	            if (KitSelector.continuePlayer(player, this.kit.getName(), InventoryMode.LOJA)) {
	               player.closeInventory();
	               BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	               if (bukkitPlayer.getInt(DataType.COINS) >= this.kit.getPrice()) {
	                  bukkitPlayer.getDataHandler().getData(DataType.COINS).remove(this.kit.getPrice());
	                  bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
	                  bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick(), bukkitPlayer.getUniqueId())).type(PacketType.BUKKIT_SEND_INFO).field("add-perm").fieldValue("hg.kit." + this.kit.getName().toLowerCase()));
	                  player.sendMessage("§eVocê comprou o kit §b%kit%!".replace("%kit%", this.kit.getName()));
	                  player.playSound(player.getLocation(), Sound.LEVEL_UP, 1.0F, 1.0F);
	               } else {
	                  player.sendMessage("§cVocê precisa de mais %valor% coins para comprar este kit!".replace("%valor%", StringUtility.formatValue(this.kit.getPrice() - bukkitPlayer.getInt(DataType.COINS))));
	               }

	            }
	         }
	      }
	   }
	}