package com.autobots.automanager.representacoes;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.*;
import com.autobots.automanager.entidades.Documento;
import com.autobots.automanager.servicos.DocumentoServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class DocumentoRepresentacao implements RepresentationModelAssembler<Documento, EntityModel<Documento>> {
    private final DocumentoServico servico;

    public DocumentoRepresentacao(DocumentoServico servico) {
        this.servico = servico;
    }

    @Override
    public EntityModel<Documento> toModel(Documento dado) {
        var controle = methodOn(DocumentoControle.class);
        var proprio = linkTo(controle.buscar(dado.getId())).withSelfRel().expand()
                .andAffordance(afford(controle.atualizarDocumento(dado.getId(), null)))
                .andAffordance(afford(controle.excluirDocumento(dado.getId())));
        var modelo = EntityModel.of(dado, proprio,
                linkTo(controle.listar(null)).withRel("documentos").expand(),
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand());
        servico.clienteId(dado.getId()).ifPresent(id -> modelo.add(
                linkTo(methodOn(ClienteControle.class).buscar(id)).withRel("cliente").expand()));
        return modelo;
    }
}
