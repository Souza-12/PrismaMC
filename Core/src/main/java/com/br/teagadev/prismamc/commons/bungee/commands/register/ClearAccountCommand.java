package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.sql.SQLException;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class ClearAccountCommand implements CommandClass {
   private static final String[] tablesToRemove = new String[]{"registros", "accounts", "hardcoregames", "kitpvp", "premium_map", "accounts_to_delete", "cosmetics", "preferences"};

   public static void clearAccount(String nick) {
      BungeeMain.runLater(() -> {
         BungeeMain.runAsync(() -> {
            String[] var1 = tablesToRemove;
            int var2 = var1.length;

            for(int var3 = 0; var3 < var2; ++var3) {
               String table = var1[var3];
               MySQLManager.deleteFromTable(table, "nick", nick);
            }

         });
      }, 1, TimeUnit.SECONDS);
   }

   @Command(
      name = "clearaccount",
      aliases = {"removeaccount"},
      groupsToUse = {Groups.DONO}
   )
   public void clearaccount(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length == 1) {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cEste jogador não possuí uma conta no servidor.");
            return;
         }

         DataHandler dataHandler = null;
         if (CommonsGeneral.getProfileManager().containsProfile(nick)) {
            dataHandler = BungeeMain.getBungeePlayer(nick).getDataHandler();
         } else {
            dataHandler = new DataHandler(nick, (UUID)null);

            try {
               dataHandler.load(new DataCategory[]{DataCategory.ACCOUNT});
            } catch (SQLException var7) {
               dataHandler = null;
               var7.printStackTrace();
               commandSender.sendMessage("§cOcorreu um erro ao tentar carregar a conta do jogador.");
            }
         }

         if (dataHandler == null) {
            return;
         }

         CommonsGeneral.getProfileManager().removeGamingProfile(CommonsGeneral.getUUIDFetcher().getOfflineUUID(nick));
         ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
         if (target != null) {
            target.disconnect("§4§lAVISO\n\n§fTodos os seus dados foram apagados do servidor.");
         }

         BungeeMain.console(commandSender.getNick() + " deletou todos os dados do usuario: §7" + nick);
         commandSender.sendMessage("§aConta de §7'§a" + nick + "§7' apagada com sucesso!");
         clearAccount(nick);
      } else {
         commandSender.sendMessage("§cUtilize: /clearaccount <Nick>");
      }

   }
}