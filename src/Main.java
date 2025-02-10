import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;


public class Main {

    public static void main(String[] args) {
        System.out.println("Поехали!");
        TaskManager taskManager = new TaskManager();
        Task task1 = new Task("Учеба", "Сделать домашнее задание");
        taskManager.createTask(task1);
        Task task2 = new Task("Еда", "Приготовить ужин");
        taskManager.createTask(task2);
        Epic epic1 = new Epic("Провести прраздник", "Подготовиться к празднику");
        Epic epic2 = new Epic("Провести отпуск", "Подготовиться к отпуску");
        int id1 = taskManager.createEpic(epic1);
        int id2 = taskManager.createEpic(epic2);
        Subtask subtask1 = new Subtask("Покупки", "Купить продукты", id1);
        taskManager.createSubtask(subtask1);
        Subtask subtask2 = new Subtask("Покупки", "Купить напитки", id1);
        taskManager.createSubtask(subtask2);
        Subtask subtask3 = new Subtask("Сходить в агенство", "Купить путевки", id2);
        System.out.println(epic2);
        System.out.println(subtask3);
        taskManager.createSubtask(subtask3);
        subtask3.setStatus(Status.IN_PROGRESS);
        taskManager.updateSubtask(subtask3);
        System.out.println(epic2);
        System.out.println(subtask3);
        subtask3.setStatus(Status.DONE);
        taskManager.updateSubtask(subtask3);
        System.out.println(epic2);
        System.out.println(subtask3);
        taskManager.deleteSubtask(subtask3.getId());
    }
}
