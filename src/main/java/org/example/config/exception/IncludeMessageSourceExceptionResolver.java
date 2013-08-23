package org.example.config.exception;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang.StringUtils;
import org.springframework.context.MessageSource;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.DefaultHandlerExceptionResolver;

/**
 * A {@link DefaultHandlerExceptionResolver} which includes the message found in the
 * {@link MessageSource} when available. This is helpful to the user to include the error message if
 * it's found.
 * 
 * @author dylants
 * 
 */
public class IncludeMessageSourceExceptionResolver extends DefaultHandlerExceptionResolver {

    private MessageSource messageSource;

    public IncludeMessageSourceExceptionResolver(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    @Override
    protected ModelAndView handleMethodArgumentNotValidException(
            MethodArgumentNotValidException ex, HttpServletRequest request,
            HttpServletResponse response, Object handler) throws IOException {
        BindingResult bindingResult = ex.getBindingResult();
        if (bindingResult != null) {
            String message = this.messageSource.getMessage(bindingResult.getFieldError(), null);
            if (StringUtils.isNotBlank(message)) {
                // return the error message along with a 400 HTTP status code
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, message);
                return new ModelAndView();
            }
        }

        return super.handleMethodArgumentNotValidException(ex, request, response, handler);
    }

}