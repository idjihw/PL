package interpreter;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Scanner;

import parser.ast.BinaryOpNode;
import parser.ast.BooleanNode;
import parser.ast.FunctionNode;
import parser.ast.IdNode;
import parser.ast.IntNode;
import parser.ast.ListNode;
import parser.ast.Node;
import parser.ast.QuoteNode;
import parser.parse.CuteParser;
import parser.parse.NodePrinter;
import parser.parse.ParserMain;

public class CuteInterpreter {
	public static void main(String[] args) throws IOException {
		
		String str = "";
		int cur = 0;
		Scanner data = new Scanner(System.in);
		
		while(true) {
			
			System.out.print("> ");
			str = data.nextLine();
	
			ClassLoader cloader = ParserMain.class.getClassLoader();
			File file = new File(cloader.getResource("interpreter/as07.txt").getFile());
			FileWriter fw = new FileWriter(file);
			
			fw.write(str);
			fw.close();
			
			CuteParser cuteParser = new CuteParser(file);
			CuteInterpreter interpreter = new CuteInterpreter();
			
			Node parseTree = cuteParser.parseExpr();
			Node resultNode = interpreter.runExpr(parseTree);
			
			NodePrinter nodePrinter = new NodePrinter(resultNode);
			nodePrinter.prettyPrint();			
						
			FileReader fr = new FileReader("output07.txt");
			System.out.print("... ");
			while((cur = fr.read()) != -1){
				System.out.print((char)cur + " ");
			}
			System.out.println("");

			fr.close();
			
		}
		
	}
	
	private void errorLog(String err) {
		System.out.println(err);
	}
	
	public Node runExpr(Node rootExpr) {
		
		if (rootExpr == null)
			return null;
		if (rootExpr instanceof IdNode)
			return rootExpr;
		else if (rootExpr instanceof IntNode)
			return rootExpr;
		else if (rootExpr instanceof BooleanNode)
			return rootExpr;
		else if (rootExpr instanceof ListNode)
			return runList((ListNode) rootExpr);
		else
			errorLog("run Expr error");
		
		return null;
	}
	
	private Node runList(ListNode list) {
		list = (ListNode)stripList(list);
		
		if (list.equals(ListNode.EMPTYLIST))
			return list;
		
		if (list.car() instanceof FunctionNode) {
			//Quote 내부의 리스트의 경우 계산하지 않음
			if (list.cdr().car() instanceof QuoteNode)
				return list;
			else 
				return runFunction((FunctionNode) list.car(), list.cdr());
		}
		
		if (list.car() instanceof BinaryOpNode) {
			//Quote 내부의 리스트의 경우 계산하지 않음
			if (list.cdr().car() instanceof QuoteNode)
				return list;
			else
				return runBinary(list);
		}
		
		return list;
	}
	
