/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistema.de.becas;

import sistemadebecas.servicio.GestorBecas;
import sistemadebecas.servicio.PersistenciaCSV;
import sistemadebecas.vista.MenuConsola;

public class SistemaDeBecas {
    
    public static void main(String[] args) {
        // Inicialización de componentes del sistema
        GestorBecas gestor = new GestorBecas();
        PersistenciaCSV persistencia = new PersistenciaCSV();
        
        // Ejecución de la interfaz de usuario
        MenuConsola menu = new MenuConsola(gestor, persistencia);
        menu.iniciar();
    }
    
}
