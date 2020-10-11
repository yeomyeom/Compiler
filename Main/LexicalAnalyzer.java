import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
	int next_token;
	String token_string;
	ArrayList<String> code;
	HashMap<String, String> simbolTable; //simbolTable (str)변수 명 : (str)변수 값 
	
	LexicalAnalyzer(ArrayList<String> input){
		this.code = input;
	}
	public void PROGRAMS() {
		simbolTable = new HashMap<String, String>();
		System.out.println("PROGRAMS");
		STATEMENTS();
		System.out.print("Result ==> ");
		for(Entry<String, String> e : simbolTable.entrySet()) {
			String valName = e.getKey();
			String value = e.getValue();
			System.out.print(String.format("%s: %s; ", valName, value));
		}
	}
	public void STATEMENTS() {
		System.out.println("STATEMENTS");
		ArrayList<Integer> token_num = new ArrayList<Integer>();
		ArrayList<String> token_str = new ArrayList<String>();
		ArrayList<String> code_str = new ArrayList<String>();
		for (String c : this.code) {
			lexical(c); // 코드를 띄어쓰기 기준으로 한 단어 한 단어씩 읽어서
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			code_str.add(c);
			if(this.token_string.equals("SEMI_COLON")) {
				System.out.println(code_str);
				token_num.remove(token_num.size() -1); // 맨뒤 세미콜론 지우기 
				code_str.remove(code_str.size() -1);
				token_str.remove(token_str.size() -1);
				String state = STATEMENT(token_num, code_str);
				System.out.println(state);
				token_num.clear();
				token_str.clear();
				code_str.clear();
			}
		}
		String state = STATEMENT(token_num, code_str);
		System.out.println(state);
	}
	public String STATEMENT(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("STATEMENT");
		System.out.println(code_str);
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
					if(val.equals("Unknown") || val.equals("ERROR")) {
						return "(ERROR)";
					}else {
						return "(OK)";
					}
				}else{
					System.out.println("statement a= 뒤에 연산할꺼 없음");
				}
			}
			else {
				System.out.println("statement 대입 op나 대입연산자가 없음");
			}
		}catch (IndexOutOfBoundsException e) {
			System.out.println("statement token_num.get 에러");
		}
		return "(ERROR)";
	}
	public String EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("EXPRESSION");
		System.out.println(code_str);
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
		System.out.println("TERM");
		System.out.println(code_str);
		if(token_num.contains(31)) {// ( 게 있는 경우
			int leftidx = token_num.indexOf(31);
			int righidx = token_num.indexOf(32);
			return EXPRESSION(splitInt(token_num, leftidx+1, righidx), splitStr(code_str, leftidx+1, righidx));
			// expression 연산 결과를 숫자로 받아 token code 재 작성(?필요한가?)
			/**
			for(int l = leftidx; l <= righidx; l++){// 괄호 연산 끝나면 최종적으로 숫자만 튀어나오게
				token_num.remove(l);
				code_str.remove(l);
			}
			token_num.add(leftidx, 1);
			code_str.add(leftidx, val); // expression 한 결과를 코드에 삽입
			if(token_num.contains(23)) {
				int index = token_num.indexOf(23);
				String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				return Integer.toString(Integer.parseInt(val1) * Integer.parseInt(val2));
			}else if(token_num.contains(24)) {
				int index = token_num.indexOf(24);
				String val1 = FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				String val2 = FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				return Integer.toString(Integer.parseInt(val1) / Integer.parseInt(val2));
			}else {
				return FACTOR(token_num, code_str);
			}
			**/
			//일단 괄호는 보류한다 일단 나중에 지금 까지 한거 잘 돌아가는지 확인 하고 추후에 하겠음
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
		System.out.println("FACTOR");
		System.out.println(code_str);
		if(token_num.get(0) == 2) {// ident
			System.out.println("IDENT");
			System.out.println("변수이름 :" + code_str.get(0));
			if(simbolTable.containsKey(code_str.get(0))) {
				return simbolTable.get(code_str.get(0));
			}else {
				return "Unknown";
			}
		}else if(token_num.get(0) == 1) {// const
			System.out.println("CONST");
			System.out.println("상수: " + code_str.get(0));
			return code_str.get(0);
		}
		/**
		else if(token_num.get(0) == 31) {
			//괄호는 term쪽에서 해결해서 factor 에는 필요 없을듯
			return "0";
		}**/
		else {
			System.out.println("FACTOR_ERROR");
			return "ERROR";
		}
	}
	public String TERM_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("TERM_TAIL");
		System.out.println(code_str);
		try {
			if(token_num.get(0) == 21 || token_num.get(0) == 22) {
				token_num.remove(0);
				code_str.remove(0);
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
				System.out.println("term_tail에 +- 연산자가 없음");
				return "ERROR";
			}
		}catch(IndexOutOfBoundsException e) {
			System.out.println("TERM_TAIL_ 앱실론");
			return "";
		}	
	}
	public String FACTOR_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("FACTOR_TAIL");
		System.out.println(code_str);
		try {
			if(token_num.get(0) == 23 || token_num.get(0) == 24) {
				token_num.remove(0);
				code_str.remove(0);
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
				System.out.println("factor_tail에 */ 연산자가 없음");
				return "ERROR";
			}
		}catch (IndexOutOfBoundsException e) {
			System.out.println("Factor_TAIL_ 앱실론");
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
		if(val1.equals("Unknown") || val2.equals("Unknown")) {
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
