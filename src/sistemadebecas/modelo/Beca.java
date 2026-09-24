/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package sistemadebecas.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class Beca {
    private String idBeca;
    private String nombreBeca;
    private double montoMensual;
    private int cuposMaximos;
    private List<Beneficiario> listaBeneficiarios; // Colección anidada

    public Beca(String idBeca, String nombreBeca, double montoMensual, int cuposMaximos) {
        this.idBeca = idBeca;
        this.nombreBeca = nombreBeca;
        this.montoMensual = montoMensual;
        this.cuposMaximos = cuposMaximos;
        this.listaBeneficiarios = new ArrayList<>();
    }

    // Cumple SIA-3: Buena práctica para NO retornar colecciones por referencia
    public List<Beneficiario> getListaBeneficiarios() {
        return Collections.unmodifiableList(listaBeneficiarios);
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

    // Cumple SIA-5: Sobrecarga de métodos 1
    public double calcularPresupuestoAnual() {
        return (listaBeneficiarios.size() * montoMensual) * 12;
    }

    public double calcularPresupuestoAnual(int meses) {
        return (listaBeneficiarios.size() * montoMensual) * meses;
    }

    public String getIdBeca() { return idBeca; }
    public String getNombreBeca() { return nombreBeca; }
    public double getMontoMensual() { return montoMensual; }
    public int getCuposMaximos() { return cuposMaximos; }
}
