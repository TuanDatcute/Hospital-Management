// Nội dung cho file: /js/ctdt-dashboard.js
document.addEventListener('DOMContentLoaded', function () {
    
    // --- Xử lý Modal ---
    const modalOverlay = document.getElementById('modal-overlay');
    const updateButtons = document.querySelectorAll('.update-result-btn');

    if (modalOverlay && updateButtons.length > 0) {
        const closeModalButton = document.getElementById('close-button');
        const cancelButton = document.getElementById('cancel-button');
        const modalTitle = document.getElementById('modal-title');
        const chiDinhIdInput = document.getElementById('chiDinhId-input');
        const trangThaiInput = document.getElementById('trangThai-input');
        const ketQuaInput = document.getElementById('ketQua-input');

        const openModal = () => modalOverlay.classList.add('active');
        const closeModal = () => modalOverlay.classList.remove('active');

        updateButtons.forEach(button => {
            button.addEventListener('click', () => {
                modalTitle.textContent = `Cập nhật: ${button.dataset.tendichvu}`;
                chiDinhIdInput.value = button.dataset.id;
                trangThaiInput.value = button.dataset.trangthai;
                ketQuaInput.value = (button.dataset.ketqua === 'null' || button.dataset.ketqua === 'undefined') ? '' : button.dataset.ketqua;
                openModal();
            });
        });

        closeModalButton.addEventListener('click', closeModal);
        cancelButton.addEventListener('click', closeModal);
        modalOverlay.addEventListener('click', (event) => {
            if (event.target === modalOverlay)
                closeModal();
        });
    }

    // --- Cập nhật thanh chỉ số sinh tồn động ---
    function updateVitalGauges() {
        const gauges = document.querySelectorAll('.vital-gauge');

        const thresholds = {
            temp: {normal: [36.5, 37.5], warning: [37.6, 38.5], range: [35, 42]},
            bp: {normal: [90, 120], warning: [121, 139], range: [70, 180]},
            hr: {normal: [60, 100], warning: [101, 120], range: [40, 180]},
            rr: {normal: [16, 20], warning: [21, 24], range: [10, 30]}
        };

        gauges.forEach(gauge => {
            const type = gauge.dataset.type;
            const rawValue = String(gauge.dataset.value).split('/')[0]; // Thêm String() để đảm bảo
            const value = parseFloat(rawValue);

            if (isNaN(value) || !thresholds[type])
                return;

            const config = thresholds[type];
            const [minRange, maxRange] = config.range;
            const fillElement = gauge.querySelector('.gauge-fill');
            const valueElement = gauge.querySelector('.value');

            let percentage = (value - minRange) / (maxRange - minRange) * 100;
            percentage = Math.max(0, Math.min(100, percentage));
            fillElement.style.width = `${percentage}%`;

            gauge.classList.remove('status-normal', 'status-warning', 'status-high');
            valueElement.classList.remove('status-normal', 'status-warning', 'status-high');

            if (value >= config.normal[0] && value <= config.normal[1]) {
                gauge.classList.add('status-normal');
                valueElement.classList.add('status-normal');
            } else if ((value > config.normal[1] && value <= config.warning[1]) || (value < config.normal[0] && value >= config.range[0])) {
                gauge.classList.add('status-warning');
                valueElement.classList.add('status-warning');
            } else {
                gauge.classList.add('status-high');
                valueElement.classList.add('status-high');
            }
        });
    }

    updateVitalGauges();
});