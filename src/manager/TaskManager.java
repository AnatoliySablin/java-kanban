package manager;

import model.Status;
import model.Epic;
import model.Task;
import model.Subtask;

import java.util.HashMap;
import java.util.ArrayList;

public class TaskManager {
    private int count;
    private HashMap < Integer, Task> tasks = new HashMap<>();
    private HashMap < Integer, Epic > epics = new HashMap<>();
    private HashMap < Integer, Subtask> subtasks = new HashMap<>();

    private int getCount(){
        return count+= 1;
    }

    public Task createTask(Task task) {
        if(task != null) {
            task.setId(getCount());
            tasks.put(task.getId(), task);
        }
        return task;
    }

    public Epic createEpic(Epic epicId) {
        if (epicId != null) {
            epicId.setId(getCount());
            epics.put(epicId.getId(), epicId);
        }
        return epicId;
    }

    public Subtask createSubtask(Subtask subtask) {
        if (subtask != null) {
            subtask.setId(getCount());
            subtasks.put(subtask.getId(), subtask);
        }
        return subtask;
    }


    public ArrayList<Task> getTasks() {
        ArrayList<Task> tasksArrayList = new ArrayList<>();
        if (!tasks.isEmpty()) {
            return new ArrayList<>(tasks.values());
        }
        return null;
    }

    public ArrayList<Epic> getEpics() {
        ArrayList<Epic> epicsArrayList = new ArrayList<>();
        if (!epics.isEmpty()) {
            return new ArrayList<>(epics.values());
        }
        return null;
    }

    public ArrayList<Subtask> getSubtasks() {
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>();
        if (!subtasks.isEmpty()) {
            return new ArrayList<>(subtasks.values());
        }
        return null;
    }

    public void clearTasksList() {
        tasks.clear();
    }

    public boolean clearEpicList() {
        if (!epics.isEmpty()) {
            epics.clear();
            subtasks.clear();
            return true;
        }
        return false;
    }

    public boolean clearSubtaskList() {
        if (!subtasks.isEmpty() && !epics.isEmpty()) {
            for (Epic epic : epics.values()) {
                epic.getSubtaskCodes().clear();
                refreshStatus(epic.getId());
            }
            subtasks.clear();
            return true;
        }
        return false;
    }

    public Task getTaskFromList(int id) {
            return tasks.get(id);
    }

    public Epic getEpicFromList(int id) {
            return epics.get(id);
    }

    public Subtask getSubtaskFromList(int id) {
            return subtasks.get(id);
    }

    public boolean deleteTask(int id) {
        if (!tasks.isEmpty() && tasks.containsKey(id)) {
            tasks.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteEpic(int id) {
        if (!epics.isEmpty() && epics.containsKey(id)) {
            ArrayList<Integer> codes =  epics.get(id).getSubtaskCodes();
            for (Integer code : codes) {
                subtasks.remove(code);
            }
            epics.remove(id);
            return true;
        }
        return false;
    }

    public boolean deleteSubtask(int id) {
        if (!subtasks.isEmpty() && subtasks.containsKey(id)) {
            Subtask subtask = subtasks.get(id);
                Epic epic = epics.get(subtask.getEpicId()) ;
                if (epic != null) {
                    ArrayList<Integer> codes = epic.getSubtaskCodes();
                    codes.remove((Integer) subtask.getId());
                    subtasks.remove(id);
                    refreshStatus(epic.getId());
                }
                return true;
        }
        return false;
    }

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

    public Epic updateEpic(Epic epic) {
        if (epic != null ) {
            int id = epic.getId();
            if (epics.containsKey(id)) {
                epics.put(epic.getId(), epic);
                return epic;
            }
        }
        return null;
    }

    public Subtask updateSubtask(Subtask subtask) {
        if(subtask != null) {
            int id = subtask.getId();
            if (subtasks.containsKey(id)) {
                subtasks.put(id, subtask);
                refreshStatus(subtask.getEpicId());
                return subtask;
            }
        }

        return null;
    }

    public ArrayList<Subtask> getSubtaskByEpic (Epic epicId) {
        ArrayList<Subtask> subtasksArrayList = new ArrayList<>();
        if(epicId != null) {
            for (Integer id : epicId.getSubtaskCodes()) {
                subtasksArrayList.add(subtasks.get(id));
            }
        }
        return subtasksArrayList;
    }


    private void refreshStatus(int epicId) {
        Epic epic = getEpicFromList(epicId);
        if (epic != null) {
            ArrayList<Status> statuses = getSubtaskStatusesList(epic);
            Status epicStatus = calculateEpicStatus(statuses);
            updateEpic(new Epic(epic.getName(), epic.getDescription(), epic.getId(), epicStatus, epic.getSubtaskCodes()));
        }
    }

    private ArrayList<Status> getSubtaskStatusesList(Epic epic) {
        ArrayList<Subtask> subtaskArrayList = getSubtaskByEpic(epic);
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
        if(!statuses.isEmpty()) {
            if (!statuses.contains(Status.DONE) && !statuses.contains(Status.IN_PROGRESS)){
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

