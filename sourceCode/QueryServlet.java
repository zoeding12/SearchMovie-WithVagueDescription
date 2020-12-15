package edu.inforetrieval.zoe;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.fileupload.FileItem;
import org.apache.commons.fileupload.disk.DiskFileItemFactory;
import org.apache.commons.fileupload.servlet.ServletFileUpload;


/**
 * Servlet implementation class QueryServlet
 */
@WebServlet("/QueryServlet")
public class QueryServlet extends HttpServlet {

    // upload directory
    private static final String UPLOAD_DIRECTORY = "upload";

    
    private static String uploadFileName;
    private static String projectPath;

    /**
     * upload data and save the files
     */
    protected void doPost(HttpServletRequest request,
        HttpServletResponse response) throws ServletException, IOException {

//    	projectPath = getServletContext().getRealPath("/");
//    	
//        
//        String outputFilePath = RankingDocuments.getIndex(projectPath, request.getParameter("query"));
//        request.setAttribute("outputpath", outputFilePath);
//        // go to showRretrieved.jsp
//        getServletContext().getRequestDispatcher("/showRetrieved.jsp").forward(
//                request, response);
    	
    	List<Movie> results = SearchMovies.runSearch(request.getParameter("query"), request.getParameter("minRate"));
    	
    	for(int i = 1; i <= results.size(); i++) {
    		request.setAttribute("title" + i, results.get(i - 1).getOriginal_title());
    		request.setAttribute("description" + i, results.get(i - 1).getDescription());
    		request.setAttribute("rate" + i, results.get(i - 1).getAvg_vote());
    	}
    	
    	//go to showRretrieved.jsp
        getServletContext().getRequestDispatcher("/searchResult.jsp").forward(
              request, response);
    	
    }
}