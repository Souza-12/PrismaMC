package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.BukkitUpdateEvent.UpdateType;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerAdminChangeEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeScoreboardEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerAdminChangeEvent.AdminChangeType;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeScoreboardEvent.ScoreboardChangeType;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerStatusUpdateEvent;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerCompassEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerCompassEvent.CompassAction;
import com.br.teagadev.prismamc.hardcoregames.manager.combatlog.CombatLogManager;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.menu.AliveGamers;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import java.util.ArrayList;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPhysicsEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerBucketEmptyEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerLoginEvent.Result;
import org.bukkit.inventory.ItemStack;

public class GeneralListeners implements Listener {
   public static int MEMBERS_SLOTS = 80;

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onLogin(PlayerLoginEvent event) {
      if (event.getResult() == Result.ALLOWED) {
         Player player = event.getPlayer();
         if (!HardcoreGamesMain.getGameManager().canLogin()) {
            event.disallow(Result.KICK_OTHER, "§cVocê não pode entrar na sala agora porque ela está " + (HardcoreGamesMain.getGameManager().isLoading() ? "carregando." : "sendo encerrada."));
         } else {
            if (Bukkit.getOnlinePlayers().size() >= MEMBERS_SLOTS) {
               if (!event.getPlayer().hasPermission("commons.entrar")) {
                  event.disallow(Result.KICK_OTHER, "§cO servidor está lotado!");
               } else {
                  event.allow();
                  this.kickRandomGamer();
               }
            }

            if (event.getResult() == Result.ALLOWED) {
               if (!GamerManager.containsGamer(player.getUniqueId())) {
                  if (HardcoreGamesMain.getGameManager().isPreGame()) {
                     GamerManager.addGamer(player.getUniqueId());
                  } else if (!player.hasPermission("commons.entrar")) {
                     event.disallow(Result.KICK_OTHER, "§cA partida já começou, compre VIP para poder espectar a partida.");
                  } else {
                     GamerManager.addGamer(player.getUniqueId());
                     event.allow();
                  }
               } else {
                  Gamer gamer = GamerManager.getGamer(player.getUniqueId());
                  if (gamer.isRelogar()) {
                     event.allow();
                  } else if (gamer.isEliminado()) {
                     if (!player.hasPermission("hardcoregames.espectar")) {
                        event.disallow(Result.KICK_OTHER, "§cVocê foi eliminado! Compre VIP para que Você possa espectar as partidas.");
                     } else {
                        event.allow();
                     }
                  }

                  gamer = null;
               }
            }
         }

         player = null;
      }
   }

   private void kickRandomGamer() {
      if (HardcoreGamesMain.getGameManager().isPreGame()) {
         Gamer randomGamer = (Gamer)GamerManager.getGamers().stream().filter((check) -> {
            return !check.getPlayer().hasPermission("commons.entrar");
         }).findAny().orElse((Gamer)null);
         if (randomGamer != null) {
            randomGamer.getPlayer().sendMessage("§cO slot que você possuia foi liberado para um jogador VIP!");
            BukkitServerAPI.redirectPlayer(randomGamer.getPlayer(), "LobbyHardcoreGames", true);
            randomGamer = null;
         }

      }
   }

