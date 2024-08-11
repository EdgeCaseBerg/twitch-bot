package space.peetseater.bot;

import java.util.LinkedList;
import java.util.List;

public abstract class EventPublisher {

    protected List<EventListener> listeners;
    public EventPublisher() {
        listeners = new LinkedList<>();
    }

    public void addListener(EventListener eventListener) {
        listeners.add(eventListener);
    }

    public void newChatMessageReceived(ChatMessage chatMessage) {
        for (EventListener eventListener : listeners) {
            eventListener.onChatMessage(chatMessage);
        }
    }
    public void newRewardEventReceived(String rewardName) {
        for (EventListener eventListener : listeners) {
            eventListener.onReward(rewardName);
        }
    }
}
