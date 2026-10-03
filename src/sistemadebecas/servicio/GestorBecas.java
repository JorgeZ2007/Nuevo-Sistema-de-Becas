/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.servicio;

import sistemadebecas.modelo.Beca;
import sistemadebecas.modelo.Beneficiario;
import sistemadebecas.excepciones.BecaNoEncontradaException;
import sistemadebecas.excepciones.RequisitoNoCumplidoException;
import sistemadebecas.modelo.Postulacion;

import java.util.*;

public class GestorBecas {
    private HashMap<Beneficiario, ArrayList<Postulacion>> mapaPostulaciones ;

    public GestorBecas() {
        this.mapaPostulaciones = new HashMap<>();
    }
    
    // Método para agregar una nueva postulacion a la lista de postulaciones
    // del Beneficiario
    private void agregarPostulacion(Beneficiario b, Postulacion p) {
        ArrayList<Postulacion> postulaciones=mapaPostulaciones.get(b) ;
        if (!postulaciones.contains(p)) {
            postulaciones.add(p) ;
            return ;
        }
    }
    
    public Map<Beneficiario, ArrayList<Postulacion>> getMapaPostulaciones() {
        return Collections.unmodifiableMap(mapaPostulaciones);
    }

    // Agregamos un Beneficiario al mapa y creamos su ArrayList
    // para almacenar sus postulaciones
    public boolean agregarBeneficiario(Beneficiario b, Postulacion p) {
        if (mapaPostulaciones.containsKey(b)) {
            agregarPostulacion(b, p) ;
            return false ;
        }
        ArrayList<Postulacion> postulaciones=new ArrayList<>() ;
        postulaciones.add(p) ;
        mapaPostulaciones.put(b, postulaciones) ;
        return true ;
    }
    
    // Mostrar las postulaciones de un beneficiario especifico y el
    // estado en el que se encuentra la postulacion
    public void mostrarPostulaciones(Beneficiario b) {
        if (b==null || !mapaPostulaciones.containsKey(b)) {
            System.out.println("Beneficiario no encontrado, intente nuevamente.") ;
            return ;
        }
        
        ArrayList<Postulacion> postulaciones=mapaPostulaciones.get(b) ;
        
        System.out.println("Postulaciones del Beneficiario " + b.getRut() + ".") ;
        for (int i=0; i<postulaciones.size() ; i++) {
            Postulacion p=postulaciones.get(i) ;
            System.out.println(i + ". Postulacion a Beca: "+ p.getBecaSolicitada()) ;
            System.out.println("Estado: " + p.getEstado()) ;
        }
    }
    
    // Eliminar una postulacion especifica de un beneficiario
    public boolean eliminarPostulacion(Beneficiario b, Postulacion p) {
        if (!mapaPostulaciones.containsKey(b)) {
            return false ;
        }
        ArrayList<Postulacion> postulaciones=mapaPostulaciones.get(b) ;
        postulaciones.remove(p) ;
        return true ;
    }
    
    // Busqueda de una postulacion especifica de un Beneficiario
    public boolean buscarPostulacion(Beneficiario b, Postulacion p) {
        if (!mapaPostulaciones.containsKey(b)) {
            return false ;
        }
        ArrayList<Postulacion> postulaciones=mapaPostulaciones.get(b) ;
        for (int i=0; i<postulaciones.size(); i++) {
            if ( (postulaciones.get(i)).equals(p) ) {
                return true ;
            }
        }
        return false ;
    }
    
    // Buscar Beneficiario a partir de un rut
    public Beneficiario buscarBeneficiario(String rut) {
        for (Beneficiario b : mapaPostulaciones.keySet()) {
        if (b.getRut().equalsIgnoreCase(rut)) {
            return b ;
        }
    }
    return null ;
    }


    public Postulacion obtenerPostulacion(int indice, Beneficiario b) {
        if (!mapaPostulaciones.containsKey(b)) {
        return null;
        }
    
        ArrayList<Postulacion> lista = mapaPostulaciones.get(b);
        if (indice < 1 || indice > lista.size()) {
            return null;   // así los bucles "while (p==null)" del menú funcionan
        }
        return lista.get(indice - 1);
    }
}
