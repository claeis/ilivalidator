package org.interlis2.validator;

import static org.junit.Assert.*;
import java.io.File;
import java.util.HashMap;

import org.junit.Ignore;
import org.junit.Test;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.ObjectEvent;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;

public class ValidatorTest {
	private static final String PLUGINPATH_DEMOPLUGIN=new File("demoplugin/build/libs").getAbsolutePath();

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
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel1e.ini");
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
    public void itfWithValidationConfig_DefaultGeometryTypeValidationOff_Ok() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/defaultGeometryTypeValidationOff.ini");
        boolean ret=Validator.runValidation("test/data/Beispiel1d.itf", settings);
        assertTrue(ret);
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
	public void xtfInvalidPlaceholderSettingFail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, "%ILI_DIR;http://models.interlis.ch/;%JAR_DIR/ilimodels");
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfValidPlaceholderSettingOk() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, "%ITF_DIR;http://models.interlis.ch/;%JAR_DIR/ilimodels");
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", settings);
		assertTrue(ret);
	}
	@Test
	public void xtfOkWithFunction() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_PLUGINFOLDER, PLUGINPATH_DEMOPLUGIN);
		boolean ret=Validator.runValidation("test/data/Beispiel3ok.xtf", settings);
		assertTrue(ret);
	}
	@Test
	public void xtfFailWithFunction() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_PLUGINFOLDER, PLUGINPATH_DEMOPLUGIN);
		boolean ret=Validator.runValidation("test/data/Beispiel3fail.xtf", settings);
		assertFalse(ret);
	}
	@Test
	public void xtfFailWithAdditionalModel() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2e.ini");
		boolean ret=Validator.runValidation("test/data/Beispiel2e.xtf", settings);
		assertFalse(ret);
	}
    @Test
    @Ignore("Issue #324")
    public void xtfFailWithView() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ILIDIRS, Validator.ITF_DIR);
        boolean ret=Validator.runValidation("test/data/Beispiel5a.xtf", settings);
        assertFalse(ret);
    }
	@Test
	public void xtfFail() {
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", null);
		assertFalse(ret);
	}
    @Test
    public void xtfFailWithXtflog() throws Exception {
        Settings settings=new Settings();
        File xtflog=new File("test/data/Beispiel4a.log");
        settings.setValue(Validator.SETTING_XTFLOG,xtflog.getPath());
        boolean ret=Validator.runValidation("test/data/Beispiel4a.xtf", settings);
        assertFalse(ret);
        HashMap<String,IomObject> objs=new HashMap<String,IomObject>();
        XtfReader reader=null;
        try {
            reader=new XtfReader(xtflog);
            IoxEvent event=null;
             do{
                event=reader.read();
                if(event instanceof StartTransferEvent){
                }else if(event instanceof StartBasketEvent){
                }else if(event instanceof ObjectEvent){
                    IomObject iomObj=((ObjectEvent)event).getIomObject();
                    if(iomObj.getobjectoid()!=null && "Error".equals(iomObj.getattrvalue("Type"))){
                        objs.put(iomObj.getobjectoid(), iomObj);
                    }
                }else if(event instanceof EndBasketEvent){
                }else if(event instanceof EndTransferEvent){
                }
             }while(!(event instanceof EndTransferEvent));
            assertEquals(1,objs.size());
            assertEquals("Beschreibung fehlt (\u00E4\u00F6\u00FC)",objs.values().iterator().next().getattrvalue("Message"));
        }finally {
            if(reader!=null) {
                reader.close();
                reader=null;
            }
        }
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
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2b.ini");
		boolean ret=Validator.runValidation("test/data/Beispiel2b.xtf", settings);
		assertTrue(ret);
	}
    @Test
    public void xtfOkWithValidationConfigAllowAreaOverlap() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2AreaOverlap.ini");
        boolean ret=Validator.runValidation("test/data/Beispiel2AreaOverlap.xtf", settings);
        assertTrue(ret);
    }
	@Test
	public void xtfFailForceTypeCheckWithValidationConfig() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_FORCE_TYPE_VALIDATION, Validator.TRUE);
		settings.setValue(Validator.SETTING_CONFIGFILE, "test/data/Beispiel2b.ini");
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
    public void xtfAllObjExtrefOk() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        boolean ret=Validator.runValidation(new String[] {
                "test/data/Beispiel2allObjExtrefOk1.xtf",
                "test/data/Beispiel2allObjExtrefOk2.xtf"
        }, settings);
        assertTrue(ret);
    }
    @Test
    public void xtfAllObjExtrefFail() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE, Validator.TRUE);
        boolean ret=Validator.runValidation(new String[] {
                "test/data/Beispiel2allObjExtrefOk1.xtf",
                "test/data/Beispiel2allObjExtrefFail2.xtf"
        }, settings);
        assertFalse(ret);
    }
    @Test
    public void xtfHttpFileOk() {
        boolean ret=Validator.runValidation("https://models.interlis.ch/refhb23/MiniCoordSysData-20200320.xml", null);
        assertTrue(ret);
    }
	@Test
	public void ili2cFail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, "emptyFolder"); // ili2c will not find any ili files there
		boolean ret=Validator.runValidation("test/data/Beispiel2a.xtf", settings);
		assertFalse(ret);
	}
    @Test
    public void xtfRuntimeParameterOk() {
        Settings settings=new Settings();
        settings.setValue(Validator.SETTING_RUNTIME_PARAMETERS, "RuntimeSystem23.JobId=test1;RuntimeSystem23.JobId2=test2");
        boolean ret=Validator.runValidation("test/data/runtimeParameter/SimpleA.xtf", settings);
        assertTrue(ret);
    }

}
