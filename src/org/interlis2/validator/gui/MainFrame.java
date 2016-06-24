package org.interlis2.validator.gui;

import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;

import ch.ehi.basics.logging.EhiLogger;
import ch.ehi.basics.view.*;
import ch.ehi.basics.settings.Settings;
import ch.ehi.basics.swing.SwingWorker;
import ch.ehi.basics.tools.StringUtility;
import ch.interlis.ili2c.gui.RepositoriesDialog;

import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;

import org.interlis2.validator.Main;
import org.interlis2.validator.Validator;

/** GUI.
 * @author ce
 */
public class MainFrame extends JFrame {
	private java.util.ResourceBundle rsrc=java.util.ResourceBundle.getBundle("org.interlis2.validator.gui.IliValidatorTexts");
	private Settings settings=null;
	private javax.swing.JPanel jContentPane = null;

	private javax.swing.JLabel xtfFileLabel = null;
	private javax.swing.JTextField xtfFileUi = null;
	private javax.swing.JButton doXtfFileSelBtn = null;
	
	private javax.swing.JLabel configFileLabel = null;
	private javax.swing.JTextField configFileUi = null;
	private javax.swing.JButton doConfigFileSelBtn = null;
	
	private javax.swing.JLabel logFileLabel = null;
	private javax.swing.JTextField logFileUi = null;
	private javax.swing.JButton doLogFileSelBtn = null;
	
	private javax.swing.JLabel xtfLogFileLabel = null;
	private javax.swing.JTextField xtfLogFileUi = null;
	private javax.swing.JButton doXtfLogFileSelBtn = null;
	
