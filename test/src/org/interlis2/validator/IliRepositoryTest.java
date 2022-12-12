package org.interlis2.validator;

import static org.junit.Assert.*;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

import org.junit.Assert;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iom_j.xtf.XtfStartTransferEvent;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class IliRepositoryTest {
	
	@Test
	public void xtf10Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
		boolean ret=Validator.runValidation("test/data/ilirepository/Simple10a.itf", settings);
		assertTrue(ret);
	}
    @Test
    public void xtf22Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple22a.xtf", settings);
        assertTrue(ret);
    }
    @Test
    public void xtf23Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple23a.xtf", settings);
        assertTrue(ret);
    }
    @Test
    public void xtf23_ConfigFail() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        settings.setValue(Validator.SETTING_CONFIGFILE, "ilidata:config");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple23fail.xtf", settings);
        assertFalse(ret);
    }
    @Test
    public void xtf23_ConfigOk() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        settings.setValue(Validator.SETTING_CONFIGFILE, "ilidata:70340b5a-248c-4216-86e4-32e6a540d629");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple23fail.xtf", settings);
        assertTrue(ret);
    }
    @Test
    public void xtf23_extRefFail() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        String[] listOfFiles = new String[2];
        listOfFiles[0]="ilidata:ExtRef23base";
        listOfFiles[1]="test/data/ilirepository/ExtRef23a.xtf";
        boolean ret=Validator.runValidation(listOfFiles, settings);
        assertFalse(ret);
    }
    @Test
    public void xtf23_extRef_BasketIdOk() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        String[] listOfFiles = new String[2];
        listOfFiles[0]="ilidata:c1105d5c-9ec3-49ed-9e4b-a7de0124138c";
        listOfFiles[1]="test/data/ilirepository/ExtRef23a.xtf";
        boolean ret=Validator.runValidation(listOfFiles, settings);
        assertTrue(ret);
    }
    @Test
    public void xtf23_extRef_DatasetIdOk() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        String[] listOfFiles = new String[2];
        listOfFiles[0]="ilidata:63553eb4-a0dc-48eb-8596-ca1aa9bdbc0f";
        listOfFiles[1]="test/data/ilirepository/ExtRef23a.xtf";
        boolean ret=Validator.runValidation(listOfFiles, settings);
        assertTrue(ret);
    }
    @Test
    public void xtf24Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple24a.xtf", settings);
        assertTrue(ret);
    }
}
