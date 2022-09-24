package com.example.stock_dividend.service;

import com.example.stock_dividend.exception.impl.NoCompanyException;
import com.example.stock_dividend.model.Company;
import com.example.stock_dividend.model.Dividend;
import com.example.stock_dividend.model.ScrapedResult;
import com.example.stock_dividend.model.constants.CacheKey;
import com.example.stock_dividend.persist.entity.CompanyEntity;
import com.example.stock_dividend.persist.entity.DividendEntity;
import com.example.stock_dividend.persist.repository.CompanyRepository;
import com.example.stock_dividend.persist.repository.DividendRepository;
import lombok.AllArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class FinanceService {

    private final CompanyRepository companyRepository;
    private final DividendRepository dividendRepository;

    @Cacheable(key = "#companyName",value = CacheKey.KEY_FIFNACE)
    public ScrapedResult getDividendByCompanyName(String companyName){

        //1.회사명을 기준으로 회사 정보를 조회
        CompanyEntity company =  companyRepository.findByName(companyName)
                .orElseThrow( () -> new NoCompanyException());

        //2. 조회된 회사 ID로 배당금 정보 조회
        List<DividendEntity> dividendEntities = dividendRepository.findAllByCompanyId(company.getId());

        //3. 결과 조합 후 반환
        List<Dividend> dividends = dividendEntities.stream().map(x-> new Dividend(x.getDate(),x.getDividend())).collect(Collectors.toList());


        return new ScrapedResult(new Company(company.getTicker(),company.getName()),dividends);
    }

}
