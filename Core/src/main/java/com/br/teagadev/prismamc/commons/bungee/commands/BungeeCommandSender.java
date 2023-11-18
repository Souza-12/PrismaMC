package com.br.teagadev.prismamc.commons.bungee.commands;

import com.br.teagadev.prismamc.commons.common.command.CommandSender;
import java.beans.ConstructorProperties;
import java.util.Collection;
import java.util.UUID;
import lombok.NonNull;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.connection.ProxiedPlayer;

public class BungeeCommandSender implements CommandSender, net.md_5.bungee.api.CommandSender {
   @NonNull
   private net.md_5.bungee.api.CommandSender sender;

   public void addGroups(String... arg0) {
      this.sender.addGroups(arg0);
   }

   public Collection<String> getGroups() {
      return this.sender.getGroups();
   }

   public String getName() {
      return this.sender.getName();
   }

   public Collection<String> getPermissions() {
      return this.sender.getPermissions();
   }

   public boolean hasPermission(String arg0) {
      if (this.sender.hasPermission("commons.cmd.all")) {
         return true;
      } else if (this.sender.hasPermission("commons.cmd." + arg0)) {
         return true;
      } else {
         this.sender.sendMessage("§cVocê não tem permissão para usar este comando.");
         return false;
      }
   }

   public void removeGroups(String... arg0) {
      this.sender.removeGroups(arg0);
   }

   public void sendMessage(String arg0) {
      this.sender.sendMessage(TextComponent.fromLegacyText(arg0));
   }

   public void sendMessage(BaseComponent... arg0) {
      this.sender.sendMessage(arg0);
   }

   public void sendMessage(BaseComponent arg0) {
      this.sender.sendMessage(arg0);
   }

   public void sendMessages(String... arg0) {
      this.sender.sendMessages(arg0);
   }

   public void setPermission(String arg0, boolean arg1) {
      this.sender.setPermission(arg0, arg1);
   }

   public UUID getUniqueId() {
      return this.sender instanceof ProxiedPlayer ? ((ProxiedPlayer)this.sender).getUniqueId() : UUID.randomUUID();
   }

   public String getNick() {
      return this.sender instanceof ProxiedPlayer ? this.sender.getName() : "CONSOLE";
   }

   public boolean isPlayer() {
      if (this.sender instanceof ProxiedPlayer) {
         return true;
      } else {
         this.sender.sendMessage("§cComando disponível apenas para Jogadores.");
         return false;
      }
   }

   public ProxiedPlayer getPlayer() {
      return (ProxiedPlayer)this.sender;
   }

   @ConstructorProperties({"sender"})
   public BungeeCommandSender(@NonNull net.md_5.bungee.api.CommandSender sender) {
      if (sender == null) {
         throw new NullPointerException("sender");
      } else {
         this.sender = sender;
      }
   }
}