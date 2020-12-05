package assinmenet;
import java.io.File;
import java.util.Scanner;
import java.util.regex.Pattern;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	public static void main(String[] args) {
		String filename;
		LexicalAnalyzer la;
		try {
			//String option = args[0];
			//filename = option;
			filename = "eval1.txt";
			try {
				File f = new File(filename);
				Scanner s = new Scanner(f);
				ArrayList<String> code = codeList(s);
				s.close();
				//for(int i=0; i<code.size(); i++) {
				//	System.out.println(code.get(i));
				//}
				la = new LexicalAnalyzer(code);
				la.START();
				//규칙에 맞게 정의되어 있는지 확인 하는 절차
			}catch(FileNotFoundException e) {
				System.out.println("file");
			}catch(@SuppressWarnings("hiding") IOException e) {
				System.out.println("text file encoding wrong");
			}
			// token_list 에 있는 애들이 문법에 적합한지 확인
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			// java -jar Main.jar 이후에 인자 잘못 입력하면 에러메시지 출력
			System.out.println("args Error");
		}
	}
	public static ArrayList<String> codeList(Scanner s){
		ArrayList<String> code = new ArrayList<String>();// 코드 한줄한줄
		ArrayList<String> word = new ArrayList<String>();// 코드 띄어쓰기 기준
		while(s.hasNextLine()) {
			code.add(s.nextLine());
		}
		for (String line : code) {
			String[] lines = line.split(" ");
			for (String words : lines) {
				words = words.replace("\t", "");
				if(Pattern.matches("^[a-zA-Z_][0-9a-zA-Z_]*,$",words)) {
					//콤마랑 붙어 있으면 띄어 놓기
					words = words.split(",")[0];
					word.add(words);
					word.add(",");
				}else if(Pattern.matches("^[a-zA-Z_][0-9a-zA-Z_]*;$", words)) {
					//세미콜론이랑 붙어 있으면 띄어 놓기
					words = words.split(";")[0];
					word.add(words);
					word.add(";");
				}else {
					word.add(words);
				}
			}
		}
		return word;
	}
}
