package datastructures;

import java.io.Serializable;
import java.util.Iterator;
import java.util.Stack;

import model.DAO.LogDAO;

public class AVL<T> implements Serializable, Iterable<T> {
    Node<T> raiz = null;
    int size = 0;

    static private final long serialVersionUID = 1L;

    public AVL() {}

    public void order() {
        order(raiz);
    }

    public void order(Node<T> tree) {
        if (tree == null) {
            return;
        }

        order(tree.left);
        System.out.println(tree.data + "\n---------------------------------");
        order(tree.right);
    }

    public void insert(long key, T data) {
        raiz = insert(raiz, key, data);
    }

    private Node<T> insert(Node<T> tree, long key, T data) {
        if (tree == null) return new Node<T>(key, data);
        
        if (key < tree.key) {
            tree.left = insert(tree.left, key, data);
        }
        else if (key > tree.key) {
            tree.right = insert(tree.right, key, data);
        } else {
            tree.data = data;
            return tree;
        }
        
        // Update the height of the node
        tree.high = 1 + biggest(high(tree.left), high(tree.right));

        tree = balanceNode(tree);

        return tree;
    }

    public void remove(long key) throws Exception {
        remove(raiz, key);
    }
    
    public Node<T> remove(Node<T> tree, long key) throws Exception {
        if (tree == null) {
            throw new Exception("Node not found");
        }

        if (key < tree.key) {
            tree.left = remove(tree.left, key);
        } else if (key > tree.key) {
            tree.right = remove(tree.right, key);
        } else {
            if (tree.left == null && tree.right == null) {  // If it is a leaf
                if (tree == raiz) raiz = null;
                tree = null;
            } else if (tree.left == null || tree.right == null) {  // If it has only one child
                if (tree == raiz) {
                    tree = (tree.left == null) ? tree.right : tree.left;
                    raiz = tree;
                } else {
                    tree = (tree.left == null) ? tree.right : tree.left;
                }
            } else {  // If it has two children
                // Find the smallest node in the right subtree
                Node<T> temp;
                int bf = getBalanceFactor(tree);
                
                // Remove from the side with the biggest height
                if (bf < 0) {
                    temp = tree.right;

                    // Search for the smallest node in the right subtree
                    while (temp.left != null) {
                        temp = temp.left;
                    }

                    tree.key = temp.key;
                    tree.data = temp.data;
                    tree.right = remove(tree.right, temp.key);  // Remove duplicated node
                } else {
                    temp = tree.left;

                    // Search for the biggest node in the left subtree
                    while (temp.right != null) {
                        temp = temp.right;
                    }

                    // Update the node
                    tree.key = temp.key;
                    tree.data = temp.data;
                    tree.left = remove(tree.left, temp.key);  // Remove duplicated node
                }
            }
        }

        if (tree == null) return tree;

        // Update the height of the node
        tree.high = 1 + biggest(high(tree.left), high(tree.right));

        tree = balanceNode(tree);

        return tree;
    }

    private Node<T> balanceNode(Node<T> tree) {
        int bf = getBalanceFactor(tree);
        int bfLeft = getBalanceFactor(tree.left);
        int bfRight = getBalanceFactor(tree.right);

        // Simple Rotations
        if (bf > 1 && bfLeft >= 0) {
            return rightRotate(tree);
        }

        if (bf < -1 && bfRight <= 0) {
            return leftRotate(tree);
        }

        // Double Rotations
        if (bf > 1 && bfLeft < 0) {
            tree.left = leftRotate(tree.left);
            return rightRotate(tree);
        }

        if (bf < -1 && bfRight > 0) {
            tree.right = rightRotate(tree.right);
            return leftRotate(tree);
        }

        return tree;
    }

    private Node<T> leftRotate(Node<T> tree) {
        Node<T> right = tree.right;
        Node<T> left = right.left;

        right.left = tree;
        tree.right = left;

        tree.high = 1 + biggest(high(tree.left), high(tree.right));
        right.high = 1 + biggest(high(right.left), high(right.right));

        if (tree == raiz) raiz = right;

        LogDAO.addLog("[AVL ROTATE] Rotação à esquerda na AVL");

        return right;
    }

    private Node<T> rightRotate(Node<T> tree) {
        Node<T> left = tree.left;
        Node<T> right = left.right;

        left.right = tree;
        tree.left = right;

        tree.high = 1 + biggest(high(tree.left), high(tree.right));
        left.high = 1 + biggest(high(left.left), high(left.right));

        LogDAO.addLog("[AVL ROTATE] Rotação à direita na AVL");

        if (tree == raiz) raiz = left;

        return left;
    }

    public T search(long key) {
        return search(raiz, key);
    }

    private T search(Node<T> tree, long key) {
        if (tree == null) return null;

        if (key < tree.key) {
            return search(tree.left, key);
        } else if (key > tree.key) {
            return search(tree.right, key);
        } else {
            return tree.data;
        }
    }
    
    public T getLast() {
        return getLast(raiz);
    }

    private T getLast(Node<T> tree) {
        if (tree.right == null) {
            return tree.data;
        }

        return getLast(tree.right);
    }

    private int getBalanceFactor(Node<T> node) {
        return (node == null) ? 0 : high(node.left) - high(node.right);
    }

    private int biggest(int a, int b) {
        return (a > b) ? a : b;
    }

    public int high() {
        return high(raiz);
    }

    private int high(Node<T> tree) {
        if (tree == null) return 0;
        return tree.high;
    }

    public boolean isEmpty() {
        return raiz == null;
    }

    public int countNodes() {
        return countNodes(raiz);
    }

    public T getRaiz() {
        return raiz.data;
    }

    private int countNodes(Node<T> tree) {
        if (tree == null) return 0;
        return 1 + countNodes(tree.left) + countNodes(tree.right);
    }

    @Override
    public Iterator<T> iterator() {
        return new AVLIterator<T>(raiz);
    }

    @SuppressWarnings("hiding")
    private class Node<T> implements Serializable {
        long key;
        T data;
        int high;
        Node<T> left, right;
        private static final long serialVersionUID = 3L;
    
        public Node(long key, T data) {
            this.key = key;
            this.data = data;
            this.high = 1;
        }
    }

    @SuppressWarnings("hiding")
    private class AVLIterator<T> implements Iterator<T> {
        private Node<T> current;
        private Stack<Node<T>> stack = new Stack<>();

        public AVLIterator(Node<T> raiz) {
            current = raiz;
        }

        @Override
        public boolean hasNext() {
            return !stack.isEmpty() || current != null;
        }

        @Override
        public T next() {
            while (current != null) {
                stack.push(current);
                current = current.left;
            }

            current = stack.pop();
            Node<T> node = current;
            current = current.right;

            return node.data;
        }
    }
}
