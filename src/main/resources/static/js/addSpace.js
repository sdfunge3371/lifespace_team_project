// ======== Configuration ========

// const APP_CONTEXT_PATH = "http://localhost:8080";
// const ADD_SPACE_URL = `${APP_CONTEXT_PATH}/spaces`;
// const SOFT_DELETE_SPACE_USAGES_URL_BASE = `${APP_CONTEXT_PATH}/space-usages/id`;

const ADD_SPACE_URL = `/spaces`;
const SOFT_DELETE_SPACE_USAGES_URL_BASE = `/space-usages/id`;
const GET_ALL_BRANCHES_URL = "/branch/getAll";
const GET_ALL_SPACES_USAGES_URL = "/space-usages";

const form = document.getElementById('spaceForm');
const submitButton = document.getElementById('submitBtn');
const errorDiv = document.getElementById('errorMessages');


// ======== DOM 物件 ========
// 空間設備
let addedEquipment = [];   // 暫存陣列 (需為JSON格式)： [{ name: '設備1'}, { name: '設備2'}, { name: '設備3'}, ...]
let equipmentModal;
let addEquipmentBtn;
let closeEquipmentModalBtn;
let confirmAddEquipmentBtn;
let equipmentNameInput;
let equipmentDisplayArea;
let equipmentDataInput;

// 空間用途
let usageModal;
let addUsageBtn;
let closeUsageModalBtn;
let confirmAddUsageBtn;
let usageNameInput;
let usageModalErrorDiv;
let spaceUsageSelectElement;
let deleteUsageBtn;

// ======== 顯示錯誤訊息 ========
function displayErrors(errors) {
    errorDiv.innerHTML = ''; // 清除之前的錯誤

// 在表單最上方新增錯誤訊息
    if (errors && Array.isArray(errors) && errors.length > 0) { // // 如果errors是Json
        const list = document.createElement('ul');
        errors.forEach(msg => {
            // 檢查msg是object還是string
            const text = (typeof msg === 'object' && msg.defaultMessage) ? msg.defaultMessage : msg;
            const item = document.createElement('li');
            item.textContent = text;
            list.appendChild(item);
        });
        errorDiv.appendChild(list);
    } else if (errors) { // 如果errors是String
        errorDiv.textContent = errors;
    }
}

// 預覽空間照片
// 利用DataTransfer物件管理上傳的檔案
function setupImagePreview() {
    const spacePhotosInput = document.getElementById('spacePhotos');
    const previewContainer = document.getElementById('imagePreviewContainer');
    const fileStore = new DataTransfer();

    if (!spacePhotosInput || !previewContainer) return;   // 如果沒有照片可預覽，則不執行以下的程式

    spacePhotosInput.addEventListener('change', handleFileSelect);

    function handleFileSelect(e) {
        const files = e.target.files; // 抓出剛剛選擇的檔案

        // 新增檔案到DataTransfer物件裡
        for (const file of files) {
            if (file.type.startsWith('image/')) {
                fileStore.items.add(file);
            } else {
                console.warn(`${file.name} 檔案規格不符，無法上傳`);
            }
        }
        spacePhotosInput.files = fileStore.files;   // 將DataTransfer的檔案送進file input欄位（input這是要傳給資料庫的）
        renderPreviews();   // 執行照片預覽功能
    }

// 照片預覽
    function renderPreviews() {
        previewContainer.innerHTML = ''; // 清除預覽照片

        // 讀取Data Transfer裡的照片
        Array.from(fileStore.files).forEach((file, index) => {
            const reader = new FileReader();    // 利用File Reader讀取照片

            reader.onload = function (e) {
                const previewItem = document.createElement('div');  // 建立一個顯示照片的div
                previewItem.classList.add('preview-item');

                const img = document.createElement('img');  // 建立img元素
                img.classList.add('preview-image');  // CSS
                console.log(e.target);
                img.src = e.target.result; // 將img src設為剛剛存入的照片來源
                img.alt = file.name;

                // 在整個div的右上角新增刪除按鈕
                const deleteBtn = document.createElement('button');
                deleteBtn.classList.add('delete-btn');  // CSS
                deleteBtn.innerHTML = '&times;'; // '×' symbol
                deleteBtn.type = 'button'; // 注意：button的default type值為"submit"，為了預防按到刪除按鈕時表單直接被送出，需改成type="button"
                deleteBtn.title = `移除 ${file.name}`; // title: hover在X上時顯示文字

                // 點擊叉叉執行移除
                deleteBtn.addEventListener('click', () => {
                    removeFile(index);
                });

                // 將圖片及叉叉按鈕加進div中
                previewItem.appendChild(img);
                previewItem.appendChild(deleteBtn);

                // div加進預覽照片container中
                previewContainer.appendChild(previewItem);
            }

            // 展示預覽照片
            reader.readAsDataURL(file);
        });
    }

// 移除預覽照片
    function removeFile(index) {
        // Remove the file from the store based on its index
        fileStore.items.remove(index);

        // Update the input's files property
        spacePhotosInput.files = fileStore.files;

        // Re-render the previews
        renderPreviews();
    }
}

