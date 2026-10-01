/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package sistemadebecas.modelo;

public class BecaAlimentacion extends Beca {
    private boolean viviendaValparaiso ;
    private int quintil ;

    public BecaAlimentacion(String id, String nombre, String desc, int monto, boolean vivienda, int q,
                             LocalDate inicio, LocalDate cierre, LocalDate rev) {
        super(id, nombre, desc, monto, inicio, cierre, rev) ;
        viviendaValparaiso=vivienda ;
        quintil=q ;
    }

    @Override
    public boolean evaluarPostulante(Beneficiario b) {
        if (b.getViviendaValparaiso()==viviendaValparaiso && b.getQuintilSocioeconomico()<=quintil) {
            return true ;
        }
        return false ;
    }

}
