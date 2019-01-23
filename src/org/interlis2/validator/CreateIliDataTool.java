package org.interlis2.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.XtfModel;
import ch.interlis.iom_j.xtf.XtfWriterBase;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.IoxUtility;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.utility.ReaderFactory;
import ch.interlis.models.DatasetIdx16.Code_;
import ch.interlis.models.DatasetIdx16.DataFile;
import ch.interlis.models.DatasetIdx16.LocalisedMText;
import ch.interlis.models.DatasetIdx16.ModelLink;
import ch.interlis.models.DatasetIdx16.MultilingualMText;
import ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata;

public class CreateIliDataTool {
   
    public static boolean start(Settings settings) {
        return new CreateIliDataTool().createIliData(settings);
    }

    private boolean createIliData(Settings settings) {
        String destinationFile = settings.getValue(Validator.SETTING_ILIDATA_XML);
        String srcFilelist = settings.getValue(Validator.SETTING_REMOTEFILE_LIST);
        String baseUrl = settings.getValue(Validator.SETTING_REPOSITORY_TO_SCAN);

        try {
            Set<File> visitedFiles = new HashSet<File>();
            if (srcFilelist != null) {
                visitAllFilesWithUrl(srcFilelist, baseUrl, visitedFiles);
            } else {
                visitAllFiles(baseUrl, null, visitedFiles);
            }
            readFilesFromSourceFolder(new File(destinationFile), visitedFiles, baseUrl, settings);
        }catch (Exception e) {
            EhiLogger.logError(e);
            return false;
        }
        return true;
    }
    
