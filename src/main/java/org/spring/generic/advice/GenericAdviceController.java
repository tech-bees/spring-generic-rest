package org.spring.generic.advice;

import org.hibernate.Remove;
import org.spring.generic.exception.InvalidIdException;
import org.spring.generic.exception.NoContentException;
import org.springframework.beans.ConversionNotSupportedException;
import org.springframework.beans.TypeMismatchException;
import org.springframework.context.MessageSource;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.*;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.http.converter.HttpMessageNotWritableException;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.validation.method.MethodValidationException;
import org.springframework.web.ErrorResponseException;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.HttpRequestMethodNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingPathVariableException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.ServletRequestBindingException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.context.request.async.AsyncRequestTimeoutException;
import org.springframework.web.method.annotation.HandlerMethodValidationException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.multipart.support.MissingServletRequestPartException;
import org.springframework.web.servlet.NoHandlerFoundException;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public abstract class GenericAdviceController extends ResponseEntityExceptionHandler {


    @Override
    public void setMessageSource(MessageSource messageSource) {
        super.setMessageSource(messageSource);
    }

    @Override
    protected MessageSource getMessageSource() {
        return super.getMessageSource();
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<?> handleDataIntegrityViolationException( DataIntegrityViolationException ex, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()), HttpStatus.DESTINATION_LOCKED.value(),
                HttpStatus.DESTINATION_LOCKED.getReasonPhrase(),"Integrity constraint violation, Target item locked with another item!");
        return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.FAILED_DEPENDENCY);
    }

    @ExceptionHandler(NoContentException.class)
    public ResponseEntity<?> handleNoContentException( NoContentException ex, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()), HttpStatus.NO_CONTENT.value(),
                HttpStatus.NO_CONTENT.getReasonPhrase(),ex.getMessage());
        return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.NO_CONTENT);
    }

    @ExceptionHandler(InvalidIdException.class)
    public ResponseEntity<?> handleInvalidIdException( InvalidIdException ex, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()), HttpStatus.BAD_REQUEST.value(),
                HttpStatus.BAD_REQUEST.getReasonPhrase(),ex.getMessage());
        return new ResponseEntity<ErrorMessage>(errorMessage,HttpStatus.BAD_REQUEST);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestPart(MissingServletRequestPartException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        return super.handleMissingServletRequestPart(ex, headers, status, request);
    }

    @Override
    protected ResponseEntity<Object> handleHttpRequestMethodNotSupported(HttpRequestMethodNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotSupported(HttpMediaTypeNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String unsupported = "Unsupported content type : "+ex.getContentType();
        String supported = "Supported content type : "+MediaType.toString(ex.getSupportedMediaTypes());
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),unsupported,supported);
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMediaTypeNotAcceptable(HttpMediaTypeNotAcceptableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingPathVariable(MissingPathVariableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMissingServletRequestParameter(MissingServletRequestParameterException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleServletRequestBindingException(ServletRequestBindingException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        List<String> errors = getStrings(ex);
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),errors);
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    private static List<String> getStrings(MethodArgumentNotValidException ex) {
        List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        List<ObjectError> objectErrors = ex.getBindingResult().getGlobalErrors();
        List<String> errors = new ArrayList<>(fieldErrors.size()+objectErrors.size());
        String error = null;
        for(FieldError fieldError : fieldErrors){
            error = fieldError.getField()+" , "+fieldError.getDefaultMessage();
            errors.add(error);
        }
        for(ObjectError objectError : objectErrors){
            error = objectError.getObjectName()+" , "+objectError.getDefaultMessage();
            errors.add(error);
        }
        return errors;
    }

    @Override
    protected ResponseEntity<Object> handleHandlerMethodValidationException(HandlerMethodValidationException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoHandlerFoundException(NoHandlerFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(NoResourceFoundException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleAsyncRequestTimeoutException(AsyncRequestTimeoutException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleErrorResponseException(ErrorResponseException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleConversionNotSupported(ConversionNotSupportedException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleTypeMismatch(TypeMismatchException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotWritable(HttpMessageNotWritableException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }


    @Override
    protected ResponseEntity<Object> handleMethodValidationException(MethodValidationException ex, HttpHeaders headers, HttpStatus status, WebRequest request) {
        String path  = request.getDescription(false);
        ErrorMessage errorMessage = new ErrorMessage(new Date(), path.substring("uri=".length()),
                status.value(), status.toString(),ex.getLocalizedMessage());
        return new ResponseEntity<>(errorMessage, headers, status);
    }

    @Override
    protected ProblemDetail createProblemDetail(Exception ex, HttpStatusCode status, String defaultDetail, String detailMessageCode, Object[] detailMessageArguments, WebRequest request) {
        return super.createProblemDetail(ex, status, defaultDetail, detailMessageCode, detailMessageArguments, request);
    }

    @Override
    protected ResponseEntity<Object> handleExceptionInternal(Exception ex, Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return super.handleExceptionInternal(ex, body, headers, statusCode, request);
    }

    @Override
    protected ResponseEntity<Object> createResponseEntity(Object body, HttpHeaders headers, HttpStatusCode statusCode, WebRequest request) {
        return super.createResponseEntity(body, headers, statusCode, request);
    }

}
