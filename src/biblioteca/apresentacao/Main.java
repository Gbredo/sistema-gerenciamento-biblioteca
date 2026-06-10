package biblioteca.apresentacao;

import biblioteca.aplicacao.EmprestimoServico;
import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;
import biblioteca.dominio.evento.EventBus;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;
import biblioteca.infraestrutura.adaptador.EmprestimoRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.LivroRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.ServicoDeLog;
import biblioteca.infraestrutura.adaptador.ServicoDeNotificacao;
import biblioteca.infraestrutura.adaptador.UsuarioRepositorioMemoria;

public class Main {

    public static void main(String[] args) {

        // --- Barramentos de eventos ---
        EventBus<EmprestimoRealizadoEvento> busEmprestimo = new EventBus<>();
        EventBus<DevolucaoRegistradaEvento> busDevolucao  = new EventBus<>();

        // --- Consumidores (handlers) ---
        ServicoDeNotificacao notificacao = new ServicoDeNotificacao();
        ServicoDeLog log                 = new ServicoDeLog();

        // --- Assinaturas ---
        busEmprestimo.assinar(notificacao);
        busEmprestimo.assinar(log.aoRealizarEmprestimo());
        busDevolucao.assinar(log.aoRegistrarDevolucao());

        // --- Repositorios e servico ---
        PortaLivroRepositorio livroRepo         = new LivroRepositorioMemoria();
        PortaUsuarioRepositorio usuarioRepo      = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio emprestimoRepo = new EmprestimoRepositorioMemoria();

        PortaEmprestimo servico = new EmprestimoServico(
                usuarioRepo, livroRepo, emprestimoRepo, busEmprestimo, busDevolucao);

        // --- Dados iniciais ---
        Livro livro = new Livro(1L, "Domain-Driven Design", "Eric Evans", "978-0321125217", 2);
        livroRepo.salvar(livro);

        Usuario usuario = new Usuario(1L, "Ana Silva", "ana@email.com");
        usuarioRepo.salvar(usuario);

        // --- Fluxo principal ---
        System.out.println("=== Realizando emprestimo ===");
        Emprestimo emprestimo = servico.realizarEmprestimo(1L, 1L);
        System.out.println("Emprestimo: " + emprestimo);
        System.out.println("Estoque atual: " + livro.getQuantidadeDisponivel());

        System.out.println("\n=== Emprestimos ativos ===");
        servico.listarEmprestimosAtivos().forEach(System.out::println);

        System.out.println("\n=== Registrando devolucao ===");
        servico.registrarDevolucao(emprestimo.getId());
        System.out.println("Situacao: " + emprestimo.getSituacao());
        System.out.println("Estoque restaurado: " + livro.getQuantidadeDisponivel());

        System.out.println("\n=== Emprestimos ativos apos devolucao ===");
        servico.listarEmprestimosAtivos()
               .forEach(System.out::println);
        if (servico.listarEmprestimosAtivos().isEmpty()) {
            System.out.println("Nenhum emprestimo ativo.");
        }

        System.out.println("\n(Verifique o arquivo biblioteca.log para o historico completo)");
    }
}
