package clases;

import java.time.LocalDate;

import utilidades.Util;

public class Trabajador extends Persona{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Puesto puesto;
	private String tipoContrato;
	private LocalDate fechaContratacion;
	
	public Trabajador(String dni, String nombre, String apellido, String email, String telefono,
			LocalDate fechaNacimiento, Puesto puesto, String tipoContrato, LocalDate fechaContratacion) {
		super(dni, nombre, apellido, email, telefono, fechaNacimiento);
		this.puesto = puesto;
		this.tipoContrato = tipoContrato;
		this.fechaContratacion = fechaContratacion;
	}

	public Trabajador(String dni) {
		super(dni);
	}

	public Puesto getPuesto() {
		return puesto;
	}

	public void setPuesto(Puesto puesto) {
		this.puesto = puesto;
	}

	public String getTipoContrato() {
		return tipoContrato;
	}

	public void setTipoContrato(String tipoContrato) {
		this.tipoContrato = tipoContrato;
	}

	public LocalDate getFechaContratacion() {
		return fechaContratacion;
	}

	public void setFechaContratacion(LocalDate fechaContratacion) {
		this.fechaContratacion = fechaContratacion;
	}

	@Override
	public String toString() {
		return "Trabajador [puesto=" + puesto + ", tipoContrato=" + tipoContrato + ", fechaContratacion="
				+ fechaContratacion + "]";
	}
	
	public void setDatos(String dni) {
		super.setDatos(dni);
		try {
			this.puesto = Puesto.valueOf(Util.introducirCadena("Puesto del trabajador (MONITOR, ENTRENADOR, FISIO, SOCORRISTA): ").toUpperCase());
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		}
		this.tipoContrato = Util.introducirCadena("Tipo de contrato del trabajador: ");
		this.fechaContratacion = Util.pidoFechaDMA("Fecha de contratacion del trabajador ");
	}
}
