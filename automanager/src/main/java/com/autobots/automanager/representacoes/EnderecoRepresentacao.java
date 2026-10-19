package com.autobots.automanager.representacoes;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.autobots.automanager.controles.*;
import com.autobots.automanager.entidades.Endereco;
import com.autobots.automanager.servicos.EnderecoServico;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class EnderecoRepresentacao implements RepresentationModelAssembler<Endereco, EntityModel<Endereco>> {
    private final EnderecoServico servico;

    public EnderecoRepresentacao(EnderecoServico servico) {
        this.servico = servico;
    }

    @Override
    public EntityModel<Endereco> toModel(Endereco dado) {
        var controle = methodOn(EnderecoControle.class);
        var proprio = linkTo(controle.buscar(dado.getId())).withSelfRel().expand()
                .andAffordance(afford(controle.atualizarEndereco(dado.getId(), null)))
                .andAffordance(afford(controle.excluirEndereco(dado.getId())));
        var modelo = EntityModel.of(dado, proprio,
                linkTo(controle.listar(null)).withRel("enderecos").expand(),
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand());
        servico.clienteId(dado.getId()).ifPresent(id -> modelo.add(
                linkTo(methodOn(ClienteControle.class).buscar(id)).withRel("cliente").expand()));
        return modelo;
    }
}
