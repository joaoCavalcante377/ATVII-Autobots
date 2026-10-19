package com.autobots.automanager.representacoes;

import java.util.Date;
import java.util.List;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.server.core.Relation;
import com.autobots.automanager.entidades.*;

@Relation(itemRelation = "cliente", collectionRelation = "clientes")
public record ClienteResposta(Long id, String nome, String nomeSocial, Date dataNascimento,
        Date dataCadastro, List<EntityModel<Documento>> documentos,
        EntityModel<Endereco> endereco, List<EntityModel<Telefone>> telefones) {
}
