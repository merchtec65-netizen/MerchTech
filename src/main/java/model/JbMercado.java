package model;

import java.sql.Date;

public class JbMercado {

    private int id;
    private String nome;
    private String cidade;
    private String bairro;
    private String rua;
    private String numero;
    private String endereco;
    private String foto;
    private boolean ativo;
    private Date criado_em;
    private Date atualizado_em;

    public JbMercado() {}

    public JbMercado(int id, String nome, String cidade, String bairro, String rua,
                     String numero, String endereco, String foto, boolean ativo,
                     Date criado_em, Date atualizado_em) {

        this.id = id;
        this.nome = nome;
        this.cidade = cidade;
        this.bairro = bairro;
        this.rua = rua;
        this.numero = numero;
        this.endereco = endereco;
        this.foto = foto;
        this.ativo = ativo;
        this.criado_em = criado_em;
        this.atualizado_em = atualizado_em;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getNome() {
        return nome;
    }

    public void setNome(String nome) {
        this.nome = nome;
    }

    public String getCidade() {
        return cidade;
    }

    public void setCidade(String cidade) {
        this.cidade = cidade;
    }

    public String getBairro() {
        return bairro;
    }

    public void setBairro(String bairro) {
        this.bairro = bairro;
    }

    public String getRua() {
        return rua;
    }

    public void setRua(String rua) {
        this.rua = rua;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public String getEndereco() {
        return endereco;
    }

    public void setEndereco(String endereco) {
        this.endereco = endereco;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String Foto) {
        this.foto = Foto;
    }

    public boolean isAtivo() {
        return ativo;
    }

    public void setAtivo(boolean ativo) {
        this.ativo = ativo;
    }

    public Date getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(Date criado_em) {
        this.criado_em = criado_em;
    }

    public Date getAtualizado_em() {
        return atualizado_em;
    }

    public void setAtualizado_em(Date atualizado_em) {
        this.atualizado_em = atualizado_em;
    }
}