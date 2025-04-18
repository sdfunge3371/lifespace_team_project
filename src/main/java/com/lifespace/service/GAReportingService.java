package com.lifespace.service;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import com.google.analytics.data.v1beta.BetaAnalyticsDataClient;
import com.google.analytics.data.v1beta.BetaAnalyticsDataSettings;
import com.google.analytics.data.v1beta.DateRange;
import com.google.analytics.data.v1beta.Dimension;
import com.google.analytics.data.v1beta.Metric;
import com.google.analytics.data.v1beta.Row;
import com.google.analytics.data.v1beta.RunReportRequest;
import com.google.analytics.data.v1beta.RunReportResponse;
import com.google.api.gax.core.CredentialsProvider;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.auth.oauth2.ServiceAccountCredentials;
import com.lifespace.dto.FaqGaEventDTO;
import com.lifespace.entity.Faq;
import com.lifespace.repository.FaqRepository;

//查詢點擊事件報表，從Google Analytics（GA4）API撈出faq_click點擊事件的熱門次數排行
@Service
public class GAReportingService {

	@Autowired
	private FaqRepository repository;

	private static final String GA_PROPERTY_ID = "485426463"; // 資源ID(查詢此屬性下的報表資料)
	private final BetaAnalyticsDataClient analyticsData;

	// 建構子：初始化 GA 授權與連線
	public GAReportingService(@Value("${ga.service-account-key}") String keyPath) throws Exception {
		// 透過環境變數或 application.properties 傳入金鑰路徑
		this.analyticsData = initializeGaClient(keyPath);
	}

	// 初始化Google Analytics的Client(金鑰檔案路徑，連接到GA伺服器，讓Java程式可以去要報表資料)
	private BetaAnalyticsDataClient initializeGaClient(String keyPath) throws Exception {
		InputStream serviceAccountStream;

		if (keyPath.startsWith("classpath:")) {
			// 從 resources 資料夾讀取金鑰（開發時使用）
			String pathInClasspath = keyPath.replace("classpath:", "");
			serviceAccountStream = getClass().getClassLoader().getResourceAsStream(pathInClasspath);
			if (serviceAccountStream == null) {
				throw new RuntimeException("找不到 classpath 中的金鑰檔案: " + pathInClasspath);
			}
		} else if (keyPath.startsWith("file:")) {
			// 從檔案路徑讀取金鑰（正式部署或 ngrok 用）
			String filePath = keyPath.replace("file:", "");
			serviceAccountStream = new FileInputStream(filePath);
		} else {
			throw new IllegalArgumentException("金鑰路徑格式錯誤，請確認讀取路徑!");
		}

		// 解析金鑰成GoogleCredentials(憑證物件)
		GoogleCredentials credentials = ServiceAccountCredentials.fromStream(serviceAccountStream);

		// 用金鑰建立 GA 服務設定
		BetaAnalyticsDataSettings.Builder settingsBuilder = BetaAnalyticsDataSettings.newBuilder();
		settingsBuilder.setCredentialsProvider(new CredentialsProvider() {
			@Override
			public GoogleCredentials getCredentials() {
				return credentials; // 這邊回傳我們剛剛解析出來的憑證
			}
		});

		BetaAnalyticsDataSettings settings = settingsBuilder.build();

		// 初始化 GA 客戶端
		return BetaAnalyticsDataClient.create(settings);
	}

	// 主方法：取得熱門事件報表資料(建立查詢的報表條件)
	public List<FaqGaEventDTO> getTopEvents(String startDate, String endDate, String eventName, int limit) {
		// 1️. 建立 GA 請求條件
		RunReportRequest request = RunReportRequest.newBuilder()
				// 1-1 設定要查詢的 GA4 資源ID（格式固定為 properties/你的資源 ID）
				.setProperty("properties/" + GA_PROPERTY_ID)

				// 1-2 設定查詢的時間範圍，addDateRanges: 新增一組日期範圍
				.addDateRanges(DateRange.newBuilder().setStartDate(startDate) // ex: "2025-04-01"
						.setEndDate(endDate) // ex: "2025-04-30"
						.build())

				// 1-3 設定維度(要分組統計的欄位)，這裡只要 FAQ ID
				.addDimensions(Dimension.newBuilder().setName("customEvent:faq_id") // 自訂維度：faq_id
						.build())

				// 1-4 設定指標(要統計的數值)，這裡是事件次數
				.addMetrics(Metric.newBuilder().setName("eventCount") // 自訂指標：eventCount
						.build())

				// 1-5 最後 build成一個實體的Request
				.build();

		// 2. 發送請求給GA，取得回傳資料
		// 取得GA回應（GA4 Data API自動依eventCount降冪回傳）
		// RunReportResponse來自Google Analytics Data API Client SDK
		RunReportResponse response = analyticsData.runReport(request);

		// 3. 將GA回應資料整理成DTO
		List<FaqGaEventDTO> result = new ArrayList<>();
		// GA 回傳的所有資料列
		List<Row> rows = response.getRowsList();

		// 只取前 limit 筆（若 GA 回傳少於 limit，就通通取完）
		int max = Math.min(limit, rows.size());
		for (int i = 0; i < max; i++) {
			// Row由GA SDK提供，每一個Row物件代表GA回傳報表裡的一筆"分群後的結果"
			Row row = rows.get(i);

			// 解析維度與指標
			// 拿到第 0 維度的字串值，customEvent:faq_id
			String faqId = row.getDimensionValues(0).getValue();
			// 拿到第 0 指標的字串值，eventCount
			int count = Integer.parseInt(row.getMetricValues(0).getValue());

			// 用faqId查詢資料庫，取得對應的FaqAsk
			Faq faq = repository.findById(faqId).orElse(null);
			String faqTitle = (faq != null ? faq.getFaqAsk() : "");

			result.add(new FaqGaEventDTO(faqId, faqTitle, count));
		}

		// 直接回傳已按照點擊次數高→低的前5筆(FaqAdminController)
		return result;
	}
}
