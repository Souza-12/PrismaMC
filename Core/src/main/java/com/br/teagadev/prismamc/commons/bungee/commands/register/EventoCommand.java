package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.ClickEvent.Action;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class EventoCommand implements CommandClass {
   public static boolean hasEvento = false;

   @Command(
      name = "evento"
   )
   public void evento(BungeeCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
         if (!hasEvento && !BungeeMain.getBungeePlayer(proxiedPlayer.getName()).isStaffer()) {
            commandSender.sendMessage("§eNenhum evento está disponivel.");
         } else if (proxiedPlayer.getServer().getInfo().getName().equalsIgnoreCase("evento")) {
            commandSender.sendMessage("§cVocê já está no evento.");
         } else {
            if (ProxyServer.getInstance().getServers().containsKey("Evento")) {
               proxiedPlayer.sendMessage("§aConectando...");
               proxiedPlayer.connect(ProxyServer.getInstance().getServerInfo("Evento"));
            } else {
               proxiedPlayer.sendMessage("§cO servidor não existe.");
            }

         }
      }
   }

   @Command(
      name = "eventomanager",
      groupsToUse = {Groups.MOD_PLUS}
   )
   public void eventomanager(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length == 0) {
         commandSender.sendMessage("§cUtilize o comando /eventomanager (on) ou (off) para liberar a entrada de players.");
      } else {
         if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
               if (hasEvento) {
                  commandSender.sendMessage("§aO evento já está liberado.");
                  return;
               }

               hasEvento = true;
               commandSender.sendMessage("§aVocê liberou a entrada de jogadores.");
               TextComponent message = new TextComponent("\n§eO servidor de §b§lEVENTO §e foi liberado!\n\n§aevento.crazzymc.com\n\n§7Clique para conectar-se.");
               message.setClickEvent(new ClickEvent(Action.RUN_COMMAND, "/evento"));
               message.setHoverEvent(new HoverEvent(net.md_5.bungee.api.chat.HoverEvent.Action.SHOW_TEXT, new TextComponent[]{new TextComponent("§aClique para se conectar")}));
               BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(""));
               BungeeCord.getInstance().broadcast(message);
               BungeeCord.getInstance().broadcast(TextComponent.fromLegacyText(""));
            } else if (args[0].equalsIgnoreCase("off")) {
               if (!hasEvento) {
                  commandSender.sendMessage("§cO evento não está liberado.");
                  return;
               }

               hasEvento = false;
               commandSender.sendMessage("§aVocê liberou apenas staffers para entrarem na sala de eventos.");
            } else {
               commandSender.sendMessage("§cUtilize o comando /eventomanager (on) ou (off) para liberar a entrada de players.");
            }
         }

      }
   }
}