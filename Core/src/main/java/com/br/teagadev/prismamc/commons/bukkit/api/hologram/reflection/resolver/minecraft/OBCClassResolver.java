package com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.minecraft;

import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.minecraft.Minecraft;
import com.br.teagadev.prismamc.commons.bukkit.api.hologram.reflection.resolver.ClassResolver;

/**
 * {@link ClassResolver} for <code>org.bukkit.craftbukkit.*</code> classes
 */
public class OBCClassResolver extends ClassResolver {

    @Override
    public Class resolve(String... names) throws ClassNotFoundException {
        for (int i = 0; i < names.length; i++) {
            if (!names[i].startsWith("org.bukkit.craftbukkit")) {
                names[i] = "org.bukkit.craftbukkit." + Minecraft.getVersion() + names[i];
            }
        }
        return super.resolve(names);
    }
}
