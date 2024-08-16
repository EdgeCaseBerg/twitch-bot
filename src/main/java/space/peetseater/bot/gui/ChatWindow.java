package space.peetseater.bot.gui;


import space.peetseater.bot.ChatMessage;
import space.peetseater.bot.EventListener;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.util.Timer;
import java.util.TimerTask;

/* A transparent window for us to capture with OBS */
public class ChatWindow implements EventListener {

    private final JFrame jFrame;
    private final DefaultListModel<ChatMessage> model;
    private final JScrollPane scrollPane;
    private final JList<ChatMessage> jList;
    public static final Color transparent = new Color(1f,1f,1f,0f);
    private final Timer removeSeenMessagesTimer;
    private final int minimumSecondsToShow = 10;

    public ChatWindow() {
        this.jFrame = new JFrame("TwitchBot-Chat");
        this.jFrame.setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        // Make Frame translucent
        this.jFrame.setUndecorated(true);
        this.jFrame.getContentPane().setBackground(transparent);
        this.jFrame.setBackground(transparent);
        this.jFrame.setPreferredSize(new Dimension(640, 320));
        this.jFrame.setLayout(new BorderLayout());

        // Setup the scrolling list window where messages will go
        model = new DefaultListModel<ChatMessage>();
        jList = new JList<>(model);
        Font bigger = jList.getFont().deriveFont(18f);
        jList.setFont(bigger);
        jList.setBackground(transparent);
        jList.setForeground(Color.WHITE);
        jList.setCellRenderer(new BiblicalChatCellRenderer(460, 18));
        scrollPane = new JScrollPane(jList);
        scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(new EmptyBorder(0,0,0,20));
        scrollPane.setBackground(transparent);
        scrollPane.getVerticalScrollBar().setVisible(false);
        scrollPane.getHorizontalScrollBar().setVisible(false);
        this.jFrame.add(scrollPane, BorderLayout.CENTER);

        // "Show" the window, it's transparent so not really.
        // But OBS will capture it just fine.
        this.jFrame.pack();
        this.jFrame.setVisible(true);

        this.removeSeenMessagesTimer = getTimer();

    }

    private Timer getTimer() {
        return new Timer("ChatWindowTimer");
    }

    @Override
    public void onChatMessage(ChatMessage chatMessage) {
        model.addElement(chatMessage);
        SwingUtilities.invokeLater(() -> {
            scrollPane.getVerticalScrollBar().setValue(scrollPane.getVerticalScrollBar().getMaximum());
            // We repaint because transparent windows don't like scrolling without smearing, so repaint manually.
            scrollPane.repaint();
        });
        // Should I setup a timer to have the value removed from the model after X seconds?
        long delay = secondsBeforeRemovingChatMessage(chatMessage);
        this.removeSeenMessagesTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                SwingUtilities.invokeLater(() -> {
                    if (model.isEmpty()) {
                        return;
                    }
                    model.removeElementAt(0);
                    SwingUtilities.invokeLater(jFrame::repaint);
                });

            }
        }, delay);
    }

    private long secondsBeforeRemovingChatMessage(ChatMessage chatMessage) {
        return Math.max(minimumSecondsToShow, chatMessage.chatMessage().length() / 5) * 1000L;
    }


    @Override
    public void onReward(String reward) {
        return;
    }
}
