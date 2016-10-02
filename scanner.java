import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.io.IOException;
class Scanner {

    private BufferedReader br; 
   
    public static  void  main(String [] args) {
        Scanner sc = new Scanner(); 
        Scanner.Token t = sc.getToken(); 
        while(t.type != Type.END) {
            System.out.println(typeString(t.type) + " " + t.data); 
            t = sc.getToken(); 
        }
    }
 
    public Scanner.Token getToken() {
        int c; 
	try {
            while((c = br.read()) != -1) {
                if(isDigit((char) c)) {
                    return new Scanner.Token(Type.NUM, String.valueOf((char) c)); 
                }
                else if ((char) c == '(') {
                    return new Scanner.Token(Type.LPAREN, "(");
                }
                else if ((char) c == ')') {
                    return new Scanner.Token(Type.RPAREN, ")"); 
                }
                else if ((char) c == '$') {
                    return new Scanner.Token(Type.DOLLAR, "$"); 
                }
                else if ((char) c == '+') {
                    br.mark(10); 
                    if ((c = br.read()) != -1) {
                        if((char) c == '+') { 
                            return new Scanner.Token(Type.INCROP, "++"); 
                        }
                    } 
                    br.reset(); 
                    return new Scanner.Token(Type.BINOP, "+");  
                } 
                else if ((char) c == '-') {
                    br.mark(10); 
                    if ((c = br.read()) != -1) {
                        if((char) c == '-') {
                            return new Scanner.Token(Type.INCROP, "--"); 
                        }
                    } 
                    br.reset(); 
                    return new Scanner.Token(Type.BINOP, "-"); 
                }
                else if((char) c == '#') {
                    br.mark(10); 
                    while((c = br.read()) != -1) { 
                        if((char) c == '\n') {
                            br.reset(); 
                            break;
                        }
                        br.mark(10);              
                    }

                }
                else {
                    if(!Character.isWhitespace((char) c)) {
                        return new Scanner.Token(Type.INVALID, String.valueOf((char) c)); 
                    } 
                }
          
            }
               
                
         
        } 
        catch (Exception e) {
            System.out.println(e.toString()); 
        } 
 
        return new Scanner.Token(Type.END, ""); 
    } 
    
    private Boolean isDigit(char c) {
        switch (c) {
            case '0':
            case '1':
            case '2':
            case '3':
            case '4':
            case '5':
            case '6':
            case '7':
            case '8':
            case '9':
                return true;
            default:
                return false;  
        } 
    }
    public Scanner() { 
        br = new BufferedReader(new InputStreamReader(System.in));
    }
        
    public enum Type { NUM, BINOP, INCROP, DOLLAR, END, INVALID, LPAREN, RPAREN};
    
    public static String typeString(Type t) {
        String s = ""; 
        switch (t) {
            case NUM:
                s = "NUM";
                break;
            case BINOP:
                s = "BINOP";
                break;
            case INCROP:
                s = "INCROP";
                break; 
            case DOLLAR:
                s = "DOLLAR"; 
                break;
            case END:
                s = "END";
                break; 
            case INVALID:
                s = "INVALID"; 
                break;
            case LPAREN:
                s = "LPAREN"; 
                break;
            case RPAREN:
                s = "RPAREN";
                break;
        }
        return s; 
    }
    public class Token {
	Type type; 
	String data; 
        	
	public Token(Type type, String data) {
            this.type = type; 
            this.data = data; 
        }; 
    }; 




};
