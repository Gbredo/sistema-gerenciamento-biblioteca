package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class EmprestimoRepositorioMemoria implements PortaEmprestimoRepositorio {

    private final Map<Long, Emprestimo> bancoDeDados = new HashMap<>();

    @Override
    public void salvar(Emprestimo emprestimo) {
        bancoDeDados.put(emprestimo.getId(), emprestimo);
    }

    @Override
    public Optional<Emprestimo> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    @Override
    public List<Emprestimo> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    @Override
    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
