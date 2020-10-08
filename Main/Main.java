import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.util.Scanner;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {
	public static void main(String[] args) {
		String filename;
		LexicalAnalyzer la;
		try {
			String option = args[0];
			if(option.equals("-v")) {
				filename = args[1];
				// v 옵션이 눌렸을때 토큰들 주르륵 출력
				try {
					File f = new File(filename);
					Scanner s = new Scanner(f);
					ArrayList<String> code = codeList(s); // 코드 띄어쓰기 단위로 나눔
					s.close();
					la = new LexicalAnalyzer(code);
					la.lexical();
				}catch(FileNotFoundException e) {
					System.out.println("file");
				}catch(IOException e) {
					System.out.println("text file encoding wrong");
				}
			}else {
				filename = option;
				try {
					File f = new File(filename);
					Scanner s = new Scanner(f);
					ArrayList<String> code = codeList(s);
					s.close();
					la = new LexicalAnalyzer(code);
					la.PROGRAMS();
					//규칙에 맞게 정의되어 있는지 확인 하는 절차
				}catch(FileNotFoundException e) {
					System.out.println("file");
				}catch(IOException e) {
					System.out.println("text file encoding wrong");
				}
				// token_list 에 있는 애들이 문법에 적합한지 확인
			}
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
				word.add(words);
			}
		}
		return word;
	}

}
