package utilidades;
import java.io.*;
public class AñadirObjetoSinCabecera extends ObjectOutputStream{
	
		//Sobrescribimos el método que crea la cabecera 
		protected void writeStreamHeader() throws IOException {
		 reset();	
	 }

		//Constructores
	public AñadirObjetoSinCabecera () throws IOException{ 
		super();
		}
		public AñadirObjetoSinCabecera(OutputStream out) throws IOException{
		super(out);
		}
	}


