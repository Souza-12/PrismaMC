package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class KillMobsCommand implements CommandClass {
   @Command(
      name = "killmobs",
      aliases = {"km"},
      groupsToUse = {Groups.MOD}
   )
   public void killMobs(BukkitCommandSender commandSender, String label, String[] args) {
      int removidos = 0;

      List entitys;
      for(Iterator var5 = Bukkit.getWorlds().iterator(); var5.hasNext(); entitys = null) {
         World world = (World)var5.next();
         entitys = world.getEntities();
         Iterator var8 = entitys.iterator();

         while(var8.hasNext()) {
            Entity entity = (Entity)var8.next();
            if (entity instanceof LivingEntity && !(entity instanceof Player)) {
               entity.remove();
               ++removidos;
            }
         }

         entitys.clear();
      }

      commandSender.sendMessage("§cForam removidos %quantia% mobs.".replace("%quantia%", "" + removidos));
      if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
         BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " limpou os mobs!]", Groups.ADMIN);
      }

   }
}