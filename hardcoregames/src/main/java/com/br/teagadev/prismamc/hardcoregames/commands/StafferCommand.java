package com.br.teagadev.prismamc.hardcoregames.commands;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.WorldEditAPI;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.Gamer;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.utility.InventoryStore;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitRunnable;

public class StafferCommand implements CommandClass {
   private final HashMap<String, InventoryStore> Skits = new HashMap();
   private static Location ponto_baixo;
   private static Location ponto_alto;

   @Command(
      name = "reviver",
      aliases = {"respawn"},
      groupsToUse = {Groups.ADMIN}
   )
   public void reviver(BukkitCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUtilize: /reviver <Nick>");
      } else {
         Player target = Bukkit.getPlayer(args[0]);
         if (target != null) {
            if (!GamerManager.getGamer(target.getUniqueId()).isPlaying()) {
               HardcoreGamesMain.getGameManager().getGameType().setGamer(target);
               commandSender.sendMessage("§aVocê reviveu o jogador §7" + target.getName());
               target.sendMessage("§aVocê foi revivido!");
            } else {
               commandSender.sendMessage("§cEste jogador não está eliminado.");
            }
         } else {
            commandSender.sendMessage("§cJogador offline.");
         }

      }
   }

   @Command(
      name = "hginfo",
      groupsToUse = {Groups.DONO}
   )
   public void hginfo(BukkitCommandSender commandSender, String label, String[] args) {
      commandSender.sendMessage("");
      int vivos = 0;
      int onlines = 0;
      int espectando = 0;
      int relog = 0;
      Iterator var8 = GamerManager.getGamers().iterator();

      while(var8.hasNext()) {
         Gamer gamers = (Gamer)var8.next();
         ++onlines;
         if (gamers.isPlaying()) {
            ++vivos;
         }

         if (gamers.isOnline() && !gamers.isPlaying()) {
            ++espectando;
         }

         if (gamers.isRelogar()) {
            ++relog;
         }
      }

      commandSender.sendMessage("");
      commandSender.sendMessage("§fJogadores jogando: §a" + vivos);
      commandSender.sendMessage("§fJogadores espectando: §a" + espectando);
      commandSender.sendMessage("§fJogadores para relogar: §a" + relog);
      commandSender.sendMessage("§fJogadores online: §a" + onlines);
      commandSender.sendMessage("");
   }

   @Command(
      name = "toggle",
      groupsToUse = {Groups.MOD_PLUS}
   )
   public void toggle(BukkitCommandSender commandSender, String label, String[] args) {
      if (args.length == 0) {
         commandSender.sendMessage("§cUse /toggle <drops/break/place/feast/minifeast> <on/off>");
         commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
         commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
      } else if (args.length == 2) {
         if (args[0].equalsIgnoreCase("doublekit")) {
            if (args[1].equalsIgnoreCase("on")) {
               HardcoreGamesOptions.DOUBLE_KIT = true;
               HardcoreGamesScoreboard.init();
               if (HardcoreGamesMain.getGameManager().isGaming()) {
                  return;
               }

               Bukkit.getOnlinePlayers().forEach((player) -> {
                  ItemBuilder itemBuilder = new ItemBuilder();
                  PlayerInventory playerInventory = player.getInventory();
                  playerInventory.clear();
                  playerInventory.setArmorContents((ItemStack[])null);
                  playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aEscolher Kit").build());
                  if (HardcoreGamesOptions.DOUBLE_KIT) {
                     playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aEscolher Kit 2").build());
                  }

                  playerInventory.addItem(new ItemStack[]{itemBuilder.type(Material.EMERALD).name("§6Loja de Kits").build()});
                  playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cVoltar ao Lobby").build());
               });
            } else if (args[1].equalsIgnoreCase("off")) {
               GamerManager.getGamers().forEach((gamer) -> {
                  gamer.setKit2("Nenhum");
               });
               HardcoreGamesOptions.DOUBLE_KIT = false;
               HardcoreGamesScoreboard.init();
               if (HardcoreGamesMain.getGameManager().isGaming()) {
                  return;
               }

               Bukkit.getOnlinePlayers().forEach((player) -> {
                  ItemBuilder itemBuilder = new ItemBuilder();
                  PlayerInventory playerInventory = player.getInventory();
                  playerInventory.clear();
                  playerInventory.setArmorContents((ItemStack[])null);
                  playerInventory.setItem(0, itemBuilder.type(Material.CHEST).name("§aEscolher Kit").build());
                  if (HardcoreGamesOptions.DOUBLE_KIT) {
                     playerInventory.setItem(1, itemBuilder.type(Material.CHEST).amount(2).name("§aEscolher Kit 2").build());
                  }

                  playerInventory.addItem(new ItemStack[]{itemBuilder.type(Material.EMERALD).name("§6Loja de Kits").build()});
                  playerInventory.setItem(8, itemBuilder.type(Material.BED).name("§cVoltar ao Lobby").build());
               });
            }
         } else if (args[0].equalsIgnoreCase("feast")) {
            if (args[1].equalsIgnoreCase("on")) {
               if (!HardcoreGamesOptions.FEAST) {
                  HardcoreGamesOptions.FEAST = true;
                  Bukkit.broadcastMessage("§aO feast foi ativado!");
               } else {
                  commandSender.sendMessage("§aO feast já está ativado!");
               }
            } else if (args[1].equalsIgnoreCase("off")) {
               if (HardcoreGamesOptions.FEAST) {
                  Bukkit.broadcastMessage("§cO feast foi desativado!");
                  HardcoreGamesOptions.FEAST = false;
               } else {
                  commandSender.sendMessage("§cO feast já está desativado!");
               }
            }
         } else if (args[0].equalsIgnoreCase("minifeast")) {
            if (args[1].equalsIgnoreCase("on")) {
               if (!HardcoreGamesOptions.MINIFEAST) {
                  HardcoreGamesOptions.MINIFEAST = true;
                  Bukkit.broadcastMessage("§aO mini-feast foi ativado!");
               } else {
                  commandSender.sendMessage("§aO mini-feast já está ativado!");
               }
            } else if (args[1].equalsIgnoreCase("off")) {
               if (HardcoreGamesOptions.MINIFEAST) {
                  HardcoreGamesOptions.MINIFEAST = false;
                  Bukkit.broadcastMessage("§cO mini-feast foi desativado!");
               } else {
                  commandSender.sendMessage("§cO mini-feast já está desativado!");
               }
            }
         } else if (args[0].equalsIgnoreCase("drops")) {
            if (args[1].equalsIgnoreCase("on")) {
               if (!HardcoreGamesOptions.DROP_OPTION) {
                  HardcoreGamesOptions.DROP_OPTION = true;
                  Bukkit.broadcastMessage("§aOs drops foram ativados!");
               } else {
                  commandSender.sendMessage("§aOs drops já estão ativados!");
               }
            } else if (args[1].equalsIgnoreCase("off")) {
               if (HardcoreGamesOptions.DROP_OPTION) {
                  HardcoreGamesOptions.DROP_OPTION = false;
                  Bukkit.broadcastMessage("§cOs drops foram desativados!");
               } else {
                  commandSender.sendMessage("§cOs drops já estão desativados!");
               }
            }
         } else if (args[0].equalsIgnoreCase("break")) {
            if (args[1].equalsIgnoreCase("on")) {
               if (!HardcoreGamesOptions.BREAK_OPTION) {
                  HardcoreGamesOptions.BREAK_OPTION = true;
                  Bukkit.broadcastMessage("§aO break foi ativado!");
               } else {
                  commandSender.sendMessage("§aO break já está ativado!");
               }
            } else if (args[1].equalsIgnoreCase("off")) {
               if (HardcoreGamesOptions.BREAK_OPTION) {
                  HardcoreGamesOptions.BREAK_OPTION = false;
                  Bukkit.broadcastMessage("§cO break foi desativado!");
               } else {
                  commandSender.sendMessage("§cO break já está desativado!");
               }
            }
         } else if (args[0].equalsIgnoreCase("place")) {
            if (args[1].equalsIgnoreCase("on")) {
               if (!HardcoreGamesOptions.PLACE_OPTION) {
                  HardcoreGamesOptions.PLACE_OPTION = true;
                  Bukkit.broadcastMessage("§aO place foi ativado!");
               } else {
                  commandSender.sendMessage("§aO place já está ativado!");
               }
            } else if (args[1].equalsIgnoreCase("off")) {
               if (HardcoreGamesOptions.PLACE_OPTION) {
                  HardcoreGamesOptions.PLACE_OPTION = false;
                  Bukkit.broadcastMessage("§cO place foi desativado!");
               } else {
                  commandSender.sendMessage("§cO place já está desativado!");
               }
            }
         } else {
            commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
            commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
            commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
         }
      } else if (args.length == 3) {
         if (args[0].equalsIgnoreCase("kit")) {
            String s = args[1].toLowerCase();
            Iterator var5;
            Player on;
            if (s.equalsIgnoreCase("*")) {
               if (args[2].equalsIgnoreCase("off")) {
                  var5 = KitManager.getAllKits().iterator();

                  while(var5.hasNext()) {
                     Kit allKits = (Kit)var5.next();
                     if (!KitManager.getKitsDesativados().contains(allKits.getName().toLowerCase())) {
                        KitManager.getKitsDesativados().add(allKits.getName().toLowerCase());
                     }
                  }

                  var5 = Bukkit.getOnlinePlayers().iterator();

                  while(var5.hasNext()) {
                     on = (Player)var5.next();
                     KitManager.removeKits(on, true);
                  }

                  Bukkit.broadcastMessage("§cTodos os kits foram desativados!");
               } else if (args[2].equalsIgnoreCase("on")) {
                  KitManager.getKitsDesativados().clear();
                  Bukkit.broadcastMessage("§aTodos os kis foram ativados!");
               } else {
                  commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
                  commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
                  commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
               }
            } else if (KitManager.getKits().containsKey(s)) {
               s = ((Kit)Objects.requireNonNull(KitManager.getKitInfo(s))).getName();
               if (args[2].equalsIgnoreCase("on")) {
                  if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
                     KitManager.getKitsDesativados().remove(s.toLowerCase());
                     commandSender.sendMessage("§aVocê ativou o kit %kit%".replace("%kit%", s));
                  } else {
                     commandSender.sendMessage("§aO kit §a%kit% ja está ativado!".replace("%kit%", s));
                  }
               } else if (args[2].equalsIgnoreCase("off")) {
                  if (KitManager.getKitsDesativados().contains(s.toLowerCase())) {
                     commandSender.sendMessage("§cO kit §a%kit% ja está desativado!".replace("%kit%", s));
                  } else {
                     KitManager.getKitsDesativados().add(s.toLowerCase());
                     commandSender.sendMessage("§cVocê desativou o kit %kit%".replace("%kit%", s));
                     var5 = Bukkit.getOnlinePlayers().iterator();

                     while(var5.hasNext()) {
                        on = (Player)var5.next();
                        KitManager.removeIfContainsKit(on, s);
                     }
                  }
               }
            } else {
               commandSender.sendMessage("§cEste kit não existe!");
            }
         }
      } else {
         commandSender.sendMessage("§cUse /toggle <drops/break/place> <on/off>");
         commandSender.sendMessage("§cUse /toggle kit <kit> <on/off>");
         commandSender.sendMessage("§cUse /toggle kit * <on/off> - para desativar/ativar todos os kits.");
      }

   }

   @Command(
      name = "fkit",
      aliases = {"forcekit"},
      groupsToUse = {Groups.MOD_PLUS}
   )
   public void fkit1(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length != 2) {
            commandSender.sendMessage("§cUtilize: /forcekit <Kit> <Jogador/Todos>");
         } else {
            String kit = args[0].toLowerCase();
            String nick = args[1];
            if (!KitManager.getKits().containsKey(kit)) {
               commandSender.sendMessage("§cEste kit não existe");
            } else {
               kit = ((Kit)Objects.requireNonNull(Objects.requireNonNull(KitManager.getKitInfo(kit)))).getName();
               if (!nick.equalsIgnoreCase("todos") && !nick.equals("*")) {
                  Player target = Bukkit.getPlayer(nick);
                  if (target == null) {
                     commandSender.sendMessage("§cJogador offline!");
                     return;
                  }

                  if (HardcoreGamesMain.getGameManager().isPreGame()) {
                     GamerManager.getGamer(target.getUniqueId()).setKit1(kit);
                  } else {
                     KitManager.handleKitSelect(target, true, kit);
                  }

                  commandSender.sendMessage("§aVocê setou o kit %kit% para %nick%.".replace("%kit%", kit).replace("%nick%", target.getName()));
               } else {
                  Iterator var6;
                  Player player;
                  if (HardcoreGamesMain.getGameManager().isPreGame()) {
                     var6 = Bukkit.getOnlinePlayers().iterator();

                     while(var6.hasNext()) {
                        player = (Player)var6.next();
                        Gamer gamer = GamerManager.getGamer(player.getUniqueId());
                        if (gamer != null) {
                           gamer.setKit1(kit);
                        }
                     }
                  } else {
                     var6 = Bukkit.getOnlinePlayers().iterator();

                     while(var6.hasNext()) {
                        player = (Player)var6.next();
                        KitManager.handleKitSelect(player, true, kit);
                     }
                  }

                  commandSender.sendMessage("§aVocê setou o kit %kit% para todos os jogadores.".replace("%kit%", kit));
               }

            }
         }
      }
   }

   @Command(
      name = "fkit2",
      aliases = {"forcekit2"},
      groupsToUse = {Groups.MOD_PLUS}
   )
   public void fkit2(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length != 2) {
            commandSender.sendMessage("§cUtilize: /forcekit <Kit> <Jogador/Todos>");
         } else if (!HardcoreGamesOptions.DOUBLE_KIT) {
            commandSender.sendMessage("§cEsta partida está habilitada apenas o primeiro KIT.");
         } else {
            String kit = args[0].toLowerCase();
            String nick = args[1];
            if (!KitManager.getKits().containsKey(kit)) {
               commandSender.sendMessage("§cEste kit não existe");
            } else {
               kit = ((Kit)Objects.requireNonNull(Objects.requireNonNull(KitManager.getKitInfo(kit)))).getName();
               if (!nick.equalsIgnoreCase("todos") && !nick.equals("*")) {
                  Player target = Bukkit.getPlayer(nick);
                  if (target == null) {
                     commandSender.sendMessage("§cJogador offline!");
                     return;
                  }

                  if (HardcoreGamesMain.getGameManager().isPreGame()) {
                     GamerManager.getGamer(target.getUniqueId()).setKit2(kit);
                  } else {
                     KitManager.handleKitSelect(target, false, kit);
                  }

                  commandSender.sendMessage("§aVocê setou o kit %kit% para %nick%.".replace("%kit%", kit).replace("%nick%", target.getName()));
               } else {
                  Iterator var6;
                  Player player;
                  if (HardcoreGamesMain.getGameManager().isPreGame()) {
                     var6 = Bukkit.getOnlinePlayers().iterator();

                     while(var6.hasNext()) {
                        player = (Player)var6.next();
                        Gamer gamer = GamerManager.getGamer(player.getUniqueId());
                        if (gamer != null) {
                           gamer.setKit2(kit);
                        }
                     }
                  } else {
                     var6 = Bukkit.getOnlinePlayers().iterator();

                     while(var6.hasNext()) {
                        player = (Player)var6.next();
                        KitManager.handleKitSelect(player, false, kit);
                     }
                  }

                  commandSender.sendMessage("§aVocê setou o kit %kit% para todos os jogadores.".replace("%kit%", kit));
               }

            }
         }
      }
   }

   @Command(
      name = "skit",
      aliases = {"simplekit"},
      groupsToUse = {Groups.MOD}
   )
   public void skit(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length == 0) {
            commandSender.sendMessage("\n§cUtilize: /skit criar <Nome>\n§cUtilize: /skit aplicar <Nome> <Nick/Todos>\n§cUtilize: /skit lista\n");
         } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("lista")) {
               if (this.Skits.size() == 0) {
                  commandSender.sendMessage("§cNenhum kit foi criado.");
                  return;
               }

               StringBuilder skits = new StringBuilder();
               Iterator var5 = this.Skits.keySet().iterator();

               while(var5.hasNext()) {
                  String kits = (String)var5.next();
                  if (skits.toString().equals("")) {
                     if (this.Skits.size() == 1) {
                        skits = new StringBuilder(kits);
                        break;
                     }

                     skits = new StringBuilder(kits);
                  } else {
                     skits.append(",").append(kits);
                  }
               }

               commandSender.sendMessage("§aSetKits criados: §a" + skits);
            } else {
               commandSender.sendMessage("\n§cUtilize: /skit criar <Nome>\n§cUtilize: /skit aplicar <Nome> <Nick/Todos>\n§cUtilize: /skit lista\n");
            }
         } else {
            String kit;
            if (args.length == 2) {
               if (!args[0].equalsIgnoreCase("criar")) {
                  commandSender.sendMessage("\n§cUtilize: /skit criar <Nome>\n§cUtilize: /skit aplicar <Nome> <Nick/Todos>\n§cUtilize: /skit lista\n");
                  return;
               }

               kit = args[1];
               if (this.Skits.containsKey(kit)) {
                  commandSender.sendMessage("§cEste kit já foi criado.");
                  return;
               }

               Player player = commandSender.getPlayer();
               this.Skits.put(kit, new InventoryStore(kit, player.getInventory().getArmorContents(), player.getInventory().getContents(), (List)player.getActivePotionEffects()));
               commandSender.sendMessage("§aVocê criou o kit §a%nome%".replace("%nome%", kit));
            } else if (args.length == 3) {
               if (!args[0].equalsIgnoreCase("aplicar")) {
                  commandSender.sendMessage("\n§cUtilize: /skit criar <Nome>\n§cUtilize: /skit aplicar <Nome> <Nick/Todos>\n§cUtilize: /skit lista\n");
                  return;
               }

               kit = args[1];
               if (!this.Skits.containsKey(kit)) {
                  commandSender.sendMessage("§cEste kit não existe!");
                  return;
               }

               InventoryStore inv = (InventoryStore)this.Skits.get(kit);
               if (!args[2].equalsIgnoreCase("todos") && !args[2].equalsIgnoreCase("*")) {
                  Player target = Bukkit.getPlayer(args[2]);
                  if (target == null) {
                     commandSender.sendMessage("§cJogador offline!");
                     return;
                  }

                  target.getPlayer().setItemOnCursor(new ItemStack(0));
                  target.getInventory().setArmorContents(inv.getArmor());
                  target.getInventory().setContents(inv.getInv());
                  target.addPotionEffects(inv.getPotions());
                  if (target.getInventory().contains(Material.WOOL)) {
                     target.getInventory().setItem(target.getInventory().first(Material.WOOL), (ItemStack)null);
                     KitManager.giveItensKit(target, GamerManager.getGamer(target.getUniqueId()).getKit1());
                  }

                  target.updateInventory();
                  if (!PlayerAPI.isFull(target.getInventory())) {
                     target.getInventory().addItem(new ItemStack[]{new ItemStack(Material.MUSHROOM_SOUP)});
                     target.getInventory().addItem(new ItemStack[]{new ItemStack(Material.MUSHROOM_SOUP)});
                     target.updateInventory();
                  }

                  commandSender.sendMessage("§aVocê aplicou o kit %nome% para o %nick%".replace("%nome%", kit).replace("%nick%", target.getName()));
               } else {
                  Iterator var12 = Bukkit.getOnlinePlayers().iterator();

                  while(var12.hasNext()) {
                     Player ons = (Player)var12.next();
                     Gamer gamer = GamerManager.getGamer(ons.getUniqueId());
                     if (gamer.isPlaying()) {
                        ons.getPlayer().setItemOnCursor(new ItemStack(0));
                        ons.getInventory().setArmorContents(inv.getArmor());
                        ons.getInventory().setContents(inv.getInv());
                        ons.addPotionEffects(inv.getPotions());
                        if (ons.getInventory().contains(Material.WOOL)) {
                           ons.getInventory().setItem(ons.getInventory().first(Material.WOOL), (ItemStack)null);
                           KitManager.giveItensKit(ons, gamer.getKit1());
                        }

                        if (!PlayerAPI.isFull(ons.getInventory())) {
                           ons.getInventory().addItem(new ItemStack[]{new ItemStack(Material.MUSHROOM_SOUP)});
                        }
                     }
                  }

                  commandSender.sendMessage("§aVocê aplicou o kit §a%nome% para todos os jogadores vivos na partida.".replace("%nome%", kit));
               }
            } else {
               commandSender.sendMessage("\n§cUtilize: /skit criar <Nome>\n§cUtilize: /skit aplicar <Nome> <Nick/Todos>\n§cUtilize: /skit lista\n");
            }
         }

      }
   }

   @Command(
      name = "start",
      aliases = {"iniciar"},
      groupsToUse = {Groups.MOD}
   )
   public void start(BukkitCommandSender commandSender, String label, String[] args) {
      if (HardcoreGamesMain.getGameManager().isPreGame()) {
         HardcoreGamesMain.getGameManager().getGameType().start();
      } else {
         commandSender.sendMessage("§eA partida já iniciou.");
      }

   }

   @Command(
      name = "tempo",
      aliases = {"t"},
      groupsToUse = {Groups.MOD}
   )
   public void tempo(BukkitCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUtilize o comando: /tempo <Segundos>");
      } else if (!StringUtility.isInteger(args[0])) {
         commandSender.sendMessage("§cUtilize o comando: /tempo <Segundos>");
      } else {
         int segundos = Integer.parseInt(args[0]);
         if (segundos <= 0) {
            commandSender.sendMessage("§cUtilize o comando: /tempo <Segundos>");
         } else {
            HardcoreGamesMain.getTimerManager().updateTime(segundos);
            Bukkit.broadcastMessage("§aO tempo foi alterado para %tempo%".replace("%tempo%", DateUtils.formatTime(segundos)));
         }
      }
   }

   @Command(
      name = "ffeast",
      aliases = {"forcefeast"},
      groupsToUse = {Groups.MOD_PLUS}
   )
   public void ffeast(BukkitCommandSender commandSender, String label, String[] args) {
      if (!HardcoreGamesMain.getGameManager().isGaming()) {
         commandSender.sendMessage(ChatColor.RED + " * A partida ainda não iniciou ou está no tempo de invencibilidade.");
      } else {
         if (StructuresManager.getFeast().isSpawned()) {
            (new PlayerCommand()).feast(commandSender, label, args);
         } else {
            StructuresManager.getFeast().createFeast(StructuresManager.getValidLocation(true));
         }

      }
   }

   @Command(
      name = "arena",
      groupsToUse = {Groups.MOD}
   )
   public void arena(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length == 0) {
            commandSender.sendMessage("§cUtilize: /arena <Largura> <Altura>\n§cUtilize: /arena limpar\n§cUtilize: /arena final");
         } else if (args.length == 1) {
            if (args[0].equalsIgnoreCase("limpar")) {
               this.limparArena(commandSender.getPlayer());
            } else if (args[0].equalsIgnoreCase("final")) {
               StructuresManager.getFinalBattle().create();
            } else {
               commandSender.sendMessage("§cUtilize: /arena <Largura> <Altura>\n§cUtilize: /arena limpar\n§cUtilize: /arena final");
            }
         } else if (args.length == 2) {
            String largura = args[0];
            String altura = args[1];
            if (!StringUtility.isInteger(largura) || !StringUtility.isInteger(altura)) {
               commandSender.sendMessage("§cUtilize: /arena <Largura> <Altura>\n§cUtilize: /arena limpar\n§cUtilize: /arena final");
               return;
            }

            criarArena(commandSender.getPlayer(), commandSender.getPlayer().getLocation(), Integer.parseInt(largura), Integer.parseInt(altura));
         } else {
            commandSender.sendMessage("§cUtilize: /arena <Largura> <Altura>\n§cUtilize: /arena limpar\n§cUtilize: /arena final");
         }

      }
   }

   public static void criarArena(final Player p, Location loc, final int largura, final int altura) {
      if (p != null) {
         p.sendMessage("§aArena sendo criada...");
      }

      final List<Location> cuboid = new ArrayList();

      for(int bX = -largura; bX <= largura; ++bX) {
         for(int bZ = -largura; bZ <= largura; ++bZ) {
            for(int bY = -1; bY <= altura; ++bY) {
               if (bY != altura && bY != -1) {
                  if (bX == -largura || bZ == -largura || bX == largura || bZ == largura) {
                     cuboid.add(loc.clone().add((double)bX, (double)bY, (double)bZ));
                  }
               } else {
                  cuboid.add(loc.clone().add((double)bX, (double)bY, (double)bZ));
               }
            }
         }
      }

      (new BukkitRunnable() {
         boolean ended = false;
         int blockAtual = 0;
         final int max = cuboid.size() + 10;
         final int blocksPerTick = largura >= 30 ? 60 : 100;
         final Random random = new Random();

         public void run() {
            if (this.ended) {
               this.cancel();
               if (p != null && p.isOnline()) {
                  p.sendMessage("§aA arena foi criada com sucesso!");
               }

               cuboid.clear();
            } else if (this.blockAtual >= this.max) {
               this.ended = true;
            } else {
               for(int i = 0; i < this.blocksPerTick; ++i) {
                  try {
                     Location location = (Location)cuboid.get(this.blockAtual + i);
                     if (location.getBlockY() == altura) {
                        WorldEditAPI.setAsyncBlock(location.getWorld(), location, this.random.nextBoolean() ? Material.BEDROCK.getId() : Material.GLOWSTONE.getId());
                     } else {
                        WorldEditAPI.setAsyncBlock(location.getWorld(), location, Material.BEDROCK.getId());
                     }
                  } catch (NullPointerException | IndexOutOfBoundsException var3) {
                     var3.printStackTrace();
                  }
               }

               this.blockAtual += 55;
            }
         }
      }).runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
      ponto_baixo = loc.clone().add((double)(largura - 1), 0.0D, (double)(largura - 1));
      Location PA = loc.clone().subtract((double)(largura - 1), 0.0D, (double)(largura - 1));
      PA.add(0.0D, (double)(altura - 1), 0.0D);
      ponto_alto = PA;
   }

   public void limparArena(Player p) {
      if (ponto_alto == null) {
         p.sendMessage("§cNão tem nenhuma arena para limpar.");
      } else {
         Iterator var2 = this.getLocationsFromTwoPoints(ponto_baixo, ponto_alto).iterator();

         while(var2.hasNext()) {
            Location location = (Location)var2.next();
            WorldEditAPI.setAsyncBlock(location.getWorld(), location, Material.AIR.getId());
         }

         p.sendMessage("§aA arena foi limpa com sucesso!");
      }
   }

   public List<Location> getLocationsFromTwoPoints(Location location1, Location location2) {
      List<Location> locations = new ArrayList();
      int topBlockX = Math.max(location1.getBlockX(), location2.getBlockX());
      int bottomBlockX = Math.min(location1.getBlockX(), location2.getBlockX());
      int topBlockY = Math.max(location1.getBlockY(), location2.getBlockY());
      int bottomBlockY = Math.min(location1.getBlockY(), location2.getBlockY());
      int topBlockZ = Math.max(location1.getBlockZ(), location2.getBlockZ());
      int bottomBlockZ = Math.min(location1.getBlockZ(), location2.getBlockZ());

      for(int x = bottomBlockX; x <= topBlockX; ++x) {
         for(int z = bottomBlockZ; z <= topBlockZ; ++z) {
            for(int y = bottomBlockY; y <= topBlockY; ++y) {
               locations.add(new Location(location1.getWorld(), (double)x, (double)y, (double)z));
            }
         }
      }

      return locations;
   }

   public List<Block> getBlocks(Location location1, Location location2) {
      List<Block> blocks = new ArrayList();
      Iterator var4 = this.getLocationsFromTwoPoints(location1, location2).iterator();

      while(var4.hasNext()) {
         Location loc = (Location)var4.next();
         Block b = loc.getBlock();
         if (!b.getType().equals(Material.AIR)) {
            blocks.add(b);
         }
      }

      return blocks;
   }
}