package model.services;

import java.util.ArrayList;
import java.util.List;

import model.entities.Department;

public class DepartmentService {

		public List<Department> findAll(){
			// aqui é um MOCK, somente instanciação de teste, sem relacionamento com o servidor;
			List<Department> list = new ArrayList<>();
			list.add(new Department(1, "Books"));
			list.add(new Department(2, "Computers"));
			list.add(new Department(3, "Electronics"));
			return list;
		}
		
		//após isso, declarar uma dependencia desse servico no controller do DepartmentListController, para carregar essa lista na view; 
}
