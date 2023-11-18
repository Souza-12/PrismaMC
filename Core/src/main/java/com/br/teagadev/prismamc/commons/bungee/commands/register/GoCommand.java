package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GoCommand implements CommandClass {
   @Command(
      name = "go",
      aliases = {"ir"},
      groupsToUse = {Groups.TRIAL}
   )
   public void go(BungeeCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
         if (args.length != 1) {
            proxiedPlayer.sendMessage("§cUtilize: /go <Nick>");
         } else {
            ProxiedPlayer player = ProxyServer.getInstance().getPlayer(args[0]);
            if (player != null && player.getServer() != null) {
               proxiedPlayer.sendMessage("§aConectando...");
               if (proxiedPlayer.getServer().getInfo().getName().equals(player.getServer().getInfo().getName())) {
                  Server.getInstance().sendPacket(player.getServer().getInfo().getName(), (new CPacketCustomAction(player.getName(), player.getUniqueId())).type(PacketType.BUKKIT_GO).field(proxiedPlayer.getName()));
               } else {
                  proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo(player.getServer().getInfo().getName()), (ignored, throwable) -> {
                     BungeeMain.runLater(() -> {
                        Server.getInstance().sendPacket(player.getServer().getInfo().getName(), (new CPacketCustomAction(player.getName(), player.getUniqueId())).type(PacketType.BUKKIT_GO).field(proxiedPlayer.getName()));
                     }, 333, TimeUnit.MILLISECONDS);
                  });
               }
            } else {
               proxiedPlayer.sendMessage("§fJogador offline!");
            }

         }
      }
   }
}