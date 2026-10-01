/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.vista;

import sistemadebecas.servicio.GestorBecas;
import sistemadebecas.servicio.PersistenciaCSV;
import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.BecaAcademica;
import sistemadebecas.modelo.BecaSocioeconomica;
import sistemadebecas.modelo.Beneficiario;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class VentanaPrincipal extends JFrame {

    private GestorBecas gestor;
    private PersistenciaCSV persistencia;

    // Componentes de la interfaz
    private JTable tablaBecas;
    private DefaultTableModel modeloTabla;
    private JButton btnRegistrarBeca;
    private JButton btnRegistrarBeneficiario;
    private JButton btnRefrescar;

    public VentanaPrincipal(GestorBecas gestor, PersistenciaCSV persistencia) {
        this.gestor = gestor;
        this.persistencia = persistencia;

        // 1. Cargar datos desde el CSV al iniciar
        this.persistencia.cargarDatosBatch(this.gestor);

        // 2. Configuración de la ventana principal
        setTitle("Sistema de Gestión de Becas (SIA) - Interfaz Gráfica");
        setSize(800, 500);
        setLocationRelativeTo(null); // Centrar en pantalla
        setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE); // Control manual del cierre

        // 3. Evento al cerrar la ventana (Guardado Batch automático)
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                persistencia.guardarDatosBatch(gestor);
                JOptionPane.showMessageDialog(null, "¡Datos guardados exitosamente en CSV!", "Sistema de Becas", JOptionPane.INFORMATION_MESSAGE);
                System.exit(0);
            }
        });

        // 4. Construcción del diseño
        initUI();
        actualizarTabla();
    }

    private void initUI() {
        setLayout(new BorderLayout(10, 10));

        // Título Superior
        JLabel lblTitulo = new JLabel("PANEL DE CONTROL DE BECAS Y BENEFICIARIOS", SwingConstants.CENTER);
        lblTitulo.setFont(new Font("Arial", Font.BOLD, 16));
        lblTitulo.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(lblTitulo, BorderLayout.NORTH);

        // Tabla Central
        String[] columnas = {"ID Beca", "Nombre Beca", "Monto Mensual", "Cupos", "Inscritos", "Presupuesto Anual"};
        modeloTabla = new DefaultTableModel(columnas, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false; // Desactivar edición directa
            }
        };
        tablaBecas = new JTable(modeloTabla);
        add(new JScrollPane(tablaBecas), BorderLayout.CENTER);

        // Panel de Botones Inferior
        JPanel panelBotones = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));

        btnRegistrarBeca = new JButton("Registrar Beca");
        btnRegistrarBeneficiario = new JButton("Postular Beneficiario");
        btnRefrescar = new JButton("Actualizar Tabla");

        panelBotones.add(btnRegistrarBeca);
        panelBotones.add(btnRegistrarBeneficiario);
        panelBotones.add(btnRefrescar);

        add(panelBotones, BorderLayout.SOUTH);

        // Listeners de los botones
        btnRegistrarBeca.addActionListener(e -> menuRegistrarBeca());
        btnRegistrarBeneficiario.addActionListener(e -> menuRegistrarBeneficiario());
        btnRefrescar.addActionListener(e -> actualizarTabla());
    }

    private void actualizarTabla() {
        modeloTabla.setRowCount(0); // Limpiar filas anteriores
        for (Beca b : gestor.getMapaBecas().values()) {
            Object[] fila = {
                b.getIdBeca(),
                b.getNombreBeca(),
                String.format("$%.2f", b.getMontoMensual()),
                b.getCuposMaximos(),
                b.getListaBeneficiarios().size(),
                String.format("$%.2f", b.calcularPresupuestoAnual())
            };
            modeloTabla.addRow(fila);
        }
    }

    private void menuRegistrarBeca() {
        String[] opciones = {"Beca Académica", "Beca Socioeconómica"};
        int tipo = JOptionPane.showOptionDialog(this, "Seleccione el tipo de beca a crear:",
                "Registrar Nueva Beca", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE,
                null, opciones, opciones[0]);

        if (tipo == -1) return;

        try {
            String id = JOptionPane.showInputDialog(this, "ID de Beca:");
            if (id == null || id.trim().isEmpty()) return;

            String nombre = JOptionPane.showInputDialog(this, "Nombre de Beca:");
            if (nombre == null || nombre.trim().isEmpty()) return;

            double monto = Double.parseDouble(JOptionPane.showInputDialog(this, "Monto mensual ($):").replace(',', '.'));
            int cupos = Integer.parseInt(JOptionPane.showInputDialog(this, "Cupos máximos:"));

            Beca nuevaBeca;
            if (tipo == 0) {
                double prom = Double.parseDouble(JOptionPane.showInputDialog(this, "Promedio mínimo exigido:").replace(',', '.'));
                nuevaBeca = new BecaAcademica(id, nombre, monto, cupos, prom);
            } else {
                int quintil = Integer.parseInt(JOptionPane.showInputDialog(this, "Quintil máximo (1-5):"));
                nuevaBeca = new BecaSocioeconomica(id, nombre, monto, cupos, quintil);
            }

            if (gestor.agregarBeca(nuevaBeca)) {
                JOptionPane.showMessageDialog(this, "Beca registrada con éxito.");
                actualizarTabla();
            } else {
                JOptionPane.showMessageDialog(this, "Error: Ya existe una beca con ese ID.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Datos ingresados no válidos: " + ex.getMessage(), "Error de Entrada", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void menuRegistrarBeneficiario() {
        try {
            String idBeca = JOptionPane.showInputDialog(this, "ID de la Beca a la cual postula:");
            if (idBeca == null || idBeca.trim().isEmpty()) return;

            String rut = JOptionPane.showInputDialog(this, "RUT del Estudiante:");
            String nombre = JOptionPane.showInputDialog(this, "Nombre Completo:");
            String fechaNac = JOptionPane.showInputDialog(this, "Fecha Nacimiento (DD/MM/AAAA):");
            String genero = JOptionPane.showInputDialog(this, "Género:");
            String carrera = JOptionPane.showInputDialog(this, "Carrera:");
            double promedio = Double.parseDouble(JOptionPane.showInputDialog(this, "Promedio de notas:").replace(',', '.'));
            int quintil = Integer.parseInt(JOptionPane.showInputDialog(this, "Quintil Socioeconómico (1-5):"));

            Beneficiario b = new Beneficiario(nombre, rut, fechaNac, genero, carrera, promedio, quintil);

            if (gestor.agregarBeneficiarioABeca(idBeca, b)) {
                JOptionPane.showMessageDialog(this, "Beneficiario evaluado y asignado con éxito a la beca.");
                actualizarTabla();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al registrar beneficiario: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
        }
    }
}
