package com.br.teagadev.prismamc.commons.bungee.commands;

import com.br.teagadev.prismamc.commons.CommonsGeneral;
import com.br.teagadev.prismamc.commons.bungee.BungeeMain;
import com.br.teagadev.prismamc.commons.common.command.CommandClass;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Command;
import com.br.teagadev.prismamc.commons.common.command.CommandFramework.Completer;
import com.br.teagadev.prismamc.commons.common.group.Groups;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.event.EventHandler;

public class BungeeCommandFramework implements CommandFramework {
   private final Map<String, Entry<Method, Object>> commandMap = new HashMap();
   private final Map<String, Entry<Method, Object>> completers = new HashMap();
   private final Plugin plugin;

   public BungeeCommandFramework(Plugin plugin) {
      this.plugin = plugin;
      this.plugin.getProxy().getPluginManager().registerListener(plugin, new BungeeCommandFramework.BungeeCompleter());
   }

   public boolean handleCommand(CommandSender sender, String label, String[] args) {
      StringBuilder line = new StringBuilder();
      line.append(label);

      int i;
      for(i = 0; i < args.length; ++i) {
         line.append(" ").append(args[i]);
      }

      for(i = args.length; i >= 0; --i) {
         StringBuilder buffer = new StringBuilder();
         buffer.append(label.toLowerCase());

         for(int x = 0; x < i; ++x) {
            buffer.append(".").append(args[x].toLowerCase());
         }

         String cmdLabel = buffer.toString();
         if (this.commandMap.containsKey(cmdLabel)) {
            Entry<Method, Object> entry = (Entry)this.commandMap.get(cmdLabel);
            Command command = (Command)((Method)entry.getKey()).getAnnotation(Command.class);
            if (sender instanceof ProxiedPlayer) {
               ProxiedPlayer p = (ProxiedPlayer)sender;
               if (!BungeeMain.isValid(p)) {
                  p.sendMessage("§cVocê não tem permissão para usar este comando.");
                  return true;
               }

               Groups tagPlayer = CommonsGeneral.getProfileManager().getGamingProfile(p.getName()).getGroup();
               boolean semPermissao = true;

               for(int uses = 0; uses < command.groupsToUse().length; ++uses) {
                  Groups tag = command.groupsToUse()[uses];
                  if (tagPlayer.getLevel() >= tag.getLevel()) {
                     semPermissao = false;
                     break;
                  }
               }

               tagPlayer = null;
               if (semPermissao && this.hasCommand(p, command.name().toLowerCase())) {
                  semPermissao = false;
               }

               if (semPermissao) {
                  p.sendMessage("§cVocê não tem permissão para usar este comando.");
                  return true;
               }

               p = null;
            }

            if (command.runAsync()) {
               CommonsGeneral.runAsync(() -> {
                  try {
                     ((Method)entry.getKey()).invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
                  } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException var5) {
                     var5.printStackTrace();
                  }

               });
            } else {
               try {
                  ((Method)entry.getKey()).invoke(entry.getValue(), new BungeeCommandSender(sender), label, args);
               } catch (InvocationTargetException | IllegalAccessException | IllegalArgumentException var15) {
                  var15.printStackTrace();
               }
            }

            return true;
         }
      }

