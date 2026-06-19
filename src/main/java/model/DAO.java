package model;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.List;

public class DAO {

    private static final String URL =
        "jdbc:firebirdsql://127.0.0.1:3050/C:/Database/MEUBANCO.FDB";

    private static final String DRIVER   = "org.firebirdsql.jdbc.FBDriver";
    private static final String USER     = "SYSDBA";
    private static final String PASSWORD = "@CHx2021$";

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
    // MERCADOS — listar
    // ─────────────────────────────────────────────
    public List<JbMercado> listarMercados() {
        List<JbMercado> lista = new ArrayList<>();
        String sql =
            "SELECT id, nome, cidade, bairro, rua, numero, endereco, foto, ativo, criado_em, atualizado_em " +
            "FROM mercado ORDER BY nome";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql);
             ResultSet rs = pst.executeQuery()) {

            while (rs.next()) {
                JbMercado m = new JbMercado();
                m.setId(rs.getInt("id"));
                m.setNome(rs.getString("nome"));
                m.setCidade(rs.getString("cidade"));
                m.setBairro(rs.getString("bairro"));
                m.setRua(rs.getString("rua"));
                m.setNumero(rs.getString("numero"));
                m.setEndereco(rs.getString("endereco"));
                m.setFoto(rs.getString("foto"));
                m.setAtivo(rs.getBoolean("ativo"));
                if (rs.getDate("criado_em")     != null) m.setCriado_em(rs.getDate("criado_em"));
                if (rs.getDate("atualizado_em") != null) m.setAtualizado_em(rs.getDate("atualizado_em"));
                lista.add(m);
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar mercados:");
            e.printStackTrace();
        }
        return lista;
    }

 // ─────────────────────────────────────────────
 // MERCADOS — cadastrar
 // ─────────────────────────────────────────────
 public boolean cadastrarMercado(JbMercado mercado) {

     String sql =
         "INSERT INTO MERCADO " +
         "(nome, cidade, bairro, rua, numero, endereco, foto, ativo, criado_em, atualizado_em) " +
         "VALUES (?, ?, ?, ?, ?, ?, ?, true, CURRENT_DATE, CURRENT_DATE)";

     try (Connection con = conectar();
          PreparedStatement pst = con.prepareStatement(sql)) {

         pst.setString(1, mercado.getNome());
         pst.setString(2, mercado.getCidade());
         pst.setString(3, mercado.getBairro());
         pst.setString(4, mercado.getRua());
         pst.setString(5, mercado.getNumero());
         pst.setString(6, mercado.getEndereco());
         pst.setString(7, mercado.getFoto());

         return pst.executeUpdate() > 0;

     } catch (Exception e) {
         System.out.println("Erro ao cadastrar mercado:");
         e.printStackTrace();
     }

     return false;
 }

