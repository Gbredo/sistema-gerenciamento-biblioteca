package biblioteca.infraestrutura;

import biblioteca.dominio.Emprestimo;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmprestimoRepositorio {

    private final Map<Long, Emprestimo> bancoDeDados = new HashMap<>();

    public void salvar(Emprestimo emprestimo) {
        bancoDeDados.put(emprestimo.getId(), emprestimo);
    }

    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
