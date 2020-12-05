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
					//같은 이름을 갖는 function이 있으면 안됨
					System.out.println("Duplicate declaration of the function name: "+ functionName);
					System.exit(0);
				}
			}
			else if(this.next_token == 6){
				if (!functionError) {
					idxFinis = index-1;
					int []list = {idxStart, idxFinis};
					simbolTable.put(functionName, list);
					functionError = true;
				}else {
					System.out.println("Syntax Error. 함수가 {} 로 묶여있지 않습니다.");
					//함수 { } 이 구조가 아님
					System.exit(0);
				}
			}
			before = c;
			index++;
		}
		/**
		// 확인용 코드
		System.out.println("===============Function================");
		simbolTable.forEach((key, value) -> {
			System.out.println(key);
			System.out.println(value[0]);
			System.out.println(value[1]);
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
			System.out.println("No starting function.");
			System.out.println(e);
			System.exit(0);
		}
		//프로그램에 장애가 없다면 출력
		System.out.println("Syntax.O.K\n");
		for(String s:printStack) {
			System.out.println(s);
			System.out.println(" ");
		}
		for(String s:ariStack) {
			System.out.println(s);
		}
		
	}
	public void FUNCTION_BODY(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" FUNCTION_BODY");
		System.out.println(codeStr);
		VAR_DEFINITIONS(funcName, getCode(codeStr,0,codeStr.size()-1));
		//FUNCTION이 종료되면 ari_stack 비우기
		cleanAriStack();
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
			System.out.println("Syntax Error. _VAR_DEFINITIONS");
			System.exit(0);
		}
	}
	
	public void VAR_DEFINITION(String funcName, ArrayList<String> codeStr) {
		System.out.println(funcName+" VAR_DEFINITION");
		System.out.println(codeStr);
		lexical(codeStr.get(0));
		if(next_token==4) {//변수명 확인
			if(checkIdentName(codeStr.get(0), funcName)) {//변수가 맞다면
				ariStack.add("Local variable:"+codeStr.get(0));//stack 에 변수 이름 추가 변수 앞에는 V라는 첨자가 붙음
				VAR_LIST(funcName, getCode(codeStr,1,codeStr.size()-1));
			}
		}else {
			System.out.println("Syntax Error. _VAR_DEFINITION");
			System.exit(0);
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
			System.out.println("Syntax Error. _VAR_LIST");
			System.exit(0);
		}
	}
	
	public void STATEMENTS(String funcName, ArrayList<String> codeStr, int executePoint) {
		//executePoint는 진행한 줄 수 기록
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
			}else {
				//Syntax error
				System.out.println("Syntax Error. _STATEMENTS");
				System.exit(0);
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
					System.out.println("Call to undefined function: "+ codeStr.get(1));
					System.exit(0);
				}
			}else if(next_token==2) {//print_ari
				System.out.println("printPRI!");
				printARI(funcName);
			}else if(next_token==4) {//ident
				System.out.println("IDENT!");
				printIdent(funcName, codeStr.get(0));
			}
		}
		else {
			System.out.println("Syntax Error. _STATEMENT");
			System.exit(0);
			//이상한 토큰이 들어옴
		}
	}
	
	public boolean checkIdentName(String name, String funcName) {
		if(name.equals("variable") || name.equals("call") || name.equals("print_ari")) {
			System.out.println("Syntax Error. 사용할 수 없는 키워드가 사용되었습니다.");
			System.exit(0);
			return false;
		}
		
		for(String funtionName : simbolTable.keySet()) {
			if(name.equals(funtionName)) {
				System.out.println("Duplicate declaration of the identifier or the function name: "+
						name + "/"+ funcName);
				System.exit(0);
				return false;
			}
		}
		for(int iter=ariStack.size()-1; iter>=0; iter--) {
			if("Dynamic Link".equals(ariStack.get(iter).split(":")[0])) {
				break;
			}
			if(name.equals(ariStack.get(iter).split(":")[1])){
				//같은 함수 내에서 이름이 같거나 
				System.out.println("Duplicate declaration of the identifier: "+ name);
				System.exit(0);
				return false;
			}
		}
		for(int iter=ariStack.size()-1; iter>=0; iter--) {
			if(ariStack.get(iter).split(":")[0].equals("Return Address")){
				System.out.println(ariStack.get(iter).split(":")[1]);
				if(ariStack.get(iter).split(":")[1].equals(name)) {
					//함수 이름이랑 변수 명이 같으면 에러 
					System.out.println("Duplicate declaration of the identifier or the function name: "+
							name + "/"+ funcName);
					System.exit(0);
					return false;
				}
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
		String functionName = nowFunctionName;
		String print = functionName+": "+ariStack.get(ariStack.size()-1)+"\n";
		for(int iter=ariStack.size()-2; iter>=0 ;iter--){
			if(ariStack.get(iter).split(":")[0].equals("Return Address")) {
				functionName = ariStack.get(iter).split(":")[1];
				print = print + functionName+": "+ariStack.get(iter);
			}else {
				print = print + "\t" +ariStack.get(iter);
			}
			print = print + "\n";
		}
		printStack.add(print);
	}
	public void printIdent(String funcName, String name) {
		int linkCount = 0;
		int localOffset = 0;
		int currentIndex=0;
		int returnIndex = 0;
		boolean isVariable = false;
		for(int iter=ariStack.size()-1; iter>=0; iter--) {
			if(ariStack.get(iter).split(":")[1].equals(name)) {
				currentIndex = iter;
				isVariable = true;
				for(int localIter=iter;localIter>=0;localIter--) {
					if(ariStack.get(localIter).split(":")[0].equals("Return Address")) {
						returnIndex = localIter;
						break;
					}
				}
				localOffset = currentIndex - returnIndex;
				break;
			}else if(ariStack.get(iter).split(":")[0].equals("Return Address")) {
				linkCount++;
			}
		}
		if(isVariable) {
			String print=funcName+":"+name+" => "+ linkCount + ","+localOffset;
			printStack.add(print);
		}else {
			System.out.println("Undefined variable used: "+name+"/"+funcName);
			System.exit(0);
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
			if(ariStack.get(iter).split(":")[0].equals("Return Address")) {
				address = iter;
				break;
			}
		}
		return Integer.toString(address);
	}
	
	public void cleanAriStack() {//한 function에 해당하는 aristack 을 날려버림
		for(int iter=ariStack.size()-1; iter>=0; iter--) {
			if(ariStack.get(iter).split(":")[0].equals("Return Address")) {
				// Return Address 제거 하고 반복문 제거
				ariStack.remove(iter);
				break;
			}
			ariStack.remove(iter);
		}
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
