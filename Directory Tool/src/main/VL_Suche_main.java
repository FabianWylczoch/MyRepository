package main;

import java.util.*;
import model.*;
import view.*;
import controller.*;

/**
 * Diese Klasse enthält die main Methode der Anwendung.
 * Neben der Erzeugung des Fensters und des Controllers, 
 * wird in ihr auch das Verzeichnis geführt.
 * 
 * @author fabian
 *
 */

public class VL_Suche_main {
	
	private static TreeSet <Vorlesung> verzeichnis;
	
	/**
	 * 
	 * Die main Methode der Anwendung
	 * 
	 * @param args (Kommandozeilenparameter)
	 */
	
	public static void main(String[] args) {
		//Anlegen des Verzeichnisses
		verzeichnis = new TreeSet<>();
		
		//Erstellen des Fensters zum Anlegen neuer Vorlesungen
		CreateView fenster = new CreateView();
		
		//Bekanntmachung von Controller, Fenster und Verzeichnis
		ViewController controller = new ViewController();
		controller.setCreateView(fenster);
		fenster.setController(controller);
		controller.setVerzeichnis(verzeichnis);
			
	}
	
	/**
	 * 
	 * Liefert das in der main Methode erstellte Verzeichnis zurück.
	 * 
	 * @return verzeichnis
	 */
	public static TreeSet<Vorlesung> getVerzeichnis() {
		return verzeichnis;
	}

}
