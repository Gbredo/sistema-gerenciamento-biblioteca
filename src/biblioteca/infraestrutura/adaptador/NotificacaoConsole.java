package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.saida.PortaNotificacao;

public class NotificacaoConsole implements PortaNotificacao {

    @Override
    public void notificarAtraso(Usuario usuario, Emprestimo emprestimo) {
        System.out.println("[AVISO] O usuario " + usuario.getNome() +
                           " esta com atraso na devolucao do livro \"" +
                           emprestimo.getLivro().getTitulo() + "\".");
    }
}
