package com.adalberto.loja.api.exceptionhandler;

import java.time.LocalDateTime;
import java.util.ArrayList;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import com.adalberto.loja.domain.exceptions.RegraDeNegocioException;

@ControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler {

	@Override
	protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
			HttpHeaders headers, HttpStatus status, WebRequest request) {
		var campos = new ArrayList<Problema.Campo>();
		for (ObjectError error : ex.getBindingResult().getAllErrors()) {
			String nome = ((FieldError) error).getField();
			String mensagem = error.getDefaultMessage();
			campos.add(new Problema.Campo(nome, mensagem));
		}

		var problema = new Problema();
		problema.setStatus(status.value());
		problema.setTitulo("Um ou mais campos estão inválidos");
		problema.setDatahora(LocalDateTime.now());
		problema.setCampos(campos);

		return super.handleExceptionInternal(ex, problema, headers, status, request);
	}

	@ExceptionHandler(RegraDeNegocioException.class)
	public ResponseEntity<Object> handleRegraDeNegocioException(RegraDeNegocioException ex, WebRequest request) {

		var problema = new Problema();
		HttpHeaders headers = new HttpHeaders();
		HttpStatus status = HttpStatus.BAD_REQUEST;
		problema.setStatus(status.value());
		problema.setTitulo(ex.getMessage());
		problema.setDatahora(LocalDateTime.now());

		return handleExceptionInternal(ex, problema, headers, status, request);

	}
}