//─────────────────────────────────────────────
//MERCADOS — remover
//─────────────────────────────────────────────
public boolean removerMercado(int id) {

  String sql = "DELETE FROM MERCADO WHERE id = ?";

  try (Connection con = conectar();
       PreparedStatement pst = con.prepareStatement(sql)) {

      pst.setInt(1, id);

      return pst.executeUpdate() > 0;

  } catch (Exception e) {
      System.out.println("Erro ao remover mercado:");
      e.printStackTrace();
  }

  return false;
}
    // ─────────────────────────────────────────────
    // AUTENTICAÇÃO
    // ─────────────────────────────────────────────
    public JbUsuario autenticar(String username, String senha) {
        String sql =
            "SELECT id, username, email, senha, nome_completo, ativo, role, criado_em, atualizado_em, ultimo_acesso " +
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
                        u.setUltimoAcesso(rs.getTimestamp("ultimo_acesso"));

                        // Login conta como atividade: já marca presença
                        registrarHeartbeat(u.getId());

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
    // HEARTBEAT — marca o usuário como ativo agora
    // ─────────────────────────────────────────────
    public boolean registrarHeartbeat(int usuarioId) {
        String sql = "UPDATE USUARIOS SET ultimo_acesso = CURRENT_TIMESTAMP WHERE id = ?";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, usuarioId);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Erro ao registrar heartbeat:");
            e.printStackTrace();
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // USUÁRIOS POR ROLE
    // ─────────────────────────────────────────────
    public List<JbUsuario> listarUsuariosPorRole(String role) {
        List<JbUsuario> lista = new ArrayList<>();
        String sql =
            "SELECT id, username, email, nome_completo, ativo, role, criado_em, atualizado_em, ultimo_acesso " +
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

                    java.sql.Timestamp ultimoAcesso = rs.getTimestamp("ultimo_acesso");
                    u.setUltimoAcesso(ultimoAcesso);

                    // Online = teve atividade nos últimos 5 minutos
                    boolean online = false;
                    if (ultimoAcesso != null) {
                        long diffMs = System.currentTimeMillis() - ultimoAcesso.getTime();
                        online = diffMs <= 5 * 60 * 1000;
                    }
                    u.setOnline(online);

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

    // ─────────────────────────────────────────────
    // REGISTROS DE VISITA — salvar
    // ─────────────────────────────────────────────
    public boolean salvarRegistro(JbRegistro r) {
        String sql =
            "INSERT INTO REGISTROS (mercado_id, usuario_id, empresa, produto, foto, hora, criado_em, visivel) " +
            "VALUES (?, ?, ?, ?, ?, ?, CURRENT_DATE, true)";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, r.getMercadoId());
            pst.setInt(2, r.getUsuarioId());
            pst.setString(3, r.getEmpresa());
            pst.setString(4, r.getProduto());
            pst.setString(5, r.getFoto());
            pst.setTime(6, java.sql.Time.valueOf(java.time.LocalTime.now()));

            return pst.executeUpdate() > 0;
        } catch (Exception e) {
            System.out.println("Erro ao salvar registro:");
            e.printStackTrace();
        }
        return false;
    }

    // ─────────────────────────────────────────────
    // REGISTROS DE VISITA — listar por mercado
    // (com filtros opcionais de data e empresa/marca)
    // ─────────────────────────────────────────────
    public List<JbRegistro> listarRegistrosPorMercado(int mercadoId, String data, String empresa) {
        List<JbRegistro> lista = new ArrayList<>();

        StringBuilder sql = new StringBuilder(
            "SELECT r.id, r.mercado_id, r.usuario_id, r.empresa, r.produto, r.foto, r.hora, r.criado_em, " +
            "       u.nome_completo AS nome_usuario " +
            "FROM REGISTROS r " +
            "JOIN USUARIOS u ON u.id = r.usuario_id " +
            "WHERE r.mercado_id = ? AND r.visivel = true "
        );

        if (data != null && !data.isBlank()) {
            sql.append("AND CAST(r.criado_em AS DATE) = ? ");
        }
        if (empresa != null && !empresa.isBlank()) {
            sql.append("AND r.empresa = ? ");
        }
        sql.append("ORDER BY r.criado_em DESC, r.hora DESC");

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql.toString())) {

            int idx = 1;
            pst.setInt(idx++, mercadoId);
            if (data != null && !data.isBlank()) {
                pst.setDate(idx++, java.sql.Date.valueOf(data)); // espera "yyyy-MM-dd"
            }
            if (empresa != null && !empresa.isBlank()) {
                pst.setString(idx++, empresa);
            }

            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    JbRegistro reg = new JbRegistro();
                    reg.setId(rs.getInt("id"));
                    reg.setMercadoId(rs.getInt("mercado_id"));
                    reg.setUsuarioId(rs.getInt("usuario_id"));
                    reg.setEmpresa(rs.getString("empresa"));
                    reg.setProduto(rs.getString("produto"));
                    reg.setFoto(rs.getString("foto"));
                    reg.setHora(rs.getTime("hora") != null ? rs.getTime("hora").toString() : "");
                    java.sql.Date d = rs.getDate("criado_em");
                    reg.setCriado_emStr(d != null ? new java.text.SimpleDateFormat("dd/MM/yyyy").format(d) : "");
                    reg.setNomeUsuario(rs.getString("nome_usuario"));
                    lista.add(reg);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar registros:");
            e.printStackTrace();
        }
        return lista;
    }

    // ─────────────────────────────────────────────
    // REGISTROS — datas que possuem registros visíveis
    // (alimenta o calendário no front-end)
    // ─────────────────────────────────────────────
    public List<String> listarDatasComRegistro(int mercadoId) {
        List<String> datas = new ArrayList<>();
        String sql =
            "SELECT DISTINCT CAST(r.criado_em AS DATE) AS dia " +
            "FROM REGISTROS r " +
            "WHERE r.mercado_id = ? AND r.visivel = true " +
            "ORDER BY dia DESC";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, mercadoId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    java.sql.Date d = rs.getDate("dia");
                    if (d != null) datas.add(d.toString()); // "yyyy-MM-dd"
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar datas com registro:");
            e.printStackTrace();
        }
        return datas;
    }

    // ─────────────────────────────────────────────
    // REGISTROS — empresas/marcas distintas de um mercado
    // (alimenta o filtro de marca no front-end)
    // ─────────────────────────────────────────────
    public List<String> listarEmpresasPorMercado(int mercadoId) {
        List<String> empresas = new ArrayList<>();
        String sql =
            "SELECT DISTINCT r.empresa " +
            "FROM REGISTROS r " +
            "WHERE r.mercado_id = ? AND r.visivel = true AND r.empresa IS NOT NULL " +
            "ORDER BY r.empresa";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, mercadoId);
            try (ResultSet rs = pst.executeQuery()) {
                while (rs.next()) {
                    String emp = rs.getString("empresa");
                    if (emp != null && !emp.isBlank()) empresas.add(emp);
                }
            }
        } catch (Exception e) {
            System.out.println("Erro ao listar empresas:");
            e.printStackTrace();
        }
        return empresas;
    }

    // ─────────────────────────────────────────────
    // REGISTROS — ocultar (soft delete)
    // Apenas marca visivel = false; não apaga linha nem arquivo.
    // ─────────────────────────────────────────────
    public boolean ocultarRegistro(int id) {
        String sql = "UPDATE REGISTROS SET visivel = false WHERE id = ?";

        try (Connection con = conectar();
             PreparedStatement pst = con.prepareStatement(sql)) {

            pst.setInt(1, id);
            return pst.executeUpdate() > 0;

        } catch (Exception e) {
            System.out.println("Erro ao ocultar registro:");
            e.printStackTrace();
        }
        return false;
    }
}