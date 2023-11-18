package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.BukkitServerAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.ArrayList;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class SyncTpallCommand implements CommandClass {
   private boolean running = false;

   @Command(
      name = "synctpall",
      aliases = {"stpall"},
      groupsToUse = {Groups.MOD}
   )
   public void synctpall(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (this.running) {
            commandSender.sendMessage("§cJá existe um SyncTpall em andamento.");
         } else {
            this.running = true;
            this.startSyncTpall(commandSender.getPlayer());
            BukkitServerAPI.warnStaff("§7[" + BukkitServerAPI.getRealNick(commandSender.getPlayer()) + " puxou todos os jogadores sincronizadamente]", Groups.ADMIN);
         }
      }
   }

   private void startSyncTpall(final Player player) {
      (new BukkitRunnable() {
         final Location loc = player.getLocation();
         final ArrayList<Player> players = (ArrayList)player.getWorld().getPlayers();
         final int toTeleport;
         int teleporteds;

         {
            this.toTeleport = this.players.size();
            this.teleporteds = 0;
         }

         public void run() {
            if (this.teleporteds >= this.toTeleport) {
               this.cancel();
               SyncTpallCommand.this.running = false;
               if (player.isOnline()) {
                  player.sendMessage("§aSyncTpall terminado!");
               }

            } else {
               for(int i = 0; i < 2; ++i) {
                  try {
                     Player t = (Player)this.players.get(this.teleporteds + i);
                     if (t != null && t != player) {
                        t.teleport(this.loc);
                     }
                  } catch (IndexOutOfBoundsException var3) {
                  } catch (NullPointerException var4) {
                  }
               }

               this.teleporteds = this.teleporteds += 2;
            }
         }
      }).runTaskTimer(BukkitMain.getInstance(), 5L, 5L);
   }
}