package org.interlis2.validator;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import ch.ehi.basics.logging.FileListener;
import ch.ehi.basics.logging.LogEvent;
import ch.interlis.iox.IoxLogEvent;

public class FileLogger extends FileListener {

	public FileLogger(File arg0) {
		super(arg0,false);
	}

	@Override
	public void outputEvent(LogEvent event,ArrayList msgv)
	{
		// get event tag
		String msgTag=getMessageTag(event);
		if(msgTag==null){
			msgTag="";
		}else{
			msgTag=msgTag+": ";
		}
		
		// get timetstamp
		String msgTimestamp=null;
		msgTimestamp=getTimestamp();
		if(msgTimestamp==null){
			msgTimestamp="";
		}else{
			msgTimestamp=msgTimestamp+": ";
		}
		
		String objRef=null;
		if(event instanceof IoxLogEvent){
			objRef="";
			IoxLogEvent ioxEvent=(IoxLogEvent) event;
			if(ioxEvent.getSourceLineNr()!=null){
				objRef=objRef+"line "+ioxEvent.getSourceLineNr()+": ";
			}
			if(ioxEvent.getSourceObjectTag()!=null){
				objRef=objRef+ioxEvent.getSourceObjectTag()+": ";
			}
			if(ioxEvent.getSourceObjectTechId()!=null){
				objRef=objRef+ioxEvent.getSourceObjectTechId()+": ";
			}
			if(ioxEvent.getSourceObjectXtfId()!=null){
				objRef=objRef+"tid "+ioxEvent.getSourceObjectXtfId()+": ";
			}
			if(ioxEvent.getSourceObjectUsrId()!=null){
				objRef=objRef+ioxEvent.getSourceObjectUsrId()+": ";
			}
		}else{
			objRef="";
		}
		
		// output all lines
		Iterator msgi=msgv.iterator();
		while(msgi.hasNext()){
			String msg=(String)msgi.next();
			outputMsgLine(event.getEventKind(),event.getCustomLevel(),msgTimestamp+msgTag+objRef+msg);
		}
		
	}
	@Override
	public String getMessageTag(LogEvent event){
		switch(event.getEventKind()){
		case LogEvent.ERROR: 
				return "Error";
		case LogEvent.ADAPTION: 
				return "Warning";
		default:
				return "Info";
		}
	}
}
