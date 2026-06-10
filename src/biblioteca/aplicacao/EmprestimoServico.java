package biblioteca.aplicacao;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.SituacaoEmprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
import biblioteca.dominio.porta.saida.PortaNotificacao;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;

import java.time.LocalDate;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

public class EmprestimoServico implements PortaEmprestimo {

    private static final int PRAZO_PADRAO_DIAS = 14;

    private final PortaUsuarioRepositorio usuarioRepositorio;
    private final PortaLivroRepositorio livroRepositorio;
    private final PortaEmprestimoRepositorio emprestimoRepositorio;
    private final PortaNotificacao notificacao;
    private final AtomicLong contadorId = new AtomicLong(1);

    public EmprestimoServico(PortaUsuarioRepositorio usuarioRepositorio,
                             PortaLivroRepositorio livroRepositorio,
                             PortaEmprestimoRepositorio emprestimoRepositorio,
                             PortaNotificacao notificacao) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.livroRepositorio = livroRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.notificacao = notificacao;
    }

    @Override
    public Emprestimo realizarEmprestimo(Long usuarioId, Long livroId) {
        Usuario usuario = usuarioRepositorio.buscarPorId(usuarioId)
                .orElseThrow(() -> new IllegalArgumentException("Usuario nao encontrado: " + usuarioId));

        if (!usuario.estaAtivo()) {
            throw new IllegalStateException("Usuario suspenso nao pode realizar emprestimos: " + usuario.getNome());
        }

        Livro livro = livroRepositorio.buscarPorId(livroId)
                .orElseThrow(() -> new IllegalArgumentException("Livro nao encontrado: " + livroId));

        livro.realizarEmprestimo();

        LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(PRAZO_PADRAO_DIAS);
        Emprestimo emprestimo = new Emprestimo(contadorId.getAndIncrement(), usuario, livro, dataDevolucaoPrevista);

        emprestimoRepositorio.salvar(emprestimo);
        return emprestimo;
    }

    @Override
    public void registrarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepositorio.buscarPorId(emprestimoId)
                .orElseThrow(() -> new IllegalArgumentException("Emprestimo nao encontrado: " + emprestimoId));

        emprestimo.registrarDevolucao();
        emprestimoRepositorio.salvar(emprestimo);
    }

    @Override
    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATIVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Emprestimo> verificarAtrasos() {
        List<Emprestimo> atrasados = listarEmprestimosAtivos().stream()
                .peek(Emprestimo::verificarAtraso)
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATRASADO)
                .collect(Collectors.toList());

        atrasados.forEach(e -> notificacao.notificarAtraso(e.getUsuario(), e));
        return atrasados;
    }
}
