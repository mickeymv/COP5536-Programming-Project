/**
 * 
 * @author Mickey Vellukunnel
 * 
 *         Implement an event counter using Red-Black tree.
 */
public class RedBlackTree {

	TreeNode root;

	private static final boolean RED = true;
	private static final boolean BLACK = false;

	private class TreeNode {
		int key; // the ID.
		int count; // number of active events with the given ID.
		int subtreeCount; // count of number of treeNodes in the subtree rooted
							// at this node. minimum = 1 (the node itself)
		TreeNode parent, leftChild, rightChild;
		boolean isRed; // Also the color of the node. By default (by using the
						// constructor) it's RED (true)

		TreeNode(int key, int count) {
			this.key = key;
			this.count = count;
			this.subtreeCount = 1;
			this.isRed = RED; // true
		}
	}

	/*
	 * Binary search tree insert.
	 */
	void insert(int key, int count) {
		TreeNode newNode = new TreeNode(key, count);
		if (root != null) {
			TreeNode parent = null, tempNode = root;
			while (tempNode != null) {
				parent = tempNode;
				if (key < tempNode.key) {
					tempNode = tempNode.leftChild;
				} else {
					tempNode = tempNode.rightChild;
				}
			}
			if (key <= parent.key) {
				parent.leftChild = newNode;
			} else {
				parent.rightChild = newNode;
			}
			newNode.parent = parent;
		} else {
			root = newNode;
		}
		insert1(newNode);
	}

	/*
	 * Binary search tree delete after finding the node with the given key.
	 */
	void delete(int key) {
		if (root != null) {
			// First find the node to delete
			TreeNode node = root;
			while (node != null && node.key != key) {
				if (key < node.key) {
					node = node.leftChild;
				} else {
					node = node.rightChild;
				}
			}
			if (node != null) { // node isn't null implies we've found the node
								// to delete.
				deleteNode(node);
			}
		}
	}

	/*
	 * deletes the given node.
	 */
	void deleteNode(TreeNode node) {
		if (node != null) {
			if (node.leftChild == null && node.rightChild == null) {
				// CASE 1: No children: if there are no children, delete the
				// node directly.
				deleteNodeReferences(node);
			} else if (node.leftChild != null && node.rightChild != null) {
				// CASE 2: 2 children: If the node has two children replace
				// node with its predecessor, and delete the predecessor
				// recursively.
				TreeNode predecessor = predecessor(node);
				replaceNode(node, predecessor);
				deleteNode(predecessor);
			} else {
				// CASE 3: Deletion of node with one child. call
				// delete of red black tree IF the node being deleted is a black
				// node.
				// (if it's red, then no RBT properties are violated)
				boolean moreFixesRequired = false;
				TreeNode child = null;
				if (node.isRed == BLACK) {
					moreFixesRequired = !deleteFix1(node);
				}
				if (node.rightChild != null) {
					child = node.rightChild;
					// CASE 3.1: 1 child: if the node only has rightChild,
					// replace
					// node's parent
					// link to its child.
					if (node.parent == null) {
						root = node.rightChild;
						moreFixesRequired = false;
					} else if (node.parent.rightChild == node) {
						node.parent.rightChild = node.rightChild;
					} else {
						node.parent.leftChild = node.rightChild;
					}
				} else {
					// CASE 3.2: 2 child: if the node only has leftChild,
					// replace
					// node's parent
					// link to its child.
					if (node.parent == null) {
						root = node.leftChild;
						moreFixesRequired = false;
					} else if (node.parent.rightChild == node) {
						node.parent.rightChild = node.leftChild;
					} else {
						node.parent.leftChild = node.leftChild;
					}
				}
				if (moreFixesRequired) {
					// If child replacing deleted node was previously black, and
					// not the current root
					delete2(child);
				}
			}
		}
	}

	/*
	 * Case 1: N is the new root. In this case, we are done. We removed one
	 * black node from every path, and the new root is black, so the properties
	 * are preserved.
	 */
	void delete1(TreeNode nodeN) {
		if (nodeN.parent != null) {
			delete2(nodeN);
		} else {
			root = nodeN;
		}
	}

