package org.interlis2.validator;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.Test;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.settings.Settings;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.logging.LogEventImpl;

public class InconsistentModelVersionTest {
    
    private static final String CONFIG_FILE = "test/data/inconsistentModelVersion/ConfigFile.ini";

    @Test
    public void consistent_Ok() throws IoxException {
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, CONFIG_FILE); 
        boolean ret = Validator.runValidation("test/data/inconsistentModelVersion/ConsistentIlidata.xml", settings);        
        assertTrue(ret);
        assertFalse(hasAnInfoLogMsgForTheInconsistentVersion(logCollector));
    }
    
    @Test
    public void inconsistent_Ok() throws IoxException {
        LogCollector logCollector = new LogCollector();
        EhiLogger.getInstance().addListener(logCollector);
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, CONFIG_FILE); 
        boolean ret = Validator.runValidation("test/data/inconsistentModelVersion/InconsistentIlidata.xml", settings);        
        assertTrue(ret);
        assertTrue(hasAnInfoLogMsgForTheInconsistentVersion(logCollector));
    }

    private boolean hasAnInfoLogMsgForTheInconsistentVersion(LogCollector logCollector) {
        ArrayList<LogEvent> errs = logCollector.getEvents();
        for (LogEvent err : errs) {
            if (err instanceof LogEventImpl) {
                String infoMsg = "The VERSION in model (Beispiel2) and transferfile do not match (2011-12-22!=2011-12-23)";
                if (err.getEventMsg().equals(infoMsg)) {
                    return true;
                }
            }
        }
        return false;
    }
    
}
