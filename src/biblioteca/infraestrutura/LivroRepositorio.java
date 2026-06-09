package biblioteca.infraestrutura;

import biblioteca.dominio.Livro;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorio {

    private final Map<Long, Livro> bancoDeDados = new HashMap<>();

    public void salvar(Livro livro) {
        bancoDeDados.put(livro.getId(), livro);
    }

    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    public List<Livro> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
