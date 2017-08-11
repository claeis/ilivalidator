package org.interlis2.validator;

import static org.junit.Assert.*;
import org.junit.Test;
import ch.ehi.basics.settings.Settings;

public class MultipleInputFilesTest {
	
	// Die 2 xtf Files haben die Selbe Model Referenz.
	// Beide haben unterschiedliche bid, oid und attrValues.
	@Test
	public void inputMultipleFiles_Ok() {
		String[] listOfFiles = new String[2];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileC1.xtf";
		listOfFiles[1]="test/data/multipleinputfiles/TestFileC2.xtf";
		boolean ret=Validator.runValidation(listOfFiles, null);
		assertTrue(ret);
	}
	
	// TestFileD2.xtf hat eine Referenz auf ein Objekt in der Datei: TestFileD1.xtf.
	// Da das Flag: setting all objects accessible False gesetzt ist, werden keine Fehler ausgegeben.
	@Test
	public void allObjectsAccessible_xtfRef_Ok() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.FALSE);
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileD2.xtf";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertTrue(ret);
	}
	
	// Es werden 3 Files ausgewaehlt.
	// TestFileE2.xtf hat eine Referenz auf ein Objekt in der Datei: TestFileE1.xtf.
	// Da das Flag: setting all objects accessible False gesetzt ist, werden keine Fehler ausgegeben.
	@Test
	public void allObjectsAccessible_MultipleFiles_xtfRef_Ok() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.FALSE);
		String[] listOfFiles = new String[3];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileE2.xtf";
		listOfFiles[1]="test/data/multipleinputfiles/TestFileE1.xtf";
		listOfFiles[2]="test/data/multipleinputfiles/TestFileE3.xtf";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertTrue(ret);
	}
	
	// Es werden 2 Files ausgewaehlt.
	// TestFileE2.xtf hat eine Referenz auf ein Objekt in der Datei: TestFileE1.xtf.
	// Da das Flag: setting all objects accessible True gesetzt ist, werden Fehlermeldungen ausgegeben.
	@Test
	public void allObjectsAccessible_MultipleFiles_xtfRef_False() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
		String[] listOfFiles = new String[2];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileE2.xtf";
		listOfFiles[1]="test/data/multipleinputfiles/TestFileE3.xtf";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
	
	// TestFileD2.xtf hat eine Referenz auf ein Objekt in der Datei: TestFileD1.xtf.
	// Da das Flag: setting all objects accessible True gesetzt ist, wird der Fehler ausgegeben.
	@Test
	public void allObjectsAccessible_xtfRef_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileD2.xtf";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
	
	// Die 2 xtf Files haben die Selbe Model Referenz.
	// Beide haben die Selbe oid und attrValues.
	@Test
	public void oidAndAttrValuesAreEqual_Fail() {
		String[] listOfFiles = new String[2];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileC2.xtf";
		listOfFiles[1]="test/data/multipleinputfiles/TestFileC3.xtf";
		boolean ret=Validator.runValidation(listOfFiles, null);
		assertFalse(ret);
	}
}