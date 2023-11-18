package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;

public class GameModeCommand implements CommandClass {
   @Command(
      name = "gamemode",
      aliases = {"gm"},
      groupsToUse = {Groups.MOD}
   )
   public void gm(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            this.changeGameMode(player);
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o seu GameMode para " + player.getGameMode().name() + "]", Groups.ADMIN);
         } else {
            if (args.length == 1) {
               Player target = BukkitServerAPI.getExactPlayerByNick(args[0]);
               if (target == null) {
                  if (args[0].equalsIgnoreCase("0")) {
                     this.changeGameMode(player, GameMode.SURVIVAL);
                  } else if (args[0].equalsIgnoreCase("1")) {
                     this.changeGameMode(player, GameMode.CREATIVE);
                  } else if (args[0].equalsIgnoreCase("2")) {
                     this.changeGameMode(player, GameMode.ADVENTURE);
                  } else if (args[0].equalsIgnoreCase("3")) {
                     this.changeGameMode(player, GameMode.SPECTATOR);
                  } else {
                     this.changeGameMode(player);
                  }

                  BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o seu GameMode para " + player.getGameMode().name() + "]", Groups.ADMIN);
                  return;
               }

               this.changeGameMode(target);
               player.sendMessage("§eVocê alterou o modo de jogo do(a) jogador(a) %nick% §apara o modo %gamemode%".replace("%nick%", target.getName()).replace("%gamemode%", target.getGameMode().name()));
               BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " alterou o GameMode para de " + BukkitServerAPI.getRealNick(target) + " para " + target.getGameMode().name() + "]", Groups.ADMIN);
            }

         }
      }
   }

   private void changeGameMode(Player player) {
      this.changeGameMode(player, (GameMode)null);
   }

   private void changeGameMode(Player player, GameMode preference) {
      if (preference != null && preference == player.getGameMode()) {
         player.sendMessage("§cVocê ja está nesse modo de jogo!");
      } else {
         if (preference == null) {
            if (player.getGameMode() == GameMode.CREATIVE) {
               preference = GameMode.SURVIVAL;
            } else {
               preference = GameMode.CREATIVE;
            }
         }

         player.setGameMode(preference);
         player.sendMessage("§aSeu modo de jogo foi alterado para %gamemode%".replace("%gamemode%", preference.name()));
      }
   }
}