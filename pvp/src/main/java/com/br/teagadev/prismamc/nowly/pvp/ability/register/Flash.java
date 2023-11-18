package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.HashSet;

import org.bukkit.Effect;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.BlockIterator;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;

public class Flash extends Kit {
	   public Flash() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.REDSTONE_TORCH_ON).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent e) {
	      if (e.getAction() != Action.PHYSICAL) {
	         if (this.hasAbility(e.getPlayer()) && this.checkItem(e.getItem(), this.getItemColor() + "Kit " + this.getName())) {
	            if (e.getAction() == Action.RIGHT_CLICK_BLOCK) {
	               e.setCancelled(true);
	               e.getPlayer().updateInventory();
	               return;
	            }

	            if (e.getAction() != Action.RIGHT_CLICK_AIR) {
	               return;
	            }

	            e.setCancelled(true);
	            Player p = e.getPlayer();
	            if (this.hasCooldown(p)) {
	               this.sendMessageCooldown(p);
	               return;
	            }

	            @SuppressWarnings("deprecation")
				Block b = e.getPlayer().getTargetBlock((HashSet<Byte>) null, 25).getLocation().getBlock();
	            if (b.getType() == Material.AIR) {
	               p.sendMessage("§cEscolha outro bloco para se teleportar.");
	               return;
	            }

	            BlockIterator list = new BlockIterator(p.getEyeLocation(), 0.0D, 100);

	            while(list.hasNext()) {
	               p.getWorld().playEffect(list.next().getLocation(), Effect.ENDER_SIGNAL, 100);
	            }

	            p.teleport(b.getLocation().clone().add(0.0D, 1.5D, 0.0D));
	            p.playSound(p.getLocation(), Sound.ENDERMAN_TELEPORT, 1.0F, 1.0F);
	            this.addCooldown(p, (long)this.getCooldownSeconds());
	         }

	      }
	   }

	   protected void clean(Player player) {
	   }
	}