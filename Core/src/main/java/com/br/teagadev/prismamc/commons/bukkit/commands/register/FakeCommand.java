package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.api.fake.FakeAPI;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.tag.TagManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import java.util.Iterator;
import java.util.Random;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class FakeCommand implements CommandClass {
   private final String[] names = new String[]{"Mine", "Craft", "Hyper", "Lord", "Zyper", "Beach", "Actor", "Games", "Nitro", "Man", "Plays", "Crazy", "Mega", "Mineman", "G0D", "Killer", "Noob", "Gamer", "Blessed", "Scroll", "Money", "Fish", "Ferrari", "Player", "Super", "Hype", "Net", "Flix", "Flex", "Corsa", "Prata", "Verde", "Rap", "Astra", "Onix"};
   private final Random random = new Random();

   @Command(
      name = "fake",
      groupsToUse = {Groups.PARTNER},
      aliases = {"nick"}
   )
   public void fake(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player p = commandSender.getPlayer();
         if (args.length == 0) {
            p.sendMessage("§cUtilize o comando: /fake (nick/random/#/list)");
         } else {
            String nick;
            if (args[0].equalsIgnoreCase("#")) {
               BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
               if (!bukkitPlayer.containsFake()) {
                  p.sendMessage("§cVocê não está utilizando um Fake.");
                  return;
               }

               if (this.requestChangeNick(p, false)) {
                  nick = bukkitPlayer.getNick();
                  bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue("");
                  BukkitMain.runAsync(() -> {
                     bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
                  });
                  FakeAPI.changePlayerName(p, nick, false);
                  p.sendMessage("§aFake removido com sucesso!");
                  TagManager.setTag(p, bukkitPlayer.getGroup().getTag(), bukkitPlayer);
                  bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_SET_FAST_SKIN).field(nick));
               }
            } else {
               BukkitPlayer bukkitPlayer;
               if (args[0].equalsIgnoreCase("random")) {
                  boolean finded = false;
                  nick = this.getRandomFake();
                  if (Bukkit.getPlayer(nick) == null) {
                     finded = true;
                  } else {
                     nick = this.getRandomFake();
                     if (Bukkit.getPlayer(nick) == null) {
                        finded = true;
                     } else {
                        nick = this.getRandomFake();
                        if (Bukkit.getPlayer(nick) == null) {
                           finded = true;
                        } else {
                           nick = this.getRandomFake();
                           if (Bukkit.getPlayer(nick) == null) {
                              finded = true;
                           }
                        }
                     }
                  }

                  if (!finded) {
                     p.sendMessage("§cNenhum fake disponivel.");
                     return;
                  }

                  if (this.isOriginal(nick)) {
                     p.sendMessage("§cEste nick está associado a uma conta original.");
                     return;
                  }

                  if (this.requestChangeNick(p, true)) {
                     bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
                     bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue(nick);
                     BukkitMain.runAsync(() -> {
                        bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
                     });
                     FakeAPI.changePlayerName(p, nick, false);
                     p.sendMessage("§aVocê alterou seu nick para: §e§l%nick%".replace("%nick%", nick));
                     TagManager.setTag(p, Groups.MEMBRO.getTag(), bukkitPlayer);
                     bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_SET_RANDOM_SKIN));
                  }
               } else if (args[0].equalsIgnoreCase("list")) {
                  if (!commandSender.hasPermission("fakelist")) {
                     return;
                  }

                  int fakes = 0;
                  Iterator var13 = CommonsGeneral.getProfileManager().getGamingProfiles().iterator();

                  while(var13.hasNext()) {
                     GamingProfile profiles = (GamingProfile)var13.next();
                     BukkitPlayer bukkitPlayer1 = (BukkitPlayer)profiles;
                     if (bukkitPlayer1.containsFake()) {
                        if (fakes == 0) {
                           p.sendMessage("");
                           p.sendMessage("§c");
                           p.sendMessage("");
                        }

                        p.sendMessage("§f- §e%nickFake% §f(§7%nickReal%§f)".replace("%nickFake%", bukkitPlayer1.getPlayer().getName()).replace("%nickReal%", bukkitPlayer1.getNick()));
                        ++fakes;
                     }
                  }

                  if (fakes == 0) {
                     p.sendMessage("§eNenhum jogador está utilizado fake!");
                  } else {
                     p.sendMessage("");
                  }
               } else {
                  String nick1 = args[0];
                  if (nick1.length() < 5) {
                     p.sendMessage("§cEste nick é muito pequeno!");
                     return;
                  }

                  if (nick1.length() > 16) {
                     p.sendMessage("§cEste nick é muito grande!");
                     return;
                  }

                  if (!this.validString(nick1)) {
                     p.sendMessage("§cEste nick contém caractéres não permitidos.");
                     return;
                  }

                  Player t = Bukkit.getPlayer(nick1);
                  if (t != null && t.isOnline()) {
                     p.sendMessage("§cEste nick está associado a uma conta original.");
                     return;
                  }

                  if (this.isOriginal(nick1)) {
                     p.sendMessage("§cEste nick está associado a uma conta original.");
                     return;
                  }

                  if (this.requestChangeNick(p, true)) {
                     bukkitPlayer = BukkitMain.getBukkitPlayer(p.getUniqueId());
                     bukkitPlayer.getDataHandler().getData(DataType.FAKE).setValue(nick1);
                     bukkitPlayer.getDataHandler().saveCategory(DataCategory.ACCOUNT);
                     FakeAPI.changePlayerName(p, nick1, false);
                     p.sendMessage("§aVocê alterou seu nick para: §e§l%nick%".replace("%nick%", nick1));
                     TagManager.setTag(p, Groups.MEMBRO.getTag(), bukkitPlayer);
                     bukkitPlayer.sendPacket((new CPacketCustomAction(bukkitPlayer.getNick())).type(PacketType.BUNGEE_SET_RANDOM_SKIN));
                  }
               }
            }

         }
      }
   }

   private boolean validString(String str) {
      return str.matches("[a-zA-Z\\d]+") && !str.toLowerCase().contains(".com") && !str.toLowerCase().contains(".") && !str.toLowerCase().contains("lixo") && !str.toLowerCase().contains("ez") && !str.toLowerCase().contains("lag") && !str.toLowerCase().contains("merda") && !str.toLowerCase().contains("mush") && !str.toLowerCase().contains("server") && !str.toLowerCase().contains("fdp") && !str.toLowerCase().contains("zenix") && !str.toLowerCase().contains("empire") && !str.toLowerCase().contains("battle") && !str.toLowerCase().contains("like") && !str.toLowerCase().contains("kits");
   }

   private boolean isOriginal(String nick) {
      try {
         return CommonsGeneral.getUUIDFetcher().getUUID(nick) != null;
      } catch (UUIDFetcherException var3) {
         return true;
      }
   }

   private String getRandomFake() {
      String randomNick = "";
      if (this.random.nextBoolean()) {
         randomNick = this.names[this.random.nextInt(this.names.length - 1)] + this.random.nextInt(6000) + this.names[this.random.nextInt(this.names.length - 1)];
      } else {
         randomNick = this.names[this.random.nextInt(this.names.length - 1)] + this.names[this.random.nextInt(this.names.length - 1)] + this.random.nextInt(6000);
      }

      if (randomNick.length() > 16) {
         randomNick = this.getRandomFake();
      }

      return randomNick;
   }

   private boolean requestChangeNick(Player player, boolean colocar) {
      PlayerRequestEvent event = new PlayerRequestEvent(player, "fake");
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (event.isCancelled()) {
         player.sendMessage("§cVocê não pode %action% o fake agora.".replace("%action%", colocar ? "colocar" : "tirar"));
         return false;
      } else {
         return true;
      }
   }
}