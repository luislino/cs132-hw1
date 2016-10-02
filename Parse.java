import java.io.BufferedReader; 
import java.io.InputStreamReader;
import java.io.IOException;
import java.lang.Exception;
class Scanner {

    public int errorLine; 
    private BufferedReader br; 
   
    public static  void  main(String [] args) {
        
        Scanner sc = new Scanner(); 
        
        Scanner.Token t = sc.getToken(); 
        while(t.type != Type.END) {
            System.out.println(typeString(t.type) + " " + t.data); 
            t = sc.getToken(); 
        }
        
    }
 
    public Scanner.Token getToken()   {
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
                        System.out.println("Parse error in line "+errorLine);
                        System.exit(0); 
                    } else {
                        if((char) c == '\n')
                            errorLine++; 
                    }
                }
          
            }
            }
             catch (Exception e) {
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
        errorLine = 1; 
    }
        
    public enum Type { NUM, BINOP, INCROP, DOLLAR, END, INVALID, LPAREN, RPAREN, CONCAT, POSTINC, PREINC};
    
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
            case CONCAT:
                s = "CONCAT"; 
                break; 
            case POSTINC:
                s = "POSTINC";
                break;  
            case PREINC:
                s = "PREINC"; 
                break; 
        }
        return s; 
    }
    public static class Token {
	Type type; 
	String data; 
        	
	public Token(Type type, String data) {
            this.type = type; 
            this.data = data; 
        }; 
    }; 




};



class Parse {

    Scanner.Token token;
    Boolean error;  
    Scanner scanner;  
    int number; 
    public static void  main(String [] args) { 
        Parse parser  = new Parse();
        System.setErr(null); 
        try { 
        parser.advance(); 
        TokenNode tree = parser.nt_start();
        printTree(tree);
        System.out.println("");
        System.out.println("Expression parsed successfully");  
        return; 
       } catch (Exception e) {
       
       }
       System.out.println("Parse error in line " + parser.scanner.errorLine); 
    } 

    public class TokenNode {
        Scanner.Token token; 
        TokenNode op1; 
        TokenNode op2;
        public TokenNode(Scanner.Token t, TokenNode o1, TokenNode o2) {
            this.token = t; 
            this.op1 = o1;
            this.op2 = o2; 
        }
         
    }; 


    TokenNode nt_start()  {
        TokenNode result = null;  
        switch(token.type) {
            case INCROP:
            case DOLLAR:
            case LPAREN:
            case NUM:
                result =  nt_string(); eat(Scanner.Type.END);
                break;
            default:
                error();
 
         
                return result; 
        }
        return result; 
    } 

    TokenNode nt_string()  {
        TokenNode first, rest;
        first = rest = null;  
        switch(token.type) {
            case INCROP:
            case DOLLAR:
            case LPAREN:
            case NUM:
                first = nt_preIncTerm();
                rest = nt_moreTerms(); 
           
                if(rest == null) {
                    return first; 
                }
                else {

                    TokenNode cursor = rest; 
                    Scanner.Type ty = cursor.token.type;
                    while(true) {
                        if((ty == Scanner.Type.BINOP || ty == Scanner.Type.CONCAT) && cursor.op1 != null && (cursor.op1.token.type == Scanner.Type.BINOP || cursor.op1.token.type == Scanner.Type.CONCAT)) {
                            cursor = cursor.op1; 
                            ty = cursor.token.type; 
                        } 
                        else {
                            break;
                        }
                    }
                    cursor.op1 = first; 
                    return rest; 
                }
             default:
                error(); 
             
        }
        return null;
    }

    TokenNode nt_preIncTerm()  { 
        TokenNode first, second;
        switch(token.type) {
            case INCROP:
                first = nt_incrop(); second = nt_preIncTerm();
                first.op1 = second; 
                first.token.data += "_"; 
                return first; 
             case DOLLAR:
             case LPAREN:
             case NUM:
                 first = nt_postIncTerm();
                 return first; 
          
               
             default: 
                 error(); 
       }
       return null;
    } 


    TokenNode nt_moreTerms() {
        TokenNode first, second,third; 
        switch(token.type) {
            case BINOP:
                first = nt_binop(); second = nt_preIncTerm(); third = nt_moreTerms(); 
                if(third == null) {
                    first.op2 = second; 
                    return first; 
                }
                else { 
 
                    first.op2 = second; 
                    TokenNode cursor = third; 
                    Scanner.Type ty = cursor.token.type; 
                    while(true) {
                        if((ty == Scanner.Type.BINOP || ty == Scanner.Type.CONCAT) && cursor.op1 != null && (cursor.op1.token.type == Scanner.Type.BINOP || cursor.op1.token.type == Scanner.Type.CONCAT)) {
                            cursor = cursor.op1; 
                            ty = cursor.token.type; 
                        } 
                        else {
                            break;
                        }
                    }
                    cursor.op1 = first; 
                    return third;
                } 
            case RPAREN:
            case END: 
                return null;     
            case DOLLAR:
            case LPAREN:
            case NUM:
                first = nt_postIncTerm(); second = nt_moreTerms(); 
                if(second == null) {
                    return new TokenNode(new Scanner.Token(Scanner.Type.CONCAT, "_"), null, first);
                }
                else {
                    TokenNode newNode = new TokenNode(new Scanner.Token(Scanner.Type.CONCAT, "_"), null, first); 

                    TokenNode cursor = second; 
                    Scanner.Type ty = cursor.token.type; 
                    while(true) {
                        if((ty == Scanner.Type.BINOP || ty == Scanner.Type.CONCAT) && cursor.op1 != null && (cursor.op1.token.type == Scanner.Type.BINOP || cursor.op1.token.type == Scanner.Type.CONCAT)) {
                            cursor = cursor.op1; 
                            ty = cursor.token.type; 
                        } 
                        else {
                            break;
                        }
                    }
                    cursor.op1 = newNode; 
                    return second; 

                     
                }
           default:
               error();  
            
        }
        return null;    
    }

