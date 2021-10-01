package view;

import java.awt.*;
import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;
import controller.*;

/**
 * 
 * Diese Klasse konstruiert das Fenster für die Eingabe neuer Vorlesungen.
 * 
 * @author fabian
 *
 */

public class CreateView extends JFrame{
	
	private JTextField titelEingabe;
	private JTextField profEingabe;
	private JTextField semesterEingabe;
	
	private JButton erstellen;
	private JButton verzeichnisAnzeigen;
	
	private JPanel titel;
	private JPanel prof;
	private JPanel semester;
	
	private Container c;
	
	/**
	 * Der Konstruktor enthält alle Anweisungen zum Bau des Fensters.
	 */
	
	public CreateView() {
		
		//Grundlegende Einstellungen
		c = getContentPane();
		c.setLayout(new GridLayout(5,1,10,10));
		((JPanel)c).setBorder(new EmptyBorder(5, 5, 5, 5));
		setBounds(100,100,500,500);
		setDefaultCloseOperation(EXIT_ON_CLOSE);
		
		//Erzeugung der Bedienelemente
		titelEingabe = new JTextField();
		profEingabe = new JTextField();
		semesterEingabe = new JTextField();
		erstellen = new JButton("Erstellen");
		verzeichnisAnzeigen = new JButton("Verzeichnis anzeigen");
		
		//Eingabepanel Titel
		titel = new JPanel();
		titel.setLayout(new GridLayout(1,1));
		titel.setBorder(new TitledBorder(null, "Titel", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		titel.add(titelEingabe);
		
		//Eingabepanel Prof
		prof = new JPanel();
		prof.setLayout(new GridLayout(1,1));
		prof.setBorder(new TitledBorder(null, "Professor", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		prof.add(profEingabe);
		
		//Eingabepanel Semester
		semester = new JPanel();
		semester.setLayout(new GridLayout(1,1));
		semester.setBorder(new TitledBorder(null, "Semester", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		semester.add(semesterEingabe);
		
		//Hinzufügen aller Elemente zum übergeordneten Container
		c.add(titel);
		c.add(prof);
		c.add(semester);
		c.add(erstellen);
		c.add(verzeichnisAnzeigen);
		
		//Sichtbarmachen des Fensters
		setVisible(true);
	}
	
	/**
	 * Liefert den "Erstellen" Button zurück.
	 * @return erstellen
	 */
	public JButton getErstellen() {
		return erstellen;
	}
	
	/**
	 * Liefert den "Verzeichnis anzeigen" Button zurück.
	 * @return verzeichnisAnzeigen
	 */
	public JButton getVerzeichnisAnzeigen() {
		return verzeichnisAnzeigen;
	}
	
	/**
	 * Liefert das Titel Eingabefeld zurück.
	 * @return titelEingabe
	 */
	public JTextField getTitelEingabe() {
		return titelEingabe;
	}
	
	/**
	 * Liefert das Profname Eingabefeld zurück.
	 * @return profEingabe
	 */
	public JTextField getProfEingabe() {
		return profEingabe;
	}
	
	/**
	 * Liefert das Semester Eingabefeld zurück.
	 * @return semesterEingabe
	 */
	public JTextField getSemesterEingabe() {
		return semesterEingabe;
	}
	
	/**
	 * Fügt dem "Erstellen" und "Verzeichnis anzeigen" Button den jeweiligen Listener hinzu.
	 * @param controller
	 */
	public void setController(ViewController controller) {
		erstellen.addActionListener(controller.getErstellenListener());
		verzeichnisAnzeigen.addActionListener(controller.getAnzeigenListener());
	}

}
