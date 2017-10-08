package prs.controllers;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import prs.business.Product;
import prs.business.User;
import prs.business.Vendor;
import prs.db.PRSFactory;
import prs.db.ProductDAO;
import prs.db.UserDAO;
import prs.db.VendorDAO;

@WebServlet("/login")
public class PrsLoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	public PrsLoginServlet() {
	}
	
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		
		//get action parameters
		ServletContext sc = getServletContext();
		String action = request.getParameter("action");
		
		
		//LOGIN********************************************************************************************
		if (action.equals("login")) {
		//set thread safe instance variables.
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		
		//call UserDAO object
		UserDAO dao = PRSFactory.getUserDAO();
		VendorDAO vdao = PRSFactory.getVendorDAO();
		
		//set url with loginValidator method.
		String url = dao.loginValidator(username, password);
		
		//get user first name, last name, privileges
		User user  = dao.getUserObject(username);
		
		//set request object
		request.setAttribute("user", user);
		//check if session exists else create session
		
		
		if (url.equalsIgnoreCase("/dashboard.jsp")) {
			
		HttpSession session = request.getSession(false);
		
			if (session == null) {
				System.out.println("No Session Found");
			} else {
				System.out.println("Current Session Found: " + session.getId());
			}
			
		session = request.getSession(true);
		
		System.out.println("Session ID: " + session.getId());
		
		
		//create and set vendors hash map with method below...
		Map<Integer, Vendor> vendors = setVendorHashMap();
		Map<Integer, Product> productHash = setProductHashMap();
		ArrayList<Vendor> vendor2 = vdao.listAllVendors();
		
		
		
		//set session attributes
		session.setAttribute("vendor2", vendor2);
		session.setAttribute("user", user);
		session.setAttribute("vendors", vendors);
		session.setAttribute("productHash", productHash);
		
		} else {
			System.out.println("No Session Created");
			}	
		//forward user to appropriate page.
		getServletContext().getRequestDispatcher(url).forward(request, response);	
	
		
		
		//LOGOUT******************************************************************************************** 
		} else if (action.equals("logout")) {
		
			System.out.println("Logging off...");
			String url = "";
			
			
				url = "/index.html";
				System.out.println("Closing Session...");
				
				//get and invalidate session
				HttpSession session = request.getSession();  
				session.removeAttribute("user");
				session.removeAttribute("vendors");
				session.removeAttribute("products");
	            session.invalidate();  
				
				System.out.println("Logged off.");
		    
			sc.getRequestDispatcher(url)
	        .forward(request, response);
	}
}



	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doPost(request, response);
	}
		
	
	//method to create Vendor HashMap
	protected static Map<Integer, Vendor> setVendorHashMap() {
		
		VendorDAO vDao = PRSFactory.getVendorDAO();	
		ArrayList<Vendor> vendors = vDao.listAllVendors();
		//create an empty hash map
		Map<Integer,Vendor> vendorMap = new HashMap<>();
		//loop to add key and values
		for(Vendor v: vendors) {
			vendorMap.put(v.getId(),v);
		}
		return vendorMap;
	}
	
	private Map<Integer, Product> setProductHashMap() {
		
		ProductDAO productDAO = PRSFactory.getProductDAO();
		ArrayList<Product> productL = productDAO.listAllProducts();
		//create an empty hash map
		Map<Integer, Product> productMap = new HashMap<>();
		
		//create loop to add values.
		
		for(Product p: productL) {
			productMap.put(p.getId(), p);
		}
		return productMap;
	}
	
	
}
