package org.interlis2.validator;

import static org.junit.Assert.*;
import org.interlis2.validator.gui.MainFrame;
import org.junit.Test;

public class SplitFilenamesTest {
	
	// Testet ob ein mehrzeiliger Text unterteilt wird (Normalfall)
	@Test
	public void splitFiles_Ok() {
		String listOfFiles ="test/data/multipleinputfiles/TestFileC1.xtf"+"\n"
							+"test/data/multipleinputfiles/TestFileC2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test/data/multipleinputfiles/TestFileC1.xtf",files[0]);
		assertEquals("test/data/multipleinputfiles/TestFileC2.xtf",files[1]);
	}
	
	// Testet ob Leerzeichen am Anfang und Ende entfernt werden
	@Test
	public void splitLineWithSpaces_Ok() {
		String listOfFiles =" TestFileC1.xtf "+"\n"
							+" TestFileC2.xtf ";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Testet ob leere Zeilen ignoriert werden
	@Test
	public void splitFilesEmptyLine_Ok() {
		String listOfFiles ="TestFileC1.xtf"+"\n"
							+ "\n"
							+"TestFileC2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Testet ob Leerzeichen innerhab der Zeilen erhalten bleiben
	@Test
	public void splitFilesWithSpaces_Ok() {
		String listOfFiles ="TestFile C1.xtf"+"\n"
							+"TestFile C2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFile C1.xtf",files[0]);
		assertEquals("TestFile C2.xtf",files[1]);
	}
	
	// Testet ob eine Zeile, die nur Leerzeichen enthaelt, ignoriert wird
	@Test
	public void splitFilesLineWithOnlySpaces_Ok() {
		String listOfFiles =" TestFileC1.xtf "+"\n"
							+ "   \n"
							+" TestFileC2.xtf ";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Testet of Zeilenumbruch nach der letzten Zeile ignoriert wird
	@Test
	public void splitFilesNewLineAtEnd_Ok() {
		String listOfFiles ="TestFile C1.xtf"+"\n"
							+"TestFileC2.xtf"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFile C1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Testet ob Sonderzeichen akzeptiert wird
	@Test
	public void splitFilesWithSpecialChars_Ok() {
		String listOfFiles =" test:;,\\data/multipleinputfiles/TestFileC1.xtf"+"\n"
							+" test/../multipleinputfiles/TestFileC2.xtf"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test:;,\\data/multipleinputfiles/TestFileC1.xtf",files[0]);
		assertEquals("test/../multipleinputfiles/TestFileC2.xtf",files[1]);
	}
	
}