package org.interlis2.validator;

import static org.junit.Assert.*;
import org.interlis2.validator.gui.MainFrame;
import org.junit.Test;

public class MultipleInputFilesUiTextTest {
	
	// Kommandozeilen Tests:
	// --models ModelC test/data/multipleinputfiles/TestFileC1.xtf
	// --models ModelC test/data/multipleinputfiles/TestFileC1.xtf test/data/multipleinputfiles/TestFileC2.xtf
	// --models ModelC test/data/multipleinputfiles/TestFileC1.xtf test/data/multipleinputfiles/TestFileC2.xtf test/data/multipleinputfiles/TestFileC3.xtf
	
	// Gui Kommandozeilen Tests:
	// --gui --models ModelC test/data/multipleinputfiles/TestFileC1.xtf
	// --gui --models ModelC test/data/multipleinputfiles/TestFileC1.xtf test/data/multipleinputfiles/TestFileC2.xtf
	// --gui --models ModelC test/data/multipleinputfiles/TestFileC1.xtf test/data/multipleinputfiles/TestFileC2.xtf test/data/multipleinputfiles/TestFileC3.xtf
	
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	@Test
	public void splitFiles_Ok() {
		String listOfFiles ="test/data/multipleinputfiles/TestFileC1.xtf"+"\n"
							+"test/data/multipleinputfiles/TestFileC2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test/data/multipleinputfiles/TestFileC1.xtf",files[0]);
		assertEquals("test/data/multipleinputfiles/TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall werden vor und nach den Pfaden Leerzeichen: " " erstellt.
	@Test
	public void splitFilesWithSpaces_Ok() {
		String listOfFiles =" TestFileC1.xtf "+"\n"
							+" TestFileC2.xtf ";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird 1 Zeile nur als neue Zeile: "\n" erstellt.
	@Test
	public void splitFilesEmptyRow_Ok() {
		String listOfFiles ="TestFileC1.xtf"+"\n"
							+ "\n"
							+"TestFileC2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall werden innerhalb der Pfade: Leerzeichen: " " definiert.
	@Test
	public void splitFilesSpaceInPath_Ok() {
		String listOfFiles ="TestFile C1.xtf"+"\n"
							+"TestFile C2.xtf";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFile C1.xtf",files[0]);
		assertEquals("TestFile C2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird 1 Zeile nur als neue Zeile mit Leerzeichen: "      \n" erstellt.
	@Test
	public void splitFilesOneLineWithSpaces_Ok() {
		String listOfFiles =" TestFileC1.xtf "+"\n"
							+ "   \n"
							+" TestFileC2.xtf ";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFileC1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird eine neue Zeile mit: "\n" erstellt.
	@Test
	public void splitFilesNewRow_Ok() {
		String listOfFiles ="TestFile C1.xtf"+"\n"
							+"TestFileC2.xtf"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("TestFile C1.xtf",files[0]);
		assertEquals("TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird die Pfadangabe zusaetzlich mit Backslashes erstellt.
	@Test
	public void splitFilesWithBackslashes_Ok() {
		String listOfFiles =" test\\data/multipleinputfiles/TestFileC1.xtf"+"\n"
							+" test/data/multipleinputfiles/TestFileC2.xtf"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test\\data/multipleinputfiles/TestFileC1.xtf",files[0]);
		assertEquals("test/data/multipleinputfiles/TestFileC2.xtf",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird die Pfadangabe zusaetzlich mit Commas versehen.
	@Test
	public void splitFilesWithCommas_Ok() {
		String listOfFiles =" test/data/multipleinputfiles/TestFileC1.xtf,"+"\n"
							+" test/data/multipleinputfiles/TestFileC2.xtf, "+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test/data/multipleinputfiles/TestFileC1.xtf,",files[0]);
		assertEquals("test/data/multipleinputfiles/TestFileC2.xtf,",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird am Ende der jeweiligen Pfade ein Semicolons gesetzt.
	@Test
	public void splitFilesWithSemikolons_Ok() {
		String listOfFiles ="test/data/multiple inputfiles/TestFileC1.xtf;"+"\n"
							+"test/data/multipleinputfiles/TestFileC2.xtf;"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test/data/multiple inputfiles/TestFileC1.xtf;",files[0]);
		assertEquals("test/data/multipleinputfiles/TestFileC2.xtf;",files[1]);
	}
	
	// Es wird getestet ob eine Fehlermeldung ausgegeben wird, wenn 1 String mit mehreren Pfaden zu Dateien erstellt wird.
	// dieser mit der Funktion: splitFilenames in einzelne Parts zersetzt wird.
	// In der Array sollen nur reine Dateipfade enthalten sein.
	// In diesem Fall wird folgendes gemacht:
	// - In diesem Fall werden vor und nach den Pfaden Leerzeichen: " " erstellt.
	// - In diesem Fall werden mehrere Zeilen mit dem Inhalt: "\n" oder "  \n" oder "  \n    " oder "\n     " erstellt.
	// - In diesem Fall wird eine Zeile mit dem Inhalt: "" erstellt.
	// - In diesem Fall wird eine Zeile mit dem Inhalt: " " erstellt.
	// - In diesem Fall wird 1 Zeile nur als neue Zeile: "\n" erstellt.
	// - In diesem Fall werden innerhalb der Pfade: 1 oder mehrere Leerzeichen: " " definiert.
	// - In diesem Fall wird die Pfadangabe zusaetzlich mit Backslashes erstellt.
	// - In diesem Fall wird die Pfadangabe zusaetzlich mit Commas versehen.
	@Test
	public void splitFilesAllTogether_Ok() {
		String listOfFiles ="        \n"+"\n"
							+" test/ d a t a   /multiple, inputfiles/TestFileC1.xtf "+"\n"
							+"        \n"
							+"        \n"
							+" test\\data/multipleinputfiles \\ TestFileC2.xtf "+"\n"
							+" "+"\n"
							+""+"\n"
							+"\n       "+"\n"
							+"          \n         "+"\n"+"\n"+"\n"+"\n"+"\n"+"\n";
		String[] files=MainFrame.splitFilenames(listOfFiles);
		assertEquals(2,files.length);
		assertEquals("test/ d a t a   /multiple, inputfiles/TestFileC1.xtf",files[0]);
		assertEquals("test\\data/multipleinputfiles \\ TestFileC2.xtf",files[1]);
	}
}