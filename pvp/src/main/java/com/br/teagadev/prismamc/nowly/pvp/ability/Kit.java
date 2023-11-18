package com.br.teagadev.prismamc.nowly.pvp.ability;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.br.teagadev.prismamc.commons.bukkit.api.cooldown.CooldownAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.teagadev.prismamc.nowly.pvp.PvPMain;
import com.br.teagadev.prismamc.nowly.pvp.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.nowly.pvp.manager.kit.KitLoader;

import lombok.Getter;
import lombok.Setter;

public abstract class Kit implements Listener {
	   private int amountUsing;
	   private String name;
	   private List<String> description;
	   private ItemStack icon;
	   private int price;
	   private int cooldownSeconds;
	   private ItemStack[] itens;
	   private final String itemColor = "§b";

	   public void initialize(String kitName) {
	      this.setName(kitName);
	      ItemStack icone = (new ItemBuilder()).type(Material.getMaterial(this.getKitsConfig().getString("kits." + this.getName() + ".icon.material"))).durability(this.getKitsConfig().getInt("kits." + this.getName() + ".icon.durability")).amount(this.getKitsConfig().getInt("kits." + this.getName() + ".icon.amount")).build();
	      this.setIcon(icone);
	      this.setItens(new ItemStack(Material.AIR));
	      this.setPrice(this.getKitsConfig().getInt("kits." + this.getName() + ".price"));
	      this.setCooldownSeconds(this.getKitsConfig().getInt("kits." + this.getName() + ".cooldown"));
	      this.updateDescription(this.getKitsConfig().getStringList("kits." + this.getName() + ".icon.description"));
	      icone = null;
	   }

	   public void updateDescription(List<String> list) {
	      this.description = list;
	      ItemMeta meta = this.getIcon().getItemMeta();
	      List<String> lore = new ArrayList();
	      Iterator var4 = this.description.iterator();

	      while(var4.hasNext()) {
	         String descriptionL = (String)var4.next();
	         lore.add(descriptionL.replaceAll("&", "§"));
	      }

	      meta.setDisplayName("§a" + this.getName());
	      meta.setLore(lore);
	      this.getIcon().setItemMeta(meta);
	      this.setAmountUsing(0);
	      lore.clear();
	      meta = null;
	      lore = null;
	   }

	   protected abstract void clean(Player var1);

	   public void setItens(ItemStack... itens) {
	      this.itens = itens;
	   }

	   protected boolean hasAbility(Player player) {
	      return GamerManager.getGamer(player.getUniqueId()).containsKit(this.getName());
	   }

	   public void registerPlayer() {
	      ++this.amountUsing;
	      if (this.amountUsing == 1) {
	         Bukkit.getPluginManager().registerEvents(this, PvPMain.getInstance());
	      }

	   }

	   public void unregisterPlayer(Player player) {
	      if (CooldownAPI.hasCooldown(player, this.getName())) {
	         CooldownAPI.removeCooldown(player, this.getName());
	      }

	      this.clean(player);
	      --this.amountUsing;
	      if (this.amountUsing == 0) {
	         HandlerList.unregisterAll(this);
	      }

	   }

	   protected boolean hasCooldown(Player player, String cooldownName) {
	      return CooldownAPI.hasCooldown(player, cooldownName);
	   }

	   protected boolean hasCooldown(Player player) {
	      return CooldownAPI.hasCooldown(player, this.getName());
	   }

	   protected void sendMessageCooldown(Player player) {
	      CooldownAPI.sendMessage(player, this.getName());
	   }

	   protected void addCooldown(Player player, long time) {
	      if (CooldownAPI.hasCooldown(player, this.getName())) {
	         CooldownAPI.removeCooldown(player, this.getName());
	      }

	      CooldownAPI.addCooldown(player, new Cooldown(this.getName(), time, this.playerOn18(player)));
	   }

	   protected void addCooldown(Player player, String cooldownName, long time) {
	      if (CooldownAPI.hasCooldown(player, cooldownName)) {
	         CooldownAPI.removeCooldown(player, cooldownName);
	      }

	      CooldownAPI.addCooldown(player, new Cooldown(cooldownName, time, this.playerOn18(player)));
	   }

	   public boolean playerOn18(Player player) {
	      return ProtocolGetter.getVersion(player) > 5;
	   }

	   public FileConfiguration getKitsConfig() {
	      return KitLoader.getKitsConfig().getConfiguration();
	   }

	   public boolean checkItem(ItemStack item, String display) {
	      return item != null && item.getType() != Material.AIR && item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().getDisplayName().startsWith(display);
	   }

	   public int getAmountUsing() {
	      return this.amountUsing;
	   }

	   public String getName() {
	      return this.name;
	   }

	   public List<String> getDescription() {
	      return this.description;
	   }

	   public ItemStack getIcon() {
	      return this.icon;
	   }

	   public int getPrice() {
	      return this.price;
	   }

	   public int getCooldownSeconds() {
	      return this.cooldownSeconds;
	   }

	   public ItemStack[] getItens() {
	      return this.itens;
	   }

	   public String getItemColor() {
	      this.getClass();
	      return "§b";
	   }

	   public void setAmountUsing(int amountUsing) {
	      this.amountUsing = amountUsing;
	   }

	   public void setName(String name) {
	      this.name = name;
	   }

	   public void setDescription(List<String> description) {
	      this.description = description;
	   }

	   public void setIcon(ItemStack icon) {
	      this.icon = icon;
	   }

	   public void setPrice(int price) {
	      this.price = price;
	   }

	   public void setCooldownSeconds(int cooldownSeconds) {
	      this.cooldownSeconds = cooldownSeconds;
	   }
	}