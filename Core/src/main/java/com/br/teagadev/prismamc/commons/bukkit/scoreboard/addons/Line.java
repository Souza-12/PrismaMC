package com.br.teagadev.prismamc.commons.bukkit.scoreboard.addons;

public class Line {
   private String name;
   private String prefix;
   private String suffix;
   private int lineId;

   public Line(String name, String prefix, String suffix, int lineId) {
      this.name = name;
      this.prefix = prefix;
      this.suffix = suffix;
      this.lineId = lineId;
   }

   public String getName() {
      return this.name;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getSuffix() {
      return this.suffix;
   }

   public int getLineId() {
      return this.lineId;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void setSuffix(String suffix) {
      this.suffix = suffix;
   }

   public void setLineId(int lineId) {
      this.lineId = lineId;
   }
}