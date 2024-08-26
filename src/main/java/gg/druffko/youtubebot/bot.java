package gg.druffko.youtubebot;

import gg.druffko.youtubebot.config.Settings;
import gg.druffko.youtubebot.content.YouTubeGrabber;
import gg.druffko.youtubebot.events.InteractionEventListener;
import gg.druffko.youtubebot.events.MessageEventListener;
import gg.druffko.youtubebot.events.ReadyEventListener;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.JDABuilder;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import net.dv8tion.jda.api.requests.GatewayIntent;

public class bot {

    public static JDA jda;

    public static void main(String[] args) throws InterruptedException {

        Settings.getConfig();
        JDABuilder jdabuilder = JDABuilder.createDefault(Settings.discordToken);

        jda = jdabuilder.enableIntents(GatewayIntent.MESSAGE_CONTENT, GatewayIntent.GUILD_MESSAGES)
                .addEventListeners(new ReadyEventListener(), new MessageEventListener(), new InteractionEventListener())
                .build();
        jda.awaitReady();

        //send messages to channel
        TextChannel textChannel = jda.getTextChannelById(Settings.dcytChannel);
        System.out.println(textChannel.getName());

        YouTubeGrabber.initializeYt();
    }
}
