package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.player.PlayerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class PingCommand implements CommandClass {
   @Command(
      name = "ping",
      aliases = {"p", "ms", "latencia"}
   )
   public void ping(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            player.sendMessage("§eSeu ping atual é: §b%quantia% ms".replace("%quantia%", "" + PlayerAPI.getPing(player)));
         } else if (args.length == 1) {
            if (!commandSender.hasPermission("ping")) {
               return;
            }

            Player p1 = Bukkit.getPlayer(args[0]);
            if (p1 == null) {
               player.sendMessage("§cJogador offline!");
               return;
            }

            player.sendMessage("§eO ping do player %nick%§f é de: §b%quantia% ms".replace("%nick%", p1.getName()).replace("%quantia%", "" + PlayerAPI.getPing(p1)));
         }

      }
   }
}