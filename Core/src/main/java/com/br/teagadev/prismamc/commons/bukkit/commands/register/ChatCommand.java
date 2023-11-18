package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ChatCommand implements CommandClass {
   @Command(
      name = "chat",
      groupsToUse = {Groups.MOD}
   )
   public void chat(BukkitCommandSender commandSender, String label, String[] args) {
      if (BukkitSettings.CHAT_OPTION) {
         BukkitSettings.CHAT_OPTION = false;
         Bukkit.broadcastMessage("§cO chat foi desativado.");
         if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " desativou o Chat!]", Groups.ADMIN);
         }
      } else {
         BukkitSettings.CHAT_OPTION = true;
         Bukkit.broadcastMessage("§aO chat foi ativado.");
         if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " ativou o Chat!]", Groups.ADMIN);
         }
      }

   }

   @Command(
      name = "clearchat",
      aliases = {"cc"},
      groupsToUse = {Groups.MOD}
   )
   public void clearChat(BukkitCommandSender commandSender, String label, String[] args) {
      Iterator var4 = Bukkit.getOnlinePlayers().iterator();

      while(var4.hasNext()) {
         Player on = (Player)var4.next();

         for(int i = 0; i <= 100; ++i) {
            on.sendMessage("");
         }
      }

      Bukkit.broadcastMessage("§aO chat foi limpo.");
   }
}