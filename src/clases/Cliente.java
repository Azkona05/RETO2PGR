package clases;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class Cliente extends Persona {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static double cuota = 30.0;
	private LocalDate fechaDeAlta;
	private Map<String, Actividad> actividadesC = new HashMap<>();

	public Cliente(String dni) {
		super(dni);
		this.fechaDeAlta = LocalDate.now();
	}

	public Cliente(String dni, String nombre, String apellido, String email, String telefono, LocalDate fechaNacimiento,
			LocalDate fechaDeAlta) {
		super(dni, nombre, apellido, email, telefono, fechaNacimiento);
		this.fechaDeAlta = LocalDate.now();
	}

	public Map<String, Actividad> getActividadesC() {
		return actividadesC;
	}

	public void setActividadesC(Map<String, Actividad> actividadesC) {
		this.actividadesC = actividadesC;
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
	}

	public double calcularDinero() {
		double cuotaTotal;
		Cliente c;
		double actCuotaTotal = 0;
		Iterator<Map.Entry<String, Actividad>> it = actividadesC.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry<String, Actividad> entry = it.next();
			String key = entry.getKey();
			Actividad a = entry.getValue();
			actCuotaTotal = +a.getPrecio();
			System.out.println(key + "->" + a);
		}

		return actCuotaTotal + this.cuota;
	}

}
