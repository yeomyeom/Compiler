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
	HashMap<String, String> simbolTable; //simbolTable (str)변수 명 : (str)변수 값 
	
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
			lexical(c); // 코드를 띄어쓰기 기준으로 한 단어 한 단어씩 읽어서
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			code_str.add(c);
			if(this.token_string.equals("SEMI_COLON")) {
				for(String code : code_str) {
					System.out.print(code + " ");
				}
				System.out.println("");
				token_num.remove(token_num.size() -1); // 맨뒤 세미콜론 지우기 
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
						if(simbolTable.containsValue("newUnknown")) {//newUnknown 이 있으면 Unknown 값으로 바꿔줌 일단 할당 된거니깐
							for(String sTval : simbolTable.keySet()) {
								if(simbolTable.get(sTval).equals("newUnknown")) {
									simbolTable.put(sTval, "Unknown");
								}
							}
							this.state = "(ERROR) 정의되지 않은 변수가 사용되었습니다.";
						}
					}
				}else{
					//System.out.println("statement a= 뒤에 연산할꺼 없음");
					this.state = "(ERROR) 대입 연산자 뒤에 피 연산자가 없습니다.";
				}
			}else if(token_num.get(0)==1 && token_num.get(1)==11) {
				this.state = "(ERROR) 상수에 값을 대입하려고 하고 있습니다.";
			}else {
				//System.out.println("statement 대입 op나 대입연산자가 없음");
				this.state = "(ERROR) 변수 또는 대입 연산자가 없습니다.";
			}
		}catch (IndexOutOfBoundsException e) {
			//System.out.println("statement token_num.get 에러");
			this.state = "(ERROR) 대입 연산 자체가 잘못되었습니다.";
		}
	}
	public String EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		if(token_num.contains(21)) {// + 연산자로 term과 term_tail 구분
			int index = token_num.indexOf(21);
			String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			return validVal("+", val1, val2);
		}else if(token_num.contains(22)) {// - 연산자로 term과 term_tail 구분
			int index = token_num.indexOf(22);
			String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			return validVal("-", val1, val2);
		}else {//term_tail이 앱실론인 경우
			return TERM(token_num, code_str);
		}
	}
	public String TERM(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		if(token_num.contains(31)) {// ( 게 있는 경우
			int leftidx = token_num.indexOf(31);
			int righidx = token_num.indexOf(32);
			return EXPRESSION(splitInt(token_num, leftidx+1, righidx), splitStr(code_str, leftidx+1, righidx));
		}else {// 괄호가 전혀 없다면 *, / 로 factor와 factor_tail을 구분한다.
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
			}else { // a=10 처럼 아이에 할당 연산이 없는 변수같은 경우 에러메시지를 위해 newUnknown이라고 따로 할당을 해줌(에러메시지 출력 이후에는 Unknown으로)
				simbolTable.put(code_str.get(0), "newUnknown");
				return "Unknown";
			}
		}else if(token_num.get(0)==1) {// const
			return code_str.get(0);
		}
		/**
		else if(token_num.get(0) == 31) {
			//괄호는 term쪽에서 해결해서 factor 에는 필요 없을듯
		}**/
		else {
			this.state = "(ERROR) 피연산자가 없습니다.";
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
						this.state = String.format("(WARNING) %s 연산자가 중복되었습니다. %s를 제거합니다.", op, op);
					}else {
						break;
					}
				}
				// + 연산지 미리 보내고 TERM, TERM_TAIL 연산 진행
				if(token_num.contains(21)) {// + 연산자로 term과 term_tail 구분
					int index = token_num.indexOf(21);
					String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("+", val1, val2);
				}else if(token_num.contains(22)) {// - 연산자로 term과 term_tail 구분
					int index = token_num.indexOf(22);
					String val1 = TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					String val2 = TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
					return validVal("-", val1, val2);
				}else {//term_tail이 앱실론인 경우
					return TERM(token_num, code_str);
				}
			}else {
				//System.out.println("term_tail에 +- 연산자가 없음");
				this.state = "(ERROR) 연산자가 없습니다.";
				return "ERROR";
			}
		}catch(IndexOutOfBoundsException e) {
			//System.out.println("TERM_TAIL_ 앱실론");
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
						this.state = String.format("(WARNING) %s 연산자가 중복되었습니다. %s 를 제거합니다.", op, op);
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
				//System.out.println("factor_tail에 */ 연산자가 없음");
				this.state = "(ERROR) 연산자가 없습니다.";
				return "ERROR";
			}
		}catch (IndexOutOfBoundsException e) {
			//System.out.println("Factor_TAIL_ 앱실론");
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
					System.out.println("(ERROR) 유효하지 않은 토큰이 있습니다.");
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
