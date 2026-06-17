const fallback = {
  fullName: "Ada Lovelace",
  type: "STUDENT",
  department: "Engineering",
  title: "Computer Science",
  email: "ada@example.com",
  phone: "+855 12 345 678",
  bloodGroup: "O+",
  expiryDate: "Not set",
  barcodeType: "CODE_128",
};

const fields = document.querySelectorAll("[data-preview]");
const templateSelect = document.querySelector("[data-preview-template]");
const photoInput = document.querySelector("[data-photo-input]");
const photoPreview = document.querySelector("[data-photo-preview]");
const initials = document.querySelector("[data-initials]");
const card = document.querySelector("[data-card]");

function textTarget(key) {
  return document.querySelector(`[data-preview-text="${key}"]`);
}

function valueFor(field) {
  return field.value && field.value.trim() ? field.value.trim() : fallback[field.dataset.preview];
}

function updateInitials(name) {
  const parts = name.trim().split(/\s+/).filter(Boolean);
  const letters = parts.length > 1 ? parts[0][0] + parts[parts.length - 1][0] : (parts[0] || "ID").slice(0, 2);
  initials.textContent = letters.toUpperCase();
}

function updateTemplate() {
  if (!templateSelect || !card) return;
  const option = templateSelect.options[templateSelect.selectedIndex];
  const primary = option?.dataset.primary || "#0f766e";
  const secondary = option?.dataset.secondary || "#ccfbf1";
  card.style.setProperty("--primary", primary);
  const photoFrame = document.querySelector(".photo-frame");
  if (photoFrame) photoFrame.style.background = secondary;
  textTarget("organization").textContent = option?.dataset.org || "ID Card System";
  textTarget("tagline").textContent = option?.dataset.tagline || "Official Identity Card";
}

function updatePreview() {
  fields.forEach((field) => {
    const key = field.dataset.preview;
    const target = textTarget(key);
    if (target) target.textContent = valueFor(field);
  });
  const nameField = document.querySelector('[data-preview="fullName"]');
  updateInitials(valueFor(nameField));
  updateTemplate();
}

fields.forEach((field) => {
  field.addEventListener("input", updatePreview);
  field.addEventListener("change", updatePreview);
});

if (templateSelect) {
  templateSelect.addEventListener("change", updateTemplate);
}

if (photoInput) {
  photoInput.addEventListener("change", () => {
    const file = photoInput.files[0];
    if (!file) return;
    photoPreview.src = URL.createObjectURL(file);
    photoPreview.hidden = false;
    initials.hidden = true;
  });
}

updatePreview();
