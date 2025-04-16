package manager;

import exceptions.ManagerSaveException;
import model.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class FileBackedTaskManager extends InMemoryTaskManager {

    private static final String CSV = "id,type,name,status,description,LocalDataTime, Duration, epic \n";
    protected File file;

    public FileBackedTaskManager(File file) {
        this.file = file;
    }

    public static FileBackedTaskManager loadFromFile(File file) {
        FileBackedTaskManager fileBackedTaskManager = new FileBackedTaskManager(file);
        try (BufferedReader bf = new BufferedReader(new FileReader(file, StandardCharsets.UTF_8))) {
            bf.readLine();
            ArrayList<Integer> id = new ArrayList<>();
            Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
                    Comparator.nullsLast(Comparator.naturalOrder())));
            while (bf.ready()) {
                String line = bf.readLine();
                if (line.isEmpty()) {
                    break;
                }
                Task task = fromString(line);
                id.add(task.getId());
                switch (task.getTaskType()) {
                    case EPIC:
                        fileBackedTaskManager.epics.put(task.getId(), (Epic) task);
                        break;
                    case TASK:
                        fileBackedTaskManager.tasks.put(task.getId(), (Task) task);
                        prioritizedTasks.add(task);
                        break;
                    case SUBTASK:
                        String[] lineArr = line.split(",");
                        Subtask subtask = new Subtask(lineArr[1], lineArr[2], Integer.parseInt(lineArr[3]),
                                Status.valueOf(lineArr[4]), Integer.parseInt(lineArr[5]),
                                LocalDateTime.parse(lineArr[6], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                                Duration.parse(lineArr[7]));
                        fileBackedTaskManager.subtasks.put(subtask.getId(), subtask);
                        if (subtask.getEndTime().isAfter(task.getEndTime())) {
                            fileBackedTaskManager.epics.get(subtask.getEpicId()).setEndTime(subtask.getEndTime());
                        }
                        subtask.setId(task.getId());
                        int epicId = subtask.getEpicId();
                        if (fileBackedTaskManager.epics.containsKey(epicId)) {
                            fileBackedTaskManager.epics.get(epicId).getSubtaskId().add(subtask.getId());
                            prioritizedTasks.add(task);
                        }
                        break;
                }
            }
            if (id.isEmpty()) {
                fileBackedTaskManager.count = 0;
            } else {
                fileBackedTaskManager.count = Collections.max(id);
            }
        } catch (IOException e) {
            throw new ManagerSaveException("Ошибка загрузки из файла");
        }
        return fileBackedTaskManager;
    }

    private static Task fromString(String line) {
        try {
            String[] lineArr = line.split(",");
            switch (TaskType.valueOf(lineArr[0])) {
                case TASK:
                    Task task = new Task(lineArr[1], lineArr[2], Integer.parseInt(lineArr[3]),
                            Status.valueOf(lineArr[4]), LocalDateTime.parse(lineArr[5],
                            DateTimeFormatter.ISO_LOCAL_DATE_TIME), Duration.parse(lineArr[6]));
                    task.setId(Integer.parseInt(lineArr[3]));
                    return task;
                case EPIC:
                    Epic epic = new Epic(lineArr[1], lineArr[2]);
                    epic.setStatus(Status.valueOf(lineArr[4]));
                    epic.setId(Integer.parseInt(lineArr[3]));
                    return epic;
                case SUBTASK:
                    Subtask subtask = new Subtask(lineArr[1], lineArr[2], Integer.parseInt(lineArr[3]),
                            Status.valueOf(lineArr[4]), Integer.parseInt(lineArr[5]),
                            LocalDateTime.parse(lineArr[6], DateTimeFormatter.ISO_LOCAL_DATE_TIME),
                            Duration.parse(lineArr[7]));
                    subtask.setId(Integer.parseInt(lineArr[3]));
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
        boolean result = super.clearEpicList();
        save();
        return result;
    }

    @Override
    public boolean clearSubtaskList() {
        boolean result = super.clearSubtaskList();
        save();
        return result;
    }

    @Override
    public Task updateTask(Task newTask) {
        Task result = super.updateTask(newTask);
        save();
        return result;
    }

    @Override
    public Epic updateEpic(Epic newEpic) {
        Epic result = super.updateEpic(newEpic);
        save();
        return result;
    }

    @Override
    public Subtask updateSubtask(Subtask newSubtask) {
        Subtask result = super.updateSubtask(newSubtask);
        save();
        return result;
    }

    @Override
    public boolean deleteTask(int id) {
        boolean result = super.deleteTask(id);
        save();
        return result;
    }

    @Override
    public boolean deleteEpic(int id) {
        boolean result = super.deleteEpic(id);
        save();
        return result;
    }

    @Override
    public boolean deleteSubtask(int id) {
        boolean result = super.deleteSubtask(id);
        save();
        return result;
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
            return String.format("%s,%s,%s,%s,%s,%s,%s,%s\n", task.getTaskType(), task.getName(),
                    task.getDescription(), task.getId(),
                    task.getStatus(), ((Subtask) task).getEpicId(), task.getStartTime(), task.getDuration());
        } else {
            return String.format("%s,%s,%s,%s,%s,%s,%s\n", task.getTaskType(), task.getName(), task.getDescription(),
                    task.getId(), task.getStatus(), task.getStartTime(), task.getDuration());
        }
    }
}
