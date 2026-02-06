package clases;

import java.io.Serializable;
import java.time.LocalDate;

import utilidades.Util;

public abstract class Persona implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	protected String dni;
	protected String nombre;
	protected String apellido;
	protected String email;
	protected String telefono;
	protected LocalDate fechaNacimiento;

	public Persona(String dni, String nombre, String apellido, String email, String telefono,
			LocalDate fechaNacimiento) {
		super();
		this.dni = dni;
		this.nombre = nombre;
		this.apellido = apellido;
		this.email = email;
		this.telefono = telefono;
		this.fechaNacimiento = fechaNacimiento;
	}

	public Persona(String dni) {
		super();
		this.dni = dni;
	}

	public String getDni() {
		return dni;
	}

	public void setDni(String dni) {
		this.dni = dni;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido() {
		return apellido;
	}

	public void setApellido(String apellido) {
		this.apellido = apellido;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public LocalDate getFechaNacimiento() {
		return fechaNacimiento;
	}

	public void setFechaNacimiento(LocalDate fechaNacimiento) {
		this.fechaNacimiento = fechaNacimiento;
	}

	@Override
	public String toString() {
		return "Persona [dni=" + dni + ", nombre=" + nombre + ", apellido=" + apellido + ", email=" + email
				+ ", telefono=" + telefono + ", fechaNacimiento=" + fechaNacimiento + "]";
	}

	public void setDatos(String dni) {
		boolean rep = false;
		this.dni = dni;
		this.nombre = Util.introducirCadena("Nombre: ");
		this.apellido = Util.introducirCadena("Apellido: ");
		this.email = Util.validarEmail("Email: ");
		do {
			this.telefono = Util.introducirCadena("Telefono: ");
			if (telefono.length() < 9 || telefono.length() > 12) {
				System.out.println("El telefono debe tener entre 9 y 12 digitos.");
			}
		} while (telefono.length() < 9 || telefono.length() > 12);
		this.fechaNacimiento = Util.pidoFechaDMA("Fecha de nacimiento ");
		do {
			if (fechaNacimiento.isAfter(LocalDate.now())) {
				System.out.println("La fecha de nacimiento no puede ser mayor que la fecha del dia de hoy.");
				rep = true;
			} else {
				rep = false;
			}
		} while (rep);
	}

	public abstract double calcularDinero();

}
