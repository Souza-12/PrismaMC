package com.br.teagadev.prismamc.commons.bukkit.api.hologram;

import com.br.teagadev.prismamc.commons.bukkit.BukkitMain;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.NMSClass;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.DataWatcher;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.Minecraft;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.DataWatcher.V1_8;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.DataWatcher.V1_9;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.DataWatcher.V1_9.ValueType;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.Minecraft.Version;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.FieldResolver;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.util.AccessUtil;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.touch.TouchAction;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.touch.TouchHandler;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.view.ViewHandler;
import com.br.teagadev.prismamc.commons.bukkit.custom.injector.PacketObject;
import com.br.teagadev.prismamc.commons.bukkit.custom.injector.listener.PacketListener;
import com.br.teagadev.prismamc.commons.bukkit.custom.injector.listener.PacketListenerAPI;
import java.lang.reflect.Field;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import net.minecraft.server.v1_8_R3.PacketPlayInUseEntity;
import net.minecraft.server.v1_8_R3.PacketPlayOutEntityMetadata;
import net.minecraft.server.v1_8_R3.PacketPlayOutSpawnEntityLiving;
import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.Plugin;

public class HologramInjector {
   private static final FieldResolver DataWatcherFieldResolver;
   private static final String DELAY_TAG = "delay.hologram";

   public static void inject(Plugin plugin) {
      PacketListenerAPI.addListener(new PacketListener() {
         public void onPacketReceiving(PacketObject packetObject) {
            Object packet = packetObject.getPacket();
            if (packet instanceof PacketPlayInUseEntity) {
               int id = (Integer)HologramInjector.getPacketValue("a", packet);
               Object useAction = HologramInjector.getPacketValue("action", packet);
               TouchAction action = TouchAction.fromUseAction(useAction);
               if (action != TouchAction.UNKNOWN) {
                  if (HologramInjector.hasDelay(packetObject.getPlayer())) {
                     packetObject.setCancelled(true);
                  } else {
                     HologramInjector.addDelay(packetObject.getPlayer());
                     Iterator var6 = HologramAPI.getHolograms().iterator();

                     while(true) {
                        Hologram h;
                        do {
                           if (!var6.hasNext()) {
                              return;
                           }

                           h = (Hologram)var6.next();
                        } while(!((DefaultHologram)h).matchesTouchID(id));

                        Iterator var8 = h.getTouchHandlers().iterator();

                        while(var8.hasNext()) {
                           TouchHandler t = (TouchHandler)var8.next();
                           t.onTouch(h, packetObject.getPlayer(), action);
                        }
                     }
                  }
               }
            }
         }

         public void onPacketSending(PacketObject packetObject) {
            Object packet = packetObject.getPacket();
            int type = -1;
            if (packet instanceof PacketPlayOutSpawnEntityLiving) {
               type = 0;
            }

            if (packet instanceof PacketPlayOutEntityMetadata) {
               type = 1;
            }

            if (type == 0 || type == 1) {
               int a = (Integer)HologramInjector.getPacketValue("a", packet);
               Object dataWatcher = type == 0 ? (Minecraft.VERSION.olderThan(Version.v1_9_R1) ? HologramInjector.getPacketValue("l", packet) : HologramInjector.getPacketValue("m", packet)) : null;
               if (dataWatcher != null) {
                  try {
                     dataWatcher = HologramInjector.cloneDataWatcher(dataWatcher);
                     AccessUtil.setAccessible(Minecraft.VERSION.olderThan(Version.v1_9_R1) ? NMSClass.DataWatcher.getDeclaredField("a") : NMSClass.DataWatcher.getDeclaredField("b")).set(dataWatcher, (Object)null);
                     if (Minecraft.VERSION.olderThan(Version.v1_9_R1)) {
                        HologramInjector.setPacketValue("l", dataWatcher, packet);
                     } else {
                        HologramInjector.setPacketValue("m", dataWatcher, packet);
                     }
                  } catch (Exception var13) {
                     var13.printStackTrace();
                     return;
                  }
               }

               List list = (List)((List)(type == 1 ? HologramInjector.getPacketValue("b", packet) : null));
               int listIndex = -1;
               String text = null;

               try {
                  if (type == 0) {
                     if (Minecraft.VERSION.olderThan(Version.v1_9_R1)) {
                        text = (String)V1_8.getWatchableObjectValue(V1_8.getValue(dataWatcher, 2));
                     } else {
                        Field dField = AccessUtil.setAccessible(NMSClass.DataWatcher.getDeclaredField("d"));
                        Object dValue = dField.get(dataWatcher);
                        if (dValue == null) {
                           return;
                        }

                        if (Map.class.isAssignableFrom(dValue.getClass()) && ((Map)dValue).isEmpty()) {
                           return;
                        }

                        text = (String)V1_9.getValue(dataWatcher, ValueType.ENTITY_NAME);
                     }
                  } else if (type == 1 && list != null) {
                     if (Minecraft.VERSION.olderThan(Version.v1_9_R1)) {
                        for(int i = 0; i < list.size(); ++i) {
                           int index = V1_8.getWatchableObjectIndex(list.get(i));
                           if (index == 2 && V1_8.getWatchableObjectType(list.get(i)) == 4) {
                              text = (String)V1_8.getWatchableObjectValue(list.get(i));
                              listIndex = i;
                              break;
                           }
                        }
                     } else if (list.size() > 2 && V1_9.getItemType(list.get(2)) == String.class) {
                        text = (String)V1_9.getItemValue(list.get(2));
                        listIndex = 2;
                     }
                  }
               } catch (Exception var15) {
               }

               if (text == null) {
                  return;
               }

               Iterator var17 = HologramAPI.getHolograms().iterator();

               while(true) {
                  if (!var17.hasNext()) {
                     if (text == null) {
                        packetObject.setCancelled(true);
                        return;
                     }

                     try {
                        if (type == 0) {
                           DataWatcher.setValue(dataWatcher, 2, ValueType.ENTITY_NAME, text);
                           break;
                        }

                        if (type != 1) {
                           break;
                        }

                        if (list != null && listIndex != -1) {
                           Object object = Minecraft.VERSION.olderThan(Version.v1_9_R1) ? V1_8.newWatchableObject(2, text) : V1_9.newDataWatcherItem(ValueType.ENTITY_NAME.getType(), text);
                           list.set(listIndex, object);
                           HologramInjector.setPacketValue("b", list, packet);
                           break;
                        }

                        return;
                     } catch (Exception var14) {
                        var14.printStackTrace();
                        break;
                     }
                  }

                  Hologram h = (Hologram)var17.next();
                  ViewHandler v;
                  if (((CraftHologram)h).matchesHologramID(a)) {
                     for(Iterator var11 = h.getViewHandlers().iterator(); var11.hasNext(); text = v.onView(h, packetObject.getPlayer(), text)) {
                        v = (ViewHandler)var11.next();
                     }
                  }
               }
            }

         }
      });
   }

