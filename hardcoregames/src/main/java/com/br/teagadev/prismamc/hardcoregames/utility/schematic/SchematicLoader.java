package com.br.teagadev.prismamc.hardcoregames.utility.schematic;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.br.teagadev.prismamc.hardcoregames.HardcoreGamesMain;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.scheduler.BukkitTask;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.WorldEditAPI;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.schematic.object.SchematicBlock;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.schematic.object.SchematicLocation;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.schematic.utils.Region;
import com.br.teagadev.prismamc.commons.bukkit.worldedit.schematic.utils.Vector;
import com.br.teagadev.prismamc.commons.common.utility.system.DateUtils;
import com.br.teagadev.prismamc.hardcoregames.manager.structures.StructuresManager;

import net.minecraft.server.v1_8_R3.NBTCompressedStreamTools;
import net.minecraft.server.v1_8_R3.NBTTagCompound;
import net.minecraft.server.v1_8_R3.NBTTagList;

public class SchematicLoader {
	   private final List<SchematicBlock> blockList = new ArrayList();
	   private final List<SchematicLocation> locationList = new ArrayList();
	   private final Region region;
	   private BukkitTask task;

	   public SchematicLoader(Region region) {
	      this.region = region;
	      World world = region.getWorld();
	      Vector min = region.getMinLocation();
	      Vector max = region.getMaxLocation();

	      for(int x = min.getBlockX(); x <= max.getBlockX(); ++x) {
	         for(int y = min.getBlockY(); y <= max.getBlockY(); ++y) {
	            for(int z = min.getBlockZ(); z <= max.getBlockZ(); ++z) {
	               Block block = world.getBlockAt(x, y, z);
	               Vector location = (new Vector(x, y, z)).subtract(min);
	               SchematicBlock schematicBlock = new SchematicBlock(location, block.getTypeId(), block.getData());
	               this.blockList.add(schematicBlock);
	            }
	         }
	      }

	   }

	   public SchematicLoader(String nome, File file) throws IOException {
	      FileInputStream stream = new FileInputStream(file);
	      NBTTagCompound nbt = NBTCompressedStreamTools.a(stream);
	      stream.close();
	      short width = nbt.getShort("Width");
	      short height = nbt.getShort("Height");
	      short length = nbt.getShort("Length");
	      int offsetX = nbt.getInt("WEOffsetX");
	      int offsetY = nbt.getInt("WEOffsetY");
	      int offsetZ = nbt.getInt("WEOffsetZ");
	      Vector offset = new Vector(offsetX, offsetY, offsetZ);
	      int originX = nbt.getInt("WEOriginX");
	      int originY = nbt.getInt("WEOriginY");
	      int originZ = nbt.getInt("WEOriginZ");
	      Vector origin = new Vector(originX, originY, originZ);
	      this.region = new Region(origin, offset, width, height, length);
	      byte[] blockId = nbt.getByteArray("Blocks");
	      byte[] blockData = nbt.getByteArray("Data");
	      byte[] addId = new byte[0];
	      short[] blocks = new short[blockId.length];
	      if (nbt.hasKey("AddBlocks")) {
	         addId = nbt.getByteArray("AddBlocks");
	      }

	      int x;
	      for(x = 0; x < blockId.length; ++x) {
	         if (x >> 1 >= addId.length) {
	            blocks[x] = (short)(blockId[x] & 255);
	         } else if ((x & 1) == 0) {
	            blocks[x] = (short)(((addId[x >> 1] & 15) << 8) + (blockId[x] & 255));
	         } else {
	            blocks[x] = (short)(((addId[x >> 1] & 240) << 4) + (blockId[x] & 255));
	         }
	      }

	      int y;
	      for(x = 0; x < width; ++x) {
	         for(y = 0; y < height; ++y) {
	            for(int z = 0; z < length; ++z) {
	               int index = y * width * length + z * width + x;
	               SchematicBlock block = new SchematicBlock(new Vector(x, y, z), blocks[index], blockData[index]);
	               this.blockList.add(block);
	            }
	         }
	      }

	      NBTTagList locations = nbt.getList("Locations", 8);

	      for(y = 0; y < locations.size(); ++y) {
	         SchematicLocation location = new SchematicLocation(locations.getString(y));
	         this.locationList.add(location);
	      }

	   }

	   public Region getRegion() {
	      return this.region;
	   }

	   public void addLocation(SchematicLocation location) {
	      this.locationList.add(location);
	   }

