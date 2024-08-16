package space.peetseater.bot;

import space.peetseater.bot.gui.ChatWindow;

import javax.swing.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException {
        String clientId = getClientID(args);
        String clientSecret = getClientSecret(args);
        String channelName = getChannelName(args);

        // Setup Twitch Connection
        TwitchEventPublisher twitchEventPublisher = new TwitchEventPublisher(clientId, clientSecret);
        twitchEventPublisher.connectToTwitch();
        twitchEventPublisher.beginListeningForRewards(channelName);
        twitchEventPublisher.beginListeningForChatMessages(channelName);

        // Setup Bible Chat Window
        SwingUtilities.invokeLater(() -> {
            ChatWindow chatWindow = new ChatWindow();
            twitchEventPublisher.addListener(chatWindow);
        });
//
        Runtime.getRuntime().addShutdownHook(new Thread() {
            public void run() {
                twitchEventPublisher.disconnectFromTwitch();
            }
        });
    }

    private void localTestChatWindow(ChatWindow chatWindow) {
        String bigString = "";
        for (int i = 0; i < 5; i++) {
            chatWindow.onChatMessage(new ChatMessage("Some text", "Chat Member1"));
            chatWindow.onChatMessage(new ChatMessage("Some more text", "Chat Member2"));
            chatWindow.onChatMessage(new ChatMessage("Some text more", "Chat Member3"));
            bigString += "a word and " + i;
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
        chatWindow.onChatMessage(new ChatMessage("%s".formatted(bigString), "Chat Member4"));
    }

    private static String getChannelName(String[] args) {
        if (args.length < 3) {
            throw new IllegalArgumentException("Pass channel as 3rd argument");
        }
        return args[2];
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

}