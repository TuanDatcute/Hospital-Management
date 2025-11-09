// Nội dung mới cho file: /js/pkb-form.js

document.addEventListener('DOMContentLoaded', function () {

    // Lấy URL từ data-attributes (an toàn hơn)
    const formGrid = document.querySelector('.form-grid');
    const APPOINTMENT_URL = formGrid.dataset.lichhenUrl;
    const PATIENT_SEARCH_URL = formGrid.dataset.patientUrl;

    // === PHẦN 1: LOGIC LỌC LỊCH HẸN ===
    const appointmentDateInput = document.getElementById('appointmentDate');
    const lichHenSelect = document.getElementById('lichHenId');
    const benhNhanSelect = document.getElementById('benhNhanId_hidden'); // Input ẩn
    const benhNhanDisplay = document.getElementById('benhNhanDisplay'); // Input hiển thị

    if (appointmentDateInput && lichHenSelect) {
        // Hàm gọi API để lấy lịch hẹn
        async function fetchAppointments() {
            const selectedDate = appointmentDateInput.value;
            if (!selectedDate) {
                lichHenSelect.innerHTML = '<option value="">-- Chọn ngày để lọc lịch hẹn --</option>';
                return;
            }

            try {
                const response = await fetch(`${APPOINTMENT_URL}${selectedDate}`);
                if (!response.ok) {
                    throw new Error('Lỗi khi tải lịch hẹn');
                }
                const appointments = await response.json();

                lichHenSelect.innerHTML = '<option value="">-- Chọn từ lịch hẹn --</option>';
                if (appointments.length === 0) {
                    lichHenSelect.innerHTML = '<option value="">-- Không có lịch hẹn nào --</option>';
                }

                appointments.forEach(app => {
                    const option = document.createElement('option');
                    option.value = app.id;
                    option.textContent = `STT ${app.stt} - ${app.tenBenhNhan} (${app.lyDoKham})`;
                    option.dataset.patientId = app.benhNhanId;
                    option.dataset.patientName = app.tenBenhNhan;
                    lichHenSelect.appendChild(option);
                });
            } catch (error) {
                console.error(error);
                lichHenSelect.innerHTML = '<option value="">-- Lỗi tải dữ liệu --</option>';
            }
        }

        // Gán sự kiện cho ô chọn ngày
        appointmentDateInput.addEventListener('change', fetchAppointments);

        // Gán sự kiện cho dropdown lịch hẹn
        lichHenSelect.addEventListener('change', function () {
            const selectedOption = this.options[this.selectedIndex];
            const patientId = selectedOption.dataset.patientId;
            const patientName = selectedOption.dataset.patientName;

            if (patientId && benhNhanDisplay) {
                // Tự động điền thông tin bệnh nhân
                benhNhanDisplay.value = patientName;
                benhNhanSelect.value = patientId;
                // Khóa ô tìm kiếm
                document.getElementById('openPatientSearchModal').disabled = true;
            } else if (benhNhanDisplay) {
                // Mở lại ô tìm kiếm
                document.getElementById('openPatientSearchModal').disabled = false;
                benhNhanDisplay.value = "";
                benhNhanSelect.value = "";
            }
        });
    }

    // === PHẦN 2: LOGIC POPUP TÌM BỆNH NHÂN ===
    const modalOverlay = document.getElementById('patient-modal-overlay');
    const openModalBtn = document.getElementById('openPatientSearchModal');
    const closeModalBtn = document.getElementById('patient-close-button');
    const cancelModalBtn = document.getElementById('patient-cancel-button');
    const searchInput = document.getElementById('patientSearchInput');
    const searchResults = document.getElementById('patient-search-results');
    let searchTimer;

    // Chỉ chạy nếu các phần tử tồn tại
    if (modalOverlay && openModalBtn && closeModalBtn) {

        const openModal = () => modalOverlay.classList.add('active');
        const closeModal = () => modalOverlay.classList.remove('active');

        openModalBtn.addEventListener('click', openModal);
        closeModalBtn.addEventListener('click', closeModal);
        cancelModalBtn.addEventListener('click', closeModal);
        modalOverlay.addEventListener('click', (e) => {
            if (e.target === modalOverlay)
                closeModal();
        });

        // Hàm tìm kiếm bệnh nhân (debounce)
        searchInput.addEventListener('keyup', () => {
            clearTimeout(searchTimer);
            const keyword = searchInput.value.trim();

            if (keyword.length < 2) { // Chỉ tìm khi gõ ít nhất 2 ký tự
                searchResults.innerHTML = '<p class="no-patient-results">Nhập ít nhất 2 ký tự để tìm...</p>';
                return;
            }

            searchTimer = setTimeout(async () => {
                try {
                    const response = await fetch(`${PATIENT_SEARCH_URL}${keyword}`);
                    if (!response.ok)
                        throw new Error('Lỗi mạng');

                    const patients = await response.json();
                    displayPatientResults(patients);
                } catch (error) {
                    searchResults.innerHTML = '<p class="no-patient-results">Lỗi khi tìm kiếm.</p>';
                }
            }, 300); // Chờ 300ms sau khi ngừng gõ
        });

        // Hàm hiển thị kết quả
        function displayPatientResults(patients) {
            if (!patients || patients.length === 0) {
                searchResults.innerHTML = '<p class="no-patient-results">Không tìm thấy bệnh nhân nào.</p>';
                return;
            }

            const table = document.createElement('table');
            table.className = 'patient-result-table';
            table.innerHTML = `<thead><tr><th>Mã BN</th><th>Họ Tên</th><th>Ngày Sinh</th></tr></thead>`;
            const tbody = document.createElement('tbody');

            patients.forEach(p => {
                const tr = document.createElement('tr');
                tr.dataset.patientId = p.id;
                tr.dataset.patientName = p.hoTen;
                tr.innerHTML = `
                    <td>${p.maBenhNhan}</td>
                    <td>${p.hoTen}</td>
                    <td>${p.ngaySinh}</td> 
                `;
                // Gán sự kiện click để chọn
                tr.addEventListener('click', () => selectPatient(p));
                tbody.appendChild(tr);
            });

            table.appendChild(tbody);
            searchResults.innerHTML = ''; // Xóa kết quả cũ
            searchResults.appendChild(table);
        }

        // Hàm khi chọn một bệnh nhân
        function selectPatient(patient) {
            benhNhanDisplay.value = `${patient.hoTen} (Mã: ${patient.maBenhNhan})`;
            benhNhanSelect.value = patient.id;

            // Khóa ô lịch hẹn (vì đã chọn bệnh nhân)
            lichHenSelect.disabled = true;

            closeModal();
        }
    }
});