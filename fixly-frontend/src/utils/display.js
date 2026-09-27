export const categories = [
  "PLUMBING",
  "ELECTRICAL",
  "CLEANING",
  "HEATING",
  "GARDENING",
  "APPLIANCE_REPAIR",
];
export const categoryLabel = (value) =>
  (value || "Home services")
    .toLowerCase()
    .replaceAll("_", " ")
    .replace(/\b\w/g, (c) => c.toUpperCase());
export const money = (value) =>
  new Intl.NumberFormat("en-GB", { style: "currency", currency: "GBP" }).format(
    Number(value) || 0,
  );
export function dateLabel(value) {
  return value
    ? new Date(`${value}T12:00:00`).toLocaleDateString("en-GB", {
        day: "numeric",
        month: "short",
        year: "numeric",
      })
    : "Date to be arranged";
}
