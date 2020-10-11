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
		System.out.println("PROGRAMS");
		simbolTable = new HashMap<String, Integer>();//simbol table ����
		STATEMENTS();
		System.out.println("Result == >"); // simbol table �� ����Ǿ� �ִ� �͵� ���
	}
	public void STATEMENTS() {
		System.out.println("STATEMENTS");
		ArrayList<Integer> token_num = new ArrayList<Integer>();
		ArrayList<String> token_str = new ArrayList<String>();
		ArrayList<String> code_str = new ArrayList<String>();
		for (String c : this.code) {
			lexical(c); // �ڵ带 ���� �������� �� �ܾ� �� �ܾ �о
			token_num.add(this.next_token);
			token_str.add(this.token_string);
			code_str.add(c);
			if(this.token_string.equals("SEMI_COLON"))
				System.out.println(code_str);
				token_num.remove(token_num.size() -1); // �ǵ� �����ݷ� ����� 
				code_str.remove(code_str.size() -1);
				String success = STATEMENT(token_num, code_str);
				System.out.print("ID: ");
				System.out.print("; CONST: ");
				System.out.print("; OP: " + ";");
				System.out.print(success);
				token_num.clear();
				token_str.clear();
				code_str.clear();
		}
		STATEMENT(token_num, code_str); // stats -> statement �� ��Ģ
		token_num.remove(token_num.size() -1); // �ǵ� �����ݷ� ����� 
		code_str.remove(code_str.size() -1);
		System.out.print("ID: ");
		System.out.print("; CONST: ");
		System.out.print("; OP: " + ";");
	}
	public String STATEMENT(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("STATEMENT");
		//[02     ,11             ,01     ] ������ CONST�� expression���� �ѱ沨
		//['IDENT','ASSIGNMENT_OP','CONST']
		//['op1'  ,':='           ,'3'    ]
		if(token_num.get(0).equals(2) && token_num.get(1).equals(11)) {
			//simbol table�� ���� ���� ����(���� �Ҵ� �������ϱ�)
			String ident_name = code_str.get(0);
			token_num.remove(0);
			token_num.remove(1);
			code_str.remove(0);
			code_str.remove(1);
			if(code_str.size() >= 1) {
				String result = EXPRESSION(token_num, code_str);
				simbolTable.put(ident_name, Integer.parseInt(result));
				return "ok";
			}
			else {
				System.out.println("(ERROR) = �ڰ� �����ϴ�.");
				return "Error";
			}
		}
		else {
			return "Error";
			//���� ���� �߻� �Ҷ� ��� ó���� ���ΰ�?
		}
	}
	public String EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("EXPRESSION");
		//EXPRESSION -> <TERM> <TERM_TAIL>
		//����ϰ� ���� statement�� �Ѱ���
		String term = TERM(token_num, code_str);
		String termTail = TERM_TAIL(token_num, code_str);
		return (Integer.parseInt(term) + Integer.parseInt(termTail));
	}
	public String TERM(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("TERM");
		//TERM -> <FACTOR> <FACTOR_TAIL>
		FACTOR(token_num, code_str);
		FACTOR_TAIL(token_num, code_str);
	}
	public String TERM_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("TERM_TAIL");
		//TERM_TAIL -> +|- <TERM> <TERM_TAIL> �Ǵ� �۽Ƿ�
		if(token_num.get(0).equals(21) || token_num.get(0).equals(22)){
			//+ - ���� ���� simboltable����
			code_str.remove(0); // + - ����� 
			token_num.remove(0);
			if (token_num.get(0).equals(21)) {
				//���ϱ� ���� ����
				
			}else {
				//���� ���� ����
			}
			TERM(token_num, code_str);
			TERM_TAIL(token_num, code_str);
		}
		else {
			return "Error";
		}
	}
	public String FACTOR(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("FACTOR");
		//FACTOR -> ( <EXPRESSION> ) �Ǵ� <IDENT> �Ǵ� <CONST>
		if(token_num.get(0).equals(31)) {
			ArrayList <String> temp_str = new ArrayList <String>();
			ArrayList <Integer> temp_num = new ArrayList <Integer>();
			for (int temp : token_num) {
				temp_num.add(temp);
				if(temp == 32) {
					//
				}
			}
			return EXPRESSION(token_num, code_str);
		}else if(token_num.get(0).equals(2)) {
			//HashMap ���� ��� ã��
			return Integer.toString(simbolTable.get(code_str.get(0)));
		}else if(token_num.get(0).equals(1)) {
			return code_str.get(0);
		}else {
			return "Error";
			//ERROR
		}
	}
	public String FACTOR_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("FACTOR_TAIL");
		//FACTOR_TAIL -> *|/ <FACTOR> <FACTOR_TAIL> �Ǵ� �۽Ƿ�
		if(code_str.get(0).equals("MULT_OPERATOR")) {
			code_str.remove(0);
			token_num.remove(0);
			FACTOR(token_num, code_str);
			FACTOR_TAIL(token_num, code_str);
		}
		else {
			
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
}
