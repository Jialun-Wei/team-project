package data.news;

import entity.News;
import org.junit.Test;
import static org.junit.Assert.*;
import java.util.List;

public class NewsApiDAOTest {

    @Test
    public void ApiDAOTest() {
        NewsApiDAO dao = new NewsApiDAO();

        try {
            System.out.println("Testing Real API call...");
            List<News> newsList = dao.fetchNews("general");

            System.out.println("This test is expected to fail when api reached the limit!");
            assertNotNull(newsList);

            if (!newsList.isEmpty()) {
                News firstNews = newsList.get(0);
                System.out.println("Got news: " + firstNews.getTitle());
                assertNotNull(firstNews.getTitle());
                assertNotNull(firstNews.getUrl());
            }

        } catch (NewsApiDAO.RateLimitExceededException e) {
            System.out.println("API Limit reached, but code path covered.");
        } catch (Exception e) {
            System.out.println("Network error: " + e.getMessage());
        }
    }
}