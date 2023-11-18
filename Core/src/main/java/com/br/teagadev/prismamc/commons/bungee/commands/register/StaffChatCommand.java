package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class StaffChatCommand implements CommandClass {
   @Command(
      name = "staffchat",
      aliases = {"sc"},
      groupsToUse = {Groups.TRIAL}
   )
   public void staffchat(BungeeCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         ProxiedPlayer proxiedPlayer = commandSender.getPlayer();
         BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(proxiedPlayer.getName());
         if (args.length == 1) {
            if (args[0].equalsIgnoreCase("on")) {
               if (proxyPlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                  proxiedPlayer.sendMessage("§aO StaffChat já está ativado!");
                  return;
               }

               proxyPlayer.set(DataType.RECEIVE_STAFFCHAT_MESSAGES, true);
               BungeeMain.runAsync(() -> {
                  proxyPlayer.getDataHandler().saveCategory(DataCategory.PREFERENCES);
               });
               proxiedPlayer.sendMessage("§aVocê ativou o StaffChat.");
            } else if (args[0].equalsIgnoreCase("off")) {
               if (!proxyPlayer.getBoolean(DataType.RECEIVE_STAFFCHAT_MESSAGES)) {
                  proxiedPlayer.sendMessage("§cO StaffChat já está desativado!");
                  return;
               }

               proxyPlayer.set(DataType.RECEIVE_STAFFCHAT_MESSAGES, false);
               BungeeMain.runAsync(() -> {
                  proxyPlayer.getDataHandler().saveCategory(DataCategory.PREFERENCES);
               });
               proxiedPlayer.sendMessage("§cVocê desativou o StaffChat.");
            } else {
               proxiedPlayer.sendMessage("");
               proxiedPlayer.sendMessage("§cUse: /staffchat");
               proxiedPlayer.sendMessage("§cUse: /staffchat <On/Off>");
               proxiedPlayer.sendMessage("");
            }

         } else {
            if (proxyPlayer.isInStaffChat()) {
               proxyPlayer.setInStaffChat(false);
               proxiedPlayer.sendMessage("§cVocê saiu do staffchat.");
            } else {
               proxyPlayer.setInStaffChat(true);
               proxiedPlayer.sendMessage("§aVocê entrou no staffchat.");
            }

         }
      }
   }
}