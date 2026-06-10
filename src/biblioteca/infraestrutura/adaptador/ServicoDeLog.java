package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.evento.DevolucaoRegistradaEvento;
import biblioteca.dominio.evento.EmprestimoRealizadoEvento;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.function.Consumer;

public class ServicoDeLog {

    private static final Path ARQUIVO_LOG = Paths.get("biblioteca.log");
    private static final DateTimeFormatter FORMATO = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public Consumer<EmprestimoRealizadoEvento> aoRealizarEmprestimo() {
        return evento -> gravar(
                "[EMPRESTIMO] id=" + evento.emprestimoId() +
                " usuario=" + evento.usuarioId() +
                " livro=" + evento.livroId() +
                " retirada=" + evento.dataRetirada()
        );
    }

    public Consumer<DevolucaoRegistradaEvento> aoRegistrarDevolucao() {
        return evento -> gravar(
                "[DEVOLUCAO]  id=" + evento.emprestimoId() +
                " data=" + evento.dataDevolucao() +
                " atraso=" + evento.comAtraso()
        );
    }

    private void gravar(String mensagem) {
        String linha = LocalDateTime.now().format(FORMATO) + " " + mensagem + System.lineSeparator();
        try {
            Files.writeString(ARQUIVO_LOG, linha, StandardCharsets.UTF_8,
                    StandardOpenOption.CREATE, StandardOpenOption.APPEND);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao gravar no log: " + ARQUIVO_LOG, e);
        }
    }
}
