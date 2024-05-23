package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.IOException;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

import application.RecursiveDescentParser;

// Add Mockito extension
@ExtendWith(MockitoExtension.class)
class RecursiveDescentParserTest {
	
	// Define variables
	private FileInputStream file;
	
	@BeforeEach
	public void setUp() { 
		// Mock the file object
		file = mock(FileInputStream.class);
		RecursiveDescentParser.setInputStream(file);
		
		// Reset the nextToken
		RecursiveDescentParser.nextToken = 0;
	}
	
	// Non-void methods
	//<-----getLex() tests-----
	@Test
	void getLexShouldPrintWholeLexAsString() {
		char[] lexeme = {'p','r','o','g','r','a','m','b','e','g','i','n','f','=','0','e','n','d'};
		RecursiveDescentParser.setLex(lexeme);
		assertEquals("programbeginf=0end", RecursiveDescentParser.getLex());
	}
	
	//<-----lookup() tests-----
	@Test
	void lookupShouldReturnNextTokenLeftParenthesis() {
		int token = RecursiveDescentParser.lookup('(');
		
		assertEquals(RecursiveDescentParser.LEFT_PAREN, token);
		assertEquals(RecursiveDescentParser.LEFT_PAREN, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenAdd() {
		int token = RecursiveDescentParser.lookup('+');
		
		assertEquals(RecursiveDescentParser.ADD_OP, token);
		assertEquals(RecursiveDescentParser.ADD_OP, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenSub() {
		int token = RecursiveDescentParser.lookup('-');
		
		assertEquals(RecursiveDescentParser.SUB_OP, token);
		assertEquals(RecursiveDescentParser.SUB_OP, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenMult() {
		int token = RecursiveDescentParser.lookup('*');
		
		assertEquals(RecursiveDescentParser.MULT_OP, token);
		assertEquals(RecursiveDescentParser.MULT_OP, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenDiv() {
		int token = RecursiveDescentParser.lookup('/');
		
		assertEquals(RecursiveDescentParser.DIV_OP, token);
		assertEquals(RecursiveDescentParser.DIV_OP, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenSemi() {
		int token = RecursiveDescentParser.lookup(';');
		
		assertEquals(RecursiveDescentParser.SEMI, token);
		assertEquals(RecursiveDescentParser.SEMI, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenAssign() {
		int token = RecursiveDescentParser.lookup('=');
		
		assertEquals(RecursiveDescentParser.ASSIGN, token);
		assertEquals(RecursiveDescentParser.ASSIGN, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenGreater() {
		int token = RecursiveDescentParser.lookup('>');
		
		assertEquals(RecursiveDescentParser.GREATER, token);
		assertEquals(RecursiveDescentParser.GREATER, RecursiveDescentParser.nextToken);
	}
	
	@Test
	void lookupShouldReturnNextTokenLess() {
		int token = RecursiveDescentParser.lookup('<');
		
		assertEquals(RecursiveDescentParser.LESS, token);
		assertEquals(RecursiveDescentParser.LESS, RecursiveDescentParser.nextToken);
	}
	
	//<-----lex() tests-----
	// Test whether the constants are correctly defined and the nextToken variable is set to correct constant
	@Test
	void lexShouldReturnLetter() {
		char chClass = RecursiveDescentParser.LETTER;
		
		assertEquals(RecursiveDescentParser.LETTER, chClass);
		assertEquals(RecursiveDescentParser.LETTER, RecursiveDescentParser.nextToken);
	}
	
	
	// Void methods tested using Mockito
	//<-----getChar() tests-----
	@Test
	void getCharVoidLetter() throws IOException {
		when(file.read()).thenReturn((int) 'a');
		
		RecursiveDescentParser.getChar();
		
		assertEquals('a', RecursiveDescentParser.nextChar);
		assertEquals(RecursiveDescentParser.LETTER, RecursiveDescentParser.charClass);
	}
	
	@Test
	void getCharVoidDigit() throws IOException {
		when(file.read()).thenReturn((int) '3');
		
		RecursiveDescentParser.getChar();
		
		assertEquals('3', RecursiveDescentParser.nextChar);
		assertEquals(RecursiveDescentParser.DIGIT, RecursiveDescentParser.charClass);
	}
	
	@Test
	void getCharVoidOther() throws IOException {
		when(file.read()).thenReturn((int) '=');
		
		RecursiveDescentParser.getChar();
		
		assertEquals('=', RecursiveDescentParser.nextChar);
		assertEquals(RecursiveDescentParser.OTHER, RecursiveDescentParser.charClass);
	}
	

}
