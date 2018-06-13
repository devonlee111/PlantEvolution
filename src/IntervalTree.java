import java.util.ArrayList;
import java.util.List;

public class IntervalTree {

    public IntervalNode root;

    public IntervalNode insert(IntervalNode root, Interval newInterval) {
    	IntervalNode newNode = new IntervalNode(newInterval);
    	
        if (root == null) {
            root = newNode;
            return root;
        }

        if (newInterval.end > root.max) {
            root.max = newInterval.end;
        }

        if (root.i.compareTo(newInterval) <= 0) {
            if (root.right == null) {
                root.right = newNode;
            }
            else {
                insert(root.right, newInterval);
            }
        }
        else {
            if (root.left == null) {
                root.left = (newNode);
            }
            else {
                insert(root.left, newInterval);
            }
        }
        return root;
    }

    public void printTree(IntervalNode root) {
        if (root == null) {
            return;
        }

        if (root.left != null) {
            printTree(root.left);
        }

        System.out.print("[" + root.i.start + "," + root.i.end + "]");

        if (root.right != null) {
            printTree(root.right);
        }
    }

    public int numOverlaps(IntervalNode node, Interval i) {
        if (node == null) {
            return 0;
        }

        int numOverlapping = 0;
        
        if (!((node.i.start > i.end) || (node.i.end < i.start))) {
            numOverlapping++;
        }

        if ((node.left != null) && (node.left.max >= i.start)) {
            numOverlapping += numOverlaps(node.left, i);
        }

        numOverlapping += numOverlaps(node.right, i);
        
        return numOverlapping;
    }
}