    private void readFilesFromSourceFolder(File destFile, Set<File> allFiles, String baseUrl, Settings settings) 
                                                                                    throws Exception {
        XtfWriterBase ioxWriter = null;
        try {
            OutputStream outStream = new FileOutputStream(destFile.getPath());
            XtfWriterBase ioxWriter1 = new XtfWriterBase(outStream, ch.interlis.models.DATASETIDX16.getIoxMapping(), "2.3");
            ((XtfWriterBase) ioxWriter1).setModels(new XtfModel[] { ch.interlis.models.DATASETIDX16.getXtfModel() });
            StartTransferEvent startTransferEvent = new StartTransferEvent();
            ((StartTransferEvent) startTransferEvent).setSender(Main.APP_NAME + "-" + Main.getVersion());
            ioxWriter1.write(startTransferEvent);
            ioxWriter = ioxWriter1;
            StartBasketEvent startBasketEvent = new StartBasketEvent(ch.interlis.models.DATASETIDX16.DataIndex, "b1");
            ioxWriter.write(startBasketEvent);
            int oid = 1;
            IoxReader ioxReader = null;
            for (File currentFile : allFiles) {
                ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata datasetMetadata = new ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata(Integer.toString(oid++));
                ch.interlis.models.DatasetIdx16.File file = new ch.interlis.models.DatasetIdx16.File();
                DataFile dataFile = new DataFile();

                file.setpath(getURLRelativePath(currentFile));
                dataFile.addfile(file);
                datasetMetadata.addfiles(dataFile);
                
                // Convert CurrentFileName to DatasetMetadata.id format
                datasetMetadata.setid(getFileNameWithoutPfadExtensionAndDate(currentFile));
                String owner = getOwnerByCurrentUser();
                datasetMetadata.setowner(owner);
                datasetMetadata.setversion("1");
                
                File localFile=null;
                if (isRemoteRepository(baseUrl)) {
                    RepositoryAccess reposAccess=new RepositoryAccess();                
                    localFile = reposAccess.getLocalFileLocation(baseUrl, getURLRelativePath(currentFile), 0, null);
                    if(localFile==null) {
                        throw new IllegalStateException("failed to download remote file "+baseUrl+" "+currentFile.getPath());
                    }
                }else {
                    localFile=new File(new File(baseUrl), currentFile.getPath());
                }
                
                // Get Model names from local File
                List<String> models = IoxUtility.getModels(localFile);
                TransferDescription td = null;
                try {
                    td = compileIli(models, settings);
                } catch (Exception e) {
                    throw new Exception("An error occurred while reading the file: " + localFile.getAbsolutePath() + e);
                }
                
                if (td == null) {
                    throw new Exception("Transfer Description can not be null for the file: " + localFile.getAbsolutePath());
                }
                String md5=RepositoryAccess.calcMD5(localFile);
                file.setmd5(md5);
                
                ioxReader = createReader(localFile.getPath());
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
                            Topic topic=(Topic) td.getElement(basketEvent.getType());
                            if(model==null) {
                                model=(Model) topic.getContainer();
                                
                                String idgeoiv=model.getMetaValue("IDGeoIV");
                                if (idgeoiv != null) {
                                    String ids[]=idgeoiv.split("\\,");
                                    for(String geoid:ids) {
                                        Code_ idgeoivCode=new Code_();
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
                                
                                setShortDescription(datasetMetadata, model.getDocumentation());
                            }
                            
                            ModelLink modelLink = new ModelLink();
                            modelLink.setname(basketEvent.getType());
                            basketMetaData.setmodel(modelLink);
                            basketMetaData.setowner(owner);
                            basketMetaData.setversion("1");
                            if(topic.getBasketOid()!=null) {
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
                ioxWriter.write(new ObjectEvent(datasetMetadata));
            }                
        } finally {
            if (ioxWriter != null) {
                ioxWriter.write(new EndBasketEvent());
                ioxWriter.write(new ch.interlis.iox_j.EndTransferEvent());
                ioxWriter.close();                    
            }
        }
    }

    public static String getOwnerByCurrentUser() {
        return "mailto:" + System.getProperty("user.name") + "@localhost";
    }

    private boolean isRemoteRepository(String uri) {
        String urilc=uri.toLowerCase();
        if(urilc.startsWith("http:") || urilc.startsWith("https:")){
            return true;
        }
        return false;
    }

    private void setShortDescription(DatasetMetadata datasetMetadata, String documentation) {
        if (documentation != null) {
            MultilingualMText mText = new MultilingualMText();
            LocalisedMText localisedMText = new LocalisedMText();
            localisedMText.setText(documentation);
            mText.addLocalisedText(localisedMText);
            datasetMetadata.setshortDescription(mText);                                    
        }
    }

    private String getFileNameWithoutPfadExtensionAndDate(File currentFile) throws IllegalArgumentException {
        String filename = currentFile.getName();
        if (filename.endsWith(".xml")) {
            filename = filename.replace(".xml", "");
        } else if (filename.endsWith(".xtf")) {
            filename = filename.replace(".xtf", "");
        } else if (filename.endsWith(".itf")) {
            filename = filename.replace(".itf", "");
        } else {
            throw new IllegalArgumentException("Invalid file extension " + filename);
        }
        filename.trim();
        // Clear, if the filename has a date 
        if (filename.length() > 10) {
            String pattern = "^[_][0-9]{8}$";
            Pattern compiledPattern = Pattern.compile(pattern);
            String filenameTmp = filename.substring(filename.length() - 9, filename.length());
            Matcher matcher = compiledPattern.matcher(filenameTmp);
            if (matcher.matches()) {
                filename = filename.substring(0, filename.length() - 9).trim();
            }            
        }
        return filename;
    }

    private TransferDescription compileIli(List<String> models, Settings settings) throws Ili2cException {
        ArrayList<String> modeldirv = new ArrayList<String>();
        ArrayList<String> modelv = new ArrayList<String>();
        if (models != null) {
            modelv.addAll(models);
        }

        String ilidirs = settings.getValue(Validator.SETTING_ILIDIRS);
        if (ilidirs == null) {
            ilidirs = Validator.SETTING_DEFAULT_ILIDIRS;
        }
        EhiLogger.logState("ilidirs <" + ilidirs + ">");
        String modeldirs[] = ilidirs.split(";");
        for (int modeli = 0; modeli < modeldirs.length; modeli++) {
            String m = modeldirs[modeli];
            if (m != null && !m.isEmpty()) {
                modeldirv.add(m);
            }
        }

        ch.interlis.ilirepository.IliManager modelManager = new ch.interlis.ilirepository.IliManager();
        modelManager.setRepositories((String[]) modeldirv.toArray(new String[] {}));
        ch.interlis.ili2c.config.Configuration ili2cConfig = modelManager.getConfig(modelv, 0.0);
        ili2cConfig.setGenerateWarnings(false);
        ch.interlis.ili2c.Ili2c.logIliFiles(ili2cConfig);
        return ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
    }

    private String getURLRelativePath(File currentFile) {
        return currentFile.getPath().replace(File.separatorChar, '/');
    }

    private void visitAllFiles(String baseFolder, File subFolder, Set<File> visitedFiles) {
        File expectedFilePath = new File(baseFolder);
        File[] listOfFiles = expectedFilePath.listFiles();
        for (File file : listOfFiles) {
            if (file.isFile()) {
                if (isItfORXtfFilename(file.getName())) {
                    visitedFiles.add(new File(subFolder.getPath() + File.separator + file.getName()));
                }
            } else {
                String concatenedFolderName = subFolder == null ? file.getName() : subFolder.getName() + File.separator + file.getName();
                visitAllFiles(file.getPath(), new File(concatenedFolderName), visitedFiles);
            }
        }         
    }
    
    private void visitAllFilesWithUrl(String srcFile, String baseUrl, Set<File> visitedFiles) throws IOException, FileNotFoundException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(srcFile));
            String readFile;
            while ((readFile = br.readLine()) != null) {
                visitedFiles.add(new File(readFile));
            }   
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    private IoxReader createReader(String filename) throws IoxException {
        IoxReader ioxReader = new ReaderFactory().createReader(new java.io.File(filename), new LogEventFactory());
        if (ioxReader instanceof ItfReader2) {
            ioxReader = new ItfReader(new java.io.File(filename));
        }
        return ioxReader;
    }
    
    private static boolean isItfORXtfFilename(String filename) {
        String xtfExt=ch.ehi.basics.view.GenericFileFilter.getFileExtension(new java.io.File(filename)).toLowerCase();
        if("itf".equals(xtfExt) || "xtf".equals(xtfExt)){
            return true;
        }
        return false;
    }
}
