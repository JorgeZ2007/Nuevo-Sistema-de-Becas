/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.servicio;

import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.BecaAcademica;
import sistemadebecas.modelo.BecaSocioeconomica;
import sistemadebecas.modelo.Beneficiario;

import java.io.*;
import java.util.Locale;

public class PersistenciaCSV {

    private static final String ARCHIVO_DATOS = "datos_becas.csv";
    private static final String SEPARADOR = ";";

    /**
     * Carga masiva de datos (Batch Load) al iniciar la aplicación.
     */
    public void cargarDatosBatch(GestorBecas gestor) {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            return; // Si el archivo no existe aún, se inicia vacio.
        }

        try (BufferedReader reader = new BufferedReader(new FileReader(archivo))) {
            String linea;

            while ((linea = reader.readLine()) != null) {
                if (linea.trim().isEmpty()) {
                    continue;
                }

                String[] campos = linea.split(SEPARADOR);
                String tipoRegistro = campos[0];

                if (tipoRegistro.equalsIgnoreCase("BECA")) {
                    // Estructura compatible: BECA;id;nombre;monto;cupos;[tipoBeca]
                    String id = campos[1];
                    String nombre = campos[2];
                    double monto = Double.parseDouble(campos[3].replace(',', '.'));
                    int cupos = Integer.parseInt(campos[4].trim());

                    String tipoBeca = (campos.length > 5) ? campos[5].trim() : "ACADEMICA";

                    Beca becaActual;
                    if (tipoBeca.equalsIgnoreCase("SOCIOECONOMICA")) {
                        // Beca Socioeconómica por defecto (quintil max 2)
                        becaActual = new BecaSocioeconomica(id, nombre, monto, cupos, 2);
                    } else {
                        // Beca Académica por defecto (promedio min 5.0)
                        becaActual = new BecaAcademica(id, nombre, monto, cupos, 5.0);
                    }

                    gestor.agregarBeca(becaActual);

                } else if (tipoRegistro.equalsIgnoreCase("BENEFICIARIO")) {
                    // Estructura: BENEFICIARIO;idBeca;nombre;rut;fechaNac;genero;carrera;promedio;quintil
                    String idBeca = campos[1];
                    String nombre = campos[2];
                    String rut = campos[3];
                    String fechaNac = campos[4];
                    String genero = campos[5];
                    String carrera = campos[6];
                    double promedio = Double.parseDouble(campos[7].replace(',', '.'));
                    int quintil = Integer.parseInt(campos[8].trim());

                    Beneficiario b = new Beneficiario(nombre, rut, fechaNac, genero, carrera, promedio, quintil);

                    try {
                        gestor.agregarBeneficiarioABeca(idBeca, b);
                    } catch (Exception e) {
                        // Silencia excepciones de registros duplicados al recargar
                    }
                }
            }
        } catch (IOException e) {
            System.err.println("Error al cargar los datos desde el archivo CSV: " + e.getMessage());
        } catch (NumberFormatException e) {
            System.err.println("Error en el formato numérico del archivo CSV: " + e.getMessage());
        }
    }

    /**
     * Guardado masivo de datos (Batch Save) al cerrar la aplicación.
     */
    public void guardarDatosBatch(GestorBecas gestor) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(ARCHIVO_DATOS))) {

            for (Beca beca : gestor.getMapaBecas().values()) {
                String tipoStr = (beca instanceof BecaSocioeconomica) ? "SOCIOECONOMICA" : "ACADEMICA";

                writer.write(String.format(Locale.US, "BECA;%s;%s;%.2f;%d;%s",
                        beca.getIdBeca(),
                        beca.getNombreBeca(),
                        beca.getMontoMensual(),
                        beca.getCuposMaximos(),
                        tipoStr));
                writer.newLine();

                for (Beneficiario b : beca.getListaBeneficiarios()) {
                    writer.write(String.format(Locale.US, "BENEFICIARIO;%s;%s;%s;%s;%s;%s;%.2f;%d",
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