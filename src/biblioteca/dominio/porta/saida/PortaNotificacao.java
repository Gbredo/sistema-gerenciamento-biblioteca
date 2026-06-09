package biblioteca.dominio.porta.saida;

import biblioteca.dominio.Emprestimo;
import biblioteca.dominio.Usuario;

public interface PortaNotificacao {

    void notificarAtraso(Usuario usuario, Emprestimo emprestimo);
}
