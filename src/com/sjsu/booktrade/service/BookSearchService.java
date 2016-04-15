package com.sjsu.booktrade.service;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.codehaus.jackson.JsonFactory;

import com.sjsu.booktrade.model.BooksTO;
import com.sjsu.booktrade.util.ConnectionPool;

/*import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.books.Books;
import com.google.api.services.books.BooksRequestInitializer;
import com.google.api.services.books.Books.Volumes.List;
import com.google.api.services.books.model.Volume;
import com.google.api.services.books.model.Volumes;*/

@Path("/books")
public class BookSearchService {

	@POST
	@Path("/tradeabook")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response tradeabook(BooksTO books) throws Exception{

		boolean addStatus = false;

		if(books.getBookName().trim().length() > 0 && books.getAuthor().trim().length() > 0 && books.getCategory().trim().length() > 0 && 
				books.getPrice() > 0){

			Connection connection = ConnectionPool.getConnectionFromPool();

			PreparedStatement preparedStatement = connection
					.prepareStatement("insert into booktrade.books values(null, ? ,? ,? ,? ,? ,? ,? )");
			preparedStatement.setString(1, books.getBookName());
			preparedStatement.setString(2, books.getAuthor());
			preparedStatement.setInt(3, books.getEdition());

			preparedStatement.setString(4, books.getPickUpOrShip());
			preparedStatement.setDouble(5, books.getPrice());
			preparedStatement.setBoolean(6, false);
			preparedStatement.setString(7, books.getCategory());
			preparedStatement.setInt(8, books.getUser().getUserId());
			preparedStatement.setBoolean(9, true);

			int registerUpdate = preparedStatement.executeUpdate();

			if(registerUpdate==1){

				preparedStatement = connection.prepareStatement("select books.bookId from booktrade.user where books.book_name = ? and books.userId=?");

				preparedStatement.setString(1, books.getBookName());
				preparedStatement.setInt(1, books.getUser().getUserId());
				ResultSet resultSet = preparedStatement.executeQuery();

				if(resultSet.next()){
					books.setBookId(resultSet.getInt("bookId"));
					addStatus = true;
				}

				if(addStatus){
					return Response.status(201).entity(books).build();
				}else{
					return Response.status(500).entity(" failed adding the book please try again "+books.getBookName()).build();
				}
			}else{
				return Response.status(500).entity(" failed adding the book please try again "+books.getBookName()).build();
			}

		}else{
			return Response.status(400).entity(" All fields should be entered, please try again").build();
		}
	}

	@POST
	@Path("/deleteBook")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response deleteBook(int bookId) throws Exception{

		boolean deleteStatus = false;

		Connection connection = ConnectionPool.getConnectionFromPool();

		PreparedStatement preparedStatement = connection
				.prepareStatement("DELETE FROM BOOKTRADE.BOOKS WHERE BOOKID=?");
		preparedStatement.setInt(1, bookId);

		deleteStatus = preparedStatement.execute();

		if(deleteStatus){
			return Response.status(201).entity(bookId).build();
		}else{
			return Response.status(400).entity(" Error occured in deleting a book. Please try again ").build();
		}

	}

	@POST
	@Path("/fetchBookDetails")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fetchBookDetails(int bookId) throws Exception{

		Connection connection = ConnectionPool.getConnectionFromPool();

		PreparedStatement preparedStatement = connection
				.prepareStatement("SELECT * FROM BOOKTRADE.BOOKS WHERE BOOKID=?");
		preparedStatement.setInt(1, bookId);

		ResultSet rs = preparedStatement.executeQuery();
		BooksTO books = new BooksTO();
		while(rs.next()){
			books.setBookName(rs.getString("book_name"));
			books.setBookId(bookId);
			books.setCategory(rs.getString("category"));
			books.setEdition(rs.getInt("edition"));
			books.setPrice(rs.getDouble("price"));
			books.setPickUpOrShip(rs.getString("pickUpOrShip"));
			books.setAuthor(rs.getString("author"));
			books.setUser(UserService.getUserDetailsFromId(rs.getInt("userId")));
		}

		return Response.status(201).entity(books).build();
	}
	
