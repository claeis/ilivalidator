package org.interlis2.validator;

import org.junit.Assert;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;

public class AutoRefDataTest {
    @Test
    public void ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_REF_MAPPING_DATA, "test/data/AutoRefData/RefMapping.xtf");
        settings.setValue(Validator.SETTING_VALIDATION_SCOPE, "B");
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        boolean ret=Validator.runValidation("test/data/AutoRefData/Simple24a.xtf", settings);
        Assert.assertTrue(ret);
    }
    @Test
    public void fail() {
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_REF_MAPPING_DATA, "test/data/AutoRefData/RefMapping.xtf");
        settings.setValue(Validator.SETTING_VALIDATION_SCOPE, "A");
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        boolean ret=Validator.runValidation("test/data/AutoRefData/Simple24a.xtf", settings);
        Assert.assertFalse(ret);
        Assert.assertEquals("No object found with OID a20.",logCollector.getErrs().get(0).getEventMsg());
    }

}
