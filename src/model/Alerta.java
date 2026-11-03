/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   Clase Alerta - Semana 6

   Alerta emitida por un sensor. La severidad determina que tan
   pronto debe atenderse; el orden de llegada solo sirve para
   desempatar (ver docs/decisiones.md, Semana 6).
   ============================================================ */

package model;

public class Alerta {

    /** El orden del enum es el orden de prioridad: CRITICA es la mayor. */
    public enum Severidad {
        INFORMATIVA, ADVERTENCIA, CRITICA
    }

    private final String idSensor;
    private final Severidad severidad;
    private final String descripcion;

    public Alerta(String idSensor, Severidad severidad, String descripcion) {
        this.idSensor = idSensor;
        this.severidad = severidad;
        this.descripcion = descripcion;
    }

    public String getIdSensor() { return idSensor; }
    public Severidad getSeveridad() { return severidad; }
    public String getDescripcion() { return descripcion; }

    @Override
    public String toString() {
        return "[" + severidad + "] " + idSensor + " - " + descripcion;
    }
}