	/*
	 * If child replacing deleted node was previously black, and not the current
	 * root. Case 2: S is red. In this case we reverse the colors of P and S,
	 * and then rotate left at P, turning S into N's grandparent. Note that P
	 * has to be black as it had a red child. The resulting subtree has a path
	 * short one black node so we are not done. Now N has a black sibling and a
	 * red parent, so we can proceed to step 4, 5, or 6. (Its new sibling is
	 * black because it was once the child of the red S.) In later cases, we
	 * will relabel N's new sibling as S.
	 */
	void delete2(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeS.isRed == RED) {
			nodeN.parent.isRed = RED;
			nodeS.isRed = BLACK;
			if (nodeN == nodeN.parent.leftChild) {
				leftRotate(nodeN.parent);
			} else {
				rightRotate(nodeN.parent);
			}
		}
		delete3(nodeN);
	}

	/*
	 * Case 3: P, S, and S's children are black. In this case, we simply repaint
	 * S red. The result is that all paths passing through S, which are
	 * precisely those paths not passing through N, have one less black node.
	 * Because deleting N's original parent made all paths passing through N
	 * have one less black node, this evens things up. However, all paths
	 * through P now have one fewer black node than paths that do not pass
	 * through P, so property 5 (all paths from any given node to its leaf nodes
	 * contain the same number of black nodes) is still violated. To correct
	 * this, we perform the rebalancing procedure on P, starting at case 1.
	 */
	void delete3(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeN.parent.isRed == BLACK && nodeS.isRed == BLACK && nodeS.leftChild.isRed == BLACK
				&& nodeS.rightChild.isRed == BLACK) {
			nodeS.isRed = RED;
			delete1(nodeN.parent);
		} else {
			delete4(nodeN);
		}
	}

	/*
	 * Case 4: S and S's children are black, but P is red. In this case, we
	 * simply exchange the colors of S and P. This does not affect the number of
	 * black nodes on paths going through S, but it does add one to the number
	 * of black nodes on paths going through N, making up for the deleted black
	 * node on those paths.
	 */
	void delete4(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeN.parent.isRed == RED && nodeS.isRed == BLACK && nodeS.leftChild.isRed == BLACK
				&& nodeS.rightChild.isRed == BLACK) {
			nodeS.isRed = RED;
			nodeN.parent.isRed = BLACK;
		} else {
			delete5(nodeN);
		}
	}

	/*
	 * Case 5: S is black, S's left child is red, S's right child is black, and
	 * N is the left child of its parent. In this case we rotate right at S, so
	 * that S's left child becomes S's parent and N's new sibling. We then
	 * exchange the colors of S and its new parent. All paths still have the
	 * same number of black nodes, but now N has a black sibling whose right
	 * child is red, so we fall into case 6. Neither N nor its parent are
	 * affected by this transformation. (Again, for case 6, we relabel N's new
	 * sibling as S.)
	 */
	void delete5(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);
		if (nodeS.isRed == BLACK) {
			/*
			 * this if statement is trivial, due to case 2 (even though case 2
			 * changed the sibling to a sibling's child, the sibling's child
			 * can't be red, since no red parent can have a red child).
			 */
			/*
			 * the following statements just force the red to be on the left of
			 * the left of the parent, or right of the right, so case six will
			 * rotate correctly.
			 */
			if (nodeN == nodeN.parent.leftChild && nodeS.rightChild.isRed == BLACK && nodeS.leftChild.isRed == RED) {
				/* this last test is trivial too due to cases 2-4. */
				nodeS.isRed = RED;
				nodeS.leftChild.isRed = BLACK;
				rightRotate(nodeS);
			} else if (nodeN == nodeN.parent.rightChild && nodeS.leftChild.isRed == BLACK
					&& nodeS.rightChild.isRed == RED) {
				/* this last test is trivial too due to cases 2-4. */
				nodeS.isRed = RED;
				nodeS.rightChild.isRed = BLACK;
				leftRotate(nodeS);
			}
		}
		delete6(nodeN);
	}

	/*
	 * Case 6: S is black, S's right child is red, and N is the left child of
	 * its parent P. In this case we rotate left at P, so that S becomes the
	 * parent of P and S's right child. We then exchange the colors of P and S,
	 * and make S's right child black. The subtree still has the same color at
	 * its root, so Properties 4 (Both children of every red node are black) and
	 * 5 (All paths from any given node to its leaf nodes contain the same
	 * number of black nodes) are not violated. However, N now has one
	 * additional black ancestor: either P has become black, or it was black and
	 * S was added as a black grandparent. Thus, the paths passing through N
	 * pass through one additional black node.
	 * 
	 * Meanwhile, if a path does not go through N, then there are two
	 * possibilities:
	 * 
	 * It goes through N's new sibling SL, a node with arbitrary color and the
	 * root of the subtree labeled 3 (s. diagram). Then, it must go through S
	 * and P, both formerly and currently, as they have only exchanged colors
	 * and places. Thus the path contains the same number of black nodes. It
	 * goes through N's new uncle, S's right child. Then, it formerly went
	 * through S, S's parent, and S's right child SR (which was red), but now
	 * only goes through S, which has assumed the color of its former parent,
	 * and S's right child, which has changed from red to black (assuming S's
	 * color: black). The net effect is that this path goes through the same
	 * number of black nodes. Either way, the number of black nodes on these
	 * paths does not change. Thus, we have restored Properties 4 (Both children
	 * of every red node are black) and 5 (All paths from any given node to its
	 * leaf nodes contain the same number of black nodes).
	 */
	void delete6(TreeNode nodeN) {
		TreeNode nodeS = sibling(nodeN);

		nodeS.isRed = nodeN.parent.isRed;
		nodeN.parent.isRed = BLACK;

		if (nodeN == nodeN.parent.leftChild) {
			nodeS.rightChild.isRed = BLACK;
			leftRotate(nodeN.parent);
		} else {
			nodeS.leftChild.isRed = BLACK;
			rightRotate(nodeN.parent);
		}
	}

	/*
	 * If the node to be deleted is black with ONE child, and the child is red,
	 * simply repaint the child black.
	 */
	boolean deleteFix1(TreeNode node) {
		if (node.isRed == BLACK && node.rightChild != null && node.rightChild.isRed == RED) {
			node.rightChild.isRed = BLACK;
			return true;
		} else if (node.isRed == BLACK && node.leftChild != null && node.leftChild.isRed == RED) {
			node.leftChild.isRed = BLACK;
			return true;
		}
		return false;
	}

	/*
	 * "Deletes" a node by removing all references to it and setting the parent
	 * reference to null;
	 */
	void deleteNodeReferences(TreeNode node) {
		if (node != null) {
			if (node == root) {
				root = null;
			} else {
				if (node.parent.leftChild == node) {
					node.parent.leftChild = null;
				} else {
					node.parent.rightChild = null;
				}
			}
		}
	}

	/*
	 * Copy all the contents of one node to another.
	 */
	void replaceNode(TreeNode replaceeNode, TreeNode replacerNode) {
		replaceeNode.key = replacerNode.key;
		replaceeNode.count = replacerNode.count;
	}

	// /*
	// * Returns the successor of the node, i.e. the left-most child in it's
	// right
	// * subtree.
	// */
	// TreeNode successor(TreeNode node) {
	// TreeNode successor = null;
	// if (node != null) {
	// successor = node.rightChild;
	// while (successor != null && successor.leftChild != null) {
	// successor = successor.leftChild;
	// }
	// }
	// return successor;
	// }

	/*
	 * Returns the predecessor of the node, i.e. the right-most child in it's
	 * left subtree.
	 */
	TreeNode predecessor(TreeNode node) {
		TreeNode predecessor = null;
		if (node != null) {
			predecessor = node.leftChild;
			while (predecessor != null && predecessor.rightChild != null) {
				predecessor = predecessor.rightChild;
			}
		}
		return predecessor;
	}

	TreeNode grandparent(TreeNode node) {
		if (node != null && node.parent != null && node.parent.parent != null) {
			return node.parent.parent;
		} else {
			return null;
		}
	}

	TreeNode uncle(TreeNode node) {
		if (node != null && node.parent != null && node.parent.parent != null) {
			if (node.parent == node.parent.parent.rightChild) {
				return node.parent.parent.leftChild;
			} else {
				return node.parent.parent.rightChild;
			}
		} else {
			return null;
		}
	}

	TreeNode sibling(TreeNode node) {
		if (node != null && node.parent != null) {
			if (node == node.parent.rightChild) {
				return node.parent.leftChild;
			} else {
				return node.parent.rightChild;
			}
		} else {
			return null;
		}
	}

	/*
	 * Case 1 of red-black tree insertion, node inserted is the first node, if
	 * so make it black.
	 */
	void insert1(TreeNode node) {
		System.out.println("\nInside insert1 inserting " + node.key);
		if (node != null) {
			if (node.parent == null) {
				node.isRed = BLACK;
			} else {
				insert2(node);
			}
		}
	}

	/*
	 * Case 2 of red-black tree insertion, parent is black, do nothing.
	 */
	void insert2(TreeNode node) {
		System.out.println("\nInside insert2 inserting " + node.key);
		if (node.parent.isRed == BLACK) {
			return;
		} else {
			insert3(node);
		}
	}

	/*
	 * Case 3 of red-black tree insertion, (parent is red and) uncle is also
	 * red. Then change parent and uncle to black, grandparent to red and
	 * recurse on grandparent.
	 */
	void insert3(TreeNode node) {
		System.out.println("\nInside insert3 inserting " + node.key);
		TreeNode uncle = uncle(node);
		if (uncle != null && uncle.isRed == RED) {
			node.parent.isRed = BLACK;
			uncle.isRed = BLACK;
			TreeNode grandparent = grandparent(node);
			grandparent.isRed = RED;
			insert1(grandparent);
		} else {
			insert4(node);
		}
	}

	/*
	 * Case 4 of red-black tree insertion, (parent is red, uncle is black)
	 * newNode is an inside child of grandparent, i.e. newNode is either the
	 * right child of grandparent's leftChild, or newNode is the left child of
	 * grandparent's rightChild, then rotate left / right respectively.
	 */
	void insert4(TreeNode node) {
		System.out.println("\nInside insert4 inserting " + node.key);
		TreeNode grandparent = grandparent(node);
		TreeNode parent = node.parent;
		if (grandparent.leftChild == parent && parent.rightChild == node) {
			// left-rotate
			parent.rightChild = node.leftChild;
			parent.parent = node;
			node.leftChild = parent;
			node.parent = grandparent;
			grandparent.leftChild = node;
			node = node.leftChild;
		} else if (grandparent.rightChild == parent && parent.leftChild == node) {
			// right-rotate
			parent.leftChild = node.rightChild;
			parent.parent = node;
			node.rightChild = parent;
			node.parent = grandparent;
			grandparent.rightChild = node;
			node = node.rightChild;
		}
		insert5(node);
	}

	/*
	 * Case 5 of red-black tree insertion, newNode is an outside child of
	 * grandparent, newNode is either the right child of grandparent's
	 * rightChild, or newNode is the left child of grandparent's leftChild, then
	 * rotate right / left respectively, then paint .
	 */
	void insert5(TreeNode node) {
		System.out.println("\nInside insert5 inserting " + node.key);
		TreeNode grandparent = grandparent(node);
		TreeNode parent = node.parent;
		parent.isRed = BLACK;
		grandparent.isRed = RED;
		if (parent.rightChild == node) {
			leftRotate(grandparent);
		} else {
			rightRotate(grandparent);
		}
	}

	void leftRotate(TreeNode node) {
		if (node != null && node.rightChild != null) {
			TreeNode rightChild = node.rightChild, grandparent = node.parent;
			node.rightChild = rightChild.leftChild;
			node.parent = rightChild;
			rightChild.leftChild = node;
			rightChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = rightChild;
				} else {
					grandparent.rightChild = rightChild;
				}
			}
		}
	}

	void rightRotate(TreeNode node) {
		if (node != null && node.leftChild != null) {
			TreeNode leftChild = node.leftChild, grandparent = node.parent;
			node.leftChild = leftChild.rightChild;
			node.parent = leftChild;
			leftChild.rightChild = node;
			leftChild.parent = grandparent;
			if (grandparent != null) {
				if (node == grandparent.leftChild) {
					grandparent.leftChild = leftChild;
				} else {
					grandparent.rightChild = leftChild;
				}
			}
		}
	}

	void printTree() {
		System.out.println("\n");
		recursivelyPrintTree(root, "");
		System.out.println("\n");
	}

	/*
	 * Print the nodes of the tree in an in-order looking (left child towards
	 * the bottom and right child towards the top) fashion.
	 */
	private void recursivelyPrintTree(TreeNode node, String indentDots) {
		if (node != null) {
			recursivelyPrintTree(node.rightChild, indentDots + ".");
			System.out.println(indentDots + node.key + " " + node.isRed + "\n");
			// System.out.println(indentDots + node.key + "\n");
			recursivelyPrintTree(node.leftChild, indentDots + ".");
		}
	}

	public static void main(String[] args) {
		int[] list = { 60, 20, 75, 10, 85, 100, 80, 35, 5, 18, 2, 4, 3, 64, 105, 46, 29, 61 };
		RedBlackTree tree = new RedBlackTree();
		for (int i : list) {
			tree.insert(i, 1);
			// System.out.println("\nThe tree after insertion of " + i);
			// tree.printTree();
		}
		System.out.println("\nThe tree after insertions");
		tree.printTree();
		int[] delList = { 60, 20, 105, 85, 80, 10 };
		for (int i : delList) {
			tree.delete(i);
			System.out.println("\nThe tree after deletion of " + i);
			tree.printTree();
		}
		System.out.println("\nThe tree after deletions");
		tree.printTree();
	}
}