// ========== 處理「空間設備」==========
// 空間設備modal開啟
function openEquipmentModal() {
// 在開啟前將input欄位清除
    equipmentNameInput.value = '';
    equipmentModal.showModal();    // 屬於<dialog>內建的方法：顯示對話框
}

// 點擊「完成」時，觸發新增資料
function addEquipmentItem() {
    const name = equipmentNameInput.value.trim();  // 抓剛剛輸入的input

    if (!name) {   // 未輸入跳出警告
        alert('請輸入設備名稱！');
        equipmentNameInput.focus();
        return;
    }

// 檢查該設備是否有被使用過
    const isDuplicate = addedEquipment.some(equip => equip.spaceEquipName === name);

    if (isDuplicate) {
        alert("此設備名稱已經被使用過");
        equipmentNameInput.focus();
        return;
    }

// 將剛剛輸入的資料丟進暫存陣列
    addedEquipment.push({spaceEquipName: name});  // 需為JSON

    renderAddedEquipment();
    updateEquipmentHiddenInput();

// 把modal關了
    equipmentModal.close();     // 屬於<dialog>內建的方法：關閉對話框
}

// 將剛剛打的input顯示在按鈕下方
function renderAddedEquipment() {

    equipmentDisplayArea.innerHTML = ''; // Clear current list

    addedEquipment.forEach((item, index) => {
        const itemDiv = document.createElement('div'); // 針對每個設備項目建立一個div
        itemDiv.classList.add('equipment-item');   // CSS

        const itemTextSpan = document.createElement('span');   // div裡面有一個span，其顯示設備的名稱
        itemTextSpan.classList.add('equipment-item-text'); // CSS
        itemTextSpan.textContent = item.spaceEquipName;

        // 增加刪除按鈕
        const deleteBtn = document.createElement('button');
        deleteBtn.classList.add('equipment-item-delete');
        deleteBtn.innerHTML = '&times;';
        deleteBtn.type = 'button';  // 注意：button的default type值為"submit"，為了預防按到刪除按鈕時表單直接被送出，需改成type="button"
        deleteBtn.title = `移除 ${item.spaceEquipName}`;
        deleteBtn.setAttribute('data-index', index); // data-index設為陣列索引值綁定在按鈕上，用來檢查移除

        deleteBtn.addEventListener('click', handleRemoveEquipment);

        // 將剛剛的元素加入display area
        itemDiv.appendChild(itemTextSpan);
        itemDiv.appendChild(deleteBtn);
        equipmentDisplayArea.appendChild(itemDiv);
    });
}

