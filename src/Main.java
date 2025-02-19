import manager.Managers;
import manager.TaskManager;
import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;


public class Main {

    private static final TaskManager inMemoryTaskManager = Managers.getDefault();

    public static void main(String[] args) {

        addTasks();
        printAllTasks();
        printViewHistory();
    }

    private static void addTasks() {
        Task washFloor = new Task("Умыться", "Почистить зубы");
        inMemoryTaskManager.addTask(washFloor);

        Task washFloorToUpdate = new Task("Проснуться", "Принять душ",
                washFloor.getId(), Status.IN_PROGRESS);
        inMemoryTaskManager.updateTask(washFloorToUpdate);
        inMemoryTaskManager.addTask(new Task("Приготовить заавтрак", "Сделать яичницу"));


        Epic flatRenovation = new Epic("Купить машину", "Нужно купить в этом году");
        inMemoryTaskManager.addEpic(flatRenovation);
        Subtask flatRenovationSubtask1 = new Subtask("Выбрать модель", "Почитать отзывы!",
                flatRenovation.getId());
        Subtask flatRenovationSubtask2 = new Subtask("Выбрать цвет", "Посоветоваться с женой",
                flatRenovation.getId());
        Subtask flatRenovationSubtask3 = new Subtask("Выбрать автосалон", "Поездить и посмотреть",
                flatRenovation.getId());
        inMemoryTaskManager.addSubtask(flatRenovationSubtask1);
        inMemoryTaskManager.addSubtask(flatRenovationSubtask2);
        inMemoryTaskManager.addSubtask(flatRenovationSubtask3);
        flatRenovationSubtask2.setStatus(Status.DONE);
        inMemoryTaskManager.updateSubtask(flatRenovationSubtask2);
    }

    private static void printAllTasks() {
        System.out.println("Задачи:");
        for (Task task : Main.inMemoryTaskManager.getTasks()) {
            System.out.println(task);
        }
        System.out.println("Эпики:");
        for (Epic epic : Main.inMemoryTaskManager.getEpics()) {
            System.out.println(epic);
        }

        System.out.println("Подзадачи:");
        for (Task subtask : Main.inMemoryTaskManager.getSubtasks()) {
            System.out.println(subtask);
        }
    }

    private static void printViewHistory() {
        //просматриваем 11 задач, в истории должны отобразиться последние 10
        Main.inMemoryTaskManager.getTask(1);
        Main.inMemoryTaskManager.getTask(2);
        Main.inMemoryTaskManager.getEpic(3);
        Main.inMemoryTaskManager.getTask(1);
        Main.inMemoryTaskManager.getSubtask(4);
        Main.inMemoryTaskManager.getSubtask(5);
        Main.inMemoryTaskManager.getSubtask(6);
        Main.inMemoryTaskManager.getEpic(3);
        Main.inMemoryTaskManager.getSubtask(4);
        Main.inMemoryTaskManager.getTask(2);
        Main.inMemoryTaskManager.getSubtask(6);

        System.out.println();
        System.out.println("История просмотров:");
        for (Task task : Main.inMemoryTaskManager.getHistory()) {
            System.out.println(task);
        }
    }
}
