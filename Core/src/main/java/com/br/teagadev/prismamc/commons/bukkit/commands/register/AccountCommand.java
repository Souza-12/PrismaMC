package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.menu.MenuListener;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.menu.AccountInventory;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import org.bukkit.entity.Player;

public class AccountCommand implements CommandClass {
   @Command(
      name = "account",
      aliases = {"acc", "perfil", "conta", "stats", "info"},
      runAsync = true
   )
   public void account(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (!MenuListener.isOpenMenus()) {
            commandSender.sendMessage("§cVocê não pode abrir menus agora.");
         } else {
            Player player = commandSender.getPlayer();
            String nickViewer = BukkitServerAPI.getRealNick(player);
            if (args.length == 1) {
               if (!commandSender.hasPermission("account")) {
                  return;
               }

               String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
               if (nick.equalsIgnoreCase("N/A")) {
                  commandSender.sendMessage("§cO nickname citado não possuí um registro na rede.");
                  return;
               }

               (new AccountInventory(nickViewer, nick)).open(player);
            } else {
               (new AccountInventory(nickViewer, nickViewer)).open(player);
            }

         }
      }
   }
}