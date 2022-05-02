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

	private void printList(ListNode listNode) {
		if(listNode == ListNode.EMPTYLIST){

			sb.append("( )");
			return;
		}
	
		else if(listNode == ListNode.ENDLIST){	
			
			return;
		}
		
		//else if(listNode.car() instanceof ListNode) {	// ( ' ( ) ) 이런 형태일때
			
		//	ListNode newlist = (ListNode) listNode.car();
			
		//	if(newlist.car() instanceof QuoteNode) {
				
		//		while(listNode.cdr() != null){

		//			printNode(listNode.car());
		//			listNode = listNode.cdr();
		//		}
		//		return;
				
		//	}
			
		//	return;
			
		//}

		else if(listNode.car() instanceof QuoteNode) {	// `가 붙은 list일때 

			while(listNode.cdr() != null){

				printNode(listNode.car());
				listNode = listNode.cdr();
			}
			return;
		}
		
		else{
			sb.append("( ");
		
			while(listNode.cdr() != null){

				printNode(listNode.car());
				listNode = listNode.cdr();
			}
			
			sb.append(") ");
		}
	}
	
	private void printNode(Node node) {
		if (node == null) 
			return;
      
		if (node instanceof ListNode) {
			ListNode ln = (ListNode) node;
			printList(ln);
		} 
		else if (node instanceof QuoteNode) {
			sb.append(node);
		}
		else {
			sb.append("[" + node + "] ");
		}
         
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
