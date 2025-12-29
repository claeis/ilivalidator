package org.interlis2.validator;

import java.util.ArrayList;

import ch.ehi.basics.logging.LogEvent;


public class LogCollector implements ch.ehi.basics.logging.LogListener {
	private ArrayList<LogEvent> events=new ArrayList<LogEvent>();


	@Override
	public void logEvent(LogEvent event) {
		events.add(event);
	}
	
	public ArrayList<LogEvent> getEvents() {
		return events;
	}
    public ArrayList<LogEvent> getErrs() {
        ArrayList<LogEvent> errs=new ArrayList<LogEvent>();
        for(LogEvent event:events) {
            if(event.getEventKind()==LogEvent.ERROR) {
                errs.add(event);
            }
        }
        return errs;
    }



}
