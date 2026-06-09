package biblioteca.dominio.porta.entrada;

import biblioteca.dominio.Emprestimo;

import java.util.List;

public interface PortaEmprestimo {

    Emprestimo realizarEmprestimo(Long usuarioId, Long livroId);

    void registrarDevolucao(Long emprestimoId);

    List<Emprestimo> listarEmprestimosAtivos();

    List<Emprestimo> verificarAtrasos();
}
