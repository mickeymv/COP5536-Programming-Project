import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class bbst {

	public static void main(String[] args) {
		RedBlackTree tree = new RedBlackTree();
		if (0 < args.length) {

			String inputFileName = args[0];
			File nodesInputFile = new File(inputFileName);
			try {
				FileReader inputFil = new FileReader(nodesInputFile);
				BufferedReader in = new BufferedReader(inputFil);

				String s = in.readLine();

				int nodesCount = Integer.parseInt(s);
				RedBlackTree.TreeNode[] sortedNodesArray = new RedBlackTree.TreeNode[nodesCount];
				s = in.readLine();

				for (int i = 0; i < nodesCount; i++) {
					String nums[] = s.split(" ");
					int nodeID = Integer.parseInt(nums[0]);
					int nodeCount = Integer.parseInt(nums[1]);
					RedBlackTree.TreeNode node = tree.new TreeNode(nodeID, nodeCount);
					node.isRed = false;
					sortedNodesArray[i] = node;
					s = in.readLine();
				}

				tree.sortedArrayToRedBlackTree(sortedNodesArray, nodesCount);

				// create a scanner so we can read the command-line input
				Scanner scanner = new Scanner(System.in);
				s = scanner.nextLine();
				while (!"quit".equals(s)) {
					String commands[] = s.split(" ");
					String command = commands[0];

					switch (command) {
					case "increase":
						tree.increase(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "reduce":
						tree.reduce(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "count":
						tree.count(Integer.parseInt(commands[1]));
						break;
					case "inrange":
						tree.inRange(Integer.parseInt(commands[1]), Integer.parseInt(commands[2]));
						break;
					case "next":
						tree.next(Integer.parseInt(commands[1]), true);
						break;
					case "previous":
						tree.previous(Integer.parseInt(commands[1]), true);
						break;
					default:
						System.out.println("\nInvalid command: '" + command + "' ! Enter 'quit' to exit. ");
						break;
					}
					s = scanner.nextLine();
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		} else {
			File nodesInputFile = new File(System.getProperty("user.dir") + "/src/test_100.txt");
			try {
				FileReader inputFil = new FileReader(nodesInputFile);
				BufferedReader in = new BufferedReader(inputFil);

				String s = in.readLine();

				int nodesCount = Integer.parseInt(s);
				RedBlackTree.TreeNode[] sortedNodesArray = new RedBlackTree.TreeNode[nodesCount];
				s = in.readLine();

				for (int i = 0; i < nodesCount; i++) {
					String nums[] = s.split(" ");
					int nodeID = Integer.parseInt(nums[0]);
					int nodeCount = Integer.parseInt(nums[1]);
					RedBlackTree.TreeNode node = tree.new TreeNode(nodeID, nodeCount);
					node.isRed = false;
					sortedNodesArray[i] = node;
					s = in.readLine();
				}

				tree.sortedArrayToRedBlackTree(sortedNodesArray, nodesCount);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			tree.increase(350, 100);
			tree.reduce(350, 50);
			tree.count(350);
			tree.inRange(300, 1000);
			tree.inRange(200, 299);
			tree.inRange(200, 1000);
			tree.inRange(300, 349);
			tree.inRange(350, 350);
			tree.inRange(349, 350);
			tree.next(300, true);
			tree.next(349, true);
			tree.next(360, true);
			tree.previous(360, true);
			tree.previous(350, true);
			tree.previous(0, true);
			tree.reduce(271, 6);
			tree.previous(350, true);
			tree.reduce(271, 3);
			tree.previous(350, true);
			tree.previous(150, true);
		}
	}

}