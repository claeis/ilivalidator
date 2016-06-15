package org.interlis2.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

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
import ch.interlis.iox.EndTransferEvent;
import ch.interlis.iox.IoxEvent;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogging;
import ch.interlis.iox.IoxReader;
import ch.interlis.iox_j.IoxUtility;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.ValidationConfig;

public class Validator {

	/** main workhorse function.
	 */
	public static void runValidation(
			String xtfFilename,
			Settings settings
		) {
		if(xtfFilename==null  || xtfFilename.length()==0){
			EhiLogger.logError("no INTERLIS file given");
			return;
		}
	    String logFilename=settings.getValue(Main.SETTING_LOGFILE);
	    String xtflogFilename=settings.getValue(Main.SETTING_XTFLOG);
		FileLogger logfile=null;
		XtfErrorsLogger xtflog=null;
		StdLogger logStderr=null;
		try{
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
		    String configFilename=settings.getValue(Main.SETTING_CONFIGFILE);

		    
			EhiLogger.logState(Main.APP_NAME+"-"+Main.getVersion());
			EhiLogger.logState("ili2c-"+ch.interlis.ili2c.Ili2c.getVersion());
			EhiLogger.logState("xtfFile <"+xtfFilename+">");
			if(configFilename!=null){
				EhiLogger.logState("configFile <"+configFilename+">");
			}
		
			TransferDescription sourceTd=null;
			TransferDescription td=null;
			ArrayList<Model> models=null;
			
			File xtfFile=new File(xtfFilename);
			String model=IoxUtility.getModelFromXtf(xtfFilename);
			if(model==null){
				return;
			}
			td=compileIli(model, null,xtfFile.getAbsoluteFile().getParentFile().getAbsolutePath(),Main.getAppHome(), settings);
			if(td==null){
				return;
			}
			
			// process data file
			EhiLogger.logState("validate data...");
			IoxReader ioxReader=null;
			ch.interlis.iox_j.validator.Validator validator=null;
			try{
				// TODO setup log output
				ValidationConfig modelConfig=new ValidationConfig();
				modelConfig.mergeIliMetaAttrs(td);
				if(configFilename!=null){
					modelConfig.mergeConfigFile(new File(configFilename));
				}
				IoxLogging errHandler=new ch.interlis.iox_j.logging.Log2EhiLogger();
				LogEventFactory errFactory=new LogEventFactory();
				errFactory.setDataSource(xtfFilename);
				validator=new ch.interlis.iox_j.validator.Validator(td,modelConfig, errHandler, errFactory, settings);
				// setup iox reader
				if(isItfFilename(xtfFilename)){
					ioxReader=new ItfReader2(new java.io.File(xtfFilename),false);
					((ItfReader2)ioxReader).setModel(td);		
				}else{
					ioxReader=new XtfReader(new java.io.File(xtfFilename));
				}
				// loop
				IoxEvent event=null;
				do{
					event=ioxReader.read();
					validator.validate(event);
				}while(!(event instanceof EndTransferEvent));
				if(logStderr.hasSeenErrors()){
					EhiLogger.logState("...validation failed");
				}else{
					EhiLogger.logState("...validation done");
				}
			}catch(Throwable ex){
				EhiLogger.logError(ex);
				EhiLogger.logState("...validation failed");
			}finally{
				if(ioxReader!=null){
					try {
						ioxReader.close();
					} catch (IoxException e) {
						EhiLogger.logError(e);
					}
					ioxReader=null;
				}
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
	}
	
	public static TransferDescription compileIli(String aclass,File ilifile,String itfDir,String appHome,Settings settings) {
		String model=null;
		if(aclass!=null){
			String names[]=aclass.split("\\.");
			model=names[0];
		}
		ArrayList modeldirv=new ArrayList();
		String ilidirs=settings.getValue(Main.SETTING_ILIDIRS);
	
		EhiLogger.logState("ilidirs <"+ilidirs+">");
		String modeldirs[]=ilidirs.split(";");
		HashSet ilifiledirs=new HashSet();
		for(int modeli=0;modeli<modeldirs.length;modeli++){
			String m=modeldirs[modeli];
			if(m.equals(Main.ITF_DIR)){
				m=itfDir;
				if(m!=null && m.length()>0){
					if(!modeldirv.contains(m)){
						modeldirv.add(m);				
					}
				}
			}else if(m.equals(Main.JAR_DIR)){
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
			if(model!=null){
				modelv.add(model);
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


	public static boolean isItfFilename(String filename)
	{
		String xtfExt=ch.ehi.basics.view.GenericFileFilter.getFileExtension(new java.io.File(filename)).toLowerCase();
		if("itf".equals(xtfExt)){
			return true;
		}
		return false;
	}
}
