package kr.co.talk.opensearch;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.opensearch.client.opensearch.OpenSearchClient;
import org.opensearch.client.opensearch._types.FieldValue;
import org.opensearch.client.opensearch.core.IndexRequest;
import org.opensearch.client.opensearch.core.IndexResponse;
import org.opensearch.client.opensearch.core.SearchResponse;
import org.opensearch.client.opensearch.indices.CreateIndexRequest;
import org.opensearch.client.opensearch.indices.CreateIndexResponse;
import org.opensearch.client.opensearch.indices.DeleteIndexRequest;
import org.opensearch.client.opensearch.indices.DeleteIndexResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class OpenSearchClientTest {

    public static final String SAMPLE_INDEX_NAME = "sample-index";
    public static final String SAMPLE_DOCUMENT_ID = "zxkjcgl";

    static class SampleData {
        private String name;

        private int age;

        public SampleData() {}

        public SampleData(String name, int age) {
            this.name = name;
            this.age = age;
        }

        public String getName() {
            return name;
        }

        public int getAge() {
            return age;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setAge(int age) {
            this.age = age;
        }
    }

    @Autowired
    private OpenSearchClient openSearchClient;

    @DisplayName("인덱스 생성 테스트. 인덱스: RDB 테이블과 비슷")
    @Test
    void createIndex() throws IOException {
        // 인덱스 생성 request 객체
        CreateIndexRequest createIndexRequest = new CreateIndexRequest.Builder().index(SAMPLE_INDEX_NAME).build();
        // 인덱스 생성
        CreateIndexResponse response = openSearchClient.indices().create(createIndexRequest);

        assertTrue(response.acknowledged() && response.index().equals(SAMPLE_INDEX_NAME));
    }


    @DisplayName("데이터 인덱싱 테스트. 인덱싱: RDB에서 INSERT와 같은 의미")
    @Test
    void indexData() throws IOException {
        // 인덱싱할 데이터. 내부적으로는 JSON으로 생성됨
        SampleData sampleData = new SampleData("sample name", 50);

        IndexRequest<SampleData> indexRequest = new IndexRequest.Builder<SampleData>()
                .index(SAMPLE_INDEX_NAME) // 어느 인덱스 (테이블)에 넣을 건지
                .id(SAMPLE_DOCUMENT_ID) // id는 보통 임의로 지정하지 않고 자동 생성되는 uuid로 저장함. 테스트를 위해서 id 지정.
                .document(sampleData) // document란 RDB에서 한 row를 의미. 즉 INSERT 할 데이터.
                .build();

        // indexing = INSERT
        IndexResponse indexResponse = openSearchClient.index(indexRequest);

        assertEquals(SAMPLE_DOCUMENT_ID, indexResponse.id());
    }

    @DisplayName("다큐먼트 조회 테스트. 다큐먼트: RDB에서 한 row")
    @Test
    void searchDocument() throws IOException {
        // ElasticSearch query는 문법을 조금 공부해야함..
        SearchResponse<SampleData> response = openSearchClient.search(searchRequest ->
                searchRequest
                        .index(SAMPLE_INDEX_NAME)
                        .query(query -> query
                                .bool(boolQuery -> boolQuery
                                        .filter(filterQuery -> filterQuery
                                                .term(termQuery -> termQuery
                                                        .field("age")
                                                        .value(FieldValue.of(50))
                                                )
                                        )
                                )
                        ), SampleData.class);
        // 설정한 아이디로 저장 됐는지
        assertEquals(SAMPLE_DOCUMENT_ID, response.hits().hits().get(0).id());

        // 저장한 데이터 꺼내오기
        SampleData responseData = response.hits().hits().get(0).source();

        assertNotNull(responseData);
        assertEquals("sample name", responseData.getName());
        assertEquals(50, responseData.getAge());
    }


    @DisplayName("인덱스 삭제 테스트.")
    @Test
    void deleteIndex() throws IOException {
        // 인덱스 삭제란 SQL의 DROP TABLE과 같은 의미.
        DeleteIndexRequest deleteIndexRequest = new DeleteIndexRequest.Builder().index(SAMPLE_INDEX_NAME).build();

        DeleteIndexResponse response = openSearchClient.indices().delete(deleteIndexRequest);

        assertTrue(response.acknowledged());
    }
}
