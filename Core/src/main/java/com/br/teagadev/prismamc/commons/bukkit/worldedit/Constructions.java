package com.br.teagadev.prismamc.commons.bukkit.worldedit;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.actionbar.ActionBarAPI;
import com.br.teagadev.prismamc.commons.bukkit.api.protocol.ProtocolGetter;
import com.br.teagadev.prismamc.commons.common.utility.string.StringUtility;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.Chunk;
import net.minecraft.server.v1_8_R3.IBlockData;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Constructions {
   private Player owner;
   private int blocksPerTick;
   private int maxBlocks;
   private int blockAtual;
   private int blocksPerSecond;
   private List<Location> locations;
   private List<Material> blocksToSet;
   private boolean finished;
   private boolean resetando;
   private boolean cancelTask;
   private boolean in18;
   private Random random;
   private Long started;
   private Long startedNano;
   private HashMap<String, Material> blocksToReset;

   public Constructions(Player owner, List<Location> locations) {
      this.owner = owner;
      this.blocksPerTick = 2;
      this.blockAtual = 0;
      this.blocksPerSecond = this.blocksPerTick * 20;
      this.finished = false;
      this.resetando = false;
      this.cancelTask = false;
      this.locations = new ArrayList();
      this.blocksToReset = new HashMap();
      this.blocksToSet = new ArrayList();
      this.locations.addAll(locations);
      this.in18 = ProtocolGetter.getVersion(owner) > 5;
      this.maxBlocks = locations.size();
      this.random = new Random();
   }

   public void processBlocks() {
      for(int i = 0; i < this.maxBlocks; ++i) {
         Location location = (Location)this.locations.get(this.blockAtual + i);
         String formated = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
         if (!this.blocksToReset.containsKey(formated)) {
            Block block = location.getBlock();
            this.blocksToReset.put(formated, block.getType());
         }
      }

   }

   public void start() {
      this.processBlocks();
      this.started = System.currentTimeMillis();
      this.startedNano = System.nanoTime();
      (new BukkitRunnable() {
         public void run() {
            if (Constructions.this.cancelTask) {
               this.cancel();
               Constructions.this.sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução concluída em: §e" + DateUtils.getElapsed(Constructions.this.started, Constructions.this.startedNano), true);
               Constructions.this.blockAtual = 0;
               Constructions.this.finished = true;
            } else {
               Constructions.this.putBlock();
            }
         }
      }).runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
   }

   public void startRegress() {
      this.blockAtual = 0;
      this.resetando = true;
      (new BukkitRunnable() {
         public void run() {
            if (Constructions.this.cancelTask) {
               this.cancel();
               Constructions.this.sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução resetada com sucesso.", true);
               Constructions.this.finished = true;
               WorldEditManager.removeConstructionByUUID(Constructions.this.getOwner().getUniqueId());
            } else {
               Constructions.this.regressBlock();
            }
         }
      }).runTaskTimer(BukkitMain.getInstance(), 1L, 1L);
   }

   public void sendMessageIfPlayerIsOnline(String mensagem, boolean msg) {
      if (this.getOwner() != null && this.getOwner().isOnline()) {
         if (this.isIn18()) {
            ActionBarAPI.send(this.getOwner(), mensagem);
         }

         if (msg) {
            this.getOwner().sendMessage(mensagem);
         }
      }

   }

   public void putBlock() {
      if (!this.finished) {
         if (this.blockAtual >= this.maxBlocks) {
            this.cancelTask = true;
         } else {
            int atualBlocksPertick = this.blocksPerTick;

            for(int i = 0; i < atualBlocksPertick; ++i) {
               try {
                  Location location = (Location)this.locations.get(this.blockAtual + i);
                  Material escolhido = this.getRandomOurExactBlock();
                  this.setAsyncBlock(location.getWorld(), location, escolhido.getId());
               } catch (IndexOutOfBoundsException var5) {
               } catch (NullPointerException var6) {
               }
            }

            this.sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fConstrução em andamento... §e(" + StringUtility.formatValue(this.blockAtual) + "/" + StringUtility.formatValue(this.maxBlocks) + ") " + StringUtility.formatValue(this.blocksPerSecond) + "b/ps", false);
            this.blockAtual += atualBlocksPertick;
         }
      }
   }

   public void regressBlock() {
      if (!this.finished) {
         if (this.blockAtual >= this.maxBlocks) {
            this.cancelTask = true;
         } else {
            int atualBlocksPertick = this.blocksPerTick;

            for(int i = 0; i < atualBlocksPertick; ++i) {
               try {
                  Location location = (Location)this.locations.get(this.blockAtual + i);
                  Block block = location.getBlock();
                  String formated = location.getBlockX() + "," + location.getBlockY() + "," + location.getBlockZ();
                  Material material = (Material)this.blocksToReset.get(formated);
                  this.setAsyncBlock(location.getWorld(), location, material.getId());
               } catch (IndexOutOfBoundsException var7) {
               } catch (NullPointerException var8) {
               }
            }

            this.sendMessageIfPlayerIsOnline("§e§lWORLDEDIT §fResetando construção... §e(" + StringUtility.formatValue(this.blockAtual) + "/" + StringUtility.formatValue(this.maxBlocks) + ") " + StringUtility.formatValue(this.blocksPerSecond) + "b/ps", false);
            this.blockAtual += atualBlocksPertick;
         }
      }
   }

   public void setAsyncBlock(World world, Location location, int blockId) {
      this.setAsyncBlock(world, location, blockId, (byte)0);
   }

   public void setAsyncBlock(World world, Location location, int blockId, byte data) {
      this.setAsyncBlock(world, location.getBlockX(), location.getBlockY(), location.getBlockZ(), blockId, data);
   }

   public void setAsyncBlock(World world, int x, int y, int z, int blockId, byte data) {
      net.minecraft.server.v1_8_R3.World w = ((CraftWorld)world).getHandle();
      Chunk chunk = w.getChunkAt(x >> 4, z >> 4);
      BlockPosition bp = new BlockPosition(x, y, z);
      int i = blockId + (data << 12);
      IBlockData ibd = net.minecraft.server.v1_8_R3.Block.getByCombinedId(i);
      chunk.a(bp, ibd);
      w.notify(bp);
   }

   public Material getRandomOurExactBlock() {
      return this.blocksToReset.size() == 1 ? (Material)this.blocksToSet.get(0) : (Material)this.blocksToSet.get(this.random.nextInt(this.blocksToSet.size()));
   }

   public void setBlocksPerTick(int blocksPerTick) {
      this.blocksPerTick = blocksPerTick;
      this.blocksPerSecond = blocksPerTick * 20;
   }

   public void setBlocksToSet(List<Material> blocksToSet) {
      this.blocksToSet.addAll(blocksToSet);
   }

   public Player getOwner() {
      return this.owner;
   }

   public int getBlocksPerTick() {
      return this.blocksPerTick;
   }

   public int getMaxBlocks() {
      return this.maxBlocks;
   }

   public int getBlockAtual() {
      return this.blockAtual;
   }

   public int getBlocksPerSecond() {
      return this.blocksPerSecond;
   }

   public List<Location> getLocations() {
      return this.locations;
   }

   public List<Material> getBlocksToSet() {
      return this.blocksToSet;
   }

   public boolean isFinished() {
      return this.finished;
   }

   public boolean isResetando() {
      return this.resetando;
   }

   public boolean isCancelTask() {
      return this.cancelTask;
   }

   public boolean isIn18() {
      return this.in18;
   }

   public Random getRandom() {
      return this.random;
   }

   public Long getStarted() {
      return this.started;
   }

   public Long getStartedNano() {
      return this.startedNano;
   }

   public HashMap<String, Material> getBlocksToReset() {
      return this.blocksToReset;
   }

   public void setOwner(Player owner) {
      this.owner = owner;
   }

   public void setMaxBlocks(int maxBlocks) {
      this.maxBlocks = maxBlocks;
   }

   public void setBlockAtual(int blockAtual) {
      this.blockAtual = blockAtual;
   }

   public void setBlocksPerSecond(int blocksPerSecond) {
      this.blocksPerSecond = blocksPerSecond;
   }

   public void setLocations(List<Location> locations) {
      this.locations = locations;
   }

   public void setFinished(boolean finished) {
      this.finished = finished;
   }

   public void setResetando(boolean resetando) {
      this.resetando = resetando;
   }

   public void setCancelTask(boolean cancelTask) {
      this.cancelTask = cancelTask;
   }

   public void setIn18(boolean in18) {
      this.in18 = in18;
   }

   public void setRandom(Random random) {
      this.random = random;
   }

   public void setStarted(Long started) {
      this.started = started;
   }

   public void setStartedNano(Long startedNano) {
      this.startedNano = startedNano;
   }

   public void setBlocksToReset(HashMap<String, Material> blocksToReset) {
      this.blocksToReset = blocksToReset;
   }
}