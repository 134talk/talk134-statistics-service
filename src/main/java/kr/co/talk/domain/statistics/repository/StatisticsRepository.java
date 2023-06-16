package kr.co.talk.domain.statistics.repository;

import org.socialsignin.spring.data.dynamodb.repository.EnableScan;
import org.springframework.data.repository.CrudRepository;

@EnableScan
public interface StatisticsRepository extends CrudRepository<StatisticsRepository, Long> {

}
