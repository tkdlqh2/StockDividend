package com.example.stock_dividend.scraper;

import com.example.stock_dividend.model.Company;
import com.example.stock_dividend.model.ScrapedResult;

public interface Scraper {
    public Company scrapCompanyByTicker(String ticker);
    public ScrapedResult scrap(Company company);
}
