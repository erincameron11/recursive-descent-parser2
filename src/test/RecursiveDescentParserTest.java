package test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import application.RecursiveDescentParser;

// Add Mockito extension
@ExtendWith(MockitoExtension.class)
class RecursiveDescentParserTest {
	
	// Define variables
	private FileInputStream file;
    private static char nextChar;
    private static int charClass;
	
	@BeforeEach
	public void setUp() { 
		// Mock the file object
		file = mock(FileInputStream.class);
		RecursiveDescentParser.setInputStream(file);
	}
	
	// Non-void methods
	@Test
	void getLexShouldPrintWholeLexAsString() {
//		rdp = new RecursiveDescentParser();
		char[] lexeme = {'p','r','o','g','r','a','m','b','e','g','i','n','f','=','0','e','n','d'};
		RecursiveDescentParser.setLex(lexeme);
		assertEquals("programbeginf=0end", RecursiveDescentParser.getLex());
	}
	
	
	// Void methods tested using Mockito
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
	
//	@Test
//	void getCharVoidEOF() throws IOException {
//		when(file.read()).thenReturn(99);
//		
//		RecursiveDescentParser.getChar();
//		
//		assertEquals((char)99, nextChar);
//		assertEquals(RecursiveDescentParser.EOF, charClass);
//	}
	
	

}
