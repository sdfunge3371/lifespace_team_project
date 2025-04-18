$(document).ready(function () {
  // 一進頁面就載入"全部"分類，並幫"全部"按鈕加上 active 樣式
  loadAllFaqs();

  // 使用者點擊各分類按鈕
  $(".hashtag-button").click(function () {
    // 切換按鈕樣式：先清除所有，再加上目前這顆
    $(".hashtag-button").removeClass("active");
    $(this).addClass("active");

    const tag = $(this).text().trim(); // 取得按鈕文字（分類名稱）

    if (tag === "全部") {
      // 點"全部"就載入 MySQL 的全部 FAQ
      loadAllFaqs();
    } else {
      // 其他分類從 Redis 載入對應資料
      $.get("/member/faq/hashtag", { tag: tag }, renderFaqFromRedis);
    }
  });

  // -------------------------------
  // 載入全部 FAQ 的小函式
  function loadAllFaqs() {
    $.get("/member/faq/query", function (data) {
		// 1. 渲染 MySQL FAQ
      renderFaq(data);                      
      // 2. 重設並標示"全部"按鈕
      $(".hashtag-button").removeClass("active");
      $(".hashtag-button").first().addClass("active");
    });
  }

  // -------------------------------
  // 渲染 MySQL FAQ（全部分類）
  function renderFaq(data) {
    $("#faqAccordion").empty(); // 清空現有畫面

    data.forEach((faq, index) => {
		// 動態生成 collapse ID
      const collapseId = `faq${index}`;  
      const headingId  = `heading${index}`;

      // 使用 Bootstrap Accordion 的結構，帶上 .faq-question 及 data-faq-id
      const html = `
        <div class="accordion-item">
          <h2 class="accordion-header" id="${headingId}">
            <button class="accordion-button collapsed faq-question"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#${collapseId}"
              aria-expanded="false"
              aria-controls="${collapseId}"
              data-faq-id="${faq.faqId}">
              ${faq.faqAsk}
            </button>
          </h2>
          <div id="${collapseId}" class="accordion-collapse collapse"
               aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
            <div class="accordion-body">${faq.faqAnswer}</div>
          </div>
        </div>`;

      $("#faqAccordion").append(html);
    });

    // 綁定 FAQ 點擊事件以送 GA，只包含 faq_id
    bindFaqClickEvent();
  }

  // -------------------------------
  // 渲染 Redis FAQ（點分類按鈕後呼叫）
  function renderFaqFromRedis(data) {
	// 清空現有畫面
    $("#faqAccordion").empty(); 
	// FAQ 索引計數器，用於生成唯一 ID
    let index = 0;               

    for (const [faqId, jsonString] of Object.entries(data)) {
		// 解析 JSON，取得 question & answer
      const faqData = JSON.parse(jsonString); 
      const question = faqData.question;
      const answer   = faqData.answer;

      const collapseId = `faqRedis${index}`;
      const headingId  = `headingRedis${index}`;

      const html = `
        <div class="accordion-item">
          <h2 class="accordion-header" id="${headingId}">
            <button class="accordion-button collapsed faq-question"
              type="button"
              data-bs-toggle="collapse"
              data-bs-target="#${collapseId}"
              aria-expanded="false"
              aria-controls="${collapseId}"
              data-faq-id="${faqId}">
              ${question}
            </button>
          </h2>
          <div id="${collapseId}" class="accordion-collapse collapse"
               aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
            <div class="accordion-body">${answer}</div>
          </div>
        </div>`;

      $("#faqAccordion").append(html);
      index++;
    }

    // 綁定 FAQ 點擊事件送GA，只包含faq_id
    bindFaqClickEvent();
  }

  // -------------------------------
  // 綁定.faq-question的GA追蹤事件(GA記錄faq_click事件)(html要先載入gtag.js)
  function bindFaqClickEvent() {
	// 先解除舊的，再綁新的，避免重複綁到同一個按鈕
    $(".faq-question").off("click").on("click", function () {
		// 讀取自訂屬性 faq-id(ex:FAQ01)
        const faqId = $(this).data("faq-id");

        if (typeof gtag === "function") {
			// 把每次點擊推到GA
			// 事件名稱要跟GA裡設定的自訂事件name一
			//GA會自動把這些事件送到G-2NM42NTB06這個專案底下
          gtag("event", "faq_click", {		
            event_category: "FAQ",
			// 自訂維度：FAQ的ID
            faq_id: faqId                  
          });
          console.log("FAQ 點擊事件已送出 →", faqId);
        }
      });
  }

});
