package com.autobots.automanager.controles;

import org.springframework.hateoas.RepresentationModel;
import org.springframework.web.bind.annotation.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@RestController
public class RaizControle {
    @GetMapping("/")
    public RepresentationModel<?> inicio() {
        return new RepresentationModel<>().add(
                linkTo(methodOn(RaizControle.class).inicio()).withSelfRel().expand(),
                linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes").expand(),
                linkTo(methodOn(DocumentoControle.class).listar(null)).withRel("documentos").expand(),
                linkTo(methodOn(TelefoneControle.class).listar(null)).withRel("telefones").expand(),
                linkTo(methodOn(EnderecoControle.class).listar(null)).withRel("enderecos").expand());
    }
}
