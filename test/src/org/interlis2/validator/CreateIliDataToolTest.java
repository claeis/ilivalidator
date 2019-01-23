package org.interlis2.validator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ch.ehi.basics.settings.Settings;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.jts.Iox2jtsException;

public class CreateIliDataToolTest {
    
    private static final String ILIDATA_XML = "test/data/createIliDataTool/ilidata.xml";

    @Test
    public void localFolder() throws IoxException {
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_ILIDATA_XML, ILIDATA_XML);
        settings.setValue(Validator.SETTING_REPOSITORY_TO_SCAN, "test/data/createIliDataTool/localfolder");
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/createIliDataTool");
        boolean ret = CreateIliDataTool.start(settings);
        assertTrue(ret);
        
        XtfReader reader = new XtfReader(new File(ILIDATA_XML));
        
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof ObjectEvent) {
                checkResult(reader, event);
            }
        } while (!(event instanceof EndTransferEvent));

        // Validate generated IliDataXml
        boolean runValidation = Validator.runValidation(new String[] { ILIDATA_XML }, null);
        assertTrue(runValidation);
    }
    
    @Test
    public void repository() throws Iox2jtsException, IoxException {
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_ILIDATA_XML, ILIDATA_XML);
        settings.setValue(Validator.SETTING_REMOTEFILE_LIST, "test/data/createIliDataTool/filelist.txt");
        settings.setValue(Validator.SETTING_REPOSITORY_TO_SCAN, "test/data/createIliDataTool/repos1");
        settings.setValue(Validator.SETTING_ILIDIRS, "test/data/createIliDataTool");
        
        boolean ret = CreateIliDataTool.start(settings);
        assertTrue(ret);
        
        XtfReader reader = new XtfReader(new File(ILIDATA_XML));
        
        IoxEvent event = null;
        do {
            event = reader.read();
            if (event instanceof ObjectEvent) {
                checkResult(reader, event);
            }
        } while (!(event instanceof EndTransferEvent));
        
        // Validate generated IliDataXml
        boolean runValidation = Validator.runValidation(new String[] { ILIDATA_XML }, null);
        assertTrue(runValidation);
    }
    
    public void checkResult(XtfReader reader, IoxEvent event) throws IoxException {
        
        //
        // 1. Objekt
        //
        IomObject iomObject = ((ObjectEvent) event).getIomObject();
        // ID
        assertEquals("Beispiel1a", iomObject.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id));
        
        // File/FileFormat
        IomObject files = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
        IomObject file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
        assertEquals("sub/Beispiel1a.itf", file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path));
        assertEquals("5f8caefa9e3c2c98d4bedda903da1c86", file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_md5));
        assertEquals("application/interlis+txt;version=1.0", files.getattrvalue(ch.interlis.models.DatasetIdx16.DataFile.tag_fileFormat));
        
        // Owner
        assertEquals(CreateIliDataTool.getOwnerByCurrentUser(), iomObject.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_owner));
        
        // Baskets
        IomObject baskets = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_baskets, 0);
        // ModelName 
        IomObject model = baskets.getattrobj(ch.interlis.models.DatasetIdx16.Metadata.tag_model, 0);
        assertEquals("Beispiel1.Bodenbedeckung", model.getattrvalue(ch.interlis.models.DatasetIdx16.ModelLink.tag_name));
        assertEquals(CreateIliDataTool.getOwnerByCurrentUser(), baskets.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_owner));
        assertEquals("itf0", baskets.getattrvalue(ch.interlis.models.DatasetIdx16.DataIndex.BasketMetadata.tag_localId));
        assertEquals("1", baskets.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version));
        
        //
        // 2. Objekt
        //
        event = reader.read();
        iomObject = ((ObjectEvent) event).getIomObject();
        // ID
        assertEquals("Beispiel2a", iomObject.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id));

        // File/FileFormat
        files = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
        file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
        assertEquals("sub/Beispiel2a.xtf", file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path));
        assertEquals("application/interlis+xml;version=2.3", files.getattrvalue(ch.interlis.models.DatasetIdx16.DataFile.tag_fileFormat));

        // Owner
        assertEquals(CreateIliDataTool.getOwnerByCurrentUser(), iomObject.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_owner));

        // Baskets
        baskets = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_baskets, 0);
        // ModelName
        model = baskets.getattrobj(ch.interlis.models.DatasetIdx16.Metadata.tag_model, 0);
        assertEquals("Beispiel2.Bodenbedeckung", model.getattrvalue(ch.interlis.models.DatasetIdx16.ModelLink.tag_name));
        assertEquals(CreateIliDataTool.getOwnerByCurrentUser(), baskets.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_owner));
        assertEquals("b1", baskets.getattrvalue(ch.interlis.models.DatasetIdx16.DataIndex.BasketMetadata.tag_localId));
        assertEquals("1", baskets.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version));
        
        // 2. Basket
        IomObject baskets2 = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_baskets, 1);
        assertEquals("cb3817b2-ebb9-4346-a406-0e30c81eff7d", baskets2.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id));
        IomObject model2 = baskets2.getattrobj(ch.interlis.models.DatasetIdx16.Metadata.tag_model, 0);
        assertEquals("Beispiel2.GebaeudeRegister", model2.getattrvalue(ch.interlis.models.DatasetIdx16.ModelLink.tag_name));
        assertEquals(CreateIliDataTool.getOwnerByCurrentUser(), baskets2.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_owner));
        assertEquals("1", baskets2.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version));
    }
}
