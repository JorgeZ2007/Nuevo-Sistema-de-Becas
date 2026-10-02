/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistemadebecas.modelo;

public class BecaResidencia extends Beca {
    private double promedioMin ;
    private boolean viviendaValparaiso ;

    public BecaResidencia(String id, String nombre, String desc, int monto, double promedio, boolean vivienda, 
                          LocalDate inicio, LocalDate cierre, LocalDate rev) {
        super(id, nombre, desc, monto, inicio, cierre, rev) ;
        promedioMin=promedio ;
        viviendaValparaiso=vivienda ;
    }

    @Override
    public boolean evaluarPostulante(Beneficiario b) {
        if (b.getPromedioNotas()>=promedioMin && b.getViviendaValparaiso()==viviendaValparaiso) {
            return true ;
        }
        return false ;
    }


    public double getPromedioMin() {return promedioMin ;} ;
    public boolean getViviendaValparaiso() {return ViviendaValparaiso ;}
}
