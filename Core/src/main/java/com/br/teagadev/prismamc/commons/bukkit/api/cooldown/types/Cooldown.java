package com.br.teagadev.prismamc.commons.bukkit.api.cooldown.types;

import java.util.concurrent.TimeUnit;

public class Cooldown {
   private final String name;
   private final Long duration;
   private final long startTime = System.currentTimeMillis();
   private final boolean barAPI;

   public Cooldown(String name, Long duration, boolean barAPI) {
      this.name = name;
      this.duration = duration;
      this.barAPI = barAPI;
   }

   public double getPercentage() {
      return this.getRemaining() * 100.0D / (double)this.duration;
   }

   public double getRemaining() {
      long endTime = this.startTime + TimeUnit.SECONDS.toMillis(this.duration);
      return (double)(-(System.currentTimeMillis() - endTime)) / 1000.0D;
   }

   public boolean expired() {
      return this.getRemaining() < 0.0D;
   }

   public String getName() {
      return this.name;
   }

   public Long getDuration() {
      return this.duration;
   }

   public boolean isBarAPI() {
      return this.barAPI;
   }
}