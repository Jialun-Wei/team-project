package data.news;

import entity.News;

import java.util.List;

public class NewsApiDAOTest {
    public static void main(String[] args) {
        NewsApiDAO dao = new NewsApiDAO();

        try {
            // 调用 DAO 获取新闻
            List<News> newsList = dao.fetchNews(null);

            if (newsList.isEmpty()) {
                System.out.println("No news fetched.");
            } else {
                System.out.println("Fetched news:");
                newsList.stream()
                        .limit(5) // 只打印前 5 条
                        .forEach(news -> {
                            System.out.println("Title: " + news.getTitle());
                            System.out.println("URL: " + news.getUrl());
                            System.out.println("Published: " + news.getTimePublished());
                            System.out.println("---------------------------");
                        });
            }

        } catch (NewsApiDAO.RateLimitExceededException e) {
            // 捕获限流异常，打印提示信息
            System.err.println("API rate limit reached:");
            System.err.println(e.getMessage());
        } catch (Exception e) {
            // 捕获其他异常，防止程序直接崩溃
            e.printStackTrace();
        }
    }
}
