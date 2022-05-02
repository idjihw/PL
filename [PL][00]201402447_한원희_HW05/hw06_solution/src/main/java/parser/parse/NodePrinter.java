package parser.parse;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintStream;
import java.io.PrintWriter;

import parser.ast.*;

public class NodePrinter {
	private final String OUTPUT_FILENAME = "output06.txt";
	private StringBuffer sb = new StringBuffer();
	private Node root;

	public NodePrinter(Node root){
		this.root = root;
	}

	private void printList(Node head) {
		if (head == null) {
			sb.append("( )");
			return;
		}

		sb.append("(");
		printNode(head);
		sb.delete(sb.length()-1,sb.length());
		sb.append(")");
	}

	private void printNode(Node head) {
		if (head == null) 
			return;
      
		if (head instanceof ListNode) {
			ListNode ln = (ListNode) head;
			printList(ln.value);
		} else {
			sb.append("[" + head + "] ");
		}
         
    printNode(head.getNext());
	}
   
	public void prettyPrint(){
		printNode(root);

		try(FileWriter fw = new FileWriter(OUTPUT_FILENAME);
			PrintWriter pw = new PrintWriter(fw)){
			pw.write(sb.toString());
		}catch (IOException e){
			e.printStackTrace();
		}
	}
}
