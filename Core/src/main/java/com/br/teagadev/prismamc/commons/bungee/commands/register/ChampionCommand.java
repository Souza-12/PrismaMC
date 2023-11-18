package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.GameServer;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ChampionCommand implements CommandClass {
   @Command(
      name = "MiniCrazzy"
   )
   public void handlecommand(BungeeCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
         if (!ProxyServer.getInstance().getServers().containsKey("MiniCrazzy")) {
            proxiedPlayer.sendMessage("§cO servidor não existe.");
         } else if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("MiniCrazzy")) {
            commandSender.sendMessage("§cVocê já está no servidor da MiniCrazzy.");
         } else {
            GameServer server = CommonsGeneral.getServersManager().getHardcoreGamesServer("MiniCrazzy", 1);
            if (!server.isOnline()) {
               proxiedPlayer.sendMessage("§cO Quartel ainda não está liberado.");
            } else {
               proxiedPlayer.sendMessage("§aConectando...");
               proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("MiniCrazzy"));
            }

         }
      }
   }
}