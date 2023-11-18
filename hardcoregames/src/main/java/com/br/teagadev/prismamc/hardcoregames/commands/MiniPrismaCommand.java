package com.br.teagadev.prismamc.hardcoregames.commands;

import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.hardcoregames.listeners.StringUtils;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class MiniPrismaCommand implements CommandClass {
   private final List<String> types = Arrays.asList("lava", "minicopa", "ultrafast", "gladiator", "arenafeast", "arenadamage", "arenapvp", "arenaold", "MiniCrazzy", "miniMiniCrazzy", "MiniCrazzytraining");
   private final List<String> modes = Arrays.asList("solo", "dupla");

   @Command(
      name = "explainevent",
      aliases = {"explicarevento", "eventoexplicar"},
      groupsToUse = {Groups.MOD}
   )
   public void explainevent(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         if (args.length == 0) {
            this.sendHelp(commandSender);
         } else {
            Player player = commandSender.getPlayer();
            if (args.length == 1) {
               Iterator var5;
               Player allUsers;
               if (args[0].equalsIgnoreCase("MiniCrazzy")) {
                  var5 = Bukkit.getOnlinePlayers().iterator();

                  while(var5.hasNext()) {
                     allUsers = (Player)var5.next();
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fSejam bem-vindos(as) a §eMiniCrazzy§f!");
                     allUsers.sendMessage("§f");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fTodos irão iniciar o jogo com um kit de itens.");
                     allUsers.sendMessage("§f");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fVocês terão §e1 minuto §fde invencibilidade para se\n§forganizarem.");
                     allUsers.sendMessage("§f");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fÉ §cproibido §finterferir na luta de jogadores que não fazem parte de seu time.");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fÉ §cproibido §faguardar que uma luta seja finalizada com intuito de atacar os vencedores.");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fNão é permitido fazer spawn traps.");
                     allUsers.sendMessage("§f");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fAos §e30 minutos §fde partida, vocês serão teleportados para uma Arena Final.");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fAlguns kits estarão desativados durante todo o evento.");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fDúvidas, reclamações e revisões de kick devem ser solicitados em nosso §9Discord§f!");
                     allUsers.sendMessage("§f");
                     allUsers.sendMessage("§6§lMiniCrazzy §7» §fIniciaremos em alguns instantes! Bom jogo e boa sorte.");
                  }
               } else {
                  this.sendHelp(commandSender);
               }

               if (args.length == 1) {
                  if (args[0].equalsIgnoreCase("arenapvp")) {
                     var5 = Bukkit.getOnlinePlayers().iterator();

                     while(var5.hasNext()) {
                        allUsers = (Player)var5.next();
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fSejam bem-vindos(as) a §eMiniCrazzy§f!");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fTodos irão iniciar o jogo com um kit de itens.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fVocês terão §e1 minuto §fde invencibilidade para se\n§forganizarem.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fÉ §cproibido §finterferir na luta de jogadores que não fazem parte de seu time.");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fÉ §cproibido §faguardar que uma luta seja finalizada com intuito de atacar os vencedores.");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fNão é permitido fazer spawn traps.");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fAos §e30 minutos §fde partida, vocês serão teleportados para uma Arena Final.");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fAlguns kits estarão desativados durante todo o evento.");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fDúvidas, reclamações e revisões de kick devem ser solicitados em nosso §9Discord§f!");
                        allUsers.sendMessage("§f");
                        allUsers.sendMessage("§6§lMiniCrazzy §7» §fIniciaremos em alguns instantes! Bom jogo e boa sorte.");
                     }
                  } else {
                     this.sendHelp(commandSender);
                  }
               }
            }

            player = null;
         }
      }
   }

   private void sendHelp(BukkitCommandSender commandSender) {
      commandSender.sendMessage("");
      commandSender.sendMessage("§cUse: /explicarevento <" + StringUtils.join(this.types, "§c, ") + ">");
      commandSender.sendMessage(" §cExplicação do comando:");
      commandSender.sendMessage(" §cEle anunciará todas as informações e instruções do evento");
      commandSender.sendMessage(" §cpara os jogadores presentes na sala com clareza e bem");
      commandSender.sendMessage(" §ctabelado para intuição.");
      commandSender.sendMessage("");
   }
}