package biblioteca.apresentacao;

import biblioteca.aplicacao.EmprestimoServico;
import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.Usuario;

import biblioteca.infraestrutura.EmprestimoRepositorio;
import biblioteca.infraestrutura.LivroRepositorio;
import biblioteca.infraestrutura.UsuarioRepositorio;
import java.util.List;

public class Main {

    public static void main(String[] args) {
        LivroRepositorio livroRepositorio = new LivroRepositorio();
        UsuarioRepositorio usuarioRepositorio = new UsuarioRepositorio();
        EmprestimoRepositorio emprestimoRepositorio = new EmprestimoRepositorio();

        EmprestimoServico servico = new EmprestimoServico(usuarioRepositorio, livroRepositorio, emprestimoRepositorio);

        // --- Dados iniciais ---
        Livro livro = new Livro(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 1);
        livroRepositorio.salvar(livro);

        Usuario usuario = new Usuario(1L, "Ana Silva", "ana@email.com");
        usuarioRepositorio.salvar(usuario);

        System.out.println("=== Estado inicial ===");
        System.out.println(livro);
        System.out.println(usuario);

        // --- Realizar empréstimo ---
        System.out.println("\n=== Realizando empréstimo ===");
        Emprestimo emprestimo = servico.realizarEmprestimo(1L, 1L);
        System.out.println("Empréstimo criado: " + emprestimo);
        System.out.println("Estoque após empréstimo: " + livro.getQuantidadeDisponivel());

        // --- Listar empréstimos ativos ---
        System.out.println("\n=== Empréstimos ativos ===");
        List<Emprestimo> ativos = servico.listarEmprestimosAtivos();
        ativos.forEach(System.out::println);

        // --- Tentar segundo empréstimo sem estoque ---
        System.out.println("\n=== Tentando empréstimo sem estoque ===");
        try {
            servico.realizarEmprestimo(1L, 1L);
        } catch (IllegalStateException e) {
            System.out.println("Erro esperado: " + e.getMessage());
        }

        // --- Registrar devolução ---
        System.out.println("\n=== Registrando devolução ===");
        servico.registrarDevolucao(emprestimo.getId());
        System.out.println("Situação do empréstimo: " + emprestimo.getSituacao());
        System.out.println("Data de devolução efetiva: " + emprestimo.getDataDevolucaoEfetiva());
        System.out.println("Estoque após devolução: " + livro.getQuantidadeDisponivel());

        // --- Listar empréstimos ativos após devolução ---
        System.out.println("\n=== Empréstimos ativos após devolução ===");
        List<Emprestimo> ativosApos = servico.listarEmprestimosAtivos();
        if (ativosApos.isEmpty()) {
            System.out.println("Nenhum empréstimo ativo.");
        } else {
            ativosApos.forEach(System.out::println);
        }
    }
}
