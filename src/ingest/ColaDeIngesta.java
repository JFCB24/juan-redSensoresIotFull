/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   ColaDeIngesta - Semana 6

   Buffer circular FIFO para las lecturas que llegan de la red de
   sensores. Al llegar al final del arreglo, las posiciones liberadas
   al desencolar se reutilizan (aritmetica modular), asi el espacio
   no se agota aunque lleguen muchas mas lecturas que la capacidad
   total acumulada.
   ============================================================ */

package ingest;

import model.LecturaSensor;

public class ColaDeIngesta {

    private final LecturaSensor[] buffer;
    private int frente;      // posicion de la lectura mas antigua
    private int fin;         // posicion donde se escribe la siguiente
    private int cantidad;
    private int descartadas;

    public ColaDeIngesta(int capacidad) {
        if (capacidad <= 0) {
            throw new IllegalArgumentException("La capacidad debe ser positiva: " + capacidad);
        }
        this.buffer = new LecturaSensor[capacidad];
    }

    /**
     * Encola una lectura. Si el buffer esta lleno la descarta y lo cuenta.
     * @return true si se encolo, false si se descarto
     */
    public boolean encolar(LecturaSensor lectura) {
        if (cantidad == buffer.length) {
            descartadas++;
            return false;
        }
        buffer[fin] = lectura;
        fin = (fin + 1) % buffer.length;
        cantidad++;
        return true;
    }

    /**
     * Quita la lectura mas antigua.
     * @return la lectura, o null si la cola esta vacia
     */
    public LecturaSensor desencolar() {
        if (cantidad == 0) return null;
        LecturaSensor lectura = buffer[frente];
        buffer[frente] = null;
        frente = (frente + 1) % buffer.length;
        cantidad--;
        return lectura;
    }

    public int tamano() { return cantidad; }

    public int capacidad() { return buffer.length; }

    public int descartadas() { return descartadas; }

    public boolean estaVacia() { return cantidad == 0; }
}
