package controller;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import model.DAO;

@WebServlet(urlPatterns = {"/Controller", "/main"})
public class Controller extends HttpServlet {
    private static final long serialVersionUID = 1L;

    DAO dao = new DAO();

    public Controller() {
        super();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/plain");
        response.getWriter().append("Servidor funcionando OK");

        dao.testeConexao();
    }
}