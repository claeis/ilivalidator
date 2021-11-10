package org.interlis2.validator;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.modelscan.IliFile;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom.IomObject;
import ch.interlis.iox_j.logging.FileLogger;

public class CheckRepoDataTool {
    
    public static boolean launch(Settings settings) {
        return new CheckRepoDataTool().checkRepoData(settings);
    }
    
    private boolean checkRepoData(Settings settings) {
        FileLogger logfile=null;
        try {
            String logFilename=settings.getValue(Validator.SETTING_LOGFILE);
            // setup logging of validation results
            if(logFilename!=null){
                File f=new java.io.File(logFilename);
                try {
                    if(Validator.isWriteable(f)) {
                        logfile=new FileLogger(f);
                        EhiLogger.getInstance().addListener(logfile);
                    }else {
                        throw new Exception("failed to write to logfile <"+f.getPath()+">");
                    }
                } catch (IOException e) {
                    throw new Exception("failed to write to logfile <"+f.getPath()+">",e);
                }
            }
            String repository = settings.getValue(Validator.SETTING_REPOSITORY);
            if (repository == null || repository.isEmpty()) {
                throw new Exception("Repository should be given as a parameter!");
            }
            ch.interlis.ili2c.Main.setHttpProxySystemProperties(settings);
            
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
            
            List<String> failedFiles=new ArrayList<String>();
            IomObject[] actualIliDatas = findActualIliDatas(ilidataContents);
            for (IomObject currentObj : actualIliDatas) {
                String tid=currentObj.getobjectoid();
                EhiLogger.traceState("validate TID <"+tid+">");
                IomObject files = currentObj.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
                if(files == null) {
                    EhiLogger.logError("TID "+tid+": missing "+ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files);
                }
                IomObject file = null;
                if(files!=null) {
                    file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
                    if(file == null) {
                        EhiLogger.logError("TID "+tid+": missing "+ch.interlis.models.DatasetIdx16.DataFile.tag_file);
                    }
                }
                String filepath=null;
                if(file!=null) {
                    filepath = file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path);
                    if(filepath == null) {
                        EhiLogger.logError("TID "+tid+": missing "+ch.interlis.models.DatasetIdx16.File.tag_path);
                    }
                }
                File localCopyOfRemoteFile = null;
                if(filepath!=null) {
                    localCopyOfRemoteFile = reposAccess.getLocalFileLocation(repository, filepath, 0, null);
                    if(localCopyOfRemoteFile == null) {
                        EhiLogger.logError("TID "+tid+": "+filepath+" could not be found in <"+repository+">");
                        failedFiles.add(filepath);
                    }
                }
                if(localCopyOfRemoteFile!=null) {
                    // Validate IliDataXml
                    boolean runValidation = Validator.runValidation(new String[] { localCopyOfRemoteFile.getAbsolutePath() }, null);
                    if (!runValidation) {
                        failedFiles.add(filepath);
                    }
                }
            }
            if(!failedFiles.isEmpty()) {
                StringBuilder failed=new StringBuilder();
                String sep="";
                for(String f:failedFiles){
                    failed.append(sep);
                    failed.append(f);
                    sep=", ";
                }
                EhiLogger.logError("validation failed with files: "+failed);
                return false;
            }
        } catch (Exception e) {
            EhiLogger.logError(e);
            return false;
        }finally {
            if(logfile!=null){
                logfile.close();
                EhiLogger.getInstance().removeListener(logfile);
                logfile=null;
            }
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
