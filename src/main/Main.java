package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import clases.Actividad;
import clases.Cliente;
import clases.Persona;
import clases.Puesto;
import clases.Trabajador;
import exception.ValidarDniException;
import utilidades.AñadirObjetoSinCabecera;
import utilidades.Util;

public class Main {

	public static void main(String[] args) {
		File fichPer = new File("personas.obj");
		File fichAct = new File("actividades.obj");

		crearDatosPrueba(fichPer, fichAct);

		int opc = 0;
		do {
			opc = Util.leerInt(
					"1.- Alta de Persona \n2.- Gestion de las actividades \n3.- Registro \n4.- Listado de las actividades \n5.- Listado de los trabajadores (antiguedad) \n6.- Busqueda especial \n7.- Estadisticas \n8.- Modificaciones \n9.- Baja de persona \n10.- Baja de actividades \n11.- Salir del programa");
			switch (opc) {
			case 1:
				altaPersona(fichPer);
				break;
			case 2:
				altaActividad(fichAct, fichPer);
				break;
			case 3:
				inscripcionActividad(fichPer, fichAct);
				break;
			case 4:
				listadoActividades(fichAct);
				break;
			case 5:
				listadoAntiguedad(fichPer);
				break;
			case 6:
				busquedaEspecial(fichPer, fichAct);
				break;
			case 7:
				estadisticas(fichAct, fichPer);
				break;
			case 8:
				modificacion(fichPer, fichAct);
				break;
			case 9:
				bajaPersona(fichPer, fichAct);
				break;
			case 10:
				File aux = new File("Personas.temp");
				bajaActividad(fichAct, aux);
				break;
			case 11:
				System.out.println("Saliendo del programa.");
				break;
			default:
				System.err.println("Opcion no valida.");
				break;
			}
		} while (opc != 11);

	}

	private static void crearDatosPrueba(File fichPer, File fichAct) {
		// 1. CREAR PERSONAS
		List<Persona> listaPersonas = new ArrayList<>();

		// Trabajador antiguo (Ana)
		Trabajador t1 = new Trabajador("11111111H");
		t1.setNombre("Ana");
		t1.setApellido("García");
		t1.setPuesto(Puesto.MONITOR);
		t1.setTipoContrato("Indefinido");
		t1.setFechaContratacion(LocalDate.of(2010, 1, 15));

		// Trabajador nuevo (Luis)
		Trabajador t2 = new Trabajador("22222222J");
		t2.setNombre("Luis");
		t2.setApellido("Rodríguez");
		t2.setPuesto(Puesto.ENTRENADOR);
		t2.setTipoContrato("Temporal");
		t2.setFechaContratacion(LocalDate.of(2022, 11, 20));

		// Clientes
		Cliente c1 = new Cliente("33333333P");
		c1.setNombre("Roberto");
		c1.setApellido("Sanz");

		Cliente c2 = new Cliente("44444444D");
		c2.setNombre("Lucía");
		c2.setApellido("Vila");

		listaPersonas.add(t1);
		listaPersonas.add(t2);
		listaPersonas.add(c1);
		listaPersonas.add(c2);

		// 2. CREAR ACTIVIDADES
		ArrayList<Actividad> listaActividades = new ArrayList<>();

		// Actividad 1: Yoga (Precio 40.0)
		Actividad act1 = new Actividad("YOG-01", "YOGA", 40.0, LocalDate.now().plusDays(2));
		// IMPORTANTE: Inicializar el TreeMap de integrantes ya que en tu clase no se
		// hace en el constructor
		act1.setIntegrantesActividad(new java.util.TreeMap<>());

		// Inscribir a Roberto y Lucía en Yoga
		act1.getIntegrantesActividad().put(c1.getDni(), c1);
		act1.getIntegrantesActividad().put(c2.getDni(), c2);

		// Actualizar el mapa interno del cliente para calcularDinero()
		c1.getActividadesC().put(act1.getCod(), act1);
		c2.getActividadesC().put(act1.getCod(), act1);

		// Actividad 2: Zumba (Precio 25.0)
		Actividad act2 = new Actividad("ZUM-01", "ZUMBA", 25.0, LocalDate.now().plusDays(5));
		act2.setIntegrantesActividad(new java.util.TreeMap<>());

		// Inscribir solo a Lucía en Zumba
		act2.getIntegrantesActividad().put(c2.getDni(), c2);
		c2.getActividadesC().put(act2.getCod(), act2);

		listaActividades.add(act1);
		listaActividades.add(act2);

		// 3. GUARDAR EN FICHEROS
		try (ObjectOutputStream oosPer = new ObjectOutputStream(new FileOutputStream(fichPer));
				ObjectOutputStream oosAct = new ObjectOutputStream(new FileOutputStream(fichAct))) {

			for (Persona p : listaPersonas)
				oosPer.writeObject(p);
			for (Actividad a : listaActividades)
				oosAct.writeObject(a);

			System.out.println(">>> Datos de prueba cargados con éxito.");
		} catch (IOException e) {
			System.err.println("Error al escribir datos: " + e.getMessage());
		}
	}

