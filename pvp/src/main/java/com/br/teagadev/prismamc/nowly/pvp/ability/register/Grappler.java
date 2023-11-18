package com.br.teagadev.prismamc.nowly.pvp.ability.register;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.br.teagadev.prismamc.commons.bukkit.utility.LocationUtil;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftSnowball;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Firework;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Snowball;
import org.bukkit.event.EventHandler;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.PlayerLeashEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.util.Vector;

import com.br.teagadev.prismamc.commons.bukkit.api.item.ItemBuilder;
import com.br.teagadev.prismamc.nowly.pvp.ability.Kit;
import com.br.teagadev.prismamc.nowly.pvp.events.PlayerDamagePlayerEvent;

import net.minecraft.server.v1_8_R3.EntityFishingHook;
import net.minecraft.server.v1_8_R3.EntityHuman;
import net.minecraft.server.v1_8_R3.EntitySnowball;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityDestroy;

public class Grappler extends Kit {
	   private final Map<Player, Grappler.Cordinha> hooks = new HashMap();
	   private final HashMap<Player, Long> leftClickGrappler = new HashMap();
	   private final HashMap<Player, Long> rightClickGrappler = new HashMap();

	   public Grappler() {
	      this.initialize(this.getClass().getSimpleName());
	      this.setItens(new ItemStack[]{(new ItemBuilder()).type(Material.LEASH).name(this.getItemColor() + "Kit " + this.getName()).build()});
	   }

	   @EventHandler
	   public void onPlayerItemHeld(PlayerItemHeldEvent event) {
	      if (this.hooks.containsKey(event.getPlayer())) {
	         ((Grappler.Cordinha)this.hooks.get(event.getPlayer())).remove();
	         this.hooks.remove(event.getPlayer());
	      }

	   }

	   @EventHandler
	   public void onMove(PlayerMoveEvent event) {
	      if (LocationUtil.isRealMovement(event.getFrom(), event.getTo())) {
	         if (this.hooks.containsKey(event.getPlayer()) && !event.getPlayer().getItemInHand().getType().equals(Material.LEASH)) {
	            ((Grappler.Cordinha)this.hooks.get(event.getPlayer())).remove();
	            this.hooks.remove(event.getPlayer());
	         }

	      }
	   }

	   @EventHandler
	   public void onLeash(PlayerLeashEntityEvent e) {
	      if (this.hasAbility(e.getPlayer())) {
	         e.setCancelled(true);
	         e.getPlayer().updateInventory();
	      }

	   }

	   @EventHandler
	   public void onInteract(PlayerInteractEvent e) {
	      if (e.getAction() != Action.PHYSICAL) {
	         Player p = e.getPlayer();
	         if (p.getItemInHand().getType().equals(Material.LEASH) && this.hasAbility(p)) {
	            e.setCancelled(true);
	            if (this.hasCooldown(p, "CombatLog")) {
	               p.addPotionEffect(new PotionEffect(PotionEffectType.SLOW, 3, 5), true);
	               p.sendMessage("§cVocê levou um hit recentemente, aguarde para usar a habilidade novamente.");
	               return;
	            }

	            if (e.getAction() != Action.LEFT_CLICK_AIR && e.getAction() != Action.LEFT_CLICK_BLOCK) {
	               if (!this.hooks.containsKey(p)) {
	                  return;
	               }

	               if (this.rightClickGrappler.containsKey(p) && (Long)this.rightClickGrappler.get(p) > System.currentTimeMillis()) {
	                  return;
	               }

	               if (!((Grappler.Cordinha)this.hooks.get(p)).isHooked()) {
	                  return;
	               }

	               this.rightClickGrappler.put(p, System.currentTimeMillis() + 150L);
	               double d = ((Grappler.Cordinha)this.hooks.get(p)).getBukkitEntity().getLocation().distance(p.getLocation());
	               double v_x = (1.0D + 0.07D * d) * (((Grappler.Cordinha)this.hooks.get(p)).getBukkitEntity().getLocation().getX() - p.getLocation().getX()) / d;
	               double v_y = (1.0D + 0.03D * d) * (((Grappler.Cordinha)this.hooks.get(p)).getBukkitEntity().getLocation().getY() - p.getLocation().getY()) / d;
	               double v_z = (1.0D + 0.07D * d) * (((Grappler.Cordinha)this.hooks.get(p)).getBukkitEntity().getLocation().getZ() - p.getLocation().getZ()) / d;
	               Vector v = p.getVelocity();
	               v.setX(v_x);
	               v.setY(v_y);
	               v.setZ(v_z);
	               p.setVelocity(v);
	               p.playSound(p.getLocation(), Sound.STEP_GRAVEL, 10.0F, 10.0F);
	            } else {
	               if (this.leftClickGrappler.containsKey(p) && (Long)this.leftClickGrappler.get(p) > System.currentTimeMillis()) {
	                  return;
	               }

	               if (this.hooks.containsKey(p)) {
	                  ((Grappler.Cordinha)this.hooks.get(p)).remove();
	               }

	               Grappler.Cordinha nmsHook = new Grappler.Cordinha(p.getWorld(), ((CraftPlayer)p).getHandle());
	               nmsHook.spawn(p.getEyeLocation().add(p.getLocation().getDirection().getX(), p.getLocation().getDirection().getY(), p.getLocation().getDirection().getZ()));
	               nmsHook.move(p.getLocation().getDirection().getX() * 3.0D, p.getLocation().getDirection().getY() * 3.0D, p.getLocation().getDirection().getZ() * 3.0D);
	               this.hooks.put(p, nmsHook);
	               this.leftClickGrappler.put(p, System.currentTimeMillis() + 250L);
	            }
	         }

	      }
	   }

