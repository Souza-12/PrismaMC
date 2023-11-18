package com.br.teagadev.prismamc.commons.common.clan;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.common.PluginInstance;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.google.gson.JsonObject;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import org.bukkit.entity.Player;

public class Clan {
   private String name;
   private String tag;
   private String owner;
   private boolean premium;
   private int maxParticipants;
   private List<String> memberList;
   private List<String> adminList;
   private List<Object> onlines;

   public Clan(String clanName, String tag) {
      this.name = clanName;
      this.tag = tag;
      this.memberList = new ArrayList();
      this.adminList = new ArrayList();
      this.onlines = new ArrayList();
   }

   public void addMember(String name) {
      if (!this.memberList.contains(name)) {
         this.memberList.add(name);
      }

   }

   public void removePlayer(String name) {
      this.adminList.remove(name);
      this.memberList.remove(name);
   }

   public void promote(String name) {
      if (!this.adminList.contains(name)) {
         this.adminList.add(name);
      }

   }

   public void unpromote(String name) {
      this.adminList.remove(name);
   }

   public boolean isAdmin(String name) {
      return this.getOwner().equalsIgnoreCase(name) ? true : this.adminList.contains(name);
   }

   public void removeOnline(Object object) {
      this.getOnlines().remove(object);
   }

   public void addOnline(Object object) {
      if (!this.getOnlines().contains(object)) {
         this.getOnlines().add(object);
      }

   }

   public JsonObject getJSON() {
      JsonObject json = new JsonObject();
      json.addProperty("dono", this.getOwner());
      json.addProperty("tag", this.getTag());
      json.addProperty("premium", this.isPremium());
      json.addProperty("maxParticipants", this.getMaxParticipants());
      json.addProperty("admins", StringUtility.formatStringToArrayWithoutSpace(this.getAdminList()));
      json.addProperty("membros", StringUtility.formatStringToArrayWithoutSpace(this.getMemberList()));
      return json;
   }

   public void message(String message) {
      Iterator var2;
      Object players;
      Player player;
      if (CommonsGeneral.getPluginInstance() == PluginInstance.BUKKIT) {
         for(var2 = this.onlines.iterator(); var2.hasNext(); player = null) {
            players = var2.next();
            player = (Player)players;
            player.sendMessage(message);
         }
      } else {
         for(var2 = this.onlines.iterator(); var2.hasNext(); player = null) {
            players = var2.next();
            ProxiedPlayer player1 = (ProxiedPlayer)players;
            player1.sendMessage(message);
         }
      }

   }

   public String getName() {
      return this.name;
   }

   public String getTag() {
      return this.tag;
   }

   public String getOwner() {
      return this.owner;
   }

   public boolean isPremium() {
      return this.premium;
   }

   public int getMaxParticipants() {
      return this.maxParticipants;
   }

   public List<String> getMemberList() {
      return this.memberList;
   }

   public List<String> getAdminList() {
      return this.adminList;
   }

   public List<Object> getOnlines() {
      return this.onlines;
   }

   public void setName(String name) {
      this.name = name;
   }

   public void setTag(String tag) {
      this.tag = tag;
   }

   public void setOwner(String owner) {
      this.owner = owner;
   }

   public void setPremium(boolean premium) {
      this.premium = premium;
   }

   public void setMaxParticipants(int maxParticipants) {
      this.maxParticipants = maxParticipants;
   }

   public void setMemberList(List<String> memberList) {
      this.memberList = memberList;
   }

   public void setAdminList(List<String> adminList) {
      this.adminList = adminList;
   }

   public void setOnlines(List<Object> onlines) {
      this.onlines = onlines;
   }
}