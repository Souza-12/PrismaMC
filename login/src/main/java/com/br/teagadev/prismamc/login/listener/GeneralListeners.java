package com.br.teagadev.prismamc.login.listener;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;

import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import lombok.SneakyThrows;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockIgniteEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.block.BlockSpreadEvent;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.*;
import org.bukkit.event.weather.WeatherChangeEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.login.LoginMain;
import com.br.teagadev.prismamc.login.LoginMessages;
import com.br.teagadev.prismamc.login.commands.ServerCommand;
import com.br.teagadev.prismamc.login.manager.captcha.CaptchaManager;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer;
import com.br.teagadev.prismamc.login.manager.gamer.Gamer.AuthenticationType;

import lombok.Getter;

public class GeneralListeners implements Listener {
	   private static final Inventory inventory = Bukkit.getServer().createInventory((InventoryHolder)null, 9, "Clique para se conectar");
	   private int MINUTES = 0;
	   private static final List<Runnable> runnableQueue = new ArrayList();

	   @EventHandler
	   public void onMinute(BukkitUpdateEvent event) {
	      if (event.getType() == UpdateType.MINUTO) {
	         if (this.MINUTES == 10) {
	            LoginMain.getManager().removeGamers(true);
	            this.MINUTES = 0;
	         } else {
	            ++this.MINUTES;
	         }
	      }
	   }

	   @EventHandler
	   public void onUpdate(BukkitUpdateEvent event) {
	      if (event.getType() == UpdateType.SEGUNDO) {
	         Iterator var2 = Bukkit.getOnlinePlayers().iterator();

	         while(var2.hasNext()) {
	            Player onlines = (Player)var2.next();
	            Gamer gamer = LoginMain.getManager().getGamer(onlines);
	            if (gamer != null) {
	               if (gamer.isCaptchaConcluido()) {
	                  if (!gamer.isLogado()) {
	                     if (gamer.getSecondsLogin() == 20) {
	                        onlines.kickPlayer("§cVocê demorou muito para se autenticar!");
	                     } else {
	                        if (gamer.getSecondsLogin() % 2 == 0) {
	                           if (gamer.getAuthenticationType() == AuthenticationType.LOGAR) {
	                              onlines.sendMessage("§cUse o comando: /login <senha>.");
	                           } else {
	                              onlines.sendMessage("§cUse o comando: /register <senha> <senha>");
	                           }
	                        }

	                        gamer.addSecondsLogin();
	                     }
	                  } else if (gamer.getSecondsConnect() == 25) {
	                     onlines.kickPlayer("§cVocê demorou muito para entrar no lobby!");
	                  } else {
	                     gamer.addSecondsConnect();
	                  }
	               } else if (gamer.getSecondsCaptcha() == 8) {
	                  onlines.kickPlayer("§cTempo esgotado! Você não concluiu o captcha e foi expulso do servidor.");
	               } else {
	                  gamer.addSecondsCaptcha();
	               }
	            }
	         }

	      }
	   }

	   @EventHandler(
	      priority = EventPriority.MONITOR
	   )
	   public void onUpdateHandleQueue(BukkitUpdateEvent event) {
	      if (event.getType() == UpdateType.SEGUNDO) {
	         if (runnableQueue.size() > 0) {
	            Runnable runnable = (Runnable)runnableQueue.remove(0);
	            runnable.run();
	         }

	      }
	   }

	   @EventHandler
	   public void onInventory(InventoryClickEvent event) {
	      if (event.getWhoClicked() instanceof Player && event.getInventory().getTitle().equals(inventory.getTitle()) && event.getCurrentItem() != null && event.getCurrentItem().getType() != Material.AIR) {
	         event.setCancelled(true);
	         Player player = (Player)event.getWhoClicked();
	         player.closeInventory();
	         BukkitServerAPI.redirectPlayer(player, "Lobby");
	      }

	   }

	   @EventHandler(
	      priority = EventPriority.MONITOR
	   )
	   public void onLogin(PlayerLoginEvent event) throws Throwable {
	      try {
	         if (event.getResult() == Result.ALLOWED) {
	            Player player = event.getPlayer();
	            AuthenticationType authenticationType = AuthenticationType.LOGAR;
	            BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
	            bukkitPlayer.getDataHandler().load(new DataCategory[]{DataCategory.REGISTER});
	            String password = bukkitPlayer.getString(DataType.REGISTRO_SENHA);
	            System.out.println(password);
	            if (password.length() < 4) {
	               authenticationType = AuthenticationType.REGISTRAR;
	               bukkitPlayer.set(DataType.LAST_IP, event.getAddress().getHostAddress());
	               bukkitPlayer.set(DataType.FIRST_LOGGED_IN, System.currentTimeMillis());
	            }

	            LoginMain.getManager().addGamer(player, authenticationType);
	         }

	      } catch (Throwable var6) {
	         throw var6;
	      }
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onJoin(PlayerJoinEvent event) {
	      Player player = event.getPlayer();
	      player.getInventory().clear();
	      player.teleport(LoginMain.getSpawn());
	      if (!player.getGameMode().equals(GameMode.ADVENTURE)) {
	         player.setGameMode(GameMode.ADVENTURE);
	      }

	      BukkitMain.runLater(() -> {
	         if (player.isOnline()) {
	            CaptchaManager.createCaptcha(player);
	         }
	      }, 30L);
	   }

	   @EventHandler
	   public void onQuit(PlayerQuitEvent event) {
	      UUID uuid = event.getPlayer().getUniqueId();
	      ((Gamer)LoginMain.getManager().getGamers().get(uuid)).refresh();
	      ServerCommand.autorizados.remove(uuid);
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onAsyncChat(AsyncPlayerChatEvent event) {
	      if (!event.getMessage().startsWith("/")) {
	         event.setCancelled(true);
	      }
	   }

	   @EventHandler
	   public void onSpread(BlockSpreadEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onFood(FoodLevelChangeEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onWeatherChange(WeatherChangeEvent event) {
	      event.setCancelled(event.toWeatherState());
	   }

	   @EventHandler
	   public void onDamage(EntityDamageEvent event) {
	      event.setCancelled(true);
	      if (event.getCause().equals(DamageCause.VOID)) {
	         event.getEntity().teleport(LoginMain.getSpawn());
	      }

	   }

	   @EventHandler
	   public void onDamage(EntityDamageByEntityEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void ignite(BlockIgniteEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onEntitySpawn(CreatureSpawnEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onDrop(PlayerDropItemEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler
	   public void onSpawn(ItemSpawnEvent event) {
	      event.setCancelled(true);
	   }

	   @EventHandler(
	      priority = EventPriority.LOWEST
	   )
	   public void onBreak(BlockBreakEvent event) {
	      event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()));
	   }

	   @EventHandler
	   public void onPlace(BlockPlaceEvent event) {
	      event.setCancelled(!event.getPlayer().getGameMode().equals(GameMode.CREATIVE) || !ServerCommand.autorizados.contains(event.getPlayer().getUniqueId()));
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent event) {
	      if (event.getPlayer().getGameMode().equals(GameMode.CREATIVE) && ServerCommand.autorizados.contains(event.getPlayer().getUniqueId())) {
	         event.setCancelled(false);
	      } else {
	         event.setCancelled(true);
	      }

	      if (event.getAction() != Action.PHYSICAL && event.getPlayer().getItemInHand().getType().equals(Material.COMPASS)) {
	         event.getPlayer().openInventory(inventory);
	      }

	   }

	   public static List<Runnable> getRunnableQueue() {
	      return runnableQueue;
	   }
	}