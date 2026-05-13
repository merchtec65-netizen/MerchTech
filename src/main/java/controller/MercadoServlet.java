package controller;

import java.io.IOException;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.DAO;
import model.JbMercado;

@WebServlet("/mercados")
public class MercadoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    DAO dao = new DAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {
            List<JbMercado> lista = dao.listarMercados();

            Gson gson = new Gson();
            String json = gson.toJson(lista);

            response.getWriter().write(json);

        } catch (Exception e) {
            e.printStackTrace();
            response.setStatus(500);
            response.getWriter().write("{\"erro\":\"Erro ao buscar mercados\"}");
        }
    }
}