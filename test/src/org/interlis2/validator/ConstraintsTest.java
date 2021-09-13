package org.interlis2.validator;

import static org.junit.Assert.*;
import org.junit.Test;
import ch.ehi.basics.settings.Settings;

public class ConstraintsTest {
	private static final String TEST_IN="test/data/constraints/";
	
	@Test
	public void allConstraints_DisableConstraintValidation_NotSet_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_CONFIGFILE, TEST_IN+"Configfile.ini");
		boolean ret=Validator.runValidation(TEST_IN+"AllConstraints.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void allConstraints_DisableConstraintValidation_FALSE_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_CONFIGFILE, TEST_IN+"Configfile.ini");
		settings.setValue(Validator.SETTING_DISABLE_CONSTRAINT_VALIDATION, Validator.FALSE);
		boolean ret=Validator.runValidation(TEST_IN+"AllConstraints.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void allConstraints_DisableConstraintValidation_TRUE_Ok() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_CONFIGFILE, TEST_IN+"Configfile.ini");
		settings.setValue(Validator.SETTING_DISABLE_CONSTRAINT_VALIDATION, Validator.TRUE);
		boolean ret=Validator.runValidation(TEST_IN+"AllConstraints.xtf", settings);
		assertTrue(ret);
	}
}