package com.eXiua.tasksi.config;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * Simple controller that forwards all non-API routes to the frontend index.html.
 *
 * - This is safer than complex view-controller patterns and avoids issues with
 *   path matching strategies.
 * - It forwards paths that do not contain a dot (no file extension) so requests
 *   for assets (like *.js, *.css) are still served as static resources.
 */
@Controller
public class SpaController {

    // Match root and any path without a dot (no file extension). Forward to index.html
    @RequestMapping({"/", "{path:[^\\.]*}", "/**/{path:[^\\.]*}"})
    public String forward() {
        return "forward:/index.html";
    }
}
