package com.br.teagadev.prismamc.commons.bungee.commands.register;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.bungee.account.BungeePlayer;
import com.br.teagadev.prismamc.commons.bungee.commands.BungeeCommandSender;
import com.br.teagadev.prismamc.commons.common.clan.Clan;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.connections.mysql.MySQLManager;
import com.br.teagadev.prismamc.commons.common.data.DataHandler;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.punishment.PunishmentHistoric;
import com.br.teagadev.prismamc.commons.common.punishment.PunishmentManager;
import com.br.teagadev.prismamc.commons.common.punishment.types.Ban;
import com.br.teagadev.prismamc.commons.common.punishment.types.Mute;
import com.br.teagadev.prismamc.commons.common.utility.mojang.UUIDFetcher.UUIDFetcherException;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.commons.custompackets.PacketType;
import com.br.teagadev.prismamc.commons.custompackets.registry.CPacketCustomAction;
import com.br.teagadev.servercommunication.server.Server;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Iterator;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class PunishCommand implements CommandClass {
   public static void handleBan(ProxiedPlayer banned, String nick, String address, String motive, String baniu, Long tempo, String clan, String data) {
      String message = "§7[O jogador %nick% foi banido %duração% pelo %baniu%]".replace("%baniu%", baniu).replace("%nick%", nick).replace("%duração%", tempo == 0L ? "PERMANENTEMENTE" : "TEMPORARIAMENTE");
      BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);
      Ban ban = new Ban(nick, address, baniu, motive, tempo);
      ban.ban();
      PunishmentManager.addCache(nick.toLowerCase(), ban);
      BungeeMain.runAsync(() -> {
         try {
            Connection connection = CommonsGeneral.getMySQL().getConnection();
            Throwable var4 = null;

            try {
               PreparedStatement insert = connection.prepareStatement("INSERT INTO accounts_to_delete_(nick, timestamp) VALUES (?, ?)");
               insert.setString(1, nick);
               insert.setString(2, String.valueOf(System.currentTimeMillis()));
               insert.close();
               insert = null;
            } catch (Throwable var14) {
               var4 = var14;
               throw var14;
            } finally {
               if (connection != null) {
                  if (var4 != null) {
                     try {
                        connection.close();
                     } catch (Throwable var13) {
                        var4.addSuppressed(var13);
                     }
                  } else {
                     connection.close();
                  }
               }

            }
         } catch (SQLException var16) {
            CommonsGeneral.error("Ocorreu um erro ao tentar inserir um nick para ser deletado.");
         }

         if (!clan.equalsIgnoreCase("Nenhum")) {
            removePlayerFromClan(banned, nick, clan);
         }

      });
   }

   public static void handleKickBan(ProxiedPlayer target, String motivo, String baniu, String data) {
      target.disconnect(TextComponent.fromLegacyText("§cVocê foi banido do servidor.\n§cPor: %baniu%\n§cMotivo: %motivo%\n\n§cSolicite seu appeal no nosso discord: discord..crazzymc.com.br\n§cAdquira unban em nossa loja: www.crazzymc.com.br".replace("%baniu%", baniu).replace("%motivo%", motivo).replace("%data%", data)));
   }

   public static void handleMute(String nick, String motive, BungeeCommandSender commandSender, Long tempo) {
      Mute mute = new Mute(nick, commandSender.getNick(), motive, tempo);
      mute.mute();
      ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
      if (target != null) {
         if (mute.isPermanent()) {
            target.sendMessage("§cVocê foi mutado permanentemente!\n§cMotivo: %motivo%\n".replace("%motivo%", motive));
         } else {
            target.sendMessage("§cVocê foi mutado temporariamente!\n§cMotivo: %motivo%\n§cDuração: %duração%\n".replace("%motivo%", motive).replace("%duração%", DateUtils.formatDifference(tempo)));
         }

         BungeeMain.getBungeePlayer(target.getName()).getPunishmentHistoric().getMuteHistory().add(mute);
      }

      String message = "§7[O jogador %nick% foi mutado %duração% pelo %mutou%]".replace("%nick%", nick).replace("%mutou%", commandSender.getNick()).replace("%duração%", tempo == 0L ? "PERMANENTEMENTE" : "TEMPORARIAMENTE");
      BungeeMain.getManager().warnStaff(message, Groups.MEMBRO);
      mute = null;
   }

   public static void removePlayerFromClan(ProxiedPlayer quit, String playerNick, String clanName) {
      boolean removeClan = false;
      boolean deleteClan = false;
      if (!ClanManager.hasClanData(clanName)) {
         removeClan = true;
         ClanManager.load(clanName);
      }

      Clan clan = ClanManager.getClan(clanName);
      if (clan != null) {
         if (clan.getMemberList().size() == 1) {
            removeClan = true;
            deleteClan = true;
         } else {
            clan.removePlayer(playerNick);
            if (clan.getOwner().equalsIgnoreCase(playerNick)) {
               String newOwner = "";
               ProxiedPlayer onlines;
               Iterator var12;
               Object object;
               if (clan.getAdminList().size() == 0) {
                  removeClan = true;
                  deleteClan = true;

                  for(var12 = clan.getOnlines().iterator(); var12.hasNext(); onlines = null) {
                     object = var12.next();
                     onlines = (ProxiedPlayer)object;
                     onlines.sendMessage("§fO seu clan foi deletado.");
                     BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(onlines.getName());
                     bungeePlayer.set(DataType.CLAN, "Nenhum");
                     bungeePlayer.setInClanChat(false);
                     Server.getInstance().getServerGeneral().sendPacket(onlines.getServer().getInfo().getName(), (new CPacketCustomAction(onlines.getName(), onlines.getUniqueId())).type(PacketType.BUNGEE_SEND_PLAYER_ACTION).field("update-data").fieldValue("clan").extraValue("Nenhum"));
                  }
               } else {
                  newOwner = (String)clan.getAdminList().get(0);
                  clan.setOwner(newOwner);

                  for(var12 = clan.getOnlines().iterator(); var12.hasNext(); onlines = null) {
                     object = var12.next();
                     onlines = (ProxiedPlayer)object;
                     onlines.sendMessage("§a%nick% §aé o novo dono do clan!".replace("%nick%", newOwner));
                  }
               }
            } else {
               ProxiedPlayer onlines;
               for(Iterator var6 = clan.getOnlines().iterator(); var6.hasNext(); onlines = null) {
                  Object object = var6.next();
                  onlines = (ProxiedPlayer)object;
                  onlines.sendMessage("§c%nick% §csaiu do clan!".replace("%nick%", playerNick));
               }

               if (quit != null) {
                  clan.removeOnline(quit);
               }
            }
         }

         clan = null;
      } else {
         CommonsGeneral.error("Erro ao remover um jogador de um clan. CLAN = NULO?");
      }

      if (removeClan) {
         ClanManager.saveClan(clanName, true);
      }

   }

   @Command(
      name = "unmute",
      aliases = {"desmutar"},
      groupsToUse = {Groups.MOD},
      runAsync = true
   )
   public void unmute(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUtilize /unmute (player)");
      } else {
         ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
         String nick;
         if (target != null) {
            BungeePlayer bungeePlayer = BungeeMain.getBungeePlayer(target.getName());
            if (bungeePlayer != null) {
               if (bungeePlayer.getPunishmentHistoric().getActualMute() == null) {
                  commandSender.sendMessage("§cEste jogador não está mutado.");
                  return;
               }

               if (!bungeePlayer.getPunishmentHistoric().getActualMute().isPunished()) {
                  commandSender.sendMessage("§cEste jogador não está mutado.");
                  return;
               }

               bungeePlayer.getPunishmentHistoric().getActualMute().unmute(commandSender.getNick());
               commandSender.sendMessage("§aJogador desmutado com sucesso!");
               nick = null;
            }

            target = null;
         } else {
            nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
            if (nick.equalsIgnoreCase("N/A")) {
               commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
            } else {
               PunishmentHistoric punishHistoric = new PunishmentHistoric(nick);

               try {
                  punishHistoric.loadMutes();
               } catch (Exception var8) {
                  commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
                  BungeeMain.console("§cOcorreu um erro ao tentar carregar um punimento (Unmute) -> " + var8.getLocalizedMessage());
                  return;
               }

               Mute mute = punishHistoric.getActualMute();
               if (mute == null) {
                  commandSender.sendMessage("§cEste jogador não está mutado.");
               } else if (mute.isPunished()) {
                  mute.unmute(commandSender.getNick());
                  commandSender.sendMessage("§aJogador desmutado com sucesso!");
                  punishHistoric = null;
               }

            }
         }
      }
   }

   @Command(
      name = "unban",
      aliases = {"desbanir"},
      groupsToUse = {Groups.MOD},
      runAsync = true
   )
   public void unban(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length != 1) {
         commandSender.sendMessage("§cUtilize /unban (player)");
      } else {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
         } else {
            DataHandler dataHandler = this.getDataHandlerByPlayer((ProxiedPlayer)null, nick);
            if (dataHandler == null) {
               commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            } else {
               Ban ban = PunishmentManager.getBanCache(nick);
               if (ban == null) {
                  label99: {
                     PunishmentHistoric punishHistoric = this.getPunishmentHistoric(nick, dataHandler);

                     try {
                        punishHistoric.loadBans();
                        break label99;
                     } catch (Exception var12) {
                        commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
                        BungeeMain.console("§cOcorreu um erro ao tentar carregar um punimento (Unban) -> " + var12.getLocalizedMessage());
                     } finally {
                        ban = punishHistoric.getActualBan();
                     }

                     return;
                  }
               }

               if (ban != null && !ban.isPunished()) {
                  commandSender.sendMessage("§cEsta conta não está banida.");
               } else {
                  if (ban != null) {
                     ban.unban(commandSender.getNick());
                     PunishmentManager.removeCache(nick);
                  }

                  commandSender.sendMessage("§aJogador desbanido com sucesso!");
                  BungeeMain.getManager().warnStaff("§7[O jogador %nick% foi desbanido por %staffer%]".replace("%nick%", nick).replace("%staffer%", commandSender.getNick()), Groups.MEMBRO);
                  dataHandler = null;
               }
            }
         }
      }
   }

   @Command(
      name = "mute",
      aliases = {"mutar"},
      groupsToUse = {Groups.TRIAL},
      runAsync = true
   )
   public void mute(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length < 2) {
         commandSender.sendMessage("§cUtilize /mute (player) (motivo)");
      } else if (!MySQLManager.contains("accounts", "nick", args[0])) {
         commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
      } else {
         ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
         boolean continuar = this.continueProcess(commandSender, target);
         if (!continuar) {
            commandSender.sendMessage("§cVocê não pode mutar este jogador!");
         } else {
            handleMute(args[0], StringUtility.createArgs(1, args), commandSender, 0L);
            commandSender.sendMessage("§aJogador mutado permanentemente com sucesso!");
         }
      }
   }

   @Command(
      name = "tempmute",
      groupsToUse = {Groups.TRIAL},
      runAsync = true
   )
   public void tempmute(BungeeCommandSender commandSender, String label, String[] args) {
      if (args.length < 3) {
         commandSender.sendMessage("§cUtilize /tempmute (player) (tempo) (motivo)");
      } else if (!MySQLManager.contains("accounts", "nick", args[0])) {
         commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
      } else {
         ProxiedPlayer target = ProxyServer.getInstance().getPlayer(args[0]);
         boolean continuar = this.continueProcess(commandSender, target);
         if (!continuar) {
            commandSender.sendMessage("§cVocê não pode mutar este jogador!");
         } else {
            long tempo = 0L;

            try {
               tempo = DateUtils.parseDateDiff(args[1], true);
            } catch (Exception var9) {
               commandSender.sendMessage("§cTempo inválido.");
               return;
            }

            handleMute(args[0], StringUtility.createArgs(2, args), commandSender, tempo);
            commandSender.sendMessage("§aJogador mutado temporariamente com sucesso!");
         }
      }
   }

   @Command(
      name = "tempban",
      groupsToUse = {Groups.MOD},
      runAsync = true
   )
   public void tempban(BungeeCommandSender commandSender, String label, String[] args) {
      String baniu = commandSender.getNick();
      if (args.length < 3) {
         commandSender.sendMessage("§cUtilize /tempban (player) (tempo) (motivo)");
      } else {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
         } else {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
            DataHandler dataHandler = this.getDataHandlerByPlayer(target, nick);
            if (dataHandler == null) {
               commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            } else {
               String lastAddress = dataHandler.getString(DataType.LAST_IP);
               String clan = dataHandler.getString(DataType.CLAN);
               int codeBan = this.isBanned(nick, lastAddress);
               if (codeBan == -1) {
                  commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
               } else if (codeBan == 1) {
                  commandSender.sendMessage("§cEste jogador já está banido!");
               } else {
                  String motivo = StringUtility.createArgs(2, args);
                  boolean continuar = this.continueProcess(commandSender, target);
                  if (!continuar) {
                     commandSender.sendMessage("§cVocê não pode banir alguém com o cargo superior ao seu.");
                  } else {
                     long tempo;
                     try {
                        tempo = DateUtils.parseDateDiff(args[1], true);
                     } catch (Exception var16) {
                        commandSender.sendMessage("§cTempo inválido.");
                        return;
                     }

                     commandSender.sendMessage("§aJogador banido temporariamente com sucesso!".replace("%duração%", DateUtils.formatDifference(tempo)));
                     String data = DateUtils.getActualDate(false);
                     handleBan(target, nick, lastAddress, motivo, baniu, tempo, clan, data);
                     ProxyServer.getInstance().broadcast("§cUm jogador foi punido por Uso de trapaças em sua sala.".replace("%nick%", nick));
                     if (target != null) {
                        handleKickBan(target, motivo, baniu, data);
                     }

                  }
               }
            }
         }
      }
   }

   @Command(
      name = "ban",
      groupsToUse = {Groups.MOD},
      runAsync = true
   )
   public void ban(BungeeCommandSender commandSender, String label, String[] args) {
      String baniu = commandSender.getNick();
      if (args.length < 2) {
         commandSender.sendMessage("§cUtilize /ban (player) (motivo)");
      } else {
         String nick = MySQLManager.getString("accounts", "nick", args[0], "nick");
         if (nick.equalsIgnoreCase("N/A")) {
            commandSender.sendMessage("§cO jogador não possuí um registro na rede.");
         } else {
            ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
            DataHandler dataHandler = this.getDataHandlerByPlayer(target, nick);
            if (dataHandler == null) {
               commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
            } else {
               String lastAddress = dataHandler.getString(DataType.LAST_IP);
               String clan = dataHandler.getString(DataType.CLAN);
               int codeBan = this.isBanned(nick, lastAddress);
               if (codeBan == -1) {
                  commandSender.sendMessage("§cOcorreu um erro, tente novamente.");
               } else if (codeBan == 1) {
                  commandSender.sendMessage("§cEste jogador já está banido!");
               } else {
                  String motivo = StringUtility.createArgs(1, args);
                  boolean continuar = this.continueProcess(commandSender, target);
                  if (!continuar) {
                     commandSender.sendMessage("§cVocê não pode banir alguém com o cargo superior ao seu.");
                  } else {
                     commandSender.sendMessage("§aJogador banido permanentemente com sucesso!");
                     String data = DateUtils.getActualDate(false);
                     handleBan(target, nick, lastAddress, motivo, baniu, 0L, clan, data);
                     ProxyServer.getInstance().broadcast("§cUm jogador foi punido por Uso de trapaças em sua sala.".replace("%nick%", nick));
                     if (target != null) {
                        handleKickBan(target, motivo, baniu, data);
                     }

                  }
               }
            }
         }
      }
   }

   private int isBanned(String nick, String lastAddress) {
      PunishmentHistoric punish = new PunishmentHistoric(nick, lastAddress);

      try {
         punish.loadBans();
      } catch (Exception var5) {
         return -1;
      }

      if (punish.getActualBan() == null) {
         return 0;
      } else {
         return punish.getActualBan().isPunished() ? 1 : 0;
      }
   }

   private boolean continueProcess(BungeeCommandSender commandSender, ProxiedPlayer target) {
      if (commandSender.getNick().equalsIgnoreCase("CONSOLE")) {
         return true;
      } else if (!BungeeMain.isValid(target)) {
         return true;
      } else {
         BungeePlayer proxyPlayer = BungeeMain.getBungeePlayer(target.getName());
         BungeePlayer proxyExpulsou = BungeeMain.getBungeePlayer(commandSender.getNick());
         return proxyPlayer.getGroup().getLevel() <= proxyExpulsou.getGroup().getLevel();
      }
   }

   private DataHandler getDataHandlerByPlayer(ProxiedPlayer target, String nick) {
      DataHandler dataHandler = null;
      if (BungeeMain.isValid(target)) {
         dataHandler = BungeeMain.getBungeePlayer(target.getName()).getDataHandler();
      } else {
         try {
            dataHandler = new DataHandler(nick, CommonsGeneral.getUUIDFetcher().getUUID(nick));
         } catch (UUIDFetcherException var6) {
            var6.printStackTrace();
            return null;
         }

         try {
            dataHandler.load(new DataCategory[]{DataCategory.ACCOUNT});
         } catch (SQLException var5) {
            BungeeMain.console("§cOcorreu um erro ao tentar carregar a categoria 'ACCOUNT' de um jogador -> " + var5.getLocalizedMessage());
            dataHandler = null;
         }
      }

      return dataHandler;
   }

   private PunishmentHistoric getPunishmentHistoric(String nick, DataHandler dataHandler) {
      PunishmentHistoric punishmentHistoric = null;
      ProxiedPlayer target = ProxyServer.getInstance().getPlayer(nick);
      if (BungeeMain.isValid(target)) {
         punishmentHistoric = BungeeMain.getBungeePlayer(nick).getPunishmentHistoric();
      } else {
         punishmentHistoric = new PunishmentHistoric(nick, dataHandler.getString(DataType.LAST_IP));
      }

      return punishmentHistoric;
   }
}