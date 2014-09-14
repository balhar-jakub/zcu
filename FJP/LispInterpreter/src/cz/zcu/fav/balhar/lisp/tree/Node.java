package cz.zcu.fav.balhar.lisp.tree;

import java.util.ArrayList;
import java.util.List;

/**
 * It represents one Node of the Tree, which I create by Syntactic parser. It can be either leaf or
 * complex node. Eevry node has parent and list of descendants. It also has expression, which this
 * node represents. It is either lisp function or atomic expression or list. Even list is stored
 * as String.
 *
 * @author Jakub Balhar
 */
public class Node {
    private boolean leaf = true;
    private List<Node> descendants;
    private Node parent;
    private String expression;
    private boolean hasExpression = false;

    /**
     * Every node except for the root has parent. Substitute nods used in calculations has parent null,
     * but they are just placeholders.
     *
     * @param parent parent of this node in the tree.
     */
    public Node(Node parent){
        this.leaf = false;
        this.parent = parent;

        descendants = new ArrayList<Node>();
    }

    /**
     * It says that this Node is leaf. Default state is that it is not.
     */
    public void setAsLeaf(){
        this.leaf= true;
    }

    /**
     * It sets expression for this node.
     *
     * @param expression expression this node holds.
     */
    public void setExpression(String expression){
        this.expression = expression;
        hasExpression = true;
    }

    /**
     * It adds descendant
     *
     * @param descendant descendant to add.
     */
    public void addDescendant(Node descendant){
        descendants.add(descendant);
    }

    /**
     * It returns all straight descendants that are leafs.
     *
     * @return Lists of leaf descendants. It may be empty.
     */
    public List<Node> getLeafDescendants(){
        List<Node> leafs = new ArrayList<Node>();
        for(Node descendant: descendants){
            if(descendant.isLeaf()){
                leafs.add(descendant);
            }
        }
        return leafs;
    }

    /**
     * It returns all straight descendants that are not leafs.
     *
     * @return Lists of complex descendants. It may be empty.
     */
    public List<Node> getOtherDescendants(){
        List<Node> leafs = new ArrayList<Node>();
        for(Node descendant: descendants){
            if(!descendant.isLeaf()){
                leafs.add(descendant);
            }
        }
        return leafs;
    }


    // Getters
    /**
     * Simple getter
     *
     * @return True if it is leaf
     */
    public boolean isLeaf(){
        return leaf;
    }

    /**
     * Simple getter
     *
     * @return parent of the node
     */
    public Node getParent(){
        return parent;
    }

    /**
     * Simple getter
     *
     * @return expression associated with this Node.
     */
    public String getExpression(){
        return expression;
    }

    /**
     * Simple getter
     *
     * @return True if it has any expression set
     */
    public boolean hasExpression() {
        return hasExpression;
    }
}
