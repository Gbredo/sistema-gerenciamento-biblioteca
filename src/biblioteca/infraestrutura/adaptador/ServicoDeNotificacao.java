package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.evento.EmprestimoRealizadoEvento;

import java.util.function.Consumer;

public class ServicoDeNotificacao implements Consumer<EmprestimoRealizadoEvento> {

    @Override
    public void accept(EmprestimoRealizadoEvento evento) {
        System.out.println("[NOTIFICACAO] Emprestimo #" + evento.emprestimoId() +
                           " confirmado para o usuario #" + evento.usuarioId() +
                           ". Devolucao prevista para: " + evento.dataRetirada().plusDays(14));
    }
}
