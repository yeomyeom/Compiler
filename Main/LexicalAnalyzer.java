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
		ArrayList<Integer> token_num = new ArrayList<Integer>();
		ArrayList<String> token_str = new ArrayList<String>();
		for (String c : code) {
			lexical(c); // 코드를 띄어쓰기 기준으로 한 단어 한 단어씩 읽어서
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			if(this.token_string.equals("SEMI_COLON"))
				STATEMENT(token_num, token_str);
				token_num.clear();
				token_str.clear();
		}
		STATEMENT(token_num, token_str); // stats -> statement 요 규칙
	}
	public void STATEMENT(ArrayList<Integer> token_num, ArrayList<String> token_str) {
		//[02     ,11             ,01     ] 예시임 CONST는 expression으로 넘길꺼
		//['IDENT','ASSIGNMENT_OP','CONST']
		if(token_str.get(0).equals("IDENT") && token_str.get(1).equals("ASSIGNMENT_OP")) {
			EXPRESSION();
		}
		else {
			//구문 오류 발생
		}
	}
	public void EXPRESSION() {
		
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
