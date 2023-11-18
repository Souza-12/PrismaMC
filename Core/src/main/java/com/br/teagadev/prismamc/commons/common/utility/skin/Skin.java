package com.br.teagadev.prismamc.commons.common.utility.skin;

public class Skin {
   private final String value;
   private final String signature;
   private final String name;

   public Skin(String name, String value, String signature) {
      this.name = name;
      this.value = value;
      this.signature = signature;
   }

   public String getValue() {
      return this.value;
   }

   public String getSignature() {
      return this.signature;
   }

   public String getName() {
      return this.name;
   }
}