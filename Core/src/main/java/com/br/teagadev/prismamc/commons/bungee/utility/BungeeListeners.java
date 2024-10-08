package com.br.teagadev.prismamc.commons.bungee.utility;

import com.br.teagadev.prismamc.commons.common.utility.ClassGetter;
import net.md_5.bungee.api.ProxyServer;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

public class BungeeListeners {

    public static void loadListeners(Plugin instance, String packageName) {
        for (Class<?> classes : ClassGetter.getClassesForPackage(instance, packageName)) {
            try {
                if (Listener.class.isAssignableFrom(classes)) {
                    Listener listener = (Listener) classes.newInstance();

                    if (listener == null)
                        continue;

                    ProxyServer.getInstance().getPluginManager().registerListener(instance, listener);

                    listener = null;
                }
            } catch (Exception exception) {
                exception.printStackTrace();
            }
        }
    }
}

