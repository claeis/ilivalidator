package org.interlis2.validator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.Ili2cMetaAttrs;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.Topic;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ili2c.modelscan.IliFile;
import ch.interlis.ili2c.modelscan.IliModel;
import ch.interlis.ilirepository.IliFiles;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.XtfModel;
import ch.interlis.iom_j.xtf.XtfWriterBase;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.IoxIliReader;
import ch.interlis.iox_j.utility.IoxUtility;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.utility.ReaderFactory;
import ch.interlis.models.DatasetIdx16.Code_;
import ch.interlis.models.DatasetIdx16.DataFile;
import ch.interlis.models.DatasetIdx16.LocalisedMText;
import ch.interlis.models.DatasetIdx16.LocalisedText;
import ch.interlis.models.DatasetIdx16.ModelLink;
import ch.interlis.models.DatasetIdx16.MultilingualMText;
import ch.interlis.models.DatasetIdx16.MultilingualText;
import ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata;

public class CreateIliDataTool {
   
    private static final String CODES_GEOIV = "https://ids.geo.admin.ch/geoiv/";
    private static final String CODES_MODEL = "http://codes.interlis.ch/model/";
    private static final String CODES_TYPE_REFERENCE_DATA = "http://codes.interlis.ch/type/referenceData";

    public static boolean start(Settings settings) {
        return new CreateIliDataTool().createIliData(settings);
    }

