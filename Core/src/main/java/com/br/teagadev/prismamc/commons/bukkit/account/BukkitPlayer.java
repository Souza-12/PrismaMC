package com.br.teagadev.prismamc.commons.bukkit.account;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.account.permission.PlayerAttachment;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerChangeTagEvent;
import com.br.teagadev.prismamc.commons.bukkit.custom.events.player.PlayerRequestEvent;
import com.br.teagadev.prismamc.commons.bukkit.scoreboard.tag.TagManager;
import com.br.teagadev.prismamc.commons.common.data.category.DataCategory;
import com.br.teagadev.prismamc.commons.common.data.type.DataType;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import com.br.teagadev.prismamc.commons.common.profile.GamingProfile;
import com.br.teagadev.prismamc.commons.common.profile.addons.League;
import com.br.teagadev.prismamc.commons.common.tag.Tag;
import com.mojang.authlib.properties.Property;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BukkitPlayer extends GamingProfile {
   private String lastMessage = "";
   private Long lastChangeSkin = 0L;
   private Property lastSkin;
   private PlayerAttachment playerAttachment;
   private Tag actualTag;

   public BukkitPlayer(String nick, String address, UUID uniqueId) {
      super(nick, address, uniqueId);
      this.actualTag = Groups.MEMBRO.getTag();
   }

   public void addXP(int amount) {
      League actualLeague = League.getRanking(this.getInt(DataType.XP));
      League newLeague = League.getRanking(this.getInt(DataType.XP) + amount);
      this.getData(DataType.XP).add(amount);
      if (actualLeague != newLeague) {
         Player player = this.getPlayer();
         Bukkit.broadcastMessage("§aParabéns! O jogador %nick% alcançou uma nova liga. Ela é %liga%".replace("%nick%", player.getName()).replace("%liga%", newLeague.getColor() + newLeague.getName().toUpperCase()));
         player.sendMessage("§aParabéns, Você subiu de liga! continue jogando e aprimorando suas habilidades.");
         TagManager.setTag(player, this.getActualTag(), this);
         Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "update-scoreboard"));
      }

   }

   public void removeXP(int amount) {
      League actualLeague = League.getRanking(this.getInt(DataType.XP));
      this.getData(DataType.XP).remove(amount);
      League newLeague = League.getRanking(this.getInt(DataType.XP));
      if (actualLeague != newLeague) {
         Player player = this.getPlayer();
         Bukkit.getServer().getPluginManager().callEvent(new PlayerRequestEvent(player, "update-scoreboard"));
         TagManager.setTag(player, this.getActualTag(), this);
      }

   }

   public void updateTag(Player player, Tag newTag, boolean forced) {
      PlayerChangeTagEvent event = new PlayerChangeTagEvent(player, this.getActualTag(), newTag, forced);
      Bukkit.getServer().getPluginManager().callEvent(event);
      if (!event.isCancelled()) {
         if (this.actualTag != newTag) {
            this.set(DataType.TAG, newTag.getName());
            BukkitMain.runAsync(() -> {
               this.getDataHandler().saveCategory(DataCategory.ACCOUNT);
            });
         }

         this.actualTag = newTag;
      }
   }

   public Player getPlayer() {
      return Bukkit.getPlayer(this.getUniqueId());
   }

   public void setLastMessage(String name) {
      this.lastMessage = name;
   }

   public boolean canChangeSkin() {
      return this.getLastChangeSkin() + TimeUnit.SECONDS.toMillis(40L) < System.currentTimeMillis();
   }

   public void injectPermissions(Player player) {
      if (this.playerAttachment == null) {
         this.playerAttachment = new PlayerAttachment(player, BukkitMain.getInstance());
      }

      this.playerAttachment.removePermissions(this.playerAttachment.getPermissions());
      this.playerAttachment.addPermissions(this.getGroup().getPermissions());
      List<String> permissions = this.getData(DataType.PERMISSIONS).getList();
      this.playerAttachment.addPermissions(permissions);
   }

   public void validateGroups() {
      Long groupTime = this.getLong(DataType.GROUP_TIME);
      if (groupTime != 0L && System.currentTimeMillis() > groupTime) {
         this.getData(DataType.GROUP_TIME).setValue(0L);
         this.getData(DataType.GROUP).setValue("Membro");
         this.getData(DataType.GROUP_CHANGED_BY).setValue("Console");
         Groups groupExpired = this.getGroup();
         if (this.getString(DataType.FAKE).isEmpty()) {
            this.setActualTag(this.getGroup().getTag());
            TagManager.setTag(this.getPlayer(), this.getGroup());
         } else {
            this.setActualTag(Groups.MEMBRO.getTag());
            TagManager.setTag(this.getPlayer(), Groups.MEMBRO);
         }

         Player player = this.getPlayer();
         player.sendMessage(String.format("§cO rank %s §cque você possuia teve seu tempo esgotado!", groupExpired.getColor() + groupExpired.getName()));
         this.injectPermissions(player);
         BukkitMain.runAsync(() -> {
            this.getDataHandler().saveCategory(DataCategory.ACCOUNT);
         });
      }

   }

   public String getLastMessage() {
      return this.lastMessage;
   }

   public Long getLastChangeSkin() {
      return this.lastChangeSkin;
   }

   public Property getLastSkin() {
      return this.lastSkin;
   }

   public PlayerAttachment getPlayerAttachment() {
      return this.playerAttachment;
   }

   public Tag getActualTag() {
      return this.actualTag;
   }

   public void setLastChangeSkin(Long lastChangeSkin) {
      this.lastChangeSkin = lastChangeSkin;
   }

   public void setLastSkin(Property lastSkin) {
      this.lastSkin = lastSkin;
   }

   public void setPlayerAttachment(PlayerAttachment playerAttachment) {
      this.playerAttachment = playerAttachment;
   }

   public void setActualTag(Tag actualTag) {
      this.actualTag = actualTag;
   }
}