package biblioteca.dominio.evento;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class EventBus<T> {

    private final List<Consumer<T>> assinantes = new ArrayList<>();

    public void assinar(Consumer<T> handler) {
        assinantes.add(handler);
    }

    public void publicar(T evento) {
        assinantes.forEach(handler -> handler.accept(evento));
    }
}
