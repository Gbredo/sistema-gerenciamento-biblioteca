package biblioteca.apresentacao;

import biblioteca.aplicacao.EmprestimoServico;
import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.infraestrutura.adaptador.EmprestimoRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.LivroRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.NotificacaoConsole;
import biblioteca.infraestrutura.adaptador.UsuarioRepositorioMemoria;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        // Composicao: unico lugar onde classes concretas sao mencionadas
        LivroRepositorioMemoria livroRepo = new LivroRepositorioMemoria();
        UsuarioRepositorioMemoria usuarioRepo = new UsuarioRepositorioMemoria();

        PortaEmprestimo servicoCompleto = new EmprestimoServico(
                usuarioRepo,
                livroRepo,
                new EmprestimoRepositorioMemoria(),
                new NotificacaoConsole()
        );

        // --- Dados iniciais ---
        Livro livro = new Livro(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 1);
        livroRepo.salvar(livro);

        Usuario usuario = new Usuario(1L, "Ana Silva", "ana@email.com");
        usuarioRepo.salvar(usuario);

        System.out.println("=== Estado inicial ===");
        System.out.println(livro);
        System.out.println(usuario);

        // --- Realizar emprestimo ---
        System.out.println("\n=== Realizando emprestimo ===");
        Emprestimo emprestimo = servicoCompleto.realizarEmprestimo(1L, 1L);
        System.out.println("Emprestimo criado: " + emprestimo);
        System.out.println("Estoque apos emprestimo: " + livro.getQuantidadeDisponivel());

        // --- Listar emprestimos ativos ---
        System.out.println("\n=== Emprestimos ativos ===");
        List<Emprestimo> ativos = servicoCompleto.listarEmprestimosAtivos();
        ativos.forEach(System.out::println);

        // --- Tentar segundo emprestimo sem estoque ---
        System.out.println("\n=== Tentando emprestimo sem estoque ===");
        try {
            servicoCompleto.realizarEmprestimo(1L, 1L);
        } catch (IllegalStateException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }

        // --- Registrar devolucao ---
        System.out.println("\n=== Registrando devolucao ===");
        servicoCompleto.registrarDevolucao(emprestimo.getId());
        System.out.println("Situacao do emprestimo: " + emprestimo.getSituacao());
        System.out.println("Data de devolucao efetiva: " + emprestimo.getDataDevolucaoEfetiva());
        System.out.println("Estoque apos devolucao: " + livro.getQuantidadeDisponivel());

        // --- Listar emprestimos ativos apos devolucao ---
        System.out.println("\n=== Emprestimos ativos apos devolucao ===");
        List<Emprestimo> ativosApos = servicoCompleto.listarEmprestimosAtivos();
        if (ativosApos.isEmpty()) {
            System.out.println("Nenhum emprestimo ativo.");
        } else {
            ativosApos.forEach(System.out::println);
        }
    }
}
