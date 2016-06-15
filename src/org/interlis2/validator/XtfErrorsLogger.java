package org.interlis2.validator;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.interlis2.validator.models.ILIVERRORS;
import org.interlis2.validator.models.IliVErrors.ErrorLog.Error_Type;

import com.vividsolutions.jts.index.strtree.STRtree;

import ch.ehi.basics.logging.AbstractStdListener;
import ch.ehi.basics.logging.LogEvent;
import ch.ehi.basics.logging.LogListener;
import ch.interlis.iom_j.Iom_jObject;
import ch.interlis.iom_j.xtf.XtfModel;
import ch.interlis.iom_j.xtf.XtfWriterBase;
import ch.interlis.iox.IoxException;
import ch.interlis.iox.IoxLogEvent;
import ch.interlis.iox.IoxWriter;
import ch.interlis.iox_j.EndBasketEvent;
import ch.interlis.iox_j.EndTransferEvent;
import ch.interlis.iox_j.ObjectEvent;
import ch.interlis.iox_j.StartBasketEvent;
import ch.interlis.iox_j.StartTransferEvent;
import ch.interlis.models.ILISMETA07;

public class XtfErrorsLogger implements LogListener {
	IoxWriter out=null;
	private int objc=1;
	public XtfErrorsLogger(File logFile,String sender)
	{
		try {
			out=new XtfWriterBase(logFile, ILIVERRORS.getIoxMapping(), "2.3");
			((XtfWriterBase)out).setModels(new XtfModel[]{ILIVERRORS.getXtfModel()});
			StartTransferEvent startTransferEvent = new StartTransferEvent();
			startTransferEvent.setSender(sender);
			out.write(startTransferEvent);
			StartBasketEvent startBasketEvent = new StartBasketEvent( ILIVERRORS.ErrorLog, "b1" );
			out.write( startBasketEvent );
		} catch (IoxException e) {
			throw new IllegalStateException(e);
		}
	}
	@Override
	public void logEvent(LogEvent event) {
		org.interlis2.validator.models.IliVErrors.ErrorLog.Error iomObj=new org.interlis2.validator.models.IliVErrors.ErrorLog.Error("o"+objc++);
		iomObj.setMessage(event.getEventMsg());
		switch(event.getEventKind()){
		case LogEvent.ERROR: 
				iomObj.setType(Error_Type.Error);
				break;
		case LogEvent.ADAPTION: 
			iomObj.setType(Error_Type.Warning);
				break;
		case LogEvent.STATE: 
			iomObj.setType(Error_Type.Info);
				break;
		default:
			iomObj.setType(Error_Type.DetailInfo);
				break;
		}
		if(event instanceof IoxLogEvent){
			IoxLogEvent ioxEvent=(IoxLogEvent)event;
			iomObj.setObjTag(ioxEvent.getSourceObjectTag());
			iomObj.setTid(ioxEvent.getSourceObjectXtfId());
			iomObj.setTechtId(ioxEvent.getSourceObjectTechId());
			iomObj.setUserId(ioxEvent.getSourceObjectUsrId()); 
			iomObj.setIliQName(ioxEvent.getModelEleQName());
			iomObj.setDataSource(ioxEvent.getDataSource());
			if(ioxEvent.getSourceLineNr()!=null){
				iomObj.setLine(ioxEvent.getSourceLineNr());
			}
			if(ioxEvent.getGeomC1()!=null && ioxEvent.getGeomC2()!=null){
				Iom_jObject iomCoord=new Iom_jObject("COORD",null);
				iomCoord.setattrvalue("C1", ioxEvent.getGeomC1().toString());
				iomCoord.setattrvalue("C2", ioxEvent.getGeomC2().toString());
				iomObj.addattrobj(iomObj.tag_Geometry, iomCoord);
			}
		}
		if(event.getException()!=null){
			StringBuffer details=new StringBuffer();
			logThrowable(details, "",event.getException(), true);
			iomObj.setTechDetails(details.toString());
		}else if(event.getOrigin()!=null){
			iomObj.setTechDetails(AbstractStdListener.fmtOriginMsg(event.getOrigin(), ""));
		}
		try {
			out.write(new ObjectEvent(iomObj));
		} catch (IoxException e) {
			throw new IllegalStateException(e);
		}
	}
	public void close()
	{
		if(out!=null){
			try {
				out.write( new EndBasketEvent() );
				out.write( new EndTransferEvent() );
				out.flush();
				out.close();
			} catch (IoxException e) {
				throw new IllegalStateException(e);
			}
			out=null;
		}
	}
	private static void logThrowable(StringBuffer out,String ind,Throwable ex,boolean doStacktrace){
		String msg=ex.getLocalizedMessage();
		if(msg!=null){
			msg=msg.trim();
			if(msg.length()==0){
				msg=null;
			}
		}
		if(msg==null){
			msg=ex.getClass().getName();
		}
		out.append(ind+msg+"\n");
		if(doStacktrace){
			StackTraceElement[] stackv=ex.getStackTrace();
			for(int i=0;i<stackv.length;i++){
				out.append(ind+"    "+stackv[i].toString()+"\n");
			}
		}
		Throwable ex2=ex.getCause();
		if(ex2!=null){
			logThrowable(out,ind+"  ",ex2,doStacktrace);
		}
		if(ex instanceof java.sql.SQLException){
			java.sql.SQLException exTarget=(java.sql.SQLException)ex;
			java.sql.SQLException exTarget2=exTarget.getNextException();
			if(exTarget2!=null){
				logThrowable(out,ind+"  ",exTarget2,doStacktrace);
			}
		}
		if(ex instanceof InvocationTargetException){
			InvocationTargetException exTarget=(InvocationTargetException)ex;
			Throwable exTarget2=exTarget.getTargetException();
			if(exTarget2!=null){
				logThrowable(out,ind+"  ",exTarget2,doStacktrace);
			}
		}
	}
	
}
