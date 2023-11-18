package com.br.teagadev.prismamc.hardcoregames.listeners;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.bossbar.BossBarAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.enums.GameStages;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.register.Gladiator;
import com.br.teagadev.prismamc.hardcoregames.ability.utility.GladiatorFight;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStageChangeEvent;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameTimerEvent;
import com.br.teagadev.prismamc.hardcoregames.events.player.PlayerDamagePlayerEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.types.MiniFeast;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.EnderPearl;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;
import org.bukkit.event.entity.FoodLevelChangeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.entity.ProjectileLaunchEvent;
import org.bukkit.event.entity.CreatureSpawnEvent.SpawnReason;
import org.bukkit.event.entity.EntityDamageEvent.DamageCause;
import org.bukkit.event.player.PlayerRespawnEvent;

public class GameListener implements Listener {
   private final Random random = new Random();

   @EventHandler
   public void onTimer(GameTimerEvent event) {
      if (event.getTime() > 5 && event.getTime() % 240 == 0 && HardcoreGamesOptions.MINIFEAST) {
         MiniFeast.create();
      }

      if (event.getTime() == 600 && HardcoreGamesOptions.FEAST) {
         StructuresManager.getFeast().createFeast(StructuresManager.getValidLocation(true));
      }

      if (event.getTime() != 2100 && event.getTime() != 2160 && event.getTime() != 2220 && event.getTime() != 2280 && event.getTime() != 2340) {
         if (event.getTime() == 2400) {
            HardcoreGamesOptions.MINIFEAST = false;
            StructuresManager.getFinalBattle().create();
         }
      } else {
         Bukkit.broadcastMessage("§aA Arena final irá spawnar em: §7%tempo%".replace("%tempo%", DateUtils.formatSeconds(2400 - event.getTime())));
      }

      if (event.getTime() != 3000 && event.getTime() != 3300 && event.getTime() != 3420 && event.getTime() != 3480 && event.getTime() != 3540) {
         if (event.getTime() == 3600) {
            HardcoreGamesMain.getGameManager().getGameType().choiceWinner();
         }
      } else {
         Bukkit.broadcastMessage("§aA partida irá acabar em: §7%tempo%".replace("%tempo%", DateUtils.formatSeconds(3600 - event.getTime())));
      }

   }

   @EventHandler
   public void onFoodLevelChange(FoodLevelChangeEvent event) {
      if (event.getEntity() instanceof Player) {
         Player player = (Player)event.getEntity();
         if (GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
            player.setSaturation(5.0F);
         } else {
            event.setCancelled(true);
         }

         player = null;
      }

   }

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      event.setDeathMessage((String)null);
      event.getDrops().clear();
      Player morreu = event.getEntity();
      Player matou = morreu.getKiller();
      Location loc = morreu.getLocation();
      GladiatorFight gamerDied;
      if (Gladiator.inGlad(morreu)) {
         gamerDied = Gladiator.getGladiatorFight(morreu);
         loc = gamerDied.getBackForPlayer(morreu);
         gamerDied.cancelGlad();
         gamerDied = null;
      }

      PlayerAPI.dropItems(morreu, loc);
      Gamer gamerDied1 = GamerManager.getGamer(morreu.getUniqueId());
      if (!this.availableToRespawn(morreu)) {
         gamerDied1.setEliminado(true);
         gamerDied1.setPlaying(false);
      }

      int jogadoresRestantes = GamerManager.getAliveGamers().size();
      if (matou != null && matou instanceof Player) {
         Gamer gamerKill = GamerManager.getGamer(matou.getUniqueId());
         gamerKill.addKill();
         Bukkit.broadcastMessage("§b%matou% (%matouKit%) matou %morreu% (%morreuKit%) usando sua %item%. \n§c%restantes% jogadores restantes.".replace("%matou%", matou.getName()).replace("%matouKit%", gamerKill.getKits()).replace("%morreu%", morreu.getName()).replace("%morreuKit%", gamerDied1.getKits()).replace("%restantes%", "" + jogadoresRestantes).replace("%item%", getItemInHand(matou.getItemInHand().getType())));
         HardcoreGamesScoreboard.getScoreBoardCommon().updateKills(matou, gamerKill.getKills());
         gamerKill = null;
      } else {
         Bukkit.broadcastMessage("§b%morreu% (%kit%) §emorreu %causa% \n§c%restantes% jogadores restantes.".replace("%morreu%", morreu.getName()).replace("%kit%", gamerDied1.getKit1()).replace("%restantes%", "" + jogadoresRestantes).replace("%causa%", this.getCausa(morreu.getLastDamageCause().getCause())));
      }

