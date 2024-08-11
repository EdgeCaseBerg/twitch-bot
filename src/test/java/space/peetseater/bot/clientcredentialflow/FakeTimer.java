package space.peetseater.bot.clientcredentialflow;

import java.util.*;

class FakeTimer extends Timer {
    private final List<TimerTask> tasks;

    public FakeTimer() {
        this.tasks = Collections.synchronizedList(new LinkedList<TimerTask>());
    }

    @Override
    public void schedule(TimerTask task, long delay) {
        this.tasks.add(task);
    }

    public void runTasks() {
        LinkedList<TimerTask> remove = new LinkedList<>();
        List<TimerTask> copy = new ArrayList<>(tasks);
        for (TimerTask task : copy) {
            task.run();
            remove.add(task);
        }
        tasks.removeAll(remove);
    }

    @Override
    public void cancel() {
        LinkedList<TimerTask> remove = new LinkedList<>();
        for (TimerTask task : tasks) {
            task.cancel();
            remove.add(task);
        }
        tasks.removeAll(remove);
    }
}
