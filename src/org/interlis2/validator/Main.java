package org.interlis2.validator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.settings.Settings;
import ch.interlis.iox_j.validator.ValidationConfig;

/** Main program and commandline interface of ilivalidator.
 */
public class Main {
	
	/** name of application as shown to user.
	 */
	public static final String APP_NAME="ilivalidator";
	/** name of jar file.
	 */
	public static final String APP_JAR="ilivalidator.jar";
	/** version of application.
	 */
	private static String version=null;
	private static int FC_VALIDATE=0;
    private static int FC_CHECK_REPO_DATA=1;
    private static int FC_CREATE_ILIDATA_XML=2;
    private static int FC_UPDATE_ILIDATA_XML=3;
	
	/** main program entry.
	 * @param args command line arguments.
	 */
	static public void main(String args[]){
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_ILIDIRS, Validator.SETTING_DEFAULT_ILIDIRS);
		String appHome=getAppHome();
		if(appHome!=null){
		    settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File(appHome,"plugins").getAbsolutePath());
		    settings.setValue(Validator.SETTING_APPHOME, appHome);
		}else{
		    settings.setValue(Validator.SETTING_PLUGINFOLDER, new java.io.File("plugins").getAbsolutePath());
		}
		Class mainFrame=null;
		try {
            //mainFrame=Class.forName("org.interlis2.validator.gui.MainFrame");
            mainFrame=Class.forName(preventOptimziation("org.interlis2.validator.gui.MainFrame")); // avoid, that graalvm native-image detects a reference to MainFrame
		}catch(ClassNotFoundException ex){
		    // ignore; report later
		}
		Method mainFrameMain=null;
		if(mainFrame!=null) {
		    try {
	            mainFrameMain = mainFrame.getMethod ("main",(new String[0]).getClass(),Settings.class);
		    }catch(NoSuchMethodException ex) {
	            // ignore; report later
		    }
		}
		// arguments on export
		String[] xtfFile=null;
		java.util.List<String> xtfRefFile=new java.util.ArrayList<String>();
		String httpProxyHost = null;
		String httpProxyPort = null;
		if(args.length==0){
			xtfFile=new String[0];
			readSettings(settings);
            // MainFrame.main(xtfFile,settings);
			runGui(mainFrameMain, xtfFile, settings);     
			return;
		}
		int argi=0;
		int function=FC_VALIDATE;
		boolean doGui=false;
		for(;argi<args.length;argi++){
			String arg=args[argi];
			if(arg.equals("--trace")){
				EhiLogger.getInstance().setTraceFilter(false);
			}else if(arg.equals("--gui")){
				readSettings(settings);
				doGui=true;
			}else if(arg.equals("--models")){
				argi++;
				settings.setValue(Validator.SETTING_MODELNAMES, args[argi]);
			}else if(arg.equals("--modeldir")){
				argi++;
				settings.setValue(Validator.SETTING_ILIDIRS, args[argi]);
            }else if(arg.equals("--refdata")){
                argi++;
                String refdatav[]= args[argi].split(";");
                for(String refdata:refdatav) {
                    xtfRefFile.add(refdata);
                }
			}else if(arg.equals("--config")) {
			    argi++;
			    settings.setValue(Validator.SETTING_CONFIGFILE, args[argi]);
            }else if(arg.equals("--metaConfig")) {
                argi++;
                settings.setValue(Validator.SETTING_META_CONFIGFILE, args[argi]);
            }else if(arg.equals("--runtimeParams")) {
                argi++;
                settings.setValue(Validator.SETTING_RUNTIME_PARAMETERS, args[argi]);
			}else if(arg.equals("--forceTypeValidation")){
				settings.setValue(Validator.SETTING_FORCE_TYPE_VALIDATION,Validator.TRUE);
			}else if(arg.equals("--disableAreaValidation")){
				settings.setValue(Validator.SETTING_DISABLE_AREA_VALIDATION,Validator.TRUE);
			}else if(arg.equals("--disableConstraintValidation")){
				settings.setValue(Validator.SETTING_DISABLE_CONSTRAINT_VALIDATION,Validator.TRUE);
			}else if(arg.equals("--multiplicityOff")){
				settings.setValue(Validator.SETTING_MULTIPLICITY_VALIDATION,ch.interlis.iox_j.validator.ValidationConfig.OFF);
			}else if(arg.equals("--allObjectsAccessible")){
				settings.setValue(Validator.SETTING_ALL_OBJECTS_ACCESSIBLE,Validator.TRUE);
			}else if(arg.equals("--allowItfAreaHoles")){
				settings.setValue(Validator.SETTING_ALLOW_ITF_AREA_HOLES,Validator.TRUE);
            }else if(arg.equals("--simpleBoundary")){
                settings.setValue(Validator.SETTING_SIMPLE_BOUNDARY,Validator.TRUE);
			}else if(arg.equals("--skipPolygonBuilding")){
				settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES, ch.interlis.iox_j.validator.Validator.CONFIG_DO_ITF_LINETABLES_DO);
            }else if(arg.equals("--singlePass")){
                settings.setValue(ch.interlis.iox_j.validator.Validator.CONFIG_DO_SINGLE_PASS, ch.interlis.iox_j.validator.Validator.CONFIG_DO_SINGLE_PASS_DO);
			}else if (arg.equals("--createIliData")){
			    function=FC_CREATE_ILIDATA_XML;
            }else if (arg.equals("--updateIliData")) {
                function=FC_UPDATE_ILIDATA_XML;
            }else if (arg.equals("--check-repo-data")) {
                function=FC_CHECK_REPO_DATA;
                argi++;
                settings.setValue(Validator.SETTING_REPOSITORY, args[argi]);
            }else if (arg.equals("--ilidata")) {
                argi++;
			    settings.setValue(Validator.SETTING_ILIDATA_XML, args[argi]);
			}else if (arg.equals("--srcfiles")) {
			    argi++;
			    settings.setValue(Validator.SETTING_REMOTEFILE_LIST, args[argi]);
			}else if (arg.equals("--datasetId")) {
			    argi++;
			    settings.setValue(Validator.SETTING_DATASETID_TO_UPDATE, args[argi]);
            }else if (arg.equals("--repos")) {
                argi++;
                settings.setValue(Validator.SETTING_REPOSITORY, args[argi]);
			}else if(arg.equals("--log")) {
			    argi++;
			    settings.setValue(Validator.SETTING_LOGFILE, args[argi]);
            }else if(arg.equals("--logtime")){
                settings.setValue(Validator.SETTING_LOGFILE_TIMESTAMP,Validator.TRUE);
			}else if(arg.equals("--xtflog")) {
			    argi++;
			    settings.setValue(Validator.SETTING_XTFLOG, args[argi]);
            }else if(arg.equals("--csvlog")) {
                argi++;
                settings.setValue(Validator.SETTING_CSVLOG, args[argi]);
			}else if(arg.equals("--plugins")) {
			    argi++;
			    settings.setValue(Validator.SETTING_PLUGINFOLDER, args[argi]);
			}else if(arg.equals("--proxy")) {
				    argi++;
				    settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST, args[argi]);
			}else if(arg.equals("--proxyPort")) {
				    argi++;
				    settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT, args[argi]);
			} else if (arg.equals("--verbose")) {
				settings.setTransientValue(ch.interlis.iox_j.validator.Validator.CONFIG_VERBOSE, ValidationConfig.TRUE);
			}else if(arg.equals("--version")){
				printVersion();
				return;
			}else if(arg.equals("--help")){
					printVersion ();
					System.err.println();
					printDescription ();
					System.err.println();
					printUsage ();
					System.err.println();
					System.err.println("OPTIONS");
					System.err.println();
					System.err.println("--gui                 start GUI.");
                    System.err.println("--refdata file        reference data file.");
				    System.err.println("--config file         config file to control validation.");
					System.err.println("--forceTypeValidation  restrict customization of validation related to \"multiplicity\".");
					System.err.println("--disableAreaValidation  disable AREA validation.");
					System.err.println("--disableConstraintValidation  disable constraint validation.");
					System.err.println("--allObjectsAccessible  assume that all objects are known to the validator.");
					System.err.println("--multiplicityOff     disable all multiplicity validation.");
					System.err.println("--createIliData(formedFilename, sourceFolder)   create a new xml file by reading/analyzing existing xtf/itf files.");
					System.err.println("--srcfiles (formedFilename, filename, remoteLocation)   reads a list of relative file names and reads all these files from the remote location and creates new xml(ilidata formatted)");
					System.err.println("--updateIliData       Ili data to be updated");
					System.err.println("--dataset             The requested Dataset ID to be updated");
					System.err.println("--skipPolygonBuilding skip polygon building (only ITF).");
					System.err.println("--allowItfAreaHoles   allow empty holes (unassigned inner boundaries) in ITF AREA attributes.");
					System.err.println("--simpleBoundary      allow only simple lines as polygon boundary lines.");
					System.err.println("--runtimeParams param=value define values of runtime parameters.");
                    System.err.println("--singlePass          skip any validations that require a second pass.");
				    System.err.println("--log file            text file, that receives validation results.");
                    System.err.println("--logtime             include timestamps in logfile.");
				    System.err.println("--xtflog file         INTERLIS transfer file, that receives validation results.");
                    System.err.println("--csvlog file         CSV file, that receives validation results.");
				    System.err.println("--models model		  user sets certain models, separated by a semicolon.");
					System.err.println("--modeldir "+Validator.SETTING_DEFAULT_ILIDIRS+" list of directories/repositories with ili-files.");
				    System.err.println("--plugins folder      directory with jar files that contain user defined functions.");
				    System.err.println("--proxy host          proxy server to access model repositories.");
				    System.err.println("--proxyPort port      proxy port to access model repositories.");
					System.err.println("--verbose             print additional information in validation results.");
					System.err.println("--trace               enable trace messages.");
					System.err.println("--help                Display this help text.");
					System.err.println("--version             Display the version of "+APP_NAME+".");
					System.err.println();
					return;
				
			}else if(arg.startsWith("-")){
				EhiLogger.logAdaption(arg+": unknown option; ignored");
			}else{
				break;
			}
		}
		settings.setValue(Validator.SETTING_REF_DATA,makeFileList(xtfRefFile.toArray(new String[xtfRefFile.size()])));
		int dataFileCount=args.length-argi;
		if(doGui){
			if(dataFileCount>0) {
				xtfFile = getDataFiles(args, argi, dataFileCount);
			}
			//MainFrame.main(xtfFile,settings);
            runGui(mainFrameMain, xtfFile, settings);                     
            return;
		}else{
            xtfFile = getDataFiles(args, argi, dataFileCount);
            boolean ok=false;
		    if(function==FC_VALIDATE) {
                if (dataFileCount == 0) {
                    EhiLogger.logError(APP_NAME+": wrong number of arguments");
                    System.exit(2);                     
                }
                ok = Validator.runValidation(xtfFile,settings);
		    }else if(function==FC_CREATE_ILIDATA_XML) {
                if(dataFileCount!=0) {
                    EhiLogger.logError(APP_NAME+": wrong number of arguments");
                    System.exit(2);                 
                }
                ok = CreateIliDataTool.start(settings);
            }else if (function==FC_UPDATE_ILIDATA_XML) {
                if (dataFileCount != 1) {
                    EhiLogger.logError(APP_NAME+": wrong number of arguments");
                    System.exit(2);                     
                }
                ok = UpdateIliDataTool.update(new File(xtfFile[0]),settings);
            }else if(function==FC_CHECK_REPO_DATA) {
                if (dataFileCount != 0) {
                    EhiLogger.logError(APP_NAME+": wrong number of arguments");
                    System.exit(2);                     
                }
                ok = CheckRepoDataTool.launch(settings);
            }else {
                throw new IllegalStateException("function=="+function);
            }
            System.exit(ok ? 0 : 1);
		}
		
	}
    private static String preventOptimziation(String val) {
        StringBuffer buf=new StringBuffer(val.length());
        buf.append(val);
        return buf.toString();
    }
    private static void runGui(Method mainFrameMain, String[] xtfFile, Settings settings) {
        if(mainFrameMain!=null) {
            try {
                mainFrameMain.invoke(null, xtfFile,settings);
                return;                 
            } catch (IllegalArgumentException ex) {
                EhiLogger.logError("failed to open GUI",ex);
            } catch (IllegalAccessException ex) {
                EhiLogger.logError("failed to open GUI",ex);
            } catch (InvocationTargetException ex) {
                EhiLogger.logError("failed to open GUI",ex);
            }
        }else {
            EhiLogger.logError(APP_NAME+": no GUI available");
        }
        System.exit(2);
    }
    private static String[] getDataFiles(String[] args, int argi, int dataFileCount) {
		String[] xtfFile;
		xtfFile=new String[dataFileCount];
		int fileCount=0;
		while(argi<args.length){
			xtfFile[fileCount]=args[argi];
			fileCount+=1;
			argi++;
		}
		return xtfFile;
	}
	/** Name of file with program settings. Only used by GUI, not used by commandline version.
	 */
	private final static String SETTINGS_FILE = System.getProperty("user.home") + "/.ilivalidator";
	/** Reads program settings.
	 * @param settings Program configuration as read from file.
	 */
	public static void readSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			if(file.exists()){
				settings.load(file);
			}
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to load settings from file "+SETTINGS_FILE,ex);
		}
	}
	/** Writes program settings.
	 * @param settings Program configuration to write.
	 */
	public static void writeSettings(Settings settings)
	{
		java.io.File file=new java.io.File(SETTINGS_FILE);
		try{
			settings.store(file,APP_NAME+" settings");
		}catch(java.io.IOException ex){
			EhiLogger.logError("failed to settings settings to file "+SETTINGS_FILE,ex);
		}
	}
	
	/** Prints program version.
	 */
	protected static void printVersion ()
	{
	  System.err.println(APP_NAME+", Version "+getVersion());
	  System.err.println("  Developed by Eisenhut Informatik AG, CH-3400 Burgdorf");
	}

	/** Prints program description.
	 */
	protected static void printDescription ()
	{
	  System.err.println("DESCRIPTION");
	  System.err.println("  Validates an INTERLIS transfer file.");
	}

	/** Prints program usage.
	 */
	protected static void printUsage()
	{
	  System.err.println ("USAGE");
	  System.err.println("  java -jar "+APP_JAR+" [Options] in.xtf");
	}
	/** Gets version of program.
	 * @return version e.g. "1.0.0"
	 */
	public static String getVersion() {
		  if(version==null){
		java.util.ResourceBundle resVersion = java.util.ResourceBundle.getBundle(ch.ehi.basics.i18n.ResourceBundle.class2qpackageName(Main.class)+".Version");
			StringBuffer ret=new StringBuffer(20);
		ret.append(resVersion.getString("version"));
			ret.append('-');
		ret.append(resVersion.getString("versionCommit"));
			version=ret.toString();
		  }
		  return version;
	}
	
	/** Gets main folder of program.
	 * 
	 * @return folder Main folder of program.
	 */
	static public String getAppHome()
	{
	  String[] classpaths = System.getProperty("java.class.path").split(System.getProperty("path.separator"));
	  for(String classpath:classpaths) {
		  if(classpath.toLowerCase().endsWith(".jar")) {
			  File file = new File(classpath);
			  String jarName=file.getName();
			  if(jarName.toLowerCase().startsWith(APP_NAME)) {
				  file=new File(file.getAbsolutePath());
				  if(file.exists()) {
					  return file.getParent();
				  }
			  }
		  }
	  }
	  return null;
	}
    public static String makeFileList(String[] xtfRefFile) {
        if(xtfRefFile==null) {
            return null;
        }
        StringBuffer files=new StringBuffer();
        String sep="";
        for(String file:xtfRefFile) {
            files.append(sep);
            files.append(file);
            sep=";";
        }
        return files.toString();
    }
	
}
