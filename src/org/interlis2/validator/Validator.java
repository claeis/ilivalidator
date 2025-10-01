package org.interlis2.validator;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import ch.interlis.iox_j.statistics.Stopwatch;
import org.interlis2.validator.impl.ErrorTracker;
import org.interlis2.validator.refmapping.RefMapping;

import ch.ehi.basics.logging.AbstractStdListener;
import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.StdListener;
import ch.ehi.basics.settings.Settings;
import ch.ehi.basics.types.OutParam;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.Ili2cFailure;
import ch.interlis.ili2c.gui.UserSettings;
import ch.interlis.ili2c.metamodel.GraphicParameterDef;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.ilirepository.Dataset;
import ch.interlis.ilirepository.IliManager;
import ch.interlis.ilirepository.impl.RepositoryAccess;
import ch.interlis.ilirepository.impl.RepositoryAccessException;
import ch.interlis.iom_j.itf.ItfReader;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.Xtf23Reader;
import ch.interlis.iom_j.xtf.Xtf24Reader;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox_j.IoxIliReader;
import ch.interlis.iox_j.PipelinePool;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.iox_j.inifile.IniFileReader;
import ch.interlis.iox_j.inifile.MetaConfig;
import ch.interlis.iox_j.logging.CsvErrorsLogger;
import ch.interlis.iox_j.logging.FileLogger;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.logging.StdLogger;
import ch.interlis.iox_j.logging.XtfErrorsLogger;
import ch.interlis.iox_j.plugins.IoxPlugin;
import ch.interlis.iox_j.plugins.PluginLoader;
import ch.interlis.iox_j.statistics.IoxStatistics;
import ch.interlis.iox_j.utility.IoxUtility;
import ch.interlis.iox_j.utility.ReaderFactory;
import ch.interlis.iox_j.validator.InterlisFunction;
import ch.interlis.iox_j.validator.ValidationConfig;
import ch.interlis.models.DatasetIdx16.DataFile;

/** High-level API of the INTERLIS validator.
 * For a usage example of this class, see the implementation of class {@link Main}.
 * For a usage example of the low-level API, see the implementation of {@link #validate(String, Settings)}. 
 */
public class Validator {
	
    public static final String ILIVRUNTIME_SCOPE = "IliVRuntime.Scope";
    public static final String MSG_VALIDATION_DONE = "...validation done";
    public static final String MSG_VALIDATION_FAILED = "...validation failed";
    public static boolean runValidation(
			String dataFilename,
			Settings settings
		) {
		if(dataFilename==null  || dataFilename.length()==0){
			EhiLogger.logError("no INTERLIS file given");
			return false;
		}
		return runValidation(new String[]{dataFilename},settings);
	}
	
