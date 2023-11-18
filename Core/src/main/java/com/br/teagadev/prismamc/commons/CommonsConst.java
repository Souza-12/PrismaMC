package com.br.teagadev.prismamc.commons;

import com.google.gson.Gson;
import com.google.gson.JsonParser;

import java.util.Random;

public class CommonsConst {

    public static final String SERVER_NAME = "CRAZZY";
    public static final String DEFAULT_COLOR = "§e";
    public static final String PREFIX = DEFAULT_COLOR + "§l" + SERVER_NAME.toUpperCase();
    public static final String LOJA = "www.crazzymc.com.br";
    public static final String DISCORD = "discord.crazzymc.com.br";
    public static final String DIR_CONFIG_NAME = "PrismaConfig";
    public static final String PERMISSION_PREFIX = "commons";

    public static final Random RANDOM = new Random();
    public static final JsonParser PARSER = new JsonParser();
    public static final Gson GSON = new Gson();
}