	private static void bajaPersona(File fichPer, File fichAct) {
		String dni;
		try {
			dni = Util.validarDni("Introduce el DNI de la persona a eliminar:");
		} catch (ValidarDniException e) {
			System.out.println(e.getMessage());
			return;
		}

		List<Persona> listaPersonas = new ArrayList<>();
		cargarListaDeFich(fichPer, listaPersonas);

		boolean eliminadoPer = listaPersonas.removeIf(p -> p.getDni().equalsIgnoreCase(dni));

		if (eliminadoPer) {
			cargarFicheroConArray(listaPersonas, fichPer);
			System.out.println("Persona eliminada del registro general.");

			ArrayList<Actividad> listaActividades = new ArrayList<>();
			deFicheroAArrayList(fichAct, listaActividades);

			for (Actividad act : listaActividades) {
				if (act.getIntegrantesActividad() != null) {
					act.getIntegrantesActividad().remove(dni);
				}
			}

			try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichAct))) {
				for (Actividad act : listaActividades) {
					oos.writeObject(act);
				}
			} catch (IOException e) {
				System.err.println("Error al actualizar el fichero de actividades.");
			}

		} else {
			System.out.println("No se encontró ninguna persona con DNI: " + dni);
		}
	}

	private static void busquedaEspecial(File fichPer, File fichAct) {

		Map<String, Actividad> actividadesC;

		boolean encontrado = false;

		ArrayList<Persona> personas = new ArrayList<>();

		ArrayList<Actividad> actividades = new ArrayList<>();

		String cod = Util.introducirCadena("Introduce el codigo de la actividad").toUpperCase();

		cargarListaDeFich(fichPer, personas);

		deFicheroAArrayList(fichAct, actividades);

		boolean actividadExiste = false;

		for (Actividad a : actividades) {

			if (a.getCod().equalsIgnoreCase(cod)) {

				actividadExiste = true;
				break;
			}
		}
		if (!actividadExiste) {
			System.out.println("ERROR: No existe ninguna actividad con código: " + cod);
			return;

		}

		for (Persona p : personas) {

			if (p instanceof Cliente) {

				Cliente c = (Cliente) p;

				actividadesC = c.getActividadesC();

				if (actividadesC == null) {

					System.out.println("No hay nadie inscrito ");

					return;

				}

				if (actividadesC.containsKey(cod)) {

					encontrado = true;

					System.out.println(c.toString());

				}

			}
		}
		if (!encontrado) {
			System.out.println("No se ha encontrado ninguna actividad con ese codigo");
			return;
		}
	}

	private static void bajaActividad(File fichAct, File aux) {

		if (!fichAct.exists()) {

			System.out.println("No existe el fichero");

			return;

		}

		boolean encontrado = false;

		ArrayList<Actividad> actividades = new ArrayList<>();

		deFicheroAArrayList(fichAct, actividades);

		for (Actividad a : actividades) {

			System.out.println("Nombre:" + a.getNombre() + " COD:" + a.getCod());

		}

		if (aux.exists()) {

			aux.delete();

		}

		String cod = Util.introducirCadena("Introduce el codigo de la  actividad");

		for (Actividad a : actividades) {

			if (a.getCod().equals(cod)) {

				encontrado = true;

			}

		}

		if (!encontrado) {

			System.out.println("No se encontro esa actividad");

			return;

		}

		try (ObjectInputStream actividadIStream = new ObjectInputStream(new FileInputStream(fichAct))) {

			try {

				while (true) {

					Actividad a = (Actividad) actividadIStream.readObject();

					if (!a.getCod().equals(cod)) {

						boolean auxVacio = !aux.exists() || aux.length() == 0;

						if (auxVacio) {

							try (ObjectOutputStream auxOStream = new ObjectOutputStream(new FileOutputStream(aux))) {

								auxOStream.writeObject(a);

							} catch (Exception exception) {

								System.out.println("Error en la escritura del fichero actividad.dat");

							}

						} else {

							try (AñadirObjetoSinCabecera auxIStream = new AñadirObjetoSinCabecera(

									new FileOutputStream(aux, true))) {

								auxIStream.writeObject(a);

							} catch (IOException e) {

								e.printStackTrace();

								System.out.println("Error de escritura: " + e.getMessage());

							}

						}

					}

				}

			} catch (EOFException exception) {

			}

		} catch (Exception exception) {

			System.out.println("Error de lectura de fichero producto.dat");

			exception.printStackTrace();

		}

		if (aux.length() == 0) {

			System.out.println("Se eliminó la ÚLTIMA actividad. Borrando archivo completo...");

			if (fichAct.delete()) {

				System.out.println("Archivo 'actividades.dat' eliminado completamente");

			} else {

				System.out.println("Error: No se pudo eliminar el archivo original");

			}

			aux.delete();

			return;

		}

		File dat = new File("actividades.dat");

		File backup = new File("actividades.backup");

		File auxx = aux;

		if (dat.renameTo(backup)) {

			if (!auxx.renameTo(dat)) {

				System.out.println("Error: no se pudo renombrar aux como dat, se intenta renombrar backup como dat");

				if (!backup.renameTo(dat)) {

					System.out.println("Error: no se pudo renombrar backup como dat");

				}

			} else {

				try {

					Files.deleteIfExists(Paths.get("actividades.backup"));

					System.out.println("Fichero borrado");

				} catch (IOException e) {

					System.out.println("Error al borrar el fichero de backup: " + e.getMessage());

				}

			}

		} else {

			System.out.println("Error: no se pudo crear el backup");

		}

		if (backup.exists()) {

			backup.delete();

		}

	}

	private static void modificacion(File fichPer, File fichAct) {
		int opc;
		do {
			opc = Util.leerInt(
					"1- Modificar cargo del trabajador\n2- Modificar fecha de la actividad\n3- Salir\nElige una opcion: ");
			switch (opc) {
			case 1:
				modificarCargo(fichPer);
				break;
			case 2:
				modificarActividad(fichAct);
				break;
			case 3:
				System.out.println("Has salido. ");
				break;
			default:
				System.err.println("Opcion no valida!");
				break;
			}
		} while (opc != 3);

	}

	private static void modificarActividad(File fichAct) {
		File fichAux = new File("ficharoAux.obj");
		String cod = Util.introducirCadena("Codigo de la actividad a modificar: ");
		boolean encontrado = false;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichAct));
				ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fichAux))) {
			while (true) {
				try {
					Actividad act = (Actividad) ois.readObject();
					if (act.getCod().equalsIgnoreCase(cod)) {
						encontrado = true;
						System.out.println("Actividad encontrado: " + act);
						int opcion = Util.leerInt("¿Desea modificar la fecha de la actividad? (1-Sí / 2-No): ");
						if (opcion == 1) {
							LocalDate fechaNueva = Util.pidoFechaDMA("Introduce nueva fecha: ");
							act.setFecha(fechaNueva);
							System.out.println("Fecha actualizada correctamente.");
						} else {
							System.out.println("Operación cancelada. No se han hecho cambios.");
						}
					}
					oos.writeObject(act);
				} catch (EOFException e) {
					break;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		}
		if (encontrado) {
			if (fichAct.delete()) {
				fichAux.renameTo(fichAct);
				System.out.println("Cambios guardados en el sistema.");
			}
		} else {
			System.out.println("No se encontró el registro. No hubo cambios.");
			fichAux.delete();
		}
	}

	private static void modificarCargo(File fichPer) {
		ArrayList<Persona> personas = new ArrayList<>();
		cargarListaDeFich(fichPer, personas);

		if (personas.isEmpty()) {
			System.out.println("El fichero no existe o está vacío.");
			return;
		}
		String dni = Util.introducirCadena("Introduce el dni del trabajador que deseas modificar: ");
		boolean encontrado = false, correcto = false;
		Puesto puesto = null;

		for (Persona p : personas) {
			if (p instanceof Trabajador) {
				Trabajador t = (Trabajador) p;
				if (t.getDni().equalsIgnoreCase(dni)) {
					encontrado = true;
					System.out.println("Empleado encontrado: " + t.toString());
					int opcion = Util.leerInt("¿Desea modificar el cargo del trabajador? (1-Sí / 2-No): ");
					if (opcion == 1) {
						do {
							try {
								puesto = Puesto.valueOf(Util
										.introducirCadena(
												"Puesto del trabajador (MONITOR, ENTRENADOR, FISIO, SOCORRISTA): ")
										.toUpperCase());
								correcto = true;
							} catch (IllegalArgumentException e) {
								correcto = false;
								e.printStackTrace();
							}
						} while (correcto == false);
						t.setPuesto(puesto);
						cargarFicheroConArray(personas, fichPer);

						System.out.println("Departamento modificado y guardado correctamente.");
					} else {
						System.out.println("Operación cancelada. No se han hecho cambios.");

					}
				}
			}
		}
	}

	private static void estadisticas(File fichAct, File fichPer) {
		if (!fichPer.exists() || !fichAct.exists()) {
			System.out.println("Faltan ficheros para calcular estadísticas.");
			return;
		}

		ArrayList<Cliente> clientes = new ArrayList<>();
		Map<String, Actividad> mapaActividades = new java.util.HashMap<>();

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichPer))) {
			while (true) {
				try {
					Persona p = (Persona) ois.readObject();
					if (p instanceof Cliente c) {
						clientes.add(c);
					}
				} catch (EOFException e) {
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error leyendo personas: " + e.getMessage());
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichAct))) {
			while (true) {
				try {
					Actividad a = (Actividad) ois.readObject();
					mapaActividades.put(a.getCod(), a);
				} catch (EOFException e) {
					break;
				}
			}
		} catch (Exception e) {
			System.err.println("Error leyendo actividades: " + e.getMessage());
		}

		System.out.println("\n---------- INFORME DE ESTADÍSTICAS ----------");

		if (mapaActividades.isEmpty()) {
			System.out.println("No hay actividades registradas.");
		} else {
			System.out.println("LISTADO DE ACTIVIDADES DISPONIBLES:");
			System.out.println("--------------------------------------------");
			for (Actividad a : mapaActividades.values()) {
				System.out.printf("- [%s] %-10s | Precio: %.2f€ | Inscritos: %d\n", a.getCod(), a.getNombre(),
						a.getPrecio(), a.getNumeroIntegrantes());
			}
			System.out.println("--------------------------------------------");
		}

		if (clientes.isEmpty()) {
			System.out.println("No hay clientes registrados.");
		} else {
			double sumaCuotas = 0;
			for (Cliente c : clientes) {
				sumaCuotas += c.calcularDinero();
			}
			System.out.printf("CUOTA MEDIA POR CLIENTE: %.2f €\n", sumaCuotas / clientes.size());
		}

		if (!mapaActividades.isEmpty()) {
			int max = 0;
			for (Actividad a : mapaActividades.values()) {
				if (a.getNumeroIntegrantes() > max) {
					max = a.getNumeroIntegrantes();
				}
			}

			System.out.println("ACTIVIDAD(ES) CON MÁS CLIENTES (" + max + "):");
			for (Actividad a : mapaActividades.values()) {
				if (a.getNumeroIntegrantes() == max) {
					System.out.println("   -> " + a.getNombre() + " (" + a.getCod() + ")");
				}
			}
		}
		System.out.println("--------------------------------------------\n");
	}

	private static void listadoAntiguedad(File fichPer) {
		List<Persona> personas = new ArrayList<>();
		ArrayList<Trabajador> trabajadores = new ArrayList<>();
		if (!fichPer.exists()) {
			System.out.println("No has introducido ninguna persona");
			return;
		}
		cargarListaDeFich(fichPer, personas);
		for (Persona per : personas) {
			if (per instanceof Trabajador) {
				Trabajador t = (Trabajador) per;
				trabajadores.add(t);
			}
		}
		for (int i = 0; i < trabajadores.size(); i++) {
			for (int j = 0; j < trabajadores.size() - 1; j++) {
				if (trabajadores.get(j).getFechaContratacion().getYear() > trabajadores.get(j + 1)
						.getFechaContratacion().getYear()) {
					Trabajador temp = trabajadores.get(j);
					trabajadores.set(j, trabajadores.get(j + 1));
					trabajadores.set(j + 1, temp);
				}
			}
		}
		for (Trabajador t : trabajadores) {
			System.out
					.println("DNI:" + t.getDni() + "- " + t.getNombre() + "- " + t.getApellido() + "- " + t.toString());
		}
	}

	private static void listadoActividades(File fichAct) {
		LocalDate fechaInicio = Util.pidoFechaDMA("Introduce la fecha de Inicio: ");
		LocalDate fechaFin = Util.pidoFechaDMA("Introduce la fecha de Fin: ");
		if (!fichAct.exists()) {
			System.out.println("Fichero no existente");
			return;
		}
		boolean hay = false;
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichAct))) {
			while (true) {
				Actividad a = (Actividad) ois.readObject();
				if (!a.getFecha().isBefore(fechaInicio) && !a.getFecha().isAfter(fechaFin)) {
					System.out.println(a);
					hay = true;
				}
			}
		} catch (EOFException e) {
			if (!hay) {
				System.out.println("No hay actividades en ese rango de fechas.");
			}
		} catch (Exception e) {
			e.getMessage();
		}
	}

	private static void inscripcionActividad(File fichPer, File fichAct) {
		String dni = null;
		do {
			try {
				dni = Util.validarDni("Introduce el dni de la persona a inscribir: ");
			} catch (ValidarDniException e) {
				dni = null;
				System.out.println(e.getMessage());
			}
		} while (dni == null);
		Persona p = obtenerPersona(fichPer, dni);
		if (p == null) {
			System.out.println("No existe ninguna persona con ese dni.");
			return;
		}
		if (!(p instanceof Cliente)) {
			System.out.println("Error: Esta persona es un Trabajador y no tiene historial de inscripciones.");
			return;
		}
		Cliente clienteActual = (Cliente) p;
		System.out.println("Persona encontrada: " + clienteActual.getNombre());
		if (clienteActual.getActividadesC().isEmpty()) {
			System.out.println("El cliente no está inscrito en ninguna actividad actualmente.");
		} else {
			System.out.println("Actividades actuales del cliente:");
			for (Actividad act : clienteActual.getActividadesC().values()) {
				System.out.println("- [" + act.getCod() + "] " + act.getNombre());
			}
		}
		System.out.println("--------------------------------------");
		String codAct = Util.introducirCadena("Introduce el código de la actividad a inscribir: ").toUpperCase();
		Actividad a = obtenerActividad(fichAct, codAct);
		if (a == null) {
			System.out.println("No existe ninguna actividad con ese código.");
			return;
		}
		boolean inscrito = Util.leerRespuesta("¿Inscribir en " + a.getNombre() + "? (S/N)");
		if (inscrito) {
			List<Persona> listaPersonas = new ArrayList<>();
			cargarListaDeFich(fichPer, listaPersonas);
			for (Persona persona : listaPersonas) {
				if (persona.getDni().equalsIgnoreCase(dni)) {
					Cliente c = (Cliente) persona;
					c.getActividadesC().put(a.getCod(), a);
					break;
				}
			}
			ArrayList<Actividad> actividadesExistentes = new ArrayList<>();
			deFicheroAArrayList(fichAct, actividadesExistentes);
			for (Actividad actividad : actividadesExistentes)
				if (actividad.getCod().equalsIgnoreCase(codAct)) {
					actividad.getIntegrantesActividad().put(codAct, clienteActual);
					break;
				}
			cargarFicheroConArray1(actividadesExistentes, fichAct);
			cargarFicheroConArray(listaPersonas, fichPer);
			System.out.println("Inscripción realizada con éxito.");
		}

	}

	private static void cargarFicheroConArray1(ArrayList<Actividad> actividadesExistentes, File fichAct) {

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fichAct));
			for (Actividad a : actividadesExistentes) {
				oos.writeObject(a);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void altaActividad(File fichAct, File fichPer) {
		ArrayList<Actividad> actividadesExistentes = new ArrayList<>();
		deFicheroAArrayList(fichAct, actividadesExistentes);

		if (!verificarMonitoresDisponibles(fichPer)) {
			System.out.println("No se tiene guardado ningun monitor ni entrenador.");
			return;
		}

		ArrayList<Actividad> nuevasActividades = new ArrayList<>();
		int rep;
		do {
			String nom;
			do {
				nom = Util.introducirCadena("Introduce el nombre de la actividad:");
				if (nom.length() < 4) {
					System.out.println("El nombre tiene que tener 4 letras como minimo.");
				}
			} while (nom.length() < 4);

			ArrayList<Actividad> todas = new ArrayList<>(actividadesExistentes);
			todas.addAll(nuevasActividades);

			String cod = gencod(nom.substring(0, 3), todas);
			double prec = Util.leerFloat("Introduce el precio:");

			LocalDate fecha;
			do {
				fecha = Util.pidoFechaDMA("Introduce la fecha:");
				if (fecha.isBefore(LocalDate.now())) {
					System.out.println("La fecha no puede ser anterior a hoy.");
				}
			} while (fecha.isBefore(LocalDate.now()));

			Actividad a = new Actividad(cod, nom, prec, fecha);
			a.setIntegrantesActividad(new java.util.TreeMap<>());
			nuevasActividades.add(a);

			rep = Util.leerInt("¿Introducir otra actividad? (1) SI / (2) NO");
		} while (rep == 1);

		boolean existe = fichAct.exists();
		try (ObjectOutputStream oos = existe ? new AñadirObjetoSinCabecera(new FileOutputStream(fichAct, true))
				: new ObjectOutputStream(new FileOutputStream(fichAct))) {

			for (Actividad act : nuevasActividades) {
				oos.writeObject(act);
			}
			System.out.println("Actividades guardadas.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static boolean verificarMonitoresDisponibles(File fichPer) {
		if (!fichPer.exists()) {
			return false;
		}

		try (ObjectInputStream leerFich = new ObjectInputStream(new FileInputStream(fichPer))) {
			while (true) {
				try {
					Persona p = (Persona) leerFich.readObject();
					if (p instanceof Trabajador) {
						Trabajador t = (Trabajador) p;
						if (t.getPuesto() == Puesto.ENTRENADOR || t.getPuesto() == Puesto.MONITOR) {
							return true;
						}
					}
				} catch (EOFException e) {
					break;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	private static String gencod(String codBuscar, ArrayList<Actividad> actividades) {
		String codAux = codBuscar.substring(0, 3);
		String aux;
		int temp = 0;
		int numMax = 0;
		for (Actividad a : actividades)
			if (a.getCod().substring(0, 3).equalsIgnoreCase(codAux)) {
				aux = a.getCod().substring(a.getCod().length() - 2);
				temp = Integer.parseInt(aux);
				if (numMax < temp) {
					numMax = temp;
				}
			}

		numMax++;
		String cod = codAux + "-" + String.format("%02d", numMax);
		return cod;
	}

	private static void deFicheroAArrayList(File fichAct, ArrayList<Actividad> actividades) {
		if (fichAct.exists()) {
			ObjectInputStream leerFichero = null;
			Actividad a;
			try {
				leerFichero = new ObjectInputStream(new FileInputStream(fichAct));
				while (true) {
					a = (Actividad) leerFichero.readObject();
					actividades.add(a);
				}
			} catch (EOFException e) {
			} catch (Exception e) {
				e.printStackTrace();
			} finally {
				try {
					leerFichero.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}
	}

	private static void altaPersona(File fichPer) {
		ObjectOutputStream oos = null;
		String dni;
		Persona p;
		int mas;
		try {
			if (fichPer.exists()) {
				oos = new AñadirObjetoSinCabecera(new FileOutputStream(fichPer, true));
			} else {
				oos = new ObjectOutputStream(new FileOutputStream(fichPer));
			}
			do {
				do {
					try {
						dni = Util.validarDni("Introduce el dni de la persona: ");
					} catch (ValidarDniException e) {
						dni = null;
						System.out.println(e.getMessage());
					}
				} while (dni == null);
				p = obtenerPersona(fichPer, dni);
				if (p != null) {
					System.out.println("Ya existe una persona con ese dni");
				} else {
					int opt = Util.leerInt("Elige entre Cliente (1) o Trabajador (2):");
					if (opt == 1) {
						System.out.println("AÑADIENDO CLIENTE NUEVO");
						p = new Cliente(dni);
						p.setDatos(dni);
						oos.writeObject(p);
					} else {
						System.out.println("AÑADIENDO TRABAJADOR NUEVO");
						p = new Trabajador(dni);
						p.setDatos(dni);
						oos.writeObject(p);
					}
				}
				mas = Util.leerInt("Quieres añadir una persona mas? (1=Si/2=No)");
			} while (mas == 1);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void cargarListaDeFich(File fichPer, List<Persona> personas) {
		personas.clear();
		ObjectInputStream ois = null;
		Persona p;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichPer));
			while (true) {
				p = (Persona) ois.readObject();
				personas.add(p);
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static void cargarFicheroConArray(List<Persona> personas, File fich) {

		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(fich));
			for (Persona p : personas) {
				oos.writeObject(p);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if (oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private static Persona obtenerPersona(File fEmpleados, String dni) {
		ObjectInputStream ois = null;
		Persona emp;
		try {
			ois = new ObjectInputStream(new FileInputStream(fEmpleados));
			while (true) {
				emp = (Persona) ois.readObject();
				if (emp.getDni().equalsIgnoreCase(dni)) {
					return emp;
				}
			}
		} catch (EOFException e) {
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	private static Actividad obtenerActividad(File fichAct, String codAct) {
		ObjectInputStream ois = null;
		Actividad act;
		try {
			ois = new ObjectInputStream(new FileInputStream(fichAct));
			while (true) {
				act = (Actividad) ois.readObject();
				if (act.getCod().equalsIgnoreCase(codAct)) {
					return act;
				}
			}
		} catch (EOFException e) {
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

}