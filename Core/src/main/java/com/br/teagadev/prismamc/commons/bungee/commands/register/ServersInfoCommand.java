package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.GameServer;
import com.br.teagadev.prismamc.commons.common.serverinfo.types.NetworkServer;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ServersInfoCommand implements CommandClass {
   @Command(
      name = "serverInfo",
      aliases = {"si", "svinfo", "server"},
      groupsToUse = {Groups.DONO}
   )
   public void serverInfo(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUtilize: /serverinfo list");
      } else {
         if (args[0].equalsIgnoreCase("list")) {
            if (!commandSender.isPlayer()) {
               return;
            }

            ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
            proxiedPlayer.sendMessage("");
            List<NetworkServer> networks = new ArrayList(CommonsGeneral.getServersManager().getServers());
            Iterator var6 = networks.iterator();

            NetworkServer networkServer;
            while(var6.hasNext()) {
               networkServer = (NetworkServer)var6.next();
               if (networkServer.getServerId() == 1) {
                  TextComponent component = new TextComponent("§e[" + networkServer.getServerType().getName().toUpperCase() + networkServer.getServerId() + "]");
                  component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder("Online: " + (networkServer.isOnline() ? "§aSim" : "§cNão") + "\nOnlines: §7" + networkServer.getOnlines() + "/" + networkServer.getMaxPlayers())).create()));
                  proxiedPlayer.sendMessage(component);
               }
            }

            var6 = networks.iterator();

            while(var6.hasNext()) {
               networkServer = (NetworkServer)var6.next();
               if (networkServer instanceof GameServer && networkServer.getServerType() == ServerType.HARDCORE_GAMES) {
                  GameServer hardcoreGamesServer = (GameServer)networkServer;
                  TextComponent component = new TextComponent("§e[HG" + hardcoreGamesServer.getServerId() + "]");
                  component.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, (new ComponentBuilder("Online: " + (hardcoreGamesServer.isOnline() ? "§aSim" : "§cNão") + "\nOnlines: §7" + hardcoreGamesServer.getOnlines() + "/" + hardcoreGamesServer.getMaxPlayers() + "\nJogadores: §7" + hardcoreGamesServer.getPlayersGaming() + "\nTempo: §7" + DateUtils.formatSeconds(hardcoreGamesServer.getTempo()) + "\nEstágio: §7" + hardcoreGamesServer.getStage().getNome())).create()));
                  proxiedPlayer.sendMessage(component);
               }
            }

            proxiedPlayer.sendMessage("");
         }

      }
   }
}