package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.account.permission.BungeePlayerPermissions;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import java.sql.SQLException;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class GroupCommand implements CommandClass {
   @Command(
      name = "group",
      aliases = {"groups", "setgroup", "groupset"},
      groupsToUse = {Groups.ADMIN},
      runAsync = true
   )
   public void group(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length < 2) {
         commandSender.sendMessage("§cUtilize: /setgroup <Nick> <Grupo>§cUtilize: /setgroup <Nick> <Grupo> <Duração>");
      } else {
         String playerNick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (playerNick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
         } else {
            if (!Groups.existGrupo(args[1])) {
               commandSender.sendMessage("§cEsse grupo não existe!");
               return;
            }

            Groups group = Groups.getGroup(args[1]);
            if (commandSender.isPlayer()) {
               BungeePlayer bp = BungeeMain.getBungeePlayer(commandSender.getPlayer().getName());
               if (group.getLevel() > bp.getGroup().getLevel()) {
                  commandSender.sendMessage("§cEsse cargo é maior do que o seu.");
                  return;
               }
            }

            long tempo = 0L;
            if (args.length > 2) {
               try {
                  tempo = DateUtils.parseDateDiff(args[2], true);
               } catch (Exception var12) {
                  commandSender.sendMessage("§cTempo inválido.");
                  return;
               }
            }

            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(playerNick);
            DataHandler dataHandler;
            if (target != null) {
               dataHandler = CommonsGeneral.getProfileManager().getGamingProfile(target.getName()).getDataHandler();
               dataHandler.set(DataType.GROUP, group.getName());
               dataHandler.set(DataType.GROUP_TIME, tempo);
               dataHandler.set(DataType.GROUP_CHANGED_BY, commandSender.getNick());
               CPacketCustomAction PACKET = (new CPacketCustomAction(target.getName(), target.getUniqueId())).type(PacketType.BUNGEE_SEND_UPDATED_STATS).field("group");
               PACKET.getJson().addProperty("dataCategory-1", dataHandler.buildJSON(DataCategory.ACCOUNT, true).toString());
               Server.getInstance().sendPacket(target.getServer().getInfo().getName(), PACKET);
               target.sendMessage("§aVocê recebeu o rank %cargo% §acom a duração %duração%".replace("%cargo%", group.getColor() + group.getName().toUpperCase()).replace("%duração%", tempo == 0L ? "§aETERNA" : "de §a" + DateUtils.formatDifference(tempo)));
               BungeePlayerPermissions.clearPermissions(target);
               BungeePlayerPermissions.injectPermissions(target, group.getName());
               dataHandler.saveCategory(DataCategory.ACCOUNT);
            } else {
               dataHandler = this.getDataHandlerByPlayer(playerNick);
               if (dataHandler == null) {
                  commandSender.sendMessage("§cOcorreu um erro!");
                  return;
               }

               dataHandler.set(DataType.GROUP, group.getName());
               dataHandler.set(DataType.GROUP_TIME, tempo);
               dataHandler.set(DataType.GROUP_CHANGED_BY, commandSender.getNick());
               dataHandler.saveCategory(DataCategory.ACCOUNT);
               boolean var10 = true;
            }

            String message = "§7[O jogador %nick% recebeu o cargo %cargo% §7pelo %setou% com duração %duração%]".replace("%nick%", playerNick).replace("%setou%", commandSender.getName()).replace("%cargo%", group.getColor() + group.getName().toUpperCase()).replace("%duração%", tempo == 0L ? "§aETERNA" : "de §a" + DateUtils.formatDifference(tempo));
            commandSender.sendMessage("§aVocê adicionou o rank %cargo% §ano jogador %nick%".replace("%cargo%", group.getColor() + group.getName().toUpperCase()).replace("%nick%", playerNick));
            BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);
         }
      }

   }

   private DataHandler getDataHandlerByPlayer(String nick) {
      DataHandler dataHandler = null;

      try {
         dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
      } catch (UUIDFetcherException var5) {
         var5.printStackTrace();
         return null;
      }

      try {
         dataHandler.load(new DataCategory[]{DataCategory.ACCOUNT});
      } catch (SQLException var4) {
         BungeeMain.console("§cOcorreu um erro ao tentar carregar a categoria 'ACCOUNT' de um jogador -> " + var4.getLocalizedMessage());
         dataHandler = null;
      }

      return dataHandler;
   }
}