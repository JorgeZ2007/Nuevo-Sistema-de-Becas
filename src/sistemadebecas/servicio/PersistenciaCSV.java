/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.servicio;

import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;

import java.io.*;

public class PersistenciaCSV {
    
    private static final String ARCHIVO_DATOS = "datos_becas.csv";
    private static final String SEPARADOR = ";";

    /**
     * Carga masiva de datos (Batch Load) al iniciar la aplicación.
     */
    public void cargarDatosBatch(GestorBecas gestor) {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            return; // Si el archivo no existe aún, se iniciará el programa con el gestor vacío.
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;
            Beca becaActual = null;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split(SEPARADOR);
                String tipoRegistro = campos[0];

                if (tipoRegistro.equalsIgnoreCase("BECA")) {
                    // Estructura: BECA;id;nombre;monto;cupos
                    String id = campos[1];
                    String nombre = campos[2];
                    double monto = Double.parseDouble(campos[3]);
                    int cupos = Integer.parseInt(campos[4]);

                    becaActual = new Beca(id, nombre, monto, cupos);
                    gestor.agregarBeca(becaActual);

                } else if (tipoRegistro.equalsIgnoreCase("BENEFICIARIO")) {
                    // Estructura: BENEFICIARIO;idBeca;nombre;rut;fechaNac;genero;carrera;promedio;quintil
                    String idBeca = campos[1];
                    String nombre = campos[2];
                    String rut = campos[3];
                    String fechaNac = campos[4];
                    String genero = campos[5];
                    String carrera = campos[6];
                    double promedio = Double.parseDouble(campos[7]);
                    int quintil = Integer.parseInt(campos[8]);

                    Beneficiario b = new Beneficiario(nombre, rut, fechaNac, genero, carrera, promedio, quintil);
                    
                    try {
                        gestor.agregarBeneficiarioABeca(idBeca, b);
                    } catch (Exception e) {
                        // Silenciar excepciones al recargar datos válidos desde archivo
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los datos desde el archivo CSV: " + e.getMessage());
        }
    }

    /**
     * Guardado masivo de datos (Batch Save) al cerrar la aplicación.
     */
    public void guardarDatosBatch(GestorBecas gestor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DATOS))) {
            
            for (Beca beca : gestor.getMapaBecas().values()) {
                // Escribir la línea correspondiente a la Beca
                writer.write(String.format("BECA;%s;%s;%.2f;%d",
                        beca.getIdBeca(),
                        beca.getNombreBeca(),
                        beca.getMontoMensual(),
                        beca.getCuposMaximos()));
                writer.newLine();

                // Escribir los beneficiarios anidados en esa Beca
                for (Beneficiario b : beca.getListaBeneficiarios()) {
                    writer.write(String.format("BENEFICIARIO;%s;%s;%s;%s;%s;%s;%.2f;%d",
                            beca.getIdBeca(),
                            b.getNombre(),
                            b.getRut(),
                            b.getFechaNacimiento(),
                            b.getGenero(),
                            b.getCarrera(),
                            b.getPromedioNotas(),
                            b.getQuintilSocioeconomico()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar los datos en el archivo CSV: " + e.getMessage());
        }
    }
}
