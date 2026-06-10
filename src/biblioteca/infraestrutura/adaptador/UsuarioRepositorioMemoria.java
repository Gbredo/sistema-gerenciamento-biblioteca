package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Usuario;
import biblioteca.dominio.porta.saida.PortaUsuarioRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class UsuarioRepositorioMemoria implements PortaUsuarioRepositorio {

    private final Map<Long, Usuario> bancoDeDados = new HashMap<>();

    @Override
    public void salvar(Usuario usuario) {
        bancoDeDados.put(usuario.getId(), usuario);
    }

    @Override
    public Optional<Usuario> buscarPorId(Long id) {
        return Optional.ofNullable(bancoDeDados.get(id));
    }

    @Override
    public List<Usuario> listarTodos() {
        return new ArrayList<>(bancoDeDados.values());
    }

    @Override
    public void remover(Long id) {
        bancoDeDados.remove(id);
    }
}
