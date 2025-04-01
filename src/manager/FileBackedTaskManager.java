package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String CSV = "id,type,name,status,description,epic \n";
    protected File file;

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
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), (Task) task);
                        break;
                    case SUBTASK:
                        String[] lineArr = line.split(",");
                        Subtask subtask = new Subtask(task.getName(), task.getDescription(), task.getStatus(), Integer.parseInt(lineArr[5]));
                        subtask.setId(fileBackedTaskManager.getCount());
                        int epicId = subtask.getEpicId();
                        if (epics.containsKey(epicId)) {
                            fileBackedTaskManager.subtasks.put(task.getId(), (Subtask) subtask);
                            epics.get(epicId).getSubtaskId().add(subtask.getId());
                        }
                        break;
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String line) {
        try {
            String[] lineArr = line.split(",");
            switch (TaskType.valueOf(lineArr[1])) {
                case TASK:
                    Task task = new Task(lineArr[2], lineArr[4], Status.valueOf(lineArr[3]));
                    task.setId(Integer.parseInt(lineArr[0]));
                    return task;
                case EPIC:
                    Epic epic = new Epic(lineArr[2], lineArr[4]);
                    epic.setStatus(Status.valueOf(lineArr[3]));
                    epic.setId(Integer.parseInt(lineArr[0]));
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(lineArr[2], lineArr[4], Status.valueOf(lineArr[3]), Integer.parseInt(lineArr[5]));
                    subtask.setId(Integer.parseInt(lineArr[0]));
                    return subtask;
                default:
                    throw new IllegalArgumentException("Неизвестный тип задачи: " + lineArr[1]);
            }
        } catch (Exception e) {
            throw new RuntimeException("Ошибка при парсинге строки: " + line, e);
        }
    }

    @Override
    public int addTask(Task task) {
        int id = super.addTask(task);
        save();
        return id;
    }

    @Override
    public int addEpic(Epic epic) {
        int id = super.addEpic(epic);
        save();
        return id;
    }

    @Override
    public int addSubtask(Subtask subtask) {
        int id = super.addSubtask(subtask);
        save();
        return id;
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
        return super.clearEpicList();
    }

    @Override
    public boolean clearSubtaskList() {
        super.clearSubtaskList();
        save();
        return super.clearSubtaskList();
    }

    @Override
    public Task updateTask(Task newTask) {
        super.updateTask(newTask);
        save();
        return super.updateTask(newTask);
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        super.updateEpic(newEpic);
        save();
        return super.updateEpic(newEpic);
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        super.updateSubtask(newSubtask);
        save();
        return super.updateSubtask(newSubtask);
    }

    @Override
    public boolean deleteTask(int id) {
        super.deleteTask(id);
        save();
        return super.deleteTask(id);
    }

    @Override
    public boolean deleteEpic(int id) {
        super.deleteEpic(id);
        save();
        return super.deleteEpic(id);
    }

    @Override
    public boolean deleteSubtask(int id) {
        super.deleteSubtask(id);
        save();
        return super.deleteSubtask(id);
    }


    protected void save() {
        try (FileWriter fw = new FileWriter(file, StandardCharsets.UTF_8)) {

            fw.write(CSV);

            if (super.getTasks().isEmpty()) {
                fw.write("");
            } else {
                for (Task task : getTasks()) {
                    fw.write(toString(task));
                }

                for (Epic epic : getEpics()) {
                    fw.write(toString(epic));
                }

                for (Subtask subtask : getSubtasks()) {
                    fw.write(toString(subtask));
                }
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка сохранения в файл");
        }
    }

    private String toString(Task task) {
        if (task.getTaskType().equals(TaskType.SUBTASK)) {
            return String.format("%s,%s,%s,%s,%s,%s\n", task.getId(), task.getTaskType(),
                    task.getName(), task.getStatus(), task.getDescription(), ((Subtask) task).getEpicId());
        } else {
            return String.format("%s,%s,%s,%s,%s\n", task.getId(), task.getTaskType(),
                    task.getName(), task.getStatus(), task.getDescription());
        }
    }

    private int getCount() {
        for (Task task : tasks.values()) {
            count = Math.max(count, task.getId());
        }

        for (Epic epic : epics.values()) {
            count = Math.max(count, epic.getId());
        }

        for (Subtask subtask : subtasks.values()) {
            count = Math.max(count, subtask.getId());
        }

        if (count > 0) {
            return count;
        } else {
            return 0;
        }
    }
}
