package biblioteca.dominio.evento;

import java.time.LocalDate;

public record DevolucaoRegistradaEvento(
        Long emprestimoId,
        LocalDate dataDevolucao,
        boolean comAtraso
) {}
