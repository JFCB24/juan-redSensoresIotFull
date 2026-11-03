/* ============================================================
   PLATAFORMA DE MONITOREO AMBIENTAL URBANO
   ColaDeAlertas - Semana 6

   Cola de prioridad para atender alertas. Dos implementaciones
   con la misma interfaz, para comparar su costo:
     - ListaOrdenada: mantiene la lista ordenada al insertar.
     - Monticulo: arreglo como heap binario (cola de prioridad real).

   Criterio de atencion (ver docs/decisiones.md, Semana 6):
     1. Mayor severidad primero (CRITICA > ADVERTENCIA > INFORMATIVA).
     2. Si empatan en severidad, atiende primero la que llego antes (FIFO).
   Ese criterio es un orden total: nunca hay dos alertas "iguales",
   asi que cualquier implementacion produce la misma secuencia.
   ============================================================ */

package control;

import model.Alerta;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public interface ColaDeAlertas {

    void encolar(Alerta alerta);

    /** @return la alerta de mayor prioridad, o null si la cola esta vacia */
    Alerta desencolar();

    int tamano();

    /** Numero de comparaciones entre alertas realizadas hasta ahora. */
    long comparaciones();

    default boolean estaVacia() { return tamano() == 0; }

    /** Alerta con su numero de llegada, para poder desempatar. */
    class Entrada {
        final Alerta alerta;
        final long llegada;

        Entrada(Alerta alerta, long llegada) {
            this.alerta = alerta;
            this.llegada = llegada;
        }
    }

    /** true si a debe atenderse antes que b. Nunca empatan: la llegada es unica. */
    static boolean debeIrAntes(Entrada a, Entrada b) {
        int severidadA = a.alerta.getSeveridad().ordinal();
        int severidadB = b.alerta.getSeveridad().ordinal();
        if (severidadA != severidadB) return severidadA > severidadB;
        return a.llegada < b.llegada;
    }

    /**
     * Lista mantenida ordenada: la alerta de mayor prioridad queda al final,
     * asi desencolar es O(1) y encolar cuesta O(n) por el desplazamiento.
     */
    class ListaOrdenada implements ColaDeAlertas {
        private final List<Entrada> lista = new ArrayList<>();
        private long llegadas;
        private long comparaciones;

        @Override
        public void encolar(Alerta alerta) {
            Entrada nueva = new Entrada(alerta, llegadas++);
            lista.add(nueva);
            int i = lista.size() - 1;
            // Desplaza hacia la derecha las que tienen mayor prioridad que la nueva
            while (i > 0) {
                comparaciones++;
                if (!debeIrAntes(lista.get(i - 1), nueva)) break;
                lista.set(i, lista.get(i - 1));
                i--;
            }
            lista.set(i, nueva);
        }

        @Override
        public Alerta desencolar() {
            if (lista.isEmpty()) return null;
            return lista.remove(lista.size() - 1).alerta;
        }

        @Override
        public int tamano() { return lista.size(); }

        @Override
        public long comparaciones() { return comparaciones; }
    }

    /**
     * Heap binario en arreglo: el padre siempre tiene mayor prioridad que sus hijos.
     * Encolar y desencolar cuestan O(log n).
     */
    class Monticulo implements ColaDeAlertas {
        private Entrada[] heap = new Entrada[16];
        private int cantidad;
        private long llegadas;
        private long comparaciones;

        @Override
        public void encolar(Alerta alerta) {
            if (cantidad == heap.length) {
                heap = Arrays.copyOf(heap, heap.length * 2);
            }
            heap[cantidad] = new Entrada(alerta, llegadas++);
            flotar(cantidad);
            cantidad++;
        }

        /** Sube el elemento en la posicion i mientras supere a su padre. */
        private void flotar(int i) {
            while (i > 0) {
                int padre = (i - 1) / 2;
                comparaciones++;
                if (!debeIrAntes(heap[i], heap[padre])) return;
                intercambiar(i, padre);
                i = padre;
            }
        }

        @Override
        public Alerta desencolar() {
            if (cantidad == 0) return null;
            Alerta raiz = heap[0].alerta;
            cantidad--;
            heap[0] = heap[cantidad];
            heap[cantidad] = null;
            if (cantidad > 0) hundir(0);
            return raiz;
        }

        /** Baja el elemento en la posicion i hasta que ningun hijo lo supere. */
        private void hundir(int i) {
            while (true) {
                int izquierdo = 2 * i + 1;
                int derecho = izquierdo + 1;
                int mejor = i;
                if (izquierdo < cantidad) {
                    comparaciones++;
                    if (debeIrAntes(heap[izquierdo], heap[mejor])) mejor = izquierdo;
                }
                if (derecho < cantidad) {
                    comparaciones++;
                    if (debeIrAntes(heap[derecho], heap[mejor])) mejor = derecho;
                }
                if (mejor == i) return;
                intercambiar(i, mejor);
                i = mejor;
            }
        }

        private void intercambiar(int a, int b) {
            Entrada tmp = heap[a];
            heap[a] = heap[b];
            heap[b] = tmp;
        }

        @Override
        public int tamano() { return cantidad; }

        @Override
        public long comparaciones() { return comparaciones; }
    }
}
