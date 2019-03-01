package org.interlis2.validator;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom.IomObject;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.xtf.XtfModel;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iom_j.xtf.XtfWriterBase;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.IoxUtility;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.models.DatasetIdx16.Code_;
import ch.interlis.models.DatasetIdx16.DataFile;
import ch.interlis.models.DatasetIdx16.ModelLink;
import ch.interlis.models.DatasetIdx16.DataIndex.BasketMetadata;
import ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata;

public class UpdateIliDataTool {

    public static boolean update(Settings settings) {
        return new UpdateIliDataTool().updateIliData(settings);
    }
    
    private boolean updateIliData(Settings settings) {
        
        try {
            String ilidataXmlFileToWrite = settings.getValue(Validator.SETTING_UPDATE_ILIDATA);
            if (ilidataXmlFileToWrite == null || ilidataXmlFileToWrite.isEmpty()) {
                throw new Exception("The desired province to update data can not be null!");
            }
            
            String datasetID = settings.getValue(Validator.SETTING_DATASETID_TO_UPDATE);
            if (datasetID == null || datasetID.isEmpty()) {
                throw new Exception("Dataset ID should be given as a parameter!");
            }
            
            String newVersionOfDataXml = settings.getValue(Validator.SETTING_NEW_VERSION_OF_DATA);
            if (newVersionOfDataXml == null || newVersionOfDataXml.isEmpty()) {
                throw new Exception("New version of ili data should be given as a parameter.");
            }
            
            String repository = settings.getValue(Validator.SETTING_REPOSITORY);
            if (repository == null || repository.isEmpty()) {
                throw new Exception("Repository should be given as a parameter!");
            } 
            
            //nimmt das ilidata.xml von diesem Repository
            RepositoryAccess reposAccess = new RepositoryAccess();                
            File localCopyOfRemoteOriginalIliDataXml = reposAccess.getLocalFileLocation(repository, IliManager.ILIDATA_XML, 0, null);
            if(localCopyOfRemoteOriginalIliDataXml == null) {
                throw new Exception("IliData could not be found in the given repository!");
            }
            
            IomObject[] oldIlidataContents = readIliData(localCopyOfRemoteOriginalIliDataXml);
            ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata newMetadata = readDataFile(new File(newVersionOfDataXml), settings);

            // Update ID
            newMetadata.setid(datasetID);
            
            long maxOid = getMaxOid(oldIlidataContents);
            String precursorVersion = null;
            String newVersion = getNewVersion(oldIlidataContents, datasetID);
            File updatedFile = null;
            IomObject latestVersion = getLatestVersion(oldIlidataContents, datasetID);
            if (latestVersion != null) {
                precursorVersion = latestVersion.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version);
                updatedFile = getFileName(latestVersion);
            }          
            
            // Set TID
            newMetadata.setobjectoid(Long.toString(maxOid + 1));
            
            // Update File Path
            if (updatedFile != null) {
                IomObject files = newMetadata.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
                IomObject file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
                File oldFile = new File(file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path));
                String newFile = updatedFile.getParent() + "/" + oldFile.getName();
                file.setattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path, newFile);
            }
            
            // Set Version
            BasketMetadata[] baskets = newMetadata.getbaskets();
            for (BasketMetadata basketMetaData : baskets) {
                basketMetaData.setversion(newVersion);
            }
            newMetadata.setversion(newVersion);
            
            // Set PrecursorVersion
            newMetadata.setprecursorVersion(precursorVersion);
            
            // Write the Result in ilidata.xml
            writeNewIliData(new File(ilidataXmlFileToWrite), newMetadata, oldIlidataContents, localCopyOfRemoteOriginalIliDataXml);
        } catch (Exception e) {
            EhiLogger.logError(e);
            return false;
        }
        return true;
    }

    private IomObject getLatestVersion(IomObject[] oldIlidataContents, String datasetID) {
        long maxVersion = 0;
        IomObject lastIomObj = null;
        List<IomObject> tmpIomObj = new ArrayList<IomObject>();
        for (int i = 0; i < oldIlidataContents.length; i++) {
            IomObject currentIomObj = oldIlidataContents[i];
            if (currentIomObj.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id).equals(datasetID)) {
                try {
                    tmpIomObj.add(currentIomObj);
                    long currentVersion = Long.parseLong(currentIomObj.getobjectoid());
                    if (currentVersion > maxVersion) {
                        maxVersion = currentVersion;
                        lastIomObj = currentIomObj;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
            }
        }
        
        // if lastIomObj is null, then it gets the biggest version via ObjectID.
        if (lastIomObj == null && tmpIomObj != null) {
            for (IomObject iomObj : tmpIomObj) {
                if (lastIomObj == null) {
                    lastIomObj = iomObj;
                } else {
                    String versionFromLastIomObj = lastIomObj.getobjectoid();
                    String versionFromCurrentIomObj = iomObj.getobjectoid();
                    if (versionFromLastIomObj.compareTo(versionFromCurrentIomObj) < 0) {
                        lastIomObj = iomObj;
                    }
                    
                }
            }
        }
        return lastIomObj;
    }

    private String getNewVersion(IomObject[] oldIlidataContents, String datasetID) {
        long maxVersion = 0;
        for (int i = 0; i < oldIlidataContents.length; i++) {
            IomObject currentIomObj = oldIlidataContents[i];
            if (currentIomObj.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_id).equals(datasetID)) {
                try {
                    long currentVersion = Long.parseLong(currentIomObj.getattrvalue(ch.interlis.models.DatasetIdx16.Metadata.tag_version));
                    if (currentVersion > maxVersion) {
                        maxVersion = currentVersion;
                    }
                } catch (NumberFormatException e) {
                    ;
                }
            }
        }
        return maxVersion != 0 ? String.valueOf(maxVersion + 1) : "1";
    }

    private long getMaxOid(IomObject[] oldIlidataContents) {
        long maxOid = 0;
        for (int i = 0; i < oldIlidataContents.length; i++) {
            IomObject currentIomObj = oldIlidataContents[i];
            try {
                long actualOid = Long.parseLong(currentIomObj.getobjectoid());
                if (actualOid > maxOid) {
                    maxOid = actualOid;
                }
            } catch (NumberFormatException e) {
                ;
            }
        }
        return maxOid;
    }

    private File getFileName(IomObject iomObject) {
        IomObject files = iomObject.getattrobj(ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata.tag_files, 0);
        IomObject file = files.getattrobj(ch.interlis.models.DatasetIdx16.DataFile.tag_file, 0);
        return new File(file.getattrvalue(ch.interlis.models.DatasetIdx16.File.tag_path));
    }

    private void writeNewIliData(File ilidataXmlFileToWrite, DatasetMetadata newMetadata, IomObject[] oldIlidataContents, File localCopyOfRemoteOriginalIliDataXml) throws Exception {
        XtfWriterBase ioxWriter = null;
        IoxReader ioxReader = null;
        try {
            ioxReader = CreateIliDataTool.createReader(localCopyOfRemoteOriginalIliDataXml);
            OutputStream outStream = new FileOutputStream(ilidataXmlFileToWrite);
            XtfWriterBase ioxWriter1 = new XtfWriterBase(outStream, ch.interlis.models.DATASETIDX16.getIoxMapping(), "2.3");
            ((XtfWriterBase) ioxWriter1).setModels(new XtfModel[] { ch.interlis.models.DATASETIDX16.getXtfModel() });
            ioxWriter = ioxWriter1;
            
            // Start Transfer Event 
            IoxEvent event = ioxReader.read();
            ioxWriter.write(event);
            
            // Start Basket Event
            event = ioxReader.read();
            ioxWriter.write(event);
            
            // Object Events 
            for (IomObject iomObj : oldIlidataContents) {
                ioxWriter.write(new ObjectEvent(iomObj));
            }
            
            // Updated New Meta Data
            ioxWriter.write(new ObjectEvent(newMetadata));
        } finally {
            ioxWriter.write(new EndBasketEvent());
            ioxWriter.write(new ch.interlis.iox_j.EndTransferEvent());
            
            if (ioxWriter != null) {
                ioxWriter.close(); 
            }
            if (ioxReader != null) {
                ioxReader.close();
            }
        }
        
    }

    private DatasetMetadata readDataFile(File newFileVersion, Settings settings) throws Exception {
        IoxReader ioxReader = null;
        ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata datasetMetadata = new ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata(Integer.toString(1));
        ch.interlis.models.DatasetIdx16.File file = new ch.interlis.models.DatasetIdx16.File();
        DataFile dataFile = new DataFile();
        
        String filePath = CreateIliDataTool.getURLRelativePath(newFileVersion);
        file.setpath(filePath);
        dataFile.addfile(file);
        datasetMetadata.addfiles(dataFile);

        String owner = CreateIliDataTool.getOwnerByCurrentUser();
        datasetMetadata.setowner(owner);
        
        // Get Model names from local File
        List<String> models = IoxUtility.getModels(newFileVersion);
        TransferDescription td = null;
        try {
            td = CreateIliDataTool.compileIli(models, new File(settings.getValue(Validator.SETTING_UPDATE_ILIDATA)).getParent());
        } catch (Exception e) {
            throw new Exception("An error occurred while reading the file: " + newFileVersion.getAbsolutePath() + e);
        }
        
        if (td == null) {
            throw new Exception("Transfer Description can not be null for the file: " + newFileVersion.getAbsolutePath());
        }
        String md5 = RepositoryAccess.calcMD5(newFileVersion);
        file.setmd5(md5);
        
        ioxReader = CreateIliDataTool.createReader(newFileVersion);
        if (ioxReader instanceof ItfReader) {
            dataFile.setfileFormat("application/interlis+txt;version=1.0");
        } else {
            dataFile.setfileFormat("application/interlis+xml;version=2.3");
        }
        
        try {
            IoxEvent event = null;
            Model model=null;
            do {
                event = ioxReader.read();
                if (event instanceof StartBasketEvent) {
                    StartBasketEvent basketEvent=(StartBasketEvent)event;
                    // fill expected values to BasketMetaData
                    ch.interlis.models.DatasetIdx16.DataIndex.BasketMetadata basketMetaData = new ch.interlis.models.DatasetIdx16.DataIndex.BasketMetadata();
                    Topic topic = (Topic) td.getElement(basketEvent.getType());
                    if(model == null) {
                        model=(Model) topic.getContainer();
                        
                        String idgeoiv = model.getMetaValue("IDGeoIV");
                        if (idgeoiv != null) {
                            String ids[]=idgeoiv.split("\\,");
                            for(String geoid:ids) {
                                Code_ idgeoivCode = new Code_();
                                idgeoivCode.setvalue("https://ids.geo.admin.ch/geoiv/"+geoid.trim());
                                datasetMetadata.addcategories(idgeoivCode);
                            }
                        }
                        if (model.getMetaValue("furtherInformation") != null) {
                            datasetMetadata.setfurtherInformation(model.getMetaValue("furtherInformation"));
                        }        
                        if (model.getMetaValue("technicalContact") != null) {
                            datasetMetadata.settechnicalContact(model.getMetaValue("technicalContact"));
                        }
                        
                        CreateIliDataTool.setShortDescription(datasetMetadata, model.getDocumentation());
                    }
                    
                    ModelLink modelLink = new ModelLink();
                    modelLink.setname(basketEvent.getType());
                    basketMetaData.setmodel(modelLink);
                    basketMetaData.setowner(owner);
                    basketMetaData.setversion("Test");
                    if(topic.getBasketOid() != null) {
                        basketMetaData.setid(basketEvent.getBid());
                    }else {
                        basketMetaData.setlocalId(basketEvent.getBid());
                    }
                    datasetMetadata.addbaskets(basketMetaData);
                }
            } while (!(event instanceof EndTransferEvent));
        } finally {
            if (ioxReader != null) {
                try {
                    ioxReader.close();
                } catch (Exception e) {
                    EhiLogger.logState("An error occurred while closing the file." + e);
                }
                ioxReader = null;
            }
        }
        return datasetMetadata;
    }

    protected static IomObject[] readIliData(File localCopyOfRemoteOriginalIliDataXml) throws Exception {
        
        XtfReader reader = null;
        List<IomObject> orginalIomObject = new ArrayList<IomObject>(); 
        try {
            reader = new XtfReader(localCopyOfRemoteOriginalIliDataXml);
            IoxEvent event = null;
            do {
                event = reader.read();
                if (event instanceof ObjectEvent) {
                    IoxEvent event1 = event;
                    IomObject iomObject = ((ObjectEvent) event1).getIomObject();
                    orginalIomObject.add(iomObject);
                }
            } while (!(event instanceof EndTransferEvent));
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return orginalIomObject.toArray(new IomObject[orginalIomObject.size()]);
    }
}
