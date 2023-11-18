package com.br.teagadev.prismamc.commons.common.serverinfo.enums;

public enum GameStages {
   UNKNOWN(0, "Unknown"),
   LOADING(1, "Carregando"),
   WAITING(2, "Waiting"),
   INVINCIBILITY(3, "Invincibility"),
   PLAYING(4, "Playing"),
   END(5, "End"),
   OFFLINE(0, "Offline");

   private final String nome;
   private final int id;

   private GameStages(int id, String nome) {
      this.nome = nome;
      this.id = id;
   }

   public static GameStages getStage(String nome) {
      GameStages finded = UNKNOWN;
      GameStages[] var2 = values();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         GameStages estagios = var2[var4];
         if (nome.equalsIgnoreCase(estagios.getNome())) {
            finded = estagios;
            break;
         }
      }

      return finded;
   }

   public String getNome() {
      return this.nome;
   }

   public int getId() {
      return this.id;
   }
}