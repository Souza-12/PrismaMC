package com.br.teagadev.prismamc.commons.common.tag;

public class Tag {
   public static final char[] chars = new char[]{'1', '2', '3', '4', '5', '6', '7', '8', '9', 'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'};
   public static int globalLevels = 0;
   private String name;
   private String prefix;
   private String color;
   private String teamCharacter;
   private int level;
   private String[] aliases;

   public Tag(String name, String prefix, String color, String... aliases) {
      this.name = name;
      this.prefix = prefix;
      this.color = color;
      this.aliases = aliases;
      ++globalLevels;
      this.level = globalLevels;
      this.teamCharacter = chars[33 - globalLevels] + "";
   }

   public Tag(String name, String prefix, String color) {
      this(name, prefix, color, name);
   }

   public Tag(String name, String prefix) {
      this(name, prefix, prefix, name);
   }

   public boolean hasPrefix() {
      return !this.prefix.isEmpty() && this.prefix.length() > 1;
   }

   public String getName() {
      return this.name;
   }

   public String getPrefix() {
      return this.prefix;
   }

   public String getColor() {
      return this.color;
   }

   public String getTeamCharacter() {
      return this.teamCharacter;
   }

   public int getLevel() {
      return this.level;
   }

   public String[] getAliases() {
      return this.aliases;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setPrefix(String prefix) {
      this.prefix = prefix;
   }

   public void setColor(String color) {
      this.color = color;
   }

   public void setTeamCharacter(String teamCharacter) {
      this.teamCharacter = teamCharacter;
   }

   public void setLevel(int level) {
      this.level = level;
   }

   public void setAliases(String[] aliases) {
      this.aliases = aliases;
   }
}