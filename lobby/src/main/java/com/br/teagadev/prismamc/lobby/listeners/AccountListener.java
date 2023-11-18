package com.br.teagadev.prismamc.lobby.listeners;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ActionItemStack;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ActionItemStack.InteractHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeVisibilityEvent;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.lobby.LobbyMain;
import com.br.teagadev.prismamc.lobby.commands.ServerCommand;
import com.br.teagadev.prismamc.lobby.common.inventory.InventoryCommon;
import java.util.Iterator;
import java.util.UUID;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class AccountListener implements Listener {
	   private final String VISIBILITY_DELAY_TAG = "visibility.delay.time";
	   private boolean registred = false;
	   private final InteractHandler profileHandler = (player, itemStack, itemAction, clickedBlock) -> {
	      if (!itemAction.name().contains("RIGHT")) {
	         return true;
	      } else {
	         player.chat("/acc");
	         return true;
	      }
	   };
	   private final InteractHandler gamesModeHandler = (player, itemStack, itemAction, clickedBlock) -> {
	      if (!itemAction.name().contains("RIGHT")) {
	         return true;
	      } else {
	         if (BukkitMain.getServerType() == ServerType.LOBBY_PVP) {
	            InventoryCommon.getGamesInventory().open(player);
	         } else {
	            InventoryCommon.getGamesInventory().open(player);
	         }

	         return true;
	      }
	   };
	   private final InteractHandler visibilityHandler = (player, itemStack, itemAction, clickedBlock) -> {
	      if (!itemAction.name().contains("RIGHT")) {
	         return true;
	      } else if (this.hasDelay(player)) {
	         return true;
	      } else {
	         this.addDelay(player);
	         if (!itemAction.name().contains("RIGHT")) {
	            return true;
	         } else {
	            DataHandler dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();
	            if (dataHandler.getBoolean(DataType.PLAYERS_VISIBILITY)) {
	               dataHandler.getData(DataType.PLAYERS_VISIBILITY).setValue(false);
	               LobbyMain.runAsync(() -> {
	                  dataHandler.saveCategory(DataCategory.PREFERENCES);
	               });
	               player.sendMessage("§aVocê ativou a opção de visibilidade dos jogadores.");
	               Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeVisibilityEvent(player, false));
	            } else {
	               dataHandler.getData(DataType.PLAYERS_VISIBILITY).setValue(true);
	               LobbyMain.runAsync(() -> {
	                  dataHandler.saveCategory(DataCategory.PREFERENCES);
	               });
	               player.sendMessage("§aVocê ativou a opção de visibilidade dos jogadores.");
	               player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), (new ItemBuilder()).type(Material.INK_SACK).durability(10).name("§fJogadores: §aON").build());
	               Bukkit.getServer().getPluginManager().callEvent(new PlayerChangeVisibilityEvent(player, true));
	            }

	            return true;
	         }
	      }
	   };

	   private void register() {
	      if (!this.registred) {
	         this.registred = true;
	         ActionItemStack.register("§fJogadores: §aON", this.visibilityHandler);
	         ActionItemStack.register("§aSelecionar jogo", this.gamesModeHandler);
	         ActionItemStack.register("§aMeu perfil", this.profileHandler);
	      }
	   }

	   @EventHandler
	   public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
	      if (BukkitMain.getServerType() != ServerType.LOBBY) {
	         DataCategory category = BukkitMain.getServerType() == ServerType.LOBBY_HARDCOREGAMES ? DataCategory.HARDCORE_GAMES : DataCategory.KITPVP;
	         BukkitPlayer bp = BukkitMain.getBukkitPlayer(e.getUniqueId());

	         try {
	            bp.getDataHandler().load(new DataCategory[]{category});
	         } catch (Exception var5) {
	            var5.printStackTrace();
	         }

	      }
	   }

	   @EventHandler
	   public void onJoin(PlayerJoinEvent event) {
	      this.register();
	      Player player = event.getPlayer();
	      ItemBuilder itemBuilder = new ItemBuilder();
	      this.addDelay(player);
	      if (player.getGameMode() != GameMode.ADVENTURE) {
	         player.setGameMode(GameMode.ADVENTURE);
	      }

	      player.setMaxHealth(1.7D);
	      player.setHealth(1.7D);
	      BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	      boolean playerVisibility = bukkitPlayer.getBoolean(DataType.PLAYERS_VISIBILITY);
	      PlayerInventory playerInventory = player.getInventory();
	      playerInventory.clear();
	      playerInventory.setArmorContents((ItemStack[])null);
	      playerInventory.setItem(0, itemBuilder.type(Material.COMPASS).name("§aSelecionar jogo").build());
	      playerInventory.setItem(1, itemBuilder.type(Material.SKULL_ITEM).skin(bukkitPlayer.getNick()).name("§aMeu perfil").build());
	      playerInventory.setItem(7, itemBuilder.type(Material.INK_SACK).durability(playerVisibility ? 10 : 8).name("§fJogadores: §aON").build());
	      playerInventory.setItem(8, itemBuilder.type(Material.NETHER_STAR).name("§aSelecionar Lobby").build());
	      player.updateInventory();
	      if (BukkitMain.getServerType() == ServerType.LOBBY) {
	         TitleAPI.sendTitle(player, "§6§lCRAZZY", "§eSeja bem-vindo(a)", 0, 0, 2);
	      }

	      Groups playerGroup = bukkitPlayer.getGroup();
	      boolean announceJoin = !bukkitPlayer.containsFake() && !VanishAPI.inAdmin(player) && playerGroup.getLevel() > Groups.MEMBRO.getLevel() && bukkitPlayer.getBoolean(DataType.ANNOUNCEMENT_JOIN);
	      if (bukkitPlayer.getActualTag() != null) {
	         bukkitPlayer.getActualTag();
	      } else {
	         playerGroup.getTag();
	      }

	      Groups group = bukkitPlayer.getGroup();
	      String joinMessage = group.getColor() + "§l" + group.getTag().getPrefix() + " " + group.getColor() + player.getName() + " §6entrou neste lobby!";
	      Iterator var12 = Bukkit.getOnlinePlayers().iterator();

	      while(var12.hasNext()) {
	         Player onlines = (Player)var12.next();
	         if (announceJoin) {
	            onlines.sendMessage(joinMessage);
	         }

	         if (onlines.getUniqueId() != player.getUniqueId()) {
	            if (!playerVisibility) {
	               player.hidePlayer(onlines);
	            }

	            if (!BukkitMain.getBukkitPlayer(onlines.getUniqueId()).getBoolean(DataType.PLAYERS_VISIBILITY)) {
	               onlines.hidePlayer(player);
	            }
	         }
	      }

	      if (playerGroup.getLevel() > Groups.MEMBRO.getLevel()) {
	         player.teleport(LobbyMain.getSpawn().clone().add(0.0D, 0.5D, 0.0D));
	         player.setAllowFlight(true);
	         player.setFlying(true);
	      } else {
	         player.teleport(LobbyMain.getSpawn());
	      }

	      LobbyMain.getScoreBoardCommon().createScoreboard(player);
	   }

	   @EventHandler
	   public void onQuit(PlayerQuitEvent event) {
	      UUID uniqueId = event.getPlayer().getUniqueId();
	      ServerCommand.autorizados.remove(uniqueId);
	   }

	   @EventHandler
	   public void onChangeVisibility(PlayerChangeVisibilityEvent event) {
	      Player player = event.getPlayer();
	      Iterator var3;
	      Player ons;
	      if (event.isVisibility()) {
	         var3 = Bukkit.getOnlinePlayers().iterator();

	         while(var3.hasNext()) {
	            ons = (Player)var3.next();
	            if (!VanishAPI.isInvisible(ons) && !VanishAPI.inAdmin(ons)) {
	               player.showPlayer(ons);
	            }
	         }

	         VanishAPI.updateInvisibles(player);
	         player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), (new ItemBuilder()).type(Material.INK_SACK).durability(10).name("§fJogadores: §aON").build());
	      } else {
	         var3 = Bukkit.getOnlinePlayers().iterator();

	         while(var3.hasNext()) {
	            ons = (Player)var3.next();
	            player.hidePlayer(ons);
	         }

	         VanishAPI.updateInvisibles(player);
	         player.getInventory().setItem(player.getInventory().first(Material.INK_SACK), (new ItemBuilder()).type(Material.INK_SACK).durability(8).name("§fJogadores: §cOFF").build());
	      }

	   }

	   private void addDelay(Player player) {
	      player.setMetadata("visibility.delay.time", new FixedMetadataValue(LobbyMain.getInstance(), System.currentTimeMillis() + 4000L));
	   }

	   private boolean hasDelay(Player player) {
	      if (!player.hasMetadata("visibility.delay.time")) {
	         return false;
	      } else {
	         return ((MetadataValue)player.getMetadata("visibility.delay.time").get(0)).asLong() > System.currentTimeMillis();
	      }
	   }
	}