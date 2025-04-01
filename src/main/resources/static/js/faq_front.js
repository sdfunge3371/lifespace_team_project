$(document).ready(function () {
  $.ajax({
    url: "/faq/frontQuery", // 呼叫你後端的 Controller `/faq/frontQuery`
    method: "GET",
    dataType: "json",
    success: function (data) {
      $("#faqAccordion").empty(); // 清空原本的 FAQ 區塊

      data.forEach(function (faq, index) {
        const collapseId = `collapse${index}`;
        const headingId = `heading${index}`;

        const faqHtml = `
          <div class="accordion-item">
            <h2 class="accordion-header" id="${headingId}">
              <button class="accordion-button collapsed" type="button"
                      data-bs-toggle="collapse"
                      data-bs-target="#${collapseId}"
                      aria-expanded="false"
                      aria-controls="${collapseId}">
                ${faq.faqAsk}
              </button>
            </h2>
            <div id="${collapseId}" class="accordion-collapse collapse"
                 aria-labelledby="${headingId}" data-bs-parent="#faqAccordion">
              <div class="accordion-body">
                ${faq.faqAnswer}
              </div>
            </div>
          </div>
        `;

        $("#faqAccordion").append(faqHtml);
      });
    },
    error: function () {
      $("#faqAccordion").html("<p>無法載入常見問題，請稍後再試。</p>");
    }
  });
});
