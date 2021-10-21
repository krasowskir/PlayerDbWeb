package org.richard.home;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

@WebServlet(urlPatterns = "/mein")
public class MeinServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println("=== params ===");
        req.getParameterMap()
                .forEach((key, value) -> System.out.println("name: " + key + " values: " + Arrays.toString(value)));

        System.out.println("=== header ==="); //deprecated shit
        Enumeration<String> headers = req.getHeaderNames();
        Iterator<String> headerIter = headers.asIterator();
        while (headerIter.hasNext()) {
            String header = headerIter.next();
            System.out.println("header: " + header + " value: " + req.getHeader(header));
        }

        System.out.println("=== session info ===");
        Random rand = new Random();
        HttpSession session = req.getSession();
        String sessVal = "";
        if (req.getParameter("clear") != null) {
            session.invalidate();
        } else {
            Enumeration<String> sessNames = session.getAttributeNames();

            if (!sessNames.hasMoreElements()) {
                System.out.println("session contains no information!");
                sessVal = "sess-" + rand.nextInt(10000);
                session.setAttribute("richSession", sessVal);
            } else {
                String name = sessNames.nextElement();
                sessVal = session.getAttribute(name).toString();
                System.out.println("session name: " + name + " value: " + sessVal);
            }
        }

        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");

        try {
            resp.setStatus(HttpServletResponse.SC_OK);
            String content = new String(Files.readAllBytes(Path.of(getServletContext().getResource("/WEB-INF/classes/index.html").toURI())));
            out.write(new StringBuilder(content).insert(173, sessVal).toString().getBytes());
            out.flush();
        } catch (URISyntaxException e) {
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            out.write("error".getBytes());
            out.flush();
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, String[]> params = req.getParameterMap();
        Set<String> paramKeys = params.keySet();
        paramKeys.forEach(k -> System.out.println("name: " + k + " values: " + Arrays.toString(params.get(k))));
        super.doPost(req, resp);
    }

    @Override
    protected void doPut(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doPut(req, resp);
    }

    @Override
    protected void doDelete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        super.doDelete(req, resp);
    }
}
