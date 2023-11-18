package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.tag.TagManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.entity.Player;

public class TagCommand implements CommandClass {
   @Command(
      name = "tag"
   )
   public void tag(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length == 0) {
            this.sendTags(player);
         } else if (args.length == 1) {
            String selectedGroup = args[0];
            if (Groups.existGrupo(selectedGroup)) {
               Groups group = Groups.getGroup(selectedGroup);
               if (TagManager.hasPermission(player, group)) {
                  BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
                  if (!bukkitPlayer.getActualTag().getName().equals(group.getTag().getName())) {
                     bukkitPlayer.updateTag(player, group.getTag(), false);
                  } else {
                     player.sendMessage("§cVocê já está utilizando está tag.");
                  }
               } else {
                  player.sendMessage("§cVocê não possui esta TAG.");
               }
            } else {
               this.sendTags(player);
            }
         }

      }
   }

   private void sendTags(Player player) {
      List<Groups> playerGroups = TagManager.getPlayerGroups(player);
      TextComponent message = new TextComponent("§aSuas tags:");

      for(int i = 0; i < playerGroups.size(); ++i) {
         message.addExtra(i == 0 ? " " : "§f, ");
         if (i == 0) {
            message.addExtra("");
         }

         Groups group = (Groups)playerGroups.get(i);
         BaseComponent baseComponent = new TextComponent(group.getName());
         baseComponent.setColor(ChatColor.getByChar(group.getColor().charAt(1)));
         baseComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§7Prévia: " + group.getColor() + "§l" + group.getTag().getPrefix() + " " + group.getColor() + player.getName() + "\n§eClique para selecionar!")}));
         baseComponent.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/tag " + group.getName()));
         message.addExtra(baseComponent);
      }

      message.addExtra("§f.");
      player.spigot().sendMessage(message);
   }

   @Completer(
      name = "tag"
   )
   public List<String> tagCompleter(BukkitCommandSender sender, String label, String[] args) {
      if (!sender.isPlayer()) {
         return Collections.emptyList();
      } else {
         Player p = sender.getPlayer();
         if (args.length < 1) {
            return (List)Arrays.stream(Groups.values()).filter((group) -> {
               return TagManager.hasPermission(p, group);
            }).map(Groups::getName).collect(Collectors.toList());
         } else {
            return args.length > 1 ? Collections.emptyList() : (List)Arrays.stream(Groups.values()).filter((group) -> {
               return group.getName().toLowerCase().startsWith(args[0].toLowerCase()) && TagManager.hasPermission(p, group);
            }).map(Groups::getName).collect(Collectors.toList());
         }
      }
   }
}