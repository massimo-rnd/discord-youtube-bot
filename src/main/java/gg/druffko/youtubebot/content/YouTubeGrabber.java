package gg.druffko.youtubebot.content;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import gg.druffko.youtubebot.config.Settings;
import net.dv8tion.jda.api.entities.channel.concrete.TextChannel;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

import java.io.*;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;

import static gg.druffko.youtubebot.bot.jda;

public class YouTubeGrabber {

    public static ArrayList<String> videoFileList = new ArrayList<String>();
    public static ArrayList<String> videoFeedList = new ArrayList<String>();

    public static String messageToSend;

    public static void initializeYt(){
        checkForFile();
        readVideoFile();
        grabYtFeed();
        compareLists();
        checkForUpdates();
    }

    public static void checkForFile(){
        File videoFile = new File(Settings.postFile);
        if (!videoFile.exists()){
            try {
                videoFile.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public static void readVideoFile(){
        try {
            Scanner scanner = new Scanner(new File(Settings.postFile));
            while(scanner.hasNextLine()){
                videoFileList.add(scanner.nextLine());
            }


        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        }
    }


    public static void grabYtFeed(){
        try {
            String apiKey = Settings.ytToken; // Replace with your actual API key
            String channelId = Settings.channelId; // Replace with the actual channel ID

            // Make a request to the YouTube Data API to get the videos from the channel
            String apiUrl = "https://www.googleapis.com/youtube/v3/search?key="+ apiKey+"&channelId="+channelId+"&part=snippet,id&order=date&maxResults=10";

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .url(apiUrl)
                    .build();

            Response response = client.newCall(request).execute();
            String responseBody = response.body().string();

            // Parse the JSON response
            JsonParser parser = new JsonParser();
            JsonObject jsonResponse = parser.parse(responseBody).getAsJsonObject();
            JsonArray items = jsonResponse.getAsJsonArray("items");

            // Print video titles and links to the command line
            for (int i = 0; i < items.size(); i++) {
                JsonObject item = items.get(i).getAsJsonObject();
                String title = item.getAsJsonObject("snippet").get("title").getAsString();
                String videoId = item.getAsJsonObject("id").get("videoId").getAsString();
                String videoLink = "https://www.youtube.com/watch?v=" + videoId;

                videoFeedList.add(title + ": " + videoLink);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void compareLists(){
        int videoFeedSize = videoFeedList.size();
        for (int i = 0; i < videoFeedSize; i++){
            String rss = videoFeedList.get(i);
            if (videoFileList.contains(rss)){
                //do nothing
            }else {
                //add to postlist, add to file, send message
                videoFileList.add(0, videoFeedList.get(i));
                messageToSend = videoFeedList.get(i);
                //write result to file
                writeResultToFile(messageToSend);
                sendMessageToChannel();
            }

        }
    }

    public static void sendMessageToChannel(){
        TextChannel textChannel = jda.getTextChannelById(Settings.dcytChannel);
        textChannel.sendMessage(messageToSend).queue();
    }

    public static void writeResultToFile(String resultToWrite){
        //write results
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(Settings.postFile, true));
            writer.append("\n");
            writer.append(resultToWrite);

            writer.close();

        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void checkForUpdates(){
        int i = 1;
        //check for updates and post if required for as long as possible
        Thread checkForUpdatesTread = new Thread(() -> {
            //do forever
            while (i > 0){
                try {
                    System.out.println("YouTube Thread started");
                    Thread.sleep(TimeUnit.MINUTES.toMillis(60));
                    readVideoFile();
                    grabYtFeed();
                    compareLists();
                    System.out.println("Restarting Thread");
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        checkForUpdatesTread.start();
    }

}
