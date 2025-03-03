package manager;

import model.Node;
import model.Task;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class InMemoryHistoryManager implements HistoryManager {

    private Node<Task> head;
    private Node<Task> tail;
    private HashMap<Integer, Node<Task>> taskHistory;


    public InMemoryHistoryManager() {
        this.taskHistory = new HashMap<>();

    }

    @Override
    public void add(Task task) {
        if (taskHistory.containsKey(task.getId())) {
            removeNode(taskHistory.get(task.getId()));
        }
        taskHistory.put(task.getId(), linkLast(task));

    }

    @Override
    public void remove(int id) {
        if (taskHistory.containsKey(id)) {
            removeNode(taskHistory.get(id));
        }
    }

    @Override
    public List<Task> getHistory() {
        ArrayList<Task> list = new ArrayList<>();

        for (Node<Task> node : taskHistory.values()) {
            list.add(node.getData());
        }
        return list;

    }

    public Node<Task> linkLast(Task task) {

        Node<Task> tailNode = this.tail;
        Node<Task> newNode = new Node<>(tailNode, task, null);

        this.tail = newNode;
        if (tailNode == null) {
            head = newNode;
        } else {
            tailNode.setNext(newNode);
        }
        return newNode;
    }

    private void removeNode(Node<Task> node) {

        Node<Task> prevNode = node.getPrev();
        Node<Task> nextNode = node.getNext();
        taskHistory.remove(node.getData().getId());

        if (prevNode == null) {
            head = nextNode;
        } else {
            prevNode.setNext(nextNode);
            node.setPrev(null);
        }
        if (nextNode == null) {
            tail = prevNode;
        } else {
            nextNode.setPrev(prevNode);
            node.setNext(null);
        }
    }
}