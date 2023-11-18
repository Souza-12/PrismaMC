package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.bungee.manager.premium.PremiumMapManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import java.sql.SQLException;

public class CreateAccountCommand implements CommandClass {
   @Command(
      name = "createaccount",
      aliases = {"createacc"},
      groupsToUse = {Groups.DONO},
      runAsync = true
   )
   public void createaccount(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length == 1) {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (!nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cJá existe uma conta criada com este nick.");
            return;
         }

         try {
            DataHandler dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
            dataHandler.load(new DataCategory[]{DataCategory.ACCOUNT});
            PremiumMapManager.load(nick);
         } catch (SQLException | UUIDFetcherException var6) {
            commandSender.sendMessage("§cOcorreu um erro ao tentar criar a conta, tente novamente.");
            var6.printStackTrace();
            return;
         }

         commandSender.sendMessage("§aConta criada com sucesso.");
      } else {
         commandSender.sendMessage("§cUtilize: /createaccount <Nick>");
      }

   }
}