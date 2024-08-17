package space.peetseater.bot.gui;

import space.peetseater.bot.ChatMessage;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChatMemberCellRenderer implements javax.swing.ListCellRenderer<ChatMessage> {

    protected final int width;
    protected int colorIdx = 0;
    protected final List<Color> chatColors;
    protected final HashMap<String, Color> chatMemberToColor;
    protected int fontSize;

    public ChatMemberCellRenderer(int width, int fontSize) {
        chatMemberToColor = new HashMap<>();
        chatColors = new ArrayList<Color>();
        chatColors.add(Color.WHITE);
        chatColors.add(Color.RED);
        chatColors.add(Color.GREEN);
        chatColors.add(Color.CYAN);
        chatColors.add(Color.PINK);
        this.width = width;
        this.fontSize = fontSize;
    }

    private Color getChatMemberColor(ChatMessage chatMessage) {
        if (chatMemberToColor.containsKey(chatMessage.username())) {
            return chatMemberToColor.get(chatMessage.username());
        }
        colorIdx++;
        Color color = chatColors.get(colorIdx % chatColors.size());
        chatMemberToColor.put(chatMessage.username(), color);
        return color;
    }

    // Overrideable for fun
    protected String formatChatMessage(ChatMessage chatMessage) {
        return "<html><body style='width: %spx'>%s: %s</body></html>".formatted(width - fontSize, chatMessage.username(), chatMessage.chatMessage());
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ChatMessage> list, ChatMessage value, int index, boolean isSelected, boolean cellHasFocus) {
        JLabel label = new JLabel(formatChatMessage(value));
        label.setFont(list.getFont());
        label.setForeground(getChatMemberColor(value));
        return label;
    }
}
