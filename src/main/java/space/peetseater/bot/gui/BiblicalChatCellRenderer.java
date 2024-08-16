package space.peetseater.bot.gui;

import space.peetseater.bot.ChatMessage;
import space.peetseater.bot.bible.BibleFromFileProvider;
import space.peetseater.bot.bible.BibleScoreCalculator;

import java.nio.file.Paths;

public class BiblicalChatCellRenderer extends ChatMemberCellRenderer {
    private final BibleScoreCalculator biblicalCalculator;

    public BiblicalChatCellRenderer(int width, int fontSize) {
        super(width, fontSize);
        this.biblicalCalculator = new BibleScoreCalculator(new BibleFromFileProvider(Paths.get("akjv.txt")));
    }

    @Override
    protected String formatChatMessage(ChatMessage chatMessage) {
        float bibleScore = biblicalCalculator.calculateScore(chatMessage.chatMessage());
        return "<html><body style='width: %spx'>[Biblicality: %s] %s: %s</body></html>".formatted(width - fontSize, bibleScore, chatMessage.username(), chatMessage.chatMessage());
    }
}
