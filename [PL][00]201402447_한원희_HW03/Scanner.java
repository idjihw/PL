import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.StringTokenizer;
import java.util.List;
import java.util.ArrayList;


public class Scanner {
	public enum TokenType{
			ID(3), INT(2);
		
			private final int finalState;
			
			TokenType(int finalState) {
				this.finalState = finalState;
			}
		}
		
		public static class Token {
			public final TokenType type;
			public final String lexme;
			Token(TokenType type, String lexme) {
				this.type = type;
				this.lexme = lexme;
			}
			@Override
			public String toString() {
				return String.format("[%s: %s]", type.toString(), lexme);
			}
		}

		private int transM[][];
		private String source;
		private StringTokenizer st;
		
		public Scanner(String source) {
			this.transM = new int[4][128];

			this.source = source == null ? "" : source;
			this.st = new StringTokenizer(this.source, " ");
			initTM();
		}
		
		private void initTM() {
		// transM[4][128] = { {...}, {...}, {...}, {...} };
		// values of entries: -1, 0, 1, 2, 3 : next state
		// TransM[0]['0'] = 2, ..., TransM[0]['9'] = 2,
		// TransM[0]['-'] = 1,
		// TransM[0]['a'] = 3, ..., TransM[0]['z'] = 3,
		// TransM[1]['0'] = 2, ..., TransM[1]['9'] = 2,
		// TransM[2]['0'] = 2, ..., TransM[1]['9'] = 2,
		// TransM[3]['A'] = 3, ..., TransM[3]['Z'] = 3,
		// TransM[3]['a'] = 3, ..., TransM[3]['z'] = 3,
		// TransM[3]['0'] = 3, ..., TransM[3]['9'] = 3,
		// ...
		// The values of the other entries are all -1.
			for(int j = 0; j < 4; j++) {
				for(int i = 0; i < 128; i++) {
					
					if(j == 0) {
						if(i >= 48 && i <= 57) 			// 아스키코드에서 숫자를 나타냄
							transM[j][i] = 2;
						else if(i == 45) 				// '-'
							transM[j][i] = 1;
						else if(i >= 65 && i <= 90) 	// 'A' ~ 'Z'
							transM[j][i] = 3;
						else if(i >= 97 && i <= 122) 	// 'a' ~ 'z'
							transM[j][i] = 3;
						else
							transM[j][i] = -1;
					}
					
					else if(j == 1 || j == 2) {			// 1일때와 2일때는 동일
						if(i >= 48 && i <= 57) 			// 숫자
							transM[j][i] = 2;
						else
							transM[j][i] = -1;
						
					}
					
					else if(j == 3) {
						if(i >= 48 && i <= 57) 			// 숫자
							transM[j][i] = 3;
						else if(i >= 65 && i <= 90) 	// 'A' ~ 'Z'
							transM[j][i] = 3;	
						else if(i >= 97 && i <= 122) 	// 'a' ~ 'z'
							transM[j][i] = 3;
						else
							transM[j][i] = -1;
					}	
				}
			}
		}
		
		private Token nextToken(){
			int stateOld = 0, stateNew;
			
			//토큰이 더 있는지 검사
			if(!st.hasMoreTokens()) return null;
			
			//그 다음 토큰을 받음
			String temp = st.nextToken();
			
			Token result = null;
			
			for(int i = 0; i<temp.length();i++){
				 //문자열의 문자를 하나씩 가져와 현재상태와 TransM를 이용하여 다음상태를 판별
				
				 //만약 입력된 문자의 상태가 reject 이면 에러메세지 출력 후 return함
				 //새로 얻은 상태를 현재 상태로 저장
				stateNew = transM[stateOld][temp.charAt(i)];	// charAt을 이용해 문자를 판별
				
				if(stateNew == -1){		// state가 -1이라면 잘못된 값
					System.out.println("Error.");
					return result;		// null을 리턴 해줍니다
				}
				
				stateOld = stateNew;	// 새로 얻은 상태를 현재 상태로
			}
			
			for (TokenType t : TokenType.values()){
				if(t.finalState == stateOld){
					result = new Token(t, temp);
					break;
				}
			}
			
			return result;
		}
		
		public List<Token> tokenize() {
			//입력으로 들어온 모든 token에 대해
			//nextToken()이용해 식별한 후 list에 추가해 반환
			
			List<Token> tokens = new ArrayList<Token>();	// 새로운 tokens list 생성
			
			Token next = this.nextToken();
				
			while(next != null){	// null이 아닐때까지 진행해 줍니다
				
				tokens.add(next);	// null이 아니라면 현재 토큰을 list에 추가
				next = this.nextToken();	// 다음 토큰으로 넘어갑니다 
				
			}
			
			return tokens;		// list 반환
		}
		
		public static void main(String[] args) throws IOException{
			FileReader fr = new FileReader("as03.txt");
			BufferedReader br = new BufferedReader(fr);
			String source = br.readLine();
			Scanner s = new Scanner(source);
			List<Token> tokens = s.tokenize();
			System.out.println(tokens);
		}
}
