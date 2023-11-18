package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.profile.addons.Medals;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.chat.HoverEvent.Action;
import org.bukkit.entity.Player;

public class MedalsCommand implements CommandClass {
   @Command(
      name = "medals",
      aliases = {"medal", "medalha", "medalhas"}
   )
   public void medals(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length >= 1) {
            String medalName = StringUtility.createArgs(0, args);
            if (Medals.existMedal(medalName)) {
               Medals medal = Medals.getMedalByName(medalName);
               if (!player.hasPermission("medals." + medal.getName().toLowerCase()) && !player.hasPermission("medals.all")) {
                  player.sendMessage("§cVocê não possui a medalha citada.");
               } else {
                  BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
                  if (bukkitPlayer.getInt(DataType.MEDAL) == medal.getId()) {
                     player.sendMessage("§cVocê removeu sua medalha.");
                     bukkitPlayer.set(DataType.MEDAL, 0);
                  } else {
                     bukkitPlayer.set(DataType.MEDAL, medal.getId());
                     player.sendMessage("§aVocê selecionou a medalha %medalha%§a com sucesso.".replace("%medalha%", medal.getColor() + medal.getName()));
                  }

                  BukkitMain.runAsync(() -> {
                     bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
                  });
                  if (!bukkitPlayer.containsFake()) {
                     bukkitPlayer.updateTag(player, bukkitPlayer.getActualTag(), true);
                  }
               }
            } else {
               this.sendMedals(player);
            }
         } else {
            this.sendMedals(player);
         }

      }
   }

   private void sendMedals(Player player) {
      List<Medals> playerMedals = this.getMedals(player);
      TextComponent message = new TextComponent("§aSuas medalhas: ");

      for(int i = 0; i < playerMedals.size(); ++i) {
         message.addExtra(i == 0 ? " " : "§f, ");
         if (i == 0) {
            message.addExtra("");
         }

         Medals medal = (Medals)playerMedals.get(i);
         TextComponent baseComponent = new TextComponent(medal.getSymbol());
         baseComponent.setColor(ChatColor.getByChar(medal.getColor().charAt(1)));
         baseComponent.setHoverEvent(new HoverEvent(Action.SHOW_TEXT, new BaseComponent[]{new TextComponent("§eClique para selecionar a " + medal.getColor() + medal.getSymbol())}));
         baseComponent.setClickEvent(new ClickEvent(net.md_5.bungee.api.chat.ClickEvent.Action.RUN_COMMAND, "/medalha " + medal.getName()));
         message.addExtra(baseComponent);
      }

      message.addExtra("§f.");
      player.spigot().sendMessage(message);
   }

   private List<Medals> getMedals(Player player) {
      return player.hasPermission("medals.all") ? Arrays.asList(Medals.values()) : (List)Arrays.stream(Medals.values()).filter((medal) -> {
         return player.hasPermission("medals." + medal.getName().toLowerCase());
      }).collect(Collectors.toList());
   }

   @Completer(
      name = "medals",
      aliases = {"medal", "medalha", "medalhas"}
   )
   public List<String> medalCompleter(BukkitCommandSender sender, String label, String[] args) {
      if (sender.isPlayer()) {
         Player p = sender.getPlayer();
         if (args.length == 1) {
            List<String> list = new ArrayList();
            Medals[] var6 = Medals.values();
            int var7 = var6.length;

            for(int var8 = 0; var8 < var7; ++var8) {
               Medals m = var6[var8];
               if (p.hasPermission("medals." + m.getName().toLowerCase()) || p.hasPermission("medals.all")) {
                  list.add(m.getName());
               }
            }

            return list;
         }
      }

      return new ArrayList();
   }
}