package org.richard.home;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletException;
import javax.servlet.annotation.HttpConstraint;
import javax.servlet.annotation.ServletSecurity;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

@ServletSecurity(
   @HttpConstraint(rolesAllowed = { "secretagent" },transportGuarantee = ServletSecurity.TransportGuarantee.CONFIDENTIAL)
)
@WebServlet("/secure")
public class SecureHelloClient extends HttpServlet {
    Logger log = LoggerFactory.getLogger(SecureHelloClient.class);

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        log.info("=== accessed secure area === ");
        OutputStream out = resp.getOutputStream();
        resp.setContentType("text/html");
        resp.setStatus(HttpServletResponse.SC_OK);
        try {
            String content = new String(Files.readAllBytes(Path.of(getServletContext().getResource("/WEB-INF/classes/secureDoc.html").toURI())));
            out.write(new StringBuilder(content).toString().getBytes());
            out.flush();
        } catch (URISyntaxException e) {
            log.error(e.toString());
        }
    }
}
