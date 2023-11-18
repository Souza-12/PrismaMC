package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import java.sql.SQLException;
import java.util.List;
import java.util.UUID;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class AddPermCommand implements CommandClass {
   @Command(
      name = "addperm",
      groupsToUse = {Groups.DONO},
      runAsync = true
   )
   public void addperm(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length != 2) {
         commandSender.sendMessage("§cUse: /addperm <Nick> <Permissao>");
      } else {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
         } else {
            String permission = args[1].toLowerCase();
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
            String replace = "§aVocê adicionou a permissão §f%permissao% para §a%nick%".replace("%permissao%", permission).replace("%nick%", nick);
            List playerPermissionsList;
            if (target == null) {
               DataHandler dataHandler = new DataHandler(nick, (UUID)null);

               try {
                  dataHandler.load(new DataCategory[]{DataCategory.ACCOUNT});
               } catch (SQLException var11) {
                  commandSender.sendMessage("§cOcorreu um erro ao tentar carregar a conta do jogador.");
                  BungeeMain.console("Ocorreu um erro ao tentar carregar a conta de um jogador (AddPermCommand) -> " + var11.getLocalizedMessage());
                  return;
               }

               playerPermissionsList = dataHandler.getData(DataType.PERMISSIONS).getList();
               if (!playerPermissionsList.contains(permission)) {
                  playerPermissionsList.add(permission);
                  dataHandler.saveCategory(DataCategory.ACCOUNT);
               }

               commandSender.sendMessage(replace);
            } else {
               BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(target.getName());
               playerPermissionsList = bungeePlayer.getData(DataType.PERMISSIONS).getList();
               if (!playerPermissionsList.contains(permission)) {
                  playerPermissionsList.add(permission);
                  bungeePlayer.getData(DataType.PERMISSIONS).setValue(playerPermissionsList);
                  CPacketCustomAction PACKET = (new CPacketCustomAction(target.getName(), target.getUniqueId())).type(PacketType.BUNGEE_SEND_UPDATED_STATS).field("add-perm").fieldValue(permission);
                  PACKET.getJson().addProperty("dataCategory-1", bungeePlayer.getDataHandler().buildJSON(DataCategory.ACCOUNT, true).toString());
                  Server.getInstance().sendPacket(target.getServer().getInfo().getName(), PACKET);
               }

               commandSender.sendMessage(replace);
            }

         }
      }
   }
}