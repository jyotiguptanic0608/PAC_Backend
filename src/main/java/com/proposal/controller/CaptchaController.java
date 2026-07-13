package com.proposal.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import javax.imageio.ImageIO;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.code.kaptcha.impl.DefaultKaptcha;

@RestController
@RequestMapping("/api")
public class CaptchaController {

    @Autowired
    private DefaultKaptcha captchaProducer;

    @GetMapping("/captcha")
    public void captcha(
            HttpServletResponse response,
            HttpSession session)
            throws Exception {

        String text =
                captchaProducer.createText();

        session.setAttribute(
                "captcha",
                text
        );

        BufferedImage image =
                captchaProducer.createImage(text);

        response.setContentType("image/jpeg");

        ImageIO.write(
                image,
                "jpg",
                response.getOutputStream()
        );

    }

}