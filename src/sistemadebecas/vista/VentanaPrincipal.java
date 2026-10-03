/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.vista;

import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;
import sistemadebecas.modelo.Postulacion;
import sistemadebecas.servicio.CargarDatosBecas;
import sistemadebecas.servicio.GestorBecas;
import sistemadebecas.servicio.PersistenciaCSV;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

/**
 * Interfaz gráfica del sistema. Replica las opciones de MenuConsola:
 *  1. Listar becas                        -> pestaña "Becas disponibles"
 *  2. Registrar beneficiario y postular   -> botón de la pestaña "Becas disponibles"
 *  3. Listar postulaciones                -> pestaña "Mis postulaciones" (tabla)
 *  4. Eliminar postulación                -> botón de "Mis postulaciones"
 *  5. Generar reporte                     -> botón de "Mis postulaciones"
 *  6. Buscar postulación                  -> botón de "Mis postulaciones"
 *  0. Salir y guardar cambios             -> botón inferior o cierre de la ventana
 */
public class VentanaPrincipal extends JFrame {

    // Mismo archivo de becas que usa MenuConsola
    private static final String ARCHIVO_BECAS = "archivo.csv";

    private final GestorBecas gestor;
    private final PersistenciaCSV persistencia;
    private final HashMap<String, Beca> mapaBecas;

    // Pestaña de becas
    private DefaultTableModel modeloBecas;
    private JTable tablaBecas;

    // Pestaña de postulaciones
    private JTextField txtRutConsulta;
    private DefaultTableModel modeloPostulaciones;
    private JTable tablaPostulaciones;
    private Beneficiario beneficiarioActual;

