package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import com.google.gson.Gson;
import model.DAO;
import model.JbUsuario;

/**
 * POST /login
 *
 * Recebe JSON: { "username": "...", "senha": "..." }
 * Retorna JSON com dados do usuário (sem a senha) e o campo "role".
 * O frontend usa o "role" para redirecionar para a tela correta.
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DAO dao = new DAO();
    private final Gson gson = new Gson();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        // Lê o corpo da requisição
        StringBuilder sb = new StringBuilder();
        try (var reader = request.getReader()) {
            String linha;
            while ((linha = reader.readLine()) != null) sb.append(linha);
        }

        // Deserializa o JSON de entrada
        LoginRequest loginReq = gson.fromJson(sb.toString(), LoginRequest.class);

        if (loginReq == null || loginReq.username == null || loginReq.senha == null) {
            response.setStatus(400);
            response.getWriter().write("{\"mensagem\":\"Dados de login inválidos.\"}");
            return;
        }

        // Valida no banco
        JbUsuario usuario = dao.autenticar(loginReq.username.trim(), loginReq.senha);

        if (usuario == null) {
            response.setStatus(401);
            response.getWriter().write("{\"mensagem\":\"Usuário ou senha inválidos.\"}");
            return;
        }

        // Monta a resposta (sem a senha)
        LoginResponse resp = new LoginResponse();
        resp.id       = usuario.getId();
        resp.username = usuario.getUsername();
        resp.nome     = usuario.getNome();
        resp.email    = usuario.getEmail();
        resp.role     = usuario.getRole();
        resp.ativo    = usuario.isAtivo();

        response.setStatus(200);
        response.getWriter().write(gson.toJson(resp));
    }

    // Classes internas para desserialização / serialização

    private static class LoginRequest {
        String username;
        String senha;
    }

    private static class LoginResponse {
        int     id;
        String  username;
        String  nome;
        String  email;
        String  role;
        boolean ativo;
    }
}