import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
	int next_token;
	String state;
	String token_string;
	ArrayList<String> code;
	HashMap<String, String> simbolTable; //simbolTable (str)���� �� : (str)���� �� 
	
	LexicalAnalyzer(ArrayList<String> input){
		this.code = input;
	}
	public void PROGRAMS() {
		simbolTable = new HashMap<String, String>();
		STATEMENTS();
		System.out.print("Result ==> ");
		for(Entry<String, String> e : simbolTable.entrySet()) {
			String valName = e.getKey();
			String value = e.getValue();
			System.out.print(String.format("%s: %s; ", valName, value));
		}
	}
	public void STATEMENTS() {
		ArrayList<Integer> token_num = new ArrayList<Integer>();
		ArrayList<String> token_str = new ArrayList<String>();
		ArrayList<String> code_str = new ArrayList<String>();
		for (String c : this.code) {
			lexical(c); // �ڵ带 ���� �������� �� �ܾ� �� �ܾ �о
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			code_str.add(c);
			if(this.token_string.equals("SEMI_COLON")) {
				for(String code : code_str) {
					System.out.print(code + " ");
				}
				System.out.println("");
				token_num.remove(token_num.size() -1); // �ǵ� �����ݷ� ����� 
				code_str.remove(code_str.size() -1);
				token_str.remove(token_str.size() -1);
				this.state = "(OK)";
				STATEMENT(token_num, code_str);
				System.out.println(this.state);
				token_num.clear();
				token_str.clear();
				code_str.clear();
			}
		}
		for(String code : code_str) {
			System.out.print(code + " ");
		}
		System.out.println("");
		this.state = "(OK)";
		STATEMENT(token_num, code_str);
		System.out.println(this.state);
	}
	public void STATEMENT(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		int idNum=0; int constNum=0; int opNum=0;
		for (Integer token : token_num) {
			if(token.equals(2)) {//id
				idNum += 1;
			}else if(token.equals(1)) {
				constNum += 1;
			}else if(token.equals(21) || token.equals(22) || token.equals(23) || token.equals(24)) {
				opNum += 1;
			}
		}
		System.out.println(String.format("ID: %d; CONST: %d; OP: %d;", idNum, constNum, opNum));
		try {
			if(token_num.get(0) == 2 && token_num.get(1) == 11) {
				String valName = code_str.get(0);
				token_num.remove(0);
				token_num.remove(0);
				code_str.remove(0);
				code_str.remove(0);
				if(token_num.size() >= 1) {
					String val = EXPRESSION(token_num, code_str);
					simbolTable.put(valName, val);
					if(val.equals("Unknown")) {
						if(simbolTable.containsValue("newUnknown")) {//newUnknown �� ������ Unknown ������ �ٲ��� �ϴ� �Ҵ� �ȰŴϱ�
							for(String sTval : simbolTable.keySet()) {
								if(simbolTable.get(sTval).equals("newUnknown")) {
									simbolTable.put(sTval, "Unknown");
								}
							}
							this.state = "(ERROR) ���ǵ��� ���� ������ ���Ǿ����ϴ�.";
						}
					}
				}else{
					//System.out.println("statement a= �ڿ� �����Ҳ� ����");
					this.state = "(ERROR) ���� ������ �ڿ� �� �����ڰ� �����ϴ�.";
				}
			}else if(token_num.get(0)==1 && token_num.get(1)==11) {
				this.state = "(ERROR) ����� ���� �����Ϸ��� �ϰ� �ֽ��ϴ�.";
			}else {
				//System.out.println("statement ���� op�� ���Կ����ڰ� ����");
				this.state = "(ERROR) ���� �Ǵ� ���� �����ڰ� �����ϴ�.";
			}
		}catch (IndexOutOfBoundsException e) {
			//System.out.println("statement token_num.get ����");
			this.state = "(ERROR) ���� ���� ��ü�� �߸��Ǿ����ϴ�.";
		}
	}
	public String EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		if(token_num.contains(21)) {// + �����ڷ� term�� term_tail ����
			int index = token_num.indexOf(21);
			String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			return validVal("+", val1, val2);
		}else if(token_num.contains(22)) {// - �����ڷ� term�� term_tail ����
			int index = token_num.indexOf(22);
			String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			return validVal("-", val1, val2);
		}else {//term_tail�� �۽Ƿ��� ���
			return TERM(token_num, code_str);
		}
	}
	public String TERM(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		if(token_num.contains(31)) {// ( �� �ִ� ���
			int leftidx = token_num.indexOf(31);
			int righidx = token_num.indexOf(32);
			return EXPRESSION(splitInt(token_num, leftidx+1, righidx), splitStr(code_str, leftidx+1, righidx));
		}else {// ��ȣ�� ���� ���ٸ� *, / �� factor�� factor_tail�� �����Ѵ�.
			if(token_num.contains(23)) {
				int index = token_num.indexOf(23);
				String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				return validVal("*", val1, val2);
			}else if(token_num.contains(24)) {
				int index = token_num.indexOf(24);
				String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				return validVal("/", val1, val2);
			}else {
				return FACTOR(token_num, code_str);
			}
		}
	}
	public String FACTOR(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		if(token_num.get(0)==2) {// ident
			if(simbolTable.containsKey(code_str.get(0))) {
				return simbolTable.get(code_str.get(0));
			}else { // a=10 ó�� ���̿� �Ҵ� ������ ���� �������� ��� �����޽����� ���� newUnknown�̶�� ���� �Ҵ��� ����(�����޽��� ��� ���Ŀ��� Unknown����)
				simbolTable.put(code_str.get(0), "newUnknown");
				return "Unknown";
			}
		}else if(token_num.get(0)==1) {// const
			return code_str.get(0);
		}
		/**
		else if(token_num.get(0) == 31) {
			//��ȣ�� term�ʿ��� �ذ��ؼ� factor ���� �ʿ� ������
		}**/
		else {
			this.state = "(ERROR) �ǿ����ڰ� �����ϴ�.";
			return "ERROR";
		}
	}
	public String TERM_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		try {
			if(token_num.get(0) == 21 || token_num.get(0) == 22) {
				token_num.remove(0);
				code_str.remove(0);
				while(true) {
					if(token_num.get(0)==23||token_num.get(0)==24||token_num.get(0)==21||token_num.get(0)==22) {
						int oper = token_num.get(0);
						String op;
						if(oper==23) {
							op = "*";
						}else if(oper==24) {
							op = "/";
						}else if(oper==21) {
							op = "+";
						}else {
							op = "-";
						}
						token_num.remove(0);
						code_str.remove(0);
						this.state = String.format("(WARNING) %s �����ڰ� �ߺ��Ǿ����ϴ�. %s�� �����մϴ�.", op, op);
					}else {
						break;
					}
				}
				// + ������ �̸� ������ TERM, TERM_TAIL ���� ����
				if(token_num.contains(21)) {// + �����ڷ� term�� term_tail ����
					int index = token_num.indexOf(21);
					String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("+", val1, val2);
				}else if(token_num.contains(22)) {// - �����ڷ� term�� term_tail ����
					int index = token_num.indexOf(22);
					String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("-", val1, val2);
				}else {//term_tail�� �۽Ƿ��� ���
					return TERM(token_num, code_str);
				}
			}else {
				//System.out.println("term_tail�� +- �����ڰ� ����");
				this.state = "(ERROR) �����ڰ� �����ϴ�.";
				return "ERROR";
			}
		}catch(IndexOutOfBoundsException e) {
			//System.out.println("TERM_TAIL_ �۽Ƿ�");
			return "";
		}
	}
	public String FACTOR_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		try {
			if(token_num.get(0) == 23 || token_num.get(0) == 24) {
				token_num.remove(0);
				code_str.remove(0);
				while(true) {
					if(token_num.get(0)==23||token_num.get(0)==24||token_num.get(0)==21||token_num.get(0)==22) {
						int oper = token_num.get(0);
						String op;
						if(oper==23) {
							op = "*";
						}else if(oper==24) {
							op = "/";
						}else if(oper==21) {
							op = "+";
						}else {
							op = "-";
						}
						token_num.remove(0);
						code_str.remove(0);
						this.state = String.format("(WARNING) %s �����ڰ� �ߺ��Ǿ����ϴ�. %s �� �����մϴ�.", op, op);
					}else {
						break;
					}
				}
				if(token_num.contains(23)) {
					int index = token_num.indexOf(23);
					String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("*", val1, val2);
				}else if(token_num.contains(24)) {
					int index = token_num.indexOf(24);
					String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("/", val1, val2);
				}else {
					return FACTOR(token_num, code_str);
				}
			}else {
				//System.out.println("factor_tail�� */ �����ڰ� ����");
				this.state = "(ERROR) �����ڰ� �����ϴ�.";
				return "ERROR";
			}
		}catch (IndexOutOfBoundsException e) {
			//System.out.println("Factor_TAIL_ �۽Ƿ�");
			return "";
		}
	}
	
	public void lexical(String input) {
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.add("^[0-9]*$"); //CONST decimal numbers 
		patterns.add("^[a-zA-Z_][0-9a-zA-Z_]*$"); //IDENT
		patterns.add(":="); //ASSIGNMENT_OP
		patterns.add(";"); //SEMI_COLON
		patterns.add("\\+"); // ADD_OPERATOR
		patterns.add("\\-"); // -
		patterns.add("\\*"); // MULT_OPERATOR
		patterns.add("/"); // divide
		patterns.add("\\("); // LEFT_PATEN
		patterns.add("\\)"); // RIGHT_PATEN
		for(int p = 0; p<patterns.size(); p++) {
			if(Pattern.matches(patterns.get(p), input)) {
				if(p==0) {
					this.next_token = 1; // CONST
					this.token_string = "CONST";
				}else if(p==1) {
					this.next_token =  2; // IDENT
					this.token_string = "IDENT";
				}else if(p==2) {
					this.next_token =  11; //ASSIGNMENT_OP
					this.token_string = "ASSIGNMENT_OP";
				}else if(p==3) {
					this.next_token =  12; //SEMI_COLON
					this.token_string = "SEMI_COLON";
				}else if(p==4) {
					this.next_token =  21; //ADD
					this.token_string = "ADD_OPERATOR";
				}else if(p==5) {
					this.next_token =  22; // SUB
					this.token_string = "ADD_OPERATOR";
				}else if(p==6) {
					this.next_token =  23; // MUL
					this.token_string = "MULT_OPERATOR";
				}else if(p==7) {
					this.next_token =  24; // DIV
					this.token_string = "MULT_OPERATOR";
				}else if(p==8) {
					this.next_token =  31; // LEFT_PAREN
					this.token_string = "LEFT_PAREN";
				}else if(p==9) {
					this.next_token =  32; // RIGHT_PAREN
					this.token_string = "RIGHT_PAREN";
				}else {
					System.out.println("(ERROR) ��ȿ���� ���� ��ū�� �ֽ��ϴ�.");
					this.next_token =  99; // ERROR
					this.token_string = "ERROR";
				}
			}
		}
	}
	public ArrayList<Integer> splitInt(ArrayList<Integer> a, int start, int fin) {
		List<Integer> temp = a.subList(start, fin);
		ArrayList<Integer> t = new ArrayList<Integer>();
		t.addAll(temp);
		return t;
	}
	public ArrayList<String> splitStr(ArrayList<String> a, int start, int fin) {
		List<String> temp = a.subList(start, fin);
		ArrayList<String> t = new ArrayList<String>();
		t.addAll(temp);
		return t;
	}
	public String validVal(String operation, String val1, String val2) {
		if(val1.equals("Unknown")||val2.equals("Unknown")||val1.equals("newUnknown")||val2.equals("newUnknown")) {
			return "Unknown";
		}else if(val1.equals("ERROR") || val2.equals("ERROR")) {
			return "ERROR";
		}else {
			if(operation.equals("+")) {
				return Integer.toString(Integer.parseInt(val1) + Integer.parseInt(val2));
			}else if(operation.equals("-")) {
				return Integer.toString(Integer.parseInt(val1) - Integer.parseInt(val2));
			}else if(operation.equals("*")) {
				return Integer.toString(Integer.parseInt(val1) * Integer.parseInt(val2));
			}else if(operation.equals("/")) {
				return Integer.toString(Integer.parseInt(val1) / Integer.parseInt(val2));
			}else {
				return "ERROR";
			}
		}
	}
}
