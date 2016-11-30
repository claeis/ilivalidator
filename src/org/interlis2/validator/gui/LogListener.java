package org.interlis2.validator.gui;

import java.util.ArrayList;
import java.util.Iterator;

import ch.ehi.basics.logging.LogEvent;

import javax.swing.JTextArea;

/** Listener that logs errors including validation results to the GUI.
 */
public class LogListener extends ch.interlis.iox_j.logging.StdLogger {
	MainFrame out=null;
	public LogListener(MainFrame out1,String logfileName) {
		super(logfileName);
		out=out1;
	}
	@Override
	public void outputMsgLine(int arg0, int arg1, String msg) {
		if(msg.endsWith("\n")){
			out.logAppend(msg);
		}else{
			out.logAppend(msg+"\n");
		}
	}

}
