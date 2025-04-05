package matching.teamify.config.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import lombok.extern.slf4j.Slf4j;
import matching.teamify.config.SessionConst;
import org.springframework.http.HttpStatus;
import org.springframework.web.servlet.HandlerInterceptor;

@Slf4j
public class LoginCheckInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {

        if (request.getMethod().equals("OPTIONS")) {
            return true;
        }

        HttpSession session = request.getSession(false);

        if (session == null || session.getAttribute(SessionConst.LOGIN_MEMBER) == null) {
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.getWriter().write("Session expired");

            log.info("session={}", session);

            return false;
        }
        return true;
    }
}
