package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.vanish.VanishAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.tag.Tag;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class TellCommand implements CommandClass {
   @Command(
      name = "tell",
      aliases = {"pm"},
      runAsync = true
   )
   public void tell(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            player.sendMessage("\n§cUtilize: /tell <Nick> <Mensagem>\n§cUtilize: /tell <On/Off>\n");
         } else if (args.length == 1) {
            DataHandler dataHandler;
            if (args[0].equalsIgnoreCase("on")) {
               dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();
               if (dataHandler.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
                  player.sendMessage("§aSuas mensagens privadas já estão ativas.");
                  return;
               }

               dataHandler.getData(DataType.RECEIVE_PRIVATE_MESSAGES).setValue(true);
               player.sendMessage("§aAgora você poderá receber mensagens privadas.");
               BukkitMain.runAsync(() -> {
                  dataHandler.saveCategory(DataCategory.ACCOUNT);
               });
            } else if (args[0].equalsIgnoreCase("off")) {
               dataHandler = BukkitMain.getBukkitPlayer(player.getUniqueId()).getDataHandler();
               if (!dataHandler.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
                  player.sendMessage("§aSuas mensagens privadas já estão ativas.");
                  return;
               }

               dataHandler.getData(DataType.RECEIVE_PRIVATE_MESSAGES).setValue(false);
               player.sendMessage("§cVocê desativou o recebimento de mensagens privadas.");
               BukkitMain.runAsync(() -> {
                  dataHandler.saveCategory(DataCategory.ACCOUNT);
               });
            } else {
               player.sendMessage("\n§cUtilize: /tell <Nick> <Mensagem>\n§cUtilize: /tell <On/Off>\n");
            }
         } else {
            this.handleTell(player, Bukkit.getPlayer(args[0]), StringUtility.createArgs(1, args));
         }

      }
   }

   private void handleTell(Player sender, Player receiver, String mensagem) {
      if (receiver == null) {
         sender.sendMessage("§cJogador offline!");
      } else if (receiver == sender) {
         sender.sendMessage("§cVocê não pode enviar mensagem para si mesmo.");
      } else {
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(sender.getUniqueId());
         BukkitPlayer bukkitPlayer1 = BukkitMain.getBukkitPlayer(receiver.getUniqueId());
         if (VanishAPI.isInvisible(receiver) && bukkitPlayer.getGroup().getLevel() <= Groups.YOUTUBER_PLUS.getLevel()) {
            sender.sendMessage("§cJogador offline!");
         } else if (!bukkitPlayer1.getBoolean(DataType.RECEIVE_PRIVATE_MESSAGES)) {
            sender.sendMessage("§cO jogador não pode receber mensagens privadas.");
         } else {
            bukkitPlayer.setLastMessage(receiver.getName());
            bukkitPlayer1.setLastMessage(sender.getName());
            Tag tag = bukkitPlayer.getGroup().getTag();
            Tag tag1 = bukkitPlayer1.getGroup().getTag();
            sender.sendMessage("§8Mensagem para §6%nick%: §f%mensagem%".replace("%nick%", tag1.getColor() + receiver.getName()).replace("%mensagem%", mensagem));
            receiver.sendMessage("§8Mensagem para §6%nick%: §f%mensagem%".replace("%nick%", tag.getColor() + sender.getName()).replace("%mensagem%", mensagem));
            if (SpyCommand.spying.size() != 0) {
               Iterator var8 = SpyCommand.getSpys().iterator();

               while(var8.hasNext()) {
                  Player spys = (Player)var8.next();
                  if (SpyCommand.isSpyingAll(spys)) {
                     spys.sendMessage("§f[SPY] §8Mensagem de §6%nick% §8para §6%nick1%§8: §f%mensagem%".replace("%nick%", tag.getColor() + sender.getName()).replace("%nick1%", tag1.getColor() + receiver.getName()).replace("%mensagem%", mensagem));
                  } else {
                     boolean send = false;
                     if (SpyCommand.isSpyingPlayer(spys, bukkitPlayer.getNick())) {
                        send = true;
                     } else if (SpyCommand.isSpyingPlayer(spys, bukkitPlayer.getNick())) {
                        send = true;
                     }

                     if (send) {
                        spys.sendMessage("§f[SPY] §8Mensagem de §6%nick% §8para §6%nick1%§8: §f%mensagem%".replace("%nick%", tag.getColor() + sender.getName()).replace("%nick1%", tag1.getColor() + receiver.getName()).replace("%mensagem%", mensagem));
                     }
                  }
               }
            }

         }
      }
   }

   @Command(
      name = "reply",
      aliases = {"r"}
   )
   public void reply(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            player.sendMessage("§cUtilize: /r <Mensagem>");
         } else {
            BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
            if (bukkitPlayer.getLastMessage().equalsIgnoreCase("")) {
               player.sendMessage("§cVocê não tem nenhuma conversa para responder.");
            } else {
               this.handleTell(player, Bukkit.getPlayer(bukkitPlayer.getLastMessage()), StringUtility.createArgs(0, args));
            }
         }
      }
   }
}