/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

public class BecaAcademica extends Beca {
    private double excelenciaExigida;

    public BecaAcademica(String idBeca, String nombreBeca, double montoMensual, int cuposMaximos, double promedioMinimo) {
        super(idBeca, nombreBeca, montoMensual, cuposMaximos, new Requisito(promedioMinimo, 5));
        this.excelenciaExigida = promedioMinimo;
    }

    @Override
    public String getTipoBeca() {
        return "Académica (Exigencia Promedio: " + excelenciaExigida + ")";
    }
}
