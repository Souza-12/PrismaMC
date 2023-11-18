package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FlyCommand implements CommandClass {
   @Command(
      name = "fly",
      groupsToUse = {Groups.SAPPHIRE}
   )
   public void fly(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            this.changeFly(player);
         } else {
            Player target = Bukkit.getPlayer(args[0]);
            if (target == null) {
               commandSender.sendMessage("§cJogador offline!");
               return;
            }

            if (target.getAllowFlight()) {
               player.sendMessage("§eO modo voar foi desativado para §b%nick%".replace("%nick%", target.getName()));
            } else {
               player.sendMessage("§eO modo voar foi ativado para §b%nick%".replace("%nick%", target.getName()));
            }

            this.changeFly(target);
            target = null;
         }

      }
   }

   public void changeFly(Player player) {
      if (player.getAllowFlight()) {
         player.setAllowFlight(false);
         player.sendMessage("§cO modo voar foi desativado.");
      } else {
         player.setAllowFlight(true);
         player.sendMessage("§aO modo voar foi ativado.");
      }

   }
}