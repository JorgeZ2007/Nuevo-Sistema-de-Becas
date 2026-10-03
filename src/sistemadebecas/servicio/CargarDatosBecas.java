
package sistemadebecas.servicio;

import java.io.*;
import java.util.*;
import sistemadebecas.modelo.*;
import sistemadebecas.excepciones.BecaNoEncontradaException;

// Clase para gestionar de forma individual los datos de las becas
// extendidas de la clase Beca obteniendolas a partir de un archivo.
public class CargarDatosBecas {
    
    private ArrayList<Beca> tiposBecas=new ArrayList<>() ;
    private HashMap<String, Beca> mapaBecas=new HashMap<>() ;

    public HashMap<String, Beca> getMapaBecas() { return mapaBecas; }

    public Beca buscarBeca(String idBeca) throws BecaNoEncontradaException {
    Beca beca = mapaBecas.get(idBeca);
    if (beca == null) {
        throw new BecaNoEncontradaException(idBeca);
    }
    return beca;
}

    public CargarDatosBecas() {
        
    }

    // Metodo para obtener los datos de las becas a partir de un archivo
    public HashMap<String, Beca> leerArchivo(String archivo) {
        try (BufferedReader reader= new BufferedReader(new FileReader(archivo))) {
            String linea ;
            linea=reader.readLine() ;
            while (linea!=null) {
                String[] valores=linea.split("#") ;
                
                // Datos iniciales compartidos por todas las becas
                String id=valores[0].trim() ;
                String nombre=valores[1].trim() ;
                String desc=valores[2].trim() ;
                int monto=Integer.parseInt(valores[3].trim()) ;
                // Datos de las fechas de postulacion (tambien compartidos por todas las becas)
                LocalDate inicioPost=LocalDate.parse(valores[6].trim()) ;
                LocalDate cierrePost=LocalDate.parse(valores[7].trim()) ;
                LocalDate cierreRev=LocalDate.parse(valores[8].trim()) ;

                // Leemos según el id para guardar de forma separada los atributos que
                // cada Beca necesita
                Beca nuevaBeca=null ;
                switch(id) {
                    case "01": // Atributos necesarios para la clase BecaMantencionAcademica
                        int puntajePaes=Integer.parseInt(valores[4].trim()) ;
                        boolean vivienda1=Boolean.parseBoolean(valores[5].trim()) ;
                        nuevaBeca= new BecaMantencionAcademica(id, nombre, desc, monto, puntajePaes, vivienda1,
                                                                inicioPost, cierrePost, cierreRev) ;
                        break ;
                    case "02": // Atributos necesarios para la clase BecaResidencia
                        double promMin2=Double.parseDouble(valores[4].trim()) ;
                        boolean vivienda2=Boolean.parseBoolean(valores[5].trim()) ;
                        nuevaBeca= new BecaResidencia(id, nombre, desc, monto, promMin2, vivienda2,
                                                                inicioPost, cierrePost, cierreRev) ;
                        break ;
                    case "03": // Atributos necesarios para la clase BecaEstudio
                        double promMin3=Double.parseDouble(valores[4].trim()) ;
                        int quintil3=Integer.parseInt(valores[5].trim()) ;
                        nuevaBeca= new BecaEstudio(id, nombre, desc, monto, promMin3, quintil3,
                                                                inicioPost, cierrePost, cierreRev) ;
                        break ;
                    case "04": // Atributos necesarios para la clase BecaAlimentacion
                        boolean vivienda4=Boolean.parseBoolean(valores[4].trim()) ;
                        int quintil4=Integer.parseInt(valores[5].trim()) ;
                        nuevaBeca= new BecaAlimentacion(id, nombre, desc, monto, vivienda4, quintil4,
                                                                inicioPost, cierrePost, cierreRev) ;
                        break ;
                }
                // Guardamos cada beca según su id en un mapa:
                if (nuevaBeca!=null) {
                    mapaBecas.put(id, nuevaBeca) ;
                }
                linea=reader.readLine() ;
            }
        } catch (IOException e) {
            System.err.println("Error al leer el archivo") ;
        } catch (NumberFormatException e) {
            System.err.println("Error en formato númerico") ;
        }

        return mapaBecas ;
    }

}
