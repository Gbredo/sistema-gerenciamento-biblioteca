package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Livro;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class LivroRepositorioMemoria implements PortaLivroRepositorio {

    private final Map<Long, Livro> bancoDeDados = new HashMap<>();

    @Override
    public void salvar(Livro livro) {
        bancoDeDados.put(livro.getId(), livro);
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    @Override
    public List<Livro> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    @Override
    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
