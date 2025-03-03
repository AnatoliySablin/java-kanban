package model;

public class Node<T> {

    private Node<T> prev;
    private Node<T> next;
    private Task data;

    public Node(Node<T> prev, Task data,Node<T> next) {
        this.data = data;
        this.next = next;
        this.prev = prev;
    }

    public Node<T> getPrev() {
        return prev;
    }

    public void setPrev(Node<T> prev) {
        this.prev = prev;
    }

    public Node<T> getNext() {
        return next;
    }

    public void setNext(Node<T> next) {
        this.next = next;
    }

    public Task getData() {
        return data;
    }

    public void setData(Task data) {
        this.data = data;
    }
}
