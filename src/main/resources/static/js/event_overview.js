// === 標籤輪播功能 ===
const tagContainer = $(".tag-container");
const tagWidth = $(".tag").outerWidth(true);

$(".left-tag-btn").click(function() {
    tagContainer.animate({ scrollLeft: "-=" + tagWidth * 1.5 }, 100);
});

$(".right-tag-btn").click(function() {
    tagContainer.animate({ scrollLeft: "+=" + tagWidth * 1.5 }, 100);
});

