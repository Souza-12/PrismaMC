package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.config.ServerInfo;

public class ServiceCommand implements CommandClass {
   @Command(
      name = "addserver",
      aliases = {"ds", "dsv"},
      groupsToUse = {Groups.DONO}
   )
   public void serverInfo(BungeeCommandSender commandSender, String label, String[] args) {
      String serverName;
      ServerInfo address;
      if (args.length == 2) {
         if (args[0].equalsIgnoreCase("remove")) {
            serverName = args[1];
            if (ProxyServer.getInstance().getServers().containsKey(serverName)) {
               address = ProxyServer.getInstance().getServerInfo(serverName);
               if (address != null) {
                  if (address.getPlayers().size() == 0) {
                     commandSender.sendMessage("§a[DynamicServers] §f" + address.getName() + " §afoi removido. (§f" + address.getAddress().getAddress().getHostAddress() + ":" + address.getAddress().getPort() + ")");
                     ProxyServer.getInstance().getServers().remove(address.getName());
                  } else {
                     commandSender.sendMessage("§cNão é possivel remover um servidor com jogadores online.");
                  }

                  address = null;
               } else {
                  commandSender.sendMessage("§cNão foi possível obter as informaçőes deste servidor.");
               }
            } else {
               commandSender.sendMessage("§cEste servidor não está registrado.");
            }

            serverName = null;
         } else {
            this.sendHelp(commandSender);
         }
      } else if (args.length == 4) {
         if (args[0].equalsIgnoreCase("create")) {
            serverName = args[1];
            if (!ProxyServer.getInstance().getServers().containsKey(serverName)) {
               String address1 = args[2];
               String portString = args[3];
               if (address1.length() > 7) {
                  if (StringUtility.isInteger(portString)) {
                     int port = Integer.parseInt(portString);
                     if (BungeeMain.registerServer(serverName, address1, port)) {
                        commandSender.sendMessage("§a[DynamicServers] §f" + serverName + " §afoi registrado. (§f" + address1 + ":" + port + ")");
                     } else {
                        commandSender.sendMessage("§cOcorreu um erro ao tentar registrar o servidor.");
                     }
                  } else {
                     commandSender.sendMessage("§cPorta invalida.");
                  }
               } else {
                  commandSender.sendMessage("§cEndereço invalida.");
               }

               address1 = null;
               portString = null;
            } else {
               commandSender.sendMessage("§cEste servidor já está registrado.");
            }

            serverName = null;
         } else {
            this.sendHelp(commandSender);
         }
      } else {
         this.sendHelp(commandSender);
      }

   }

   private void sendHelp(BungeeCommandSender commandSender) {
      commandSender.sendMessage("§cUtilize: /dynamicservers create <Name> <Address> <Port>");
      commandSender.sendMessage("§cUtilize: /dynamicservers remove <Name>");
   }
}