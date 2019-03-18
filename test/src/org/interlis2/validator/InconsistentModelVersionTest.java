package org.interlis2.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.settings.Settings;
import ch.interlis.iom_j.itf.LogCollector;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.logging.LogEventImpl;

public class InconsistentModelVersionTest {
    
    private static final String CONFIG_FILE = "test/data/inconsistentModelVersion/ConfigFile.toml";

    @Test
    public void consistent_Ok() throws IoxException {
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, CONFIG_FILE); 
        boolean ret = Validator.runValidation("test/data/inconsistentModelVersion/Consistent/ilidata.xml", settings);        
        assertTrue(ret);
        assertFalse(hasAnInfoLogMsgForTheInconsistentVersion(logCollector));
    }
    
    @Test
    public void inconsistent_Ok() throws IoxException {
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, CONFIG_FILE); 
        boolean ret = Validator.runValidation("test/data/inconsistentModelVersion/Inconsistent/ilidata.xml", settings);        
        assertTrue(ret);
        assertTrue(hasAnInfoLogMsgForTheInconsistentVersion(logCollector));
    }

    private boolean hasAnInfoLogMsgForTheInconsistentVersion(LogCollector logCollector) {
        ArrayList<LogEvent> errs = logCollector.getErrs();
        for (LogEvent err : errs) {
            if (err instanceof LogEventImpl) {
                String infoMsg = "The Iliversion (2018-11-19) in a Model (DatasetIdx16) and the XML Model version (2018-11-21) in a model (DatasetIdx16) do not match.";
                if (err.getEventMsg().equals(infoMsg)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