   public static void setPacketValue(String field, Object value, Object packet) {
      FieldResolver fieldResolver = new FieldResolver(packet.getClass());

      try {
         fieldResolver.resolve(new String[]{field}).set(packet, value);
      } catch (Exception var5) {
         throw new RuntimeException(var5);
      }
   }

   public static Object getPacketValue(String field, Object packet) {
      FieldResolver fieldResolver = new FieldResolver(packet.getClass());

      try {
         return fieldResolver.resolve(new String[]{field}).get(packet);
      } catch (Exception var4) {
         throw new RuntimeException(var4);
      }
   }

   private static Object cloneDataWatcher(Object original) throws Exception {
      if (original == null) {
         return null;
      } else {
         Object clone = DataWatcher.newDataWatcher((Object)null);
         int index = 0;
         Object current = null;
         if (Minecraft.VERSION.olderThan(Version.v1_9_R1)) {
            while((current = V1_8.getValue(original, index++)) != null) {
               V1_8.setValue(clone, V1_8.getWatchableObjectIndex(current), V1_8.getWatchableObjectValue(current));
            }
         } else {
            Field mapField = DataWatcherFieldResolver.resolve(new String[]{"c"});
            mapField.set(clone, mapField.get(original));
         }

         return clone;
      }
   }

   private static boolean hasDelay(Player player) {
      if (!player.hasMetadata("delay.hologram")) {
         return false;
      } else {
         return ((MetadataValue)player.getMetadata("delay.hologram").get(0)).asLong() > System.currentTimeMillis();
      }
   }

   private static void addDelay(Player player) {
      player.setMetadata("delay.hologram", new FixedMetadataValue(BukkitMain.getInstance(), System.currentTimeMillis() + 600L));
   }

   static {
      DataWatcherFieldResolver = new FieldResolver(NMSClass.DataWatcher);
   }
}