    TokenNode nt_postIncTerm()  { 
        TokenNode  first, second; 
        first = second = null; 
        switch(token.type) {
            case DOLLAR:
            case LPAREN: 
            case NUM:
                first = nt_term();
                second = nt_postInc();
                if(second == null) {
                    return first; 
                }
                else {
                    TokenNode cursor = second;
                    while(cursor.op1 != null){ 
                        cursor = cursor.op1;
                    }
                    cursor.op1 = first;
                    return second;
                 }
            default:
                error(); 
        }
        return null;     

    }


    TokenNode nt_term()  {
        TokenNode result  = null;	
        switch(token.type) {
            case DOLLAR: 
                result = nt_lvalue(); 
                break;
            case LPAREN:
                result = nt_grouping(); 
                break;
            case NUM:
                result = nt_num(); 
                break; 
            default:
                error(); 
        }
        return result;    
        
    }

    TokenNode nt_postInc()  {
        TokenNode first, second; 
        switch(token.type) {
            case INCROP:
                first = nt_incrop(); second = nt_postInc(); 
                first.token.data = "_" + first.token.data; 
                if(second == null) {
                    return first; 
                } 
                else {
                   TokenNode cursor = second; 
                    while(cursor.op1 != null) {
                        cursor = cursor.op1; 
                    }
                    cursor.op1 = first; 
                    return second; 
                }
            case BINOP:
            case DOLLAR:
            case LPAREN:
            case RPAREN:
            case END: 
            case NUM:
                return null;
        }
        return null; 
    }
        


    TokenNode nt_incrop()  {
        if(token.type == Scanner.Type.INCROP) {
            Scanner.Token tok = new Scanner.Token(Scanner.Type.INVALID, ""); 
            tok.type = Scanner.Type.INCROP; 
            tok.data = token.data; 
            TokenNode result =  new TokenNode(tok, null, null); 
            eat(Scanner.Type.INCROP); 
            return result; 
        }
        else {
            error(); 
            return null;  
        }   
                         
                

    }


    TokenNode nt_binop()  {
        if(token.type == Scanner.Type.BINOP) {
            Scanner.Token tok = new Scanner.Token(Scanner.Type.BINOP, token.data); 
            TokenNode result = new TokenNode(tok, null, null);
            eat(Scanner.Type.BINOP); 
            return result; 
        }
        else {
            error(); 
            return null;
        }
    }

   TokenNode nt_num()  {
       if(token.type == Scanner.Type.NUM) {
           Scanner.Token tok = new Scanner.Token(Scanner.Type.NUM, token.data);
           TokenNode result = new TokenNode(tok, null, null); 
           eat(Scanner.Type.NUM); 
           return result; 
        }
        else {
            error();
            return null; 
        }
    }

    TokenNode nt_lvalue()  { 
        if(token.type == Scanner.Type.DOLLAR) {
            eat(Scanner.Type.DOLLAR); 
            TokenNode first, second; 
            second = nt_term(); 
            Scanner.Token tok = new Scanner.Token(Scanner.Type.DOLLAR, "$");  
            first = new TokenNode(tok, second, null); 
            return first;    
        }
        else {
            error(); 
            return null; 
        }
    }

    TokenNode nt_grouping()  {
        if(token.type == Scanner.Type.LPAREN) {
            TokenNode first; 
            eat(Scanner.Type.LPAREN);
            first = nt_string(); 
            eat(Scanner.Type.RPAREN); 
            return first;  
        } 
        else {
            error(); 
            return null;
        }
    }

    
             
    void advance()   {token = scanner.getToken(); 
    }
 
    void eat(Scanner.Type t)  {
        if(token.type == t) {
            advance();
        }
        else {
            error(); 
        }
    }

    
    
    void error()  {
        error = true; 
        System.out.println("Parse error in line " + scanner.errorLine);
        System.exit(0); 
    }

    public Parse() {
        scanner = new Scanner();
        error = false;  
    }

    static void printTree(TokenNode tnode) { 
        switch(tnode.token.type) {
            case NUM:
                System.out.print(" " + tnode.token.data); 
                break;
            case INCROP:
            case PREINC:
            case POSTINC:
            case DOLLAR:
                printTree(tnode.op1);
                System.out.print(" " + tnode.token.data);
                break;
            case BINOP:
            case CONCAT:
                printTree(tnode.op1);
                printTree(tnode.op2);
                System.out.print(" " + tnode.token.data); 
                break;
         
            
        }
    }
            
}; 







