package space.peetseater.bot;

public interface EventListener {
    void onChatMessage(ChatMessage chatMessage);
    void onReward(String reward);
}
