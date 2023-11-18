package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import org.bukkit.Bukkit;

public class BroadCastCommand implements CommandClass {
   @Command(
      name = "broadcast",
      aliases = {"bc"},
      groupsToUse = {Groups.MOD}
   )
   public void broadcast(BukkitCommandSender commandSender, String label, String[] args) {
      if (args.length == 0) {
         commandSender.sendMessage("§cPara enviar um anúncio no seu servidor utilize: /bc (mensagem)");
      } else {
         Bukkit.broadcastMessage("§6§lCRAZZY §8» §f" + StringUtility.createArgs(0, args).replaceAll("&", "§"));
         if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " utilizou o BroadCast!]", Groups.ADMIN);
         }

      }
   }
}