// 移除項目：點擊刪除鍵時觸發
function handleRemoveEquipment(e) {
    const indexToRemove = parseInt(e.target.getAttribute('data-index')); // 自剛剛點擊的按鈕取出上面方法存入的索引值

    if (indexToRemove >= 0 && indexToRemove < addedEquipment.length) {
        addedEquipment.splice(indexToRemove, 1); // splice：自索引值indexToRemove起，刪除1個元素
        renderAddedEquipment(); // 重新生成設備清單
        updateEquipmentHiddenInput(); // 更新hidden input
    }
}

// Hidden input用來處理給後端JSON資料
function updateEquipmentHiddenInput() {
    equipmentDataInput.value = JSON.stringify(addedEquipment); // 將JSON丟進hidden input元素
    console.log("Hidden Equipment Data:", equipmentDataInput.value);
}

// ========== 處理「空間用途」==========

function openUsageModal() {
    usageNameInput.value = '';
    if (usageModalErrorDiv) usageModalErrorDiv.textContent = '';
    usageModal.showModal();
}

// 將剛剛輸入的新的用途加到多選清單（自AJAX POST處理完成後觸發）
function addUsageOptionToSelect(usage) {

// 建立option物件
    const option = document.createElement('option');
    option.value = usage.spaceUsageId;   // value設為ID
    option.textContent = usage.spaceUsageName;   // 顯示內容設為名稱

    spaceUsageSelectElement.appendChild(option);   // 將選項加到多選清單中

    if (spaceUsageSelectElement.disabled) {
        spaceUsageSelectElement.disabled = false;
    }
}

// 回上一頁
function historyBack() {
    history.back();
}

let isFormDirty = true; // 預設：表單可能有資料，離開前要確認

window.addEventListener('beforeunload', function (e) {
    if (isFormDirty) {
        e.preventDefault(); // 取消預設動作
        return ''; // 大多數瀏覽器會顯示內建提示文字
    }
});



// ========== AJAX CRUD ==========
// --- 1. POST: 新增空間 ---
submitButton.addEventListener('click', function () {    // 點擊「完成」按鈕時要做的事
    isFormDirty = false; // 提交後不需要提示

// 移除成功、錯誤訊息
    errorDiv.innerHTML = '';

    const formData = new FormData();

    if (!form.branchId.value) {
        displayErrors(["請選擇所屬分點"]);
        return;
    }

    const spaceData = {
        branchId: form.branchId.value,
        spaceName: form.spaceName.value,
        spacePeople: form.spacePeople.value,
        spaceSize: form.spaceSize.value,
        spaceHourlyFee: form.spaceHourlyFee.value,
        spaceDailyFee: form.spaceDailyFee.value,
        spaceUsageIds: Array.from(form.spaceUsageIds.selectedOptions).map(opt => opt.value),
        spaceEquipments: JSON.parse(form.spaceEquipments.value),
        spaceDesc: form.spaceDesc.value,
        spaceAlert: form.spaceAlert.value,
        spaceStatus: form.spaceStatus.value,
        spaceFloor: form.spaceFloor.value
    };

    console.log(form.branchId.value);

    formData.append("data", new Blob([JSON.stringify(spaceData)], {type: "application/json"}));

    const photoInput = document.getElementById("spacePhotos");
    for (const file of photoInput.files) {
        formData.append("photos", file);
    }

// 傳送資料時，將按鈕disabled，以免重複上傳
    submitButton.disabled = true;
    submitButton.textContent = '傳送中...';

// 提交request body
    fetch(ADD_SPACE_URL, {
        method: 'POST',
        body: formData      // 如果request body是multipart/form-data，這邊就不用指定content-type，而且也不用JSON stringify formData
    })
        .then(response => {
            if (!response.ok) {
                return response.json().then(errorData => {  // 回傳JSON
                    const backendErrors = errorData.errors || errorData.message || `伺服器錯誤: ${response.status}`;    // 依序先抓JSON中的errors, message，如果都沒有，就代表是500
                    throw backendErrors;    // 拋出例外給外層的catch
                }).catch((backendErrors) => {
                    // 回傳的JSON格式有誤，拋出例外給外層的catch
                    displayErrors(backendErrors);
                    throw [`伺服器發生錯誤: ${response.status} ${response.statusText}`];
                });
            }
            return response.json(); // 回傳JSON，成功就是剛剛新增的JSON，失敗就是回傳錯誤訊息的JSON回傳格式範例：{ success: true, message: "..." }
        })
        .then(data => {
            form.reset(); // 務必要清空清空表單
            // window.location.href = 'listSpaces.html';
            window.location.href = '/listSpaces';
        })
        .catch(errorOrErrors => {
            console.error('提交時發生錯誤:', errorOrErrors);

        })
        .finally(() => {
            // 提交完成後，要將按鈕回復
            submitButton.disabled = false;
            submitButton.textContent = '完成';
        });
});

