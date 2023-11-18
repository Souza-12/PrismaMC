package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.BungeeMessages;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class KickCommand implements CommandClass {
   @Command(
      name = "kick",
      aliases = {"expulsar"},
      groupsToUse = {Groups.YOUTUBER_PLUS}
   )
   public void kick(BungeeCommandSender commandSender, String label, String[] args) {
      String expulsou = commandSender.getNick();
      if (args.length == 0) {
         commandSender.sendMessage("§cUtilize /kick (player) (motivo)");
      } else {
         String nick = args[0];
         ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
         if (target == null) {
            commandSender.sendMessage("§fJogador offline!");
         } else {
            boolean kick = true;
            if (!expulsou.equalsIgnoreCase("CONSOLE") && BungeeMain.isValid(target)) {
               BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(target.getName());
               BungeePlayer proxyExpulsou = BungeeMain.getBungeePlayer(commandSender.getPlayer().getName());
               if (proxyPlayer.getGroup().getLevel() > proxyExpulsou.getGroup().getLevel()) {
                  commandSender.sendMessage("§cVocê não pode expulsar alguém com o grupo §6§lSUPERIOR §cque o seu.");
                  kick = false;
               }
            }

            if (kick) {
               String motivo = "Não especificado";
               if (args.length >= 2) {
                  motivo = StringUtility.createArgs(1, args);
               }

               commandSender.sendMessage("§aJogador(a) expulso(a) com sucesso!");
               target.disconnect(BungeeMessages.VOCE_FOI_EXPULSO.replace("%expulsou%", expulsou).replace("%motivo%", motivo));
            }
         }
      }
   }
}