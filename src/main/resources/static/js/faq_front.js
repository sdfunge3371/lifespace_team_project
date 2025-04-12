$(document).ready(function () {
  // 點擊 hashtag 按鈕
  $(".hashtag-button").click(function () {
    // 切換按鈕樣式
    $(".hashtag-button").removeClass("active");
    $(this).addClass("active");

    const tag = $(this).text().trim();

    // 如果點的是「全部」，就回原本的 MySQL 查詢 API
    if (tag === "全部") {
      $.get("/member/faq/query", renderFaq);
    } else {
      $.get("/member/faq/hashtag", { tag: tag }, renderFaqFromRedis);
    }
  });

  // 顯示 Redis FAQ
  function renderFaqFromRedis(data) {
    $("#faqAccordion").empty();
    let index = 0;
    for (const [question, answer] of Object.entries(data)) {
      const collapseId = `faqRedis${index}`;
      const headingId = `headingRedis${index}`;
      const html = `
        <div class="accordion-item">
          <h2 class="accordion-header" id="${headingId}">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
              data-bs-target="#${collapseId}" aria-expanded="false" aria-controls="${collapseId}">
              ${question}
            </button>
          </h2>
          <div id="${collapseId}" class="accordion-collapse collapse" aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
            <div class="accordion-body">${answer}</div>
          </div>
        </div>`;
      $("#faqAccordion").append(html);
      index++;
    }
  }

  // 顯示 MySQL FAQ（你的原本渲染函式）
  function renderFaq(data) {
    $("#faqAccordion").empty();
    data.forEach((faq, index) => {
      const collapseId = `faq${index}`;
      const headingId = `heading${index}`;
      const html = `
        <div class="accordion-item">
          <h2 class="accordion-header" id="${headingId}">
            <button class="accordion-button collapsed" type="button" data-bs-toggle="collapse"
              data-bs-target="#${collapseId}" aria-expanded="false" aria-controls="${collapseId}">
              ${faq.faqAsk}
            </button>
          </h2>
          <div id="${collapseId}" class="accordion-collapse collapse" aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
            <div class="accordion-body">${faq.faqAnswer}</div>
          </div>
        </div>`;
      $("#faqAccordion").append(html);
    });
  }

  // 預設載入「全部」FAQ
  $.get("/member/faq/query", renderFaq);
});

			
			
			
//  $.ajax({
//    url: "/member/faq/query", 
//    method: "GET",
//    dataType: "json",
//    success: function (data) {
//      $("#faqAccordion").empty(); // 清空原本的 FAQ 區塊
//
//      data.forEach(function (faq, index) {
//        const collapseId = `collapse${index}`;
//        const headingId = `heading${index}`;
//
//        const faqHtml = `
//          <div class="accordion-item">
//            <h2 class="accordion-header" id="${headingId}">
//              <button class="accordion-button collapsed" type="button"
//                      data-bs-toggle="collapse"
//                      data-bs-target="#${collapseId}"
//                      aria-expanded="false"
//                      aria-controls="${collapseId}">
//                ${faq.faqAsk}
//              </button>
//            </h2>
//            <div id="${collapseId}" class="accordion-collapse collapse"
//                 aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
//              <div class="accordion-body">
//                ${faq.faqAnswer}
//              </div>
//            </div>
//          </div>
//        `;
//
//        $("#faqAccordion").append(faqHtml);
//      });
//    },
//    error: function () {
//      $("#faqAccordion").html("<p>無法載入常見問題，請稍後再試。</p>");
//    }
//  });

