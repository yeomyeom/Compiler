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
		try {
			String option = args[0];
			if(option.equals("-v")) {
				filename = args[1];
				// v �ɼ��� �������� ��ū�� �ָ��� ���
				for(String token : lexical(filename)) {
					System.out.println(token);
				}
			}else {
				filename = option;
				// ������ �´��� üũ�ϰ� simbol ���̺� �����
				ArrayList<String> token_list = lexical(filename);
			}
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			// java -jar Main.jar ���Ŀ� ���� �߸� �Է��ϸ� �����޽��� ���
			System.out.println("args Error");
		}
	}
	public static ArrayList<String> lexical(String file_name){
		try {
			// open txt file
			File f = new File(file_name);
			Scanner s = new Scanner(f);
			ArrayList<String> code = new ArrayList<String>();// �ڵ� ��������
			ArrayList<String> word = new ArrayList<String>();// �ڵ� ���� ����
			ArrayList<String> token_string = new ArrayList<String>();// �ڵ带 ��ūȭ �Ѱ�
			while(s.hasNextLine()) {
				code.add(s.nextLine());
			}
			for (String line : code) {
				String[] lines = line.split(" ");
				for (String words : lines) {
					word.add(words);
				}
			}
			token_string = tokenize(word);
			s.close();
			return token_string;
		}catch(FileNotFoundException e) {
			System.out.println("file");
			return null;
		}catch(IOException e) {
			System.out.println("text file encoding wrong");
			return null;
		}
	}
	public static ArrayList<String> tokenize(ArrayList<String> input) {
		ArrayList<String> patterns = new ArrayList<String>();
		ArrayList<String> output = new ArrayList<String>();
		patterns.add("^[0-9]*$"); //CONST decimal numbers 
		patterns.add("^[a-zA-Z_][0-9a-zA-Z_]*$"); //IDNET
		patterns.add(":="); //ASSIGNMENT_OP
		patterns.add(";"); //SEMI_COLON
		patterns.add("\\+|\\-"); // ADD_OPERATOR
		patterns.add("\\*|/"); // MULT_OPERATOR
		patterns.add("\\("); // LEFT_PATEN
		patterns.add("\\)"); // RIGHT_PATEN
		for(int i = 0 ; i<input.size() ; i++) {
			for(int p = 0; p<patterns.size(); p++) {
				if(Pattern.matches(patterns.get(p), input.get(i))) {
					if(p==0) {
						output.add("CONST");
					}else if(p==1) {
						output.add("IDENT");
					}else if(p==2) {
						output.add("ASSIGNMENT_OP");
					}else if(p==3) {
						output.add("SEMI_COLON");
					}else if(p==4) {
						output.add("ADD_OPERATOR");
					}else if(p==5) {
						output.add("MULT_OPERATOR");
					}else if(p==6) {
						output.add("LEFT_PATEN");
					}else if(p==7) {
						output.add("RIGHT_PATEN");
					}else {
						output.add("ERROR");
					}
				}
			}
		}
		return output;
	}
}
