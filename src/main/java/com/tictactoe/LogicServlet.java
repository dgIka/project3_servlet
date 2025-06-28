package com.tictactoe;


import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.List;

@WebServlet(name = "LogicServlet", value = "/logic")
public class LogicServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession currentSession = request.getSession();

        Field field = extractField(currentSession);

        int index = getSelectedIndex(request);

        Sign currentSign = field.getField().get(index);

        if (currentSign != Sign.EMPTY) {
            RequestDispatcher dispatcher = request.getRequestDispatcher("/index.jsp");
            dispatcher.forward(request, response);
            return;
        }

        field.getField().put(index, Sign.CROSS);
        if(checkWin(response, currentSession, field)) {
            return;
        }
        int emptyFieldIndex = field.getEmptyFieldIndex();

        if (emptyFieldIndex >= 0) {
            field.getField().put(emptyFieldIndex, Sign.NOUGHT);
            if(checkWin(response, currentSession, field)) {
                return;
            }
        }

        List<Sign> data = field.getFieldData();

        currentSession.setAttribute("data", data);
        currentSession.setAttribute("field", field);
        response.sendRedirect("/index.jsp");
    }

    private int getSelectedIndex(HttpServletRequest request) {
        String click = request.getParameter("click");
        boolean isNumeric = click.chars().allMatch(Character::isDigit);
        return isNumeric ? Integer.parseInt(click) : 0;
    }

    private Field extractField(HttpSession session) {
        Object field = session.getAttribute("field");
        if (Field.class != field.getClass()) {
            session.invalidate();
            throw new RuntimeException("Session is broken, try one more time");
        }
        return (Field) field;
    }

    private boolean checkWin(HttpServletResponse response, HttpSession session, Field field) throws IOException {
        Sign winner = field.checkWin();
        if (Sign.CROSS == winner || Sign.NOUGHT == winner) {
            session.setAttribute("winner", winner);
            List<Sign> data = field.getFieldData();
            session.setAttribute("data", data);
            response.sendRedirect("/index.jsp");
            return true;
        } return false;
    }
}
