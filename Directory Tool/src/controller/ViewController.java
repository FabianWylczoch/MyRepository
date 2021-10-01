package controller;

import java.util.*;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.table.AbstractTableModel;
import main.VL_Suche_main;
import java.awt.event.*;
import java.io.*;
import java.io.File;
import java.io.FileWriter;
import java.io.PrintWriter;
import model.*;
import view.*;

/**
 * 
 * Diese Klasse enthält als Unterklasse alle benötigten Button Listener.
 * 
 * @author fabian
 *
 */

public class ViewController {
	
	//Alle Referenzen mit entsprechenden setter Methoden
	CreateView createview;
	VerzeichnisView verzeichnisview;
	TreeSet <Vorlesung> verzeichnis;
	
	public void setVerzeichnis(TreeSet <Vorlesung> verzeichnis) {
		this.verzeichnis = verzeichnis;
	}
	
	public void setCreateView(CreateView createview) {
		this.createview = createview;
	}
	
	public void setVerzeichnisview(VerzeichnisView verzeichnisview) {
		this.verzeichnisview = verzeichnisview;
	}
	
	/**
	 * 
	 * Listener für den "Erstellen" Button der CreateView
	 * 
	 * @author fabian
	 *
	 */
	
	class ErstellenListener implements ActionListener {
		
		String titel;
		String prof;
		String semester;
		
		public void actionPerformed(ActionEvent e) {
			
			//Lokale Strings auf die Eingabe der jeweiligen Felder setzten
			titel = createview.getTitelEingabe().getText();
			prof = createview.getProfEingabe().getText();
			semester = createview.getSemesterEingabe().getText();
			
			//Erstellen einer neuen Vorlesung und Hinzufügen zum Verzeichnis
			verzeichnis.add(new Vorlesung(titel,prof,semester));
			
			//Leeren der Eingabefelder
			createview.getTitelEingabe().setText("");
			createview.getProfEingabe().setText("");
			createview.getSemesterEingabe().setText("");			
		}		
	}
	
	/**
	 * Gibt den oben beschriebenen Listener zurück
	 * @return ErstellenListener
	 */
	public ErstellenListener getErstellenListener() {
		return new ErstellenListener();
	}
	
	
	
	/**
	 * 
	 * Listener für den "Anzeigen" Button der CreateView
	 * 
	 * @author fabian
	 *
	 */
	
	class AnzeigenListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Hier wird analog zur main Methode das Verzeichnisfenster und der 
			//Controller erstellt und miteinander bekanntgemacht.
			VerzeichnisView fenster = new VerzeichnisView(verzeichnis);
			ViewController controller = new ViewController();	
			controller.setVerzeichnisview(fenster);
			fenster.setController(controller);
		}		
	}
	
	/**
	 * Gibt den oben beschriebenen Listener zurück
	 * @return AnzeigenListener
	 */
	public AnzeigenListener getAnzeigenListener() {
		return new AnzeigenListener();
	}
	
	
	
	/**
	 * 
	 * Listener für den "Speichern" Button der VerzeichnisView
	 * 
	 * @author fabian
	 *
	 */
	
	class SpeichernListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Schreibt alle Vorlesungen des gesamten aktuellen Verzeichnisses in eine Datei im src Projektverzeichnis.
			verzeichnis = VL_Suche_main.getVerzeichnis();
			File output = new File("src/Verzeichnis.txt");
			
			try {
				PrintWriter outputWriter = new PrintWriter(new FileWriter(output));
				
				outputWriter.printf("%-20s %-20s %s %n %n", "Titel:", "Profname:", "Semester:");
				
				Iterator <Vorlesung> it = verzeichnis.iterator();
				while(it.hasNext()) {
					Vorlesung vl = (Vorlesung) it.next();
					outputWriter.print(vl);
				}
				outputWriter.close();
				JOptionPane.showMessageDialog(null, "Gespeichert!");
			}
			catch(IOException ioe){}
		}
	}
	
	/**
	 * Gibt den oben beschriebenen Listener zurück
	 * @return SpeichernListener
	 */
	public SpeichernListener getSpeichernListener() {
		return new SpeichernListener();
	}
	
	
	
	/**
	 * 
	 * Listener für den "Suchen" Button der VerzeichnisView
	 * 
	 * @author fabian
	 *
	 */
	
	class SuchenListener implements ActionListener {
		
		@Override
		public void actionPerformed(ActionEvent e) {
			
			//Holt das aktuelle Verzeichnis und erzeugt ein Verzeichnis, das nach der Suche angezeigt wird.
			verzeichnis = VL_Suche_main.getVerzeichnis();
			TreeSet <Vorlesung> durchsuchtesVerzeichnis = new TreeSet<>();
			
			//Holt den Suchbegriff
			String suche = verzeichnisview.getSuchfeld().getText();
			
			//Bei keinem eingegebenen Begriff wird das originale Verzeichnis zurückgeliefert.
			if (suche.equals("")) {
				durchsuchtesVerzeichnis = VL_Suche_main.getVerzeichnis();
			}
			//Sonst wird das gesamte Verzeichnis durchlaufen und jede Vorlesung, bei der entweder 
			//Titel, Profname oder Semester mit dem Suchbegriff übereinstimmen, dem zurückzugebenden Verzeichnis hinzugefügt.
			else {
			Iterator <Vorlesung> it = verzeichnis.iterator();
				while (it.hasNext()) {
					Vorlesung vl = (Vorlesung) it.next();				
					if (vl.getTitel().equalsIgnoreCase(suche) || vl.getProfname().equalsIgnoreCase(suche) || vl.getSemester().equalsIgnoreCase(suche)) {
						durchsuchtesVerzeichnis.add(vl);
					}
				}
			}
			
			//Hier wird wieder das TreeSet in ein zweidimensionales Feld überführt.
			Vorlesung[] array = durchsuchtesVerzeichnis.toArray(new Vorlesung[durchsuchtesVerzeichnis.size()]);		
			String[] columnNames = {"Titel", "Professor", "Semester"};
			String[][] tabelle = new String[array.length][3];
			int i = 0;
			while (i<array.length) {
				tabelle[i][0] = array[i].getTitel();
				tabelle[i][1] = array[i].getProfname();
				tabelle[i][2] = array[i].getSemester();
				i++;
				}
			
			//Diese Klasse beschreibt ein mithilfe des 2D Arrays gebautes Modell,
			//das anschließend dem JTable in der VerzeichnisView übergeben werden kann.
			class MeinModel extends AbstractTableModel{				
								
				@Override
				public int getRowCount() {
					return tabelle.length;
				}

				@Override
				public int getColumnCount() {
					return 3;
				}
				
				@Override
				public String getColumnName(int col) {
		            return columnNames[col];
		        }

				@Override
				public Object getValueAt(int rowIndex, int columnIndex) {
					return tabelle[rowIndex][columnIndex];
				}
				
			}
			
			verzeichnisview.getVerzeichnisTabelle().setModel(new MeinModel());			
		}		
	}
	
	/**
	 * Gibt den oben beschriebenen Listener zurück
	 * @return SuchenListener
	 */
	public SuchenListener getSuchenListener() {
		return new SuchenListener();
	}

}
