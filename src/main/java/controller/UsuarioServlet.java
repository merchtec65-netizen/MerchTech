package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;
import com.google.gson.Gson;
import model.DAO;
import model.JbUsuario;

/**
 * GET  /usuarios?role=promotor  → lista usuários por papel
 * POST /usuarios                → cadastra novo usuário (apenas supervisor deve chamar)
 */
@WebServlet("/usuarios")
public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DAO dao   = new DAO();
    private final Gson gson = new Gson();

    // ─── GET: lista usuários por role ────────────────────────────────────────────

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String role = request.getParameter("role");

        // Se não informar role, retorna lista vazia por segurança
        if (role == null || role.isBlank()) {
            response.getWriter().write("[]");
            return;
        }

        List<JbUsuario> lista = dao.listarUsuariosPorRole(role.trim().toLowerCase());

        // Remove senhas antes de serializar
        lista.forEach(u -> u.setSenha(null));

        response.getWriter().write(gson.toJson(lista));
    }

    // ─── POST: cadastra novo usuário ─────────────────────────────────────────────

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Lê o corpo
        StringBuilder sb = new StringBuilder();
        try (var reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) sb.append(linha);
        }

        JbUsuario novo = gson.fromJson(sb.toString(), JbUsuario.class);

        // Validações básicas
        if (novo == null || novo.getUsername() == null || novo.getUsername().isBlank()) {
            response.setStatus(400);
            response.getWriter().write("{\"mensagem\":\"Usuário é obrigatório.\"}");
            return;
        }

        if (novo.getSenha() == null || novo.getSenha().length() < 6) {
            response.setStatus(400);
            response.getWriter().write("{\"mensagem\":\"Senha deve ter pelo menos 6 caracteres.\"}");
            return;
        }

        if (novo.getNome() == null || novo.getNome().isBlank()) {
            response.setStatus(400);
            response.getWriter().write("{\"mensagem\":\"Nome é obrigatório.\"}");
            return;
        }

        // Garante que o role seja sempre "promotor" neste endpoint
        // (supervisores só podem ser criados diretamente no banco)
        novo.setRole("promotor");

        // Verifica se o username já existe
        if (dao.usernameExiste(novo.getUsername())) {
            response.setStatus(409);
            response.getWriter().write("{\"mensagem\":\"Usuário \\\"" + novo.getUsername() + "\\\" já está em uso.\"}");
            return;
        }

        boolean ok = dao.cadastrarUsuario(novo);

        if (ok) {
            response.setStatus(201);
            response.getWriter().write("{\"mensagem\":\"Promotor cadastrado com sucesso.\"}");
        } else {
            response.setStatus(500);
            response.getWriter().write("{\"mensagem\":\"Erro interno ao cadastrar.\"}");
        }
    }
}