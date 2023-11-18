package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

public class InvseeCommand implements CommandClass {
   @Command(
      name = "invsee",
      aliases = {"inv"},
      groupsToUse = {Groups.TRIAL}
   )
   public void invsee(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player p = commandSender.getPlayer();
         if (args.length == 0) {
            p.sendMessage("§cUtilize: /invsee <Nick>");
         } else if (args.length == 1) {
            Player d = Bukkit.getPlayer(args[0]);
            if (d == null) {
               p.sendMessage("§cJogador offline!");
               return;
            }

            if (d == p) {
               p.sendMessage("§cVocê não pode abrir o seu próprio inventário.");
               return;
            }

            p.setMetadata("inventory-view", new FixedMetadataValue(BukkitMain.getInstance(), d.getUniqueId().toString()));
            p.openInventory(d.getInventory());
            p.sendMessage("§aVocê abriu o inventário de %nick%".replace("%nick%", d.getName()));
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(p) + " abriu o inventário de " + d.getName() + "]", Groups.ADMIN);
         } else {
            p.sendMessage("§cUtilize: /invsee <Nick>");
         }

      }
   }
}