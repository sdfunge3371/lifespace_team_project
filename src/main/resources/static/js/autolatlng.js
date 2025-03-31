// (自動存入經緯度的備用code)
// Global variable for Autocomplete instance
// let autocomplete;

// Function to initialize Google Maps Autocomplete
// function initMap() {
//     const addressInput = document.getElementById('spaceAddress');
//     const latitudeInput = document.getElementById('latitude');
//     const longitudeInput = document.getElementById('longitude');

//     autocomplete = new google.maps.places.Autocomplete(addressInput, {
//         componentRestrictions: { country: 'tw' } // Restrict to Taiwan
//     });

//     autocomplete.addListener('place_changed', function () {
//         const place = autocomplete.getPlace();

//         if (!place.geometry || !place.geometry.location) {
//             console.error('沒有找到此地址的地理資訊');
//             latitudeInput.value = ''; // Clear fields if no geometry
//             longitudeInput.value = '';
//             return;
//         }

//         const lat = place.geometry.location.lat();
//         const lng = place.geometry.location.lng();

//         latitudeInput.value = lat.toFixed(6);
//         longitudeInput.value = lng.toFixed(6);

//         // Optionally update the address input to the formatted address
//         // addressInput.value = place.formatted_address;

//         console.log('已成功獲取經緯度:', lat, lng);
//     });
//     console.log("Google Maps Autocomplete Initialized.");
// }

// 格式錯誤處理（後端處理過前端應該就不用了）
// // 檢查formData中的必填欄位
// requiredFields.forEach(fieldName => {
//     const field = form.elements[fieldName];
//     if (!formData.get(fieldName) || formData.get(fieldName).trim() === '') {
//         isValid = false;
//         // Find the label associated with the field for a better error message
//         const label = field.previousElementSibling?.tagName === 'LABEL' ? field.previousElementSibling.textContent : fieldName;
//         errors.push(`${label.replace(':', '')} 為必填欄位。`);
//     }
// });

// // Add more specific validation if needed (e.g., number ranges)
// const spacePeople = parseInt(formData.get('spacePeople'), 10);
// if (isNaN(spacePeople) || spacePeople <= 0) {
//     isValid = false;
//     errors.push('空間人數必須是正整數。');
// }
// // ... add similar checks for size, fees ...

// if (!isValid) {
//     displayErrors(errors);
//     return; // Stop submission if client-side validation fails
// }