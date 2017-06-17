package org.interlis2.validator;

import static org.junit.Assert.*;
import java.util.ArrayList;
import org.junit.Test;

public class MultipleInputFilesTest {
	
	// Die 2 xtf Files haben die Selbe Model Referenz.
	// Beide haben unterschiedliche bid, oid und attrValues.
	@Test
	public void inputMultipleFiles_Ok() {
		String[] listOfFiles = new String[2];
		listOfFiles[0]="test/data/multipleinputfiles/TestFileC.xtf";
		listOfFiles[1]="test/data/multipleinputfiles/TestFileC2.xtf";
		boolean ret=Validator.runValidation(listOfFiles, null);
		assertTrue(ret);
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