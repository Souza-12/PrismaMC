package com.br.teagadev.prismamc.commons.bukkit.listeners;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.BukkitSettings;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.common.clan.Clan;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.addons.League;
import com.br.teagadev.prismamc.commons.common.profile.addons.Medals;
import com.br.teagadev.prismamc.commons.common.serverinfo.ServerType;
import java.util.Iterator;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.help.HelpTopic;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;

public class ChatListener implements Listener {
   private final String CHAT_COOLDOWN_TAG = "chat-cooldown";
   private final String DELAY_COMMAND_TAG = "command-cooldown";
   private final Long CHAT_COOLDOWN_TIME = 2000L;
   private final Long COMMAND_COOLDOWN_TIME = 1000L;
   private final String[] cmdsBlockeds = new String[]{"/?", "/bukkit:", "/say", "/kill", "/msg", "/me", "/w", "/help", "minecraft:", "/calc", "//calc", "calc"};

   @EventHandler(
      priority = EventPriority.HIGHEST,
      ignoreCancelled = true
   )
   public void onAsyncPlayerChat(AsyncPlayerChatEvent event) {
      Player player = event.getPlayer();
      BukkitPlayer profile = (BukkitPlayer)CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId());
      if (!BukkitSettings.CHAT_OPTION && profile.getGroup().getLevel() < Groups.TRIAL.getLevel()) {
         event.setCancelled(true);
         player.sendMessage("§cO chat está desativado.");
      } else {
         if (profile.getGroup().getLevel() < Groups.TRIAL.getLevel()) {
            event.setCancelled(this.inChatCooldown(player));
         }

         if (!event.isCancelled()) {
            event.setCancelled(true);
            String message = event.getMessage();
            if (message.contains("%")) {
               message = message.replaceAll("%", "%%");
            }

            if (message.contains("&") && profile.getGroup().getLevel() > Groups.MEMBRO.getLevel()) {
               message = message.replaceAll("&", "§");
            }

            String prefix = "";
            Medals medal = Medals.getMedalById(profile.getInt(DataType.MEDAL));
            if (medal != null) {
               prefix = medal.getColor() + medal.getSymbol() + " ";
            }

            if (!profile.getString(DataType.CLAN).equalsIgnoreCase("Nenhum")) {
               Clan clan = ClanManager.getClan(profile.getString(DataType.CLAN));
               prefix = prefix + "§7[" + (clan.isPremium() ? "§6" : "") + clan.getTag() + "] ";
            }

            League league = League.getRanking(profile.getInt(DataType.XP));
            if (profile.getActualTag().getLevel() != Groups.MEMBRO.getLevel()) {
               prefix = prefix + profile.getActualTag().getColor() + "§l" + profile.getActualTag().getPrefix() + profile.getActualTag().getColor() + " " + player.getName();
            } else {
               prefix = prefix + "§7" + player.getName();
            }

            String formattedMessage;
            Iterator var9;
            Player onlines;
            if (BukkitMain.getServerType() != ServerType.LOBBY && BukkitMain.getServerType() != ServerType.LOBBY_PVP && BukkitMain.getServerType() != ServerType.LOBBY_HARDCOREGAMES && BukkitMain.getServerType() != ServerType.LOBBY_DUELS) {
               prefix = "§7[" + league.getColor() + league.getSymbol() + "§7] " + prefix;
               formattedMessage = prefix + "§7 » §f" + message;
               var9 = event.getRecipients().iterator();

               while(var9.hasNext()) {
                  onlines = (Player)var9.next();
                  onlines.sendMessage(formattedMessage);
               }
            } else {
               prefix = prefix + "";
               formattedMessage = prefix + "§7 » §f" + message;
               var9 = event.getRecipients().iterator();

               while(var9.hasNext()) {
                  onlines = (Player)var9.next();
                  onlines.sendMessage(formattedMessage);
               }
            }

         }
      }
   }

   private boolean inChatCooldown(Player player) {
      boolean cooldown = false;
      if (player.hasMetadata("chat-cooldown")) {
         if (((MetadataValue)player.getMetadata("chat-cooldown").get(0)).asLong() + this.CHAT_COOLDOWN_TIME > System.currentTimeMillis()) {
            player.sendMessage("§cAguarde para digitar no chat novamente.");
            cooldown = true;
         } else {
            player.setMetadata("chat-cooldown", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
         }
      } else {
         player.setMetadata("chat-cooldown", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
      }

      return cooldown;
   }

   private boolean inCommandDelay(Player player) {
      boolean cooldown = false;
      if (player.hasMetadata("command-cooldown")) {
         if (((MetadataValue)player.getMetadata("command-cooldown").get(0)).asLong() + this.COMMAND_COOLDOWN_TIME > System.currentTimeMillis()) {
            player.sendMessage("§cVocê está tentando executar comandos muito rápido!");
            cooldown = true;
         } else {
            player.setMetadata("command-cooldown", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
         }
      } else {
         player.setMetadata("command-cooldown", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis()));
      }

      return cooldown;
   }

   @EventHandler
   public void onQuit(PlayerQuitEvent event) {
      if (event.getPlayer().hasMetadata("chat-cooldown")) {
         event.getPlayer().removeMetadata("chat-cooldown", BukkitMain.getInstance());
      }

      if (event.getPlayer().hasMetadata("command-cooldown")) {
         event.getPlayer().removeMetadata("command-cooldown", BukkitMain.getInstance());
      }

   }

   @EventHandler(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerCommandPreprocess(PlayerCommandPreprocessEvent event) {
      if (!event.isCancelled()) {
         if (!event.getPlayer().isOp() && this.inCommandDelay(event.getPlayer())) {
            event.setCancelled(true);
            return;
         }

         String cmd = event.getMessage().split(" ")[0];
         HelpTopic topic = Bukkit.getServer().getHelpMap().getHelpTopic(cmd);
         if (topic == null) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cEste comando não existe.");
         }
      }

   }

   @EventHandler
   public void onPlayerCommandPreprocess2(PlayerCommandPreprocessEvent event) {
      String message = event.getMessage().toLowerCase();
      if (message.equals("stop")) {
         event.setCancelled(true);
         event.getPlayer().chat("/parar");
      } else if (!message.equals("/pl") && !message.equals("/plugin") && !message.startsWith("ver") && !message.startsWith("icanhasbukkit") && !message.equals("/plugins") && !message.startsWith("/help") && !message.startsWith("/bukkit:")) {
         boolean block = false;
         String[] var4 = this.cmdsBlockeds;
         int var5 = var4.length;

         for(int var6 = 0; var6 < var5; ++var6) {
            String cmds = var4[var6];
            if (message.startsWith(cmds) && !cmds.equalsIgnoreCase("/w") && !message.equalsIgnoreCase("/killmobs") && !message.equalsIgnoreCase("/whitelist") && !message.contains("medal") && !message.contains("memory") && !message.equalsIgnoreCase("/wand")) {
               block = true;
               break;
            }
         }

         if (block) {
            event.setCancelled(true);
            event.getPlayer().sendMessage("§cEste comando está bloqueado em nosso servidor.");
         }

      } else {
         event.setCancelled(true);
      }
   }
}