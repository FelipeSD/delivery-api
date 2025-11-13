package com.deliverytech.delivery_api.produto.interfaces;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.deliverytech.delivery_api.produto.dto.ProdutoFiltroDTO;
import com.deliverytech.delivery_api.produto.model.Produto;

public interface ProdutoRepositoryCustom {
  Page<Produto> buscarComFiltros(ProdutoFiltroDTO filtro, Pageable pageable);
}
