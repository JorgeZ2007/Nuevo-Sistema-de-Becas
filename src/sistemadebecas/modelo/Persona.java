package sistemadebecas.modelo;

public class Persona {
    private String nombre;
    private String rut;

    public Persona() {
    }

    public Persona(String nombre, String rut) {
        this.nombre = nombre;
        this.rut = rut;
    }

    public String obtenerDetalleFormateado() {
        return "RUT: " + rut + " | Nombre: " + nombre ;
    }

    public String getNombre () { 
        return nombre; 
    }
    public String getRut () {
        return rut; }
    }