	   @EventHandler(
	      ignoreCancelled = true
	   )
	   public void onPlayerDamage(PlayerDamagePlayerEvent event) {
	      if (this.hasAbility(event.getDamaged())) {
	         this.addCooldown(event.getDamaged(), "CombatLog", 3L);
	      }

	   }

	   protected void clean(Player player) {
	      if (this.hooks.containsKey(player)) {
	         ((Grappler.Cordinha)this.hooks.get(player)).remove();
	         this.hooks.remove(player);
	      }

	      this.leftClickGrappler.remove(player);
	      this.rightClickGrappler.remove(player);
	   }

	   public class Cordinha extends EntityFishingHook {
	      private Snowball sb;
	      private EntitySnowball controller;
	      public EntityHuman owner;
	      public Entity hooked;
	      public boolean lastControllerDead;
	      public boolean isHooked;

	      public Cordinha(World world, EntityHuman entityhuman) {
	         super(((CraftWorld)world).getHandle(), entityhuman);
	         this.owner = entityhuman;
	      }

	      protected void c() {
	      }

	      public void t_() {
	         if (!this.lastControllerDead && this.controller.dead) {
	            ((Player)this.owner.getBukkitEntity()).sendMessage("§aSua corda prendeu em algo.");
	         }

	         this.lastControllerDead = this.controller.dead;
	         Iterator var1 = this.controller.world.getWorld().getEntities().iterator();

	         while(true) {
	            Entity entity;
	            do {
	               do {
	                  do {
	                     do {
	                        do {
	                           do {
	                              if (!var1.hasNext()) {
	                                 try {
	                                    this.locX = this.hooked.getLocation().getX();
	                                    this.locY = this.hooked.getLocation().getY();
	                                    this.locZ = this.hooked.getLocation().getZ();
	                                    this.motX = 0.0D;
	                                    this.motY = 0.04D;
	                                    this.motZ = 0.0D;
	                                    this.isHooked = true;
	                                 } catch (Exception var3) {
	                                    if (this.controller.dead) {
	                                       this.isHooked = true;
	                                    }

	                                    this.locX = this.controller.locX;
	                                    this.locY = this.controller.locY;
	                                    this.locZ = this.controller.locZ;
	                                 }

	                                 return;
	                              }

	                              entity = (Entity)var1.next();
	                           } while(!(entity instanceof LivingEntity));
	                        } while(entity instanceof Firework);
	                     } while(entity.getEntityId() == this.getBukkitEntity().getEntityId());
	                  } while(entity.getEntityId() == this.owner.getBukkitEntity().getEntityId());
	               } while(entity.getEntityId() == this.controller.getBukkitEntity().getEntityId());
	            } while(entity.getLocation().distance(this.controller.getBukkitEntity().getLocation()) >= 2.0D && (!(entity instanceof Player) || ((Player)entity).getEyeLocation().distance(this.controller.getBukkitEntity().getLocation()) >= 2.0D));

	            this.controller.die();
	            this.hooked = entity;
	            this.isHooked = true;
	            this.locX = entity.getLocation().getX();
	            this.locY = entity.getLocation().getY();
	            this.locZ = entity.getLocation().getZ();
	            this.motX = 0.0D;
	            this.motY = 0.04D;
	            this.motZ = 0.0D;
	         }
	      }

	      public void die() {
	      }

	      public void remove() {
	         super.die();
	      }

	      public void spawn(Location location) {
	         this.sb = (Snowball)this.owner.getBukkitEntity().launchProjectile(Snowball.class);
	         this.sb.setVelocity(this.sb.getVelocity().multiply(2.25D));
	         this.controller = ((CraftSnowball)this.sb).getHandle();
	         PacketPlayOutEntityDestroy packet = new PacketPlayOutEntityDestroy(new int[]{this.controller.getId()});
	         Iterator var3 = Bukkit.getOnlinePlayers().iterator();

	         while(var3.hasNext()) {
	            Player p = (Player)var3.next();
	            ((CraftPlayer)p).getHandle().playerConnection.sendPacket(packet);
	         }

	         ((CraftWorld)location.getWorld()).getHandle().addEntity(this);
	      }

	      public boolean isHooked() {
	         return this.isHooked;
	      }

	      public void setHookedEntity(Entity damaged) {
	         this.hooked = damaged;
	      }
	   }
	}