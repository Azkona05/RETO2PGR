package utilidades;

import java.io.EOFException;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.NoSuchElementException;
import java.util.Scanner;

import exception.ValidarDniException;
import exception.ValidarEmailException;
import exception.ValidarOrigenVuelo;

//import exception.ValidarDniException;
//import exception.ValidarEmailException;

public class Util {
	// Utilidad para leer cadenas
	public static String introducirCadena(String mensaje) {
		Scanner sc = new Scanner(System.in);

		String cadena = "";
		System.out.println(mensaje);

		try {
			cadena = sc.next();
		} catch (NoSuchElementException er) {
			System.out.println("Error al introducir datos");
			System.exit(0);
		}
		// sc.close();
		return cadena;
	}

	// Utilidad para leer un real
	public static float leerFloat(String message) {
		float n = 0;
		boolean ok;

		do {
			try {
				ok = true;
				n = Float.parseFloat(introducirCadena(message));
			} catch (NumberFormatException e) {
				System.out.println("te equivocaste wey, intentalo otra vez: ");
				ok = false;
			}
		} while (!ok);

		return n;
	}

	// Utilidad para leer un entero
	public static int leerInt(String message) {
		int n = -1;
		boolean ok;

		do {
			try {
				ok = true;
				n = Integer.parseInt(introducirCadena(message));
			} catch (NumberFormatException e) {
				System.out.println("No has introducido un entero, intentalo otra vez: ");
				ok = false;
			}
		} while (!ok);

		return n;
	}

	// Leer un string
	public static String leerString(int x, String message) {
		String cadena = null;
		boolean ok;
		do {
			ok = true;
			cadena = introducirCadena(message);
			if (cadena.length() > x) {
				System.out.println("Error al introducir datos. ");
				ok = false;
			}
		} while (!ok);
		return cadena;
	}

	// Leer una respuesta
	public static boolean leerRespuesta(String message) {
		String respu;
		do {
			respu = introducirCadena(message).toLowerCase();
		} while (!respu.equals("0") && !respu.equals("1") && !respu.equals("si") && !respu.equals("no")
				&& !respu.equals("s") && !respu.equals("n") && !respu.equals("true") && !respu.equals("false"));
		if (respu.equals("1") || respu.equals("si") || respu.equals("s") || respu.equals("true")) {
			return true;
		} else {
			return false;
		}
	}

	// leer int entre un rango
	public static int leerInt(int x, int y, String message) {
		int num = 0;
		boolean ok;
		do {
			try {
				ok = true;
				num = Integer.parseInt(introducirCadena(message));

			} catch (NumberFormatException e) {
				System.out.println("Hay que introducir numeros");
				ok = false;
				num = x;

			}
			if (num < x || num > y) {
				System.out.println("Dato fuera de rango, introduce entre " + x + " y " + y);
				ok = false;
			}
		} while (!ok);
		return num;
	}

	// leer float entre un rango
	public static float leerFloat(float x, float y, String message) {
		float fNumero = 0;
		boolean ok;
		do {
			try {
				ok = true;
				fNumero = Float.parseFloat(introducirCadena(message));
			} catch (NumberFormatException e) {
				System.out.println("Hay que introducir numeros. Vuelve aintroducir: ");
				ok = false;
				fNumero = x;
			}
			if (fNumero < x || fNumero > y) {
				System.out.println("Dato fuera de rando. Introduce entre " + x + " y " + y);
				ok = false;
			}
		} while (!ok);
		return fNumero;
	}

	// leer caracter
	public static char leerChar(String message) {
		boolean error = false;
		String letra;

		do {
			error = false;
			letra = introducirCadena(message);
			if (letra.length() != 1) {
				System.out.println("Error, introduce un caracter: ");
				error = true;
			}

		} while (error);
		return letra.charAt(0);
	}

	// Pido fecha
	public static LocalDate pidoFechaDMA(String message) {
		String fechaS;
		boolean hay;
		LocalDate fecha = null;
		// parseador
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd");
		do {
			hay = true;
			fechaS = Util.introducirCadena(message + " en formato aaaa-MM-dd: ");
			try {
				fecha = LocalDate.parse(fechaS, formateador);
			} catch (DateTimeParseException p) {
				System.out.println("Error... formato de fecha introducido incorrecto.");
				hay = false;
			}
		} while (!hay);
		return fecha;
	}

