// File: js/index.js (Đã sửa lỗi cấu trúc)

document.addEventListener('DOMContentLoaded', () => {

    // --- SCRIPT CHO THANH CUỘN CHUYÊN KHOA ---
    const specialtiesScroller = document.getElementById('specialtiesScroller');
    const specialtiesBtnLeft = document.getElementById('specialtiesScrollLeftBtn');
    const specialtiesBtnRight = document.getElementById('specialtiesScrollRightBtn');
    const specialtyFirstCard = specialtiesScroller ? specialtiesScroller.querySelector('.specialty-card') : null;

    let specialtyCardWidth = 280;
    let specialtyGap = 20;
    if (specialtyFirstCard) {
        specialtyCardWidth = specialtyFirstCard.offsetWidth;
        const gridStyle = window.getComputedStyle(specialtyFirstCard.parentElement);
        specialtyGap = parseFloat(gridStyle.gap) || 20;
    }
    const specialtyScrollAmount = 3 * (specialtyCardWidth + specialtyGap);

    function scrollSpecialties(direction) {
        if (specialtiesScroller) {
            const newScrollLeft = specialtiesScroller.scrollLeft + direction * specialtyScrollAmount;
            specialtiesScroller.scrollTo({left: newScrollLeft, behavior: 'smooth'});
        }
    }
    function checkSpecialtiesScrollButtons() {
        if (!specialtiesScroller || !specialtiesBtnLeft || !specialtiesBtnRight)
            return;
        setTimeout(() => {
            const maxScrollLeft = specialtiesScroller.scrollWidth - specialtiesScroller.clientWidth;
            const currentScroll = Math.round(specialtiesScroller.scrollLeft);
            specialtiesBtnLeft.classList.toggle('disabled', currentScroll < specialtyGap);
            specialtiesBtnRight.classList.toggle('disabled', currentScroll >= (maxScrollLeft - specialtyGap));
        }, 350);
    }
    if (specialtiesScroller) {
        specialtiesScroller.addEventListener('scroll', checkSpecialtiesScrollButtons);
    }

    // --- SCRIPT CHO THANH CUỘN DANH MỤC (FEATURES) ---
    const featuresScroller = document.getElementById('featuresScroller');
    const featuresBtnLeft = document.getElementById('featuresScrollLeftBtn');
    const featuresBtnRight = document.getElementById('featuresScrollRightBtn');
    const featureFirstCard = featuresScroller ? featuresScroller.querySelector('.feature-item') : null;

    let featureCardWidth = 170;
    let featureGap = 15;
    if (featureFirstCard) {
        featureCardWidth = featureFirstCard.offsetWidth;
        const gridStyle = window.getComputedStyle(featureFirstCard.parentElement);
        featureGap = parseFloat(gridStyle.gap) || 15;
    }
    const featuresScrollAmount = 4 * (featureCardWidth + featureGap);

    function scrollFeatures(direction) {
        if (featuresScroller) {
            const newScrollLeft = featuresScroller.scrollLeft + direction * featuresScrollAmount;
            featuresScroller.scrollTo({left: newScrollLeft, behavior: 'smooth'});
        }
    }
    function checkFeaturesScrollButtons() {
        if (!featuresScroller || !featuresBtnLeft || !featuresBtnRight)
            return;
        setTimeout(() => {
            const maxScrollLeft = featuresScroller.scrollWidth - featuresScroller.clientWidth;
            const currentScroll = Math.round(featuresScroller.scrollLeft);
            featuresBtnLeft.style.display = currentScroll < featureGap ? 'none' : 'flex';
            featuresBtnRight.style.display = currentScroll >= (maxScrollLeft - featureGap) ? 'none' : 'flex';
        }, 100);
    }
    if (featuresScroller) {
        featuresScroller.addEventListener('scroll', checkFeaturesScrollButtons);
    }

    // --- GỌI CÁC HÀM CHECK KHI TẢI TRANG VÀ RESIZE ---
    checkSpecialtiesScrollButtons();
    checkFeaturesScrollButtons();

    let resizeTimer;
    window.addEventListener('resize', () => {
        clearTimeout(resizeTimer);
        resizeTimer = setTimeout(() => {
            checkSpecialtiesScrollButtons();
            checkFeaturesScrollButtons();
        }, 250);
    });

    // --- KHỞI TẠO SWIPER ---
    var swiper = new Swiper(".mySwiper", {
        spaceBetween: 30,
        effect: "fade",
        loop: true,
        autoplay: {
            delay: 5000,
            disableOnInteraction: false,
        },
        pagination: {
            el: ".swiper-pagination",
            clickable: true,
        },
        navigation: {
            nextEl: ".swiper-button-next",
            prevEl: ".swiper-button-prev",
        },
    });

});