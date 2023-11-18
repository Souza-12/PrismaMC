package com.br.teagadev.prismamc.commons.bukkit.scoreboard.tag;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.common.clan.Clan;
import com.br.teagadev.prismamc.commons.common.clan.ClanManager;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.profile.addons.League;
import com.br.teagadev.prismamc.commons.common.profile.addons.Medals;
import com.br.teagadev.prismamc.commons.common.tag.Tag;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Team;

public class TagManager {
   public static final boolean USE_TAGS = true;

   public static void setTag(Player player, Groups group) {
      setTag(player, group.getTag());
   }

   public static void setTag(Player player, Tag tag) {
      setTag(player, tag, CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId()));
   }

   public static void setTag(Player player, Tag playerTag, GamingProfile playerProfile) {
      String playerTeamID = playerTag.getTeamCharacter() + player.getUniqueId().toString().substring(0, 12);
      String playerPrefix = playerTag.getColor() + (playerTag.getLevel() == Groups.MEMBRO.getLevel() ? "" : "§l" + playerTag.getPrefix() + playerTag.getColor() + " ");
      String playerSuffix = getSuffix(playerProfile);
      Team playerTeam = createTeamIfNotExists(player, player.getName(), playerTeamID, playerPrefix, playerSuffix);
      cleanOldersTeam(player, player.getName(), playerTeam.getName());
      Iterator var7 = Bukkit.getOnlinePlayers().iterator();

      while(var7.hasNext()) {
         Player onlines = (Player)var7.next();
         if (onlines.getUniqueId() != player.getUniqueId()) {
            BukkitPlayer bp = BukkitMain.getBukkitPlayer(onlines.getUniqueId());
            String onlineTeamID = bp.getActualTag().getTeamCharacter() + onlines.getUniqueId().toString().substring(0, 12);
            String onlinePrefix = bp.getActualTag().getColor() + (bp.getActualTag().getLevel() == Groups.MEMBRO.getLevel() ? "" : "§l" + bp.getActualTag().getPrefix() + bp.getActualTag().getColor() + " ");
            String onlineSuffix = getSuffix(bp);
            Team onlineTeam = createTeamIfNotExists(player, onlines.getName(), onlineTeamID, onlinePrefix, onlineSuffix);
            cleanOldersTeam(player, onlines.getName(), onlineTeam.getName());
            createTeamIfNotExists(onlines, player.getName(), playerTeam.getName(), playerTeam.getPrefix(), playerTeam.getSuffix());
         }
      }

   }

   private static String getSuffix(GamingProfile profile) {
      String suffix = "";
      if (BukkitMain.getServerType().useSuffixRank()) {
         League league = League.getRanking(profile.getInt(DataType.XP));
         if (profile.containsFake()) {
            league = League.BronzeI;
         }

         suffix = " " + league.getColor() + league.getSymbol();
      } else if (!profile.containsFake() && !profile.getString(DataType.CLAN).equalsIgnoreCase("Nenhum") && profile.getBoolean(DataType.CLAN_TAG_DISPLAY)) {
         Clan clan = ClanManager.getClan(profile.getString(DataType.CLAN));
         suffix = " §7[" + clan.getTag() + "]";
      }

      Medals medal = Medals.getMedalById(profile.getInt(DataType.MEDAL));
      if (medal != null) {
         suffix = suffix + " " + medal.getColor() + medal.getSymbol();
      }

      return suffix;
   }

   private static void cleanOldersTeam(Player player, String entryName, String teamName) {
      Iterator var3 = player.getScoreboard().getTeams().iterator();

      while(var3.hasNext()) {
         Team team = (Team)var3.next();
         if (team.hasEntry(entryName) && !team.getName().equals(teamName)) {
            team.unregister();
         }
      }

   }

   public static void removePlayerTag(String name) {
      Iterator var1 = Bukkit.getOnlinePlayers().iterator();

      while(var1.hasNext()) {
         Player players = (Player)var1.next();
         Team entryTeam = players.getScoreboard().getEntryTeam(name);
         if (entryTeam != null && entryTeam.getEntries().contains(name)) {
            entryTeam.removeEntry(name);
            if (entryTeam.getEntries().isEmpty()) {
               entryTeam.unregister();
            }
         }
      }

   }

   private static Team createTeamIfNotExists(Player p, String entrie, String teamID, String prefix, String suffix) {
      if (p.getScoreboard() == null) {
         p.setScoreboard(Bukkit.getServer().getScoreboardManager().getNewScoreboard());
      }

      Team team = p.getScoreboard().getTeam(teamID);
      if (team == null) {
         team = p.getScoreboard().registerNewTeam(teamID);
      }

      if (!team.hasEntry(entrie)) {
         team.addEntry(entrie);
      }

      team.setPrefix(prefix);
      team.setSuffix(suffix);
      return team;
   }

   public static boolean hasPermission(Player player, Groups group) {
      return hasPermission(player, group.getTag());
   }

   public static boolean hasPermission(Player player, Tag tag) {
      if (tag.getLevel() == Groups.MEMBRO.getLevel()) {
         return true;
      } else if (player.hasPermission("tag.all")) {
         return true;
      } else if (player.hasPermission("tag." + tag.getName().toLowerCase())) {
         return true;
      } else {
         return CommonsGeneral.getProfileManager().getGamingProfile(player.getUniqueId()).getGroup().getLevel() >= tag.getLevel();
      }
   }

   public static List<Groups> getPlayerGroups(Player player) {
      List<Groups> list = new ArrayList();

      for(int i = Groups.values().length; i > 0; --i) {
         Groups tag = Groups.values()[i - 1];
         if (hasPermission(player, tag)) {
            list.add(tag);
         }
      }

      return list;
   }
}