package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Scout extends Kit {
	   public Scout() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.getMaterial(373)).durability(34).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent event) {
	      if (event.getAction() != Action.PHYSICAL) {
	         Player player = event.getPlayer();
	         if (player.getItemInHand().getType().equals(Material.getMaterial(373)) && this.hasAbility(player)) {
	            if (this.hasCooldown(player)) {
	               this.sendMessageCooldown(player);
	               return;
	            }

	            player.addPotionEffect(new PotionEffect(PotionEffectType.SPEED, 210, 2));
	            player.sendMessage("§aVocê recebeu velocidade por um tempo valido de 30 segundos!");
	            this.addCooldown(player, (long)this.getCooldownSeconds());
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}