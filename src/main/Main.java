package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clases.Actividad;
import clases.Cliente;
import clases.Persona;
import clases.Sala;
import clases.Trabajador;
import utilidades.AñadirObjetoSinCabecera;
import utilidades.Util;

public class Main {

	public static void main(String[] args) {
		File fichPer = new File("personas.dat");
		File fichAct = new File("actividades.dat");
		File fichSal = new File("salas.obj");

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
				altaActividad(fichAct);
				break;
			case 3:
				inscripcionActividad(fichPer, fichAct);
				break;
			case 4:
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				break;
			case 11:
				System.out.println("Saliendo del programa.");
				break;
			default:
				break;
			}
		} while (opc != 11);

	}

	private static void inscripcionActividad(File fichPer, File fichAct) {
		String dni = Util.introducirCadena("Introduce el dni de la persona a inscribir: ");
		Persona p = obtenerPersona(fichPer, dni);
		if (p == null) {
			System.out.println("No existe ninguna persona con ese dni.");
		} else {
			System.out.println("Persona encontrada: " + p.getNombre());
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

	private static void altaActividad(File fichAct) {
		
		ArrayList<Actividad> actividades = new ArrayList<>();
		deFicheroAArrayList(fichAct, actividades);
		
		int rep;
		boolean repetir = false;
		do {
			String nom = Util.introducirCadena("Introduce el nombre de la actividad:");
			String codBuscar = nom.substring(0,3);
			String cod = gencod(codBuscar, actividades);
			double prec = Util.leerFloat("Introduce el precio de la actividad");
			LocalDate fecha = Util.pidoFechaDMA("Introduce la fecha de la actividad");
			
			Actividad a = new Actividad(cod,nom,prec,fecha);
			actividades.add(a);			
			
			rep = Util.leerInt("¿Introducir una nueva actividad (1) SI / (2) NO)?");
			if (rep == 1) {
				repetir = true;
			}
		}while(repetir);
		
		if (fichAct.exists()) {
			try (AñadirObjetoSinCabecera introAct = new AñadirObjetoSinCabecera(new FileOutputStream(fichAct))){
				while(true) {
					introAct.writeObject(actividades);
				}
			} catch(Exception e) {
				e.getMessage();
			}
		} else {
			try (ObjectOutputStream introAct = new ObjectOutputStream(new FileOutputStream(fichAct))){
				while(true) {
					introAct.writeObject(actividades);
				}
			}catch(Exception e) {
			
			}
		}
	}
	
	private static String gencod(String codBuscar, ArrayList<Actividad> actividades) {
		String codAux = codBuscar.substring(0,3);
		String aux;
		int temp = 0;
		int numMax = 0;
		for (Actividad a:actividades)
			if(a.getCod().substring(0,3).equalsIgnoreCase(codAux)) {
				aux = a.getCod().substring(a.getCod().length()-2);
				temp = Integer.parseInt(aux);
				if (numMax < temp) {
					numMax = temp;
				}
			} 
		
		numMax++;
		String cod = codAux +"-"+ String.format("%02d", numMax);
		return cod;
	}
	
	private static void deFicheroAArrayList(File fichAct, ArrayList<Actividad> actividades) {
		if(fichAct.exists()) {
			ObjectInputStream leerFichero = null;
			Actividad a;
			try {
				leerFichero = new ObjectInputStream(new FileInputStream(fichAct));
				while(true) {
					a =  (Actividad) leerFichero.readObject();
					actividades.add(a);
				}
			}catch(EOFException e) {
			}catch(Exception e) {
				e.printStackTrace();
			} finally {
				try {
					leerFichero.close();
				}catch(IOException e) {
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
				dni = Util.introducirCadena("Introduce el dni de la persona: ");
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

	private static void cargarListaDeFich(File ficheroEq, List<Persona> personas) {
		ObjectInputStream ois = null;
		Persona emp;
		try {
			ois = new ObjectInputStream(new FileInputStream(ficheroEq));
			while (true) {
				emp = (Persona) ois.readObject();
				personas.add(emp);
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
			for (Persona emp : personas) {
				oos.writeObject(emp);
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

package main;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import clases.Actividad;
import clases.Cliente;
import clases.Persona;
import clases.Sala;
import clases.Trabajador;
import utilidades.AñadirObjetoSinCabecera;
import utilidades.Util;

public class Main {

	public static void main(String[] args) {
		File fichPer = new File("personas.dat");
		File fichAct = new File("actividades.dat");
		File fichSal = new File("salas.obj");

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
				altaActividad(fichAct);
				break;
			case 3:
				break;
			case 4:
				listadoActividades(fichAct);
				break;
			case 5:
				break;
			case 6:
				break;
			case 7:
				estadisticas(fichAct, fichPer);
				break;
			case 8:
				break;
			case 9:
				break;
			case 10:
				break;
			case 11:
				System.out.println("Saliendo del programa.");
				break;
			default:
				break;
			}
		} while (opc != 11);

	}

	private static void altaActividad(File fichAct) {

		String nom = Util.introducirCadena("Introduce el nombre de la actividad");
		if (fichAct.exists()) {

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
				dni = Util.introducirCadena("Introduce el dni de la persona: ");
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

	private static void cargarListaDeFich(File ficheroEq, List<Persona> personas) {

		ObjectInputStream ois = null;
		Persona emp;
		try {
			ois = new ObjectInputStream(new FileInputStream(ficheroEq));
			while (true) {
				emp = (Persona) ois.readObject();
				personas.add(emp);
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
			for (Persona emp : personas) {
				oos.writeObject(emp);
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

	private static void listadoActividades(File fichAct) {

		LocalDate fechaInicio = Util.pidoFechaDMA("Introduce la fecha de Inicio (DD-MM-AAAA): ");

		LocalDate fechaFin = Util.pidoFechaDMA("Introduce la fecha de Fin (DD-MM-AAAA): ");

		if (!fichAct.exists()) {
			System.out.println("Fichero no existente");
			return;
		}

		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(fichAct))) {

			ArrayList<Actividad> actividades = (ArrayList<Actividad>) ois.readObject();

			boolean hay = false;

			for (Actividad a : actividades) {
				LocalDate fechaAct = a.getFecha();

				if (!fechaAct.isBefore(fechaInicio) && !fechaAct.isAfter(fechaFin)) {

					System.out.println(a);
					hay = true;
				}
			}

			if (!hay) {
				System.out.println("No hay actividades en ese rango de fechas.");
			}

		} catch (Exception e) {
			System.out.println("Error de lectura: " + e.getMessage());
		}
	}
	
	private static void estadisticas(File fichAct, File fichPer) {
		
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
