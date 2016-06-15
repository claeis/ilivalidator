package org.interlis2.validator;

import java.io.File;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import ch.ehi.basics.logging.AbstractFilteringListener;
import ch.ehi.basics.logging.FileListener;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.logging.StdListener;
import ch.ehi.basics.logging.StdLogEvent;
import ch.interlis.iox.IoxLogEvent;

public class StdLogger extends AbstractFilteringListener {
	private int ioxErrc=0;
	private String logfile=null;
	private final static String INFO="Info";
	public StdLogger(String logfile){
		this.logfile=logfile;
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
			ioxErrc++;
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
		if(event instanceof IoxLogEvent && logfile!=null){
			outputMsgLine(LogEvent.ADAPTION,0,msgTimestamp+INFO+": see <"+logfile+"> for more validation results");
		}
	}

	@Override
	public void outputMsgLine(int arg0, int arg1, String msg) {
		if(msg.endsWith("\n")){
			System.err.print(msg);
		}else{
			System.err.println(msg);
		}
		System.err.flush();
	}
	
	@Override
	public String getMessageTag(LogEvent event){
		switch(event.getEventKind()){
		case LogEvent.ERROR: 
				return "Error";
		case LogEvent.ADAPTION: 
				return "Warning";
		default:
				return INFO;
		}
	}
	@Override
	public boolean skipEvent(LogEvent event){
		if(event instanceof IoxLogEvent){
			if(ioxErrc>0 && logfile!=null){
				return true;
			}
		}
		return super.skipEvent(event);
	}
}
