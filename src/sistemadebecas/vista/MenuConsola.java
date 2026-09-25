/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.vista;

import sistemadebecas.servicio.GestorBecas;
import sistemadebecas.servicio.PersistenciaCSV;
import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;
import sistemadebecas.excepciones.BecaNoEncontradaException;
import sistemadebecas.excepciones.RequisitoNoCumplidoException;

import java.util.*;

public class MenuConsola {
    private GestorBecas gestor;
    private PersistenciaCSV persistencia;
    private Scanner scanner;

    public MenuConsola(GestorBecas gestor, PersistenciaCSV persistencia) {
        this.gestor = gestor;
        this.persistencia = persistencia;
        this.scanner = new Scanner(System.in);
    }

    public void iniciar() {
        // Carga Batch al iniciar
        persistencia.cargarDatosBatch(gestor);

        int opcion = -1;
        do {
            mostrarMenuPrincipal();
            opcion = leerEntero("Seleccione una opción: ");
            procesarOpcion(opcion);
        } while (opcion != 0);

        // Guardado Batch al salir
        persistencia.guardarDatosBatch(gestor);
        System.out.println("¡Datos guardados exitosamente! Programa finalizado.");
    }

    private void mostrarMenuPrincipal() {
        System.out.println("\n========== SISTEMA DE GESTIÓN DE BECAS ==========");
        System.out.println("1. Registrar nueva Beca");
        System.out.println("2. Listar todas las Becas");
        System.out.println("3. Buscar / Eliminar Beca");
        System.out.println("4. Registrar Beneficiario en Beca");
        System.out.println("5. Listar Beneficiarios de una Beca");
        System.out.println("6. Buscar / Eliminar Beneficiario de Beca");
        System.out.println("7. Ver Reporte de Postulantes Prioritarios (Filtro Socioeconómico + Promedio)");
        System.out.println("0. Salir y Guardar Cambios");
        System.out.println("=================================================");
    }

    private void procesarOpcion(int opcion) {
        try {
            switch (opcion) {
                case 1:
                    registrarBeca();
                    break;
                case 2:
                    listarBecas();
                    break;
                case 3:
                    buscarOEliminarBeca();
                    break;
                case 4:
                    registrarBeneficiario();
                    break;
                case 5:
                    listarBeneficiarios();
                    break;
                case 6:
                    buscarOEliminarBeneficiario();
                    break;
                case 7:
                    mostrarPrioritarios();
                    break;
                case 0:
                    System.out.println("Cerrando sesión y guardando datos...");
                    break;
                default:
                    System.out.println("Opción inválida. Intente de nuevo.");
            }
        } catch (BecaNoEncontradaException | RequisitoNoCumplidoException e) {
            System.err.println(e.getMessage());
        } catch (Exception e) {
            System.err.println("Ocurrió un error inesperado: " + e.getMessage());
        }
    }


    private void registrarBeca() {
        System.out.println("\n--- REGISTRO DE BECA ---");
        System.out.print("ID de Beca: ");
        String id = scanner.nextLine().trim();
        System.out.print("Nombre de Beca: ");
        String nombre = scanner.nextLine().trim();
        double monto = leerDouble("Monto mensual ($): ");
        int cupos = leerEntero("Cupos máximos: ");

        Beca nuevaBeca = new Beca(id, nombre, monto, cupos);
        if (gestor.agregarBeca(nuevaBeca)) {
            System.out.println("Beca registrada con éxito.");
        } else {
            System.out.println("Error: Ya existe una beca con ese ID.");
        }
    }

    private void listarBecas() {
        System.out.println("\n--- LISTADO DE BECAS ---");
        if (gestor.getMapaBecas().isEmpty()) {
            System.out.println("No hay becas registradas en el sistema.");
            return;
        }

        for (Beca b : gestor.getMapaBecas().values()) {
            System.out.printf("[%s] %s | Monto: $%.2f | Cupos: %d/%d | Presupuesto Anual Estimado: $%.2f%n",
                    b.getIdBeca(), b.getNombreBeca(), b.getMontoMensual(),
                    b.getListaBeneficiarios().size(), b.getCuposMaximos(),
                    b.calcularPresupuestoAnual());
        }
    }

    private void buscarOEliminarBeca() throws BecaNoEncontradaException {
        System.out.println("\n--- BUSCAR / ELIMINAR BECA ---");
        System.out.print("Ingrese ID de la Beca: ");
        String id = scanner.nextLine().trim();

        Beca b = gestor.buscarBeca(id);
        System.out.println("Beca encontrada: " + b.getNombreBeca() + " - Monto: $" + b.getMontoMensual());

        System.out.print("¿Desea eliminar esta beca? (s/n): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("s")) {
            gestor.eliminarBeca(id);
            System.out.println("Beca eliminada correctamente.");
        }
    }