    public static boolean runValidation(
            String dataFiles[],
            Settings settings
        ) {
        return new Validator().validate(dataFiles,settings);
    }
	/** main workhorse function.
	 * @param dataFilename File to validate.
	 * @param settings Configuration of program. 
	 * This is not the INI file, that controls the model specific validation.
	 * @return true if validation succeeds, false if it fails (or any program error). 
	 * @see #SETTING_MODELNAMES
	 * @see #SETTING_ILIDIRS
	 * @see #SETTING_CONFIGFILE
     * @see #SETTING_META_CONFIGFILE
	 * @see #SETTING_LOGFILE
	 * @see #SETTING_XTFLOG
     * @see #SETTING_CSVLOG
	 * @see #SETTING_PLUGINDIR
	 */
	public boolean validate(
			String dataFiles[],
			Settings settings
		) {
		if(dataFiles==null  || dataFiles.length==0){
			EhiLogger.logError("no INTERLIS file given");
			return false;
		}
		if(settings==null){
			settings=new Settings();
		}
	    String logFilename=settings.getValue(Validator.SETTING_LOGFILE);
	    String xtflogFilename=settings.getValue(Validator.SETTING_XTFLOG);
        String csvlogFilename=settings.getValue(Validator.SETTING_CSVLOG);
	    boolean doTimestamp=TRUE.equals(settings.getValue(Validator.SETTING_LOGFILE_TIMESTAMP));
		FileLogger logfile=null;
		XtfErrorsLogger xtflog=null;
        CsvErrorsLogger csvlog=null;
		AbstractStdListener logStderr=null;
		boolean ret=false;
		try{
			// setup logging of validation results
			if(logFilename!=null){
			    File f=new java.io.File(logFilename);
			    try {
                    if(isWriteable(f)) {
                        logfile=new FileLogger(f,doTimestamp);
                        EhiLogger.getInstance().addListener(logfile);
                    }else {
                        EhiLogger.logError("failed to write to logfile <"+f.getPath()+">");
                        return false;
                    }
                } catch (IOException e) {
                    EhiLogger.logError("failed to write to logfile <"+f.getPath()+">",e);
                    return false;
                }
			}
			if(xtflogFilename!=null){
			    File f=new java.io.File(xtflogFilename);
                try {
                    if(isWriteable(f)) {
                        xtflog=new XtfErrorsLogger(f, Main.APP_NAME+"-"+Main.getVersion());
                        EhiLogger.getInstance().addListener(xtflog);
                    }else {
                        EhiLogger.logError("failed to write to logfile <"+f.getPath()+">");
                        return false;
                    }
                } catch (IOException e) {
                    EhiLogger.logError("failed to write to logfile <"+f.getPath()+">",e);
                    return false;
                }
			}
            if(csvlogFilename!=null){
                File f=new java.io.File(csvlogFilename);
                try {
                    if(isWriteable(f)) {
                        csvlog=new CsvErrorsLogger(f);
                        EhiLogger.getInstance().addListener(csvlog);
                    }else {
                        EhiLogger.logError("failed to write to logfile <"+f.getPath()+">");
                        return false;
                    }
                } catch (IOException e) {
                    EhiLogger.logError("failed to write to logfile <"+f.getPath()+">",e);
                    return false;
                }
            }
			if(!TRUE.equals(settings.getValue(SETTING_DISABLE_STD_LOGGER))) {
				logStderr=new StdLogger(logFilename);
				EhiLogger.getInstance().addListener(logStderr);
				EhiLogger.getInstance().removeListener(StdListener.getInstance());
			}else {
				logStderr=new ErrorTracker();
				EhiLogger.getInstance().addListener(logStderr);
			}
            String metaConfigFilename=settings.getValue(Validator.SETTING_META_CONFIGFILE);
		    String pluginFolder=settings.getValue(Validator.SETTING_PLUGINFOLDER);
		    String appHome=settings.getValue(Validator.SETTING_APPHOME);
		    		    
		    // give user important info (such as input files or program version)
			EhiLogger.logState(Main.APP_NAME+"-"+Main.getVersion());
			EhiLogger.logState("ili2c-"+ch.interlis.ili2c.Ili2c.getVersion());
			EhiLogger.logState("iox-ili-"+ch.interlis.iox_j.utility.IoxUtility.getVersion());
			EhiLogger.logState("java.version "+System.getProperty("java.version"));
            EhiLogger.logState("User <"+java.lang.System.getProperty("user.name")+">");
            String DATE_FORMAT = "yyyy-MM-dd HH:mm";
            SimpleDateFormat dateFormat = new java.text.SimpleDateFormat(DATE_FORMAT);
            EhiLogger.logState("Start date "+dateFormat.format(new java.util.Date()));
			EhiLogger.logState("maxMemory "+java.lang.Runtime.getRuntime().maxMemory()/1024L+" KB");
			Stopwatch stopwatch = new Stopwatch();
			stopwatch.Start();
			for(String dataFile:dataFiles){
				EhiLogger.logState("dataFile <"+dataFile+">");
			}
            if(metaConfigFilename!=null){
                EhiLogger.logState("metaConfigFile <"+metaConfigFilename+">");
            }
			if(pluginFolder!=null){
				EhiLogger.logState("pluginFolder <"+pluginFolder+">");
			}
		
			td=null;
			
            ch.interlis.ilirepository.IliManager repoManager=createRepositoryManager(new File(dataFiles[0]).getAbsoluteFile().getParentFile().getAbsolutePath(),appHome,settings);
			
			// get local copy of metaConfigFile
            if(metaConfigFilename!=null) {
                List<String> metaConfigFiles=new ArrayList<String>();
                java.util.Set<String> visitedFiles=new HashSet<String>();
                metaConfigFiles.add(metaConfigFilename);
                Settings metaSettings=new Settings();
                while(!metaConfigFiles.isEmpty()) {
                    metaConfigFilename=metaConfigFiles.remove(0);
                    if(!visitedFiles.contains(metaConfigFilename)) {
                        visitedFiles.add(metaConfigFilename);
                        EhiLogger.traceState("metaConfigFile <"+metaConfigFilename+">");
                        File metaConfigFile;
                        try {
                            metaConfigFile = IliManager.getLocalCopyOfReposFile(repoManager,metaConfigFilename);
                        } catch (Ili2cException e1) {
                            EhiLogger.logError("failed to get local copy of meta-config file <"+metaConfigFilename+">", e1);
                            return false;
                        }
                        OutParam<String> baseConfigs=new OutParam<String>();
                        Settings newSettings=null;
                        try {
                            newSettings = readMetaConfig(metaConfigFile,baseConfigs);
                            if(baseConfigs.value!=null) {
                                String[] baseConfigv = baseConfigs.value.split(";");
                                for(String baseConfig:baseConfigv){
                                    metaConfigFiles.add(baseConfig);
                                }
                            }
                        } catch (IOException e) {
                            EhiLogger.logError("failed to read meta config file <"+metaConfigFile.getPath()+">", e);
                            return false;
                        }
                        MetaConfig.mergeSettings(newSettings,metaSettings);
                    }
                }
                MetaConfig.mergeSettings(metaSettings,settings);
            }
            MetaConfig.removeNullFromSettings(settings);
            
            String configFilename=settings.getValue(Validator.SETTING_CONFIGFILE);
            String modelNames=settings.getValue(Validator.SETTING_MODELNAMES);
            if(configFilename!=null){
                EhiLogger.logState("configFile <"+configFilename+">");
            }
            if(modelNames!=null){
                EhiLogger.logState("modelNames <"+modelNames+">");
            }
            skipPolygonBuilding = ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES_DO.equals(settings.getValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES));

            String dataFromSettings=settings.getValue(Validator.SETTING_DATA);
            if(dataFromSettings!=null) {
                String[] datav = dataFromSettings.split(";");
                String newdata[]=new String[datav.length+dataFiles.length];
                int idx=0;
                for(idx=0;idx<datav.length;idx++){
                    newdata[idx]=datav[idx];
                }
                for(idx=0;idx<dataFiles.length;idx++){
                    newdata[datav.length+idx]=dataFiles[idx];
                }
                dataFiles=newdata;
            }
            String[] refDataFiles=getValueList(settings.getValue(Validator.SETTING_REF_DATA));
            for(String refDataFile:refDataFiles){
                EhiLogger.logState("refDataFile <"+refDataFile+">");
            }
            String[] refMappingFiles=getValueList(settings.getValue(Validator.SETTING_REF_MAPPING_DATA));
            for(String refDataFile:refMappingFiles){
                EhiLogger.logState("refMappingFile <"+refDataFile+">");
            }
			// get local copies of remote files
            for(int idx=0;idx<dataFiles.length;idx++){
                String dataFile=dataFiles[idx];
                java.io.File localFile;
                try {
                    localFile = IliManager.getLocalCopyOfReposFile(repoManager, dataFile);
                } catch (Ili2cException e) {
                    EhiLogger.logError("failed to get local copy of data file <"+dataFile+">", e);
                    return false;
                }
                dataFiles[idx]=localFile.getPath();
            }
            for(int idx=0;idx<refMappingFiles.length;idx++){
                String dataFile=refMappingFiles[idx];
                java.io.File localFile;
                try {
                    localFile = IliManager.getLocalCopyOfReposFile(repoManager, dataFile);
                } catch (Ili2cException e) {
                    EhiLogger.logError("failed to get local copy of data file <"+dataFile+">", e);
                    return false;
                }
                refMappingFiles[idx]=localFile.getPath();
            }
            // get local copy of configFile
            java.io.File configFile=null;
            if(configFilename!=null) {
                try {
                    configFile=IliManager.getLocalCopyOfReposFile(repoManager,configFilename);
                } catch (Ili2cException e) {
                    EhiLogger.logError("failed to get local copy of config file <"+configFilename+">", e);
                    return false;
                }
            }
            
			// specified model names
			List<String> modelnames=new ArrayList<String>();
			String specifiedModelNames=null;
			if(settings.getValue(Validator.SETTING_MODELNAMES)!=null) {
				specifiedModelNames=settings.getValue(Validator.SETTING_MODELNAMES);
				List<String> modelNameList=getSpecifiedModelNames(specifiedModelNames);
				modelnames.addAll(modelNameList);
			}else {
				// find out, which ili model is required
				for(String dataFile:dataFiles){
					List<String> modelnameFromFile=ch.interlis.iox_j.IoxUtility.getModels(new java.io.File(dataFile));
					if(modelnameFromFile==null){
						return false;
					}
					modelnames.addAll(modelnameFromFile);
				}
				String localRefDataFiles[]=new String[refDataFiles.length];
                for(int idx=0;idx<refDataFiles.length;idx++){
                    String dataFile=refDataFiles[idx];
                    java.io.File localFile;
                    try {
                        localFile = IliManager.getLocalCopyOfReposFile(repoManager, dataFile);
                    } catch (Ili2cException e) {
                        EhiLogger.logError("failed to get local copy of data file <"+dataFile+">", e);
                        return false;
                    }
                    localRefDataFiles[idx]=localFile.getPath();
                }
                for(String dataFile:localRefDataFiles){
                    List<String> modelnameFromFile=ch.interlis.iox_j.IoxUtility.getModels(new java.io.File(dataFile));
                    if(modelnameFromFile==null){
                        return false;
                    }
                    modelnames.addAll(modelnameFromFile);
                }
			}
			for(String dataFile:dataFiles){
				if(isItfFilename(dataFile)){
					settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE_DO);
				}
			}
			List<String> modelNamesFromConfig = null;
			if(configFile!=null){
				try {
					modelNamesFromConfig=getModelsFromConfigFile(configFile);
					boolean versionControl = getVersionControlFormConfigFile(configFile);
					if (versionControl) {
					    settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_XTF_VERIFYMODEL, ch.interlis.iox_j.validator.Validator.CONFIG_DO_XTF_VERIFYMODEL_DO);
					}
					modelnames.addAll(modelNamesFromConfig);
				} catch (IOException e) {
					EhiLogger.logError("failed to read config file <"+configFile.getPath()+">", e);
					return false;
				}
			}
            PluginLoader loader=new PluginLoader();
            loader.loadPlugins();
			if(pluginFolder!=null){
				loader.loadPlugins(new File(pluginFolder));
			}
            Map<String,Class> userFunctions=new java.util.HashMap<String,Class>();
            userFunctions.putAll(PluginLoader.getInterlisFunctions(loader.getAllPlugins()));
            Map<String,Class> definedFunctions=(Map<String,Class>)settings.getTransientObject(ch.interlis.iox_j.validator.Validator.CONFIG_CUSTOM_FUNCTIONS);
            if(definedFunctions!=null) {
                userFunctions.putAll(definedFunctions);
            }
            settings.setTransientObject(ch.interlis.iox_j.validator.Validator.CONFIG_CUSTOM_FUNCTIONS, userFunctions);
            List<Class> userReaders=new java.util.ArrayList<Class>();
            userReaders.addAll(getIoxReaders(loader.getAllPlugins()));
            List<Class> definedReaders=(List<Class>)settings.getTransientObject(ReaderFactory.CONFIG_CUSTOM_READERS);
            if(definedReaders!=null) {
                userReaders.addAll(definedReaders);
            }
            settings.setTransientObject(ReaderFactory.CONFIG_CUSTOM_READERS, userReaders);
            IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
            LogEventFactory errFactory=new LogEventFactory();
            errFactory.setLogger(errHandler);
            
			String modelVersion=null;
			try {
	            modelVersion = IoxUtility.getModelVersion(dataFiles, errFactory,settings);
			}catch(IoxException ex) {
			    EhiLogger.logAdaption("failed to get version from data file; "+ex.toString()+"; ignored");
			}
			
			// read ili models
			td=compileIli(repoManager,modelVersion,modelnames, null, settings);
			if(td==null){
				return false;
			}
			String scope=settings.getValue(Validator.SETTING_VALIDATION_SCOPE);
			td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_RUNTIME_SYSTEM_NAME, Main.APP_NAME);
            td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_RUNTIME_SYSTEM_VERSION, Main.getVersion());
			String rtTxt=settings.getValue(SETTING_RUNTIME_PARAMETERS);
			if(rtTxt!=null) {
			    String rtv[]=rtTxt.split(";");
			    for(String rt:rtv) {
			        String kv[]=rt.split("=");
			        if(kv.length==2) {
			            if(kv[0]!=null) {
			                if(td.getElement(kv[0])==null) {
			                    EhiLogger.logAdaption("unknown runtime parameter <"+kv[0]+">; ignored");
			                }else {
	                            if(kv[1]!=null) {
	                                EhiLogger.logState("runtime parameter "+kv[0]+" <"+kv[1]+">");
	                                td.setActualRuntimeParameter(kv[0], kv[1]);
	                            }
			                }
			            }else {
                            EhiLogger.logError("strange runtime parameter syntax <"+rt+">");
                            return false;
			            }
			        }else {
	                    EhiLogger.logError("strange runtime parameter syntax <"+rt+">");
	                    return false;
			        }
			    }
			}
            if(scope!=null) {
                td.setActualRuntimeParameter(ILIVRUNTIME_SCOPE, scope);
            }else {
                scope=(String)td.getActualRuntimeParameter(ILIVRUNTIME_SCOPE);
            }
            if(scope!=null) {
                EhiLogger.logState("validatonScope <"+scope+">");
            }
			// read mapping files
			RefMapping refMapping=new RefMapping();
            for(String file:refMappingFiles){
                refMapping.addFile(new File(file));
            }
			// process data files
			EhiLogger.logState("validate data...");
			ch.interlis.iox_j.validator.Validator validator=null;
			long startTime=System.currentTimeMillis();
			long currentSlice=0l;
			try{
				// setup log output
				ValidationConfig modelConfig=new ValidationConfig();
				modelConfig.mergeIliMetaAttrs(td);
				if(configFile!=null){
					modelConfig.mergeConfigFile(configFile);
				}
				final String settingsForceTypeValidation = settings.getValue(SETTING_FORCE_TYPE_VALIDATION);
				if(settingsForceTypeValidation!=null) {
	                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ALLOW_ONLY_MULTIPLICITY_REDUCTION, TRUE.equals(settingsForceTypeValidation)?ValidationConfig.ON:null);
				}
				String settingsDisableAreaValidation = settings.getValue(SETTING_DISABLE_AREA_VALIDATION);
				if(settingsDisableAreaValidation!=null) {
	                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.AREA_OVERLAP_VALIDATION, TRUE.equals(settingsDisableAreaValidation)?ValidationConfig.OFF:null);
				}
				final String disableConstraintValidation = settings.getValue(SETTING_DISABLE_CONSTRAINT_VALIDATION);
				if(disableConstraintValidation!=null) {
	                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.CONSTRAINT_VALIDATION, TRUE.equals(disableConstraintValidation)?ValidationConfig.OFF:null);
				}
				final String settingsAllObjectsAccessible = settings.getValue(SETTING_ALL_OBJECTS_ACCESSIBLE);
				if(settingsAllObjectsAccessible!=null) {
	                modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ALL_OBJECTS_ACCESSIBLE, settingsAllObjectsAccessible);
				}
                final String mandatoryBaskets = settings.getValue(SETTING_MANDATORY_BASKETS);
                if(mandatoryBaskets!=null) {
                    modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.MANDATORY_BASKETS, mandatoryBaskets);
                }
                final String optionalBaskets = settings.getValue(SETTING_OPTIONAL_BASKETS);
                if(optionalBaskets!=null) {
                    modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.OPTIONAL_BASKETS, optionalBaskets);
                }
                final String bannedBaskets = settings.getValue(SETTING_BANNED_BASKETS);
                if(bannedBaskets!=null) {
                    modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.BANNED_BASKETS, bannedBaskets);
                }
				allowItfAreaHoles = TRUE.equals(settings.getValue(SETTING_ALLOW_ITF_AREA_HOLES));
				skipGeometryErrors=ValidationConfig.OFF.equals(modelConfig.getConfigValue(ValidationConfig.PARAMETER, ValidationConfig.DEFAULT_GEOMETRY_TYPE_VALIDATION));
				String settingsMultiplicityValidation=settings.getValue(SETTING_MULTIPLICITY_VALIDATION);
				if(settingsMultiplicityValidation!=null){
					modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.MULTIPLICITY, settingsMultiplicityValidation);
				}
                String settingsSimpleBoundary=settings.getValue(SETTING_SIMPLE_BOUNDARY);
                if(settingsSimpleBoundary!=null){
                    modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.SIMPLE_BOUNDARY, settingsSimpleBoundary);
                }
				if (modelNamesFromConfig == null || modelNamesFromConfig.size() == 0) {
				    if (specifiedModelNames != null) {
				        modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ADDITIONAL_MODELS, specifiedModelNames);
				    }
				}
				
				PipelinePool pool=new PipelinePool();
				validator=new ch.interlis.iox_j.validator.Validator(td,modelConfig, errHandler, errFactory, pool,settings);
				validator.setAutoSecondPass(false);
				statistics=new IoxStatistics(td,settings);
				java.util.Set<String> topics=new HashSet<String>();
				// loop over data objects
				for(String filename:dataFiles){
					// setup data reader (ITF or XTF)
					IoxReader ioxReader=null;
					ioxReader = createReader(filename, td,errFactory,settings,pool);
			        if(ioxReader instanceof IoxIliReader){
			            ((IoxIliReader) ioxReader).setModel(td);    
			        }
			        String fileMd5=RepositoryAccess.calcMD5(new File(filename));
					statistics.setFilename(filename);
					errFactory.setDataSource(filename);
		            td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_CURRENT_TRANSFERFILE, filename);
					try{
						IoxEvent event=null;
						do{
				            long currentTime=System.currentTimeMillis();
				            long slice=(currentTime-startTime)/1000l/60l/10l;
						    if(slice>currentSlice) {
						        currentSlice=slice;
	                            EhiLogger.logState("...object count "+validator.getObjectCount()+" (structured elements "+validator.getStructCount()+")...");
						    }
							event=ioxReader.read();
                            if(event instanceof ch.interlis.iox.StartBasketEvent) {
                                topics.add(((ch.interlis.iox.StartBasketEvent)event).getType());
                            }
							if(event instanceof ch.interlis.iox_j.StartBasketEvent) {
							    ((ch.interlis.iox_j.StartBasketEvent)event).setFileMd5(fileMd5);
							}
							// feed object by object to validator
							validator.validate(event);
							statistics.add(event);
						}while(!(event instanceof EndTransferEvent));
					}finally{
						if(ioxReader!=null){
							try {
								ioxReader.close();
							} catch (IoxException e) {
								EhiLogger.logError(e);
							}
							ioxReader=null;
						}
					}
				}
				String autoRefDataFiles[]=refMapping.getRefData(scope,topics.toArray(new String[topics.size()]));
				refDataFiles=mergeValueList(refDataFiles,autoRefDataFiles);
	            for(int idx=0;idx<refDataFiles.length;idx++){
	                String dataFile=refDataFiles[idx];
	                java.io.File localFile;
	                try {
	                    localFile = IliManager.getLocalCopyOfReposFile(repoManager, dataFile);
	                } catch (Ili2cException e) {
	                    EhiLogger.logError("failed to get local copy of data file <"+dataFile+">", e);
	                    return false;
	                }
	                refDataFiles[idx]=localFile.getPath();
	            }
                for(String filename:refDataFiles){
                    // setup data reader (ITF or XTF)
                    IoxReader ioxReader=null;
                    ioxReader = createReader(filename, td,errFactory,settings,pool);
                    if(ioxReader instanceof IoxIliReader){
                        ((IoxIliReader) ioxReader).setModel(td);    
                    }
                    String fileMd5=RepositoryAccess.calcMD5(new File(filename));
                    statistics.setFilename(filename);
                    errFactory.setDataSource(filename);
                    td.setActualRuntimeParameter(ch.interlis.ili2c.metamodel.RuntimeParameters.MINIMAL_RUNTIME_SYSTEM01_CURRENT_TRANSFERFILE, filename);
                    try{
                        IoxEvent event=null;
                        do{
                            long currentTime=System.currentTimeMillis();
                            long slice=(currentTime-startTime)/1000l/60l/10l;
                            if(slice>currentSlice) {
                                currentSlice=slice;
                                EhiLogger.logState("...object count "+validator.getObjectCount()+" (structured elements "+validator.getStructCount()+")...");
                            }
                            event=ioxReader.read();
                            if(event instanceof ch.interlis.iox_j.StartBasketEvent) {
                                ((ch.interlis.iox_j.StartBasketEvent)event).setFileMd5(fileMd5);
                            }
                            // feed object by object to validator
                            validator.addReferenceData(event);
                            statistics.add(event);
                        }while(!(event instanceof EndTransferEvent));
                    }finally{
                        if(ioxReader!=null){
                            try {
                                ioxReader.close();
                            } catch (IoxException e) {
                                EhiLogger.logError(e);
                            }
                            ioxReader=null;
                        }
                    }
                }

				validator.doSecondPass();
                EhiLogger.logState("object count "+validator.getObjectCount()+" (structured elements "+validator.getStructCount()+")");
				stopwatch.Stop();
				statistics.write2logger();
				// check for errors
				if(logStderr.hasSeenErrors()){
					EhiLogger.logState(MSG_VALIDATION_FAILED);
				}else{
					EhiLogger.logState(MSG_VALIDATION_DONE);
					ret=true;
				}
			}catch(Throwable ex){
				if(statistics!=null) {
					statistics.write2logger();
				}
				EhiLogger.logError(ex);
				EhiLogger.logState(MSG_VALIDATION_FAILED);
			}finally{
				if(validator!=null){
					validator.close();
					validator=null;
				}
				EhiLogger.logState("End date " + dateFormat.format(new java.util.Date()) + " validation took " + stopwatch);
			}
		} catch (IoxException e) {
			EhiLogger.logError(e);
		}finally{
			if(xtflog!=null){
				xtflog.close();
				EhiLogger.getInstance().removeListener(xtflog);
				xtflog=null;
			}
            if(csvlog!=null){
                csvlog.close();
                EhiLogger.getInstance().removeListener(csvlog);
                csvlog=null;
            }
			if(logfile!=null){
				logfile.close();
				EhiLogger.getInstance().removeListener(logfile);
				logfile=null;
			}
			if(logStderr!=null){
				EhiLogger.getInstance().addListener(StdListener.getInstance());
				EhiLogger.getInstance().removeListener(logStderr);
				logStderr=null;
			}
		}
		return ret;
	}

    private String[] mergeValueList(String[] refDataFiles, String[] autoRefDataFiles) {
        List<String> refFiles=new ArrayList<String>();
        for(String file:refDataFiles) {
            if(file!=null && file.length()>0 && !refFiles.contains(file)) {
                refFiles.add(file);
            }
        }
        for(String file:autoRefDataFiles) {
            if(file!=null && file.length()>0 && !refFiles.contains(file)) {
                refFiles.add(file);
            }
        }
        return refFiles.toArray(new String[refFiles.size()]);
    }

    private String[] getValueList(String refDataFromSettings) {
        String[] refDataFiles=new String[]{};
        if(refDataFromSettings!=null) {
            List<String> refFiles=new ArrayList<String>();
            String refFilev[] = refDataFromSettings.split(";");
            for(String refFile:refFilev){
                if(refFile.length()>0) {
                    refFiles.add(refFile);
                }
            }
            refDataFiles=refFiles.toArray(new String[refFiles.size()]);
        }
        return refDataFiles;
    }
    private List<GraphicParameterDef> getRuntimeParameters(TransferDescription td) {
        List<GraphicParameterDef> ret=new ArrayList<GraphicParameterDef>();
        for(Iterator modelIt=td.iterator();modelIt.hasNext();) {
            Object modelo=modelIt.next();
            if(modelo instanceof Model) {
                for(Iterator paramIt=((Model)modelo).iterator();paramIt.hasNext();) {
                    Object paramo=paramIt.next();
                    if(paramo instanceof GraphicParameterDef) {
                        ret.add((GraphicParameterDef)paramo);
                    }
                }
            }
        }
        return ret;
    }

    private Settings readMetaConfig(File metaConfigFile,OutParam<String> baseConfig) throws IOException {
        Settings settings=new Settings();
        ValidationConfig config = IniFileReader.readFile(metaConfigFile);
        baseConfig.value=config.getConfigValue(MetaConfig.CONFIGURATION, MetaConfig.CONFIG_BASE_CONFIG);
        String referenceData=config.getConfigValue(MetaConfig.CONFIGURATION, MetaConfig.CONFIG_REFERENCE_DATA);
        settings.setValue(Validator.SETTING_REF_DATA, referenceData);
        String validConfig=config.getConfigValue(MetaConfig.CONFIGURATION, MetaConfig.CONFIG_VALIDATOR_CONFIG);
        settings.setValue(Validator.SETTING_CONFIGFILE, validConfig);
        java.util.Set<String> params=config.getConfigParams(Validator.METACONFIG_ILIVALIDATOR);
        if(params!=null) {
            for(String arg:params) {
                if(arg.equals("models")){
                    settings.setValue(Validator.SETTING_MODELNAMES, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("config")) {
                    settings.setValue(Validator.SETTING_CONFIGFILE, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("forceTypeValidation")){
                    settings.setValue(Validator.SETTING_FORCE_TYPE_VALIDATION,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("disableAreaValidation")){
                    settings.setValue(Validator.SETTING_DISABLE_AREA_VALIDATION,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("disableConstraintValidation")){
                    settings.setValue(Validator.SETTING_DISABLE_CONSTRAINT_VALIDATION,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("multiplicityOff")){
                    settings.setValue(Validator.SETTING_MULTIPLICITY_VALIDATION,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("allObjectsAccessible")){
                    settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("allowItfAreaHoles")){
                    settings.setValue(Validator.SETTING_ALLOW_ITF_AREA_HOLES,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("simpleBoundary")){
                    settings.setValue(Validator.SETTING_SIMPLE_BOUNDARY,config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("skipPolygonBuilding")){
                    settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("mandatoryBaskets")){
                    settings.setValue(Validator.SETTING_MANDATORY_BASKETS, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("optionalBaskets")){
                    settings.setValue(Validator.SETTING_OPTIONAL_BASKETS, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("bannedBaskets")){
                    settings.setValue(Validator.SETTING_BANNED_BASKETS, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else if(arg.equals("refmapping")){
                    settings.setValue(Validator.SETTING_REF_MAPPING_DATA, config.getConfigValue(Validator.METACONFIG_ILIVALIDATOR, arg));
                }else {
                    EhiLogger.logAdaption("unknown parameter in metaconfig <"+arg+">");
                }
            }
        }
        return settings;
    }

    private TransferDescription td=null;
	public TransferDescription getModel()
	{
	    return td;
	}
    private IoxStatistics statistics=null;
    public IoxStatistics getStatistics()
    {
        return statistics;
    }
	

	public static boolean isWriteable(File f) throws IOException {
        f.createNewFile();
        return f.canWrite();
    }

    private boolean getVersionControlFormConfigFile(File configFile) throws IOException {
        if (configFile != null) {
            ValidationConfig modelConfig=new ValidationConfig();
            modelConfig.mergeConfigFile(configFile);
            String versionControl = modelConfig.getConfigValue(ValidationConfig.PARAMETER, ValidationConfig.VERIFY_MODEL_VERSION);
            return versionControl != null ? versionControl.equals(TRUE) ? true : false : false;
        }
        return false;
    }

    /** template method to allow for any other IoxReader
	 */
	protected IoxReader createReader(String filename, TransferDescription td,LogEventFactory errFactory,Settings settings,PipelinePool pool) throws IoxException {
		IoxReader ioxReader=new ReaderFactory().createReader(new java.io.File(filename), errFactory,settings);
		if(ioxReader instanceof ItfReader2 && skipPolygonBuilding){
			ioxReader=new ItfReader(new java.io.File(filename));
		}
		if(ioxReader instanceof ItfReader2){
		    if(skipGeometryErrors) {
	            ((ItfReader2) ioxReader).setIgnorePolygonBuildingErrors(ItfReader2.POLYGON_BUILDING_ERRORS_OFF);
		    }
            ((ItfReader2) ioxReader).setAllowItfAreaHoles(allowItfAreaHoles);
		    ((ItfReader2) ioxReader).setIoxDataPool(pool);
		}
		return ioxReader;
	}
	
	private static List<String> getModelsFromConfigFile(File configFile) throws IOException {
		List<String> ret=new ArrayList<String>();
		if(configFile!=null){
			ValidationConfig modelConfig=new ValidationConfig();
			modelConfig.mergeConfigFile(configFile);
			String additionalModels=modelConfig.getConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ADDITIONAL_MODELS);
			if(additionalModels!=null){
				String[] additionalModelv = additionalModels.split(";");
				for(String additionalModel:additionalModelv){
					ret.add(additionalModel);
				}
			}
		}
		return ret;
	}
	
	private static List<String> getSpecifiedModelNames(String modelNames) {
		List<String> ret=new ArrayList<String>();
		if(modelNames!=null){
			String[] modelNameList = modelNames.split(";");
			for(String modelName:modelNameList){
				ret.add(modelName);
			}
		}
		return ret;
	}

	/** Compiles the required Interlis models.
	 * @param iliVersion version of required ili model (null, "1.0","2.2", "2.3", "2.4")
	 * @param aclass Interlis qualified class name of a required class.
	 * @param ilifile Interlis model file to read. null if not known.
	 * @param itfDir Folder with Interlis model files or null.
	 * @param appHome Folder of program. Function will check in ilimodels sub-folder for Interlis models.
	 * @param settings Configuration of program.
	 * @return root object of java representation of Interlis model.
	 * @see #SETTING_ILIDIRS
	 */
    public static TransferDescription compileIli(String iliVersion,List<String> modelNames,File ilifile,String itfDir,String appHome,Settings settings) {
        ch.interlis.ilirepository.IliManager modelManager=createRepositoryManager(itfDir,appHome,settings);
        return compileIli(modelManager,iliVersion, modelNames, ilifile, settings);
    }
	public static TransferDescription compileIli(ch.interlis.ilirepository.IliManager modelManager,String iliVersion,List<String> modelNames,File ilifile,Settings settings) {
		
		TransferDescription td=null;
		ch.interlis.ili2c.config.Configuration ili2cConfig=null;
		if(ilifile!=null){
			try {
				//ili2cConfig=ch.interlis.ili2c.ModelScan.getConfig(modeldirv, modelv);
				ArrayList<String> ilifiles=new ArrayList<String>();
				ilifiles.add(ilifile.getPath());
				ili2cConfig=modelManager.getConfigWithFiles(ilifiles);
				ili2cConfig.setGenerateWarnings(false);
			} catch (Ili2cException ex) {
				EhiLogger.logError(ex);
				return null;
			}
		}else{
			ArrayList<String> modelv=new ArrayList<String>();
			if(modelNames!=null){
				modelv.addAll(modelNames);
			}
			try {
			    double version=0.0;
			    if(iliVersion!=null) {
			        version=Double.parseDouble(iliVersion);
			    }
				ili2cConfig=modelManager.getConfig(modelv, version);
				ili2cConfig.setGenerateWarnings(false);
			} catch (Ili2cException ex) {
				EhiLogger.logError(ex);
				return null;
			}
			
		}
		
	
		try {
			ch.interlis.ili2c.Ili2c.logIliFiles(ili2cConfig);
			td=ch.interlis.ili2c.Ili2c.runCompiler(ili2cConfig);
		} catch (Ili2cFailure ex) {
			EhiLogger.logError(ex);
			return null;
		}
		return td;
	}
    public static ch.interlis.ilirepository.IliManager createRepositoryManager(String itfDir,String appHome,Settings settings) {
        ArrayList modeldirv=new ArrayList();
        String ilidirs=settings.getValue(Validator.SETTING_ILIDIRS);
        if(ilidirs==null){
            ilidirs=Validator.SETTING_DEFAULT_ILIDIRS;
        }
    
        EhiLogger.logState("modeldir <"+ilidirs+">");
        String modeldirs[]=ilidirs.split(";");
        HashSet ilifiledirs=new HashSet();
        for(int modeli=0;modeli<modeldirs.length;modeli++){
            String m=modeldirs[modeli];
            if(m.contains(Validator.ITF_DIR)){
                m=m.replace(ITF_DIR, itfDir);
                if(m!=null && m.length()>0){
                    if(!modeldirv.contains(m)){
                        modeldirv.add(m);               
                    }
                }
            }else if(m.contains(Validator.JAR_DIR)){
                if(appHome!=null){
                    m=m.replace(JAR_DIR,appHome);
                    modeldirv.add(m);               
                }else {
                    // ignore it
                }
            }else{
                if(m!=null && m.length()>0){
                    modeldirv.add(m);               
                }
            }
        }       
        
        ch.interlis.ili2c.Main.setHttpProxySystemProperties(settings);
        ch.interlis.ilirepository.IliManager repositoryManager = (ch.interlis.ilirepository.IliManager) settings
                .getTransientObject(UserSettings.CUSTOM_ILI_MANAGER);
        if(repositoryManager==null) {
            repositoryManager=new ch.interlis.ilirepository.IliManager();
            settings.setTransientObject(UserSettings.CUSTOM_ILI_MANAGER,repositoryManager);
        }
        repositoryManager.setRepositories((String[])modeldirv.toArray(new String[]{}));
        return repositoryManager;
    }

	/** Checks, if a given filename is an Interlis 1 transferfilename.
	 * @param filename Name to check.
	 * @return true if it is a ITF filename, otherwise false.
	 */
	public static boolean isItfFilename(String filename)
	{
		String xtfExt=ch.ehi.basics.view.GenericFileFilter.getFileExtension(new java.io.File(filename)).toLowerCase();
		if("itf".equals(xtfExt)){
			return true;
		}
		return false;
	}
	/** Name of ilivalidator section in metaconfig file.
	 */
    public static final String METACONFIG_ILIVALIDATOR="ch.ehi.ilivalidator";
	/** Default path with folders of Interlis model files.
	 * @see #SETTING_ILIDIRS
	 */
	public static final String SETTING_DEFAULT_ILIDIRS = Validator.ITF_DIR+";http://models.interlis.ch/;"+Validator.JAR_DIR+"/ilimodels";
	/** Path with folders of Interlis model files. Multiple entries are separated by semicolon (';'). 
	 * Might contain "http:" URLs which should contain model repositories. 
	 * Might include placeholders ITF_DIR or JAR_DIR. 
	 * @see #ITF_DIR
	 * @see #JAR_DIR
	 */
	public static final String SETTING_ILIDIRS="org.interlis2.validator.ilidirs";
	/** model names. Multiple model names are separated by semicolon (';'). 
	 */
	public static final String SETTING_MODELNAMES="org.interlis2.validator.modelNames";
    /** data files to include in validation. Multiple files are separated by semicolon (';'). 
     */
    public static final String SETTING_DATA="org.interlis2.validator.data";
    /** reference data files to include in validation. Multiple files are separated by semicolon (';'). 
     */
    public static final String SETTING_REF_DATA="org.interlis2.validator.refdata";
    /** mapping to evaluate reference data files to include in validation. Multiple files are separated by semicolon (';'). 
     */
    public static final String SETTING_REF_MAPPING_DATA="org.interlis2.validator.refmapping";
    /** Id of validation scope to evaluate reference data (e.g. municipality id or canton). 
     */
    public static final String SETTING_VALIDATION_SCOPE="org.interlis2.validator.validationScope";
	/** the main folder of program.
	 */
	public static final String SETTING_APPHOME="org.interlis2.validator.appHome";
    /** Name of the ilidata file (XTF format) to write.
     */
    public static final String SETTING_ILIDATA_XML="org.interlis2.validator.ilidata";
    /** Name of file with the list of filenames.
     */
    public static final String SETTING_REMOTEFILE_LIST="org.interlis2.validator.filelist";
    /** Dataset ID of the data.
     */
    public static final String SETTING_DATASETID_TO_UPDATE = "org.interlis2.validator.datasetIDToUpdate";
    /** Repository URL
     */
    public static final String SETTING_REPOSITORY="org.interlis2.validator.baseUrl";
	/** Last used folder in the GUI.
	 */
	public static final String SETTING_DIRUSED="org.interlis2.validator.dirused";
	/** Name of the config file, that controls the model specific validation.
	 */
	public static final String SETTING_CONFIGFILE = "org.interlis2.validator.configfile";
    /** Name of the meta-config file, that controls the model specific setup.
     */
    public static final String SETTING_META_CONFIGFILE = "org.interlis2.validator.metaconfigfile";
	/** Restrict customization of validation related to \"multiplicity\". Possible values "true", "false".
	 */
	public static final String SETTING_FORCE_TYPE_VALIDATION = "org.interlis2.validator.forcetypevalidation";
	/** Global setting for multiplicity validation. Possible values "on", "warning", "off". Default "on".
	 */
	public static final String SETTING_MULTIPLICITY_VALIDATION = null;
    /** Validate if an XML element BOUNDARY is only one boundary (shell or hole) of the polygon. Possible values "true", "false".
     */
    public static final String SETTING_SIMPLE_BOUNDARY = "org.interlis2.validator.simpleBoundary";
	/** Disable AREA validation. Possible values "true", "false".
	 */
	public static final String SETTING_DISABLE_AREA_VALIDATION = "org.interlis2.validator.disableareavalidation";
	/** Disable constraint validation. Possible values "true", "false".
	 */
	public static final String SETTING_DISABLE_CONSTRAINT_VALIDATION = "org.interlis2.validator.disableconstraintvalidation";
	/** Disable AREA validation. Possible values "true", "false".
	 */
	public static final String SETTING_DISABLE_STD_LOGGER = "org.interlis2.validator.disablestdlogger";
	/** Name of the log file that receives the validation results.
	 */
	public static final String SETTING_LOGFILE = "org.interlis2.validator.log";
    /** include timestamps in log file.
     */
    public static final String SETTING_LOGFILE_TIMESTAMP = "org.interlis2.validator.log.timestamp";
	/** Assume that all objects are known to the validator. "true", "false". Default "false".
	 */
	public static final String SETTING_ALL_OBJECTS_ACCESSIBLE = "org.interlis2.validator.allobjectsaccessible";
	/** Assume that all objects are known to the validator. "true", "false". Default "false".
	 */
	public static final String SETTING_ALLOW_ITF_AREA_HOLES = "org.interlis2.validator.allowitfareaholes";
    /** Define runtime parameters. Qualified ili-name equals value; semicolon separated.
     * e.g. RuntimeSystem23.JobId1=test1;RuntimeSystem23.JobId2=test2
     */
    public static final String SETTING_RUNTIME_PARAMETERS = "org.interlis2.validator.runtimeParameters";
    /** List of required topics in transfer. Qualified ili-name of topics; semicolon separated.
     * e.g. ModelA.TopicA;ModelA.TopicB
     */
    public static final String SETTING_MANDATORY_BASKETS = "org.interlis2.validator.mandatoryBaskets";
    /** List of optional topics in transfer. Qualified ili-name of topics; semicolon separated.
     * e.g. ModelA.TopicA;ModelA.TopicB
     */
    public static final String SETTING_OPTIONAL_BASKETS = "org.interlis2.validator.optionalBaskets";
    /** List of not allowed topics in transfer. Qualified ili-name of topics; semicolon separated.
     * e.g. ModelA.TopicA;ModelA.TopicB
     */
    public static final String SETTING_BANNED_BASKETS = "org.interlis2.validator.bannedBaskets";
	/** Name of the data file (XTF format) that receives the validation results.
	 */
	public static final String SETTING_XTFLOG = "org.interlis2.validator.xtflog";
    /** Name of the data file (CSV format) that receives the validation results.
     */
    public static final String SETTING_CSVLOG = "org.interlis2.validator.csvlog";
	/** Name of the folder that contains jar files with plugins.
	 */
	public static final String SETTING_PLUGINFOLDER = "org.interlis2.validator.pluginfolder";
	/** Placeholder, that will be replaced by the folder of the current to be validated transfer file. 
	 * @see #SETTING_ILIDIRS
	 */
	public static final String ITF_DIR="%ITF_DIR";
	/** Placeholder, that will be replaced by the folder of the validator program. 
	 * @see #SETTING_ILIDIRS
	 */
	public static final String JAR_DIR="%JAR_DIR";
	public static final String TRUE=ValidationConfig.TRUE;
	public static final String FALSE=ValidationConfig.FALSE;
	private boolean skipPolygonBuilding;
    private boolean skipGeometryErrors=false;
	private boolean allowItfAreaHoles;
    public static List<Class> getIoxReaders(List<IoxPlugin> plugins)
    {
        List<Class> funcs=new ArrayList<Class>();
        for(IoxPlugin plugin:plugins){
            if(plugin instanceof IoxReader){
                funcs.add(plugin.getClass());
            }
        }
        return funcs;
    }

}