	private javax.swing.JTextArea logUi = null;
	private javax.swing.JButton clearlogBtn = null;
	public MainFrame() {
		super();
		initialize();
	}
	private void initialize() {
		this.setSize(500, 361);
		this.setContentPane(getJContentPane());
		this.setDefaultCloseOperation(javax.swing.JFrame.EXIT_ON_CLOSE);
		this.setName(Main.APP_NAME);
		this.setTitle(rsrc.getString("MainFrame.Title"));
		
	    //Create the menu bar.
		JMenuBar menuBar = new JMenuBar();
	    setJMenuBar(menuBar);

	    JMenu menu = new JMenu(rsrc.getString("MainFrame.ToolsMenu"));
	    menu.setMnemonic(KeyEvent.VK_T);
	    menuBar.add(menu);
	    JMenuItem menuItem = new JMenuItem(rsrc.getString("MainFrame.ModelRepositoriesMenuItem"));
		menuItem.addActionListener(new ActionListener(){
			public void actionPerformed(java.awt.event.ActionEvent e){
				RepositoriesDialog dlg=new RepositoriesDialog(MainFrame.this);
				dlg.setIlidirs(settings.getValue(Validator.SETTING_ILIDIRS));
				dlg.setHttpProxyHost(settings.getValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST));
				dlg.setHttpProxyPort(settings.getValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT));
				if(dlg.showDialog()==RepositoriesDialog.OK_OPTION){
					String ilidirs=dlg.getIlidirs();
					if(ilidirs==null){
						ilidirs=Validator.SETTING_DEFAULT_ILIDIRS;
					}
					settings.setValue(Validator.SETTING_ILIDIRS,ilidirs);
					settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST,dlg.getHttpProxyHost());
					settings.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT,dlg.getHttpProxyPort());
					saveSettings();
				}
			}

		});
	    menu.add(menuItem);
	    
	}
	private void saveSettings() {
		Settings toSave=new Settings();
		toSave.setValue(Validator.SETTING_ILIDIRS,settings.getValue(Validator.SETTING_ILIDIRS));
		toSave.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST,settings.getValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_HOST));
		toSave.setValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT,settings.getValue(ch.interlis.ili2c.gui.UserSettings.HTTP_PROXY_PORT));
		Main.writeSettings(toSave);
	}
	private javax.swing.JPanel getJContentPane() {
		if (jContentPane == null) {
			jContentPane = new javax.swing.JPanel();
			java.awt.GridBagConstraints xtfFileLabelConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints xtfFileUiConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints doXtfFileSelBtnConstraints = new java.awt.GridBagConstraints();

			java.awt.GridBagConstraints logFileLabelConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints logFileUiConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints doLogFileSelBtnConstraints = new java.awt.GridBagConstraints();
			
			java.awt.GridBagConstraints xtfLogFileLabelConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints xtfLogFileUiConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints doXtfLogFileSelBtnConstraints = new java.awt.GridBagConstraints();
			
			java.awt.GridBagConstraints configFileLabelConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints configFileUiConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints doConfigFileSelBtnConstraints = new java.awt.GridBagConstraints();
			
			java.awt.GridBagConstraints clearlogBtnConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints logPaneConstraints = new java.awt.GridBagConstraints();
			java.awt.GridBagConstraints doValidateConstraints = new java.awt.GridBagConstraints();
			
			xtfFileLabelConstraints.gridx = 0;
			xtfFileLabelConstraints.gridy = 0;
			xtfFileLabelConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			xtfFileUiConstraints.weightx = 1.0;
			xtfFileUiConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			xtfFileUiConstraints.gridx = 1;
			xtfFileUiConstraints.gridy = 0;
			xtfFileUiConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			doXtfFileSelBtnConstraints.gridx = 2;
			doXtfFileSelBtnConstraints.gridy = 0;
			
			
			logFileLabelConstraints.gridx = 0;
			logFileLabelConstraints.gridy = 1;
			logFileLabelConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			logFileUiConstraints.weightx = 1.0;
			logFileUiConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			logFileUiConstraints.gridx = 1;
			logFileUiConstraints.gridy = 1;
			logFileUiConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			doLogFileSelBtnConstraints.gridx = 2;
			doLogFileSelBtnConstraints.gridy = 1;

			xtfLogFileLabelConstraints.gridx = 0;
			xtfLogFileLabelConstraints.gridy = 2;
			xtfLogFileLabelConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			xtfLogFileUiConstraints.weightx = 1.0;
			xtfLogFileUiConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			xtfLogFileUiConstraints.gridx = 1;
			xtfLogFileUiConstraints.gridy = 2;
			xtfLogFileUiConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			doXtfLogFileSelBtnConstraints.gridx = 2;
			doXtfLogFileSelBtnConstraints.gridy = 2;
			
			configFileLabelConstraints.gridx = 0;
			configFileLabelConstraints.gridy = 3;
			configFileLabelConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			configFileUiConstraints.weightx = 1.0;
			configFileUiConstraints.fill = java.awt.GridBagConstraints.HORIZONTAL;
			configFileUiConstraints.gridx = 1;
			configFileUiConstraints.gridy = 3;
			configFileUiConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
			doConfigFileSelBtnConstraints.gridx = 2;
			doConfigFileSelBtnConstraints.gridy = 3;
			
			logPaneConstraints.weightx = 1.0;
			logPaneConstraints.weighty = 1.0;
			logPaneConstraints.fill = java.awt.GridBagConstraints.BOTH;
			logPaneConstraints.gridx = 0;
			logPaneConstraints.gridy = 12;
			logPaneConstraints.gridheight = 2;
			logPaneConstraints.gridwidth = 2;
			doValidateConstraints.gridy = 12;
			doValidateConstraints.gridx = 2;
			clearlogBtnConstraints.gridx = 2;
			clearlogBtnConstraints.gridy = 13;
			clearlogBtnConstraints.anchor = java.awt.GridBagConstraints.NORTH;
			jContentPane.setLayout(new java.awt.GridBagLayout());
			jContentPane.add(getXtfFileLabel(), xtfFileLabelConstraints);
			jContentPane.add(getXtfFileUi(), xtfFileUiConstraints);
			jContentPane.add(getDoXtfFileSelBtn(), doXtfFileSelBtnConstraints);
			
			jContentPane.add(getLogFileLabel(), logFileLabelConstraints);
			jContentPane.add(getLogFileUi(), logFileUiConstraints);
			jContentPane.add(getDoLogFileSelBtn(), doLogFileSelBtnConstraints);

			jContentPane.add(getXtfLogFileLabel(), xtfLogFileLabelConstraints);
			jContentPane.add(getXtfLogFileUi(), xtfLogFileUiConstraints);
			jContentPane.add(getDoXtfLogFileSelBtn(), doXtfLogFileSelBtnConstraints);
			
			jContentPane.add(getConfigFileLabel(), configFileLabelConstraints);
			jContentPane.add(getConfigFileUi(), configFileUiConstraints);
			jContentPane.add(getDoConfigFileSelBtn(), doConfigFileSelBtnConstraints);
			
			jContentPane.add(getJScrollPane(), logPaneConstraints);
			jContentPane.add(getClearlogBtn(), clearlogBtnConstraints);
			jContentPane.add(getDoValidateBtn(), doValidateConstraints);
		}
		return jContentPane;
	}
	private javax.swing.JLabel getXtfFileLabel() {
		if(xtfFileLabel == null) {
			xtfFileLabel = new javax.swing.JLabel();
			xtfFileLabel.setText(rsrc.getString("MainFrame.xtfFileLabel"));
		}
		return xtfFileLabel;
	}
	private javax.swing.JLabel getConfigFileLabel() {
		if(configFileLabel == null) {
			configFileLabel = new javax.swing.JLabel();
			configFileLabel.setText(rsrc.getString("MainFrame.configFileLabel"));
		}
		return configFileLabel;
	}
	private javax.swing.JLabel getLogFileLabel() {
		if(logFileLabel == null) {
			logFileLabel = new javax.swing.JLabel();
			logFileLabel.setText(rsrc.getString("MainFrame.logFileLabel"));
		}
		return logFileLabel;
	}
	private javax.swing.JLabel getXtfLogFileLabel() {
		if(xtfLogFileLabel == null) {
			xtfLogFileLabel = new javax.swing.JLabel();
			xtfLogFileLabel.setText(rsrc.getString("MainFrame.xtfLogFileLabel"));
		}
		return xtfLogFileLabel;
	}
	private javax.swing.JTextField getXtfFileUi() {
		if(xtfFileUi == null) {
			xtfFileUi = new javax.swing.JTextField();
		}
		return xtfFileUi;
	}
	private javax.swing.JTextField getConfigFileUi() {
		if(configFileUi == null) {
			configFileUi = new javax.swing.JTextField();
		}
		return configFileUi;
	}
	private javax.swing.JTextField getLogFileUi() {
		if(logFileUi == null) {
			logFileUi = new javax.swing.JTextField();
		}
		return logFileUi;
	}
	private javax.swing.JTextField getXtfLogFileUi() {
		if(xtfLogFileUi == null) {
			xtfLogFileUi = new javax.swing.JTextField();
		}
		return xtfLogFileUi;
	}
	private javax.swing.JTextArea getLogUi() {
		if(logUi == null) {
			logUi = new javax.swing.JTextArea();
			logUi.setEditable(false);
		}
		return logUi;
	}
	private javax.swing.JButton getClearlogBtn() {
		if(clearlogBtn == null) {
			clearlogBtn = new javax.swing.JButton();
			clearlogBtn.setText(rsrc.getString("MainFrame.clearLogButton"));
			clearlogBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					logClear();
				}
			});
		}
		return clearlogBtn;
	}
	public String getXtfFile(){
		return StringUtility.purge(getXtfFileUi().getText());
	}
	public void setXtfFile(String dbhost){
		getXtfFileUi().setText(dbhost);
	}
	public String getConfigFile(){
		return StringUtility.purge(getConfigFileUi().getText());
	}
	public void setConfigFile(String dbhost){
		getConfigFileUi().setText(dbhost);
	}
	public String getLogFile(){
		return StringUtility.purge(getLogFileUi().getText());
	}
	public void setLogFile(String logfile){
		getLogFileUi().setText(logfile);
	}
	public String getXtfLogFile(){
		return StringUtility.purge(getXtfLogFileUi().getText());
	}
	public void setXtfLogFile(String xtflogfile){
		getXtfLogFileUi().setText(xtflogfile);
	}
	public Settings getSettings()
	{
		String logFile=getLogFile();
		settings.setValue(Validator.SETTING_LOGFILE,logFile);
		String xtflogFile=getXtfLogFile();
		settings.setValue(Validator.SETTING_XTFLOG,xtflogFile);
		String configFile=getConfigFile();
		settings.setValue(Validator.SETTING_CONFIGFILE,configFile);
		return settings;
	}
	public void setSettings(Settings settings)
	{
		this.settings=settings;
	}
	private StringBuffer body=new StringBuffer();
	private javax.swing.JScrollPane jScrollPane = null;
	private javax.swing.JButton doValidateBtn = null;
	public void logAppend(String msg){
		body.append(msg);
		if(!msg.endsWith("\n")){
			body.append("\n");
		}
		getLogUi().setText(body.toString());
	}
	public void logClear(){
		body=new StringBuffer();
		getLogUi().setText(body.toString());
	}
	private javax.swing.JScrollPane getJScrollPane() {
		if(jScrollPane == null) {
			jScrollPane = new javax.swing.JScrollPane();
			jScrollPane.setViewportView(getLogUi());
		}
		return jScrollPane;
	}
	public static void main(String xtfFile,Settings settings) {
		MainFrame frame=new MainFrame();
		frame.setSettings(settings);
		EhiLogger.getInstance().addListener(new LogListener(frame));
		frame.setXtfFile(xtfFile);
		String logFile=settings.getValue(Validator.SETTING_LOGFILE);
		frame.setLogFile(logFile);
		String xtflogFile=settings.getValue(Validator.SETTING_XTFLOG);
		frame.setXtfLogFile(xtflogFile);
		String configFile=settings.getValue(Validator.SETTING_CONFIGFILE);
		frame.setConfigFile(configFile);
		frame.show();
	}
	private javax.swing.JButton getDoValidateBtn() {
		if(doValidateBtn == null) {
			doValidateBtn = new javax.swing.JButton();
			doValidateBtn.setText(rsrc.getString("MainFrame.doValidateButton"));
			doValidateBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {
					SwingWorker worker = new SwingWorker() {
						public Object construct() {
							try {
								Validator.runValidation(
									getXtfFile(),
									getSettings()
								);
							}
							catch (Exception ex) {
								EhiLogger.logError(rsrc.getString("MainFrame.generalError"),ex);
							}
							return null;
						}
					};
					worker.start();
				}
			});
		}
		return doValidateBtn;
	}
	private javax.swing.JButton getDoXtfFileSelBtn() {
		if(doXtfFileSelBtn == null) {
			doXtfFileSelBtn = new javax.swing.JButton();
			doXtfFileSelBtn.setText("...");
			doXtfFileSelBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String file=getXtfFile();
					FileChooser fileDialog =  new FileChooser(file);
					fileDialog.setDialogTitle(rsrc.getString("MainFrame.xtfFileChooserTitle"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.xtfFileFilter"),"xtf"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.itfFileFilter"),"itf"));
					fileDialog.addChoosableFileFilter(GenericFileFilter.createXmlFilter());

					if (fileDialog.showOpenDialog(MainFrame.this) == FileChooser.APPROVE_OPTION) {
						file=fileDialog.getSelectedFile().getAbsolutePath();
						setXtfFile(file);
					}					
				}
			});
		}
		return doXtfFileSelBtn;
	}
	private javax.swing.JButton getDoLogFileSelBtn() {
		if(doLogFileSelBtn == null) {
			doLogFileSelBtn = new javax.swing.JButton();
			doLogFileSelBtn.setText("...");
			doLogFileSelBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String file=getLogFile();
					FileChooser fileDialog =  new FileChooser(file);
					fileDialog.setDialogTitle(rsrc.getString("MainFrame.logFileChooserTitle"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.logFileFilter"),"log"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.txtFileFilter"),"txt"));

					if (fileDialog.showSaveDialog(MainFrame.this) == FileChooser.APPROVE_OPTION) {
						file=fileDialog.getSelectedFile().getAbsolutePath();
						setLogFile(file);
					}					
				}
			});
		}
		return doLogFileSelBtn;
	}
	private javax.swing.JButton getDoXtfLogFileSelBtn() {
		if(doXtfLogFileSelBtn == null) {
			doXtfLogFileSelBtn = new javax.swing.JButton();
			doXtfLogFileSelBtn.setText("...");
			doXtfLogFileSelBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String file=getLogFile();
					FileChooser fileDialog =  new FileChooser(file);
					fileDialog.setDialogTitle(rsrc.getString("MainFrame.xtflogFileChooserTitle"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.xtfFileFilter"),"xtf"));

					if (fileDialog.showSaveDialog(MainFrame.this) == FileChooser.APPROVE_OPTION) {
						file=fileDialog.getSelectedFile().getAbsolutePath();
						setXtfLogFile(file);
					}					
				}
			});
		}
		return doXtfLogFileSelBtn;
	}
	private javax.swing.JButton getDoConfigFileSelBtn() {
		if(doConfigFileSelBtn == null) {
			doConfigFileSelBtn = new javax.swing.JButton();
			doConfigFileSelBtn.setText("...");
			doConfigFileSelBtn.addActionListener(new java.awt.event.ActionListener() { 
				public void actionPerformed(java.awt.event.ActionEvent e) {    
					String file=getLogFile();
					FileChooser fileDialog =  new FileChooser(file);
					fileDialog.setDialogTitle(rsrc.getString("MainFrame.configFileChooserTitle"));
					fileDialog.addChoosableFileFilter(new GenericFileFilter(rsrc.getString("MainFrame.configFileFilter"),"toml"));

					if (fileDialog.showSaveDialog(MainFrame.this) == FileChooser.APPROVE_OPTION) {
						file=fileDialog.getSelectedFile().getAbsolutePath();
						setConfigFile(file);
					}					
				}
			});
		}
		return doConfigFileSelBtn;
	}
}
