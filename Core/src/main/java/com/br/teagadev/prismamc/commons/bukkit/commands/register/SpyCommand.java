package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.UUID;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SpyCommand implements CommandClass {
   public static final HashMap<UUID, List<String>> spying = new HashMap();
   private final String COMMAND_USAGE = "§a§lSPY §fUtilize: /spy <Nick/All>";
   private final String SPYING_ALL = "§a§lSPY §fVocê está espiando todos os §a§lTELLS!";
   private final String NOT_SPYING_ALL = "§a§lSPY §fVocê não está espionando mais!";
   private final String NOT_SPYING_PLAYER = "§a§lSPY §fVocê não está espiando o jogador §a%nick%";
   private final String SPYING_PLAYER = "§a§lSPY §fVocê está espiando o jogador §a%nick%";

   public static List<Player> getSpys() {
      List<Player> list = new ArrayList();
      Iterator var1 = spying.keySet().iterator();

      while(var1.hasNext()) {
         UUID uuids = (UUID)var1.next();
         Player target = Bukkit.getPlayer(uuids);
         if (target != null && target.isOnline()) {
            list.add(target);
         }
      }

      return list;
   }

   public static boolean isSpyingPlayer(Player player, String nick) {
      return !spying.containsKey(player.getUniqueId()) ? false : ((List)spying.get(player.getUniqueId())).contains(nick);
   }

   public static boolean isSpyingAll(Player player) {
      return !spying.containsKey(player.getUniqueId()) ? false : ((List)spying.get(player.getUniqueId())).contains("all");
   }

   @Command(
      name = "spy",
      aliases = {"espiar"},
      groupsToUse = {Groups.ADMIN}
   )
   public void spy(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         if (args.length != 1) {
            player.sendMessage("§a§lSPY §fUtilize: /spy <Nick/All>");
         } else {
            String nick = args[0];
            if (this.isAll(nick)) {
               if (!spying.containsKey(player.getUniqueId())) {
                  spying.put(player.getUniqueId(), Collections.singletonList("all"));
                  player.sendMessage("§a§lSPY §fVocê está espiando todos os §a§lTELLS!");
               } else if (!((List)spying.get(player.getUniqueId())).contains("all")) {
                  spying.put(player.getUniqueId(), Collections.singletonList("all"));
                  player.sendMessage("§a§lSPY §fVocê está espiando todos os §a§lTELLS!");
               } else {
                  spying.remove(player.getUniqueId());
                  player.sendMessage("§a§lSPY §fVocê não está espionando mais!");
               }
            } else {
               Player target = Bukkit.getPlayer(nick);
               if (target != null) {
                  String realNick = BukkitMain.getBukkitPlayer(target.getUniqueId()).getNick();
                  if (!spying.containsKey(player.getUniqueId())) {
                     spying.put(player.getUniqueId(), Collections.singletonList(realNick));
                     player.sendMessage("§a§lSPY §fVocê está espiando o jogador §a%nick%".replace("%nick%", realNick));
                  } else {
                     List list;
                     if (((List)spying.get(player.getUniqueId())).contains(realNick)) {
                        list = (List)spying.get(player.getUniqueId());
                        list.remove(realNick);
                        if (list.size() == 0) {
                           spying.remove(player.getUniqueId());
                        } else {
                           spying.put(player.getUniqueId(), list);
                        }

                        player.sendMessage("§a§lSPY §fVocê não está espiando o jogador §a%nick%".replace("%nick%", realNick));
                        list.clear();
                        list = null;
                     } else {
                        list = (List)spying.get(player.getUniqueId());
                        list.add(realNick);
                        spying.put(player.getUniqueId(), list);
                        player.sendMessage("§a§lSPY §fVocê está espiando o jogador §a%nick%".replace("%nick%", realNick));
                        list.clear();
                        list = null;
                     }
                  }

                  target = null;
               } else {
                  player.sendMessage("§cJogador offline!");
               }
            }

         }
      }
   }

   private boolean isAll(String nick) {
      if (nick.equalsIgnoreCase("*")) {
         return true;
      } else {
         return nick.equalsIgnoreCase("all") ? true : nick.equalsIgnoreCase("todos");
      }
   }

   @Completer(
      name = "spy",
      aliases = {"espiar"}
   )
   public List<String> tagcompleter(BukkitCommandSender sender, String label, String[] args) {
      if (sender.isPlayer() && args.length == 1) {
         List<String> list = new ArrayList();
         list.add("all");
         Iterator var5 = Bukkit.getOnlinePlayers().iterator();

         while(var5.hasNext()) {
            Player ons = (Player)var5.next();
            if (ons.getName().toLowerCase().startsWith(args[0].toLowerCase())) {
               list.add(ons.getName());
            }
         }

         return list;
      } else {
         return new ArrayList();
      }
   }
}