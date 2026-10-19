package com.autobots.automanager.representacoes;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.*;
import com.autobots.automanager.entidades.Telefone;
import com.autobots.automanager.servicos.TelefoneServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class TelefoneRepresentacao implements RepresentationModelAssembler<Telefone, EntityModel<Telefone>> {
    private final TelefoneServico servico;

    public TelefoneRepresentacao(TelefoneServico servico) {
        this.servico = servico;
    }

    @Override
    public EntityModel<Telefone> toModel(Telefone dado) {
        var controle = methodOn(TelefoneControle.class);
        var proprio = linkTo(controle.buscar(dado.getId())).withSelfRel().expand()
                .andAffordance(afford(controle.atualizarTelefone(dado.getId(), null)))
                .andAffordance(afford(controle.excluirTelefone(dado.getId())));
        var modelo = EntityModel.of(dado, proprio,
                linkTo(controle.listar(null)).withRel("telefones").expand(),
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand());
        servico.clienteId(dado.getId()).ifPresent(id -> modelo.add(
                linkTo(methodOn(ClienteControle.class).buscar(id)).withRel("cliente").expand()));
        return modelo;
    }
}
