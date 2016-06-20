/* This file is part of the avgbs2txt project.
 * For more information, please see <http://www.eisenhutinformatik.ch/avgbs2txt/>.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 2.1 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library; if not, write to the Free Software
 * Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 */
package org.interlis2.validator.gui;

import java.util.ArrayList;
import java.util.Iterator;
import ch.ehi.basics.logging.LogEvent;
import javax.swing.JTextArea;

/** listener that logs errors to GUI.
 * @author ce
 * @version $Revision: 1.0 $ $Date: 10.02.2005 $
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
