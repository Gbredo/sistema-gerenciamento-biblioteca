package biblioteca.aplicacao;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.SituacaoEmprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;
import biblioteca.dominio.evento.EventBus;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
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
    private final EventBus<EmprestimoRealizadoEvento> busEmprestimo;
    private final EventBus<DevolucaoRegistradaEvento> busDevolucao;
    private final AtomicLong contadorId = new AtomicLong(1);

    public EmprestimoServico(PortaUsuarioRepositorio usuarioRepositorio,
                             PortaLivroRepositorio livroRepositorio,
                             PortaEmprestimoRepositorio emprestimoRepositorio,
                             EventBus<EmprestimoRealizadoEvento> busEmprestimo,
                             EventBus<DevolucaoRegistradaEvento> busDevolucao) {
        this.usuarioRepositorio = usuarioRepositorio;
        this.livroRepositorio = livroRepositorio;
        this.emprestimoRepositorio = emprestimoRepositorio;
        this.busEmprestimo = busEmprestimo;
        this.busDevolucao = busDevolucao;
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
        livroRepositorio.salvar(livro);

        LocalDate dataDevolucaoPrevista = LocalDate.now().plusDays(PRAZO_PADRAO_DIAS);
        Emprestimo emprestimo = new Emprestimo(contadorId.getAndIncrement(), usuario, livro, dataDevolucaoPrevista);
        emprestimoRepositorio.salvar(emprestimo);

        busEmprestimo.publicar(new EmprestimoRealizadoEvento(
                emprestimo.getId(), usuarioId, livroId, emprestimo.getDataEmprestimo()));

        return emprestimo;
    }

    @Override
    public void registrarDevolucao(Long emprestimoId) {
        Emprestimo emprestimo = emprestimoRepositorio.buscarPorId(emprestimoId)
                .orElseThrow(() -> new IllegalArgumentException("Emprestimo nao encontrado: " + emprestimoId));

        emprestimo.registrarDevolucao();
        livroRepositorio.salvar(emprestimo.getLivro());
        emprestimoRepositorio.salvar(emprestimo);

        boolean comAtraso = emprestimo.getDataDevolucaoEfetiva()
                .isAfter(emprestimo.getDataDevolucaoPrevista());

        busDevolucao.publicar(new DevolucaoRegistradaEvento(
                emprestimoId, emprestimo.getDataDevolucaoEfetiva(), comAtraso));
    }

    @Override
    public List<Emprestimo> listarEmprestimosAtivos() {
        return emprestimoRepositorio.listarTodos().stream()
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATIVO)
                .collect(Collectors.toList());
    }

    @Override
    public List<Emprestimo> verificarAtrasos() {
        return listarEmprestimosAtivos().stream()
                .peek(Emprestimo::verificarAtraso)
                .filter(e -> e.getSituacao() == SituacaoEmprestimo.ATRASADO)
                .collect(Collectors.toList());
    }
}
