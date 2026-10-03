/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.servicio;

import sistemadebecas.excepciones.BecaNoEncontradaException;
import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;
import sistemadebecas.modelo.Postulacion;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Persistencia batch en archivo CSV: se carga al iniciar la aplicación y se guarda al salir.
 *
 * Las becas NO se guardan aquí, porque se leen siempre desde "archivo.csv" (CargarDatosBecas).
 * Este archivo guarda solo lo que cambia durante la ejecución: los beneficiarios y sus postulaciones.
 *
 * Formato (separador ';', una línea por registro):
 *   BENEFICIARIO;rut;nombre;promedio;quintil;puntajePaes;viviendaValparaiso
 *   POSTULACION;rut;idBeca
 */
public class PersistenciaCSV {

    private static final String ARCHIVO_DATOS = "datos_becas.csv";
    private static final String SEPARADOR = ";";

    /**
     * Carga los beneficiarios y sus postulaciones desde el archivo.
     * Las becas ya deben estar cargadas en el CargarDatosBecas recibido.
     */
    public void cargarDatosBatch(GestorBecas gestor, CargarDatosBecas cargador) {
        File archivo = new File(ARCHIVO_DATOS);
        if (!archivo.exists()) {
            return; // primera ejecución: no hay datos que cargar
        }

        // Los beneficiarios se leen primero a un mapa auxiliar (por rut) para poder
        // enlazarlos con las líneas POSTULACION, que pueden venir en cualquier orden.
        Map<String, Beneficiario> beneficiarios = new HashMap<>();
        ArrayList<String[]> lineasPostulacion = new ArrayList<>();

        try (BufferedReader reader = new BufferedReader(
                new InputStreamReader(new FileInputStream(archivo), StandardCharsets.UTF_8))) {

            String linea;
            int numeroLinea = 0;
            while ((linea = reader.readLine()) != null) {
                numeroLinea++;
                if (linea.trim().isEmpty()) {
                    continue;
                }
                String[] campos = linea.split(SEPARADOR, -1);
                try {
                    if (campos[0].equals("BENEFICIARIO")) {
                        Beneficiario b = leerBeneficiario(campos);
                        beneficiarios.put(b.getRut(), b);
                    } else if (campos[0].equals("POSTULACION")) {
                        lineasPostulacion.add(campos);
                    }
                    // Cualquier otro tipo de línea se ignora (por ejemplo, de un formato antiguo)
                } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
                    System.err.println("Línea " + numeroLinea + " ignorada (formato inválido): " + linea);
                }
            }
        } catch (IOException e) {
            System.err.println("Error al leer '" + ARCHIVO_DATOS + "': " + e.getMessage());
            return;
        }

        // Se reconstruyen las postulaciones
        for (String[] campos : lineasPostulacion) {
            try {
                Beneficiario b = beneficiarios.get(campos[1]);
                if (b == null) {
                    System.err.println("Postulación ignorada: no existe el beneficiario " + campos[1]);
                    continue;
                }
                Beca beca = cargador.buscarBeca(campos[2]);
                gestor.agregarBeneficiario(b, new Postulacion(b, beca));
            } catch (BecaNoEncontradaException e) {
                // La beca ya no está en archivo.csv: se omite esa postulación
                System.err.println("Postulación ignorada. " + e.getMessage());
            } catch (ArrayIndexOutOfBoundsException e) {
                System.err.println("Postulación ignorada (formato inválido).");
            }
        }
    }

    /** Guarda todos los beneficiarios y sus postulaciones, sobrescribiendo el archivo. */
    public void guardarDatosBatch(GestorBecas gestor) {
        try (BufferedWriter writer = new BufferedWriter(
                new OutputStreamWriter(new FileOutputStream(ARCHIVO_DATOS), StandardCharsets.UTF_8))) {

            for (Map.Entry<Beneficiario, ArrayList<Postulacion>> entrada
                    : gestor.getMapaPostulaciones().entrySet()) {

                Beneficiario b = entrada.getKey();
                ArrayList<Postulacion> postulaciones = entrada.getValue();
                if (postulaciones.isEmpty()) {
                    continue; // sin postulaciones no hay nada que conservar
                }

                writer.write(String.join(SEPARADOR,
                        "BENEFICIARIO",
                        b.getRut(),
                        b.getNombre().replace(SEPARADOR, ","), // el nombre no puede traer el separador
                        String.format(Locale.US, "%.2f", b.getPromedioNotas()),
                        String.valueOf(b.getQuintilSocioeconomico()),
                        String.valueOf(b.getPuntajePaes()),
                        String.valueOf(b.getViviendaValparaiso())));
                writer.newLine();

                for (Postulacion p : postulaciones) {
                    writer.write(String.join(SEPARADOR,
                            "POSTULACION",
                            b.getRut(),
                            p.getBecaSolicitada().getIdBeca()));
                    writer.newLine();
                }
            }
        } catch (IOException e) {
            System.err.println("Error al guardar '" + ARCHIVO_DATOS + "': " + e.getMessage());
        }
    }

    // Convierte una línea BENEFICIARIO en un objeto. Puede lanzar NumberFormatException
    // o ArrayIndexOutOfBoundsException, que se tratan en el try-catch de quien la llama.
    private Beneficiario leerBeneficiario(String[] c) {
        String rut = c[1].trim();
        String nombre = c[2].trim();
        double promedio = Double.parseDouble(c[3].trim().replace(',', '.'));
        int quintil = Integer.parseInt(c[4].trim());
        int puntajePaes = Integer.parseInt(c[5].trim());
        boolean vivienda = Boolean.parseBoolean(c[6].trim());
        return new Beneficiario(nombre, rut, promedio, quintil, puntajePaes, vivienda);
    }
}