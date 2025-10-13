package com.deliverytech.delivery_api.services;

import java.util.List;

import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.dtos.ClienteResponseDTO;

public interface ClienteService {
  ClienteResponseDTO cadastrarCliente(ClienteDTO dto);

  ClienteResponseDTO buscarClientePorId(Long id);

  ClienteResponseDTO buscarClientePorEmail(String email);

  ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto);

  ClienteResponseDTO ativarDesativarCliente(Long id);

  List<ClienteResponseDTO> listarClientesAtivos();
}
