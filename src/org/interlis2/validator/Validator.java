package org.interlis2.validator;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.logging.StdListener;
import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.Ili2c;
import ch.interlis.ili2c.Ili2cException;
import ch.interlis.ili2c.Ili2cFailure;
import ch.interlis.ili2c.metamodel.Model;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom_j.itf.ItfReader2;
import ch.interlis.iom_j.xtf.XtfReader;
import ch.interlis.iox.EndBasketEvent;
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox.StartBasketEvent;
import ch.interlis.iox.StartTransferEvent;
import ch.interlis.iox_j.IoxUtility;
import ch.interlis.iox_j.PipelinePool;
import ch.interlis.iox_j.logging.FileLogger;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.logging.StdLogger;
import ch.interlis.iox_j.logging.XtfErrorsLogger;
import ch.interlis.iox_j.plugins.PluginLoader;
import ch.interlis.iox_j.validator.ValidationConfig;

/** High-level API of the INTERLIS validator.
 * For a usage example of this class, see the implementation of class {@link Main}.
 * For a usage example of the low-level API, see the implementation of {@link #runValidation(String, Settings)}. 
 */
public class Validator {

	/** main workhorse function.
	 * @param xtfFilename File to validate.
	 * @param settings Configuration of program. 
	 * This is not the TOML file, that controls the model specific validation.
	 * @return true if validation succeeds, false if it fails (or any program error). 
	 * @see #SETTING_ILIDIRS
	 * @see #SETTING_CONFIGFILE
	 * @see #SETTING_LOGFILE
	 * @see #SETTING_XTFLOG
	 * @see #SETTING_PLUGINDIR
	 */
	public static boolean runValidation(
			String xtfFilename,
			Settings settings
		) {
		if(xtfFilename==null  || xtfFilename.length()==0){
			EhiLogger.logError("no INTERLIS file given");
			return false;
		}
		return runValidation(new String[]{xtfFilename},settings);
	}
	
