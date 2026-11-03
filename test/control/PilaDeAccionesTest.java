import control.PilaDeAcciones;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class PilaDeAccionesTest {

    @Test
    public void testDesapilarDevuelveEnOrdenInverso() {
        PilaDeAcciones<String> pila = new PilaDeAcciones<>();
        pila.apilar("A");
        pila.apilar("B");
        pila.apilar("C");
        assertEquals("C", pila.desapilar());
        assertEquals("B", pila.desapilar());
        assertEquals("A", pila.desapilar());
        assertTrue(pila.estaVacia());
    }

    @Test
    public void testCimaNoQuitaElemento() {
        PilaDeAcciones<String> pila = new PilaDeAcciones<>();
        pila.apilar("X");
        assertEquals("X", pila.cima());
        assertEquals("X", pila.cima());
        assertEquals(1, pila.tamano());
    }

    @Test
    public void testVaciaDevuelveNull() {
        PilaDeAcciones<String> pila = new PilaDeAcciones<>();
        assertNull(pila.cima());
        assertNull(pila.desapilar());
        assertEquals(0, pila.tamano());
    }

    @Test
    public void testCrecePasandoLaCapacidadInicial() {
        PilaDeAcciones<Integer> pila = new PilaDeAcciones<>();
        for (int i = 0; i < 100; i++) {
            pila.apilar(i);
        }
        assertEquals(100, pila.tamano());
        for (int i = 99; i >= 0; i--) {
            assertEquals(i, pila.desapilar());
        }
    }
}
