package com.umc.pediupatrao.controller;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@ControllerAdvice(assignableTypes = HomeController.class)
public class FormularioExceptionHandler {

    @ExceptionHandler({
        IllegalArgumentException.class,
        IllegalStateException.class
    })
    public String tratarErro(
            RuntimeException exception,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes) {

        redirectAttributes.addFlashAttribute(
                "erro",
                exception.getMessage()
        );

        String caminho = request.getRequestURI();

        if (caminho.startsWith("/usuarios")) {
            return "redirect:/usuarios";
        }

        if (caminho.startsWith("/clientes")) {
            return "redirect:/clientes";
        }

        return "redirect:/";
    }
}