package clases;

import java.time.LocalDate;

import utilidades.Util;

public class Cliente extends Persona {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static double cuota = 30.0;
	private LocalDate fechaDeAlta;
	
	public Cliente(String dni) {
		super(dni);
	}
	
	public Cliente(String dni, String nombre, String apellido, String email, String telefono, LocalDate fechaNacimiento,
			LocalDate fechaDeAlta) {
		super(dni, nombre, apellido, email, telefono, fechaNacimiento);
		this.fechaDeAlta = fechaDeAlta;
	}
	public LocalDate getFechaDeAlta() {
		return fechaDeAlta;
	}
	public void setFechaDeAlta(LocalDate fechaDeAlta) {
		this.fechaDeAlta = fechaDeAlta;
	}
	public static double getCuota() {
		return cuota;
	}

	@Override
	public String toString() {
		return super.toString() + "Cliente [fechaDeAlta=" + fechaDeAlta + "]";
	}

	public void setDatos(String dni) {
		super.setDatos(dni);
		this.fechaDeAlta = Util.pidoFechaDMA("Fecha de alta ");
	}
	
}
