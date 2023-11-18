package com.br.teagadev.prismamc.commons.common.group;

import com.br.teagadev.prismamc.commons.common.tag.Tag;
import java.util.Arrays;
import java.util.List;

public enum Groups {
   MEMBRO(new Tag("Membro", "", "§7", new String[]{"Default"}), new String[0]),
   SAPPHIRE(new Tag("Sapphire", "SAPPHIRE", "§3"), new String[0]),
   LEGEND(new Tag("Legend", "LEGEND", "§6"), new String[0]),
   RUBY(new Tag("Ruby", "RUBY", "§c"), new String[0]),
   BETA(new Tag("Beta", "BETA", "§1"), new String[0]),
   PARTNER(new Tag("Partner", "PARTNER", "§b"), new String[0]),
   STREAMER(new Tag("Streamer", "STREAMER", "§3", new String[]{"stream"}), new String[0]),
   YOUTUBER(new Tag("Youtuber", "YT", "§b", new String[]{"YT"}), new String[0]),
   YOUTUBER_PLUS(new Tag("Prime", "PRIME", "§b", new String[]{"plus", "yt+", "ytplus"}), new String[0]),
   TRIAL(new Tag("Trial", "TRIAL", "§d", new String[]{"TRIAL"}), new String[0]),
   MOD(new Tag("Mod", "MOD", "§5", new String[]{"moderador"}), new String[0]),
   MOD_PLUS(new Tag("Mod+", "MOD+", "§5", new String[]{"moderador+"}), new String[0]),
   ADMIN(new Tag("Admin", "ADMIN", "§4", new String[]{"adm", "Administrador"}), new String[0]),
   DEVELOPER(new Tag("Developer", "DEV", "§b", new String[]{"Dev", "Desenvolvedor"}), new String[0]),
   DONO(new Tag("Dono", "DONO", "§4", new String[]{"Owner"}), new String[0]);

   private final Tag tag;
   private List<String> permissions;

   private Groups(Tag tag, List<String> permissions) {
      this.tag = tag;
      this.permissions = permissions;
   }

   private Groups(Tag tag, String... permissions) {
      this.tag = tag;
      this.permissions = Arrays.asList(permissions);
   }

   public static Groups getGroup(String name) {
      Groups groupFinded = MEMBRO;
      Groups[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Groups groups = var2[var4];
         if (groups.getTag().getName().equalsIgnoreCase(name)) {
            groupFinded = groups;
            break;
         }

         String[] var6 = groups.getTag().getAliases();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String aliases = var6[var8];
            if (aliases.equalsIgnoreCase(name)) {
               groupFinded = groups;
               break;
            }
         }
      }

      return groupFinded;
   }

   public static Boolean existGrupo(String grupo) {
      boolean existe = false;
      Groups[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Groups groups = var2[var4];
         if (groups.getTag().getName().equalsIgnoreCase(grupo)) {
            existe = true;
            break;
         }

         String[] var6 = groups.getTag().getAliases();
         int var7 = var6.length;

         for(int var8 = 0; var8 < var7; ++var8) {
            String aliases = var6[var8];
            if (aliases.equalsIgnoreCase(grupo)) {
               existe = true;
               break;
            }
         }
      }

      return existe;
   }

   public void setPermissions(List<String> permissions) {
      this.permissions = permissions;
   }

   public int getLevel() {
      return this.getTag().getLevel();
   }

   public String getColor() {
      return this.getTag().getColor();
   }

   public String getName() {
      return this.getTag().getName();
   }

   public Tag getTag() {
      return this.tag;
   }

   public List<String> getPermissions() {
      return this.permissions;
   }
}