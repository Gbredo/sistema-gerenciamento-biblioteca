package biblioteca.dominio.evento;

import java.time.LocalDate;

public record EmprestimoRealizadoEvento(
        Long emprestimoId,
        Long usuarioId,
        Long livroId,
        LocalDate dataRetirada
) {}
