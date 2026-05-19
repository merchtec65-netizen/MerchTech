package controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

import com.google.gson.Gson;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import model.DAO;
import model.JbMercado;

@WebServlet("/mercados")
@MultipartConfig
public class MercadoServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;

    DAO dao = new DAO();

    // LISTAR MERCADOS
    @Override
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            List<JbMercado> lista = dao.listarMercados();

            Gson gson = new Gson();

            response.getWriter().write(
                gson.toJson(lista)
            );

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(500);

            response.getWriter().write(
                "{\"erro\":\"Erro ao buscar mercados\"}"
            );
        }
    }

    // CADASTRAR MERCADO
    @Override
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String nome    = request.getParameter("nome");
            String cidade  = request.getParameter("cidade");
            String bairro  = request.getParameter("bairro");
            String rua     = request.getParameter("rua");
            String numero  = request.getParameter("numero");

            Part fotoPart = request.getPart("foto");

            String nomeArquivo = null;

            // SALVA foto
            if (fotoPart != null &&
                fotoPart.getSize() > 0) {

                nomeArquivo = System.currentTimeMillis()
                        + "_"
                        + Paths.get(
                            fotoPart.getSubmittedFileName()
                          ).getFileName().toString();

                String uploadPath =
                    getServletContext().getRealPath("")
                    + File.separator
                    + "uploads";

                File pasta = new File(uploadPath);

                if (!pasta.exists()) {
                    pasta.mkdirs();
                }

                fotoPart.write(
                    uploadPath
                    + File.separator
                    + nomeArquivo
                );
            }

            // MONTA OBJETO
            JbMercado m = new JbMercado();

            m.setNome(nome);
            m.setCidade(cidade);
            m.setBairro(bairro);
            m.setRua(rua);
            m.setNumero(numero);

            m.setEndereco(
                rua + ", " + numero
            );

            m.setFoto(nomeArquivo);

            boolean ok =
                dao.cadastrarMercado(m);

            if (ok) {

                response.getWriter().write(
                    "{\"sucesso\":true}"
                );

            } else {

                response.setStatus(500);

                response.getWriter().write(
                    "{\"erro\":\"Erro ao salvar mercado\"}"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(500);

            response.getWriter().write(
                "{\"erro\":\"Erro interno\"}"
            );
        }
    }

    // REMOVER MERCADO
    @Override
    protected void doDelete(HttpServletRequest request,
                            HttpServletResponse response)
            throws IOException {

        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        try {

            String idParam = request.getParameter("id");

            if (idParam == null || idParam.isEmpty()) {

                response.setStatus(400);

                response.getWriter().write(
                    "{\"erro\":\"ID do mercado não informado\"}"
                );

                return;
            }

            int id = Integer.parseInt(idParam);

            boolean ok = dao.removerMercado(id);

            if (ok) {

                response.getWriter().write(
                    "{\"sucesso\":true}"
                );

            } else {

                response.setStatus(404);

                response.getWriter().write(
                    "{\"erro\":\"Mercado não encontrado\"}"
                );
            }

        } catch (Exception e) {

            e.printStackTrace();

            response.setStatus(500);

            response.getWriter().write(
                "{\"erro\":\"Erro ao remover mercado\"}"
            );
        }
    }
}