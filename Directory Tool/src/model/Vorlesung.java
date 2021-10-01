package model;

/**
 * 
 * Diese Klasse dient als Bauplan für die Vorlesungen,
 * die im entsprechenden Fenster angelegt werden können.
 * 
 * @author fabian
 *
 */

public class Vorlesung implements Comparable{
	
	//Eine Vorlesung besitzt die Attribute Titel, Name des Professors und das Semester.
	private String titel;
	private String profname;
	private String semester;
	
	/**
	 * 
	 * Der Konstruktor setzt die zuvor genannten Attribute fest.
	 * 
	 * @param titel
	 * @param profname
	 * @param semester
	 */
	public Vorlesung(String titel, String profname, String semester) {	
		this.titel = titel;
		this.profname = profname;
		this.semester = semester;	
	}
	
	/**
	 * 
	 * Liefert den Titel der Vorlesung zurück.
	 * 
	 * @return titel
	 */
	public String getTitel() {
		return titel;
	}
	
	/**
	 * 
	 * Liefert den Namen des Professors der Vorlesung zurück.
	 * 
	 * @return profname
	 */
	public String getProfname() {
		return profname;
	}
	
	/**
	 * 
	 * Liefert das Semester der Vorlesung zurück.
	 * 
	 * @return semester
	 */
	public String getSemester() {
		return semester;
	}

	/**
	 * 
	 * Ordnet die Vorlesungen in der Tabelle alphabethisch nach Titel.
	 * 
	 * @return cv
	 */
	@Override
	public int compareTo(Object o) {
		int cv = this.getTitel().compareTo(((Vorlesung)o).getTitel());
		return cv;
	}
	
	/**
	 * 
	 * Liefert einen String, in dem zwischen Beginn des Wortes Titel, Profnamen und Semester jeweils
	 * 20 Plätze frei sind. (Für Formatierung der .txt Datei)
	 * 
	 * @return string
	 */
	@Override
	public String toString() {
		return String.format("%-20s %-20s %s %n", titel, profname, semester);
	}
	
}
