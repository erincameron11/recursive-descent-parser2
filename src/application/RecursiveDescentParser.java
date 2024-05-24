package application;

import java.io.FileInputStream;
import java.io.IOException;

public class RecursiveDescentParser {
	// Default no-arg constructor
	public RecursiveDescentParser() { /* For JUnit tests */ }	
	
	// Define single character variables
	public final static char ADD_OP = '+';
	public final static char SUB_OP = '-';
	public final static char MULT_OP = '*';
	public final static char DIV_OP = '/';
	public final static char LEFT_PAREN = '(';
	public final static char RIGHT_PAREN = ')';
	public final static char SEMI = ';';
	public final static char ASSIGN = '=';
	public final static char GREATER = '>';
	public final static char LESS = '<';
	
	// Character class variables
	public final static int LETTER = 0; // identifier
	public final static int DIGIT = 1; // integer constant
	public final static int OTHER = 3; // for operations, parenthesis, etc
	public final static int IDENTIFIER = 11; // for variable names
	public final static char INT_CONSTANT = 10; // for integer values
	public final static char IF_CODE = 31; // if statements
	public final static char THEN = 30; // if statements
	public final static char END = 29; // end of a program
	public final static char LOOP = 28; // loop statements
	public final static char PROGRAM = 27; // beginning of a program
	public final static char BEGIN = 26; // beginning of a program
	public final static int EOF = 99; // end of file
	
	// Declare variables
	public static int nextToken; // the next numeric char value
	public static int charClass; // the numeric class of the char
	static int lexLen; // how long a lexeme is
	public static char nextChar;
	static char lexeme[] = new char[100]; // buffer of 100
	public static FileInputStream file; // the file to be read from
	static String filename; // the filename
	static boolean hasProgramBegin = false; // set to true when "program begin" is at beginning of program
	static boolean hasError = false; // set to true when an error has occurred
	static boolean hasProgram = false; // set to true when program keyword present
	static int lineNumber;
	