// --- 2. GET: 取得分點列表 ---
async function fetchBranches() {
    const selectElement = document.getElementById('branchId');
    const loadingOptionText = '讀取中...';
    const defaultOptionText = '請選擇分點';

// 載入時寫讀取中
    selectElement.innerHTML = `<option value="">${loadingOptionText}</option>`;
    selectElement.disabled = true; // 把空間用途label disabled

    try {
        const response = await fetch(GET_ALL_BRANCHES_URL); // 抓Controller裡的網址抓過來，需利用相對網址，請自行到branch overloading方法

        if (!response.ok) {  // 處理404或500
            throw new Error(`HTTP error! status: ${response.status}`);
        }

        // 解析取得所有空間用途回傳JSON
        // [{ branchId: 'B001', branchName: '南京復興', ... }, ...]
        const branches = await response.json();

        // 把loading刪除
        selectElement.innerHTML = '';

        // 將下拉式清單顯示為「請選擇分點」
        const defaultOption = document.createElement('option');
        defaultOption.value = ""; // 「請選擇分點」option的value值為空值
        defaultOption.textContent = defaultOptionText;   // 請選擇分點
        defaultOption.disabled = true; // 將預設option作為placeholder
        defaultOption.selected = true;
        selectElement.appendChild(defaultOption);

        // 將資料丟進下拉式清單
        if (branches && branches.length > 0) {
            branches.forEach(branch => {
                const option = document.createElement('option');
                option.value = branch.branchId;  // 將value值設為「分點編號」
                option.textContent = branch.branchName;  // 將value值設為「分點名稱」
                selectElement.appendChild(option);
            });
            selectElement.disabled = false; // 如果有選項，則enable下拉式清單
        }

    } catch (error) {
        console.error('Error fetching branches:', error);
    }
}


// --- 3. GET: 取得空間用途列表 ---
async function fetchSpaceUsages() {
    spaceUsageSelectElement = document.getElementById('spaceUsageId');  // 抓空間用途下拉式清單元素

// 抓不到下拉式清單時錯誤處理
    if (!spaceUsageSelectElement) {
        console.error("Element with ID 'spaceUsageId' not found.");
        return;
    }

    const loadingOptionText = '讀取中...';
    spaceUsageSelectElement.innerHTML = `<option disabled>${loadingOptionText}</option>`;   // 「讀取中...」選項，並設為disabled
    spaceUsageSelectElement.disabled = true;
    deleteUsageBtn.style.display = 'none';   // 多選清單還沒讀取完不能移除

    try {
        const response = await fetch(GET_ALL_SPACES_USAGES_URL); // 從後端抓資料

        const spaceUsagesData = await response.json(); // 解析JSON

        // 將「讀取中...」移除
        spaceUsageSelectElement.innerHTML = '';

        let availableCount = 0; // 數有幾個空間用途為"AVAILABLE"
        if (spaceUsagesData && spaceUsagesData.length > 0) {
            spaceUsagesData.forEach(usage => {
                if (usage.spaceUsageStatus !== "DELETED") {
                    const option = document.createElement('option');
                    option.value = usage.spaceUsageId;
                    option.textContent = usage.spaceUsageName;
                    spaceUsageSelectElement.appendChild(option);
                    availableCount++; // Increment count
                }
            });
        }

        // 只有多選清單有項目的時候，才會顯示移除按鈕
        if (availableCount > 0) {
            spaceUsageSelectElement.disabled = false;
            deleteUsageBtn.style.display = 'inline-block'; // Show delete btn if options exist
        } else {
            spaceUsageSelectElement.disabled = true;
            deleteUsageBtn.style.display = 'none'; // Keep delete btn hidden
        }

    } catch (error) {
        console.error('Error fetching space usages:', error);
    }
}

