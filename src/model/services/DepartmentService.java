package model.services;

import java.util.ArrayList;
import java.util.List;

import model.dao.DaoFactory;
import model.dao.DepartmentDao;
import model.entities.Department;

public class DepartmentService {

	private DepartmentDao dao = DaoFactory.createDepartmentDao();
	//relacionando com o BD;
	
	public List<Department> findAll(){
			return dao.findAll();
		}		
		//após isso, declarar uma dependencia desse servico no controller do DepartmentListController, para carregar essa lista na view; 
}
