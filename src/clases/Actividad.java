package clases;

import java.io.Serializable;
import java.time.LocalDate;
import java.util.TreeMap;

import utilidades.Util;

public class Actividad implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String codigo;
	private String nombre;
	private double precio;
	private LocalDate fecha;
	private TreeMap<String, Persona> integrantesActividad;

	public Actividad() {
		super();
	}

	public Actividad(String codigo, String nombre, double precio, LocalDate fecha) {
		super();
		this.codigo = codigo;
		this.nombre = nombre;
		this.precio = precio;
		this.fecha = fecha;
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
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

	public String toString() {
		return "Actividad [nombre=" + nombre + ", precio=" + precio + ", fecha=" + fecha + "]";
	}

	public void setDatos() {
		this.nombre = Util.introducirCadena("Introduce el nombre de la actividad: ");
		this.precio = Util.leerFloat("Introduce el precio de la actividad: ");
		this.fecha = Util.pidoFechaDMA("Introduce la fecha de la actividad ");
	}

}