// --- 4. POST: 新增空間用途到表單裡的多選清單 ---
async function addUsageItem() {

    const name = usageNameInput.value.trim();  // 取得input欄位的值

// 檢查輸入欄位是否為空
    if (!name) {
        if (usageModalErrorDiv) alert('請輸入用途名稱！');
        usageNameInput.focus();
        return;
    }

// 檢查欄位是否重複
    let isDuplicate = false;
    const availableOptions = spaceUsageSelectElement.options;

    for (const option of availableOptions) {
        const optionText = option.textContent.trim();  // 取得選項文字
        if (name === optionText) {
            isDuplicate = true;
            break;
        }
    }

    if (isDuplicate) {
        alert('此用途名稱已存在於列表中！'); // 維持原本的提示方式
        usageNameInput.focus();
        return;   // 找到重複就不繼續執行了
    }

// for @RequestBody SpaceUsage (後端會自動建立ID, status, created_time)
    const usageData = {
        spaceUsageName: name
    };

// 提交時disable button
    confirmAddUsageBtn.disabled = true;

    const add_space_usage_url = GET_ALL_SPACES_USAGES_URL;
    try {
        const response = await fetch(add_space_usage_url, {
            method: 'POST',
            headers: {
                'Content-Type': 'application/json',
            },
            body: JSON.stringify(usageData)
        });

        // 檢查錯誤
        if (!response.ok) {
            if (response.status === 409) {
                const errorMsg = await response.text(); // 取得後端的error message
                throw new Error(errorMsg);
            } else if (response.status === 400) { // 格式錯誤 (只會有不得空白)
                const errorData = await response.json();
                const message = errorData.message || (errorData.errors ? errorData.errors.map(e => e.defaultMessage || e).join(', ') : `輸入錯誤 (${response.status})`);
                throw new Error(message);
            } else {
                // 404, 500
                throw new Error(response.status);
            }
        }

        // 成功
        const newUsage = await response.json(); // 取得剛剛建立的JSON資料

        // 將剛剛的項目丟進多選清單中
        addUsageOptionToSelect(newUsage);

        // 重置modal input欄位
        usageNameInput.value = '';

        usageModal.close();

    } catch (error) {
        console.error('Error adding space usage:', error);
    } finally {
        confirmAddUsageBtn.disabled = false;
    }
}

// --------- 5. PUT: 對「空間用途」進行軟刪除 ---------

async function handleDeleteSpaceUsage() {
    const selectedOptions = Array.from(spaceUsageSelectElement.selectedOptions);    // selecteddOptions: 抓出在<select>物件中你選到的東西，組成陣列

// 按下「移除空間用途」後，就會刪除那些你剛剛在多選清單中選的用途
    if (selectedOptions.length === 0) {
        alert("請先從列表中選取要刪除的空間用途。");
        return;
    }

// 確認對話框
    const confirmDelete = confirm(`確定要刪除選取的 ${selectedOptions.length} 個空間用途嗎？\n注意：其他空間若有包含那些用途也會一併刪除`);

    if (!confirmDelete) {   // 否：不執行
        return;
    }

// 開始處裡軟刪除，此時把移除按鈕disabled
    deleteUsageBtn.disabled = true;

    for (const option of selectedOptions) {
        const usageId = option.value;   // value存ID
        const usageName = option.textContent;   // 內容存名稱
        const deleteUrl = `${SOFT_DELETE_SPACE_USAGES_URL_BASE}/${encodeURIComponent(usageId)}/soft-delete`; // 組成 PUT URL

        const response = await fetch(deleteUrl, {   // 開始丟給後端處理，此時，狀態已經成DELETED了
            method: 'PUT',
            headers: {
                'Content-Type': 'application/json'
            }
        });

        option.remove();
    }

// 處裡軟刪除完成，此時把移除按鈕enabled
    deleteUsageBtn.disabled = false;

// 若多選清單為空，則隱藏刪除按鈕
    if (spaceUsageSelectElement.options.length === 0) {
        deleteUsageBtn.style.display = 'none';
    }

}

