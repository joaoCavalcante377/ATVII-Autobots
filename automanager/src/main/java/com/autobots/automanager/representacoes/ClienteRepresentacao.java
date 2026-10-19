package com.autobots.automanager.representacoes;

import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.RepresentationModelAssembler;
import org.springframework.stereotype.Component;
import com.autobots.automanager.entidades.Cliente;
import com.autobots.automanager.controles.*;
import static org.springframework.hateoas.server.mvc.WebMvcLinkBuilder.*;

@Component
public class ClienteRepresentacao implements RepresentationModelAssembler<Cliente, EntityModel<ClienteResposta>> {
    private final DocumentoRepresentacao documentos;
    private final TelefoneRepresentacao telefones;
    private final EnderecoRepresentacao enderecos;

    public ClienteRepresentacao(DocumentoRepresentacao documentos, TelefoneRepresentacao telefones,
            EnderecoRepresentacao enderecos) {
        this.documentos = documentos;
        this.telefones = telefones;
        this.enderecos = enderecos;
    }

    @Override
    public EntityModel<ClienteResposta> toModel(Cliente cliente) {
        long id = cliente.getId();
        var dados = new ClienteResposta(id, cliente.getNome(), cliente.getNomeSocial(),
                cliente.getDataNascimento(), cliente.getDataCadastro(),
                cliente.getDocumentos().stream().map(documentos::toModel).toList(),
                cliente.getEndereco() == null ? null : enderecos.toModel(cliente.getEndereco()),
                cliente.getTelefones().stream().map(telefones::toModel).toList());
        var proprio = linkTo(methodOn(ClienteControle.class).buscar(id)).withSelfRel().expand()
                .andAffordance(afford(methodOn(ClienteControle.class).atualizar(id, null)))
                .andAffordance(afford(methodOn(ClienteControle.class).excluir(id)));
        var modelo = EntityModel.of(dados, proprio,
                linkTo(methodOn(ClienteControle.class).listar()).withRel("clientes").expand(),
                linkTo(methodOn(DocumentoControle.class).listar(id)).withRel("documentos").expand()
                        .andAffordance(afford(methodOn(DocumentoControle.class).criarDocumento(id, null))),
                linkTo(methodOn(TelefoneControle.class).listar(id)).withRel("telefones").expand()
                        .andAffordance(afford(methodOn(TelefoneControle.class).criarTelefone(id, null))),
                linkTo(methodOn(RaizControle.class).inicio()).withRel("inicio").expand());
        if (cliente.getEndereco() == null) {
            modelo.add(linkTo(methodOn(EnderecoControle.class).listar(id)).withRel("enderecos").expand()
                    .andAffordance(afford(methodOn(EnderecoControle.class).criarEndereco(id, null))));
        } else {
            modelo.add(linkTo(methodOn(EnderecoControle.class).buscar(cliente.getEndereco().getId()))
                    .withRel("endereco").expand());
        }
        return modelo;
    }
}
