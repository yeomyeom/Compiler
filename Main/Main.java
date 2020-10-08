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
				// v �ɼ��� �������� ��ū�� �ָ��� ���
				try {
					File f = new File(filename);
					Scanner s = new Scanner(f);
					ArrayList<String> code = codeList(s); // �ڵ� ���� ������ ����
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
					//��Ģ�� �°� ���ǵǾ� �ִ��� Ȯ�� �ϴ� ����
				}catch(FileNotFoundException e) {
					System.out.println("file");
				}catch(IOException e) {
					System.out.println("text file encoding wrong");
				}
				// token_list �� �ִ� �ֵ��� ������ �������� Ȯ��
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			// java -jar Main.jar ���Ŀ� ���� �߸� �Է��ϸ� �����޽��� ���
			System.out.println("args Error");
		}
	}
	public static ArrayList<String> codeList(Scanner s){
		ArrayList<String> code = new ArrayList<String>();// �ڵ� ��������
		ArrayList<String> word = new ArrayList<String>();// �ڵ� ���� ����
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
