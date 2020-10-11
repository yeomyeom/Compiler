import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
	int next_token;
	String token_string;
	ArrayList<String> code;
	HashMap<String, Integer> simbolTable;
	
	LexicalAnalyzer(ArrayList<String> input){
		this.code = input;
	}
	public void PROGRAMS() {
		simbolTable = new HashMap<String, Integer>();//simbol table 생성
		STATEMENTS();
		System.out.println("Result == >"); // simbol table 에 저장되어 있는 것들 출력
	}
	public void STATEMENTS() {
		int success = 0; // 0: success 1: warning 2: error
		ArrayList<Integer> token_num = new ArrayList<Integer>();
		ArrayList<String> token_str = new ArrayList<String>();
		ArrayList<String> code_str = new ArrayList<String>();
		for (String c : this.code) {
			lexical(c); // 코드를 띄어쓰기 기준으로 한 단어 한 단어씩 읽어서
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			code_str.add(c);
			if(this.token_string.equals("SEMI_COLON"))
				System.out.println(code_str);
				token_num.remove(token_num.size() -1); // 맨뒤 세미콜론 지우기 
				code_str.remove(code_str.size() -1);
				success = STATEMENT(token_num, code_str);
				System.out.print("ID: ");
				System.out.print("; CONST: ");
				System.out.print("; OP: " + ";");
				token_num.clear();
				token_str.clear();
				code_str.clear();
		}
		STATEMENT(token_num, code_str); // stats -> statement 요 규칙
		token_num.remove(token_num.size() -1); // 맨뒤 세미콜론 지우기 
		code_str.remove(code_str.size() -1);
		System.out.print("ID: ");
		System.out.print("; CONST: ");
		System.out.print("; OP: " + ";");
	}
	public int STATEMENT(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		//[02     ,11             ,01     ] 예시임 CONST는 expression으로 넘길꺼
		//['IDENT','ASSIGNMENT_OP','CONST']
		//['op1'  ,':='           ,'3'    ]
		if(token_num.get(0).equals(2) && token_num.get(1).equals(11)) {
			//simbol table에 변수 정보 전달(현재 할당 연산지니깐)
			String ident_name = code_str.get(0);
			token_num.remove(0);
			token_num.remove(1);
			code_str.remove(0);
			code_str.remove(1);
			if(code_str.size() >= 1) {
				int result = EXPRESSION(token_num, code_str);
				simbolTable.put(ident_name, result);
			}
			else {
				System.out.println("(ERROR) STATEMENT 할당된 연산자가 없습니다.");
			}
		}
		else {
			//구문 오류 발생 할때 어떻게 처리할 것인가?
		}
		return 0;
	}
	public int EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		//EXPRESSION -> <TERM> <TERM_TAIL>
		//전단계 Statement 변수에 값을 저장 
		int vale = 0;
		TERM(token_num, code_str);
		TERM_TAIL(token_num, code_str);
		return vale;
	}
	public void TERM(ArrayList<Integer> token_num, ArrayList<String> token_str) {
		//TERM -> <FACTOR> <FACTOR_TAIL>
		FACTOR(token_num, token_str);
		FACTOR_TAIL(token_num, token_str);
	}
	public void TERM_TAIL(ArrayList<Integer> token_num, ArrayList<String> token_str) {
		//TERM_TAIL -> +|- <TERM> <TERM_TAIL> 또는 앱실론
		if(token_str.get(0).equals("ADD_OPERATOR")){
			//+ - 연산 진행 simboltable에서
			token_str.remove(0);
			token_num.remove(0);
			TERM(token_num, token_str);
			TERM_TAIL(token_num, token_str);
		}
		else {
			return;
		}
	}
	public void FACTOR(ArrayList<Integer> token_num, ArrayList<String> token_str) {
		//FACTOR -> ( <EXPRESSION> ) 또는 <IDENT> 또는 <CONST>
		if(token_str.get(0).equals("LEFT_PAREN")) {
			
			EXPRESSION(token_num, token_str);
		}else if(token_str.get(0).equals("IDENT")) {
			
		}else if(token_str.get(0).equals("CONST")) {
			
		}else {
			//ERROR
		}
	}
	public void FACTOR_TAIL(ArrayList<Integer> token_num, ArrayList<String> token_str) {
		//FACTOR_TAIL -> *|/ <FACTOR> <FACTOR_TAIL> 또는 앱실론
		if(token_str.get(0).equals("MULT_OPERATOR")) {
			token_str.remove(0);
			token_num.remove(0);
			FACTOR(token_num, token_str);
			FACTOR_TAIL(token_num, token_str);
		}
		else {
			return;
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
					this.next_token = 01; // CONST
					this.token_string = "CONST";
				}else if(p==1) {
					this.next_token =  02; // IDENT
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
	public void lexical() {
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
		for(int i = 0; i<this.code.size(); i++) {
			for(int p = 0; p<patterns.size(); p++) {
				if(Pattern.matches(patterns.get(p), this.code.get(i))) {
					if(p==0) {
						System.out.println("CONST");
					}else if(p==1) {
						System.out.println("IDENT");
					}else if(p==2) {
						System.out.println("ASSIGNMENT_OP");
					}else if(p==3) {
						System.out.println("SEMI_COLON");
					}else if(p==4) {
						System.out.println("ADD_OPERATOR");
					}else if(p==5) {
						System.out.println("ADD_OPERATOR");
					}else if(p==6) {
						System.out.println("MULT_OPERATOR");
					}else if(p==7) {
						System.out.println("MULT_OPERATOR");
					}else if(p==8) {
						System.out.println("LEFT_PAREN");
					}else if(p==9) {
						System.out.println("RIGHT_PAREN");
					}else {
						System.out.println("ERROR");
					}
				}
			}
		}
	}
}
