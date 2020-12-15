package org.interlis2.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom.IomObject;

public class CheckRepoDataTool {
    
    public static boolean launch(Settings settings) {
        return new CheckRepoDataTool().checkRepoData(settings);
    }
    
    private boolean checkRepoData(Settings settings) {
        try {
            String repository = settings.getValue(Validator.SETTING_REPOSITORY);
            if (repository == null || repository.isEmpty()) {
                throw new Exception("Repository should be given as a parameter!");
            }
            
            // Get the IliDataXml from the Repository
            RepositoryAccess reposAccess = new RepositoryAccess();                
            File localCopyOfRemoteOriginalIliDataXml = reposAccess.getLocalFileLocation(repository, IliManager.ILIDATA_XML, 0, null);
            if(localCopyOfRemoteOriginalIliDataXml == null) {
                throw new Exception(IliManager.ILIDATA_XML+" could not be found in <"+repository+">");
            }
            
            IomObject[] ilidataContents = UpdateIliDataTool.readIliData(localCopyOfRemoteOriginalIliDataXml);
            if (ilidataContents == null) {
                throw new Exception("Contents of the "+IliManager.ILIDATA_XML+" should not be empty!");
            }
            
            IomObject[] actualIliDatas = findActualIliDatas(ilidataContents);
            for (IomObject currentObj : actualIliDatas) {
                IomObject files = currentObj.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
                IomObject file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
                String filepath = file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path);
                File localCopyOfRemoteFile = reposAccess.getLocalFileLocation(repository, filepath, 0, null);
                
                // Validate IliDataXml
                boolean runValidation = Validator.runValidation(new String[] { localCopyOfRemoteFile.getAbsolutePath() }, null);
                if (!runValidation) {
                    return false;
                }
            }
        } catch (Exception e) {
            EhiLogger.logError(e);
            return false;
        }
        return true;
    }
    
    public IomObject[] findActualIliDatas(IomObject[] ilidataContents) throws Exception {
        Map<String, List<IomObject>> actualIliDatas = new HashMap<String, List<IomObject>>();
        try {
            for (IomObject currentIomObj : ilidataContents) {
                if(currentIomObj.getobjecttag().equals(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag)) {
                    String currentID = currentIomObj.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id);
                    String currentpreVersion = currentIomObj.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_precursorVersion);
                    if (currentpreVersion == null || currentpreVersion.isEmpty()) {
                        // then it has a new DatasetID
                        appendNewIomObj(actualIliDatas, currentIomObj, currentID);
                    } else {
                        List<IomObject> iomObj = actualIliDatas.get(currentID);
                        if (iomObj == null || iomObj.isEmpty()) {
                            List<IomObject> objectList = new ArrayList<IomObject>();
                            actualIliDatas.put(currentID, objectList);
                        } else {
                            appendNewIomObj(actualIliDatas, currentIomObj, currentID);
                            String[] precursorValues = findAllPrecursorVersions(iomObj);
                            if (precursorValues != null) {
                                for (int i = 0; i < iomObj.size(); i++) {
                                    String currentVersionName = iomObj.get(i).getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version);
                                    for (String preValue : precursorValues) {
                                        if (preValue.equals(currentVersionName)) {
                                            iomObj.remove(i);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            throw new Exception(e);
        }
        List<IomObject> returnValues = new ArrayList<IomObject>();
        for (List<IomObject> values : actualIliDatas.values()) {
            for (IomObject iomObj : values) {
                returnValues.add(iomObj);
            }
       }
        
        return returnValues.toArray(new IomObject[returnValues.size()]);
    }

    private String[] findAllPrecursorVersions(List<IomObject> iomObj) {
        List<String> returnPreValues = new ArrayList<String>();
        for (int i = 0; i < iomObj.size(); i++) {
            String tmpPrecursor = iomObj.get(i).getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_precursorVersion);
            if (tmpPrecursor != null) {
                returnPreValues.add(tmpPrecursor);
            }
        }
        return returnPreValues.toArray(new String[returnPreValues.size()]);
    }

    private void appendNewIomObj(Map<String, List<IomObject>> actualIliDatas, IomObject currentIomObj,
            String currentID) {
        List<IomObject> iomObj = actualIliDatas.get(currentID);
        if (iomObj == null) {
            iomObj = new ArrayList<IomObject>();
        }
        iomObj.add(currentIomObj);
        actualIliDatas.put(currentID, iomObj);
    }
}
