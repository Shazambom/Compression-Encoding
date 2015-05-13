/**
 * Created by ian on 5/9/15.
 */
public class Node implements
        Comparable<Node>{
    private int weight;
    private byte value;
    private Node left;
    private Node right;


    /**
     * Initializes the object
     * @param weight the weight of the char
     * @param value the value of the char
     */
    public Node(int weight, byte value) {
        this.weight = weight;
        this.value = value;
        left = null;
        right = null;
    }

    /**
     * Initializes the object
     * @param weight the weight of the char
     * @param value the value of the char
     * @param left the left child of the node
     * @param right the right child of the node
     */
    public Node(int weight, byte value, Node left, Node right) {
        this.weight = weight;
        this.value = value;
        this.left = left;
        this.right = right;
    }


    /**
     * Gets the left child node
     * @return the left variable
     */
    public Node getLeft() {
        return left;
    }

    /**
     * Sets the left child
     * @param left the node to be set to left
     */
    public void setLeft(Node left) {
        this.left = left;
    }

    /**
     * Gets the right child node
     * @return the right variable
     */
    public Node getRight() {
        return right;
    }

    /**
     * Sets the right child
     * @param right the node to be set to right
     */
    public void setRight(Node right) {
        this.right = right;
    }

    /**
     * Gets the weight
     * @return the weight of the char
     */
    public int getWeight() {
        return weight;
    }

    /**
     * Sets the weight variable
     * @param weight the new value of weight
     */
    public void setWeight(int weight) {
        this.weight = weight;
    }
    /**
     * Gets the value
     * @return the value of the char
     */
    public byte getValue() {
        return value;
    }

    /**
     * Increases the weight of the Node
     */
    public void increaseWeight() {
        weight++;
    }

    @Override
    public int compareTo(Node other) {
        if (weight < other.getWeight()) {
            return -1;
        } else if (weight > other.getWeight()) {
            return 1;
        } else {
            return ((int) value) - ((int) other.getValue());
        }
    }
}
