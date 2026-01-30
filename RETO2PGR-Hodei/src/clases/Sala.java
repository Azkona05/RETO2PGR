package clases;

import java.io.Serializable;

import utilidades.Util;

public class Sala implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private int capacidad;

	public Sala() {
		super();
	}

	public Sala(String nombre, int capacidad) {
		super();
		this.nombre = nombre;
		this.capacidad = capacidad;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public int getCapacidad() {
		return capacidad;
	}

	public void setCapacidad(int capacidad) {
		this.capacidad = capacidad;
	}

	@Override
	public String toString() {
		return "Sala [nombre=" + nombre + ", capacidad=" + capacidad + "]";
	}

	public void setDatos() {
		this.nombre = Util.introducirCadena("Introduce el nombre de la sala: ");
		this.capacidad = Util.leerInt("Introduce capacidad de la sala: ");
	}
}
