package controller;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import model.DAO;

/**
 * POST /heartbeat?usuario_id=123
 *
 * Chamado periodicamente pelo painel do promotor (promotor.html) enquanto
 * a tela estiver aberta, para sinalizar que o usuário está ativo/online.
 * O supervisor considera o promotor "online" se o último heartbeat
 * (ou login) foi há no máximo 5 minutos.
 */
@WebServlet("/heartbeat")
public class HeartbeatServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final DAO dao = new DAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        String idParam = request.getParameter("usuario_id");

        if (idParam == null || idParam.isBlank()) {
            response.setStatus(400);
            response.getWriter().write("{\"erro\":\"usuario_id não informado\"}");
            return;
        }

        try {
            int usuarioId = Integer.parseInt(idParam);
            boolean ok = dao.registrarHeartbeat(usuarioId);

            if (ok) {
                response.getWriter().write("{\"sucesso\":true}");
            } else {
                response.setStatus(404);
                response.getWriter().write("{\"erro\":\"Usuário não encontrado\"}");
            }
        } catch (NumberFormatException e) {
            response.setStatus(400);
            response.getWriter().write("{\"erro\":\"usuario_id inválido\"}");
        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"erro\":\"Erro interno\"}");
        }
    }
}
