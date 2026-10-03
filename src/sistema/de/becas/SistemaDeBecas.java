/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema.de.becas;

import sistemadebecas.servicio.GestorBecas;
import sistemadebecas.servicio.PersistenciaCSV;
import sistemadebecas.vista.MenuConsola;
import sistemadebecas.vista.VentanaPrincipal;
import sistemadebecas.servicio.CargarDatosBecas;

import javax.swing.*;

public class SistemaDeBecas {

    public static void main(String[] args) {
        GestorBecas gestor = new GestorBecas();
        PersistenciaCSV persistencia = new PersistenciaCSV();
        CargarDatosBecas cargador = new CargarDatosBecas();

        String[] opciones = {"Ventana Gráfica (GUI)", "Consola de Comandos"};
        int seleccion = JOptionPane.showOptionDialog(
                null,
                "¿Cómo desea ejecutar el Sistema de Gestión de Becas?",
                "Modo de Ejecución",
                JOptionPane.DEFAULT_OPTION,
                JOptionPane.QUESTION_MESSAGE,
                null,
                opciones,
                opciones[0]
        );

        if (seleccion == 0) {
            // Ejecutar Modo Ventana Swing
            SwingUtilities.invokeLater(() -> {
                VentanaPrincipal ventana = new VentanaPrincipal(gestor, persistencia, cargador);
                ventana.setVisible(true);
            });
        } else if (seleccion == 1) {
            // Ejecutar Modo Consola
            MenuConsola menu = new MenuConsola(gestor, persistencia, cargador);
            menu.iniciar();
        }
    }
}