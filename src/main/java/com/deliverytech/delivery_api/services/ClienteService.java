package com.deliverytech.delivery_api.services;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.dtos.ClienteDTO;
import com.deliverytech.delivery_api.dtos.ClienteResponseDTO;

public interface ClienteService {
  ClienteResponseDTO cadastrarCliente(ClienteDTO dto);

  ClienteResponseDTO buscarClientePorId(Long id);

  ClienteResponseDTO buscarClientePorEmail(String email);

  ClienteResponseDTO atualizarCliente(Long id, ClienteDTO dto);

  ClienteResponseDTO ativarDesativarCliente(Long id);

  Page<ClienteResponseDTO> listarClientesAtivos(Pageable pageable);
}
