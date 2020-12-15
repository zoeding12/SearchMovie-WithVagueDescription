package edu.inforetrieval.zoe;

import com.alibaba.fastjson.JSON;
import org.apache.http.HttpHost;
import org.apache.lucene.analysis.fr.FrenchLightStemFilter;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.index.query.BoolQueryBuilder;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.SearchHit;
import org.elasticsearch.search.builder.SearchSourceBuilder;
import org.elasticsearch.search.sort.ScoreSortBuilder;
import org.elasticsearch.search.sort.SortOrder;
import org.elasticsearch.search.suggest.term.TermSuggestion;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

public class SearchMovies {

    // a map of genres of all genres exist in the data set
    public static Map<String, String> GENRES = new HashMap<>();
    public static RestHighLevelClient client;



    public static List<Movie> runSearch(String query, String minRate){
        initialize();
        double minimumRate = 6.0;
        
        try {
        	minimumRate = Double.parseDouble(minRate);
        }catch (Exception e) {
        	System.out.println("Exception: the input cannot be parsed to double. Use default.");
        }
        return search(query, minimumRate);
    }

    public static void initialize(){
        // build the map of genres
        GENRES.put("drama", "Drama");
        GENRES.put("comedy", "Comedy");
        GENRES.put("romance", "Romance");
        GENRES.put("action", "Action");
        GENRES.put("thriller", "Thriller");
        GENRES.put("crime", "Crime");
        GENRES.put("horror", "Horror");
        GENRES.put("adventure", "Adventure");
        GENRES.put("mystery", "Mystery");
        GENRES.put("family", "Family");
        GENRES.put("fantasy", "Fantasy");
        GENRES.put("sci-fi", "Sci-Fi");
        GENRES.put("science fiction", "Sci-Fi");
        GENRES.put("biography", "Biography");
        GENRES.put("history", "History");
        GENRES.put("war", "War");
        GENRES.put("animation", "Animation");
        GENRES.put("musical", "Musical");
        GENRES.put("music", "Music");
        GENRES.put("western", "Western");
        GENRES.put("sport", "Sport");
        GENRES.put("film-noir", "Film-Noir");
        GENRES.put("reality-TV", "Reality-TV");
        GENRES.put("adult", "Adult");
        GENRES.put("documentary", "Documentary");
        GENRES.put("news", "News");

        // connect to the elasticsearch client
        client = new RestHighLevelClient(
                RestClient.builder(
                        new HttpHost("localhost", 9200, "http"),
                        new HttpHost("localhost", 9201, "http")));



    }

    public static Map<String, Integer> searchByGenre(String[] queries){

        Map<String, Integer> genreFound = new HashMap<>();

        for(int i = 0; i < queries.length; i++){
            if(GENRES.containsKey(queries[i])){
                genreFound.put(GENRES.get(queries[i]), i);
            }
        }

        // return null if genre not found
        if(genreFound.size() == 0) return null;

        return genreFound;
    }

    public static List<Movie> search(String query, double minRate){

        String[] queries = parseQuery(query);

        Map<String, Integer> genreFound = searchByGenre(queries);

        String newQuery = null;
        BoolQueryBuilder queryBuilder = null;

        if(genreFound == null){
            // search without using higher weight for genre
            StringBuilder sb = new StringBuilder();
            for(String q : queries){
                sb.append(q + " ");
            }
            sb.deleteCharAt(sb.length() - 1);
            newQuery = sb.toString();
            // QueryBuilder for other fields
            queryBuilder = QueryBuilders.boolQuery();
            queryBuilder.should(QueryBuilders.matchQuery("title", newQuery).boost(1))
                    .should(QueryBuilders.matchQuery("original_title", newQuery).boost(3))
                    .should(QueryBuilders.matchQuery("actors", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("director", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("writer", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("description", newQuery).boost(4))
                    .should(QueryBuilders.matchQuery("plot", newQuery).boost(4))
                    .should(QueryBuilders.matchQuery("genre", newQuery).boost(3))
            		.must(QueryBuilders.rangeQuery("avg_vote").gte(minRate));

        }else{
            // query words without the genre
            StringBuilder sb = new StringBuilder();
            for(String q : queries){
                if(!genreFound.containsKey(GENRES.get(q))){
                    sb.append(q + " ");
                }
            }
            sb.deleteCharAt(sb.length() - 1);
            newQuery = sb.toString();

            // search with genre as a filter
            queryBuilder = QueryBuilders.boolQuery();

            // QueryBuilder for genre
            BoolQueryBuilder genreBuilder = QueryBuilders.boolQuery();
            for(String genre : genreFound.keySet()){
                genreBuilder.should(QueryBuilders.termQuery("genre", genre));
            }

            // TODO: if genreBuilder cannot retrieve enough movies, don't limit with the MUST
            queryBuilder.must(genreBuilder);

            queryBuilder.should(QueryBuilders.matchQuery("title", newQuery).boost(1))
                    .should(QueryBuilders.matchQuery("original_title", newQuery).boost(3))
                    .should(QueryBuilders.matchQuery("actors", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("director", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("writer", newQuery).boost(2))
                    .should(QueryBuilders.matchQuery("description", newQuery).boost(4))
                    .should(QueryBuilders.matchQuery("plot", newQuery).boost(4))
                    .must(QueryBuilders.rangeQuery("avg_vote").gte(minRate));

        }

        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        // sort with 1. relevance score; 2. average vote score from the dataset; 3. votes got from the dataset
        searchSourceBuilder.query(queryBuilder)
                .sort("_score", SortOrder.DESC)
                .sort("avg_vote", SortOrder.DESC)
                .sort("votes", SortOrder.DESC);

        // set the searching index
        SearchRequest searchRequest = new SearchRequest("movie_info");

        // Add SourceBuilder to the SearchRequest
        searchRequest.source(searchSourceBuilder);

        SearchResponse response = null;
        try {
            response = client.search(searchRequest, RequestOptions.DEFAULT);
        } catch (IOException e) {
            e.printStackTrace();
        }
        SearchHit[] searchHits = response.getHits().getHits();

        List<Movie> results =
                Arrays.stream(searchHits)
                        .map(hit -> JSON.parseObject(hit.getSourceAsString(), Movie.class))
                        .collect(Collectors.toList());

        for(Movie movie : results){
            System.out.println(movie.getOriginal_title() + " || " + movie.getAvg_vote() + " || " + movie.getGenres());
        }

        // close the client
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        
        return results;

    }

    public static String[] parseQuery(String query){
        String[] result = query.split(",");
        for(int i = 0; i < result.length; i++){
            result[i] = result[i].trim().toLowerCase();
        }
        return result;
    }
}