	// Constructor
	public RecursiveDescentParser(String filename) {
		this.filename = filename;
	}
	
	
	//<-------------------------------PROGRAM--------------------------------
	static void program() {	
		// Get the first lexeme
		lex();
		lineNumber = 1;
		
		// Look for the first keyword program
		if(nextToken == PROGRAM) {
			// Set the boolean
			hasProgram = true;
			// Pass the program keyword
			lex();
		} else {
			// Display the error
			error("Keyword Error", "\'program\' keyword expected");
			
			// Continue to see if there are more errors in the program
		}
		
		// Look for the second keyword begin
		if(nextToken == BEGIN) {
			// Pass the begin keyword
			lex();
			
			// If the program keyword exists
			if(hasProgram) {
				// Set the hasProgramBegin boolean to true
				hasProgramBegin = true;
			}
			
			// Then call the statement_list function
			statement_list();
		} else {
			// Display the error
			error("Keyword Error", "\'begin\' keyword expected");
			
			// Continue to see if there are more errors in the program
			statement_list();
		}
		
		// Look for the end keyword if there have been no errors
		if(nextToken != END && !hasError) {
			error("Keyword Error", "\'end\' keyword expected");
		}
	}
	
	
	//<-------------------------------STATEMENT_LIST--------------------------------
	static void statement_list() {
		// Parse the first statement
		statement();
		
		// Loop through until the next token is not a ; and statement
		while(nextToken == SEMI) {
			// Pass the semicolon
			lex();
			
			// If end is called after a semicolon
			// A statement that has 'end' as the next token is incorrect (because then the 
			// file can end with a semicolon -- which shouldn't be allowed)
			if(nextToken == END) {
				error("Syntax Error", "Unnecessary \';\' found at the end of the statement");
			}
			
			// Call the statement function
			statement();
		}
		
		if(nextToken != END) {
			error("Syntax Error", "Expected \';\' at the end of the statement");
		}
	}
	
	
	//<-------------------------------STATEMENT--------------------------------
	static void statement() {
		// calls the assignment_statement, if_statement, or loop_statement
		
		if(nextToken == IF_CODE) {
			if_statement();
		} else if(nextToken == LOOP) {
			loop_statement();
		} else if(nextToken == IDENTIFIER){
			assignment_statement();
		} 		
	}
	
	
	//<-------------------------------ASSIGNMENT_STATEMENT--------------------------------
	static void assignment_statement() {
		// reduces the statement to <variable> = <expression>
		
		// Call the variable function
		variable();
		
		if(nextToken != ASSIGN) {
			error("Syntax Error", "Expected \'=\' in assignment statement");
		} else {
			// Pass the assign operator
			lex();
			
			// Call the expression function
			expression();
		}
	}
	
	
	//<-------------------------------IF_STATEMENT--------------------------------
	static void if_statement() {
		// reduces the statement to if (<logic_expression>) then <statement>
		
		// Ensuring that the first token is an 'if'
		if(nextToken != IF_CODE) {
			error("Keyword Error", "\'if\' keyword expected");
			
		// Otherwise, we have an 'if', get the next token
		} else {
			// Pass the if keyword
			lex();
			
			// Look for the left parenthesis
			if(nextToken != LEFT_PAREN) {
				error("Syntax Error", "Mismatched parenthesis: Expected \'(\' in if statement");
			} else {
				// Pass the left parenthesis
				lex();
				
				// Call the function for logic_expression in the if brackets
				logic_expression();
				
				if(nextToken != RIGHT_PAREN) {
					error("Syntax Error", "Mismatched parenthesis: Expected \')\' in if statement");
				} else {
					// Pass the right parenthesis
					lex();
					
					// Look for the 'then' keyword
					if(nextToken != THEN) {
						error("Keyword Error", "\'then\' keyword expected at the end of the if statement");
					} else {
						// Pass the 'then' keyword
						lex();
						
						// Call the statement function
						statement();
					}
				}
			}
		}
	}
	
	
	//<-------------------------------LOOP_STATEMENT--------------------------------
	static void loop_statement() {
		// reduces the statement to loop (<logic_expression>) <statement>
		
		if(nextToken != LOOP) {
			error("Keyword Error", "\'loop\' keyword expected in loop statement");
		} else {
			// Pass the loop keyword
			lex();
			
			// Look for the left parenthesis
			if(nextToken != LEFT_PAREN) {
				error("Syntax Error", "Mismatched parenthesis: Expected \'(\' in loop statement");
			} else {
				// Pass the left parenthesis
				lex();
				
				// Call the logic_expression function
				logic_expression();
				
				// Look for the right parenthesis
				if(nextToken != RIGHT_PAREN) {
					error("Syntax Error", "Mismatched parenthesis: Expected \')\' in loop statement");
				} else {
					// Pass the right parenthesis
					lex();
					
					// Call the statement function
					statement();
				}
			}
		}
	}
	
	
	//<-------------------------------VARIABLE--------------------------------
	static void variable() {
		// reduces the statement to an identifier
		
		// Retrieve the next token
		lex();
	}
	
	
	//<-------------------------------EXPRESSION--------------------------------
	static void expression() {
		// reduces the statement to <term> {(+ | -) <term>}
		
		// Parse the first term
		term();
		
		// Loop through until the next token is not a + or -
		while(nextToken == ADD_OP || nextToken == SUB_OP) {
			lex();
			term();
		}
	}
	
	
	//<-------------------------------TERM--------------------------------
	static void term() {
		// reduces the statement to <factor> {(*|/) <factor>}
		
		// Parse the first factor
		factor();
		
		// Loop through until the next token is not a * or /
		while(nextToken == MULT_OP || nextToken == DIV_OP) {
			lex();
			factor();
		}
	}
	
	
	//<-------------------------------FACTOR-------------------------------- 
	static void factor() {
		// reduces the statement to identifier | int_constant | (expr)
		
		// If the next token is an integer literal or identifier
		if(nextToken == IDENTIFIER || nextToken == INT_CONSTANT) {
			// Then we need to locate the next token in the stream
			lex();
		} else if(nextToken == END) {
			error("Syntax Error", "Expected expression in assignment statement");
		
			
		// Otherwise, the token is  ( <expression> ) we need to 
		// check for the left parenthesis, expression in middle, and
		// right parenthesis
		} else {
			if(nextToken == LEFT_PAREN) {
				// Call lex to pass by the left parenthesis
				lex();
				
				// Call the expression function for the middle expression
				expression();
				
				// Then we should have a right parenthesis to close it off
				// if not, then there is an error
				if(nextToken == RIGHT_PAREN) {
					lex();
				} else {
					error("Syntax Error", "Mismatched parenthesis: Expected \')\' in factor expression");
				} 
			} else {
				error("Syntax Error", "Mismatched parenthesis: Expected \'(\' in factor expression");
			}
		}
	}
		
	
	//<-------------------------------LOGIC_EXPRESSION--------------------------------
	static void logic_expression() {
		// reduces the statement to <variable> (<|>) <variable>
		
		// Parse the first variable
		variable();
		
		// Looking for the next token to be a > or <
		if(nextToken == GREATER || nextToken == LESS) {
			// Pass the character by
			lex();
			
		// Otherwise, there is an error
		} else {
			error("Syntax Error", "Expected comparison operator in logic expression statement");
		}
		
		// Parse the second variable
		variable();
	}
	
	
	//<-------------------------------LEX--------------------------------
	public static int lex() {
		// Pass any whitespace or newline characters
		getNonBlank();
		
		// Reset the lexeme buffer each run
		lexeme = new char[100];
		lexLen = 0;
		
		switch(charClass) {
			/* -----LETTER CHARCLASS----- */
			case LETTER: 
				addChar();
				getChar();
				
				// Loop through until a non-letter or non-digit appear
				while(charClass == LETTER || charClass == DIGIT) {
					addChar();
					getChar();
				}
				
				// Get the lexeme value and trim the leading or trailing whitespaces
				String lex = getLex().trim();
				
				// Check if the phrase is any of the keywords
				if("program".equals(lex)) {
					addChar();
					nextToken = PROGRAM;
				} else if("begin".equals(lex)) {
					addChar();
					nextToken = BEGIN;
				} else if("if".equals(lex)) {
					addChar();
					nextToken = IF_CODE;
				} else if("end".equals(lex)) {
					addChar();
					nextToken = END;
				} else if("loop".equals(lex)) {
					addChar();
					nextToken = LOOP;
				} else if("then".equals(lex)) {
					addChar();
					nextToken = THEN;
				} else {
					nextToken = IDENTIFIER;
				}
				
				break;
				
			/* -----DIGIT CHARCLASS----- */
			case DIGIT:
				addChar();
				getChar();
				
				// Loop through until the next char is not a digit
				while(charClass == DIGIT) {
					addChar();
					getChar();
				}
				nextToken = INT_CONSTANT;
				break;
				
			/* -----OTHER CHARCLASS----- */
			case OTHER:
				lookup(nextChar);
				getChar();
				break;
				
			/* -----EOF CHARCLASS----- */
			case EOF:
				nextToken = EOF;
				lexeme[0] = 'E';
				lexeme[1] = 'O';
				lexeme[2] = 'F';
				lexeme[3] = 0;
				break;
		}
		
		return nextToken;
	}
	
	
	//<-------------------------------LOOKUP--------------------------------
	// To determine the code for single special characters
	public static int lookup(char c) {
		switch(c) {
			case '(':
				addChar();
				nextToken = LEFT_PAREN;
				break;
			case ')':
				addChar();
				nextToken = RIGHT_PAREN;
				break;
			case '+':
				addChar();
				nextToken = ADD_OP;
				break;
			case '-':
				addChar();
				nextToken = SUB_OP;
				break;
			case '*':
				addChar();
				nextToken = MULT_OP;
				break;
			case '/':
				addChar();
				nextToken = DIV_OP;
				break;
			case ';':
				addChar();
				nextToken = SEMI;
				break;
			case '=':
				addChar();
				nextToken = ASSIGN;
				break;
			case '>':
				addChar();
				nextToken = GREATER;
				break;
			case '<':
				addChar();
				nextToken = LESS;
				break;
			default:
				addChar();
				nextToken = EOF;
				break;
		}
		return nextToken;
	}
	
	
	//<-------------------------------GETNONBLANK--------------------------------
	static void getNonBlank() {
		// While the next input is a whitespace, continue past it
		while(Character.isWhitespace(nextChar) || nextChar == '\n') {
			// Increase the line number
			if(nextChar == '\n') {
				lineNumber++;
			}
			getChar();
		}
	}
	
	
	//<-------------------------------ADDCHAR--------------------------------
	public static void addChar() {
		if(lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} else {
			error("Overflow Error", "Lexeme " + getLex() + " too long");
		}
	}
	
	
	//<-------------------------------GETCHAR--------------------------------
	public static void getChar() {
		// If it is not EOF
		try {
			// Reads a single byte from the input stream
			if((nextChar = (char)file.read()) != EOF) {
				// If it is an alphabetic character
				if(Character.isAlphabetic(nextChar)) {
					charClass = LETTER;
				// If it is a digit character
				} else if(Character.isDigit(nextChar)) {
					charClass = DIGIT;
				// Else if it is anything else
				} else {
					charClass = OTHER;
				}
			// If we are at the end of the file, the charClass is EOF
			} else {
				charClass = EOF;
			}
		} catch (IOException e) {
			error("File Error", "File read error");
			e.printStackTrace();
		} 
	}
	//<-----JUNIT TESTING-----
	public static void setInputStream(FileInputStream inputStream) {
        file = inputStream;
    }
	
	
	//<-------------------------------GETLEX--------------------------------
	// Method to get the String value out of the lexeme buffer array
	public static String getLex() {
		String lex = "";
		for(int i = 0; i < lexeme.length; i++) {
			lex = lex + lexeme[i];
		}
		return lex;
	}
	//<-----JUNIT TESTING-----
	public static void setLex(char[] l) {
		lexeme = l;
	}
		
	
	//<-------------------------------ERROR--------------------------------
	static void error(String errorType, String errorMsg) {
		// Set the error flag
		hasError = true;
		
		// Append the error text to the error log
		lineNumber--;
		Main.errorTA.appendText(errorType + ": line " + lineNumber + " -- " + errorMsg + "\n");
	}
		
	
	
	//<-------------------------------START--------------------------------
	// Main Method to run the program
	public void start() {		
		try {
			// Open the txt file
			file = new FileInputStream("./" + filename);
			
			// Reset the errors boolean and lineNumber
			hasError = false;
			lineNumber = 0;
		
			
			// Start the parser
			program();
			
			// If we make it to this point without any errors, display a success message
			if(!hasError) {
				Main.errorTA.setStyle("-fx-text-fill: green");
				Main.errorTA.setText("Syntax parsed without errors.");
				Main.checkmark.setVisible(true);
			}
		
		// Catch if the file does not exist
		} catch (IOException e) {
			error("File Error", "File " + filename + " does not exist.");
			e.printStackTrace();
		}
	}	
}