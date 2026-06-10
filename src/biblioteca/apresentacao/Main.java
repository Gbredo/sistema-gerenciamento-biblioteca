package biblioteca.apresentacao;

import biblioteca.aplicacao.EmprestimoServico;
import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Livro;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.entrada.PortaEmprestimo;
import biblioteca.dominio.porta.saida.PortaEmprestimoRepositorio;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;
import biblioteca.dominio.porta.saida.PortaNotificacao;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;
import biblioteca.infraestrutura.adaptador.EmprestimoRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.LivroRepositorioCsv;
import biblioteca.infraestrutura.adaptador.LivroRepositorioMemoria;
import biblioteca.infraestrutura.adaptador.NotificacaoConsole;
import biblioteca.infraestrutura.adaptador.UsuarioRepositorioMemoria;

public class Main {

    public static void main(String[] args) {

        // ================================================================
        // PARTE 1 — Adaptadores em memoria
        // ================================================================
        System.out.println("================================================");
        System.out.println(" PARTE 1: Adaptadores em memoria");
        System.out.println("================================================");

        PortaLivroRepositorio livroMemoria        = new LivroRepositorioMemoria();
        PortaUsuarioRepositorio usuarioMemoria     = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio empMemoria      = new EmprestimoRepositorioMemoria();
        PortaNotificacao notificacao               = new NotificacaoConsole();

        PortaEmprestimo servicoMemoria = new EmprestimoServico(
                usuarioMemoria, livroMemoria, empMemoria, notificacao);

        Livro livro1 = new Livro(1L, "Clean Code", "Robert C. Martin", "978-0132350884", 2);
        livroMemoria.salvar(livro1);

        Usuario usuario1 = new Usuario(1L, "Ana Silva", "ana@email.com");
        usuarioMemoria.salvar(usuario1);

        System.out.println("\n[Dados salvos em memoria]");
        System.out.println(livro1);
        System.out.println(usuario1);

        Emprestimo emp1 = servicoMemoria.realizarEmprestimo(1L, 1L);
        System.out.println("\n[Emprestimo realizado via adaptador em memoria]");
        System.out.println(emp1);
        System.out.println("Estoque apos emprestimo: " + livro1.getQuantidadeDisponivel());

        System.out.println("\n[Emprestimos ativos]");
        servicoMemoria.listarEmprestimosAtivos().forEach(System.out::println);

        // ================================================================
        // PARTE 2 — Troca para adaptador CSV (sem tocar em regra de negocio)
        // ================================================================
        System.out.println("\n================================================");
        System.out.println(" PARTE 2: Adaptador trocado para CSV (livros.csv)");
        System.out.println("================================================");

        // Apenas a porta do livro e trocada; usuario e emprestimo permanecem em memoria
        PortaLivroRepositorio livroCsv            = new LivroRepositorioCsv("livros.csv");
        PortaUsuarioRepositorio usuarioMemoria2    = new UsuarioRepositorioMemoria();
        PortaEmprestimoRepositorio empMemoria2     = new EmprestimoRepositorioMemoria();

        PortaEmprestimo servicoCsv = new EmprestimoServico(
                usuarioMemoria2, livroCsv, empMemoria2, notificacao);

        Livro livro2 = new Livro(2L, "The Pragmatic Programmer", "Andrew Hunt", "978-0201616224", 3);
        livroCsv.salvar(livro2);
        System.out.println("\n[Livro salvo no CSV]");
        System.out.println(livro2);

        System.out.println("\n[Conteudo do livros.csv apos salvar]");
        livroCsv.listarTodos().forEach(System.out::println);

        Usuario usuario2 = new Usuario(2L, "Carlos Souza", "carlos@email.com");
        usuarioMemoria2.salvar(usuario2);

        Emprestimo emp2 = servicoCsv.realizarEmprestimo(2L, 2L);
        System.out.println("\n[Emprestimo realizado via adaptador CSV]");
        System.out.println(emp2);
        System.out.println("Estoque apos emprestimo (refletido no CSV): " + livro2.getQuantidadeDisponivel());

        System.out.println("\n[Conteudo do livros.csv apos emprestimo decrementar estoque]");
        livroCsv.listarTodos().forEach(System.out::println);

        System.out.println("\n[Emprestimos ativos via servico CSV]");
        servicoCsv.listarEmprestimosAtivos().forEach(System.out::println);
    }
}
