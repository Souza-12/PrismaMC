package com.br.teagadev.prismamc.commons.bukkit.menu;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.ClickType;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuClickHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuInventory;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuItem;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.profile.addons.League;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.sql.SQLException;
import java.text.SimpleDateFormat;

public class AccountInventory extends MenuInventory {
	   public AccountInventory(String nick) {
	      this(nick, nick);
	   }

	   public AccountInventory(final String nickViewer, final String nickAlvo) {
	      super(nickAlvo.equalsIgnoreCase(nickViewer) ? "Seu perfil" : "Perfil do jogador " + nickAlvo, 5);
	      Player target = BukkitServerAPI.getExactPlayerByNick(nickAlvo);
	      final GamingProfile profile = this.getProfile(target, nickAlvo);
	      if (profile != null) {
	         Groups playerGroup = profile.getGroup();
	         League playerLeague = League.getRanking(profile.getInt(DataType.XP));
	         ItemBuilder itemBuilder = new ItemBuilder();
	         String firstLogin = "Nunca";
	         String lastLogin = "Nunca";
	         if (profile.getLong(DataType.FIRST_LOGGED_IN) != 0L) {
	            firstLogin = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(profile.getLong(DataType.FIRST_LOGGED_IN));
	         }

	         if (profile.getLong(DataType.LAST_LOGGED_IN) != 0L) {
	            lastLogin = (new SimpleDateFormat("dd/MM/yyyy HH:mm:ss")).format(profile.getLong(DataType.LAST_LOGGED_IN));
	         }

	         this.setItem(13, itemBuilder.type(Material.SKULL_ITEM).skin(nickAlvo).name("§a" + nickAlvo).lore(new String[]{"§7Rank: " + playerGroup.getTag().getColor() + playerGroup.getTag().getName(), "§7Primeiro Login: " + firstLogin, "§7Último Login: " + lastLogin}).build());
	         this.setItem(30, new MenuItem(itemBuilder.type(Material.PAPER).name("§aSuas estatísticas").lore(new String[]{"§7Veja suas estatísticas."}).build(), new MenuClickHandler() {
	            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
	               player.closeInventory();
	               (new PlayerStatisticsInventory(nickViewer, nickAlvo, profile)).open(player);
	            }
	         }));
	         this.setItem(31, new MenuItem(itemBuilder.type(Material.ITEM_FRAME).name("§aBiblioteca de skins").lore(new String[]{"§7Escolha uma nova skin, e altere a velha."}).build(), new MenuClickHandler() {
	            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
	               player.closeInventory();
	               (new LibraryInventory(nickViewer, nickAlvo)).open(player);
	            }
	         }));
	         this.setItem(32, new MenuItem(itemBuilder.type(Material.NAME_TAG).name("§aSuas medalhas").lore(new String[]{"§7Veja suas medalhas."}).build(), new MenuClickHandler() {
	            public void onClick(Player player, Inventory inventory, ClickType type, ItemStack itemStack, int slot) {
	               player.closeInventory();
	               player.chat("/medals");
	            }
	         }));
	      }
	   }

	   private GamingProfile getProfile(Player target, String nickAlvo) {
	      BukkitPlayer profile;
	      if (target != null) {
	         profile = BukkitMain.getBukkitPlayer(target.getUniqueId());
	      } else {
	         profile = new BukkitPlayer(nickAlvo, "", CommonsGeneral.getUUIDFetcher().getOfflineUUID(nickAlvo));
	      }

	      if (!profile.getDataHandler().isCategoryLoaded(DataCategory.ACCOUNT)) {
	         try {
	            profile.getDataHandler().load(new DataCategory[]{DataCategory.ACCOUNT});
	         } catch (SQLException var5) {
	            var5.printStackTrace();
	            profile = null;
	         }
	      }

	      return profile;
	   }
	}