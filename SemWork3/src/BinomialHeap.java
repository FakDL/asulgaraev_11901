import java.util.Comparator;
import java.util.NoSuchElementException;

public class BinomialHeap<T> {
    private Node head;
    private final Comparator<T> comp;


    private class Node {
        T key;
        int degree;
        Node parent, child, sibling;
    }


    public BinomialHeap() {
        comp = new MyComparator();
    }

    public BinomialHeap(Comparator<T> C) {
        comp = C;
    }

    public BinomialHeap(T[] a) {
        comp = new MyComparator();
        for (T k : a) insert(k);
    }

    public boolean isEmpty() {
        return head == null;
    }

    public void insert(T key) {
        Node x = new Node();
        x.key = key;
        x.degree = 0;
        BinomialHeap<T> H = new BinomialHeap<T>();
        H.head = x;
        this.head = this.union(H).head;
    }

    public T minKey() {
        if (this.isEmpty()) throw new NoSuchElementException("This heap is empty");
        Node min = head;
        Node current = head;
        while (current.sibling != null) {
            min = (greater(min.key, current.sibling.key)) ? current : min;
            current = current.sibling;
        }
        return min.key;
    }

    public T delMin() {
        if(this.isEmpty()) throw new NoSuchElementException("This heap is empty");
        Node min = eraseMin();
        Node x = (min.child == null) ? min : min.child;
        if (min.child != null) {
            min.child = null;
            Node prevx = null, nextx = x.sibling;
            while (nextx != null) {
                x.sibling = prevx;
                prevx = x;
                x = nextx;nextx = nextx.sibling;
            }
            x.sibling = prevx;
            BinomialHeap<T> H = new BinomialHeap<T>();
            H.head = x;
            head = union(H).head;
        }
        return min.key;
    }

    public BinomialHeap<T> union(BinomialHeap<T> heap) {
        if (heap == null) throw new IllegalArgumentException("Can't merge a heap with null");
        this.head = merge(new Node(), this.head, heap.head).sibling;
        Node x = this.head;
        Node prevx = null, nextx = x.sibling;
        while (nextx != null) {
            if (x.degree < nextx.degree ||
                    (nextx.sibling != null && nextx.sibling.degree == x.degree)) {
                prevx = x; x = nextx;
            } else if (greater(nextx.key, x.key)) {
                x.sibling = nextx.sibling;
                link(nextx, x);
            } else {
                if (prevx == null) { this.head = nextx; }
                else { prevx.sibling = nextx; }
                link(x, nextx);
                x = nextx;
            }
            nextx = x.sibling;
        }
        return this;
    }

    public void delete(Node node) {
        node.key = minKey();
        delMin();
    }

    private void decKey(Node node, T key) {
        while (node.parent != null) {
            if(greater(node.parent.key, node.key)){
                node.key = node.parent.key;
                node = node.parent;
            }
        }
        node.key = key;
    }

    private boolean greater(T n, T m) {
        if (n == null) return false;
        if (m == null) return true;
        return comp.compare(n, m) > 0;
    }


    private void link(Node root1, Node root2) {
        root1.sibling = root2.child;
        root2.child = root1;
        root2.degree++;
        root1.parent = root2;
    }

    private Node eraseMin() {
        Node min = head;
        Node previous = null;
        Node current = head;
        while (current.sibling != null) {
            if (greater(min.key, current.sibling.key)) {
                previous = current;
                min = current.sibling;
            }
            current = current.sibling;
        }
        previous.sibling = min.sibling;
        if (min == head) head = min.sibling;
        return min;
    }

    private Node merge(Node h, Node x, Node y) {
        if (x == null && y == null) return h;
        else if (x == null) h.sibling = merge(y, null, y.sibling);
        else if (y == null) h.sibling = merge(x, x.sibling, null);
        else if (x.degree < y.degree) h.sibling = merge(x, x.sibling, y);
        else                        h.sibling = merge(y, x, y.sibling);
        return h;
    }


    private class MyComparator implements Comparator<T> {
        @Override
        public int compare(T key1, T key2) {
            return ((Comparable<T>) key1).compareTo(key2);
        }
    }

}