   @EventHandler
   public void onJoin(PlayerJoinEvent event) {
      Player player = event.getPlayer();
      HardcoreGamesMain.getTimerManager().updateAlive();
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      HardcoreGamesScoreboard.createScoreboard(gamer);
      if (!HardcoreGamesMain.getGameManager().isPreGame()) {
         gamer.setOnline(true);
         if (gamer.isRelogar()) {
            gamer.setRelogar(false);
            gamer.setEliminado(true);
            Bukkit.broadcastMessage("§a%nick% voltou ao jogo.".replace("%nick%", player.getName()));
            if (gamer.getTaskID() != -1) {
               Bukkit.getServer().getScheduler().cancelTask(gamer.getTaskID());
               gamer.setTaskID(-1);
            }

            return;
         }

         if (gamer.isEliminado()) {
            HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);
            return;
         }

         if (player.hasPermission("hardcoregames.respawn") && HardcoreGamesMain.getTimerManager().getTime().get() <= 300) {
            gamer.setEliminado(true);
            HardcoreGamesMain.getGameManager().getGameType().setGamer(player);
            player.teleport(HardcoreGamesUtility.getRandomLocation(160));
         } else {
            HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onUpdate(BukkitUpdateEvent event) {
      if (event.getType() == UpdateType.SEGUNDO) {
         HardcoreGamesMain.getTimerManager().onSecond();
      }
   }

   @EventHandler
   public void onAdminChange(PlayerAdminChangeEvent event) {
      if (event.getChangeType() == AdminChangeType.ENTROU) {
         HardcoreGamesMain.getGameManager().getGameType().setEspectador(event.getPlayer(), true);
      } else {
         HardcoreGamesMain.getGameManager().getGameType().setGamer(event.getPlayer());
      }

   }

   @EventHandler
   public void onCompass(PlayerInteractEvent event) {
      if (event.hasItem() && event.getItem().getType() == Material.COMPASS && event.getAction() != Action.PHYSICAL) {
         Player player = event.getPlayer();
         if (!GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
            (new AliveGamers()).open(player);
            return;
         }

         if (VanishAPI.inAdmin(player)) {
            (new AliveGamers()).open(player);
            return;
         }

         PlayerCompassEvent compassEvent = new PlayerCompassEvent(player, event.getAction().name().contains("RIGHT") ? CompassAction.RIGHT : CompassAction.LEFT);
         Bukkit.getServer().getPluginManager().callEvent(compassEvent);
         Player alvo = compassEvent.getTarget();
         if (alvo == null) {
            player.sendMessage("§cNenhum jogador foi encontrado! Bússola apontando para o spawn.");
            player.setCompassTarget(player.getWorld().getSpawnLocation());
         } else {
            player.sendMessage("§eSua bússola está sendo apontada para o jogador §7%nick%".replace("%nick%", alvo.getName()));
            player.setCompassTarget(alvo.getLocation());
            alvo = null;
         }

         player = null;
         compassEvent = null;
      }

   }

   @EventHandler
   public void onScoreChange(PlayerChangeScoreboardEvent event) {
      if (event.getChangeType() == ScoreboardChangeType.ATIVOU) {
         HardcoreGamesScoreboard.createScoreboard(event.getPlayer());
      }

   }

   @EventHandler
   public void onPlayerBucketEmpty(PlayerBucketEmptyEvent event) {
      if (!HardcoreGamesOptions.PLACE_OPTION && !event.getPlayer().hasPermission("hardcoregames.place")) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onPlace(BlockPlaceEvent event) {
      if (event.getBlock().getLocation().getBlockY() > 128) {
         event.setCancelled(true);
         event.getPlayer().sendMessage("§cA altura maxima permitida para construção é de 128.");
      } else {
         if (!HardcoreGamesOptions.PLACE_OPTION && !event.getPlayer().hasPermission("hardcoregames.place")) {
            event.setCancelled(true);
         }

      }
   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onBreak(BlockBreakEvent event) {
      if (!HardcoreGamesOptions.BREAK_OPTION && !event.getPlayer().hasPermission("hardcoregames.break")) {
         event.setCancelled(true);
      }

      if (HardcoreGamesMain.getTimerManager().getTime().get() < 300 && event.getBlock().getType().equals(Material.IRON_ORE)) {
         event.getPlayer().sendMessage(ChatColor.RED + "Você só pode minerar após os 5 minutos.");
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onPhysic(BlockPhysicsEvent event) {
      if (event.getBlock().getType().equals(Material.BROWN_MUSHROOM)) {
         event.setCancelled(true);
      } else if (event.getBlock().getType().equals(Material.RED_MUSHROOM)) {
         event.setCancelled(true);
      }

   }

   @EventHandler(
      priority = EventPriority.NORMAL,
      ignoreCancelled = true
   )
   public void onBreak1(BlockBreakEvent event) {
      Player player = event.getPlayer();
      Gamer gamer = GamerManager.getGamer(player.getUniqueId());
      if (gamer.isPlaying()) {
         Material material = event.getBlock().getType();
         ItemStack[] var5;
         int var6;
         int var7;
         ItemStack itens;
         if (material != Material.COBBLESTONE && material != Material.STONE) {
            if (material == Material.BROWN_MUSHROOM) {
               if (!PlayerAPI.isFull(player.getInventory())) {
                  event.getBlock().setType(Material.AIR);
                  player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.BROWN_MUSHROOM)});
               } else {
                  var5 = player.getInventory().getContents();
                  var6 = var5.length;

                  for(var7 = 0; var7 < var6; ++var7) {
                     itens = var5[var7];
                     if (itens.getType().equals(material) && itens.getAmount() != 64) {
                        event.getBlock().setType(Material.AIR);
                        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.BROWN_MUSHROOM)});
                        break;
                     }
                  }
               }
            } else if (material == Material.RED_MUSHROOM) {
               if (!PlayerAPI.isFull(player.getInventory())) {
                  event.getBlock().setType(Material.AIR);
                  player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.RED_MUSHROOM)});
               } else {
                  var5 = player.getInventory().getContents();
                  var6 = var5.length;

                  for(var7 = 0; var7 < var6; ++var7) {
                     itens = var5[var7];
                     if (itens.getType().equals(material) && itens.getAmount() != 64) {
                        event.getBlock().setType(Material.AIR);
                        player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.RED_MUSHROOM)});
                        break;
                     }
                  }
               }
            } else if (material.name().contains("LOG") && !gamer.containsKit("Lumberjack") && !gamer.containsKit("JackHammer")) {
               if (!PlayerAPI.isFull(player.getInventory())) {
                  ArrayList<ItemStack> items = new ArrayList(event.getBlock().getDrops());
                  Iterator var13 = items.iterator();

                  while(var13.hasNext()) {
                     ItemStack item = (ItemStack)var13.next();
                     player.getInventory().addItem(new ItemStack[]{item});
                  }

                  event.getBlock().setType(Material.AIR);
               } else {
                  var5 = player.getInventory().getContents();
                  var6 = var5.length;

                  for(var7 = 0; var7 < var6; ++var7) {
                     itens = var5[var7];
                     if (itens.getType().equals(material) && itens.getAmount() != 64) {
                        ArrayList<ItemStack> items = new ArrayList(event.getBlock().getDrops());
                        Iterator var10 = items.iterator();

                        while(var10.hasNext()) {
                           ItemStack item = (ItemStack)var10.next();
                           player.getInventory().addItem(new ItemStack[]{item});
                        }

                        event.getBlock().setType(Material.AIR);
                        items.clear();
                        items = null;
                        break;
                     }
                  }
               }
            }
         } else if (!PlayerAPI.isFull(player.getInventory())) {
            event.getBlock().setType(Material.AIR);
            player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.COBBLESTONE)});
         } else {
            var5 = player.getInventory().getContents();
            var6 = var5.length;

            for(var7 = 0; var7 < var6; ++var7) {
               itens = var5[var7];
               if (itens.getType().equals(material) && itens.getAmount() != 64) {
                  event.getBlock().setType(Material.AIR);
                  player.getInventory().addItem(new ItemStack[]{new ItemStack(Material.COBBLESTONE)});
                  break;
               }
            }
         }

         material = null;
      }

      player = null;
      gamer = null;
   }

   @EventHandler
   public void onDrop(PlayerDropItemEvent event) {
      if (!HardcoreGamesOptions.DROP_OPTION) {
         event.setCancelled(true);
      } else if (BukkitServerAPI.checkItem(event.getItemDrop().getItemStack(), "§bKit")) {
         event.setCancelled(true);
      }

   }

   @EventHandler
   public void onEntityDamage(EntityDamageByEntityEvent event) {
      if (!event.isCancelled()) {
         if (event.getEntity() instanceof Player) {
            Player damager = null;
            Projectile pr;
            if (event.getDamager() instanceof Player) {
               damager = (Player)event.getDamager();
            } else if (event.getDamager() instanceof Projectile) {
               pr = (Projectile)event.getDamager();
               if (pr.getShooter() != null && pr.getShooter() instanceof Player) {
                  damager = (Player)pr.getShooter();
               }

               pr = null;
            }

            if (damager != null) {
               PlayerDamagePlayerEvent event2 = new PlayerDamagePlayerEvent(damager, (Player)event.getEntity(), event.getDamage());
               Bukkit.getPluginManager().callEvent(event2);
               event.setCancelled(event2.isCancelled());
               event.setDamage(event2.getDamage());
               damager = null;
               pr = null;
            }
         }
      }
   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
      CombatLogManager.newCombatLog(event.getDamaged(), event.getPlayer());
   }

   @EventHandler(
      priority = EventPriority.MONITOR
   )
   public void onDeath(PlayerDeathEvent e) {
      CombatLogManager.removeCombatLog(e.getEntity());
   }

   @EventHandler
   public void onCompass(PlayerCompassEvent event) {
      event.setTarget(HardcoreGamesUtility.getRandomPlayer(event.getPlayer()));
   }

   @EventHandler
   public void onUpdateServer(ServerStatusUpdateEvent event) {
      GameStages stage = HardcoreGamesMain.getGameManager().getStage();
      event.writeMemberSlots(80);
      if (stage == GameStages.END) {
         event.writeHungerGames(0, 0, stage.getNome());
      } else {
         event.writeHungerGames(GamerManager.getGamersVivos().size(), HardcoreGamesMain.getTimerManager().getTime().get(), stage.getNome());
      }

      stage = null;
   }
}