      return true;
   }

   public boolean hasCommand(ProxiedPlayer proxiedPlayer, String command) {
      return proxiedPlayer.hasPermission("commons.cmd.all") ? true : proxiedPlayer.hasPermission("commons.cmd." + command);
   }

   public void registerCommands(CommandClass cls) {
      Method[] var2 = cls.getClass().getMethods();
      int var3 = var2.length;

      for(int var4 = 0; var4 < var3; ++var4) {
         Method m = var2[var4];
         String[] var7;
         int var8;
         int var9;
         String alias;
         if (m.getAnnotation(Command.class) != null) {
            Command command = (Command)m.getAnnotation(Command.class);
            if (m.getParameterTypes().length != 3 || !BungeeCommandSender.class.isAssignableFrom(m.getParameterTypes()[0]) && !String.class.isAssignableFrom(m.getParameterTypes()[1]) && !String[].class.isAssignableFrom(m.getParameterTypes()[2])) {
               System.out.println("Unable to register command " + m.getName() + ". Unexpected method arguments");
            } else {
               this.registerCommand(command, command.name(), m, cls);
               var7 = command.aliases();
               var8 = var7.length;

               for(var9 = 0; var9 < var8; ++var9) {
                  alias = var7[var9];
                  this.registerCommand(command, alias, m, cls);
               }
            }
         } else if (m.getAnnotation(Completer.class) != null) {
            Completer comp = (Completer)m.getAnnotation(Completer.class);
            if (m.getParameterTypes().length == 3 && (m.getParameterTypes()[0] == ProxiedPlayer.class || m.getParameterTypes()[1] == String.class || m.getParameterTypes()[2] == String[].class)) {
               if (m.getReturnType() != List.class) {
                  System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected return type");
               } else {
                  this.registerCompleter(comp.name(), m, cls);
                  var7 = comp.aliases();
                  var8 = var7.length;

                  for(var9 = 0; var9 < var8; ++var9) {
                     alias = var7[var9];
                     this.registerCompleter(alias, m, cls);
                  }
               }
            } else {
               System.out.println("Unable to register tab completer " + m.getName() + ". Unexpected method arguments");
            }
         }
      }

   }

   private void registerCommand(Command command, String label, Method m, Object obj) {
      Entry<Method, Object> entry = new SimpleEntry(m, obj);
      this.commandMap.put(label.toLowerCase(), entry);
      String cmdLabel = label.replace(".", ",").split(",")[0].toLowerCase();
      BungeeCommandFramework.BungeeCommand cmd;
      if (command.permission().isEmpty()) {
         cmd = new BungeeCommandFramework.BungeeCommand(cmdLabel);
      } else {
         cmd = new BungeeCommandFramework.BungeeCommand(cmdLabel, command.permission());
      }

      this.plugin.getProxy().getPluginManager().registerCommand(this.plugin, cmd);
   }

   private void registerCompleter(String label, Method m, Object obj) {
      this.completers.put(label, new SimpleEntry(m, obj));
   }

   public Plugin getPlugin1() {
      return this.plugin;
   }

   // $FF: synthetic method
   // $FF: bridge method
   public Object getPlugin() {
      return this.getPlugin1();
   }

   public class BungeeCompleter implements Listener {
      @EventHandler
      public void onTabComplete(TabCompleteEvent event) {
         if (event.getSender() instanceof ProxiedPlayer) {
            ProxiedPlayer pp = (ProxiedPlayer)event.getSender();
            if (event.getCursor().contains(" ")) {
               String[] split = event.getCursor().split(" ");
               String label = split[0].toLowerCase().substring(1);
               String[] args = split.length < 2 ? new String[0] : (String[])Arrays.copyOfRange(split, 1, split.length);
               if (BungeeCommandFramework.this.completers.containsKey(label)) {
                  try {
                     Entry<Method, Object> entry = (Entry)BungeeCommandFramework.this.completers.get(label);
                     event.getSuggestions().clear();
                     event.getSuggestions().addAll((List)((Method)entry.getKey()).invoke(entry.getValue(), pp, label, args));
                  } catch (Exception var7) {
                     var7.printStackTrace();
                  }
               }

            }
         }
      }
   }

   class BungeeCommand extends net.md_5.bungee.api.plugin.Command {
      protected BungeeCommand(String label) {
         super(label);
      }

      protected BungeeCommand(String label, String permission) {
         super(label, permission, new String[0]);
      }

      public void execute(CommandSender sender, String[] args) {
         BungeeCommandFramework.this.handleCommand(sender, this.getName(), args);
      }
   }
}