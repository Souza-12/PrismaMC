package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.ProxyServer;

public class AlertCommand implements CommandClass {
   @Command(
      name = "alert",
      aliases = {"aviso"},
      groupsToUse = {Groups.ADMIN}
   )
   public void alert(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length == 0) {
         commandSender.sendMessage("§cUtilize para anunciar em todos os servidores /alert <mensagem>");
      } else {
         StringBuilder builder = new StringBuilder();
         builder.append("§6§lCRAZZY §8» §f");
         String[] var5 = args;
         int var6 = args.length;

         for(int var7 = 0; var7 < var6; ++var7) {
            String s = var5[var7];
            builder.append(ChatColor.translateAlternateColorCodes('&', s));
            builder.append(" ");
         }

         String message = builder.substring(0, builder.length() - 1);
         ProxyServer.getInstance().broadcast(message);
      }
   }
}