    private boolean createIliData(Settings settings) {
        String destinationFile = settings.getValue(Validator.SETTING_ILIDATA_XML);
        String filelistFile = settings.getValue(Validator.SETTING_REMOTEFILE_LIST);
        String baseUrl = settings.getValue(Validator.SETTING_REPOSITORY);
        ch.interlis.ili2c.Main.setHttpProxySystemProperties(settings);

        try {
            Set<File> filelist = new HashSet<File>();
            if (filelistFile != null) {
                readFilelistFromFile(filelistFile, filelist);
            } else {
                File localFolder=new File(baseUrl);
                if(!localFolder.exists() || !localFolder.isDirectory() || !localFolder.canRead()) {
                    EhiLogger.logError("can't read repos folder <"+baseUrl+">");
                    return false;
                }
                scanLocalFolder(localFolder, null, filelist);
            }
            readFilesFromSourceFolder(new File(destinationFile), filelist, baseUrl, settings);
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
            int oid = getStartTid(settings);
            //IoxReader ioxReader = null;
            final String defaultVersion = getDefaultVersion(settings);
            boolean addReferenceDataTag=true;
            for (File currentFile : allFiles) {
                String version=defaultVersion;
                StringBuffer versionBuf=new StringBuffer();
                getFileNameWithoutPfadExtensionAndDate(currentFile, versionBuf);
                if(versionBuf.length()>0) {
                    version=versionBuf.toString();
                }
                ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata datasetMetadata = generateNewDatasetMetaData(currentFile, settings, baseUrl, version, oid++,addReferenceDataTag);
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

    private String getDefaultVersion(Settings settings) {
        return "1";
    }

    private int getStartTid(Settings settings) {
        return 1;
    }

    protected DatasetMetadata generateNewDatasetMetaData(File currentFile, Settings settings, String baseUrl, String version, int oid,boolean addReferenceDataTag) throws Exception {
        IoxReader ioxReader = null;
        ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata datasetMetadata = new ch.interlis.models.DatasetIdx16.DataIndex.DatasetMetadata(Integer.toString(oid));
        ch.interlis.models.DatasetIdx16.File file = new ch.interlis.models.DatasetIdx16.File();
        DataFile dataFile = new DataFile();
        
        String filePath = getURLRelativePath(currentFile);
        file.setpath(filePath);
        dataFile.addfile(file);
        datasetMetadata.addfiles(dataFile);
        
        setTitle(datasetMetadata, currentFile);
        String id = getDatasetId(currentFile, baseUrl,settings);
        datasetMetadata.setid(id);
        String owner = getOwner(settings);
        datasetMetadata.setowner(owner);
        datasetMetadata.setversion(version);

        RepositoryAccess reposAccess = null;
        File localFile=null;
        if (isRemoteRepository(baseUrl)) {
            reposAccess = new RepositoryAccess();
            localFile = reposAccess.getLocalFileLocation(baseUrl, getURLRelativePath(currentFile), 0, null);
            if (localFile == null) {
                throw new IllegalStateException (
                        "failed to download remote file " + baseUrl + " " + currentFile.getPath());
            }
        } else {
            localFile = new File(new File(baseUrl), currentFile.getPath());
        }           
        IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
        LogEventFactory errFactory=new LogEventFactory();
        errFactory.setLogger(errHandler);

        // Get Model names from local File
        List<String> models = IoxUtility.getModels(localFile);
        String modelVersion = IoxUtility.getModelVersion(new String[] {localFile.getPath()}, errFactory);
        TransferDescription td = null;
        try {
            td=Validator.compileIli(modelVersion,models, null,localFile.getAbsoluteFile().getParentFile().getAbsolutePath(),Main.getAppHome(), settings);
        } catch (Exception e) {
            throw new Exception("Failed to read file <" + localFile.getAbsolutePath()+">",e);
        }
        
        if (td == null) {
            throw new Exception("Transfer Description can not be null for the file: " + localFile.getAbsolutePath());
        }
        String md5=RepositoryAccess.calcMD5(localFile);
        file.setmd5(md5);
        
        ioxReader = createReader(localFile);
        if (ioxReader instanceof IoxIliReader) {
            dataFile.setfileFormat(((IoxIliReader)ioxReader).getMimeType());
        } else {
            dataFile.setfileFormat(IoxIliReader.XTF_23);
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
                        
                        String furtherInformation=model.getMetaValue(Ili2cMetaAttrs.ILIMODELSXML_FURTHER_INFORMATION);
                        if (furtherInformation != null) {
                            datasetMetadata.setfurtherInformation(furtherInformation);
                        }        
                        String technicalContact=model.getMetaValue(Ili2cMetaAttrs.ILIMODELSXML_TECHNICAL_CONTACT);
                        if (technicalContact != null) {
                            datasetMetadata.settechnicalContact(technicalContact);
                        }
                        
                        //setShortDescription(datasetMetadata, model.getDocumentation(),model.getScopedName());
                        
                        if(addReferenceDataTag) {
                            Code_ referenceDataCode=new Code_();
                            referenceDataCode.setvalue(CODES_TYPE_REFERENCE_DATA);
                            datasetMetadata.addcategories(referenceDataCode);
                            if(reposAccess!=null) {
                                addModelCodes(datasetMetadata,model,reposAccess.getIliFiles(baseUrl));
                            }
                        }
                    }
                    
                    ModelLink modelLink = new ModelLink();
                    modelLink.setname(basketEvent.getType());
                    basketMetaData.setmodel(modelLink);
                    basketMetaData.setowner(owner);
                    basketMetaData.setversion(version);
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
        return datasetMetadata;
    }

    private static String getDatasetId(File currentFile, String baseUrl, Settings settings) 
            throws MalformedURLException 
    {
        String id = getFileNameWithoutPfadExtensionAndDate(currentFile,null);
        if(isRemoteRepository(baseUrl)) {
            java.net.URL url=new java.net.URL(baseUrl);
            String domains[]=url.getHost().split("\\.");
            StringBuffer domainName=new StringBuffer();
            String sep="";
            for(int i=domains.length-1;i>=0;i--) {
                domainName.append(sep);
                domainName.append(domains[i]);
                sep=".";
            }
            id=domainName+"."+id;
        }
        return id;
    }

    private void addModelCodes(DatasetMetadata datasetMetadata, Model modelx, IliFiles ilifiles) {
        String modelName=modelx.getName();
        double iliVersion=Double.parseDouble(modelx.getIliVersion());
        List<String> useModels=new ArrayList<String>();
        Iterator<IliFile> filei=ilifiles.iteratorFile();
        while(filei.hasNext()){
            IliFile ilifile=filei.next();
            Iterator<IliModel> modeli=ilifile.iteratorModel();
            while(modeli.hasNext()){
                IliModel model=modeli.next();
                if(model.getIliVersion()==iliVersion){             
                    for(Iterator<String> depIt=model.getDependencies().iterator();depIt.hasNext();) {
                        String dep=depIt.next();
                        if(dep.equals(modelName)){
                            if(!useModels.contains(model.getName())) {
                                useModels.add(model.getName());
                            }
                        }
                    }
                }
            }
        }
        for(String model:useModels) {
            if("INTERLIS".equals(model)) {
                // ignore model INTERLIS
            }else {
                Code_ modelCode=new Code_();
                modelCode.setvalue(CODES_MODEL+model);
                datasetMetadata.addcategories(modelCode);
            }
        }
        
    }

    protected static String getOwner(Settings settings) {
        return "mailto:models@geo.admin.ch";
        //return "mailto:" + System.getProperty("user.name") + "@localhost";
    }

    private static boolean isRemoteRepository(String uri) {
        String urilc=uri.toLowerCase();
        if(urilc.startsWith("http:") || urilc.startsWith("https:")){
            return true;
        }
        return false;
    }

    protected static void setShortDescription(DatasetMetadata datasetMetadata, String documentation,String name) {
        if (documentation != null || name!=null) {
            MultilingualMText mText = new MultilingualMText();
            LocalisedMText localisedMText = new LocalisedMText();
            if(documentation!=null) {
                localisedMText.setText(documentation);
            }else {
                localisedMText.setText(name);
            }
            mText.addLocalisedText(localisedMText);
            datasetMetadata.setshortDescription(mText);                                    
        }
    }
    protected static void setTitle(DatasetMetadata datasetMetadata, File currentFile) {
        
        if (currentFile != null) {
            String amt=currentFile.getParent();;
            String name=getFileNameWithoutPfadExtensionAndDate(currentFile, null);
            
            MultilingualText mText = new MultilingualText();
            LocalisedText localisedMText = new LocalisedText();
            localisedMText.setText("Katalog "+amt+" "+name);
            mText.addLocalisedText(localisedMText);
            datasetMetadata.settitle(mText);                                    
        }
    }

    protected static String getFileNameWithoutPfadExtensionAndDate(File currentFile,StringBuffer version) throws IllegalArgumentException {
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
        // Check, if the filename has a date 
        if (filename.length() > 10) {
            String pattern = "^[_][0-9]{8}$";
            Pattern compiledPattern = Pattern.compile(pattern);
            String filenameTmp = filename.substring(filename.length() - 9, filename.length());
            Matcher matcher = compiledPattern.matcher(filenameTmp);
            if (matcher.matches()) {
                if(version!=null) {
                    version.append(filename.substring(filename.length() - 8).trim());
                }
                filename = filename.substring(0, filename.length() - 9).trim();
            }            
        }
        return filename;
    }


    protected static String getURLRelativePath(File currentFile) {
        return currentFile.getPath().replace(File.separatorChar, '/');
    }

    private void scanLocalFolder(File baseFolder, String subFolder, Set<File> visitedFiles) {
        File expectedFilePath = subFolder==null ? baseFolder : new File(baseFolder,subFolder);
        File[] listOfFiles = expectedFilePath.listFiles();
        for (File file : listOfFiles) {
            String relativeFileName = subFolder == null ? file.getName() : subFolder + "/" + file.getName();
            if (file.isFile()) {
                if (isItfORXtfFilename(file.getName())) {
                    visitedFiles.add(new File(relativeFileName));
                }
            } else if(file.isDirectory()){
                scanLocalFolder(baseFolder, relativeFileName, visitedFiles);
            }
        }         
    }
    
    private void readFilelistFromFile(String srcFile, Set<File> filelist) throws IOException, FileNotFoundException {
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(srcFile));
            String readFile;
            while ((readFile = br.readLine()) != null) {
                filelist.add(new File(readFile));
            }   
        } finally {
            if (br != null) {
                br.close();
            }
        }
    }

    protected static IoxReader createReader(File filename) throws IoxException {
        IoxReader ioxReader = new ReaderFactory().createReader(filename, new LogEventFactory());
        if (ioxReader instanceof ItfReader2) {
            ioxReader = new ItfReader(filename);
        }
        return ioxReader;
    }
    
    private boolean isItfORXtfFilename(String filename) {
        String xtfExt=ch.ehi.basics.view.GenericFileFilter.getFileExtension(new java.io.File(filename)).toLowerCase();
        if("itf".equals(xtfExt) || "xtf".equals(xtfExt)){
            return true;
        }
        return false;
    }
}
