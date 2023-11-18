package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;

public class AdminCommand implements CommandClass {
   @Command(
      name = "admin",
      aliases = {"adm", "v"},
      groupsToUse = {Groups.TRIAL}
   )
   public void admin(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         VanishAPI.changeAdmin(commandSender.getPlayer());
      }
   }
}