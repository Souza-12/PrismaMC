package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class FindCommand implements CommandClass {
   @Command(
      name = "find",
      aliases = {"procurar"},
      groupsToUse = {Groups.TRIAL}
   )
   public void find(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUse: /find <Nick>");
      } else {
         ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
         if (player != null && player.getServer() != null) {
            commandSender.sendMessage("§eJogador encontrado!\n§eOnline no servidor: §b%servidor%".replace("%servidor%", player.getServer().getInfo().getName()));
         } else {
            commandSender.sendMessage("§fJogador offline!");
         }

      }
   }
}