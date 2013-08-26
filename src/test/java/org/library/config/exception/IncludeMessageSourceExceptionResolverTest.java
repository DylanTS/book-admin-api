package org.library.config.exception;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.easymock.EasyMock;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceResolvable;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.servlet.ModelAndView;

public class IncludeMessageSourceExceptionResolverTest {

    private static final String MESSAGE = "fix yer errors!";

    private MessageSource messageSource;
    private IncludeMessageSourceExceptionResolver resolver;
    private MethodArgumentNotValidException ex;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private Object handler;

    @Before
    public void setupBefore() {
        this.messageSource = EasyMock.createNiceMock(MessageSource.class);
        EasyMock.expect(
                this.messageSource.getMessage(EasyMock.isA(MessageSourceResolvable.class),
                        (Locale) EasyMock.isNull())).andReturn(MESSAGE).anyTimes();
        EasyMock.replay(this.messageSource);
        this.resolver = new IncludeMessageSourceExceptionResolver(this.messageSource);

        this.ex = EasyMock.createNiceMock(MethodArgumentNotValidException.class);
        this.request = new MockHttpServletRequest();
        this.response = new MockHttpServletResponse();
        this.handler = new Object();
    }

    @Test
    public void testHandleMethodArgumentNotValidException_NoBindingResult() throws IOException {
        ModelAndView mav = this.resolver.handleMethodArgumentNotValidException(ex, request,
                response, handler);
        Assert.assertNotNull("model and view must not be null", mav);
        Assert.assertEquals("status must be 400", HttpServletResponse.SC_BAD_REQUEST,
                response.getStatus());
        Assert.assertNull("message must be null", response.getErrorMessage());
    }

    @Test
    public void testHandleMethodArgumentNotValidException() throws IOException {
        BindingResult bindingResult = EasyMock.createNiceMock(BindingResult.class);
        FieldError fieldError = EasyMock.createNiceMock(FieldError.class);
        EasyMock.expect(bindingResult.getFieldError()).andReturn(fieldError).anyTimes();
        EasyMock.replay(bindingResult);
        EasyMock.expect(ex.getBindingResult()).andReturn(bindingResult).anyTimes();
        EasyMock.replay(ex);

        ModelAndView mav = this.resolver.handleMethodArgumentNotValidException(ex, request,
                response, handler);
        Assert.assertNotNull("model and view must not be null", mav);
        Assert.assertEquals("status must be 400", HttpServletResponse.SC_BAD_REQUEST,
                response.getStatus());
        Assert.assertEquals("message must match", MESSAGE, response.getErrorMessage());
    }

}
