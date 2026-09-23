public class Beca {
    protected String nombre;
    private String descr ;
    protected double montoBase;

    public Beca(String nombre, String descr,double montoBase) {
        this.nombre = nombre;
        this.descr=descr ;
        this.montoBase = montoBase;
    }

    public String getNombre() {
        return nombre;
    }

    public double getMontoBase() {
        return montoBase;
    }

    public double calcularMontoFinal() {
        return this.montoBase;
    }

    public void mostrarInformacion() {
        System.out.println("Beca: " + nombre + " | Monto Base: $" + montoBase);
    }
}