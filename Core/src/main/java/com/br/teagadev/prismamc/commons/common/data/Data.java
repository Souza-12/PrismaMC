package com.br.teagadev.prismamc.commons.common.data;

import java.util.List;

public class Data {
   private Object data;

   public Data(Object data) {
      this.data = data;
   }

   public void add() {
      this.add(1);
   }

   public void add(int quantia) {
      this.setValue(this.getInt() + quantia);
   }

   public void remove() {
      this.remove(1);
   }

   public void remove(int quantia) {
      int atual = this.getInt();
      if (atual - quantia < 0) {
         this.setValue(0);
      } else {
         this.setValue(atual - quantia);
      }
   }

   public void setValue(Object data) {
      this.data = data;
   }

   public Object getObject() {
      return this.data;
   }

   public String getString() {
      return (String)this.data;
   }

   public Integer getInt() {
      return (Integer)this.data;
   }

   public Long getLong() {
      return (Long)this.data;
   }

   public Boolean getBoolean() {
      return (Boolean)this.data;
   }

   public List<String> getList() {
      return (List)this.data;
   }

   public String toString() {
      return this.data != null ? this.data.toString() : "null";
   }
}