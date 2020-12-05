package assinmenet;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.regex.Pattern;

public class LexicalAnalyzer {
	int next_token;
	String state;
	String token_string;
	ArrayList<String> code;
	HashMap<String, int[]> simbolTable; //simbolTable (str)변수 명 : (str)변수 값 
	ArrayList<String> ariStack;
	ArrayList<String> printStack;
	
	LexicalAnalyzer(ArrayList<String> input){
		this.code = input;
	}
	public void START() {
		simbolTable = new HashMap<String, int[]>(); // function들을 저장하는 역할
		FUNCTIONS();
	}
	public void FUNCTIONS() {
		//ArrayList<Integer> token_num = new ArrayList<Integer>();
		//ArrayList<String> token_str = new ArrayList<String>();
		//ArrayList<String> code_str = new ArrayList<String>();
		int index=0;
		int idxStart=0;
		int idxFinis=0;
		boolean functionError = true;
		String functionName = null;
		String before = null;
		for(String c : this.code) {
			lexical(c);
			if(this.next_token == 5) {
				functionName = before;
				if(checkFunctionName(functionName)) {
					idxStart = index+1;
					functionError = false;
				}else {
					//function 이름이 중복되었을 때 처리
					System.out.println("Duplicate declaration of the function name: "+ functionName);
					break;
				}
			}
			else if(this.next_token == 6){
				if (!functionError) {
					idxFinis = index-1;
					int []list = {idxStart, idxFinis};
					simbolTable.put(functionName, list);
					functionError = true;
				}else {
					System.out.println("Syntax Error._Functions");
					//함수 { } 이 구조가 아님
					break;
				}
			}
			before = c;
			index++;
		}
		System.out.println("===============Function================");
		simbolTable.forEach((key, value) -> {
			System.out.println(key);
			System.out.println(value[0]);
			System.out.println(value[1]);
		});
		//main을 찾고 시작
		/**
		simbolTable.forEach((key, value) -> {
			if(key.equals("main")) {
				FUNCTION_BODY("main", getCode(this.code, value[0], value[1]));
				return;
			}
		});
		**/
		try {
			HashMap<String, int[]> mainFunc = findFunction("main");
			int[] value = mainFunc.get("main");
			ariStack = new ArrayList<String>();//
			printStack = new ArrayList<String>();// 마지막에 syntax 에러가 없다면 출력하기 위한 용도
			FUNCTION_BODY("main", getCode(this.code, value[0], value[1]));
			//printStack에 있는거 출력
		}catch(Exception e) {
			System.out.println("Main 함수 못 찾음");
			System.out.println(e);
		}
		//프로그램에 장애가 없다면 출력
		for(String s:printStack) {
			System.out.println(s);
		}
	}
	public void FUNCTION_BODY(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" FUNCTION_BODY");
		System.out.println(codeStr);
		VAR_DEFINITIONS(funcName, getCode(codeStr,0,codeStr.size()-1));
	}
	
	public void VAR_DEFINITIONS(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" VAR_DEFINITIONS");
		System.out.println(codeStr);
		lexical(codeStr.get(0));
		if(next_token == 3) {
			int idx = codeStr.indexOf(";");
			VAR_DEFINITION(funcName, getCode(codeStr,1,idx));// variable이라는 문자를 빼고 뒤에것 넘겨줌
			//variable a, b, c; 이줄 VAR_DEFINION에 보내고 
			//나머지는 다시 VAR_DEFINIONS에 넣어 variable이 안나올때까지 반복시킨다.
			VAR_DEFINITIONS(funcName, getCode(codeStr,idx+1,codeStr.size()-1));
		}else if(next_token==1 || next_token==2 || next_token==4) {
			STATEMENTS(funcName, getCode(codeStr,0,codeStr.size()-1), 1);
		}else {
			//syntax error
		}
	}
	
	public void VAR_DEFINITION(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" VAR_DEFINITION");
		System.out.println(codeStr);
		lexical(codeStr.get(0));
		if(next_token==4) {//변수명 확인
			if(checkIdentName(codeStr.get(0))) {//변수가 맞다면
				ariStack.add("Local variable:"+codeStr.get(0));//stack 에 변수 이름 추가 변수 앞에는 V라는 첨자가 붙음
				VAR_LIST(funcName, getCode(codeStr,1,codeStr.size()-1));
			}else {
				System.out.println("변수 명이 이미 존재합니다.");
			}
		}else {
			System.out.println("Syntax error");
		}
	}
	
	public void VAR_LIST(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" VAR_LIST");
		System.out.println(codeStr);
		lexical(codeStr.get(0));
		if(next_token==7) {//comma
			VAR_DEFINITION(funcName, getCode(codeStr, 1, codeStr.size()-1));
		}else if(next_token==8) {//semicolon
			//정상종료
		}else {
			System.out.println("Syntax Error");
		}
	}
	
	public void STATEMENTS(String funcName, ArrayList<String> codeStr, int executePoint) {
		//executePoint는 진행한 줄수 기록
		System.out.println(funcName+" STATEMENTS");
		System.out.println(codeStr);
		if(!codeStr.isEmpty()) {
			lexical(codeStr.get(0));
			//variable은 VAR_DEFINITIONS에서 처리했으니 걱정 말라고
			if(next_token==1 || next_token==2 || next_token==4) {//call print_ari IDENT
				int idx = codeStr.indexOf(";");
				STATEMENT(funcName,getCode(codeStr,0,idx-1),executePoint);//세미콜론은 뺴고 전송한다
				executePoint ++;//STATEMENT 실행하면 
				STATEMENTS(funcName, getCode(codeStr, idx+1,codeStr.size()-1),executePoint);//다음 줄 구문 실행
			}else if(next_token == 99) {//ERROR
				//Syntax error
				System.out.println("Syntax error");
			}
			else {
				//Syntax error
				System.out.println("Syntax error");
			}
		}else {
			//STATEMENTS 정상 종료됨
		}
	}
	
	public void STATEMENT(String funcName, ArrayList<String> codeStr, int ep) {
		System.out.println(funcName+" STATEMENT");
		System.out.println(codeStr);//세미콜론은 STATEMENT에서 뺌
		if(!codeStr.isEmpty()) {
			lexical(codeStr.get(0));
			if(next_token==1) {//call
				HashMap<String, int[]> newFunc = findFunction(codeStr.get(1));
				if(newFunc != null) {
					System.out.println("CALL!");
					String dynamicLink = getDynamicLink();
					ariStack.add("Return Address:"+funcName+":"+ep);
					ariStack.add("Dynamic Link:"+dynamicLink);
					int[] list = newFunc.get(codeStr.get(1));
					//새로운 함수 시작
					FUNCTION_BODY(codeStr.get(1), getCode(this.code, list[0], list[1]));
				}else {
					//function 찾을 수 없음
					System.out.println("함수 이름이 존재하지 않습니다.");
				}
			}else if(next_token==2) {//print_ari
				System.out.println("printPRI!");
				printARI(funcName);
			}else if(next_token==4) {//ident
				System.out.println("IDENT!");
				if(checkIdentName(codeStr.get(0))) {
					printIdent(codeStr.get(0));
				}else {
					System.out.println("변수명이 존재합니다.");
					//나중에 함수 명이랑도 겹치는지 봐야함
				}
			}
		}
		else {
			//이상한 토큰이 들어옴
		}
	}
	
	public boolean checkIdentName(String name) {
		if(name.equals("variable") || name.equals("call") || name.equals("print_ari")) {
			System.out.println("Syntax Error_Check IdentName");
			return false;
		}
		for(int iter=ariStack.size()-1; iter>=0; iter--) {// 지역변수중에 겹치는 것이 있는지 확인
			if("Dynamic Link".equals(ariStack.get(iter).split(":")[0])) {
				break;
			}
			if(name.equals(ariStack.get(iter).split(":")[1])){
				return false;
			}
		}
		return true;
	}
	
	public boolean checkFunctionName(String funcName) {
		for(String funtionName : simbolTable.keySet()) {
			if(funcName.equals(funtionName)) {
				return false;
			}
		}
		return true;
	}
	
	public void printARI(String nowFunctionName) {
		System.out.println("printARI");
		String functionName = nowFunctionName;
		String print = functionName+": ";
		for(int iter=ariStack.size()-1; iter>=0 ;iter--){
			if(ariStack.get(iter).split(":")[0].equals("Return Address")) {
				functionName = ariStack.get(iter).split(":")[1];
				print = print + ariStack.get(iter);
			}else {
				print = print + "\t" +ariStack.get(iter);
			}
			print = print + "\n";
		}
		System.out.println(print);
		printStack.add(print);
	}
	public void printIdent(String name) {
		System.out.println("printIdent");
		System.out.println(name);
		for(int iter=ariStack.size()-1; iter>=0; iter--) {
			
		}
	}
	public HashMap<String, int[]> findFunction(String funcName){
		for(Map.Entry<String, int[]> element: simbolTable.entrySet()) {
			if(element.getKey().equals(funcName)) {
				HashMap<String, int[]> table = new HashMap<String, int[]>();
				int []list = {element.getValue()[0], element.getValue()[1]};
				table.put(element.getKey(), list);
				return table;
			}
		}
		return null;
	}
	public ArrayList<String> getCode(ArrayList<String> code, int idxStart, int idxFinish) {
		//idxStart ~ idxFinish 까지 코드를 반환해줌(function {주저리 주저리})
		ArrayList<String> functionCode = new ArrayList<String>();
		functionCode.addAll(code.subList(idxStart, idxFinish+1));
		return functionCode;
	}
	
	public String getDynamicLink() {
		int address=0;
		for(int iter=ariStack.size()-1;iter>=0;iter--) {
			String kind = ariStack.get(iter).split(":")[0];
			if(kind=="RA") {
				address = iter;
				break;
			}
		}
		return Integer.toString(address);
	}
	
	public void lexical(String input) {
		ArrayList<String> patterns = new ArrayList<String>();
		patterns.add("^[a-zA-Z_][0-9a-zA-Z_]*$"); //IDENT
		patterns.add("\\{"); // {
		patterns.add("\\}"); // }
		patterns.add(","); // COMMA
		patterns.add(";"); //SEMI_COLON
		if(input.equals("call")) {
			this.next_token = 1;
			this.token_string = "CALL";
		}else if(input.equals("print_ari")) {
			this.next_token = 2;
			this.token_string = "PRINT_ARI";
		}else if(input.equals("variable")) {
			this.next_token = 3;
			this.token_string = "VARIABLE";
		}else {
			for(int p=0; p<patterns.size(); p++) {
				if(Pattern.matches(patterns.get(p), input)) {
					if(p==0) {
						this.next_token = 4;
						this.token_string = "IDENT";
						return;
					}else if(p==1) {
						this.next_token = 5;
						this.token_string = "LBRACE";
						return;
					}else if(p==2) {
						this.next_token = 6;
						this.token_string = "RBRACE";
						return;
					}else if(p==3) {
						this.next_token = 7;
						this.token_string = "COMMA";
						return;
					}else if(p==4) {
						this.next_token = 8;
						this.token_string = "SEMI_COLON";
						return;
					}
					else {
						System.out.println("(ERROR) 유효하지 않은 토큰이 있습니다.");
						this.next_token = 99;
						this.token_string = "ERROR";
					}
				}
			}

		}
	}
}
