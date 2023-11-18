package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SkinCommand implements CommandClass {
   @Command(
      name = "skin",
      groupsToUse = {Groups.SAPPHIRE},
      runAsync = true
   )
   public void skin(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            player.sendMessage("§cUtilize: /skin <Nick>");
         } else {
            if (this.requestChangeSkin(player)) {
               BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
               if (!bukkitPlayer.canChangeSkin()) {
                  player.sendMessage("§cAguarde para trocar de skin novamente!");
                  return;
               }

               bukkitPlayer.setLastChangeSkin(System.currentTimeMillis());
               if (args[0].equalsIgnoreCase("atualizar")) {
                  player.sendMessage("§aSkin sendo baixada, aguarde...");
                  bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_UPDATE_SKIN).field(args[0]));
               } else if (args[0].equalsIgnoreCase("random")) {
                  player.sendMessage("§aSkin sendo baixada, aguarde...");
                  bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_SET_RANDOM_SKIN));
               } else {
                  if (!MySQLManager.contains("skins", "nick", args[0])) {
                     player.sendMessage("§aSkin sendo baixada, aguarde...");
                  }

                  bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_SET_SKIN).field(args[0]));
               }
            } else {
               player.sendMessage("§cVocê não pode trocar sua skin agora!");
            }

         }
      }
   }

   private boolean requestChangeSkin(Player player) {
      PlayerRequestEvent event = new PlayerRequestEvent(player, "skin");
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         player.sendMessage("§cVocê não pode trocar sua skin agora!");
         return false;
      } else {
         return true;
      }
   }
}