      handleStats(matou, morreu);
      gamerDied1 = null;
      morreu = null;
      matou = null;
      loc = null;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.MONITOR
   )
   public void onEntitySpawn(CreatureSpawnEvent event) {
      if (event.getEntityType() != EntityType.GHAST && event.getEntityType() != EntityType.PIG_ZOMBIE) {
         if (event.getSpawnReason() == SpawnReason.NATURAL) {
            if (this.random.nextInt(5) > 2) {
               event.setCancelled(true);
            }

         }
      } else {
         event.setCancelled(true);
      }
   }

   @EventHandler
   public void onRespawn(PlayerRespawnEvent event) {
      event.setRespawnLocation(HardcoreGamesUtility.getRandomLocation(160));
   }

   @EventHandler
   public void onAutoRespawn(PlayerRespawnEvent event) {
      Player player = event.getPlayer();
      if (this.availableToRespawn(player)) {
         HardcoreGamesMain.getGameManager().getGameType().setGamer(player);
      } else {
         HardcoreGamesMain.getGameManager().getGameType().setEspectador(player);
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
         HardcoreGamesMain.runAsync(() -> {
            bukkitPlayer.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES});
         });
         if (!HardcoreGamesUtility.availableToSpec(player)) {
            BukkitServerAPI.redirectPlayer(player, "LobbyHardcoreGames", true);
         }
      }

      player = null;
   }

   @EventHandler(
      ignoreCancelled = true,
      priority = EventPriority.NORMAL
   )
   public void onHit(PlayerDamagePlayerEvent event) {
      Gamer gamer = GamerManager.getGamer(event.getDamaged());
      if (HardcoreGamesOptions.DOUBLE_KIT) {
         BossBarAPI.send(event.getPlayer(), event.getDamaged().getName() + " - " + gamer.getKit1() + " - " + gamer.getKit2(), 3);
      } else {
         BossBarAPI.send(event.getPlayer(), event.getDamaged().getName() + " - " + gamer.getKit1(), 3);
      }

   }

   @EventHandler
   public void onEnderPearl(ProjectileLaunchEvent event) {
      if (event.getEntity().getShooter() instanceof Player && event.getEntity() instanceof EnderPearl) {
         int tempo = HardcoreGamesMain.getTimerManager().getTime().get();
         if (tempo > 2375 && tempo < 2405) {
            event.setCancelled(true);
         }
      }

   }

   @EventHandler(
      priority = EventPriority.MONITOR,
      ignoreCancelled = true
   )
   public void onCreatureSpawn(CreatureSpawnEvent event) {
      if (HardcoreGamesMain.getTimerManager().getTime().get() > 2340) {
         event.setCancelled(true);
      }

   }

   private boolean availableToRespawn(Player player) {
      if (BukkitMain.getServerType() != ServerType.EVENTO && BukkitMain.getServerType() != ServerType.MINIPRISMA) {
         return player.hasPermission("hardcoregames.respawn") && HardcoreGamesMain.getTimerManager().getTime().get() <= 300;
      } else {
         return false;
      }
   }

   private static String getItemInHand(Material material) {
      String causa = "o Punho";
      switch(material) {
      case WOOD_SWORD:
         causa = "uma Espada de Madeira";
         break;
      case STONE_SWORD:
         causa = "uma Espada de Pedra";
         break;
      case GOLD_SWORD:
         causa = "uma Espada de Ouro";
         break;
      case IRON_SWORD:
         causa = "uma Espada de Ferro";
         break;
      case DIAMOND_SWORD:
         causa = "uma Espada de Diamante";
         break;
      case WOOD_PICKAXE:
         causa = "uma Picareta de Madeira";
         break;
      case STONE_PICKAXE:
         causa = "uma Picareta de Pedra";
         break;
      case GOLD_PICKAXE:
         causa = "uma Picareta de Ouro";
         break;
      case IRON_PICKAXE:
         causa = "uma Picareta de Ferro";
         break;
      case DIAMOND_PICKAXE:
         causa = "uma Picareta de Diamante";
         break;
      case WOOD_AXE:
         causa = "um Machado de Madeira";
         break;
      case STONE_AXE:
         causa = "um Machado de Pedra";
         break;
      case GOLD_AXE:
         causa = "um Machado de Ouro";
         break;
      case IRON_AXE:
         causa = "um Machado de Ferro";
         break;
      case DIAMOND_AXE:
         causa = "um Machado de Diamante";
         break;
      case COMPASS:
         causa = "uma Bussola";
         break;
      case MUSHROOM_SOUP:
         causa = "uma Sopa";
         break;
      case STICK:
         causa = "um Graveto";
      }

      return causa;
   }

   private String getCausa(DamageCause deathCause) {
      String cause = "por uma causa desconhecida";
      switch(deathCause) {
      case ENTITY_ATTACK:
         cause = "atacado por um monstro";
         break;
      case CUSTOM:
         cause = "de uma forma não conhecida";
         break;
      case BLOCK_EXPLOSION:
         cause = "explodido em mil pedaços";
         break;
      case ENTITY_EXPLOSION:
         cause = "explodido por um monstro";
         break;
      case CONTACT:
         cause = "espetado por um cacto";
         break;
      case FALL:
         cause = "de queda";
         break;
      case FALLING_BLOCK:
         cause = "stompado por um bloco";
         break;
      case FIRE_TICK:
         cause = "pegando fogo";
         break;
      case FIRE:
         cause = "pegando fogo";
         break;
      case LAVA:
         cause = "nadando na lava";
         break;
      case LIGHTNING:
         cause = "atingido por um raio";
         break;
      case MAGIC:
         cause = "atingido por uma magia";
         break;
      case MELTING:
         cause = "atingido por um boneco de neve";
         break;
      case POISON:
         cause = "envenenado";
         break;
      case PROJECTILE:
         cause = "atingido por um projétil";
         break;
      case STARVATION:
         cause = "de fome";
         break;
      case SUFFOCATION:
         cause = "sufocado";
         break;
      case SUICIDE:
         cause = "se suicidando";
         break;
      case THORNS:
         cause = "encostando em alguns espinhos";
         break;
      case VOID:
         cause = "pelo void";
         break;
      case WITHER:
         cause = "pelo efeito do whiter";
         break;
      case DROWNING:
         cause = "afogado";
      }

      return cause;
   }

   public static void handleStats(Player killer, Player died) {
      BukkitPlayer bukkitPlayerDied = BukkitMain.getBukkitPlayer(died.getUniqueId());
      died.sendMessage("§c-%quantia% XP".replace("%quantia%", "2"));
      died.sendMessage("§c-%quantia% coins".replace("%quantia%", "10"));
      bukkitPlayerDied.add(BukkitMain.getServerType() == ServerType.HARDCORE_GAMES ? DataType.HG_DEATHS : DataType.HG_EVENT_DEATHS);
      bukkitPlayerDied.remove(DataType.COINS, 10);
      bukkitPlayerDied.removeXP(2);
      HardcoreGamesMain.runAsync(() -> {
         bukkitPlayerDied.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES});
      });
      if (killer != null) {
         BukkitPlayer bukkitPlayerKiller = BukkitMain.getBukkitPlayer(killer.getUniqueId());
         int xp = PlayerAPI.getXPKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLEXP_TIME));
         int coins = PlayerAPI.getCoinsKill(killer, bukkitPlayerKiller.getLong(DataType.DOUBLECOINS_TIME));
         bukkitPlayerKiller.add(DataType.COINS, coins);
         bukkitPlayerKiller.addXP(xp);
         bukkitPlayerKiller.add(BukkitMain.getServerType() == ServerType.HARDCORE_GAMES ? DataType.HG_KILLS : DataType.HG_EVENT_KILLS);
         HardcoreGamesMain.runAsync(() -> {
            bukkitPlayerKiller.getDataHandler().saveCategorys(new DataCategory[]{DataCategory.ACCOUNT, DataCategory.HARDCORE_GAMES});
         });
      }
   }

   @EventHandler
   public void onGameEnd(GameStageChangeEvent event) {
      if (event.getNewStage() == GameStages.END) {
         HardcoreGamesMain.console("Removing listeners from GameListener");
         HandlerList.unregisterAll(this);
      }

   }
}