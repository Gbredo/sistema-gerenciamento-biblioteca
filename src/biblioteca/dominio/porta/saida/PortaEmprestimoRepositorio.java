package biblioteca.dominio.porta.saida;

import biblioteca.dominio.Emprestimo;

import java.util.List;
import java.util.Optional;

public interface PortaEmprestimoRepositorio {

    void salvar(Emprestimo emprestimo);

    Optional<Emprestimo> buscarPorId(Long id);

    List<Emprestimo> listarTodos();

    void remover(Long id);
}
