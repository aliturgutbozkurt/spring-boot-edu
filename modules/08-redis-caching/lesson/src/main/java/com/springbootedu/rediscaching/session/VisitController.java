package com.springbootedu.rediscaching.session;

import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * Lesson 3.7 — ordinary HttpSession code; Spring Session transparently stores the session in Redis.
 */
// tag::session[]
@RestController
public class VisitController {

    @GetMapping("/api/visits")
    public String visit(HttpSession session) {
        Integer visits = (Integer) session.getAttribute("visits");
        int count = visits == null ? 1 : visits + 1;
        session.setAttribute("visits", count);                    // written to Redis at the end of the request
        return "visits in this session: " + count;
    }
}
// end::session[]
