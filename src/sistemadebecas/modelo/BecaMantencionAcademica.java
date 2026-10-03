/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistemadebecas.modelo;
import java.time.LocalDate;

public class BecaMantencionAcademica extends Beca {
    private int puntajePaes ;
    private boolean viviendaValparaiso ;

    public BecaMantencionAcademica(String id, String nombre, String desc, int monto, int paes, boolean vivienda, 
                                    LocalDate inicio, LocalDate cierre, LocalDate rev) {
        super(id, nombre, desc, monto, inicio, cierre, rev) ;
        puntajePaes=paes ;
        viviendaValparaiso=vivienda ;
    }

    // Sobreescritura metodo de Beca evaluarPostulante.
    // Para obtener la beca el Beneficiario debe cumplir con el puntaje Paes requerido
    // y no vivir en la región de Valparaíso
    @Override
    public boolean evaluarPostulante(Beneficiario b) {
        if (b.getPuntajePaes()>=puntajePaes && b.getViviendaValparaiso()!=viviendaValparaiso) {
            return true ;
        }
        return false ;
    }

    public int getPuntajePaes() {return puntajePaes ;} ;
    public boolean getViviendaValparaiso() {return viviendaValparaiso ;}

}
