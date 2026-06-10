package biblioteca.infraestrutura.adaptador;

import biblioteca.dominio.Livro;
import biblioteca.dominio.porta.saida.PortaLivroRepositorio;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class LivroRepositorioCsv implements PortaLivroRepositorio {

    // Formato: id,titulo,autor,isbn,quantidadeDisponivel
    private static final String SEPARADOR = ",";

    private final Path arquivo;

    public LivroRepositorioCsv(String nomeArquivo) {
        this.arquivo = Paths.get(nomeArquivo);
        if (!Files.exists(arquivo)) {
            try {
                Files.createFile(arquivo);
            } catch (IOException e) {
                throw new UncheckedIOException("Nao foi possivel criar o arquivo CSV: " + nomeArquivo, e);
            }
        }
    }

    @Override
    public void salvar(Livro livro) {
        List<Livro> todos = listarTodos();

        boolean atualizado = false;
        for (int i = 0; i < todos.size(); i++) {
            if (todos.get(i).getId().equals(livro.getId())) {
                todos.set(i, livro);
                atualizado = true;
                break;
            }
        }
        if (!atualizado) {
            todos.add(livro);
        }

        persistir(todos);
    }

    @Override
    public Optional<Livro> buscarPorId(Long id) {
        return listarTodos().stream()
                .filter(l -> l.getId().equals(id))
                .findFirst();
    }

    @Override
    public List<Livro> listarTodos() {
        try {
            List<String> linhas = Files.readAllLines(arquivo, StandardCharsets.UTF_8);
            List<Livro> livros = new ArrayList<>();
            for (String linha : linhas) {
                if (!linha.isBlank()) {
                    livros.add(deserializar(linha));
                }
            }
            return livros;
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao ler o arquivo CSV: " + arquivo, e);
        }
    }

    @Override
    public void remover(Long id) {
        List<Livro> todos = listarTodos();
        todos.removeIf(l -> l.getId().equals(id));
        persistir(todos);
    }

    // --- helpers privados ---

    private void persistir(List<Livro> livros) {
        List<String> linhas = new ArrayList<>();
        for (Livro l : livros) {
            linhas.add(serializar(l));
        }
        try {
            Files.write(arquivo, linhas, StandardCharsets.UTF_8);
        } catch (IOException e) {
            throw new UncheckedIOException("Erro ao escrever no arquivo CSV: " + arquivo, e);
        }
    }

    private String serializar(Livro livro) {
        // Escapa vírgulas dentro dos campos envolvendo-os em aspas duplas
        return escapar(String.valueOf(livro.getId())) + SEPARADOR +
               escapar(livro.getTitulo())              + SEPARADOR +
               escapar(livro.getAutor())               + SEPARADOR +
               escapar(livro.getIsbn())                + SEPARADOR +
               escapar(String.valueOf(livro.getQuantidadeDisponivel()));
    }

    private Livro deserializar(String linha) {
        String[] campos = dividir(linha);
        if (campos.length != 5) {
            throw new IllegalArgumentException("Linha CSV invalida: " + linha);
        }
        Long id                  = Long.parseLong(campos[0].trim());
        String titulo            = campos[1].trim();
        String autor             = campos[2].trim();
        String isbn              = campos[3].trim();
        int quantidadeDisponivel = Integer.parseInt(campos[4].trim());
        return new Livro(id, titulo, autor, isbn, quantidadeDisponivel);
    }

    // Envolve em aspas duplas se o valor contiver vírgula ou aspas
    private String escapar(String valor) {
        if (valor.contains(SEPARADOR) || valor.contains("\"")) {
            return "\"" + valor.replace("\"", "\"\"") + "\"";
        }
        return valor;
    }

    // Divide respeitando campos entre aspas duplas (CSV simples, sem quebras de linha internas)
    private String[] dividir(String linha) {
        List<String> campos = new ArrayList<>();
        StringBuilder campo = new StringBuilder();
        boolean dentroDeAspas = false;

        for (int i = 0; i < linha.length(); i++) {
            char c = linha.charAt(i);

            if (dentroDeAspas) {
                if (c == '"') {
                    // Aspas duplas escapadas dentro de um campo ("") → uma aspa literal
                    if (i + 1 < linha.length() && linha.charAt(i + 1) == '"') {
                        campo.append('"');
                        i++;
                    } else {
                        dentroDeAspas = false;
                    }
                } else {
                    campo.append(c);
                }
            } else {
                if (c == '"') {
                    dentroDeAspas = true;
                } else if (c == ',') {
                    campos.add(campo.toString());
                    campo.setLength(0);
                } else {
                    campo.append(c);
                }
            }
        }
        campos.add(campo.toString());
        return campos.toArray(new String[0]);
    }
}
