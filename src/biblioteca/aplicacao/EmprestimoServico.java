package biblioteca.aplicacao;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.SituacaoEmprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.infraestrutura.EmprestimoRepositorio;
import biblioteca.infraestrutura.LivroRepositorio;

import biblioteca.infraestrutura.UsuarioRepositorio;
import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EmprestimoServico {

    private static final int PRAZO_PADRAO_DIAS = 14;

    private final UsuarioRepositorio usuarioRepositorio;
    private final LivroRepositorio livroRepositorio;
    private final EmprestimoRepositorio emprestimoRepositorio;
    private final AtomicLong contadorId = new AtomicLong(1);

    public EmprestimoServico(UsuarioRepositorio usuarioRepositorio,
                             LivroRepositorio livroRepositorio,
                             EmprestimoRepositorio emprestimoRepositorio) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.livroRepositorio = livroRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
    }

    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado: " + usuarioId));

        if (!usuario.estaAtivo()) {
            throw new IllegalStateException("Usuário suspenso não pode realizar empréstimos: " + usuario.getNome());
        }

        Livro livro = livroRepositorio.buscarPorId(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro não encontrado: " + livroId));

        livro.realizarEmprestimo();

        LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(PRAZO_PADRAO_DIAS);
        Emprestimo emprestimo = new Emprestimo(contadorId.getAndIncrement(), usuario, livro, dataDevolucaoPrevista);

        emprestimoRepositorio.salvar(emprestimo);
        return emprestimo;
    }

    public void registrarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepositorio.buscarPorId(emprestimoId)
                .orElseThrow(() -> new IllegalArgumentException("Empréstimo não encontrado: " + emprestimoId));

        emprestimo.registrarDevolucao();
        emprestimoRepositorio.salvar(emprestimo);
    }

    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATIVO)
                .collect(Collectors.toList());
    }

    public List<Emprestimo> verificarAtrasos() {
        return listarEmprestimosAtivos().stream()
                .peek(Emprestimo::verificarAtraso)
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATRASADO)
                .collect(Collectors.toList());
    }
}
