package com.autobots.automanager.servicos;
import java.util.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;
import static org.springframework.http.HttpStatus.*;
import com.autobots.automanager.entidades.*;
import com.autobots.automanager.repositorios.*;
@Service
@Transactional
public class ClienteServico {
 private final ClienteRepositorio repositorio;
 public ClienteServico(ClienteRepositorio repositorio) { this.repositorio = repositorio; }
 public Cliente buscar(long id) {
  Cliente c = repositorio.findById(id).orElseThrow(() -> new ResponseStatusException(NOT_FOUND, "Cliente nao encontrado"));
  c.getDocumentos().size(); c.getTelefones().size();
  return c;
 }
 public List<Cliente> listar() {
  List<Cliente> clientes = repositorio.findAll();
  clientes.forEach(c -> { c.getDocumentos().size(); c.getTelefones().size(); });
  return clientes;
 }
 public Cliente criar(Cliente c) {
  if (c.getId()!=null || (c.getEndereco()!=null && c.getEndereco().getId()!=null)
    || c.getDocumentos().stream().anyMatch(d -> d.getId()!=null)
    || c.getTelefones().stream().anyMatch(t -> t.getId()!=null))
   throw new ResponseStatusException(BAD_REQUEST, "Nao envie IDs no cadastro");
  c.setDataCadastro(new Date());
  return repositorio.saveAndFlush(c);
 }
 public Cliente atualizar(long id, String nome, String nomeSocial, Date nascimento) {
  Cliente c=buscar(id); c.setNome(nome); c.setNomeSocial(nomeSocial); c.setDataNascimento(nascimento);
  return repositorio.saveAndFlush(c);
 }
 public void excluir(long id) { repositorio.delete(buscar(id)); repositorio.flush(); }
}
