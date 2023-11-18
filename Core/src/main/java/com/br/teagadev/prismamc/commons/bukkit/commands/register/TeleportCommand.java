package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TeleportCommand implements CommandClass {
   @Command(
      name = "tp",
      aliases = {"teleport"},
      groupsToUse = {Groups.MOD}
   )
   public void tp(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length == 0) {
            commandSender.sendMessage("§cUtilize: /tp <Nick>\n§cUtilize: /tp <Player1> <Player2>\n§cUtilize: /tp <X> <Y> <Z>\n§cUtilize: /tp <Nick> <X> <Y> <Z>");
         } else {
            Player target;
            Player player2;
            if (args.length == 1) {
               target = Bukkit.getServer().getPlayer(args[0]);
               if (target == null) {
                  commandSender.sendMessage("§cJogador offline!");
                  return;
               }

               player2 = commandSender.getPlayer();
               player2.setFallDistance(-5.0F);
               player2.teleport(target.getLocation());
               commandSender.sendMessage("§aVocê foi teleportado para §7§l%nick% §acom sucesso!".replace("%nick%", target.getName()));
               BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player2) + " se teleportou para o " + BukkitServerAPI.getRealNick(target) + "]", Groups.ADMIN);
               target = null;
               player2 = null;
            } else if (args.length == 2) {
               target = Bukkit.getServer().getPlayer(args[0]);
               player2 = Bukkit.getServer().getPlayer(args[1]);
               if (target == null || player2 == null) {
                  commandSender.sendMessage("§cJogador offline!");
                  return;
               }

               target.setFallDistance(-5.0F);
               target.teleport(player2.getLocation());
               commandSender.sendMessage("§cVocê teleportou §7%nick% §apara o §7%nick1%".replace("%nick%", target.getName()).replace("%nick1%", player2.getName()));
               BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(target) + " foi teleportado para o " + BukkitServerAPI.getRealNick(player2) + " pelo " + commandSender.getNick() + "]", Groups.ADMIN);
               target = null;
               player2 = null;
            } else {
               int y;
               Location loc;
               int x;
               if (args.length == 3) {
                  if (!StringUtility.isInteger(args[0]) || !StringUtility.isInteger(args[1]) || !StringUtility.isInteger(args[2])) {
                     commandSender.sendMessage("§cUtilize: /tp <Nick>\n§cUtilize: /tp <Player1> <Player2>\n§cUtilize: /tp <X> <Y> <Z>\n§cUtilize: /tp <Nick> <X> <Y> <Z>");
                     return;
                  }

                  int x1 = Integer.parseInt(args[0]);
                  x1 = Integer.parseInt(args[1]);
                  y = Integer.parseInt(args[2]);
                  Player player = commandSender.getPlayer();
                  loc = new Location(player.getWorld(), (double)x1 + 0.5D, (double)x1, (double)y + 0.5D);
                  player.setFallDistance(-5.0F);
                  player.teleport(loc);
                  commandSender.sendMessage("§aVocê se teleportou para §7%coords%".replace("%coords%", "x " + x1 + ", y " + x1 + ", z " + y));
                  BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " se teleportou para as coordenadas: " + x1 + ", " + x1 + ", " + y + "]", Groups.ADMIN);
                  loc = null;
                  player = null;
               } else if (args.length == 4) {
                  target = Bukkit.getServer().getPlayer(args[0]);
                  if (target == null) {
                     commandSender.sendMessage("§cJogador offline!");
                     return;
                  }

                  if (!StringUtility.isInteger(args[1]) || !StringUtility.isInteger(args[2]) || !StringUtility.isInteger(args[3])) {
                     commandSender.sendMessage("§cUtilize: /tp <Nick>\n§cUtilize: /tp <Player1> <Player2>\n§cUtilize: /tp <X> <Y> <Z>\n§cUtilize: /tp <Nick> <X> <Y> <Z>");
                     return;
                  }

                  x = Integer.parseInt(args[1]);
                  y = Integer.parseInt(args[2]);
                  int z = Integer.parseInt(args[3]);
                  loc = new Location(target.getWorld(), (double)x + 0.5D, (double)y, (double)z + 0.5D);
                  target.setFallDistance(-5.0F);
                  target.teleport(loc);
                  commandSender.sendMessage("§aVocê teleportou o §7%nick% §apara §7%coords%".replace("%nick%", target.getName()).replace("%coords%", "x " + x + ", y " + y + ", z " + z));
                  BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(target) + " foi teleportado para as coordenadas: " + x + ", " + y + ", " + z + " pelo " + commandSender.getNick() + "]", Groups.ADMIN);
                  target = null;
                  loc = null;
               } else {
                  commandSender.sendMessage("§cUtilize: /tp <Nick>\n§cUtilize: /tp <Player1> <Player2>\n§cUtilize: /tp <X> <Y> <Z>\n§cUtilize: /tp <Nick> <X> <Y> <Z>");
               }
            }
         }

      }
   }
}