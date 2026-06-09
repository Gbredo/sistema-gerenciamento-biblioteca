package biblioteca.infraestrutura;

import biblioteca.dominio.Usuario;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositorio {

    private final Map<Long, Usuario> bancoDeDados = new HashMap<>();

    public void salvar(Usuario usuario) {
        bancoDeDados.put(usuario.getId(), usuario);
    }

    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    public List<Usuario> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
