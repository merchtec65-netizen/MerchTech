package model;

import java.sql.Date;

public class JbUsuario {
    private int id;
    private String username;
    private String email;
    private String senha;
    private String nome;
    private boolean isAtivo;
    private String role;       // "promotor" ou "supervisor"
    private Date criado_em;
    private Date atualizado_em;

    public JbUsuario() { super(); }

    public JbUsuario(int id, String username, String email, String senha, String nome,
                     boolean isAtivo, String role, Date criado_em, Date atualizado_em) {
        this.id            = id;
        this.username      = username;
        this.email         = email;
        this.senha         = senha;
        this.nome          = nome;
        this.isAtivo       = isAtivo;
        this.role          = role;
        this.criado_em     = criado_em;
        this.atualizado_em = atualizado_em;
    }

    public int getId()                       { return id; }
    public void setId(int id)                { this.id = id; }

    public String getUsername()              { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getEmail()                 { return email; }
    public void setEmail(String email)       { this.email = email; }

    public String getSenha()                 { return senha; }
    public void setSenha(String senha)       { this.senha = senha; }

    public String getNome()                  { return nome; }
    public void setNome(String nome)         { this.nome = nome; }

    public boolean isAtivo()                 { return isAtivo; }
    public void setAtivo(boolean isAtivo)    { this.isAtivo = isAtivo; }

    public String getRole()                  { return role; }
    public void setRole(String role)         { this.role = role; }

    public Date getCriado_em()               { return criado_em; }
    public void setCriado_em(Date criado_em) { this.criado_em = criado_em; }

    public Date getAtualizado_em()                     { return atualizado_em; }
    public void setAtualizado_em(Date atualizado_em)   { this.atualizado_em = atualizado_em; }
}