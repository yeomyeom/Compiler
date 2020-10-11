import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Vmode {
	ArrayList<String> code;
	int next_token;
	String token_string;
	
	public Vmode(ArrayList<String>code) {
		this.code = code;
	}
	public void PROGRAMS() {
		System.out.println("PROGRAMS");
		STATEMENTS();
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
			if(this.token_string.equals("SEMI_COLON")) {
				System.out.println(code_str);
				token_num.remove(token_num.size() -1); // �ǵ� �����ݷ� ����� 
				code_str.remove(code_str.size() -1);
				token_str.remove(token_str.size() -1);
				STATEMENT(token_num, code_str);
				token_num.clear();
				token_str.clear();
				code_str.clear();
			}
		}
		STATEMENT(token_num, code_str);
	}
	public void STATEMENT(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("STATEMENT");
		System.out.println(code_str);
		try {
			if(token_num.get(0) == 2 && token_num.get(1) == 11) {
				token_num.remove(0);
				token_num.remove(0);
				code_str.remove(0);
				code_str.remove(0);
				if(token_num.size() >= 1) {
					EXPRESSION(token_num, code_str);
				}else{
					System.out.println("statement a= �ڿ� �����Ҳ� ����");
				}
			}
			else {
				System.out.println("statement ���� op�� ���Կ����ڰ� ����");
			}
		}catch (IndexOutOfBoundsException e) {
			System.out.println("statement token_num.get ����");
		}
	}
	public void EXPRESSION(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("EXPRESSION");
		System.out.println(code_str);
		if(token_num.contains(21)) {// + �����ڷ� term�� term_tail ����
			int index = token_num.indexOf(21);
			TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
		}else if(token_num.contains(22)) {// - �����ڷ� term�� term_tail ����
			int index = token_num.indexOf(22);
			TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
			TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
		}else {//term_tail�� �۽Ƿ��� ���
			TERM(token_num, code_str);
		}
	}
	public void TERM(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("TERM");
		System.out.println(code_str);
		if(token_num.contains(31)) {// ( �� �ִ� ���
			int leftidx = token_num.indexOf(31);
			int righidx = token_num.indexOf(32);
			EXPRESSION(splitInt(token_num, leftidx+1, righidx), splitStr(code_str, leftidx+1, righidx));
			if(token_num.contains(23)) {// * �� Factor�� fac_tail ����
				int index = token_num.indexOf(23);
				FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			}else if(token_num.contains(24)) {// /�� factor�� tail ����
				int index = token_num.indexOf(24);
				FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			}else {// factor_tail �� �۽Ƿ��� ���
				FACTOR(token_num, code_str);
			}
		}else {// ��ȣ�� ���� ���ٸ� *, / �� factor�� factor_tail�� �����Ѵ�.
			if(token_num.contains(23)) {
				int index = token_num.indexOf(23);
				FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			}else if(token_num.contains(24)) {
				int index = token_num.indexOf(24);
				FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
				FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
			}else {
				FACTOR(token_num, code_str);
			}
		}
	}
	public void FACTOR(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("FACTOR");
		System.out.println(code_str);
		if(token_num.get(0) == 31) {
			//��ȣ�� term�ʿ��� �ذ��ߴٰ�
		}else if(token_num.get(0) == 2) {// ident
			System.out.println("IDENT");
			System.out.println("�����̸� :" + code_str.get(0));
			//return ;
		}else if(token_num.get(0) == 1) {// const
			System.out.println("CONST");
			System.out.println("���: " + code_str.get(0));
			//return ;
		}
	}
	public void TERM_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("TERM_TAIL");
		System.out.println(code_str);
		try {
			if(token_num.get(0) == 21 || token_num.get(0) == 22) {
				token_num.remove(0);
				code_str.remove(0);
				// + ������ �̸� ������ TERM, TERM_TAIL ���� ����
				if(token_num.contains(21)) {// + �����ڷ� term�� term_tail ����
					int index = token_num.indexOf(21);
					TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				}else if(token_num.contains(22)) {// - �����ڷ� term�� term_tail ����
					int index = token_num.indexOf(22);
					TERM(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					TERM_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				}else {//term_tail�� �۽Ƿ��� ���
					TERM(token_num, code_str);
				}
			}else {
				System.out.println("term_tail�� +- �����ڰ� ����");
			}
		}catch(IndexOutOfBoundsException e) {
			System.out.println("TERM_TAIL_ �۽Ƿ�");
		}	
	}
	public void FACTOR_TAIL(ArrayList<Integer> token_num, ArrayList<String> code_str) {
		System.out.println("FACTOR_TAIL");
		System.out.println(code_str);
		try {
			if(token_num.get(0) == 23 || token_num.get(0) == 24) {
				token_num.remove(0);
				code_str.remove(0);
				if(token_num.contains(23)) {
					int index = token_num.indexOf(23);
					FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				}else if(token_num.contains(24)) {
					int index = token_num.indexOf(24);
					FACTOR(splitInt(token_num, 0, index), splitStr(code_str, 0, index));
					FACTOR_TAIL(splitInt(token_num, index, token_num.size()), splitStr(code_str, index, code_str.size()));
				}else {
					FACTOR(token_num, code_str);
				}
			}else {
				System.out.println("factor_tail�� */ �����ڰ� ����");
			}
		}catch (IndexOutOfBoundsException e) {
			System.out.println("Factor_TAIL_ �۽Ƿ�");
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
}