    public VentanaPrincipal(GestorBecas gestor, PersistenciaCSV persistencia, CargarDatosBecas cargador) {
        this.gestor = gestor;
        this.persistencia = persistencia;

        // Igual que MenuConsola.iniciar(): primero las becas postulables y luego la carga batch
        this.mapaBecas = cargador.leerArchivo(ARCHIVO_BECAS);
        this.persistencia.cargarDatosBatch(this.gestor, cargador);

        setTitle("Sistema de Gestión de Becas (SIA)");
        setSize(850, 520);
        setMinimumSize(new Dimension(700, 420));
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // el cierre se controla a mano

        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                salirYGuardar();
            }
        });

        initUI();
        actualizarTablaBecas();

        if (mapaBecas.isEmpty()) {
            JOptionPane.showMessageDialog(this,
                    "No se cargaron becas desde '" + ARCHIVO_BECAS + "'.\n"
                    + "Verifique que el archivo exista en la carpeta del proyecto.",
                    "Aviso", JOptionPane.WARNING_MESSAGE);
        }
    }

    // ------------------------------------------------------------------
    // Construcción de la interfaz
    // ------------------------------------------------------------------

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        JLabel titulo = new JLabel("SISTEMA DE GESTIÓN DE BECAS", SwingConstants.CENTER);
        titulo.setFont(new Font("Arial", Font.BOLD, 16));
        titulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 0, 10));
        add(titulo, BorderLayout.NORTH);

        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Becas disponibles", crearPanelBecas());
        pestanas.addTab("Mis postulaciones", crearPanelPostulaciones());
        add(pestanas, BorderLayout.CENTER);

        JButton btnSalir = new JButton("Salir y guardar cambios");
        btnSalir.addActionListener(e -> salirYGuardar());
        JPanel panelSur = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        panelSur.add(btnSalir);
        add(panelSur, BorderLayout.SOUTH);
    }

    private JPanel crearPanelBecas() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        String[] columnas = {"ID", "Nombre", "Descripción", "Estado"};
        modeloBecas = crearModeloNoEditable(columnas);
        tablaBecas = new JTable(modeloBecas);
        tablaBecas.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaBecas.getColumnModel().getColumn(0).setMaxWidth(60);
        panel.add(new JScrollPane(tablaBecas), BorderLayout.CENTER);

        JButton btnPostular = new JButton("Registrar / postular a la beca seleccionada");
        btnPostular.addActionListener(e -> registrarYPostular());
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER));
        botones.add(btnPostular);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private JPanel crearPanelPostulaciones() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Búsqueda del beneficiario por RUT
        JPanel panelBusqueda = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 5));
        txtRutConsulta = new JTextField(14);
        JButton btnBuscar = new JButton("Buscar beneficiario");
        btnBuscar.addActionListener(e -> consultarBeneficiario());
        txtRutConsulta.addActionListener(e -> consultarBeneficiario()); // Enter también busca
        panelBusqueda.add(new JLabel("RUT:"));
        panelBusqueda.add(txtRutConsulta);
        panelBusqueda.add(btnBuscar);
        panel.add(panelBusqueda, BorderLayout.NORTH);

        // Tabla de postulaciones del beneficiario
        String[] columnas = {"N°", "Beca", "Estado"};
        modeloPostulaciones = crearModeloNoEditable(columnas);
        tablaPostulaciones = new JTable(modeloPostulaciones);
        tablaPostulaciones.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tablaPostulaciones.getColumnModel().getColumn(0).setMaxWidth(60);
        panel.add(new JScrollPane(tablaPostulaciones), BorderLayout.CENTER);

        // Acciones sobre la postulación seleccionada
        JButton btnEliminar = new JButton("Eliminar postulación");
        JButton btnReporte = new JButton("Generar reporte");
        JButton btnVerificar = new JButton("Buscar postulación");
        btnEliminar.addActionListener(e -> eliminarPostulacion());
        btnReporte.addActionListener(e -> generarReporte());
        btnVerificar.addActionListener(e -> verificarPostulacion());

        JPanel botones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 5));
        botones.add(btnEliminar);
        botones.add(btnReporte);
        botones.add(btnVerificar);
        panel.add(botones, BorderLayout.SOUTH);

        return panel;
    }

    private DefaultTableModel crearModeloNoEditable(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
    }

    // ------------------------------------------------------------------
    // Opción 1: listar becas
    // ------------------------------------------------------------------

    private void actualizarTablaBecas() {
        modeloBecas.setRowCount(0);
        List<String> ids = new ArrayList<>(mapaBecas.keySet());
        Collections.sort(ids);
        for (String id : ids) {
            Beca beca = mapaBecas.get(id);
            beca.cambioEstado(); // actualiza el estado según la fecha actual
            modeloBecas.addRow(new Object[]{
                beca.getIdBeca(),
                beca.getNombreBeca(),
                beca.getDescripcionBeca(),
                beca.getEstado()
            });
        }
    }

    // ------------------------------------------------------------------
    // Opción 2: registrar beneficiario y postular a una beca
    // ------------------------------------------------------------------

    private void registrarYPostular() {
        int fila = tablaBecas.getSelectedRow();
        if (fila < 0) {
            aviso("Seleccione en la tabla la beca a la que desea postular.");
            return;
        }
        Beca beca = mapaBecas.get((String) modeloBecas.getValueAt(fila, 0));

        String rut = JOptionPane.showInputDialog(this, "Ingrese su rut:");
        if (rut == null || rut.trim().isEmpty()) {
            return; // el usuario canceló
        }
        rut = rut.trim();

        Beneficiario b = gestor.buscarBeneficiario(rut);
        if (b == null) {
            b = pedirDatosBeneficiario(rut);
            if (b == null) {
                return; // canceló el formulario
            }
        }
        postularBeca(b, beca);
    }

    /** Formulario para registrar un beneficiario nuevo. Devuelve null si se cancela. */
    private Beneficiario pedirDatosBeneficiario(String rut) {
        JTextField txtNombre = new JTextField(20);
        JSpinner spQuintil = new JSpinner(new SpinnerNumberModel(3, 1, 5, 1));
        JComboBox<String> cbVivienda = new JComboBox<>(new String[]{"NO", "SI"});
        JTextField txtPromedio = new JTextField(6);
        JTextField txtPaes = new JTextField(6);

        JPanel form = new JPanel(new GridLayout(0, 2, 8, 8));
        form.add(new JLabel("RUT:"));
        form.add(new JLabel(rut));
        form.add(new JLabel("Nombre:"));
        form.add(txtNombre);
        form.add(new JLabel("Quintil socioeconómico (1-5):"));
        form.add(spQuintil);
        form.add(new JLabel("¿Vivienda en la región de Valparaíso?"));
        form.add(cbVivienda);
        form.add(new JLabel("Promedio académico actual:"));
        form.add(txtPromedio);
        form.add(new JLabel("Puntaje PAES:"));
        form.add(txtPaes);

        while (true) {
            int respuesta = JOptionPane.showConfirmDialog(this, form, "Registro de nuevo beneficiario",
                    JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
            if (respuesta != JOptionPane.OK_OPTION) {
                return null;
            }
            try {
                String nombre = txtNombre.getText().trim();
                if (nombre.isEmpty()) {
                    throw new IllegalArgumentException("El nombre no puede estar vacío.");
                }
                int quintil = (Integer) spQuintil.getValue();
                boolean vivienda = "SI".equals(cbVivienda.getSelectedItem());
                double promedio = Double.parseDouble(txtPromedio.getText().trim().replace(',', '.'));
                int puntajePaes = Integer.parseInt(txtPaes.getText().trim());
                if (promedio < 0 || puntajePaes < 0) {
                    throw new IllegalArgumentException("Los valores no pueden ser negativos.");
                }
                return new Beneficiario(nombre, rut, promedio, quintil, puntajePaes, vivienda);
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(this,
                        "El promedio debe ser un número decimal y el puntaje PAES un número entero.",
                        "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            } catch (IllegalArgumentException ex) {
                JOptionPane.showMessageDialog(this, ex.getMessage(),
                        "Datos inválidos", JOptionPane.ERROR_MESSAGE);
            }
        }
    }

    /** Misma lógica que MenuConsola.postularBeca(), pero con la beca elegida en la tabla. */
    private void postularBeca(Beneficiario b, Beca beca) {
        beca.cambioEstado();
        if (beca.getEstado().equals("Cerrado")) {
            JOptionPane.showMessageDialog(this, "La beca ya no se encuentra abierta a postulaciones.",
                    "Postulación", JOptionPane.INFORMATION_MESSAGE);
            actualizarTablaBecas();
            return;
        }

        Postulacion post = new Postulacion(b, beca);
        gestor.agregarBeneficiario(b, post);
        JOptionPane.showMessageDialog(this, "Beca postulada exitosamente.",
                "Postulación", JOptionPane.INFORMATION_MESSAGE);

        // Si se está mirando a este mismo beneficiario, se refresca su tabla
        if (beneficiarioActual != null && beneficiarioActual.equals(b)) {
            actualizarTablaPostulaciones();
        }
    }

    // ------------------------------------------------------------------
    // Opción 3: listar postulaciones de un beneficiario
    // ------------------------------------------------------------------

    private void consultarBeneficiario() {
        String rut = txtRutConsulta.getText().trim();
        if (rut.isEmpty()) {
            aviso("Ingrese un RUT.");
            return;
        }
        Beneficiario b = gestor.buscarBeneficiario(rut);
        if (b == null) {
            beneficiarioActual = null;
            modeloPostulaciones.setRowCount(0);
            JOptionPane.showMessageDialog(this, "Beneficiario no encontrado, intente nuevamente.",
                    "Búsqueda", JOptionPane.WARNING_MESSAGE);
            return;
        }
        beneficiarioActual = b;
        actualizarTablaPostulaciones();
    }

    private void actualizarTablaPostulaciones() {
        modeloPostulaciones.setRowCount(0);
        if (beneficiarioActual == null) {
            return;
        }
        List<Postulacion> lista = obtenerPostulaciones(beneficiarioActual);
        for (int i = 0; i < lista.size(); i++) {
            Postulacion p = lista.get(i);
            modeloPostulaciones.addRow(new Object[]{
                i + 1,
                p.getBecaSolicitada().getNombreBeca(),
                p.getEstado()
            });
        }
    }

    /**
     * Obtiene las postulaciones de un beneficiario usando GestorBecas.obtenerPostulacion(indice, b),
     * que numera desde 1 y lanza IndexOutOfBoundsException al pasarse del final.
     * (Más limpio: agregar a GestorBecas un método que devuelva la lista completa.)
     */
    private List<Postulacion> obtenerPostulaciones(Beneficiario b) {
        List<Postulacion> resultado = new ArrayList<>();
        int indice = 1;
        while (true) {
            try {
                Postulacion p = gestor.obtenerPostulacion(indice, b);
                if (p == null) {
                    break;
                }
                resultado.add(p);
                indice++;
            } catch (IndexOutOfBoundsException e) {
                break; // ya no hay más postulaciones
            }
        }
        return resultado;
    }

    /** Devuelve la postulación elegida en la tabla, o null si falta algún paso. */
    private Postulacion postulacionSeleccionada() {
        if (beneficiarioActual == null) {
            aviso("Primero busque un beneficiario por su RUT.");
            return null;
        }
        int fila = tablaPostulaciones.getSelectedRow();
        if (fila < 0) {
            aviso("Seleccione una postulación de la tabla.");
            return null;
        }
        List<Postulacion> lista = obtenerPostulaciones(beneficiarioActual);
        if (fila >= lista.size()) {
            aviso("Postulación ingresada incorrectamente, intente nuevamente.");
            return null;
        }
        return lista.get(fila);
    }

    // ------------------------------------------------------------------
    // Opción 4: eliminar postulación
    // ------------------------------------------------------------------

    private void eliminarPostulacion() {
        Postulacion p = postulacionSeleccionada();
        if (p == null) {
            return;
        }
        int confirmacion = JOptionPane.showConfirmDialog(this,
                "¿Desea eliminar la postulación a '" + p.getBecaSolicitada().getNombreBeca() + "'?",
                "Eliminar postulación", JOptionPane.YES_NO_OPTION);
        if (confirmacion != JOptionPane.YES_OPTION) {
            return;
        }
        gestor.eliminarPostulacion(beneficiarioActual, p);
        JOptionPane.showMessageDialog(this, "Postulación eliminada correctamente.",
                "Eliminar postulación", JOptionPane.INFORMATION_MESSAGE);
        actualizarTablaPostulaciones();
    }

    // ------------------------------------------------------------------
    // Opción 5: generar reporte
    // ------------------------------------------------------------------

    private void generarReporte() {
        Postulacion p = postulacionSeleccionada();
        if (p == null) {
            return;
        }
        try {
            // Postulacion.generarReporte() imprime por consola; se captura para mostrarlo en la ventana
            String texto = capturarSalida(p::generarReporte);

            JTextArea area = new JTextArea(texto, 8, 40);
            area.setEditable(false);
            JOptionPane.showMessageDialog(this, new JScrollPane(area),
                    "Reporte de postulación", JOptionPane.INFORMATION_MESSAGE);
            actualizarTablaPostulaciones();
        } catch (RuntimeException ex) {
            JOptionPane.showMessageDialog(this, "No se pudo generar el reporte: " + ex.getMessage(),
                    "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    /** Ejecuta una acción y devuelve lo que habría impreso por System.out. */
    private String capturarSalida(Runnable accion) {
        PrintStream original = System.out;
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            PrintStream temporal = new PrintStream(buffer, true, "UTF-8");
            System.setOut(temporal);
            accion.run();
            temporal.flush();
            return buffer.toString("UTF-8");
        } catch (UnsupportedEncodingException e) {
            return "No se pudo leer el reporte: " + e.getMessage();
        } finally {
            System.setOut(original); // siempre se restaura la consola
        }
    }

    // ------------------------------------------------------------------
    // Opción 6: buscar postulación
    // ------------------------------------------------------------------

    private void verificarPostulacion() {
        Postulacion p = postulacionSeleccionada();
        if (p == null) {
            return;
        }
        if (!gestor.buscarPostulacion(beneficiarioActual, p)) {
            JOptionPane.showMessageDialog(this, "Usted no ha postulado a esta beca.",
                    "Buscar postulación", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(this,
                "Usted tiene una postulación realizada a la beca " + p.getBecaSolicitada().getNombreBeca(),
                "Buscar postulación", JOptionPane.INFORMATION_MESSAGE);
    }

    // ------------------------------------------------------------------
    // Opción 0: salir y guardar (guardado batch)
    // ------------------------------------------------------------------

    private void salirYGuardar() {
        persistencia.guardarDatosBatch(gestor);
        JOptionPane.showMessageDialog(this, "¡Datos guardados exitosamente! Programa finalizado.",
                "Sistema de Becas", JOptionPane.INFORMATION_MESSAGE);
        dispose();
        System.exit(0);
    }

    private void aviso(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Aviso", JOptionPane.WARNING_MESSAGE);
    }
}