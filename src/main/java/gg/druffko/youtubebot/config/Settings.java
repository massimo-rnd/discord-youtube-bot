package gg.druffko.youtubebot.config;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

public class Settings {

    public static String discordToken = "";
    public static String dcytChannel = "";
    public static String postFile = "";
    public static String ytToken = "";
    public static String channelId = "";


    public static void getConfig(){
        String botConfigPath ="bot.properties";

        Properties botProperties = new Properties();
        try {
            botProperties.load(new FileInputStream(botConfigPath));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        discordToken = botProperties.getProperty("discordToken");
        dcytChannel = botProperties.getProperty("dcytChannel");
        postFile = botProperties.getProperty("postFile");
        ytToken = botProperties.getProperty("ytToken");
        channelId = botProperties.getProperty("channelId");
    }
}