import java.io.File;
import java.io.FileReader;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class Main {

	public static void main(String[] args) {
		
		try {
			String option = args[0];
			String filename;
			if(option.equals("-v")) {
				filename = args[1];
				// open txt file
				System.out.println("-v option selected");
			}
			else {
				filename = option;
				try {
					// open txt file
					File f = new File(filename);
					FileReader f_read = new FileReader(f);
					BufferedReader buf = new BufferedReader(f_read);
					String l;
					ArrayList<String> words = new ArrayList<String>();
					ArrayList<String> words_token = new ArrayList<String>();
					while((l = buf.readLine()) != null) {
						words = new ArrayList<String>();
						words_token = new ArrayList<String>();
						words = splitWord(l);
						//for TEST print line by line
						System.out.println("\n===WORDS===");
						for(int i=0; i<words.size(); i++) {
							System.out.print(words.get(i) + " ");
						}
						//for TEST print token
						System.out.println("\n===TOKEN===");
						words_token = tokenize(words);
						for(int i=0; i<words_token.size(); i++) {
							System.out.print(words_token.get(i) + " ");
						}
						//qualify statement has no error or warning
						
						//일단 정상 범주까지만 해놓고 warning 하자
					}
					buf.close();
				}catch(FileNotFoundException e) {
					System.out.println("file");
				}catch(IOException e) {
					System.out.println("text file encoding wrong");
				}
				
			}
			
		}catch(java.lang.ArrayIndexOutOfBoundsException e){
			System.out.println("File name is null");
		}
	}
	
	public static ArrayList<String> splitWord(String input) {
		ArrayList<String> words = new ArrayList<String>();
		for(String word : input.split(" ")) {
			words.add(word);
		}
		return words;
	}
	public static ArrayList<String> tokenize(ArrayList<String> input) {
		ArrayList<String> patterns = new ArrayList<String>();
		ArrayList<String> output = new ArrayList<String>();
		patterns.add("^[0-9]*$"); //CONST decimal numbers 
		patterns.add("^[a-zA-Z_][0-9a-zA-Z_]*$"); //IDNET
		patterns.add(":="); //ASSIGNMENT_OP
		patterns.add(";"); //SEMI_COLON
		patterns.add("\\+ | \\-"); // ADD_OPERATOR
		patterns.add("\\* | /"); // MULT_OPERATOR
		patterns.add("\\("); // LEFT_PATEN
		patterns.add("\\)"); // RIGHT_PATEN
		for(int i = 0 ; i<input.size() ; i++) {
			for(int p = 0; p<patterns.size(); p++) {
				if(Pattern.matches(patterns.get(p), input.get(i))) {
					if(p==0) {
						output.add("CONST");
					}else if(p==1) {
						output.add("IDNET");
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
	
	//simbol table 생성 해야함
}
