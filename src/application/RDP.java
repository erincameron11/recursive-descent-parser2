package application;

import java.io.FileInputStream;
import java.io.IOException;

public class RDP {
	// Define single character variables
	final static char ADD_OP = '+';
	final static char SUB_OP = '-';
	final static char MULT_OP = '*';
	final static char DIV_OP = '/';
	final static char LEFT_PAREN = '(';
	final static char RIGHT_PAREN = ')';
	final static char SEMI = ';';
	final static char ASSIGN = '=';
	final static char GREATER = '>';
	final static char LESS = '<';
	
	// Character class variables
	final static int LETTER = 0; // identifier
	final static int DIGIT = 1; // integer constant
	final static int OTHER = 3; // for operations, parenthesis, etc
	final static int IDENTIFIER = 11; // for variable names
	final static char INT_CONSTANT = 10; // for integer values
	final static char IF_CODE = 31; // if statements
	final static char THEN = 30; // if statements
	final static char END = 29; // end of a program
	final static char LOOP = 28; // loop statements
	final static char PROGRAM = 27; // beginning of a program
	final static char BEGIN = 26; // beginning of a program
	final static int EOF = 99; // end of file
	
	// Declare variables
	static int nextToken; // the next numeric char value
	static int charClass; // the numeric class of the char
	static int lexLen; // how long a lexeme is
	static char nextChar;
	static char lexeme[] = new char[100]; // buffer of 100
	static FileInputStream file; // the file to be read from
	static String filename; // the filename
	static boolean hasProgramBegin = false; // set to true when "program begin" is at beginning of program
	static boolean hasError = false; // set to true when an error has occurred
	static boolean hasProgram = false; // set to true when program keyword present
	
	// Constructor
	public RDP(String filename) {
		this.filename = filename;
	}
	
	
	//<-------------------------------PROGRAM--------------------------------
	static void program() {	
		// Look for the first keyword program
		if(nextToken == PROGRAM) {
			// Set the boolean
			hasProgram = true;
			// Pass the program keyword
			lex();
		} else {
			// Display the error
			error("Missing program keyword from program");
			
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
			error("Missing begin keyword from program");
			
			// Continue to see if there are more errors in the program
			statement_list();
		}
		
		// Look for the end keyword if there have been no errors
		if(nextToken != END && !hasError) {
			error("Missing end keyword in program");
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
				error("Extra semicolon at the end of statement_list");
			}
			
			// Call the statement function
			statement();
		}
		
		if(nextToken != END) {
			error("Missing semicolon from statement_list");
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
			error("Missing assignment operator in assignment statement");
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
			error("Missing if keyword in if statement");
			
		// Otherwise, we have an 'if', get the next token
		} else {
			// Pass the if keyword
			lex();
			
			// Look for the left parenthesis
			if(nextToken != LEFT_PAREN) {
				error("Missing left parenthesis in if statement");
			} else {
				// Pass the left parenthesis
				lex();
				
				// Call the function for logic_expression in the if brackets
				logic_expression();
				
				if(nextToken != RIGHT_PAREN) {
					error("Missing right parenthesis in if statement");
				} else {
					// Pass the right parenthesis
					lex();
					
					// Look for the 'then' keyword
					if(nextToken != THEN) {
						error("Missing then keyword in if statement");
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
			error("Missing loop keyword in loop statement");
		} else {
			// Pass the loop keyword
			lex();
			
			// Look for the left parenthesis
			if(nextToken != LEFT_PAREN) {
				error("Missing left parenthesis in loop statement");
			} else {
				// Pass the left parenthesis
				lex();
				
				// Call the logic_expression function
				logic_expression();
				
				// Look for the right parenthesis
				if(nextToken != RIGHT_PAREN) {
					error("Missing right parenthesis in loop statement");
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
			error("Missing expression in assignment statement");
		
			
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
					error("Missing right parenthesis in factor expression");
				} 
			} else {
				error("Missing left parenthesis in factor expression");
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
			error("Missing comparison operator");
		}
		
		// Parse the second variable
		variable();
	}
	
	
	//<-------------------------------LEX--------------------------------
	static int lex() {
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
	static int lookup(char c) {
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
			getChar();
		}
	}
	
	
	//<-------------------------------ADDCHAR--------------------------------
	static void addChar() {
		if(lexLen <= 98) {
			lexeme[lexLen++] = nextChar;
			lexeme[lexLen] = 0;
		} else {
			System.out.print("Error! The lexeme has exceeded the buffer space.");
			error("Lexeme too long");
		}
	}
	
	
	//<-------------------------------GETCHAR--------------------------------
	static void getChar() {
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
			System.out.print("Error - getChar() file read error.\n");
			e.printStackTrace();
		} 
	}
	
	
	//<-------------------------------GETLEX--------------------------------
	// Method to get the String value out of the lexeme buffer array
	static String getLex() {
		String lex = "";
		for(int i = 0; i < lexeme.length; i++) {
			lex = lex + lexeme[i];
		}
		return lex;
	}
		
	
	//<-------------------------------ERROR--------------------------------
	static void error(String errorMsg) {
		// Set the error flag
		hasError = true;
		
		// Append the error text to the error log
		RecursiveDescentParser.errorTA.appendText(errorMsg + "\n");
	}
		
	
	
	//<-------------------------------START--------------------------------
	// Main Method to run the program
	public void start() {		
		try {
			// Open the txt file
			file = new FileInputStream("./" + filename);
			
			// Reset the errors boolean
			hasError = false;
			
			// Get the first lexeme
			lex();
			
			// Start the parser
			program();
			
			// If we make it to this point without any errors, display a success message
			if(!hasError) {
				RecursiveDescentParser.errorTA.setStyle("-fx-text-fill: green");
				RecursiveDescentParser.errorTA.setText("Syntax parsed without errors.");
				RecursiveDescentParser.checkmark.setVisible(true);
			}
		
		// Catch if the file does not exist
		} catch (IOException e) {
			System.out.print("File: " + filename + " does not exist.\n");
			e.printStackTrace();
		}
	}	
}