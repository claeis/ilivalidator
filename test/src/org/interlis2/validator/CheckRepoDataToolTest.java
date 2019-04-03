package org.interlis2.validator;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.Test;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom.IomObject;
import ch.interlis.iox.IoxException;

public class CheckRepoDataToolTest {
    
    private static final String REPOSITORY = "test/data/checkRepoDataTool/repos1";

    @Test
    public void update_OK() throws IoxException {
        Settings settings = new Settings();
        settings.setValue(Validator.SETTING_REPOSITORY, REPOSITORY);
        boolean ret = CheckRepoDataTool.launch(settings);
        assertTrue(ret);
    }
    
    @Test
    public void method_findActualIliDatasTest_OK() throws Exception {
        RepositoryAccess reposAccess = new RepositoryAccess();
        File localCopyOfRemoteOriginalIliDataXml = reposAccess.getLocalFileLocation(REPOSITORY, IliManager.ILIDATA_XML, 0, null);
        
        IomObject[] ilidataContents = UpdateIliDataTool.readIliData(localCopyOfRemoteOriginalIliDataXml);
        CheckRepoDataTool repoData = new CheckRepoDataTool();
        IomObject[] actualIliDatas = repoData.findActualIliDatas(ilidataContents);
        
        assertEquals(actualIliDatas[0].getobjectoid(), "1");
        assertEquals(actualIliDatas[1].getobjectoid(), "1a");
        assertEquals(actualIliDatas[2].getobjectoid(), "8a");
    }

}