	   public List<Location> getConvertedLocation(String key, Location pasteLocation) {
	      List<Location> result = new ArrayList();
	      Iterator var4 = this.locationList.iterator();

	      while(var4.hasNext()) {
	         SchematicLocation locations = (SchematicLocation)var4.next();
	         if (locations.getKey().equals(key)) {
	            Vector vector = locations.getLocation().clone().add((new Vector(pasteLocation)).add(this.region.getOffset()).subtract(this.region.getMinLocation()));
	            result.add(vector.toLocation(pasteLocation.getWorld()));
	         }
	      }

	      return result;
	   }

	   public void paste(String nome, Location location, int bpt) {
	      Vector finalLocation = (new Vector(location)).add(this.region.getOffset());
	      World world = location.getWorld();
	      this.pasteBlocks(nome, world, finalLocation, bpt, false);
	   }

	   public void paste(String nome, Location location, int bpt, boolean force) {
	      Vector finalLocation = (new Vector(location)).add(this.region.getOffset());
	      World world = location.getWorld();
	      this.pasteBlocks(nome, world, finalLocation, bpt, force);
	   }

	   private void pasteBlocks(String nome, World world, Vector pasteLocation, int bpt, boolean force) {
	      HardcoreGamesMain.console("Colando a Schematic '" + nome + "' forçado -> " + (force ? "Sim" : "Nao") + " com " + this.blockList.size() + " blocos a serem colados.");
	      long started = System.currentTimeMillis();
	      long startedNano = System.nanoTime();
	      if (!force) {
	         this.task = Bukkit.getScheduler().runTaskTimer(BukkitMain.getInstance(), () -> {
	            Iterator<SchematicBlock> iterator = this.blockList.iterator();

	            for(int i = 0; i < bpt; ++i) {
	               if (!iterator.hasNext()) {
	                  HardcoreGamesMain.console("Schematic '" + nome + "' colada em -> " + DateUtils.getElapsed(started, startedNano));
	                  this.task.cancel();
	                  break;
	               }

	               SchematicBlock schemBlock = (SchematicBlock)iterator.next();
	               Vector finalLocation = schemBlock.getLocation().clone().add(pasteLocation);
	               WorldEditAPI.setAsyncBlock(world, new Location(world, (double)finalLocation.getBlockX(), (double)finalLocation.getBlockY(), (double)finalLocation.getBlockZ()), schemBlock.getID(), schemBlock.getData());
	               Block block;
	               if (nome.equalsIgnoreCase("feast")) {
	                  block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
	                  if (schemBlock.getID() == 116) {
	                     StructuresManager.getFeast().setEnchantmentTable(block.getLocation());
	                     block.setType(Material.AIR);
	                  } else if (block.getTypeId() == 54 || block.getTypeId() == 146) {
	                     block.setType(Material.AIR);
	                     StructuresManager.getFeast().addChest(block);
	                  }
	               } else if (nome.equalsIgnoreCase("minifeast")) {
	                  block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
	                  if (block.getTypeId() == 54 || block.getTypeId() == 146) {
	                     block.setType(Material.CHEST);
	                     StructuresManager.addItensChestMinifeast((Chest)block.getLocation().getBlock().getState());
	                  }
	               }

	               iterator.remove();
	            }

	         }, 0L, 1L);
	      } else {
	         for(Iterator iterator = this.blockList.iterator(); iterator.hasNext(); iterator.remove()) {
	            SchematicBlock schemBlock = (SchematicBlock)iterator.next();
	            Vector finalLocation = schemBlock.getLocation().clone().add(pasteLocation);
	            WorldEditAPI.setAsyncBlock(world, new Location(world, (double)finalLocation.getBlockX(), (double)finalLocation.getBlockY(), (double)finalLocation.getBlockZ()), schemBlock.getID(), schemBlock.getData());
	            Block block;
	            if (nome.equalsIgnoreCase("feast")) {
	               block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
	               if (schemBlock.getID() == 116) {
	                  StructuresManager.getFeast().setEnchantmentTable(block.getLocation());
	                  block.setType(Material.AIR);
	               } else if (block.getTypeId() == 54 || block.getTypeId() == 146) {
	                  block.setType(Material.AIR);
	                  StructuresManager.getFeast().addChest(block);
	               }
	            } else if (nome.equalsIgnoreCase("minifeast")) {
	               block = world.getBlockAt(finalLocation.getBlockX(), finalLocation.getBlockY(), finalLocation.getBlockZ());
	               if (block.getTypeId() == 54 || block.getTypeId() == 146) {
	                  block.setType(Material.CHEST);
	                  StructuresManager.addItensChestMinifeast((Chest)block.getLocation().getBlock().getState());
	               }
	            }
	         }

	      }
	   }

	   public void setBlock(World world, int x, int y, int z, int id, byte data) {
	      world.getBlockAt(x, y, z).setTypeIdAndData(id, data, false);
	   }
	}