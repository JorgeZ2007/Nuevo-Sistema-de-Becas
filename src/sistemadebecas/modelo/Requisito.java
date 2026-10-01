/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor. skibidi sigma ponmi chamba prueba
 */
package sistemadebecas.modelo;

public class Requisito {
    private double promedioMinimo;
    private int quintilMaximo;

    public Requisito(double promedioMinimo, int quintilMaximo) {
        this.promedioMinimo = promedioMinimo;
        this.quintilMaximo = quintilMaximo;
    }

    public boolean cumpleRequisitos(Beneficiario b) {
        return b.getPromedioNotas() >= promedioMinimo && b.getQuintilSocioeconomico() <= quintilMaximo;
    }

    public double getPromedioMinimo() {
        return promedioMinimo;
    }

    public int getQuintilMaximo() {
        return quintilMaximo;
    }
}