	public static boolean runValidation(
			String xtfFilename[],
			Settings settings
		) {
		if(xtfFilename==null  || xtfFilename.length==0){
			EhiLogger.logError("no INTERLIS file given");
			return false;
		}
		if(settings==null){
			settings=new Settings();
		}
	    String logFilename=settings.getValue(Validator.SETTING_LOGFILE);
	    String xtflogFilename=settings.getValue(Validator.SETTING_XTFLOG);
		FileLogger logfile=null;
		XtfErrorsLogger xtflog=null;
		StdLogger logStderr=null;
		boolean ret=false;
		try{
			// setup logging of validation results
			if(logFilename!=null){
				logfile=new FileLogger(new java.io.File(logFilename));
				EhiLogger.getInstance().addListener(logfile);
			}
			if(xtflogFilename!=null){
				xtflog=new XtfErrorsLogger(new java.io.File(xtflogFilename), Main.APP_NAME+"-"+Main.getVersion());
				EhiLogger.getInstance().addListener(xtflog);
			}
			logStderr=new StdLogger(logFilename);
			EhiLogger.getInstance().addListener(logStderr);
			EhiLogger.getInstance().removeListener(StdListener.getInstance());
		    String configFilename=settings.getValue(Validator.SETTING_CONFIGFILE);
		    String pluginFolder=settings.getValue(Validator.SETTING_PLUGINFOLDER);
		    		    
		    // give user important info (such as input files or program version)
			EhiLogger.logState(Main.APP_NAME+"-"+Main.getVersion());
			EhiLogger.logState("ili2c-"+ch.interlis.ili2c.Ili2c.getVersion());
			EhiLogger.logState("iox-ili-"+ch.interlis.iox_j.IoxUtility.getVersion());
			EhiLogger.logState("maxMemory "+java.lang.Runtime.getRuntime().maxMemory()/1024L+" KB");
			for(String xtfFile:xtfFilename){
				EhiLogger.logState("xtfFile <"+xtfFile+">");
			}
			if(configFilename!=null){
				EhiLogger.logState("configFile <"+configFilename+">");
			}
			if(pluginFolder!=null){
				EhiLogger.logState("pluginFolder <"+pluginFolder+">");
			}
		
			TransferDescription sourceTd=null;
			TransferDescription td=null;
			ArrayList<Model> models=null;
			
			// find out, which ili model is required
			List<String> modelnames=new ArrayList<String>();
			for(String xtfFile:xtfFilename){
				String modelnameFromXtf=IoxUtility.getModelFromXtf(xtfFile);
				if(modelnameFromXtf==null){
					return false;
				}
				modelnames.add(modelnameFromXtf);
				if(isItfFilename(xtfFile)){
					settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_OIDPERTABLE_DO);
				}
			}
			if(configFilename!=null){
				try {
					List<String> modelNamesFromConfig=getModelsFromConfigFile(configFilename);
					modelnames.addAll(modelNamesFromConfig);
				} catch (FileNotFoundException e) {
					EhiLogger.logError("config file <"+configFilename+"> not found", e);
					return false;
				}
			}
			Map<String,Class> userFunctions=null;
			if(pluginFolder!=null){
				PluginLoader loader=new PluginLoader();
				loader.loadPlugins(new File(pluginFolder));
				userFunctions=PluginLoader.getInterlisFunctions(loader.getAllPlugins());
				settings.setTransientObject(ch.interlis.iox_j.validator.Validator.CONFIG_CUSTOM_FUNCTIONS, userFunctions);
			}
			
			// read ili models
			td=compileIli(modelnames, null,new File(xtfFilename[0]).getAbsoluteFile().getParentFile().getAbsolutePath(),Main.getAppHome(), settings);
			if(td==null){
				return false;
			}
			
			// process data files
			EhiLogger.logState("validate data...");
			ch.interlis.iox_j.validator.Validator validator=null;
			try{
				// setup log output
				ValidationConfig modelConfig=new ValidationConfig();
				modelConfig.mergeIliMetaAttrs(td);
				if(configFilename!=null){
					modelConfig.mergeConfigFile(new File(configFilename));
				}
				modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.ALLOW_ONLY_MULTIPLICITY_REDUCTION, TRUE.equals(settings.getValue(SETTING_FORCE_TYPE_VALIDATION))?ValidationConfig.ON:null);
				modelConfig.setConfigValue(ValidationConfig.PARAMETER, ValidationConfig.AREA_OVERLAP_VALIDATION, TRUE.equals(settings.getValue(SETTING_DISABLE_AREA_VALIDATION))?ValidationConfig.OFF:null);
				IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
				LogEventFactory errFactory=new LogEventFactory();
				errFactory.setLogger(errHandler);
				PipelinePool pool=new PipelinePool();
				validator=new ch.interlis.iox_j.validator.Validator(td,modelConfig, errHandler, errFactory, pool,settings);
				validator.setAutoSecondPass(false);
				// loop over data objects
				for(String xtfFile:xtfFilename){
					// setup data reader (ITF or XTF)
					IoxReader ioxReader=null;
					if(isItfFilename(xtfFile)){
						ioxReader=new ItfReader2(new java.io.File(xtfFile),false);
						((ItfReader2)ioxReader).setIoxDataPool(pool);
						((ItfReader2)ioxReader).setModel(td);		
					}else{
						ioxReader=new XtfReader(new java.io.File(xtfFile));
					}
					errFactory.setDataSource(xtfFile);
					
					try{
						IoxEvent event=null;
						do{
							event=ioxReader.read();
							// feed object by object to validator
							validator.validate(event);
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
				// check for errors
				if(logStderr.hasSeenErrors()){
					EhiLogger.logState("...validation failed");
				}else{
					EhiLogger.logState("...validation done");
					ret=true;
				}
			}catch(Throwable ex){
				EhiLogger.logError(ex);
				EhiLogger.logState("...validation failed");
			}finally{
				if(validator!=null){
					validator.close();
					validator=null;
				}
			}
		}finally{
			if(xtflog!=null){
				xtflog.close();
				EhiLogger.getInstance().removeListener(xtflog);
				xtflog=null;
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
	
	private static List<String> getModelsFromConfigFile(String configFilename) throws FileNotFoundException {
		List<String> ret=new ArrayList<String>();
		if(configFilename!=null){
			ValidationConfig modelConfig=new ValidationConfig();
			modelConfig.mergeConfigFile(new File(configFilename));
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

	/** Compiles the required Interlis models.
	 * @param aclass Interlis qualified class name of a required class.
	 * @param ilifile Interlis model file to read. null if not known.
	 * @param itfDir Folder with Interlis model files or null.
	 * @param appHome Folder of program. Function will check in ilimodels sub-folder for Interlis models.
	 * @param settings Configuration of program.
	 * @return root object of java representation of Interlis model.
	 * @see #SETTING_ILIDIRS
	 */
	public static TransferDescription compileIli(List<String> modelNames,File ilifile,String itfDir,String appHome,Settings settings) {
		ArrayList modeldirv=new ArrayList();
		String ilidirs=settings.getValue(Validator.SETTING_ILIDIRS);
		if(ilidirs==null){
			ilidirs=Validator.SETTING_DEFAULT_ILIDIRS;
		}
	
		EhiLogger.logState("ilidirs <"+ilidirs+">");
		String modeldirs[]=ilidirs.split(";");
		HashSet ilifiledirs=new HashSet();
		for(int modeli=0;modeli<modeldirs.length;modeli++){
			String m=modeldirs[modeli];
			if(m.equals(Validator.ITF_DIR)){
				m=itfDir;
				if(m!=null && m.length()>0){
					if(!modeldirv.contains(m)){
						modeldirv.add(m);				
					}
				}
			}else if(m.equals(Validator.JAR_DIR)){
				m=appHome;
				if(m!=null){
					m=new java.io.File(m,"ilimodels").getAbsolutePath();
				}
				if(m!=null && m.length()>0){
					modeldirv.add(m);				
				}
			}else{
				if(m!=null && m.length()>0){
					modeldirv.add(m);				
				}
			}
		}		
		
		ch.interlis.ili2c.Main.setHttpProxySystemProperties(settings);
		TransferDescription td=null;
		ch.interlis.ili2c.config.Configuration ili2cConfig=null;
		if(ilifile!=null){
			//ili2cConfig=new ch.interlis.ili2c.config.Configuration();
			//ili2cConfig.addFileEntry(new ch.interlis.ili2c.config.FileEntry(ilifile.getPath(),ch.interlis.ili2c.config.FileEntryKind.ILIMODELFILE));				
			try {
				//ili2cConfig=ch.interlis.ili2c.ModelScan.getConfig(modeldirv, modelv);
				ch.interlis.ilirepository.IliManager modelManager=new ch.interlis.ilirepository.IliManager();
				modelManager.setRepositories((String[])modeldirv.toArray(new String[]{}));
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
				//ili2cConfig=ch.interlis.ili2c.ModelScan.getConfig(modeldirv, modelv);
				ch.interlis.ilirepository.IliManager modelManager=new ch.interlis.ilirepository.IliManager();
				modelManager.setRepositories((String[])modeldirv.toArray(new String[]{}));
				ili2cConfig=modelManager.getConfig(modelv, 0.0);
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
	/** Default path with folders of Interlis model files.
	 * @see #SETTING_ILIDIRS
	 */
	public static final String SETTING_DEFAULT_ILIDIRS = Validator.ITF_DIR+";http://models.interlis.ch/;"+Validator.JAR_DIR;
	/** Path with folders of Interlis model files. Multiple entries are separated by semicolon (';'). 
	 * Might contain "http:" URLs which should contain model repositories. 
	 * Might include placeholders ITF_DIR or JAR_DIR. 
	 * @see #ITF_DIR
	 * @see #JAR_DIR
	 */
	public static final String SETTING_ILIDIRS="org.interlis2.validator.ilidirs";
	/** Last used folder in the GUI.
	 */
	public static final String SETTING_DIRUSED="org.interlis2.validator.dirused";
	/** Name of the config file, that controls the model specific validation.
	 */
	public static final String SETTING_CONFIGFILE = "org.interlis2.validator.configfile";
	/** Restrict customization of validation related to \"multiplicity\". Possible values "true", "false".
	 */
	public static final String SETTING_FORCE_TYPE_VALIDATION = "org.interlis2.validator.forcetypevalidation";
	/** Disable AREA validation. Possible values "true", "false".
	 */
	public static final String SETTING_DISABLE_AREA_VALIDATION = "org.interlis2.validator.disableareavalidation";
	/** Name of the log file that receives the validation results.
	 */
	public static final String SETTING_LOGFILE = "org.interlis2.validator.log";
	/** Name of the data file (XTF format) that receives the validation results.
	 */
	public static final String SETTING_XTFLOG = "org.interlis2.validator.xtflog";
	/** Name of the folder that contains jar files with plugins.
	 */
	public static final String SETTING_PLUGINFOLDER = "org.interlis2.validator.pluginfolder";
	/** Placeholder, that will be replaced by the folder of the current to be validated transfer file. 
	 * @see #SETTING_ILIDIRS
	 */
	public static final String ITF_DIR="%ITF_DIR";
	/** Placeholder, that will be replaced by the "ilimodels" subfolder of the validator program. 
	 * @see #SETTING_ILIDIRS
	 */
	public static final String JAR_DIR="%JAR_DIR";
	public static final String TRUE="true";
	public static final String FALSE="false";
}