    private void registrarBeneficiario() throws BecaNoEncontradaException, RequisitoNoCumplidoException {
        System.out.println("\n--- REGISTRO DE BENEFICIARIO ---");
        System.out.print("ID de la Beca a la cual postula: ");
        String idBeca = scanner.nextLine().trim();

        System.out.print("RUT: ");
        String rut = scanner.nextLine().trim();
        System.out.print("Nombre completo: ");
        String nombre = scanner.nextLine().trim();
        System.out.print("Fecha de Nacimiento (DD/MM/AAAA): ");
        String fechaNac = scanner.nextLine().trim();
        System.out.print("Género: ");
        String genero = scanner.nextLine().trim();
        System.out.print("Carrera: ");
        String carrera = scanner.nextLine().trim();
        double promedio = leerDouble("Promedio de notas (ej. 5.5): ");
        int quintil = leerEntero("Quintil socioeconómico (1-5): ");

        Beneficiario b = new Beneficiario(nombre, rut, fechaNac, genero, carrera, promedio, quintil);
        
        if (gestor.agregarBeneficiarioABeca(idBeca, b)) {
            System.out.println("Beneficiario asignado correctamente a la beca.");
        }
    }

    private void listarBeneficiarios() throws BecaNoEncontradaException {
        System.out.println("\n--- LISTAR BENEFICIARIOS DE UNA BECA ---");
        System.out.print("ID de Beca: ");
        String idBeca = scanner.nextLine().trim();

        System.out.print("¿Desea filtrar por quintil máximo? (Ingrese quintil 1-5 o presione ENTER para ver todos): ");
        String filtroInput = scanner.nextLine().trim();

        List<Beneficiario> lista;
        if (filtroInput.isEmpty()) {
            lista = gestor.obtenerBeneficiarios(idBeca); 
        } else {
            int quintilMax = Integer.parseInt(filtroInput);
            lista = gestor.obtenerBeneficiarios(idBeca, quintilMax); 
        }

        if (lista.isEmpty()) {
            System.out.println("No se encontraron beneficiarios con los criterios especificados.");
            return;
        }

        System.out.println("\nBeneficiarios asignados:");
        for (Beneficiario b : lista) {
            System.out.println("- " + b.obtenerDetalleFormateado());
        }
    }

    private void buscarOEliminarBeneficiario() throws BecaNoEncontradaException {
        System.out.println("\n--- BUSCAR / ELIMINAR BENEFICIARIO ---");
        System.out.print("ID de Beca: ");
        String idBeca = scanner.nextLine().trim();
        System.out.print("RUT del Beneficiario: ");
        String rut = scanner.nextLine().trim();

        Beca beca = gestor.buscarBeca(idBeca);
        Beneficiario b = beca.buscarBeneficiario(rut);

        if (b == null) {
            System.out.println("Beneficiario no encontrado en esta beca.");
            return;
        }

        System.out.println("Encontrado: " + b.obtenerDetalleFormateado());
        System.out.print("¿Desea eliminar a este beneficiario de la beca? (s/n): ");
        String resp = scanner.nextLine().trim();
        if (resp.equalsIgnoreCase("s")) {
            gestor.eliminarBeneficiarioDeBeca(idBeca, rut);
            System.out.println("Beneficiario eliminado con éxito.");
        }
    }

    private void mostrarPrioritarios() throws BecaNoEncontradaException {
        System.out.println("\n--- REPORTES: POSTULANTES PRIORITARIOS ---");
        System.out.print("ID de Beca: ");
        String idBeca = scanner.nextLine().trim();

        List<Beneficiario> prioritarios = gestor.obtenerPostulantesPrioritarios(idBeca);

        if (prioritarios.isEmpty()) {
            System.out.println("No hay beneficiarios prioritarios (Quintil <= 2 y Promedio >= 5.5).");
            return;
        }

        System.out.println("Beneficiarios con Prioridad de Asignación:");
        for (Beneficiario b : prioritarios) {
            System.out.println("* " + b.obtenerDetalleFormateado());
        }
    }


    private int leerEntero(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                int val = Integer.parseInt(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número entero.");
            }
        }
    }

    private double leerDouble(String mensaje) {
        while (true) {
            try {
                System.out.print(mensaje);
                double val = Double.parseDouble(scanner.nextLine().trim());
                return val;
            } catch (NumberFormatException e) {
                System.out.println("Entrada inválida. Ingrese un número decimal (ej. 5.5).");
            }
        }
    }
}
