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
import org.bukkit.entity.Item;

public class ClearDropsCommand implements CommandClass {
   @Command(
      name = "cleardrops",
      aliases = {"cd"},
      groupsToUse = {Groups.MOD}
   )
   public void clearDrops(BukkitCommandSender commandSender, String label, String[] args) {
      int removidos = 0;
      Iterator var5 = Bukkit.getWorlds().iterator();

      while(var5.hasNext()) {
         World world = (World)var5.next();
         List<Entity> items = world.getEntities();
         Iterator var8 = items.iterator();

         while(var8.hasNext()) {
            Entity item = (Entity)var8.next();
            if (item instanceof Item) {
               item.remove();
               ++removidos;
            }
         }
      }

      commandSender.sendMessage("§cForam removidos %quantia% itens do chão.".replace("%quantia%", "" + removidos));
      if (!commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
         BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " limpou o chão!]", Groups.ADMIN);
      }

   }
}