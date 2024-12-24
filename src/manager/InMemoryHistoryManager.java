package manager;

import entity.Task;

import java.util.ArrayList;

public class InMemoryHistoryManager implements HistoryManager{
    private ArrayList<Task> history = new ArrayList<>(); // создали список для хранения истории

    private static final int HISTORY_LIMIT = 10; // создали лимит

    @Override
    public void add(Task task) { // добавляем историю
        if (history.size() == HISTORY_LIMIT) {
            history.remove(0);
        }
        history.add(task);
    }

    @Override
    public ArrayList<Task> getHistory() {
        return new ArrayList<>(history);
    }
}
