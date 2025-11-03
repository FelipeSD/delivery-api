package com.deliverytech.delivery_api.interfaces;

import com.deliverytech.delivery_api.dtos.ProdutoFiltroDTO;
import com.deliverytech.delivery_api.entities.Produto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProdutoRepositoryCustom {
  Page<Produto> buscarComFiltros(ProdutoFiltroDTO filtro, Pageable pageable);
}
