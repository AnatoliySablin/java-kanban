package manager;

import model.Epic;
import model.Status;
import model.Subtask;
import model.Task;

import java.time.Duration;
import java.util.*;
import java.util.stream.Collectors;

public class InMemoryTaskManager implements TaskManager {
    protected final HashMap<Integer, Task> tasks = new HashMap<>();
    protected final HashMap<Integer, Epic> epics = new HashMap<>();
    protected final HashMap<Integer, Subtask> subtasks = new HashMap<>();
    protected final Set<Task> prioritizedTasks = new TreeSet<>(Comparator.comparing(Task::getStartTime,
            Comparator.nullsLast(Comparator.naturalOrder())));
    private final HistoryManager historyManager = Managers.getDefaultHistory();
    protected int count;

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
            if (validator(task)) {
                task.setId(getCount());
                tasks.put(task.getId(), task);
                prioritizedTasks.add(task);
            }
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
                if (validator(subtask)) {
                    prioritizedTasks.add(subtask);
                }
                epics.get(epicId).getSubtaskId().add(subtask.getId());
                lifespanOfEpic(epics.get(epicId));
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
        List<Integer> keys = new ArrayList<>(tasks.keySet());
        keys.forEach(this::deleteTask);
    }

    @Override
    public boolean clearEpicList() {
        if (!epics.isEmpty()) {
            List<Integer> epicIds = new ArrayList<>(epics.keySet());
            epicIds.stream().forEach(this::deleteEpic);
            return true;
        }
        return false;
    }

    @Override
    public boolean clearSubtaskList() {
        if (!subtasks.isEmpty() && !epics.isEmpty()) {
            epics.values().forEach(epic -> {
                epic.getSubtaskId().clear();
                updateStatus(epic.getId());
                lifespanOfEpic(epic);
            });

            List<Integer> keys = new ArrayList<>(subtasks.keySet());
            keys.forEach(this::deleteSubtask);

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
            prioritizedTasks.remove(tasks.get(id));
            tasks.remove(id);
            historyManager.remove(id);
            return true;
        }
        return false;
    }

    @Override
    public boolean deleteEpic(int id) {
        if (epics.containsKey(id)) {
            Epic epic = epics.get(id);

            List<Integer> codes = epic.getSubtaskId();
            codes.forEach(code -> {
                prioritizedTasks.remove(subtasks.get(code));
                subtasks.remove(code);
                historyManager.remove(code);
            });


            epics.remove(id);
            historyManager.remove(id);

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
            prioritizedTasks.remove(subtasks.get(id));
            subtasks.remove(id);
            lifespanOfEpic(epics.get(subtask.getEpicId()));
            historyManager.remove(id);
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
                if (validator(task)) {
                    prioritizedTasks.remove(tasks.get(id));
                    prioritizedTasks.add(task);
                }
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
                if (validator(subtask)) {
                    prioritizedTasks.remove(subtasks.get(id));
                    prioritizedTasks.add(subtask);
                    lifespanOfEpic(epics.get(subtask.getEpicId()));
                }
                return subtask;
            }
        }
        return null;
    }

    @Override
    public List<Subtask> getEpicSubtasks(int epicId) {
        Epic epic = epics.get(epicId);

        if (epic == null) {
            return Collections.emptyList();
        }

        return epic.getSubtaskId()
                .stream()
                .map(subtasks::get)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }


    protected void updateStatus(int epicId) {
        Epic epic = getEpic(epicId);
        if (epic != null) {
            ArrayList<Status> statuses = getSubtaskStatusesList(epicId);
            Status epicStatus = calculateEpicStatus(statuses);
            epic.setStatus(epicStatus);
        }
    }

    private ArrayList<Status> getSubtaskStatusesList(int epicId) {
        List<Subtask> subtaskArrayList = getEpicSubtasks(epicId);
        ArrayList<Status> statuses = new ArrayList<>();
        if (!subtaskArrayList.isEmpty()) {
            for (Subtask subtaskFromList : subtaskArrayList) {
                statuses.add(subtaskFromList.getStatus());
            }
        }
        return statuses;
    }

    protected Status calculateEpicStatus(ArrayList<Status> statuses) {
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

    public void lifespanOfEpic(Epic epic) {
        if (!epic.getSubtaskId().isEmpty()) {
            final Subtask minSubtask = (Subtask) epic.getSubtaskId().stream()
                    .map(this::getSubtask)
                    .min(Comparator.comparing(Subtask::getStartTime))
                    .orElseThrow();
            epic.setStartTime(minSubtask.getStartTime());

            final Subtask maxSubtask = (Subtask) epic.getSubtaskId().stream()
                    .map(this::getSubtask)
                    .max(Comparator.comparing(Subtask::getEndTime))
                    .orElseThrow();
            epic.setEndTime(maxSubtask.getEndTime());

            if (Optional.ofNullable(epic.getStartTime()).isPresent()
                    && Optional.ofNullable(epic.getEndTime()).isPresent()) {
                Duration totalDuration = epic.getSubtaskId()
                        .stream()
                        .map(subtasks::get)
                        .map(Subtask::getDuration)
                        .reduce(Duration.ZERO, Duration::plus);

                epic.setDuration(totalDuration);
            } else {
                epic.setDuration(null);
            }
        } else {
            epic.setStartTime(null);
            epic.setDuration(null);
            epic.setEndTime(null);
        }
    }

    private boolean validator(Task task) {
        if (task == null) {
            throw new IllegalArgumentException("Task не может быть null");
        }

        if (task.getStartTime() == null || task.getEndTime() == null) {
            throw new IllegalArgumentException("Время начала и окончания задачи не могут быть null");
        }

        return prioritizedTasks.stream()
                .filter(t -> t.getId() != task.getId())
                .noneMatch(prioritizedTask -> {
                    return !(
                            task.getEndTime().isBefore(prioritizedTask.getStartTime()) ||
                                    task.getStartTime().isAfter(prioritizedTask.getEndTime())
                    );
                });
    }

    public List<Task> getPrioritizedTasks() {
        return new ArrayList<>(prioritizedTasks);
    }
}

