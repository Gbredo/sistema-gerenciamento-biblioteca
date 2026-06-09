package biblioteca.dominio;

import java.time.LocalDate;

public class Emprestimo {

    private Long id;
    private Usuario usuario;
    private Livro livro;
    private LocalDate dataEmprestimo;
    private LocalDate dataDevolucaoPrevista;
    private LocalDate dataDevolucaoEfetiva;
    private SituacaoEmprestimo situacao;

    public Emprestimo(Long id, Usuario usuario, Livro livro, LocalDate dataDevolucaoPrevista) {
        this.id = id;
        this.usuario = usuario;
        this.livro = livro;
        this.dataEmprestimo = LocalDate.now();
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
        this.situacao = SituacaoEmprestimo.ATIVO;
    }

    public void registrarDevolucao() {
        this.dataDevolucaoEfetiva = LocalDate.now();
        this.situacao = SituacaoEmprestimo.DEVOLVIDO;
        livro.devolverLivro();
    }

    public void verificarAtraso() {
        if (situacao == SituacaoEmprestimo.ATIVO && LocalDate.now().isAfter(dataDevolucaoPrevista)) {
            this.situacao = SituacaoEmprestimo.ATRASADO;
        }
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Usuario getUsuario() {
        return usuario;
    }

    public void setUsuario(Usuario usuario) {
        this.usuario = usuario;
    }

    public Livro getLivro() {
        return livro;
    }

    public void setLivro(Livro livro) {
        this.livro = livro;
    }

    public LocalDate getDataEmprestimo() {
        return dataEmprestimo;
    }

    public LocalDate getDataDevolucaoPrevista() {
        return dataDevolucaoPrevista;
    }

    public void setDataDevolucaoPrevista(LocalDate dataDevolucaoPrevista) {
        this.dataDevolucaoPrevista = dataDevolucaoPrevista;
    }

    public LocalDate getDataDevolucaoEfetiva() {
        return dataDevolucaoEfetiva;
    }

    public SituacaoEmprestimo getSituacao() {
        return situacao;
    }

    public void setSituacao(SituacaoEmprestimo situacao) {
        this.situacao = situacao;
    }

    @Override
    public String toString() {
        return "Emprestimo{id=" + id +
               ", usuario=" + usuario.getNome() +
               ", livro=" + livro.getTitulo() +
               ", dataEmprestimo=" + dataEmprestimo +
               ", dataDevolucaoPrevista=" + dataDevolucaoPrevista +
               ", situacao=" + situacao + '}';
    }
}
