package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCompleter implements CommandClass {
   @Completer(
      name = "ban",
      aliases = {"mute", "find", "kick", "premium", "setgroup", "group", "unmute", "report", "tempban", "go", "addcoins", "addxp", "addperm"}
   )
   public List<String> multiCompleter(ProxiedPlayer p, String label, String[] args) {
      if (args.length < 1) {
         return (List)BungeeCord.getInstance().getPlayers().stream().map(CommandSender::getName).collect(Collectors.toList());
      } else {
         return args.length > 1 ? Collections.emptyList() : (List)BungeeCord.getInstance().getPlayers().stream().map(CommandSender::getName).filter((name) -> {
            return name.toLowerCase().startsWith(args[0].toLowerCase());
         }).collect(Collectors.toList());
      }
   }
}