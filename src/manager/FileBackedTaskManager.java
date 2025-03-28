package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String CSV = "id,type,name,status,description,epic \n";
    private File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bf = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            String line = bf.readLine();
            while (bf.ready()) {
                line = bf.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                switch (task.getTaskType()) {
                    case EPIC:
                        fileBackedTaskManager.addEpic((Epic) task);
                        break;
                    case TASK:
                        fileBackedTaskManager.addTask(task);
                        break;
                    case SUBTASK:
                        Subtask subtask = new Subtask(task.getName(), task.getDescription(), task.getStatus());
                        String[] lineArr = line.split(",");
                        subtask.setEpicId(Integer.parseInt(lineArr[5]));
                        fileBackedTaskManager.addTask(subtask);
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String line) {
        String[] lineArr = line.split(",");
        switch (TaskType.valueOf(lineArr[1])) {
            case TASK:
                Task task = new Task(lineArr[2], lineArr[4], Status.valueOf(lineArr[3]));
                task.setId(Integer.parseInt(lineArr[0]));
                task.setTaskType(TaskType.TASK);
                return task;
            case EPIC:
                Epic epic = new Epic(lineArr[2], lineArr[4]);
                epic.setStatus(Status.valueOf(lineArr[3]));
                epic.setId(Integer.parseInt(lineArr[0]));
                epic.setTaskType(TaskType.EPIC);
                return epic;
            case SUBTASK:
                Subtask subtask = new Subtask(lineArr[2], lineArr[4], Status.valueOf(lineArr[3]));
                subtask.setEpicId(Integer.parseInt(lineArr[5]));
                subtask.setId(Integer.parseInt(lineArr[0]));
                subtask.setTaskType(TaskType.SUBTASK);
                return subtask;
            default:
                Task task1 = null;
                return task1;
        }
    }

    @Override
    public int addTask(Task task) {
        super.addTask(task);
        save();
        return 0;
    }

    @Override
    public int addEpic(Epic epic) {
        super.addEpic(epic);
        save();
        return 0;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        super.addSubtask(subtask);
        save();
        return 0;
    }

    @Override
    public void clearTasksList() {
        super.clearTasksList();
        save();
    }

    @Override
    public boolean clearEpicList() {
        super.clearEpicList();
        save();
        return false;
    }

    @Override
    public boolean clearSubtaskList() {
        super.clearSubtaskList();
        save();
        return false;
    }

    @Override
    public Task updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
        return newTask;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
        return newEpic;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
        return newSubtask;
    }

    @Override
    public boolean deleteTask(int id) {
        super.deleteTask(id);
        save();
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        super.deleteEpic(id);
        save();
        return false;
    }

    @Override
    public boolean deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
        return false;
    }

    public void save() {
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {

            fw.write(CSV);

            if (super.getTasks().isEmpty()) {
                fw.write("");
            } else {
                for (Task task : super.getTasks()) {
                    fw.write(toString(task));
                }
            }

            if (super.getEpics().isEmpty()) {
                fw.write("");
            } else {
                for (Epic epic : super.getEpics()) {
                    fw.write(toString(epic));
                }
            }

            if (super.getSubtasks().isEmpty()) {
                fw.write("");
            } else {
                for (Subtask subtask : super.getSubtasks()) {
                    fw.write(toString(subtask));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    public String toString(Task task) {
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), task.getTaskType(),
                    task.getName(), task.getStatus(), task.getDescription(), ((Subtask) task).getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s\n", task.getId(), task.getTaskType(),
                    task.getName(), task.getStatus(), task.getDescription());
        }
    }
}
