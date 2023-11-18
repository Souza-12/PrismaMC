package com.br.teagadev.prismamc.commons.common.utility;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class Cache {
   private String name;
   private Object value1;
   private Object value2;
   private long createdTime;
   private long expireAfter;
   private int used;

   public Cache(String name, Object value1, Object value2, int expireAfter) {
      this.name = name;
      this.value1 = value1;
      this.value2 = value2;
      this.createdTime = System.currentTimeMillis();
      this.used = 0;
      this.expireAfter = System.currentTimeMillis() + TimeUnit.HOURS.toMillis((long)expireAfter);
   }

   public Cache(String name, Object value1) {
      this(name, value1, (Object)null, 1);
   }

   public Cache(String name, Object value1, int expireAfter) {
      this(name, value1, (Object)null, expireAfter);
   }

   public Cache(String name, Object value1, Object value2) {
      this(name, value1, value2, 1);
   }

   public Boolean getBoolean1() {
      ++this.used;
      this.expireAfter += TimeUnit.MINUTES.toMillis(5L);
      return (Boolean)this.value1;
   }

   public Boolean getBoolean2() {
      ++this.used;
      this.expireAfter += TimeUnit.MINUTES.toMillis(5L);
      return (Boolean)this.value2;
   }

   public String getString1() {
      ++this.used;
      this.expireAfter += TimeUnit.MINUTES.toMillis(5L);
      return (String)this.value1;
   }

   public String getString2() {
      ++this.used;
      this.expireAfter += TimeUnit.MINUTES.toMillis(5L);
      return (String)this.value2;
   }

   public UUID getUUID() {
      ++this.used;
      this.expireAfter += TimeUnit.MINUTES.toMillis(5L);
      return (UUID)this.value1;
   }

   public boolean isExpired() {
      return this.expireAfter < System.currentTimeMillis();
   }

   public String getName() {
      return this.name;
   }

   public Object getValue1() {
      return this.value1;
   }

   public Object getValue2() {
      return this.value2;
   }

   public long getCreatedTime() {
      return this.createdTime;
   }

   public long getExpireAfter() {
      return this.expireAfter;
   }

   public int getUsed() {
      return this.used;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setValue1(Object value1) {
      this.value1 = value1;
   }

   public void setValue2(Object value2) {
      this.value2 = value2;
   }

   public void setCreatedTime(long createdTime) {
      this.createdTime = createdTime;
   }

   public void setExpireAfter(long expireAfter) {
      this.expireAfter = expireAfter;
   }

   public void setUsed(int used) {
      this.used = used;
   }
}