	@POST
	@Path("/fetchAllAvailableBooks")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fetchAllAvailableBooks() throws Exception{

		Connection connection = ConnectionPool.getConnectionFromPool();

		PreparedStatement preparedStatement = connection
				.prepareStatement("SELECT * FROM BOOKTRADE.BOOKS WHERE ISAVAILABLE=1");

		ResultSet rs = preparedStatement.executeQuery();
		List<BooksTO> booksList = new ArrayList<BooksTO>();
		while(rs.next()){
			BooksTO books = new BooksTO();
			books.setBookName(rs.getString("book_name"));
			books.setBookId(rs.getInt("bookId"));
			books.setCategory(rs.getString("category"));
			books.setEdition(rs.getInt("edition"));
			books.setPrice(rs.getDouble("price"));
			books.setPickUpOrShip(rs.getString("pickUpOrShip"));
			books.setAuthor(rs.getString("author"));
			books.setUser(UserService.getUserDetailsFromId(rs.getInt("userId")));
			booksList.add(books);
		}

		return Response.status(201).entity(booksList).build();
	}

	/**\
	 *
	 * JsonFactory jsonFactory = new JacksonFactory();     
    final Books books = new Books(new NetHttpTransport(), jsonFactory);
    List volumesList = books.volumes.list("9780262140874");

    volumesList.setMaxResults((long) 2);

    volumesList.setFilter("ebooks");
    try
    {
        Volumes volumes = volumesList.execute();
        for (Volume volume : volumes.getItems()) 
        {
            VolumeVolumeInfo volumeInfomation = volume.getVolumeInfo();
            System.out.println("Title: " + volumeInfomation.getTitle());
            System.out.println("Id: " + volume.getId());
            System.out.println("Authors: " + volumeInfomation.getAuthors());
            System.out.println("date published: " + volumeInfomation.getPublishedDate());
            System.out.println();
        }

    } catch (Exception ex) {
        // TODO Auto-generated catch block
        System.out.println("didnt wrork "+ex.toString());
    }
	 */
	
	/*@POST
	@Path("/fetchBookViaISBN")
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_JSON)
	public Response fetchBookViaISBN(long isbn) throws Exception{

		JsonFactory jsonFactory = new JsonFactory();  
		String url = "https://www.googleapis.com/books/v1/volumes?q=isbn:"+isbn+"&key=AIzaSyCeePI9-ohjbLSBm-vxDBYm5oIyTcANCPU";
	    final Books books = new Books(new NetHttpTransport(), jsonFactory);
	    List volumesList = books.volumes.list("9780262140874");

	    volumesList.setMaxResults((long) 2);

	    volumesList.setFilter("ebooks");
	    try
	    {
	        Volumes volumes = volumesList.execute();
	        for (Volume volume : volumes.getItems()) 
	        {
	            VolumeInfo volumeInfomation = volume.getVolumeInfo();
	            System.out.println("Title: " + volumeInfomation.getTitle());
	            System.out.println("Id: " + volume.getId());
	            System.out.println("Authors: " + volumeInfomation.getAuthors());
	            System.out.println("date published: " + volumeInfomation.getPublishedDate());
	            System.out.println();
	        }

	    } catch (Exception ex) {
	        // TODO Auto-generated catch block
	        System.out.println("didnt wrork "+ex.toString());
	    }
		BooksTO books = new BooksTO();
		while(rs.next()){
			books.setBookName(rs.getString("book_name"));
			books.setBookId(bookId);
			books.setCategory(rs.getString("category"));
			books.setEdition(rs.getInt("edition"));
			books.setPrice(rs.getDouble("price"));
			books.setPickUpOrShip(rs.getString("pickUpOrShip"));
			books.setAuthor(rs.getString("author"));
			books.setUser(UserService.getUserDetailsFromId(rs.getInt("userId")));
		}

		return Response.status(201).entity(books).build();
	}*/
}
