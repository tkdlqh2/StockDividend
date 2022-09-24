package com.example.stock_dividend.scheduler;

import com.example.stock_dividend.model.Company;
import com.example.stock_dividend.model.ScrapedResult;
import com.example.stock_dividend.model.constants.CacheKey;
import com.example.stock_dividend.persist.entity.CompanyEntity;
import com.example.stock_dividend.persist.entity.DividendEntity;
import com.example.stock_dividend.persist.repository.CompanyRepository;
import com.example.stock_dividend.persist.repository.DividendRepository;
import com.example.stock_dividend.scraper.Scraper;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@EnableCaching
@AllArgsConstructor
@Component
public class ScraperScheduler {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    private final Scraper yahooFinanceScraper;

    @CacheEvict(value = CacheKey.KEY_FIFNACE, allEntries = true)
    @Scheduled(cron = "${scheduler.scrap.yahoo}")
    public void yahooFinanceScheduling(){
        log.info("scrapping scheduler is started");
        //회사 이름 모두 가져오기
        List<CompanyEntity> companies = companyRepository.findAll();

        // 회사마다 배당금 정보를 새로 스크래핑
        for(CompanyEntity company : companies){
            ScrapedResult scrapedResult = yahooFinanceScraper.scrap(new Company(company.getTicker(), company.getName()));
            //스크래핑한 배당금 정보 중 데이터베이스에 없는 값을 저장
            scrapedResult.getDividendList().stream()
                    .map(e-> new DividendEntity(company.getId(),e))
                    .forEach(e->{
                        boolean exists = this.dividendRepository.existsByCompanyIdAndDate(e.getCompanyId(),e.getDate());
                        if(!exists){
                            dividendRepository.save(e);
                        }
                    });

            //연속적으로 서버에 요청을 날리지 않도록 일시정지
            try{
                Thread.sleep(3000);
            } catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
        }
    }

}