	public static LocalDateTime pidoFechaConHora(String message) {
		String fechaS;
		boolean hay;
		LocalDateTime fecha = null;
		DateTimeFormatter formateador = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
		do {
			hay = true;
			fechaS = Util.introducirCadena(message + " en formato aaaa-MM-dd HH:mm: ");
			try {
				fecha = LocalDateTime.parse(fechaS, formateador);
			} catch (DateTimeParseException p) {
				System.out.println("Error... formato de fecha y hora introducido incorrecto.");
				hay = false;
			}
		} while (!hay);
		return fecha;
	}

	public static char leerCharArray(char caracteres[], String message) {
		int i;
		boolean error = false;
		String letra;
		char aux = 0;

		do {
			error = false;
			letra = introducirCadena(message);
			if (letra.length() != 1) {
				System.out.println("Error, introduce un caracter: ");
				error = true;
			} else {
				aux = letra.toUpperCase().charAt(0);
				for (i = 0; i < caracteres.length; i++) {
					if (Character.toUpperCase(caracteres[i]) == Character.toUpperCase(aux)) {
						break;
					}
				}
				if (i == caracteres.length) {
					error = true;
					System.out.println("Error, el caracter introducido no es valido. ");
				}
			}
		} while (error);
		return aux;
	}

	// Devuelve el n�mero de objetos de un fichero
	public static int calculoFichero(File fich) {
		int cont = 0;
		if (fich.exists()) {
			FileInputStream fis = null;
			ObjectInputStream ois = null;
			try {
				fis = new FileInputStream(fich);
				ois = new ObjectInputStream(fis);

				Object aux = ois.readObject();

				while (aux != null) {
					cont++;
					aux = ois.readObject();
				}
			} catch (EOFException e1) {
				System.out.println("Has acabado de leer, tienes " + cont + " objetos");
			} catch (Exception e2) {
				e2.printStackTrace();
			}
			try {
				ois.close();
				fis.close();
			} catch (IOException e) {
				System.out.println("Error al cerrar los flujos");
			}
		}
		return cont;
	}

	public static String validarEmail(String mensaje) {
		String PATRON_EMAIL = "^[\\w._%+-]+@[\\w.-]+\\.[a-zA-Z]{2,6}$";
		String email = null;
		boolean error = true;

		do {
			error = false;
			try {
				email = Util.introducirCadena(mensaje);
				if (!email.matches(PATRON_EMAIL)) {
					throw new ValidarEmailException(email);
				}
			} catch (ValidarEmailException e) {
				System.out.println("Error con el email, introducelo de nuevo! ");
				error = true;
			}
		} while (error);
		return email;
	}

	public static String validarDni(String mensaje) throws ValidarDniException {
		String dni, numeroStr;
		char letra, letraCalculada;
		int numero;

		dni = Util.introducirCadena("Introduce el dni:");

		if (!dni.matches("^[0-9]{8}[A-Z]$")) {
			throw new ValidarDniException("Error: El DNI debe tener 8 dígitos seguidos de una letra.");
		}

		numeroStr = dni.substring(0, 8);
		letra = dni.charAt(8);
		numero = Integer.parseInt(numeroStr);
		letraCalculada = calcularLetraDni(numero);

		if (letra != letraCalculada) {
			throw new ValidarDniException("Error: La letra del DNI no es correcta.");
		}

		return dni;
	}

	private static char calcularLetraDni(int numero) {
		char[] letras = { 'T', 'R', 'W', 'A', 'G', 'M', 'Y', 'F', 'P', 'D', 'X', 'B', 'N', 'J', 'Z', 'S', 'Q', 'V', 'H',
				'L', 'C', 'K', 'E', 'T' };
		int indice = numero % 23;
		return letras[indice];
	}

	public static String validarOrigen(String mensaje) throws ValidarOrigenVuelo {
		String origen;

		origen = Util.introducirCadena("Introduce el origen:");

		if (!origen.matches(("[a-zA-Z]{5,10}"))) {
			throw new ValidarOrigenVuelo("Error: El origen debe tener entre 5 y 10 caracteres.");
		}
		return origen;
	}
}