package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.Bukkit;

public class DanoCommand implements CommandClass {
   @Command(
      name = "dano",
      groupsToUse = {Groups.MOD}
   )
   public void dano(BukkitCommandSender commandSender, String label, String[] args) {
      if (BukkitSettings.DANO_OPTION) {
         BukkitSettings.DANO_OPTION = false;
         Bukkit.broadcastMessage("§cO DANO foi desativado!");
      } else {
         BukkitSettings.DANO_OPTION = true;
         Bukkit.broadcastMessage("§aO DANO foi ativado!");
      }

      if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
         BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " " + (BukkitSettings.DANO_OPTION ? "ativou" : "desativou") + " o Dano!]", Groups.ADMIN);
      }

   }

   @Command(
      name = "pvp",
      groupsToUse = {Groups.MOD}
   )
   public void pvp(BukkitCommandSender commandSender, String label, String[] args) {
      if (BukkitSettings.PVP_OPTION) {
         BukkitSettings.PVP_OPTION = false;
         Bukkit.broadcastMessage("§cO PVP foi desativado");
      } else {
         BukkitSettings.PVP_OPTION = true;
         Bukkit.broadcastMessage("§aO PVP foi ativado!");
      }

      if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
         BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " " + (BukkitSettings.PVP_OPTION ? "ativou" : "desativou") + " o PvP!]", Groups.ADMIN);
      }

   }
}