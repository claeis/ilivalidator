package org.interlis2.validator.demo;

import ch.ehi.basics.settings.Settings;
import ch.interlis.ili2c.metamodel.TransferDescription;
import ch.interlis.iom.IomObject;
import ch.interlis.iox.IoxValidationConfig;
import ch.interlis.iox_j.logging.LogEventFactory;
import ch.interlis.iox_j.validator.InterlisFunction;
import ch.interlis.iox_j.validator.ObjectPool;
import ch.interlis.iox_j.validator.Value;

public class CheckGebaeudeVersicherungsSystemIoxPlugin implements InterlisFunction {
	private LogEventFactory logger=null;
	@Override
	public void init(TransferDescription td, Settings settings,
			IoxValidationConfig validationConfig, ObjectPool objectPool,
			LogEventFactory logEventFactory) {
		logger=logEventFactory;
		
	}

	@Override
	public Value evaluate(IomObject mainObj, Value[] actualArguments) {
		String value=actualArguments[0].getValue();
		logger.addEvent(logger.logInfoMsg("evaluate "+getQualifiedIliName()+"(assNr="+value+")"));
		if(value.startsWith("ok")){
			return new Value(true);
		}
		return new Value(false);
	}

	@Override
	public String getQualifiedIliName() {
		return "Beispiel3.checkGebaeudeVersicherungsSystem";
	}

}
