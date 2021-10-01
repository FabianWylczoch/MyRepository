package view;

import model.*;
import view.*;
import controller.*;
import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.AbstractTableModel;
import java.util.*;
import java.util.List;
import java.awt.event.*;

/**
 * 
 * Diese Klasse konstruiert das Fenster zum Anzeigen des Vorlesungsverzeichnisses
 * 
 * @author fabian
 *
 */

public class VerzeichnisView extends JFrame{
	
	Container c;
	JTable verzeichnisTabelle;
	TreeSet <Vorlesung> verzeichnis;
	Vorlesung[] array;
	String[][] tabelle;
	
	JPanel suchelemente;
	JPanel bedienelemente;
	JTextField suchfeld;
	JButton suchen;
	JButton speichern;
	
	/**
	 * 
	 * Der Konstruktor bekommt beim Aufruf das aktuelle Verzeichnis übergeben, 
	 * das im Fenster angezeigt werden soll.
	 * 
	 * @param verzeichnis
	 */
	
	public VerzeichnisView(TreeSet <Vorlesung> verzeichnis) {
		
		this.verzeichnis = verzeichnis;
		
		//Formt das als TreeSet vorliegende Verzeichnis über ein Array in ein zweidimensionales 
		//Stringfeld um, das im Konstruktor zusammen mit den Spaltennamen übergeben werden kann.
		Vorlesung[] array = this.verzeichnis.toArray(new Vorlesung[this.verzeichnis.size()]);
		String[][] tabelle = new String[array.length][3];
		int i = 0;
		while (i<array.length) {
			tabelle[i][0] = array[i].getTitel();
			tabelle[i][1] = array[i].getProfname();
			tabelle[i][2] = array[i].getSemester();
			i++;
			}
		String[] columnNames = {"Titel", "Professor", "Semester"};
		verzeichnisTabelle = new JTable(tabelle,columnNames);
		
		//Grundlegende Einstellungen
		c = getContentPane();
		((JPanel)c).setBorder(new EmptyBorder(5, 5, 5, 5));
		setBounds(150,150,500,500);
		c.setLayout(new GridLayout(2,1));
		setDefaultCloseOperation(HIDE_ON_CLOSE);
		
		//Erzeugung der Bedienelemente
		bedienelemente = new JPanel();
		suchelemente = new JPanel();
		suchfeld = new JTextField();
		suchen = new JButton("suchen");
		speichern = new JButton("speichern");
		
		//Design und Hinzufügen der Bedienelemente
		bedienelemente.setLayout(new GridLayout(2,1));
		suchelemente.setLayout(new GridLayout(2,1));
		suchelemente.setBorder(new TitledBorder(null, "Suche", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		suchelemente.add(suchfeld);
		suchelemente.add(suchen);
		bedienelemente.add(suchelemente);
		bedienelemente.add(speichern);
		
		//Hinzufügen des Verzeichnisses und der Bedienelemente zum übergeordneten Container
		c.add(new JScrollPane(verzeichnisTabelle));
		c.add(bedienelemente);
		
		//Sichtbarmachen des Fensters
		setVisible(true);		
	}
	
	/**
	 * Fügt dem "Speichern" und "Suchen" Button den entsprechenden Listener hinzu.
	 * @param controller
	 */
	public void setController(ViewController controller) {
		speichern.addActionListener(controller.getSpeichernListener());
		suchen.addActionListener(controller.getSuchenListener());
	}
	
	/**
	 * Liefert das Suchfeld zurück
	 * @return suchfeld
	 */
	public JTextField getSuchfeld() {
		return suchfeld;
	}
	
	/**
	 * Liefert die fertig implementierte Tabelle zurück.
	 * @return verzeichnisTabelle
	 */
	public JTable getVerzeichnisTabelle() {
		return verzeichnisTabelle;
	}

}