	private Node runFunction(FunctionNode operator, ListNode operand) {
		
		switch (operator.funcType) {
		 // CAR, CDR, CONS, NULL_Q…등 모든 function node에 대한 동작 구현
			case CAR:
				// quoteNode일 경우 그 다음 노드를 출력
				if(operand.car() instanceof ListNode) {
					ListNode Li = (ListNode) operand.car();
					if(Li.car() instanceof QuoteNode) {
						ListNode Li2 = (ListNode) Li.cdr().car();
						return Li2.car();
					}
				}
				else  {
					return ((ListNode)runExpr(operand)).car();
				}
				
			case CDR:
				// quoteNode의 다음을 출력
				if(operand.cdr() instanceof ListNode) {
					ListNode Li = (ListNode) operand.car();
					if(Li.car() instanceof QuoteNode) {
						ListNode Li2 = (ListNode) Li.cdr().car();
						return Li2.cdr();
					}
				}
				else  {
					return ((ListNode)runExpr(operand)).cdr();
				}
				
			case CONS:
				
				Node head = operand.car();
				Node tail = operand.cdr().car();
				
				// quoteNode를 만나면 그 다음 노드를 head로
				if(head instanceof ListNode) {
					if(((ListNode)head).car() instanceof QuoteNode) {
						head = ((ListNode)head).cdr().car();
					}
					else {
						head = runExpr(head);
					}
				}
				
				// quoteNode를 만나면 그 다음 노드를 tail로
				if(tail instanceof ListNode) {
					if(((ListNode)tail).car() instanceof QuoteNode) {
						tail = ((ListNode) tail).cdr().car();
					}
					else {
						tail = runExpr(tail);
					}
				}
				
				return ListNode.cons(head, (ListNode)tail);
				
			case NULL_Q:
				if(operand.car() == null)
                    return BooleanNode.TRUE_NODE;
				
				else if(operand.car() instanceof ListNode) {
						ListNode Li = (ListNode) operand.car();
						if(Li.car() instanceof QuoteNode) {
							ListNode Li2 = (ListNode) Li.cdr().car();
							if(Li2.car() == null)
								return BooleanNode.TRUE_NODE;
							else
								return BooleanNode.FALSE_NODE;
						}
				}
				
				else 
                    return BooleanNode.FALSE_NODE;
				
			case ATOM_Q:
				if(operand.car() instanceof ListNode) {
					ListNode Li = (ListNode) operand.car();
					if(Li.car() instanceof QuoteNode) {
						if(Li.cdr().car() instanceof ListNode) {
							ListNode Li2 = (ListNode) Li.cdr().car();
							if(Li2.car() == null) 
								return BooleanNode.TRUE_NODE;
							else
								return BooleanNode.FALSE_NODE;
						}
						else
							return BooleanNode.FALSE_NODE;
						
					} 
					
				}
				else 
					return BooleanNode.TRUE_NODE;
				
			case EQ_Q:
				int empty = 0;
				
				// 첫번째 리스트가 null인지
				if(operand.car() instanceof ListNode) {
					ListNode Li = (ListNode) operand.car();
					if(Li.car() instanceof QuoteNode) {
						if(Li.cdr().car() instanceof ListNode) {
							ListNode Li2 = (ListNode) Li.cdr().car();
							if(Li2.car() == null) {
								empty += 1;			
							}
						}
					}
				}
				
				// 두번째 리스트가 null인지
				if(operand.cdr().car() instanceof ListNode) {
					ListNode Li = (ListNode) operand.cdr().car();
					if(Li.car() instanceof QuoteNode) {
						if(Li.cdr().car() instanceof ListNode) {
							ListNode Li2 = (ListNode) Li.cdr().car();
							if(Li2.car() == null) {
								empty += 1;			
							}
						}
					}
				}
				
				// 두개의 비교되는 리스트가 둘다 null 이라면
				if(empty == 2) {
					return BooleanNode.TRUE_NODE;
				}
				
				ListNode one = (ListNode) operand.car();
				ListNode two = (ListNode) operand.cdr().car();
				
				// ListNode가 아닐때 검사해줍니다
				if(one.car() instanceof QuoteNode && two.car() instanceof QuoteNode) {
					if(!(one.cdr().car() instanceof ListNode) && !(two.cdr().car() instanceof ListNode)) {
						if(one.cdr().car().toString().equals(two.cdr().car().toString())) {
							return BooleanNode.TRUE_NODE;
						}
					}
				}
				
		 		return BooleanNode.FALSE_NODE;
		 	
			case NOT:
				if(operand.car() == BooleanNode.TRUE_NODE)
					return BooleanNode.FALSE_NODE;
				else if(operand.car() == BooleanNode.FALSE_NODE)
					return BooleanNode.TRUE_NODE;
			
				return BooleanNode.FALSE_NODE;
				
			case COND:
				if(runExpr(((ListNode)operand.car()).car()) == BooleanNode.TRUE_NODE)
					return runExpr(((ListNode)operand.car()).cdr().car());
				
				if(runExpr(((ListNode)operand.cdr().car()).car()) == BooleanNode.TRUE_NODE)
					return runExpr(((ListNode)operand.cdr().car()).cdr().car());
				
				return null;
				
				
			default:
				break;
				
				
		}
		
		return null;
	}
	
	private Node stripList(ListNode node) {
		if (node.car() instanceof ListNode && node.cdr().car() == null) {
			Node listNode = node.car();
			return listNode;
		} else {
			return node;
		}
	
	}
	private Node runBinary(ListNode list) {
			 
		BinaryOpNode operator = (BinaryOpNode) list.car();
		ListNode operand = list.cdr();

        Node x = operand.car();
        Node y = operand.cdr().car();    
        x = runExpr(x);
		y = runExpr(y);
		
        // 노드들의 value를 받아옴
        int var_x = ((IntNode) x).getValue();
        int var_y = ((IntNode) y).getValue();
        
		// 구현과정에서 필요한 변수 및 함수 작업 가능
		switch (operator.binType) {
		// +, -, *, = 등 모든 binary node에 대한 동작 구현
		 // +,-,/ 등에 대한 바이너리 연산 동작 구현
			case PLUS:
				return new IntNode(String.valueOf(var_x + var_y));
			case MINUS:
				return new IntNode(String.valueOf(var_x - var_y));
			case TIMES:
				return new IntNode(String.valueOf(var_x * var_y));
			case DIV:
				return new IntNode(String.valueOf(var_x / var_y));
			case LT:
				if(var_x < var_y)
					return BooleanNode.TRUE_NODE;
	            else
	            	return BooleanNode.FALSE_NODE;
			case GT:
				if(var_x > var_y)
					return BooleanNode.TRUE_NODE;
				else
					return BooleanNode.FALSE_NODE;
			case EQ:
				if(var_x == var_y)
					return BooleanNode.TRUE_NODE;
				else
					return BooleanNode.FALSE_NODE;
			default:
				break;
		}
		return null;
	}
}

