package space.peetseater.bot;

import io.obswebsocket.community.client.OBSRemoteController;
import space.peetseater.bot.gui.ChatWindow;
import space.peetseater.bot.rewards.BestGirlVotesDB;
import space.peetseater.bot.rewards.OBSRewardListener;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String clientId = getClientID(args);
        String clientSecret = getClientSecret(args);
        String channelName = getChannelName(args);
        String ipAddress = getIpAddress(args);
        String obsPassword = getObsPassword(args);

        BestGirlVotesDB bestGirlVotesDB = new BestGirlVotesDB();
        Path bestGirlData = Paths.get("bestgirl.votes");
        try {
            bestGirlVotesDB.loadScore(bestGirlData);
        } catch (NoSuchFileException noSuchFileException) {
            System.out.println("Best girl db not created yet, suppressing no such file exception");
        }

        OBSRemoteController obs = getObsRemoteController(ipAddress,obsPassword);
        obs.connect();
        OBSRewardListener obsRewardListener = new OBSRewardListener(obs, bestGirlVotesDB);

        // Setup Twitch Connection
        TwitchEventPublisher twitchEventPublisher = new TwitchEventPublisher(clientId, clientSecret);
        twitchEventPublisher.connectToTwitch();
        twitchEventPublisher.beginListeningForRewards(channelName);
        twitchEventPublisher.beginListeningForChatMessages(channelName);
        twitchEventPublisher.addListener(obsRewardListener);

        // Setup Bible Chat Window
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            twitchEventPublisher.addListener(chatWindow);
        });

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            twitchEventPublisher.disconnectFromTwitch();
            obs.disconnect();
            try {
                bestGirlVotesDB.saveScore(bestGirlData);
            } catch (IOException e) {
                System.out.printf("Could not save best girl votes! %s%n", bestGirlVotesDB);
                throw new RuntimeException(e);
            }
        }));
    }

    private static OBSRemoteController getObsRemoteController(String ipAddress, String password) {
        return OBSRemoteController.builder()
                .autoConnect(false)
                .host(ipAddress)
                .port(4455)
                .password(password)
                .lifecycle()
                .onReady(() -> System.out.println("OBS is ready!"))
                .onClose(webSocketCloseCode -> {
                    // TODO: Remove event handler for rewards when obs connection is closed?
                })
                .and()
                .connectionTimeout(4).build();
    }

    // Handy method for testing Swing window without needing to connect to twitch :P
    private void localTestChatWindow(ChatWindow chatWindow) {
        StringBuilder bigString = new StringBuilder();
        for (int i = 0; i < 5; i++) {
            chatWindow.onChatMessage(new ChatMessage("Some text", "Chat Member1"));
            chatWindow.onChatMessage(new ChatMessage("Some more text", "Chat Member2"));
            chatWindow.onChatMessage(new ChatMessage("Some text more", "Chat Member3"));
            bigString.append("a word and ").append(i);
        }
        chatWindow.onChatMessage(new ChatMessage("Some text", "Chat Member1"));
        chatWindow.onChatMessage(new ChatMessage("Some more text", "Chat Member2"));
        chatWindow.onChatMessage(new ChatMessage("Some text more", "Chat Member3"));
        chatWindow.onChatMessage(new ChatMessage("Some text text", "Chat Member4"));
        try {
            Thread.sleep(10);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        chatWindow.onChatMessage(new ChatMessage("%s".formatted(bigString.toString()), "Chat Member4"));
    }

    public static String getClientID(String[] args) throws IOException {
        if (args.length < 1) {
            throw new IllegalArgumentException("Pass file with oauth token for twitch");
        }
        return Files.readString(Paths.get(args[0]));
    }

    public static String getClientSecret(String[] args) throws IOException {
        if (args.length < 2) {
            throw new IllegalArgumentException("Pass file with oauth token for twitch");
        }
        return Files.readString(Paths.get(args[1]));
    }

    private static String getChannelName(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Pass channel as 3rd argument");
        }
        return args[2];
    }

    private static String getIpAddress(String[] args) {
        if (args.length < 4) {
            throw new IllegalArgumentException("Pass obs ip address as 4th argument");
        }
        return args[3];
    }

    private static String getObsPassword(String[] args) {
        if (args.length < 5) {
            throw new IllegalArgumentException("Pass obs socket password as 5th argument");
        }
        return args[4];
    }

}