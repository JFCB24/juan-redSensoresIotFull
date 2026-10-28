# Decisiones de diseño — Semana 3

## 1. Punto de entrada

El proyecto mantiene un único punto de entrada:

```text
IngestaSensores.main()
```

No se crean aplicaciones independientes por semana.

`BancoDePruebas` es una clase auxiliar y no contiene `main`.

## 2. Búsqueda por timestamp

Se utilizan dos estrategias:

- Búsqueda lineal: no requiere ordenamiento.
- Búsqueda binaria: requiere que el arreglo esté ordenado por timestamp.

Los datos sintéticos de `GeneradorDatos` se generan en orden cronológico, por lo que la búsqueda binaria por timestamp cumple su precondición.

## 3. Búsqueda por PM2.5

No se asume que los datos estén ordenados por PM2.5.

Por tanto, la búsqueda binaria por PM2.5 se conserva como experimento para demostrar el efecto de una precondición incumplida.

## 4. Comparación de String

Los identificadores de estación se comparan mediante:

```java
equals()
```

y no mediante:

```java
==
```

porque se necesita comparar contenido.

## 5. Medición

La comparación principal entre algoritmos utiliza el número de comparaciones.

El tiempo en milisegundos se conserva como evidencia experimental, pero no es la única medida utilizada.

## 6. Evolución del proyecto

La Semana 3 agrega una nueva capacidad a la misma plataforma:

```text
Sensores
   ↓
Ingesta
   ↓
Repositorio
   ↓
Búsqueda
   ↓
Medición de eficiencia
```

La Semana 4 podrá extender esta misma arquitectura para estudiar ordenamiento.

---

# Decisiones de diseño — Semana 5 (listas enlazadas)

## 7. Lista enlazada y contrato `Repositorio`

`RepositorioEnlazado` implementa la misma interfaz `Repositorio` que `RepositorioArreglo`. El ciclo de `BancoDeListas` (experimento 1) no sabe qué implementación usa, lo que permite cambiar de mecanismo sin reescribir el código que consume los datos.

## 8. Atributo `ultimo`

Se mantiene un enlace al último nodo para que `agregarAlFinal` sea O(1). Hay que actualizarlo en tres casos: lista vacía, eliminación del último nodo y eliminación del único nodo (queda en `null`).

## 9. Eliminación: puentear, no cortar

Al eliminar un nodo del medio, el anterior se enlaza directamente con el siguiente del nodo eliminado. Asignar `null` cortaría la cadena y dejaría inalcanzables los nodos posteriores.

## 10. Consistencia entre `tamano()` y los nodos reales

`tamano()` devuelve el contador `cantidad`. Para verificarlo se usa `contarRecorriendo()`, que cuenta los nodos siguiendo los enlaces. Las pruebas comparan ambos valores después de cada operación relevante.

## 11. Lista doblemente enlazada (`HistorialNavegable`)

Cada `NodoDoble` tiene `siguiente` y `anterior`. Al agregar al final se enlazan ambos sentidos. La navegación hacia atrás usa `anterior`, y `recorrerHaciaAtras()` permite verificar que el enlace inverso visita todos los nodos.

## 12. Medición de la lista enlazada

Resultados medidos en esta máquina (no son los valores de referencia de la guía):

| Experimento | Resultado medido |
|-------------|------------------|
| 3. Insertar al inicio, 100.000 lecturas | Arreglo: 4.999.950.000 movimientos. Lista: 0 saltos. |
| 4. Recorrer con `obtener(i)`, 50.000 lecturas | Arreglo: 50.000 accesos. Lista: 1.249.975.000 saltos. |
| 5. Búsqueda sobre la cadena, 50.000 lecturas | Lineal: 50.000 saltos. Binaria: 749.990 saltos (16 comparaciones). |

Conclusión: insertar al inicio es constante en la lista y O(n) en el arreglo. Acceder por posición en la lista cuesta O(n) por llamada, de modo que un ciclo `obtener(i)` es O(n²). La búsqueda binaria reduce las comparaciones, pero cada punto medio cuesta recorrer desde el inicio; por eso en una lista no conviene.

## 13. Qué estructura elegir

- Predominan inserciones al inicio: lista enlazada, porque insertar cuesta O(1).
- Predominan consultas por posición: arreglo, porque el acceso es directo.
