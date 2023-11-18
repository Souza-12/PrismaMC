package com.br.teagadev.prismamc.hardcoregames.ability;

import com.br.teagadev.prismamc.commons.bukkit.api.cooldown.CooldownAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.cooldown.types.Cooldown;
import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesOptions;
import com.br.teagadev.prismamc.hardcoregames.events.game.GameStartedEvent;
import com.br.teagadev.prismamc.hardcoregames.manager.gamer.GamerManager;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitLoader;
import com.br.teagadev.prismamc.hardcoregames.manager.kit.KitManager;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public abstract class Kit implements Listener {
   private String name;
   private List<String> description;
   private ItemStack icon;
   private int price;
   private int cooldownSeconds;
   private ItemStack[] itens;
   private final String itemColor = "§b";
   private boolean useInvincibility;
   private boolean callEventRegisterPlayer = false;
   private boolean listenerRegistred;
   private int amountUsing;

   public void initialize(String kitName) {
      this.setName(kitName);
      ItemStack icone = (new ItemBuilder()).type(Material.getMaterial(this.getKitsConfig().getString("kits." + this.getName() + ".icon.material"))).durability(this.getKitsConfig().getInt("kits." + this.getName() + ".icon.durability")).amount(this.getKitsConfig().getInt("kits." + this.getName() + ".icon.amount")).build();
      this.setIcon(icone);
      this.setItens(new ItemStack(Material.AIR));
      this.setPrice(this.getKitsConfig().getInt("kits." + this.getName() + ".price"));
      this.setCooldownSeconds(this.getKitsConfig().getInt("kits." + this.getName() + ".cooldown"));
      this.updateDescription(this.getKitsConfig().getStringList("kits." + this.getName() + ".icon.description"));
      this.setUseInvincibility(false);
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

   @EventHandler(
      priority = EventPriority.LOWEST
   )
   public void onDeath(PlayerDeathEvent event) {
      if (this.containsHability(event.getEntity())) {
         this.clean(event.getEntity());
      }

   }

   @EventHandler
   public void onStart(GameStartedEvent event) {
      this.setIcon((ItemStack)null);
   }

   public void setItens(ItemStack... itens) {
      this.itens = itens;
   }

   protected boolean containsHability(Player player) {
      return GamerManager.getGamer(player.getUniqueId()).containsKit(this.getName());
   }

   protected boolean useAbility(Player player) {
      if (HardcoreGamesMain.getGameManager().isPreGame()) {
         return false;
      } else if (HardcoreGamesOptions.KITS_DISABLEDS) {
         return false;
      } else if (KitManager.getKitsDesativados().contains(this.getName().toLowerCase())) {
         return false;
      } else {
         return HardcoreGamesMain.getGameManager().isInvencibilidade() && !this.isUseInvincibility() ? false : this.containsHability(player);
      }
   }

   public void registerListener() {
      if (!this.listenerRegistred) {
         this.listenerRegistred = true;
         Bukkit.getPluginManager().registerEvents(this, HardcoreGamesMain.getInstance());
         HardcoreGamesMain.console(this.getName() + " Listener has been registred!");
      }

   }

   public void addUsing() {
      ++this.amountUsing;
   }

   public void registerPlayer() {
      ++this.amountUsing;
      if (this.amountUsing == 1 && !HardcoreGamesMain.getGameManager().isPreGame()) {
         this.registerListener();
      }

   }

   public void cleanPlayer(Player player) {
      CooldownAPI.removeAllCooldowns(player);
      this.clean(player);
   }

   public void unregisterPlayer(Player player) {
      CooldownAPI.removeAllCooldowns(player);
      this.clean(player);
      --this.amountUsing;
      if (this.amountUsing == 0 && this.isListenerRegistred()) {
         HardcoreGamesMain.console(this.getName() + " Listener has been unregistred!");
         HandlerList.unregisterAll(this);
         this.listenerRegistred = false;
      }

   }

   protected abstract void clean(Player var1);

   protected boolean hasCooldown(Player player) {
      return CooldownAPI.hasCooldown(player, "Kit");
   }

   protected boolean hasCooldown(Player player, String cooldown) {
      return CooldownAPI.hasCooldown(player, cooldown);
   }

   protected void sendMessageCooldown(Player player) {
      CooldownAPI.sendMessage(player, "Kit");
   }

   protected void sendMessageCooldown(Player player, String cooldown) {
      CooldownAPI.sendMessage(player, cooldown);
   }

   protected void addCooldown(Player player, String cooldownName, long time) {
      if (CooldownAPI.hasCooldown(player, cooldownName)) {
         CooldownAPI.removeCooldown(player, cooldownName);
      }

      CooldownAPI.addCooldown(player, new Cooldown(cooldownName, time, this.playerOn18(player)));
   }

   protected void addCooldown(Player player, long time) {
      if (CooldownAPI.hasCooldown(player, "Kit")) {
         CooldownAPI.removeCooldown(player, "Kit");
      }

      CooldownAPI.addCooldown(player, new Cooldown("Kit", time, this.playerOn18(player)));
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

   public boolean isUseInvincibility() {
      return this.useInvincibility;
   }

   public boolean isCallEventRegisterPlayer() {
      return this.callEventRegisterPlayer;
   }

   public boolean isListenerRegistred() {
      return this.listenerRegistred;
   }

   public int getAmountUsing() {
      return this.amountUsing;
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

   public void setUseInvincibility(boolean useInvincibility) {
      this.useInvincibility = useInvincibility;
   }

   public void setCallEventRegisterPlayer(boolean callEventRegisterPlayer) {
      this.callEventRegisterPlayer = callEventRegisterPlayer;
   }

   public void setListenerRegistred(boolean listenerRegistred) {
      this.listenerRegistred = listenerRegistred;
   }

   public void setAmountUsing(int amountUsing) {
      this.amountUsing = amountUsing;
   }
}