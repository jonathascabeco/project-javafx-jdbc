package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.SellerDao;
import model.entities.Seller;

public class SellerService {

	private SellerDao dao = DaoFactory.createSellerDao();
	// relacionando com o BD;

	public List<Seller> findAll() {
		return dao.findAll();
	}
	// após isso, declarar uma dependencia desse servico no controller do
	// SellerListController, para carregar essa lista na view;

	public void saveOrUpdate(Seller obj) {
		// ou inserir ou atualizar;
		if (obj.getId() == null) {
			dao.insert(obj);
		} else {
			dao.update(obj);
		}
	}

	public void remove(Seller obj) {
		dao.deleteById(obj.getId());
		// remover departamento do banco de dados;
	}
}
