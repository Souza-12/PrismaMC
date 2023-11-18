package com.br.teagadev.prismamc.commons.bukkit.commands.register;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.BukkitPlayer;
import com.br.teagadev.prismamc.commons.bukkit.commands.BukkitCommandSender;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.profile.addons.League;
import org.bukkit.entity.Player;

public class RankCommand implements CommandClass {
   @Command(
      name = "rank",
      aliases = {"rank", "liga", "ligas", "nivel", "level", "nvl", "lvl"}
   )
   public void rank(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
         int XP = bukkitPlayer.getInt(DataType.XP);
         League liga = League.getRanking(XP);
         int leftXp = liga.getExperience() - XP;
         int porcentLevel = liga.getExperience() - XP;
         String barExp = this.display(player, leftXp, liga.getExperience());
         League next = liga.getNextLeague();
         player.sendMessage("");
         player.sendMessage("§f" + liga.getColor() + liga.getName() + " §7" + barExp + "§7> §f" + next.getNextLeague().getColor() + next.getNextLeague().getName() + " §7" + XP + "§7/" + next.getNextLeague().getExperience() + "§7 XP's§f (" + porcentLevel * 100 / liga.getExperience() + "§f%)");
         player = null;
         bukkitPlayer = null;
         liga = null;
      }
   }

   @Command(
      name = "ranklist",
      aliases = {"ranks", "lvllist", "leaguelist", "ligalist", "ligal"}
   )
   public void rankList(BukkitCommandSender commandSender, String label, String[] args) {
      if (commandSender.isPlayer()) {
         Player player = commandSender.getPlayer();
         BukkitPlayer bukkitPlayer = BukkitMain.getBukkitPlayer(player.getUniqueId());
         int XP = bukkitPlayer.getInt(DataType.XP);
         League liga = League.getRanking(XP);
         int leftXp = liga.getExperience() - XP;
         int porcentLevel = liga.getExperience() + XP;
         this.display(player, leftXp, liga.getExperience());
         League next = liga.getNextLeague();
         player.sendMessage("");
         player.sendMessage("");
         player.sendMessage("§aLista de ranks:");
         player.sendMessage(" §4❂ Champion");
         player.sendMessage(" §c✿ Immortal: " + League.ImmortalI.getSymbol() + "§f, §c" + League.ImmortalII.getSymbol() + "§f, §c" + League.ImmortalIII.getSymbol() + "§f, §c" + League.ImmortalIV.getSymbol());
         player.sendMessage(" §5✦ Enderlore: " + League.EnderloreI.getSymbol() + "§f, §5" + League.EnderloreII.getSymbol() + "§f, §5" + League.EnderloreIII.getSymbol() + "§f, §5" + League.EnderloreIV.getSymbol() + "§f, §5" + League.EnderloreV.getSymbol());
         player.sendMessage(" §b❆ Diamond: " + League.DiamondI.getSymbol() + "§f, §b" + League.DiamondII.getSymbol() + "§f, §b" + League.DiamondIII.getSymbol() + "§f, §b" + League.DiamondIV.getSymbol() + "§f, §b" + League.DiamondV.getSymbol());
         player.sendMessage(" §3❖ Platinum: " + League.PlatinumI.getSymbol() + "§f, §3" + League.PlatinumII.getSymbol() + "§f, §3" + League.PlatinumIII.getSymbol() + "§f, §3" + League.PlatinumIV.getSymbol() + "§f, §3" + League.PlatinumV.getSymbol());
         player.sendMessage(" §6✻ Gold: " + League.GoldI.getSymbol() + "§f, §6" + League.GoldII.getSymbol() + "§f, §6" + League.GoldIII.getSymbol() + "§f, §6" + League.GoldIV.getSymbol() + "§f, §6" + League.GoldV.getSymbol());
         player.sendMessage(" §7✯ Bronze: " + League.BronzeI.getSymbol() + "§f, §7" + League.BronzeII.getSymbol() + "§f, §7" + League.BronzeIII.getSymbol() + "§f, §7" + League.BronzeIV.getSymbol() + "§f, §7" + League.BronzeV.getSymbol());
         player.sendMessage("");
         player = null;
         bukkitPlayer = null;
         liga = null;
      }
   }

   private String display(Player player, int remaing, int total) {
      StringBuilder bar = new StringBuilder();
      double percentage = (double)(remaing * 100 / total);
      double count = 20.0D - Math.max(percentage > 0.0D ? 1.0D : 0.0D, percentage / 5.0D);

      int a;
      for(a = 0; (double)a < count; ++a) {
         bar.append("§a§m-");
      }

      for(a = 0; (double)a < 20.0D - count; ++a) {
         bar.append("§7§m-");
      }

      return bar.toString();
   }
}