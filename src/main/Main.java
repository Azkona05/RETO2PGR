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
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import clases.Actividad;
import clases.Cliente;
import clases.Persona;
import clases.Puesto;
import clases.Sala;
import clases.Trabajador;
import exception.ValidarDniException;
import utilidades.AñadirObjetoSinCabecera;
import utilidades.Util;

public class Main {

	public static void main(String[] args) {
		File fichPer = new File("personas.dat");
		File fichAct = new File("actividades.dat");
		File fichSal = new File("salas.txt");

		cargarSalas(fichSal);

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

	private static void bajaPersona(File fichPer, File fichAct) {
		String dni;
		boolean personaEncontrada = false;
		do {
			try {
				dni = Util.validarDni("Introduce el DNI del cliente a eliminar:");
			} catch (ValidarDniException e) {
				dni = null;
				e.printStackTrace();
			}
		} while (dni == null);

		try (ObjectInputStream leerFich = new ObjectInputStream(new FileInputStream(fichAct))) {
			while (true) {
				try {
					Actividad a = (Actividad) leerFich.readObject();
					if (a.getIntegrantesActividad() != null) {
						Iterator<Map.Entry<String, Persona>> iterator = a.getIntegrantesActividad().entrySet()
								.iterator();
						while (iterator.hasNext()) {
							Map.Entry<String, Persona> entry = iterator.next();

							if (entry.getKey().equals(dni)) {
								System.out.println("Persona encontrada: " + entry.getValue());
								System.out.println("En la actividad: " + a.getNombre());

								iterator.remove();
								personaEncontrada = true;
								break;
							}
						}
					}
				} catch (EOFException eof) {
					break;
				}
			}
			if (personaEncontrada) {
				System.out.println("Persona con DNI " + dni + " eliminada correctamente.");
			} else {
				System.out.println("No se encontró ninguna persona con DNI: " + dni);
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private static void busquedaEspecial(File fichPer, File fichAct) {
		boolean encontrado = false;
		ArrayList<Persona> personas = new ArrayList<>();
		String cod = Util.introducirCadena("Introduce el codigo de la actividad");
		cargarListaDeFich(fichPer, personas);
		for (int i = 0; i < personas.size(); i++) {
			if (personas.get(i) instanceof Cliente) {
				Cliente c = (Cliente) personas.get(i);
				if (c.getActividadesC().containsValue(cod)) {
					encontrado = true;
				}
			}
		}
		if (!encontrado) {
			System.out.println("No se ha encontrado ninguna actividad con ese codigo");
			return;
		} else {
			for (Persona p : personas) {
				if (p instanceof Cliente) {
					Cliente c = (Cliente) p;
					if (c.getActividadesC().containsValue(cod)) {
						System.out.println(c);
					}
				}
			}
		}
	}

	private static void bajaActividad(File fichAct, File aux) {
		if (!fichAct.exists()) {
			System.out.println("No existe el fichero");
		}
		if (aux.exists()) {
			aux.delete();
		}
		String cod = Util.introducirCadena("Introduce el codigo de la  actividad");
		try (ObjectInputStream personaIStream = new ObjectInputStream(new FileInputStream(fichAct))) {
			try {
				while (true) {
					Persona persona = (Persona) personaIStream.readObject();
					if (persona instanceof Cliente) {
						Cliente c = (Cliente) persona;
						Map<String, Actividad> activity = c.getActividadesC();
						Iterator<Map.Entry<String, Actividad>> it = activity.entrySet().iterator();
						while (it.hasNext()) {
							Map.Entry<String, Actividad> entry = it.next();
							String key = entry.getKey();
							Actividad t = entry.getValue();
							if (!t.getCod().equals(cod)) {
								boolean auxVacio = !aux.exists() || aux.length() == 0;
								if (auxVacio) {
									try (ObjectOutputStream auxOStream = new ObjectOutputStream(
											new FileOutputStream(aux))) {

										auxOStream.writeObject(activity);

									} catch (Exception exception) {
										System.out.println("Error en la escritura del fichero actividad.dat");
									}
								} else {
									try (AñadirObjetoSinCabecera auxIStream = new AñadirObjetoSinCabecera(
											new FileOutputStream(aux, true))) {

										auxIStream.writeObject(activity);

									} catch (IOException e) {
										e.printStackTrace();
										System.out.println("Error de escritura: " + e.getMessage());

									}
								}
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
		ArrayList<Actividad> actividades = new ArrayList<>();
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichPer))) {
			while (true) {
				Persona p = (Persona) ois.readObject();
				if (p instanceof Cliente c) {
					clientes.add(c);
				}
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.getMessage();
			return;
		}
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichAct))) {
			while (true) {
				Actividad a = (Actividad) ois.readObject();
				actividades.add(a);
			}
		} catch (EOFException e) {
		} catch (Exception e) {
			e.getMessage();
			return;
		}
		if (clientes.isEmpty()) {
			System.out.println("No hay clientes registrados.");
		} else {
			double sumaCuotas = 0;
			for (Cliente c : clientes) {
				sumaCuotas += c.calcularDinero();
			}
			System.out.printf("Cuota media de los clientes: %.2f €%n", sumaCuotas / clientes.size());
		}
		if (actividades.isEmpty()) {
			System.out.println("No hay actividades registradas.");
			return;
		}
		int max = 0;
		for (Actividad a : actividades) {
			if (a.getNumeroIntegrantes() > max) {
				max = a.getNumeroIntegrantes();
			}
		}
		System.out.println("Actividades con más clientes (" + max + "):");
		for (Actividad a : actividades) {
			if (a.getNumeroIntegrantes() == max) {
				System.out.println(a);
			}
		}
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
		} else {
			System.out.println("Persona encontrada: " + p.getNombre());
		}
		if (!(p instanceof Cliente)) {
			System.out.println("Error: Esta persona es un Trabajador y no puede inscribirse en actividades.");
			return;
		}
		String codAct = Util.introducirCadena("Introduce el codigo de la actividad: ");
		Actividad a = obtenerActividad(fichAct, codAct);
		if (a == null) {
			System.out.println("No existe ninguna actividad con ese codigo.");
		} else {
			System.out.println("Actividad encontrada: " + a.getNombre());
		}
		boolean inscrito = Util.leerRespuesta(
				"Quieres inscribir a " + p.getNombre() + " en la actividad " + a.getNombre() + "? (S/N)");
		if (inscrito == true) {
			List<Persona> listaPersonas = new ArrayList<>();
			cargarListaDeFich(fichPer, listaPersonas);
			for (Persona persona : listaPersonas) {
				if (persona.getDni().equalsIgnoreCase(dni)) {
					Cliente cliente = (Cliente) persona;
					cliente.getActividadesC().put(a.getCod(), a);
					break;
				}
			}
			cargarFicheroConArray(listaPersonas, fichPer);
			System.out.println("Actividad añadida al mapa del cliente correctamente.");

		} else {
			System.out.println("Inscripción cancelada.");
		}
	}

	private static void altaActividad(File fichAct, File fichPer) {

		ArrayList<Actividad> actividades = new ArrayList<>();
		deFicheroAArrayList(fichAct, actividades);

		boolean hayMonitorOEntrenador = verificarMonitoresDisponibles(fichPer);

		if (!hayMonitorOEntrenador) {
			System.out.println("No se tiene guardado ningun monitor ni entrenador");
			return;
		}

		boolean seguir = false;
		int rep;
		do {
			String nom = Util.introducirCadena("Introduce el nombre de la actividad:");

			String codBuscar = nom.substring(0, 3);
			String cod = gencod(codBuscar, actividades);

			double prec = Util.leerFloat("Introduce el precio de la actividad");
			LocalDate fecha = Util.pidoFechaDMA("Introduce la fecha de la actividad");

			Actividad a = new Actividad(cod, nom, prec, fecha);
			actividades.add(a);

			rep = Util.leerInt("¿Introducir una nueva actividad (1) SI / (2) NO)?");
			seguir = (rep == 1);

		} while (seguir);

		do {
			String nom = Util.introducirCadena("Introduce el nombre de la actividad:");
			String codBuscar = nom.substring(0, 3);
			String cod = gencod(codBuscar, actividades);
			double prec = Util.leerFloat("Introduce el precio de la actividad");
			LocalDate fecha = Util.pidoFechaDMA("Introduce la fecha de la actividad");

			Actividad a = new Actividad(cod, nom, prec, fecha);
			actividades.add(a);

			rep = Util.leerInt("¿Introducir una nueva actividad (1) SI / (2) NO)?");
			if (rep == 1) {
				seguir = true;
			} else {
				seguir = false;
				break;
			}
		} while (seguir);

		if (fichAct.exists()) {
			try (AñadirObjetoSinCabecera introAct = new AñadirObjetoSinCabecera(new FileOutputStream(fichAct, true))) {
				for (Actividad act : actividades) {
					introAct.writeObject(act);
				}
				System.out.println(actividades.size() + " actividad(es) añadida(s) correctamente.");
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
			try (ObjectOutputStream introAct = new ObjectOutputStream(new FileOutputStream(fichAct))) {
				for (Actividad act : actividades) {
					introAct.writeObject(act);
				}
				System.out.println(actividades.size() + " actividad(es) añadida(s) correctamente.");
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	private static void cargarSalas(File fichSal) {
		ArrayList<Sala> salas = new ArrayList<>();

		Sala s;
		try (ObjectOutputStream datosSalas = new ObjectOutputStream(new FileOutputStream(fichSal));) {
			s = new Sala("FITNESS1", 30);
			salas.add(s);
			s = new Sala("FITNESS2", 20);
			salas.add(s);
			s = new Sala("PISCINA1", 25);
			salas.add(s);
			s = new Sala("PISCINA2", 25);
			salas.add(s);
			s = new Sala("PISTA1", 2);
			salas.add(s);
			s = new Sala("PISTA2", 4);
			salas.add(s);
			s = new Sala("ZONA1", 40);
			salas.add(s);
			s = new Sala("FISIO1", 2);
			salas.add(s);
			s = new Sala("FISIO2", 2);
			salas.add(s);
			s = new Sala("PISTA1", 25);
			salas.add(s);
			s = new Sala("CANCHA1", 50);
			salas.add(s);
			s = new Sala("CANCHA2", 50);
			salas.add(s);
			s = new Sala("VESTUARIOM", 35);
			salas.add(s);
			s = new Sala("VESTUARIOF", 35);
			salas.add(s);
			s = new Sala("FRONTON", 10);
			salas.add(s);
			s = new Sala("PABELLON", 35);
			salas.add(s);

			datosSalas.writeObject(salas);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}
