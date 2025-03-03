package manager;

import model.Status;
import model.Epic;
import model.Task;
import model.Subtask;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class InMemoryTaskManager implements TaskManager {
    private int count;
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    private final HistoryManager historyManager = Managers.getDefaultHistory();

    @Override
    public List<Task> getHistory() {
        return historyManager.getHistory();
    }


    private int getCount() {
        return count += 1;
    }


    @Override
    public int addTask(Task task) {
        if (task != null) {
            task.setId(getCount());
            tasks.put(task.getId(), task);
            return task.getId();
        } else {
            return -1;
        }
    }

    @Override
    public int addEpic(Epic epic) {
        if (epic != null) {
            epic.setId(getCount());
            epics.put(epic.getId(), epic);
            return epic.getId();
        } else {
            return -1;
        }
    }

    @Override
    public int addSubtask(Subtask subtask) {
        if (subtask != null) {
            int epicId = subtask.getEpicId();
            if (epics.containsKey(epicId)) {
                subtask.setId(getCount());
                subtasks.put(subtask.getId(), subtask);
                epics.get(epicId).getSubtaskId().add(subtask.getId());
                updateStatus(epicId);
            }
            return subtask.getId();
        } else {
            return -1;
        }
    }


    @Override
    public ArrayList<Task> getTasks() {
        return new ArrayList<>(tasks.values());
    }

    @Override
    public ArrayList<Epic> getEpics() {
        return new ArrayList<>(epics.values());
    }

    @Override
    public ArrayList<Subtask> getSubtasks() {
        return new ArrayList<>(subtasks.values());
    }

    @Override
    public void clearTasksList() {
        tasks.clear();
    }

    @Override
    public boolean clearEpicList() {
        if (!epics.isEmpty()) {
            epics.clear();
            subtasks.clear();
            return true;
        }
        return false;
    }

    @Override
    public boolean clearSubtaskList() {
        if (!subtasks.isEmpty() && !epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.getSubtaskId().clear();
                updateStatus(epic.getId());
            }
            subtasks.clear();
            return true;
        }
        return false;
    }

    @Override
    public Task getTask(int id) {
        Task task = tasks.get(id);
        if (task != null) {
            historyManager.add(task);
        }
        return task;
    }

    @Override
    public Epic getEpic(int id) {
        Epic epic = epics.get(id);
        if (epic != null) {
            historyManager.add(epic);
        }
        return epic;
    }

    @Override
    public Subtask getSubtask(int id) {
        Subtask subtask = subtasks.get(id);
        if (subtask != null) {
            historyManager.add(subtask);
        }
        return subtask;
    }

    @Override
    public boolean deleteTask(int id) {
        if (!tasks.isEmpty() && tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (epics.containsKey(id)) {
            ArrayList<Integer> codes = epics.get(id).getSubtaskId();
            for (Integer code : codes) {
                subtasks.remove(code);
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteSubtask(int id) {
        if (!subtasks.isEmpty() && subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
            Epic epic = epics.get(subtask.getEpicId());
            if (epic != null) {
                ArrayList<Integer> codes = epic.getSubtaskId();
                codes.remove((Integer) subtask.getId());
            }
            subtasks.remove(id);
            updateStatus(epic.getId());
            return true;
        }
        return false;
    }

    @Override
    public Task updateTask(Task task) {
        if (task != null) {
            int id = task.getId();
            if (tasks.containsKey(id)) {
                tasks.put(task.getId(), task);
                return task;
            }
        }

        return null;
    }

    @Override
    public Epic updateEpic(Epic epic) {
        if (epic != null) {
            int epicId = epic.getId();
            if (epics.containsKey(epicId)) {
                Epic unupdatedEpic = epics.get(epicId);
                unupdatedEpic.setName(epic.getName());
                unupdatedEpic.setDescription(epic.getDescription());
                return epic;
            }
        }
        return null;
    }

    @Override
    public Subtask updateSubtask(Subtask subtask) {
        if (subtask != null) {
            int id = subtask.getId();
            if (subtasks.containsKey(id)) {
                subtasks.put(id, subtask);
                updateStatus(subtask.getEpicId());
                return subtask;
            }
        }

        return null;
    }

    @Override
    public ArrayList<Subtask> getEpicSubtasks(int epicId) {
        ArrayList<Subtask> tasks = new ArrayList<>();
        Epic epic = epics.get(epicId);
        if (epic == null) {
            return tasks;
        }
        for (int id : epic.getSubtaskId()) {
            tasks.add(subtasks.get(id));
        }
        return tasks;
    }


    private void updateStatus(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic != null) {
            ArrayList<Status> statuses = getSubtaskStatusesList(epicId);
            Status epicStatus = calculateEpicStatus(statuses);
            epic.setStatus(epicStatus);
        }
    }

    private ArrayList<Status> getSubtaskStatusesList(int epicId) {
        ArrayList<Subtask> subtaskArrayList = getEpicSubtasks(epicId);
        ArrayList<Status> statuses = new ArrayList<>();
        if (!subtaskArrayList.isEmpty()) {
            for (Subtask subtaskFromList : subtaskArrayList) {
                statuses.add(subtaskFromList.getStatus());
            }
        }
        return statuses;
    }

    private Status calculateEpicStatus(ArrayList<Status> statuses) {
        Status epicStatus = Status.IN_PROGRESS;
        if (!statuses.isEmpty()) {
            if (!statuses.contains(Status.DONE) && !statuses.contains(Status.IN_PROGRESS)) {
                epicStatus = Status.NEW;
            } else if (!statuses.contains(Status.NEW) && !statuses.contains(Status.IN_PROGRESS)) {
                epicStatus = Status.DONE;
            }
        } else {
            epicStatus = Status.NEW;
        }
        return epicStatus;
    }
}

