package com.example.stock_dividend;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.EnableScheduling;

import java.io.IOException;

@EnableScheduling
@EnableCaching
@SpringBootApplication
public class StockDividendApplication {

	public static void main(String[] args) {

		SpringApplication.run(StockDividendApplication.class, args);

	}

}
