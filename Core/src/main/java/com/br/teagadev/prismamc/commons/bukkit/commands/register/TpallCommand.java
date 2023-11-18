package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public class TpallCommand implements CommandClass {
   @Command(
      name = "tpall",
      groupsToUse = {Groups.DONO}
   )
   public void tpall(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         Location loc = player.getLocation();
         Iterator var6 = Bukkit.getOnlinePlayers().iterator();

         while(var6.hasNext()) {
            Player ons = (Player)var6.next();
            if (ons != player) {
               ons.setFallDistance(-5.0F);
               ons.teleport(loc);
            }
         }

         player.sendMessage("§aTodos os jogadores foram teletransportado!");
         BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(player) + " puxou todos os jogadores]", Groups.ADMIN);
         loc = null;
         player = null;
      }
   }
}