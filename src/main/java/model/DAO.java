package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

public class DAO {

    private static final String URL =
        "jdbc:firebirdsql://localhost:3050/C:/Users/mps12/Documents/Projeto/MerchTech/Database/MEUBANCO.FDB?encoding=UTF8&sessionTimeZone=America/Cuiaba";

    private static final String DRIVER = "org.firebirdsql.jdbc.FBDriver";
    private static final String USER = "SYSDBA";
    private static final String PASSWORD = "masterkey";

    // ─────────────────────────────────────────────
    // CONEXÃO
    // ─────────────────────────────────────────────
    private Connection conectar() {
        try {
            Class.forName(DRIVER);
            return DriverManager.getConnection(URL, USER, PASSWORD);
        } catch (Exception e) {
            System.out.println("Erro na conexão:");
            e.printStackTrace();
            return null;
        }
    }

    public void testeConexao() {
        try (Connection con = conectar()) {
            System.out.println(con != null ? "Conexão OK" : "Falha na conexão");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    // ─────────────────────────────────────────────
    // MERCADOS
    // ─────────────────────────────────────────────
    public List<JbMercado> listarMercados() {

        List<JbMercado> lista = new ArrayList<>();

        String sql =
            "SELECT id, nome, cidade, bairro, rua, numero, endereco, ativo, criado_em, atualizado_em " +
            "FROM mercado ORDER BY nome";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            if (con == null) return lista;

            while (rs.next()) {

                JbMercado m = new JbMercado();

                m.setId(rs.getInt("id"));
                m.setNome(rs.getString("nome"));
                m.setCidade(rs.getString("cidade"));
                m.setBairro(rs.getString("bairro"));
                m.setRua(rs.getString("rua"));
                m.setNumero(rs.getString("numero"));
                m.setEndereco(rs.getString("endereco"));
                m.setAtivo(rs.getBoolean("ativo"));

                if (rs.getDate("criado_em") != null)
                    m.setCriado_em(rs.getDate("criado_em"));

                if (rs.getDate("atualizado_em") != null)
                    m.setAtualizado_em(rs.getDate("atualizado_em"));

                lista.add(m);
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar mercados:");
            e.printStackTrace();
        }

        return lista;
    }

    // ─────────────────────────────────────────────
    // AUTENTICAÇÃO
    // ─────────────────────────────────────────────
    public JbUsuario autenticar(String username, String senha) {

        String sql =
            "SELECT id, username, email, senha, nome_completo, ativo, role, criado_em, atualizado_em " +
            "FROM USUARIOS WHERE UPPER(username) = UPPER(?) AND ativo = true";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {

                if (rs.next()) {

                    String senhaBanco = rs.getString("senha");

                    if (senha.equals(senhaBanco)) {

                        JbUsuario u = new JbUsuario();

                        u.setId(rs.getInt("id"));
                        u.setUsername(rs.getString("username"));
                        u.setEmail(rs.getString("email"));
                        u.setNome(rs.getString("nome_completo"));
                        u.setAtivo(rs.getBoolean("ativo"));
                        u.setRole(rs.getString("role"));
                        u.setCriado_em(rs.getDate("criado_em"));
                        u.setAtualizado_em(rs.getDate("atualizado_em"));

                        return u;
                    }
                }
            }

        } catch (Exception e) {
            System.out.println("Erro no login:");
            e.printStackTrace();
        }

        return null;
    }

    // ─────────────────────────────────────────────
    // USUÁRIOS POR ROLE
    // ─────────────────────────────────────────────
    public List<JbUsuario> listarUsuariosPorRole(String role) {

        List<JbUsuario> lista = new ArrayList<>();

        String sql =
            "SELECT id, username, email, nome_completo, ativo, role, criado_em, atualizado_em " +
            "FROM USUARIOS WHERE role = ? ORDER BY nome_completo";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, role);

            try (ResultSet rs = pst.executeQuery()) {

                while (rs.next()) {

                    JbUsuario u = new JbUsuario();

                    u.setId(rs.getInt("id"));
                    u.setUsername(rs.getString("username"));
                    u.setEmail(rs.getString("email"));
                    u.setNome(rs.getString("nome_completo"));
                    u.setAtivo(rs.getBoolean("ativo"));
                    u.setRole(rs.getString("role"));
                    u.setCriado_em(rs.getDate("criado_em"));
                    u.setAtualizado_em(rs.getDate("atualizado_em"));

                    lista.add(u);
                }
            }

        } catch (Exception e) {
            System.out.println("Erro ao listar usuários:");
            e.printStackTrace();
        }

        return lista;
    }

    // ─────────────────────────────────────────────
    // CHECAR USERNAME
    // ─────────────────────────────────────────────
    public boolean usernameExiste(String username) {

        String sql = "SELECT 1 FROM USUARIOS WHERE username = ?";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, username);

            try (ResultSet rs = pst.executeQuery()) {
                return rs.next();
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }

    // ─────────────────────────────────────────────
    // CADASTRAR USUÁRIO
    // ─────────────────────────────────────────────
    public boolean cadastrarUsuario(JbUsuario u) {

        String sql =
            "INSERT INTO USUARIOS (username, email, senha, nome_completo, ativo, role, criado_em, atualizado_em) " +
            "VALUES (?, ?, ?, ?, true, ?, CURRENT_DATE, CURRENT_DATE)";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setString(1, u.getUsername());
            pst.setString(2, u.getEmail());
            pst.setString(3, u.getSenha());
            pst.setString(4, u.getNome());
            pst.setString(5, u.getRole());

            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Erro ao cadastrar usuário:");
            e.printStackTrace();
        }

        return false;
    }
}