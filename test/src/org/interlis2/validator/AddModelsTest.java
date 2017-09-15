package org.interlis2.validator;

import static org.junit.Assert.*;
import org.junit.Test;
import ch.ehi.basics.settings.Settings;

public class AddModelsTest {
	
	// Der Benutzer setzt ein Modell und einen Pfad zum Ordner des Modells.
	// Es wird getestet ob das gesetzte Modell in dem gesetzten Pfad gefunden wird und den passenden Inhalt enthaelt.
	// Es wird mit einer CSV Datei getestet.
	@Test
	public void csv_AddSingleModel_Ok() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelCsv");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/CsvTest.csv";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertTrue(ret);
	}
	
	// Der Benutzer setzt mehrere Modelle und einen Pfad zum Ordner der Modelle.
	// Es wird getestet ob die gesetzten Modelle in dem gesetzten Pfad gefunden werden koennen und den passenden Inhalt enthalten.
	// Es wird mit CSV Dateien getestet.
	@Test
	public void csv_AddMultipleModels_Ok() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelCsv;ModelCsv2;ModelCsv3");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/CsvTest.csv";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertTrue(ret);
	}
	
	// Der Benutzer setzt ein Modell und einen Pfad zum Ordner des Modells.
	// Es wird getestet ob das gesetzte Modell in dem gesetzten Pfad gefunden wird und den passenden Inhalt enthaelt.
	// Es wird mit einer XTF Datei getestet. In diesem Test kann das passende Modell nicht gefunden werden.
	//
	// Das passende Modell: ModelXtf.ili --> ModelXtf existiert im selben Pfad.
	// Das Modell darf nicht gefunden werden, wenn setModels gesetzt ist.
	@Test
	public void xtf_ModelNotFound_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelXtf2");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/XtfTest.xtf";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
	
	// Der Benutzer setzt ein Modell und einen Pfad zum Ordner des Modells.
	// Es wird getestet ob das gesetzte Modell in dem gesetzten Pfad gefunden wird und den passenden Inhalt enthaelt.
	// Es wird mit einer CSV Datei getestet. In diesem Test kann das passende Modell nicht gefunden werden.
	@Test
	public void csv_WrongModelName_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelCsv2");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/CsvTest.csv";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
	
	// Der Benutzer setzt mehrere Modelle und einen Pfad zum Ordner der Modelle.
	// Es wird getestet ob die gesetzten Modelle im gesetzten Pfad gefunden werden und die passenden Inhalte enthalten.
	// Es wird mit CSV Dateien getestet. In diesem Test kann kein passendes Modell gefunden werden.
	@Test
	public void csv_MultipleModelsNotFound_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelItf;ModelXtf1;ModelXtf2");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/CsvTest.csv";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
	
	// Der Benutzer setzt einen Pfad. Innerhalb des Pfades, befinden sich die folgenden Dateien.
	// - ModelCsv.csv --> 3 Attribute.
	// - ModelCsv.ili --> ModelCsv --> 3 Attribute.
	// - ModelCsv2.ili --> ModelCsv2 --> 2 Attribute.
	//
	// Geprueft wird:
	// - ModelCsv2.ili --> ModelCsv2 (in welchem sich 2 Attribute befinden).
	//
	// Ignoriert wird:
	// - ModelCsv.ili --> ModelCsv (Modell hat den selben Namen wie die CSV Datei). --> 3 Attribute (Passende Anzahl Attribute).
	//
	// Falls setModels funktioniert, wird muss folgende Fehlermeldung ausgegeben werden:
	// 'attribute count of record: 3 not found in classes of model: ModelCsv2'.
	@Test
	public void csv_ModelNameLikeCsvFile_ModelNotFound_Fail() {
		Settings settings=new Settings();
		settings.setValue(Validator.SETTING_MODELNAMES, "ModelCsv2");
		settings.setValue(Validator.SETTING_ILIDIRS, "test/data/addModels");
		String[] listOfFiles = new String[1];
		listOfFiles[0]="test/data/addModels/ModelCsv.csv";
		boolean ret=Validator.runValidation(listOfFiles, settings);
		assertFalse(ret);
	}
}