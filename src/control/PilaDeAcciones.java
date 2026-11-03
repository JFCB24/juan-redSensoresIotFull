/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   PilaDeAcciones - Semana 6

   Pila LIFO para deshacer las acciones del operador. La ultima
   accion realizada es la primera que se revierte (Ctrl+Z).
   ============================================================ */

package control;

import java.util.Arrays;

public class PilaDeAcciones<T> {

    private Object[] datos = new Object[8];
    private int cantidad;

    /** Registra una accion encima de la pila. */
    public void apilar(T accion) {
        if (cantidad == datos.length) {
            datos = Arrays.copyOf(datos, datos.length * 2);
        }
        datos[cantidad++] = accion;
    }

    /**
     * Devuelve la accion en la cima sin quitarla.
     * @return la ultima accion apilada, o null si la pila esta vacia
     */
    @SuppressWarnings("unchecked")
    public T cima() {
        if (cantidad == 0) return null;
        return (T) datos[cantidad - 1];
    }

    /**
     * Quita y devuelve la accion en la cima.
     * @return la accion quitada, o null si la pila esta vacia
     */
    public T desapilar() {
        T accion = cima();
        if (accion != null) {
            datos[--cantidad] = null; // no retener referencias sueltas
        }
        return accion;
    }

    public int tamano() { return cantidad; }

    public boolean estaVacia() { return cantidad == 0; }
}
