package model;

import java.util.Date;

public class JbRegistro {

    private int id;

    private int mercadoId;

    private int usuarioId;

    private String empresa;

    private String produto;

    private String foto;

    private String hora;

    private String nomeUsuario;

    private Date criado_em;

    private boolean visivel = true;

    public boolean isVisivel() {
        return visivel;
    }

    public void setVisivel(boolean visivel) {
        this.visivel = visivel;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getMercadoId() {
        return mercadoId;
    }

    public void setMercadoId(int mercadoId) {
        this.mercadoId = mercadoId;
    }

    public int getUsuarioId() {
        return usuarioId;
    }

    public void setUsuarioId(int usuarioId) {
        this.usuarioId = usuarioId;
    }

    public String getEmpresa() {
        return empresa;
    }

    public void setEmpresa(String empresa) {
        this.empresa = empresa;
    }

    public String getProduto() {
        return produto;
    }

    public void setProduto(String produto) {
        this.produto = produto;
    }

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public String getNomeUsuario() {
        return nomeUsuario;
    }

    public void setNomeUsuario(String nomeUsuario) {
        this.nomeUsuario = nomeUsuario;
    }

    private String criado_emStr;

    public String getCriado_emStr() {
        return criado_emStr;
    }

    public void setCriado_emStr(String criado_emStr) {
        this.criado_emStr = criado_emStr;
    }
    
    public Date getCriado_em() {
        return criado_em;
    }

    public void setCriado_em(Date criado_em) {
        this.criado_em = criado_em;
    }
}