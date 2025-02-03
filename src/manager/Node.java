package manager;


public class Node<T> {
    T data;
    Node<T> next;
    Node<T> previous;


    public Node(T data, Node<T> previous, Node<T> next) {
        this.data = data;
        this.previous = previous;
        this.next = next;
    }

    public Node(T data) {
        this.data = data;
    }


}