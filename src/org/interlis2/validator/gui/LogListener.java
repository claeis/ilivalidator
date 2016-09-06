package org.interlis2.validator.gui;

import java.util.ArrayList;
import java.util.Iterator;
import ch.ehi.basics.logging.LogEvent;
import javax.swing.JTextArea;

/** Listener that logs errors including validation results to the GUI.
 */
public class LogListener implements ch.ehi.basics.logging.LogListener {
	MainFrame out=null;
	public LogListener(MainFrame out1){
		out=out1;
	}
	/** called by logging system to log one event.
	 */
	public void logEvent(LogEvent event) {
		ArrayList msgv=ch.ehi.basics.logging.StdListener.formatOutput(event,false,false);
		Iterator msgi=msgv.iterator();
		while(msgi.hasNext()){
			String msg=(String)msgi.next();
			out.logAppend(msg);
		}
	}

}
