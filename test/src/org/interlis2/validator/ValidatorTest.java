package org.interlis2.validator;

import static org.junit.Assert.*;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;


public class ValidatorTest {

	@Test
	public void itfOk() {
		boolean ret=Validator.runValidation("test/data/Beispiel1a.itf", null);
		assertTrue(ret);
	}
	
	@Test
	public void itfReader2Ok() {
		boolean ret=Validator.runValidation("test/data/ItfReader2/SurfaceBasic.itf", null);
		assertTrue(ret);
	}
	
	@Test
	public void xtfReaderOk() {
		boolean ret=Validator.runValidation("test/data/Xtf23Reader/SimpleCoord23a.xtf", null);
		assertTrue(ret);
	}
	
	@Test
	public void itfOkWithSettings() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		boolean ret=Validator.runValidation("test/data/Beispiel1a.itf", settings);
		assertTrue(ret);
	}
	@Test
	public void itfOkWithValidationConfig() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel1e.toml");
		boolean ret=Validator.runValidation("test/data/Beispiel1e.itf", settings);
		assertTrue(ret);
	}
	@Test
	public void itfFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel1b.itf", null);
		assertFalse(ret);
	}
	@Test
	public void itfFailWithSettings() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		boolean ret=Validator.runValidation("test/data/Beispiel1b.itf", settings);
		assertFalse(ret);
	}
	@Test
	public void itfAreaBasicOk() {
		boolean ret=Validator.runValidation("test/data/Beispiel1c.itf", null);
		assertTrue(ret);
	}
	@Test
	public void itfAreaOpenFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel1d.itf", null);
		assertFalse(ret);
	}
	@Test
	public void xtfOk() {
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", null);
		assertTrue(ret);
	}
	@Test
	public void xtfOkWithSettings() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", settings);
		assertTrue(ret);
	}
	@Test
	public void xtfOkWithFunction() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File("plugins").getAbsolutePath());
		boolean ret=Validator.runValidation("test/data/Beispiel3ok.xtf", settings);
		assertTrue(ret);
	}
	@Test
	public void xtfFailWithFunction() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File("plugins").getAbsolutePath());
		boolean ret=Validator.runValidation("test/data/Beispiel3fail.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfFailWithAdditionalModel() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2e.toml");
		boolean ret=Validator.runValidation("test/data/Beispiel2e.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", null);
		assertFalse(ret);
	}
	@Test
	public void xtfFailWithSettings() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfOkWithValidationConfig() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2b.toml");
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", settings);
		assertTrue(ret);
	}
    @Test
    public void xtfOkWithValidationConfigAllowAreaOverlap() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2AreaOverlap.toml");
        boolean ret=Validator.runValidation("test/data/Beispiel2AreaOverlap.xtf", settings);
        assertTrue(ret);
    }
	@Test
	public void xtfFailForceTypeCheckWithValidationConfig() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_FORCE_TYPE_VALIDATION, Validator.TRUE);
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2b.toml");
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfAreaBasicOk() {
		boolean ret=Validator.runValidation("test/data/Beispiel2c.xtf", null);
		assertTrue(ret);
	}
	@Test
	public void xtfAreaOpenFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel2d.xtf", null);
		assertFalse(ret);
	}
	@Test
	public void xtfRefOk() {
		boolean ret=Validator.runValidation("test/data/Beispiel2refOk.xtf", null);
		assertTrue(ret);
	}
	@Test
	public void xtfRefFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel2refFail.xtf", null);
		assertFalse(ret);
	}
	@Test
	public void ili2cFail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, "emptyFolder"); // ili2c will not find any ili files there
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", settings);
		assertFalse(ret);
	}

}
