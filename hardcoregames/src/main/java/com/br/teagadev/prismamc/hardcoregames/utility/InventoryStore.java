package com.br.teagadev.prismamc.hardcoregames.utility;

import java.util.List;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;

public class InventoryStore {
   private String nome;
   private ItemStack[] armor;
   private ItemStack[] inv;
   private List<PotionEffect> potions;

   public InventoryStore(String nome, ItemStack[] armor, ItemStack[] inv, List<PotionEffect> potions) {
      this.nome = nome;
      this.armor = armor;
      this.inv = inv;
      this.potions = potions;
   }

   public String getNome() {
      return this.nome;
   }

   public ItemStack[] getArmor() {
      return this.armor;
   }

   public ItemStack[] getInv() {
      return this.inv;
   }

   public List<PotionEffect> getPotions() {
      return this.potions;
   }

   public void setNome(String nome) {
      this.nome = nome;
   }

   public void setArmor(ItemStack[] armor) {
      this.armor = armor;
   }

   public void setInv(ItemStack[] inv) {
      this.inv = inv;
   }

   public void setPotions(List<PotionEffect> potions) {
      this.potions = potions;
   }
}