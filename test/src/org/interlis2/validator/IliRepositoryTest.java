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
    public void xtf24Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/ilirepository/repos");
        boolean ret=Validator.runValidation("test/data/ilirepository/Simple24a.xtf", settings);
        assertTrue(ret);
    }
}
