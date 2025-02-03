package manager;

public class Managers {

    public static TaskManager getDefaultManger() {
        return new InMemoryTaskManager();
    }


    public static HistoryManager getDefaultHistory() {
        return new InMemoryHistoryManager();
    }
}