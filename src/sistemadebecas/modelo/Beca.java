/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

import java.util.*;

public abstract class Beca {
    private String idBeca;
    private String nombreBeca;
    private double montoMensual;
    private int cuposMaximos;
    private List<Beneficiario> listaBeneficiarios;
    private Requisito requisito;

    public Beca(String idBeca, String nombreBeca, double montoMensual, int cuposMaximos, Requisito requisito) {
        this.idBeca = idBeca;
        this.nombreBeca = nombreBeca;
        this.montoMensual = montoMensual;
        this.cuposMaximos = cuposMaximos;
        this.requisito = requisito;
        this.listaBeneficiarios = new ArrayList<>();
    }

    public abstract String getTipoBeca();

    public boolean evaluarPostulante(Beneficiario b) {
        if (requisito != null) {
            return requisito.cumpleRequisitos(b);
        }
        return true;
    }

    public boolean agregarBeneficiario(Beneficiario b) {
        if (listaBeneficiarios.size() < cuposMaximos) {
            return listaBeneficiarios.add(b);
        }
        return false;
    }

    public boolean eliminarBeneficiario(String rut) {
        return listaBeneficiarios.removeIf(b -> b.getRut().equalsIgnoreCase(rut));
    }

    public Beneficiario buscarBeneficiario(String rut) {
        for (Beneficiario b : listaBeneficiarios) {
            if (b.getRut().equalsIgnoreCase(rut)) {
                return b;
            }
        }
        return null;
    }

    public double calcularPresupuestoAnual() {
        return this.montoMensual * 12 * this.listaBeneficiarios.size();
    }

    public double calcularPresupuestoAnual(int meses) {
        return this.montoMensual * meses * this.listaBeneficiarios.size();
    }

    // Getters y Setters
    public String getIdBeca() { return idBeca; }
    public String getNombreBeca() { return nombreBeca; }
    public double getMontoMensual() { return montoMensual; }
    public int getCuposMaximos() { return cuposMaximos; }
    public Requisito getRequisito() { return requisito; }

    public List<Beneficiario> getListaBeneficiarios() {
        return Collections.unmodifiableList(listaBeneficiarios);
    }
}
