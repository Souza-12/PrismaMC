package com.br.teagadev.prismamc.commons.bukkit.scoreboard.sidebar;

import com.br.teagadev.prismamc.commons.bukkit.scoreboard.addons.Line;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.bukkit.ChatColor;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class Sidebar {
   private final Scoreboard scoreboard;
   private Objective objective;
   private boolean hided = true;
   private final HashMap<String, Line> lines;

   public Sidebar(Scoreboard scoreboard) {
      this.scoreboard = scoreboard;
      this.lines = new HashMap();
      this.show();
   }

   public void show() {
      if (this.hided) {
         this.hided = false;
         this.objective = this.scoreboard.registerNewObjective("sidebar", "dummy");
         this.objective.setDisplaySlot(DisplaySlot.SIDEBAR);
      }
   }

   public void hide() {
      if (!this.hided) {
         this.hided = true;

         for(int i = 1; i < 16; ++i) {
            Team team = this.scoreboard.getTeam("sidebar-" + i);
            if (team != null) {
               team.unregister();
               team = null;
            }
         }

         Objective sidebar = this.scoreboard.getObjective("sidebar");
         if (sidebar != null) {
            sidebar.unregister();
            sidebar = null;
         }

         this.lines.clear();
         this.objective = null;
         sidebar = null;
      }
   }

   public void showHealth() {
      if (this.scoreboard.getObjective("showhealth") == null) {
         Objective obj = this.scoreboard.registerNewObjective("showhealth", "health");
         obj.setDisplaySlot(DisplaySlot.BELOW_NAME);
         obj.setDisplayName("§c❤");
      }

   }

   public void addBlankLine() {
      int size = this.lines.size();
      this.addLine("blank-line-" + size, this.randomChar(size));
   }

   public void addLine(String text) {
      int size = this.lines.size();
      this.addLine("random-line-" + size, text);
   }

   public void addLine(String name, String prefix, String suffix) {
      this.lines.put(name.toLowerCase(), new Line(name, prefix, suffix, this.lines.size() + 1));
   }

   public void update() {
      if (!this.isHided()) {
         int size = Math.min(16, this.lines.size());
         int added = 0;
         List<Line> lineList = new ArrayList(this.lines.values());
         lineList.sort(Comparator.comparing(Line::getLineId));

         for(Iterator var4 = lineList.iterator(); var4.hasNext(); ++added) {
            Line line = (Line)var4.next();
            String name = line.getName();
            if (name.length() > 16) {
               name = name.substring(0, 16);
            }

            int slot = size - added;
            line.setLineId(slot);
            this.setText(slot, line.getPrefix(), line.getSuffix());
         }

      }
   }

   public void updateLine(String lineName, String text) {
      String prefix = "";
      String suffix = "";
      if (text.length() <= 16) {
         prefix = text;
      } else {
         prefix = text.substring(0, 16);
         suffix = ChatColor.getLastColors(prefix) + text.substring(16);
         if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
         }
      }

      this.updateLine(lineName, prefix, suffix);
   }

   public void addLine(String name, String text) {
      String prefix = "";
      String suffix = "";
      if (text.length() <= 16) {
         prefix = text;
      } else {
         prefix = text.substring(0, 16);
         suffix = ChatColor.getLastColors(prefix) + text.substring(16);
         if (suffix.length() > 16) {
            suffix = suffix.substring(0, 16);
         }
      }

      this.addLine(name, prefix, suffix);
   }

   public void updateLine(String lineName, String prefix, String suffix) {
      if (!this.isHided()) {
         Line line = (Line)this.lines.getOrDefault(lineName.toLowerCase(), (Line)null);
         if (line != null) {
            line.setPrefix(prefix);
            line.setSuffix(suffix);
            this.setText(line.getLineId(), line.getPrefix(), line.getSuffix());
            line = null;
         }
      }
   }

   private void setText(int line, String prefix, String suffix) {
      Team team = this.getScoreboard().getTeam("sidebar-" + line);
      if (team == null) {
         team = this.createTeam(line);
      }

      team.setPrefix(prefix);
      team.setSuffix(suffix);
      suffix = null;
      prefix = null;
   }

   private Team createTeam(int line) {
      Team team = this.getScoreboard().registerNewTeam("sidebar-" + line);
      String score = ChatColor.values()[line - 1].toString();
      this.getObjective().getScore(score).setScore(line);
      if (!team.hasEntry(score)) {
         team.addEntry(score);
      }

      score = null;
      return team;
   }

   public void setTitle(String name) {
      this.objective.setDisplayName(name);
   }

   public String randomChar(int slot) {
      return ChatColor.values()[slot].toString() + ChatColor.RESET;
   }

   public Scoreboard getScoreboard() {
      return this.scoreboard;
   }

   public Objective getObjective() {
      return this.objective;
   }

   public boolean isHided() {
      return this.hided;
   }

   public HashMap<String, Line> getLines() {
      return this.lines;
   }
}