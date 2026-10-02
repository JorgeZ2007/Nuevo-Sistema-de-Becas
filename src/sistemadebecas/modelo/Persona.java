package sistemadebecas.modelo;

public class Persona {
    protected String nombre;
    protected String rut;

    public Persona() {
    }

    public Persona(String nombre, String rut) {
        this.nombre = nombre;
        this.rut = rut;
    }

    public String obtenerDetalleFormateado() {
        return "RUT: " + rut + " | Nombre: " + nombre + " | F.Nac: " + fechaNacimiento + " | Género: " + genero;
    }

    public String getNombre() { return nombre; }
    public String getRut() { return rut; }
}
