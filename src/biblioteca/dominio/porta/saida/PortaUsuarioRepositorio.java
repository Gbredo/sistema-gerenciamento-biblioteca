package biblioteca.dominio.porta.saida;

import biblioteca.dominio.Usuario;

import java.util.List;
import java.util.Optional;

public interface PortaUsuarioRepositorio {

    void salvar(Usuario usuario);

    Optional<Usuario> buscarPorId(Long id);

    List<Usuario> listarTodos();

    void remover(Long id);
}
