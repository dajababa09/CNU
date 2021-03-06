package PROJECT2;

public class CuteInterpreter {
	private final static BooleanNode TRUE_NODE = new BooleanNode();
	private final static BooleanNode FALSE_NODE = new BooleanNode();
	private static SymbolTable Symbol_Table = new SymbolTable();
	static {
		TRUE_NODE.value = true;
		FALSE_NODE.value = false;
	}

	private void errorLog(String err) {
		System.out.println(err);
	}

	enum CopyMode {
		NO_NEXT, NEXT
	}

	private Node runFunction(FunctionNode func) {
		Node rhs1 = func.getNext();
		Node rhs2 = (rhs1 != null) ? rhs1.getNext() : null;
		
		
		switch (func.value) {
		case CAR: {
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			if (!checkQuote(rhs1))
				errorLog("Syntax Error!");
			Node item = runQuote((ListNode) rhs1); // quote value 값
			// copyNode를 이용하여 값을 return 하시오.
			Node result = copyNode(((ListNode) item).value, CopyMode.NO_NEXT);
			return result;
		}
		case CDR: {
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			if (!checkQuote(rhs1))
				errorLog("Syntax Error!");
			Node item = runQuote((ListNode) rhs1);
			// copyNode를 이용하여 값을 return 하시오.
			ListNode result = new ListNode();
			result.value = copyNode(((ListNode) item).value.next, CopyMode.NEXT);
			return result;
		}
		case CONS: {
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			Node head = runExpr(rhs1); // 원소
			Node tail = runExpr(rhs2); // List
			// CONS에 맞게 작성할것
			ListNode result = new ListNode();
			result.value = head;
			result.value.next = ((ListNode) tail).value;
			return result;
		}
		case ATOM_Q:
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			if (!(rhs1 instanceof ListNode)
					|| (((ListNode) rhs1).value == null))
				return TRUE_NODE;
			else
				return FALSE_NODE;
		case EQ_Q:
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			if (rhs1 != null && rhs1.equals(rhs2)) {
				return TRUE_NODE;
			} else {
				return FALSE_NODE;
			}
		case NULL_Q:
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}			
			if (rhs1 instanceof ListNode && ((ListNode) rhs1).value == null)
				return TRUE_NODE;

			else
				return FALSE_NODE;
		case NOT:
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			if (((BooleanNode) rhs1).value == false) {
				return TRUE_NODE;
			} else {
				return FALSE_NODE;
			}
		case COND:
			if(rhs1 instanceof IdNode){
				rhs1 = Symbol_Table.lookupTable(((IdNode) rhs1).value);
			}
			if(rhs2 instanceof IdNode){
				rhs2 = Symbol_Table.lookupTable(((IdNode) rhs2).value);
			}
			Node tmp2 = rhs1;
			Node tmp1 = ((ListNode) rhs1).value;
			while (tmp2 != null) {
				tmp1 = ((ListNode) tmp2).value;
				if (tmp1 instanceof ListNode) {
					Node runResult = this.runList((ListNode) tmp1);
					if (runResult instanceof BooleanNode
							&& ((BooleanNode) runResult).value == true) {
						return tmp1.getNext();
					}
				} else {
					if (tmp1 instanceof BooleanNode
							&& ((BooleanNode) tmp1).value == true) {
						return tmp1.getNext();
					}
				}
				tmp2 = tmp2.getNext();
			}
		case DEFINE: //rhs1,rhs2
			ListNode tmp = new ListNode();
			if(rhs2 instanceof ListNode)
			{
				if(((ListNode) rhs2).value instanceof QuoteNode)
				{
				}
				else if (((FunctionNode)((ListNode) rhs2).value).value.name().equals("LAMBDA")){
					tmp.value = rhs2;
				}
				else{
					rhs2 = runExpr(rhs2);
				}
			}
			Symbol_Table.insertTable(((IdNode)rhs1).value, rhs2);
			break;
		case LAMBDA:
			Node result = null;
			if (rhs2.next == null){
				return func;
			}
			else{
				Symbol_Table.insertTable(((IdNode)((ListNode)rhs1).value).value, rhs2.next);
				result = runExpr(rhs2);
				Symbol_Table.deleteTable(((IdNode)((ListNode)rhs1).value).value);
				return result;
			}
			
		default:
			break;
		}
		return null;
	}

	private Node copyNode(Node node, CopyMode mode) { // node를 복사
		// mode에 따라서 next를 복사함
		if (node == null)
			return null;
		Node result = null;
		if (node instanceof BinarayOpNode)
			result = new BinarayOpNode();
		else if (node instanceof BooleanNode)
			result = new BooleanNode();
		else if (node instanceof FunctionNode)
			result = new FunctionNode();
		else if (node instanceof IdNode)
			result = new IdNode();
		else if (node instanceof IntNode)
			result = new IntNode();
		else if (node instanceof ListNode)
			result = new ListNode();
		result.copyValue(node);
		if (mode == CopyMode.NEXT && result != null)
			result.setNext(copyNode(node.getNext(), mode));
		return result;
	}

	private Node runList(ListNode list) {
		// list의 value가 QuoteNode일 경우
		if (list.value instanceof QuoteNode)
			return runQuote(list);
		Node opCode = list.value;
		if (opCode == null)
			return list;
		if (opCode instanceof FunctionNode){
			if(((FunctionNode) opCode).value.name().equals("LAMBDA") && opCode.next.next.next == null )
			{
				opCode.next.next.next = list.next;
			}
			return runFunction((FunctionNode) opCode);
		}
		if (opCode instanceof BinarayOpNode)
			return runBinary((BinarayOpNode) opCode);
		if (opCode instanceof IdNode){
			if(Symbol_Table.lookupTable(((IdNode) opCode).value) != null){
				opCode = Symbol_Table.lookupTable(((IdNode) opCode).value);
				((ListNode)opCode).value.next.next.next = list.value.next;
				return runList((ListNode) opCode);
			}
		}
		if (opCode instanceof ListNode)
		{
			return runList((ListNode)opCode);
		}
		return list;
	}

	private Node runQuote(ListNode node) {
		// QuoteNode의 value를 반환함
		QuoteNode qItem = (QuoteNode) node.value;
		Node item = qItem.value;
		return item;
	}

	public Node runExpr(Node rootExpr) {
		if (rootExpr == null)
			return null;
		if (rootExpr instanceof IdNode){
			return rootExpr;
		}
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

	private boolean checkQuote(Node node) {
		// QuoteNode의 형태인지 확인하는 메소드
		if (!(node instanceof ListNode))
			return false;
		ListNode tmp = (ListNode) node;
		if (!(tmp.value instanceof QuoteNode))
			return false;
		return true;
	}

	private Node runBinary(BinarayOpNode node) {
		Node result;
		Node left = node.getNext();
		Node right = left.getNext();
		if(left instanceof IdNode){
			left = Symbol_Table.lookupTable(((IdNode) left).value);
		}
		if(right instanceof IdNode){
			right = Symbol_Table.lookupTable(((IdNode) right).value);
		}
		left = runExpr(left);
		right = runExpr(right);
		if (left == null || right == null)
			errorLog("runBinary runExpr null");
		if (!(left instanceof IntNode) || !(right instanceof IntNode)) {
			errorLog("Type Error!");
			return null;
		}

		switch (node.value) {
		case MINUS:
			result = new IntNode();
			((IntNode) result).value = ((IntNode) left).value
					- ((IntNode) right).value;
			return result;
		case PLUS: // +
			result = new IntNode();
			((IntNode) result).value = ((IntNode) left).value
					+ ((IntNode) right).value;
			return result;
		case TIMES: // *
			result = new IntNode();
			((IntNode) result).value = ((IntNode) left).value
					* ((IntNode) right).value;
			return result;
		case DIV: // '/
			result = new IntNode();
			((IntNode) result).value = ((IntNode) left).value
					/ ((IntNode) right).value;
			return result;
		case LT: // <
			result = new BooleanNode();
			if (((IntNode) left).value < ((IntNode) right).value) {
				((BooleanNode) result).value = true;
			} else {
				((BooleanNode) result).value = false;
			}
			return result;
		case GT: // >
			result = new BooleanNode();
			if (((IntNode) left).value > ((IntNode) right).value) {
				((BooleanNode) result).value = true;
			} else {
				((BooleanNode) result).value = false;
			}
			return result;
		case EQ: // =
			result = new BooleanNode();
			if (((IntNode) left).value == ((IntNode) right).value) {
				((BooleanNode) result).value = true;
			} else {
				((BooleanNode) result).value = false;
			}
			return result;

			// 기타 연산자, MINUS와 비슷하게 각자 구현할 것 PLUS, TIMES, DIV, LT, GT, EQ
			// 관계연산은 TRUE, FALSE반환
		}
		return null;
	}
}