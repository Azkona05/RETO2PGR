package clases;

import java.io.Serializable;
import java.time.LocalDate;

public class Actividad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String nombre;
	private double precio;
	private LocalDate fecha;

	public Actividad() {
		super();
	}

	public Actividad(String nombre, double precio, LocalDate fecha) {
		super();
		this.nombre = nombre;
		this.precio = precio;
		this.fecha = fecha;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public double getPrecio() {
		return precio;
	}

	public void setPrecio(double precio) {
		this.precio = precio;
	}

	public LocalDate getFecha() {
		return fecha;
	}

	public void setFecha(LocalDate fecha) {
		this.fecha = fecha;
	}

}
