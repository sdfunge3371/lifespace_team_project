package com.lifespace.service;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import com.lifespace.constant.FaqHashtag;

@Service
public class FaqRedisService {

	// StringRedisTemplate : Spring 內建的 Redis 操作工具類，專門用來操作字串格式的資料
	// 常用於快取 FAQ 資料、記錄登入狀態、熱門查詢等用途
	// 內建支援多種 Redis 結構操作（例如：opsForValue, opsForHash, opsForList...）
	@Autowired
	private StringRedisTemplate redisTemplate;

	// --------測試Redis連線--------
	public void testConnection() {
		redisTemplate.opsForValue().set("testConnection", "OK");
		String result = redisTemplate.opsForValue().get("testConnection");
		System.out.print("恭喜連線狀態: " + result);
	}

	// --------將FAQ資料寫入Redis的Hash結構--------
	public void addFaq(FaqHashtag hashtag, String question, String answer) {

		// 建立Redis的Key(hashtag.name() 會回傳 Enum 常數名稱)
		String redisKey = "faq:" + hashtag.name();

		// 使用Redis Hash結構將FAQ的問題與答案形式存入對應的分類
		redisTemplate.opsForHash().put(redisKey, question, answer);
	}

	// --------呼叫addFaq()寫入多筆資料進Redis(這是資料初始化時，上線會關閉)--------
	public void initFaqData() {

		// 先清空所有分類的資料
		for (FaqHashtag tag : FaqHashtag.values()) {
			redisTemplate.delete("faq:" + tag.name());
		}

		// USAGE(使用規範)、RENTAL(租賃相關)、PAYMENT(付款方式)、OTHER(其他)
		List<Runnable> faqInsertions = List.of(() -> addFaq(FaqHashtag.USAGE, "LifeSpace入住與離場",
				"房間密碼將於預訂時間前5分鐘 生效，若提早進入，將收取 NT$1,000 罰款。\n" + "預訂時間結束後，請準時離場，若超時 10 分鐘內將不另收費，但超過 15 分鐘則以半小時為單位計費。\n"
						+ "若需延長使用時間，請提前於 30 分鐘前聯繫客服，以確保場地可延長使用。"),
				() -> addFaq(FaqHashtag.USAGE, "LifeSpace 空間介紹",
						"'LifeSpace 是一個多功能彈性租賃空間，適合 辦公、娛樂、會議、創作，或任何你需要專屬空間的時刻！\n"
								+ "透過線上預訂，即可輕鬆入場，無需繁瑣對接，讓你隨時擁有一個自在、靈活的專屬空間，盡情享受你的 Life Space！"),
				() -> addFaq(FaqHashtag.OTHER, "如要辦活動如何場勘？",
						"1.如何預約場勘？\n" + "透過官方網站 / 客服提前預約場勘時間。\n" + " 場勘時長約30分鐘，免費參觀與測試場地設備;如超過30分鐘則酌收額外費用。\r\n"
								+ " 2.場勘時可確認的事項：\r\n" + " 空間大小、可容納人數；設備（投影機、音響、桌椅擺設等）；是否有額外需求（餐飲、垃圾處理等）。\r\n"
								+ "*建議提前3-5天預約場勘，以確保有空檔安排！"),
				() -> addFaq(FaqHashtag.OTHER, "安全與保險",
						"LifeSpace不負責租用者在場內的個人物品，如有遺失、被竊或損壞，本公司不承擔任何保管及賠償責任。\n"
								+ " LifeSpace 已投保公共意外責任險，若因場地問題導致意外，我們將依保險規範提供理賠服務。\r\n" + " 其他使用規範與罰則，請參考使用者條款。"),
				() -> addFaq(FaqHashtag.USAGE, "使用加時、超時規則說明",
						"若需延長使用時間，請在活動結束前，前往「密碼頁面」或「我的訂單頁面」，點選延長加時。\n"
								+ " 若未及時加時，導致影響下一位使用者或管家清潔作業，將收取超時費用。未於規範時間內完成延長加時，將無法繼續使用空間，請準時離場。若需再次使用，請重新預約場地。\r\n"
								+ " 超時費用以每 30 分鐘為單位計算，並收取2 倍的該空間租賃費用。詳細規範請參閱**「使用者條款」第66條超時費用**。"),
				() -> addFaq(FaqHashtag.RENTAL, "租賃空間如何計價",
						"LifeSpace 採時計計費，最低租用時間為 1 小時，之後以 30 分鐘為單位計費。\n" + " 平日 / 假日：費用可能不同，詳細費率可於預訂頁面 查詢。\r\n"
								+ " 超時費用：超時10分鐘內不加收，超過15分鐘依半小時計費。\n" + " 延長租用：請於30分鐘前 聯繫客服確認是否可延長。"),
				() -> addFaq(FaqHashtag.PAYMENT, "付款以及發票的開立方式？",
						"一般租用，有提供線上刷卡、街口支付、AFTEE先享後付。若有企業月結需求，請與LifeSpace客服聯繫。\n"
								+ "儲值點數，以點數租用空間，則提供網路刷卡、ATM轉帳、超商繳納等多元付款方式。\r\n"
								+ "  發票僅提供「雲端發票」，請您在儲值點數、預訂付款時確認資料無誤或輸入統編，付款完成後，系統會自動寄送給您。"),
				() -> addFaq(FaqHashtag.OTHER, "防詐騙資訊提醒",
						"提醒您，請警惕以下常見詐騙手法：\n" + " 1.訂單設定錯誤或金額異常；\n" + " 2.利用分期付款、每月扣款誘使您修改付款方式；\n" + " 3.重複訂購多筆商品；\n"
								+ " 4.故意製造宅配或超商配送、簽收文件錯誤（如簽收單誤簽為簽帳單、條碼刷錯、重複扣款）；\n"
								+ " 5.詐騙分子往往以“更改訂單並要求退費”為藉口，要求您操作 ATM 進行資金轉移。\n" + " 若接獲可疑來電，請立即撥打 165 反詐騙諮詢專線求助。"),
				() -> addFaq(FaqHashtag.RENTAL, "LifeSpace 提供哪些公用設備與設施？",
						"1.基本設備\n" + " 高速 Wi-Fi：穩定網路，方便工作與娛樂。\n" + " 空調與暖氣：維持舒適的室內溫度。\n" + " 電源插座與 USB 充電站：隨時為設備充電。\n"
								+ " 智慧門禁系統：電子門鎖，安全有保障。\n" + "  監視系統：公共區域監控，提升安全性。\n" + " 2.公共空間:\n"
								+ " 有公共廁所，但衛生紙需自行攜帶。\n" + " 設有垃圾桶或垃圾集中區。 若垃圾量較多，請自備垃圾袋並放置於集中區。\n"
								+ " 垃圾處理規範：部分垃圾可能有丟棄限制，詳情請參閱使用規範。若擔心垃圾超過限制，請於預訂前確認是否可代收或需額外清潔費用。\n"
								+ " 部分空間提供飲水機，部分則需自行攜帶飲水。 詳情請參閱各空間說明。"),
				() -> addFaq(FaqHashtag.USAGE, "使用規範",
						"保持適當音量，避免大聲喧嘩、跑跳，以維護場地周邊環境安寧。使用空間時，請妥善愛惜場地設施，避免破壞裝潢與家具，如有損毀，將依照損害程度酌收維修費用。\n"
								+ " 使用後請恢復原狀，物品歸位、擦拭桌面、白板擦除，丟棄垃圾請參考現場使用須知，留給下一位使用者乾淨舒適的環境。離開時，請關閉電燈、冷氣，確保所有設備皆已關閉，以節約能源。"),
				() -> addFaq(FaqHashtag.OTHER, "注意事項",
						"由於空間使用者的故意或過失，導致LifeSpace遭受損害時，LifeSpace將向該使用者要求賠償。空間使用者於租用開始之前、中、後，任何放置於LifeSpace空間內、公共區域之物品，LifeSpace將不負責任何保管及賠償責任。\n"
								+ "LifeSpace保留主動刪除空間使用者預訂之權利。\n" + " *若違反上述規定，LifeSpace 有權立即終止租賃，並取消未來預約，且不退費！"),
				() -> addFaq(FaqHashtag.OTHER, "自助式空間如何清潔？",
						"使用結束後，請遵守「使用後恢復原狀」原則，讓下一位使用者也能擁有舒適的環境！\n" + " 清潔步驟如下：\n" + " 垃圾分類丟棄（垃圾桶位置請參考現場指引）。\n"
								+ " 擦拭桌面 & 白板，恢復使用前狀態。\n" + " 歸位家具 & 設備（如桌椅、遙控器等）。\n" + " 關閉電燈 & 冷氣，節約能源。\n"
								+ " *若未完成清潔，可能會收取 NT$600-NT$5,000清潔費，請共同維護空間品質！"));
		// 執行所有 FAQ 寫入
		faqInsertions.forEach(Runnable::run);

		// 自動統計 FAQ 筆數
		System.out.println("Redis FAQ 初始化完成，共匯入 " + faqInsertions.size() + " 筆 FAQ");
	}

	// ------取得redis對應的Hash結構------
	public Map<String, String> getFaqByHashtag(String displayName) {
		// 先將傳入的分類文字轉換為對應的 Enum（ex: 使用規範 → USAGE）
		Optional<FaqHashtag> tagOpt = FaqHashtag.fromDisplayName(displayName);

		if (tagOpt.isEmpty()) {
			// 如果轉換失敗（例如傳入亂碼），就回傳空 Map
			return Collections.emptyMap();
		}
		// 取得正確分類
		FaqHashtag tag = tagOpt.get();
		// 組成 Redis 的 Key，例如 faq:USAGE
		String redisKey = "faq:" + tag.name();

		// 從 Redis Hash 中取得所有 FAQ（key=問題, value=答案）
		Map<Object, Object> redisData = redisTemplate.opsForHash().entries(redisKey);

		// 轉型為 Map<String, String>，因為 Redis 回傳的是 Object 型別
		return redisData.entrySet().stream()
				.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
	}
}
