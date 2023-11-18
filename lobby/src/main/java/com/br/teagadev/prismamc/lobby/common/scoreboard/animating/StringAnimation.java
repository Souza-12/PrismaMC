package com.br.teagadev.prismamc.lobby.common.scoreboard.animating;

import java.util.ArrayList;
import java.util.List;

public class StringAnimation {
   private String string;
   private List<String> strings;
   private int index;
   private boolean bools;

   public StringAnimation(String title, String color1, String color2, String color3) {
      this(title, color1, color2, color3, 12);
   }

   public StringAnimation(String title, String color1, String color2, String color3, int value) {
      this.string = title;
      this.strings = new ArrayList();
      this.create(color1, color2, color3, value);
   }

   public void create(String color1, String color2, String color3, int value) {
      if (this.string != null && !this.string.isEmpty()) {
         int i;
         for(i = 0; i < this.string.length(); ++i) {
            if (this.string.charAt(i) != ' ') {
               this.strings.add(color1 + this.string.substring(0, i) + color2 + this.string.charAt(i) + color3 + this.string.substring(i + 1));
            }
         }

         for(i = 0; i < value; ++i) {
            this.strings.add(color1 + this.string);
         }

         for(i = 0; i < this.string.length(); ++i) {
            if (this.string.charAt(i) != ' ') {
               this.strings.add(color3 + this.string.substring(0, i) + color2 + this.string.charAt(i) + color1 + this.string.substring(i + 1));
            }
         }

         for(i = 0; i < value; ++i) {
            this.strings.add(color3 + this.string);
         }
      }

   }

   public String next() {
      if (this.strings.isEmpty()) {
         return "";
      } else {
         if (this.bools) {
            --this.index;
            if (this.index <= 0) {
               this.bools = false;
            }
         } else {
            ++this.index;
            if (this.index >= this.strings.size()) {
               this.bools = true;
               return this.next();
            }
         }

         return (String)this.strings.get(this.index);
      }
   }
}