// ======== Initialization (載入網頁時要做的事情) ========
document.addEventListener('DOMContentLoaded', function () {
// 登入攔截
    let adminId = '';  // 假設登入者 ID

    $.ajax({
        url: "http://localhost:8080/admin/faq/profile",
        method: "GET",
        xhrFields: {
            withCredentials: true // 等同於 fetch 的 credentials: "include"
        },
        success: function (response) {
            adminId = response.adminId;
            console.log("登入的管理員ID：", adminId);

        },
        error: function (xhr) {
            if (xhr.status === 401) {
                alert("尚未登入，請先登入");
                // window.location.href = "/loginAdmin.html";
                window.location.href = "/loginAdmin";
            } else {
                console.error("無法取得會員資料", xhr);
            }
        }
    });

// 空間設備
    equipmentModal = document.getElementById('equipmentModal');
    addEquipmentBtn = document.getElementById('addEquipmentBtn');
    closeEquipmentModalBtn = document.getElementById('closeEquipmentModalBtn');
    confirmAddEquipmentBtn = document.getElementById('confirmAddEquipmentBtn');
    equipmentNameInput = document.getElementById('equipmentNameInput');
    equipmentDisplayArea = document.getElementById('equipmentDisplayArea');
    equipmentDataInput = document.getElementById('equipmentDataInput');
// 點擊「新增空間設備」時
    addEquipmentBtn.addEventListener('click', openEquipmentModal);
// 點擊叉叉時
    closeEquipmentModalBtn.addEventListener('click', () => {
        equipmentModal.close();
    });
// 點擊「完成」時
    confirmAddEquipmentBtn.addEventListener('click', addEquipmentItem);

// 點擊空間設備modal外面時
    equipmentModal.addEventListener('click', (event) => {
        if (event.target === equipmentModal) {
            equipmentModal.close();
        }
    });

// 空間用途
    usageModal = document.getElementById('usageModal');
    addUsageBtn = document.getElementById('addUsageBtn');
    closeUsageModalBtn = document.getElementById('closeUsageModalBtn');
    confirmAddUsageBtn = document.getElementById('confirmAddUsageBtn');
    usageNameInput = document.getElementById('usageNameInput');
    usageModalErrorDiv = document.getElementById('usageModalError');
    spaceUsageSelectElement = document.getElementById('spaceUsageId');
    deleteUsageBtn = document.getElementById('deleteUsageBtn');

    addUsageBtn.addEventListener('click', openUsageModal);
    closeUsageModalBtn.addEventListener('click', () => {
        usageModal.close();
    });
    confirmAddUsageBtn.addEventListener('click', addUsageItem);  // 完成後，呼叫Ajax
    deleteUsageBtn.addEventListener('click', handleDeleteSpaceUsage);

// 點擊modal外的任何地方也可以關閉modal
    usageModal.addEventListener('click', (event) => {
        if (event.target === usageModal) {
            usageModal.close();
        }
    });


// 初始化
    fetchBranches();
    fetchSpaceUsages();
    setupImagePreview();

    renderAddedEquipment();
    updateEquipmentHiddenInput();
});