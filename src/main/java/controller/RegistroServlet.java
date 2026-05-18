package controller;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import model.DAO;
import model.JbRegistro;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.util.List;

@WebServlet(urlPatterns = {
    "/registrar-visita",
    "/registros"
})

@MultipartConfig
public class RegistroServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    DAO dao = new DAO();

    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String mercadoParam = request.getParameter("mercado");

            if (mercadoParam == null || mercadoParam.isEmpty()) {

                response.setStatus(400);
                response.getWriter().write(
                    "{\"erro\":\"mercado não informado\"}"
                );
                return;
            }

            int mercadoId = Integer.parseInt(mercadoParam);

            List<JbRegistro> lista =
                dao.listarRegistrosPorMercado(mercadoId);

            String json = new Gson().toJson(lista);

            response.getWriter().write(json);

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(500);
            response.getWriter().write(
                "{\"erro\":\"Erro ao buscar registros\"}"
            );
        }
    }

    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        try {

            String mercadoStr = request.getParameter("mercado_id");
            String usuarioStr = request.getParameter("usuario_id");

            if (mercadoStr == null || usuarioStr == null) {
                response.setStatus(400);
                response.getWriter().write("IDs inválidos");
                return;
            }

            int mercadoId = Integer.parseInt(mercadoStr);
            int usuarioId = Integer.parseInt(usuarioStr);

            String empresa = request.getParameter("empresa");
            String produto = request.getParameter("produto");

            Part arquivo = request.getPart("foto");

            if (arquivo == null || arquivo.getSubmittedFileName() == null) {
                response.setStatus(400);
                response.getWriter().write("Arquivo inválido");
                return;
            }

            String nomeArquivo =
                    System.currentTimeMillis() + "_" + arquivo.getSubmittedFileName();

            String pasta = getServletContext().getRealPath("/uploads");

            if (pasta == null) {
                pasta = System.getProperty("java.io.tmpdir") + "/uploads";
            }

            File dir = new File(pasta);
            if (!dir.exists()) dir.mkdirs();

            Path caminho = Paths.get(pasta, nomeArquivo);

            Files.copy(arquivo.getInputStream(), caminho);

            JbRegistro r = new JbRegistro();
            r.setMercadoId(mercadoId);
            r.setUsuarioId(usuarioId);
            r.setEmpresa(empresa);
            r.setProduto(produto);
            r.setFoto(nomeArquivo);
            r.setHora(LocalTime.now().toString());

            boolean ok = dao.salvarRegistro(r);

            response.setStatus(ok ? 200 : 500);

        } catch (Exception e) {
            e.printStackTrace(); // ESSENCIAL
            response.setStatus(500);
        }
    }
}