package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.server.ServerStopEvent;
import com.br.teagadev.prismamc.commons.bukkit.queue.PlayerBukkitQueue;
import com.br.teagadev.prismamc.commons.bukkit.queue.QueueType;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class StopCommand implements CommandClass {
   @Command(
      name = "stop",
      aliases = {"rl", "reload", "parar", "reiniciar", "stop"},
      groupsToUse = {Groups.DONO}
   )
   public void stop(BukkitCommandSender commandSender, String label, String[] args) {
      commandSender.sendMessage("");
      commandSender.sendMessage("§cFechando servidor...");
      commandSender.sendMessage("");
      Bukkit.getServer().getPluginManager().callEvent(new ServerStopEvent());
      if (Bukkit.getOnlinePlayers().size() == 0) {
         Bukkit.shutdown();
      } else {
         BukkitSettings.DANO_OPTION = false;
         BukkitSettings.PVP_OPTION = false;
         BukkitSettings.CHAT_OPTION = false;
         BukkitSettings.LOGIN_OPTION = false;
         commandSender.sendMessage("§aTentando enviar todos os jogadores locais para o Lobby.");
         ServerType serverToConnect = ServerType.LOBBY;
         if (BukkitMain.getServerType().isPvP(false)) {
            serverToConnect = ServerType.LOBBY_PVP;
         } else if (BukkitMain.getServerType().isHardcoreGames(false)) {
            serverToConnect = ServerType.LOBBY_HARDCOREGAMES;
         }

         PlayerBukkitQueue queue;
         Iterator var6;
         Player onlines;
         if (BukkitMain.getServerType() != ServerType.LOBBY && BukkitMain.getServerType() != ServerType.LOGIN) {
            queue = new PlayerBukkitQueue(15, true, QueueType.CONNECT);
            queue.setStopOnFinish(true);
            queue.setDestroyOnFinish(true);
            var6 = Bukkit.getOnlinePlayers().iterator();

            while(var6.hasNext()) {
               onlines = (Player)var6.next();
               queue.addToQueue(onlines, serverToConnect.getName());
               onlines.sendMessage("§cO Servidor atual irá reiniciar, Você será movido automaticamente para o Lobby.");
            }

            queue.start();
         } else {
            queue = new PlayerBukkitQueue(10, true, QueueType.KICK);
            queue.setStopOnFinish(true);
            queue.setDestroyOnFinish(true);
            var6 = Bukkit.getOnlinePlayers().iterator();

            while(var6.hasNext()) {
               onlines = (Player)var6.next();
               queue.addToQueue(onlines);
               onlines.sendMessage("§cO Servidor atual irá reiniciar, Você será movido automaticamente para o Lobby.");
            }

            queue.start();
         }

         commandSender.sendMessage("");
         if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " executou o comando para parar o servidor!]", Groups.ADMIN);
         }

      }
   }
}