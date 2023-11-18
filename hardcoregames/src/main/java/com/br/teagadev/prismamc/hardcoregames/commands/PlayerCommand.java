package com.br.teagadev.prismamc.hardcoregames.commands;

import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.title.TitleAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.ability.Kit;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import com.br.teagadev.prismamc.hardcoregames.manager.scoreboard.HardcoreGamesScoreboard;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;
import com.br.teagadev.prismamc.hardcoregames.utility.HardcoreGamesUtility;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class PlayerCommand implements CommandClass {
   @Command(
      name = "spawn"
   )
   public void spawn(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (!HardcoreGamesMain.getGameManager().isPreGame()) {
            commandSender.sendMessage("§cO jogo já iniciou!");
         } else if (HardcoreGamesMain.getTimerManager().getTime().get() <= 11) {
            commandSender.sendMessage("§cVocê não pode ir para o spawn agora!");
         } else {
            Player player = commandSender.getPlayer();
            player.teleport(HardcoreGamesUtility.getRandomLocation(30));
            player = null;
         }
      }
   }

   @Command(
      name = "specs",
      groupsToUse = {Groups.BETA}
   )
   public void specs(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (HardcoreGamesMain.getGameManager().isPreGame()) {
            player.sendMessage("§cO jogo ainda não iniciou!");
         } else if (args.length != 1) {
            commandSender.sendMessage("§cUtilize: /specs <On/Off>");
         } else {
            int escondidos;
            Iterator var6;
            Player onlines;
            if (args[0].equalsIgnoreCase("on")) {
               escondidos = 0;
               var6 = Bukkit.getOnlinePlayers().iterator();

               while(var6.hasNext()) {
                  onlines = (Player)var6.next();
                  if (!VanishAPI.inAdmin(onlines) && !VanishAPI.isInvisible(onlines) && !GamerManager.getGamer(onlines.getUniqueId()).isPlaying()) {
                     ++escondidos;
                     player.showPlayer(onlines);
                  }
               }

               if (escondidos != 0) {
                  player.sendMessage("§aEspectadores agora estão visiveis!");
               } else {
                  player.sendMessage("§cNenhum espectador encontrado!");
               }
            } else if (args[0].equalsIgnoreCase("off")) {
               escondidos = 0;
               var6 = Bukkit.getOnlinePlayers().iterator();

               while(var6.hasNext()) {
                  onlines = (Player)var6.next();
                  if (!GamerManager.getGamer(onlines.getUniqueId()).isPlaying()) {
                     ++escondidos;
                     player.hidePlayer(onlines);
                  }
               }

               if (escondidos != 0) {
                  player.sendMessage("§cEspectadores agora estão invisiveis!");
               } else {
                  player.sendMessage("§cNenhum espectador encontrado!");
               }
            } else {
               commandSender.sendMessage("§cUtilize: /specs <On/Off>");
            }

         }
      }
   }

   @Command(
      name = "desistir",
      groupsToUse = {Groups.BETA}
   )
   public void desistir(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (HardcoreGamesMain.getGameManager().isPreGame()) {
            commandSender.sendMessage("§cO jogo ainda não iniciou!");
         } else {
            Player player = commandSender.getPlayer();
            if (GamerManager.getGamer(player.getUniqueId()).isPlaying()) {
               player.sendMessage("§aVocê desistiu da partida!");
               PlayerAPI.dropItems(player, player.getLocation());
               HardcoreGamesMain.getGameManager().getGameType().setEspectador(player, false);
            } else {
               player.sendMessage("§eVocê não está jogando!");
            }

            player = null;
         }
      }
   }

   @Command(
      name = "feast"
   )
   public void feast(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (HardcoreGamesMain.getGameManager().isPreGame()) {
            player.sendMessage("§cO jogo ainda não iniciou!");
         } else if (!player.getInventory().contains(Material.COMPASS)) {
            player.sendMessage("§cVocê precisa ter uma bússola no inventário!");
         } else if (StructuresManager.getFeast().getLocation() == null) {
            player.sendMessage("§cO FEAST ainda não spawnou!");
         } else {
            player.setCompassTarget(StructuresManager.getFeast().getLocation());
            player.sendMessage("§eBússola apontada para o FEAST!");
            player = null;
         }
      }
   }

   @Command(
      name = "kit"
   )
   public void kit(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            List<String> kits = null;
            if (!HardcoreGamesOptions.DOUBLE_KIT) {
               kits = (List)KitManager.getPlayerKits(player).stream().map(Kit::getName).collect(Collectors.toList());
            } else {
               kits = (List)KitManager.getAllKits().stream().map(Kit::getName).collect(Collectors.toList());
            }

            TextComponent text = new TextComponent("§a" + (HardcoreGamesOptions.DOUBLE_KIT ? "Todos os Kits" : "Seus Kits") + " (" + kits.size() + "): ");

            for(int i = 0; i < kits.size(); ++i) {
               String kit = (String)kits.get(i);
               text.addExtra(i == 0 ? "" : ", ");
               text.addExtra(this.buildKitComponent(KitManager.getKitInfo(kit.toLowerCase()), true));
            }

            player.spigot().sendMessage(text);
            player.sendMessage("");
            text = null;
            kits.clear();
            kits = null;
         } else if (args.length == 1) {
            String kit = args[0].toLowerCase();
            if (!KitManager.getKits().containsKey(kit)) {
               player.sendMessage("§cEste kit não existe!");
               return;
            }

            if (HardcoreGamesOptions.KITS_DISABLEDS) {
               player.sendMessage("§cTodos os kits estão desativados!");
               return;
            }

            if (KitManager.getKitsDesativados().contains(kit.toLowerCase())) {
               player.sendMessage("§cEste kit está desativado!!");
               return;
            }

            if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
               player.sendMessage("§cVocê já está com esse kit");
               return;
            }

            if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
               player.sendMessage("§cVocê já está com esse kit");
               return;
            }

            if (KitManager.hasCombinationOp(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
               player.sendMessage("§cEsta combinação de kit está bloqueada!");
               return;
            }

            boolean hasPermission = true;
            if (!HardcoreGamesOptions.DOUBLE_KIT) {
               hasPermission = KitManager.hasPermissionKit(player, kit, true);
            }

            if (!hasPermission) {
               return;
            }

            kit = KitManager.getKitInfo(kit).getName();
            if (HardcoreGamesMain.getGameManager().isPreGame()) {
               GamerManager.getGamer(player.getUniqueId()).setKit1(kit);
               player.sendMessage("§aVocê selecionou o kit §b%kit%".replace("%kit%", kit));
               TitleAPI.sendTitle(player, "§b%kit%".replace("%kit%", kit), "§aSelecionado!", 0, 0, 5);
               HardcoreGamesScoreboard.getScoreBoardCommon().updateKit1(player, kit);
            } else if (player.hasPermission("hardcoregames.pegarkit")) {
               if (HardcoreGamesMain.getTimerManager().getTime().get() <= 300) {
                  if (GamerManager.getGamer(player.getUniqueId()).getKit1().equalsIgnoreCase("Nenhum")) {
                     player.sendMessage("§aVocê selecionou o kit §b%kit%".replace("%kit%", kit));
                     TitleAPI.sendTitle(player, "§b%kit%".replace("%kit%", kit), "§aSelecionado!", 0, 0, 5);
                     KitManager.handleKitSelect(player, true, kit);
                  } else {
                     player.sendMessage("§cVocê já está com um kit selecionado!");
                  }
               } else {
                  player.sendMessage("§cO tempo para pegar kit sendo VIP esgotou.");
               }
            } else {
               player.sendMessage("§cVocê não tem permissão para pegar kit após a partida ter iniciado!");
            }
         }

      }
   }

   @Command(
      name = "kit2"
   )
   public void kit2(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (!HardcoreGamesOptions.DOUBLE_KIT) {
            commandSender.sendMessage("§cEsta partida está habilitada apenas o primeiro KIT.");
         } else {
            Player player = commandSender.getPlayer();
            if (args.length == 0) {
               List<String> kits = (List)KitManager.getPlayerKits(player).stream().map(Kit::getName).collect(Collectors.toList());
               TextComponent text = new TextComponent("§aSeus Kits (" + kits.size() + "): ");

               for(int i = 0; i < kits.size(); ++i) {
                  String kit = (String)kits.get(i);
                  text.addExtra(i == 0 ? "" : ", ");
                  text.addExtra(this.buildKitComponent(KitManager.getKitInfo(kit.toLowerCase()), false));
               }

               player.spigot().sendMessage(text);
               player.sendMessage("");
               kits.clear();
            } else if (args.length == 1) {
               String kit = args[0].toLowerCase();
               if (!KitManager.getKits().containsKey(kit)) {
                  player.sendMessage("§cEste kit não existe!");
                  return;
               }

               if (HardcoreGamesOptions.KITS_DISABLEDS) {
                  player.sendMessage("§cTodos os kits estão desativados!");
                  return;
               }

               if (KitManager.getKitsDesativados().contains(kit.toLowerCase())) {
                  player.sendMessage("§cEste kit está desativado!!");
                  return;
               }

               if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
                  player.sendMessage("§cVocê já está com esse kit");
                  return;
               }

               if (KitManager.isSameKit(kit, GamerManager.getGamer(player.getUniqueId()).getKit2())) {
                  player.sendMessage("§cVocê já está com esse kit");
                  return;
               }

               if (KitManager.hasCombinationOp(kit, GamerManager.getGamer(player.getUniqueId()).getKit1())) {
                  player.sendMessage("§cEsta combinação de kit está bloqueada!");
                  return;
               }

               if (!KitManager.hasPermissionKit(player, kit, true)) {
                  return;
               }

               kit = KitManager.getKitInfo(kit).getName();
               if (HardcoreGamesMain.getGameManager().isPreGame()) {
                  GamerManager.getGamer(player.getUniqueId()).setKit2(kit);
                  player.sendMessage("§aVocê selecionou o kit §b%kit%".replace("%kit%", kit));
                  TitleAPI.sendTitle(player, "§b%kit%".replace("%kit%", kit), "§aSelecionado!", 0, 0, 5);
                  HardcoreGamesScoreboard.getScoreBoardCommon().updateKit2(player, kit);
               } else if (player.hasPermission("hardcoregames.pegarkit")) {
                  if (HardcoreGamesMain.getTimerManager().getTime().get() <= 300) {
                     if (GamerManager.getGamer(player.getUniqueId()).getKit2().equalsIgnoreCase("Nenhum")) {
                        player.sendMessage("§aVocê selecionou o kit §b%kit%".replace("%kit%", kit));
                        TitleAPI.sendTitle(player, "§b%kit%".replace("%kit%", kit), "§aSelecionado!", 0, 0, 5);
                        KitManager.handleKitSelect(player, false, kit);
                     } else {
                        player.sendMessage("§cVocê já está com um kit selecionado!");
                     }
                  } else {
                     player.sendMessage("§cO tempo para pegar kit sendo VIP esgotou.");
                  }
               } else {
                  player.sendMessage("§cVocê não tem permissão para pegar kit após a partida ter iniciado!");
               }
            }

         }
      }
   }

   @Completer(
      name = "kit",
      aliases = {"kit2"}
   )
   public List<String> kitCompleter(BukkitCommandSender sender, String label, String[] args) {
      List<String> list = new ArrayList();
      if (args.length == 1) {
         String search = args[0].toLowerCase();
         Iterator var6 = KitManager.getAllKits().iterator();

         while(var6.hasNext()) {
            Kit kit = (Kit)var6.next();
            if (kit.getName().toLowerCase().startsWith(search)) {
               list.add(kit.getName());
            }
         }
      }

      return list;
   }

   private BaseComponent buildKitComponent(Kit kit, boolean primary) {
      BaseComponent baseComponent = new TextComponent("§6" + kit.getName());
      BaseComponent descComponent = new TextComponent("§eDescrição: \n");
      Iterator var5 = kit.getDescription().iterator();

      while(var5.hasNext()) {
         String desc = (String)var5.next();
         descComponent.addExtra(desc.replaceAll("&", "§") + "\n");
      }

      baseComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[]{descComponent, new TextComponent("\n"), new TextComponent("§aClique para selecionar!")}));
      baseComponent.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, (primary ? "/kit " : "/kit2 ") + kit.getName()));
      return baseComponent;
   }
}